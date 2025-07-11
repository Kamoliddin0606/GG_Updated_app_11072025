package com.kashtansystem.project.gloriyamarketing.adapters.holders;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.activity.agent.MakeOrderNewActivity;
import com.kashtansystem.project.gloriyamarketing.activity.agent.ProductListNewActivity;
import com.kashtansystem.project.gloriyamarketing.activity.agent.SelectedGoodsActivity;
import com.kashtansystem.project.gloriyamarketing.activity.main.BaseActivity;
import com.kashtansystem.project.gloriyamarketing.adapters.GiftListAdapter;
import com.kashtansystem.project.gloriyamarketing.adapters.SpinnerBaseAdapter;
import com.kashtansystem.project.gloriyamarketing.core.OfflineManager;
import com.kashtansystem.project.gloriyamarketing.database.AppDB;
import com.kashtansystem.project.gloriyamarketing.models.listener.OnDateSelectedListener;
import com.kashtansystem.project.gloriyamarketing.models.template.CompetitorScoutingTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.CreditVisitTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.GoodsByBrandTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.MadeOrderTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.PriceTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.PriceTypeTemplate;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqCheckCredit;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.L;
import com.kashtansystem.project.gloriyamarketing.utils.Util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by FlameKaf on 17.07.2017.
 * ----------------------------------
 * The fragment argument representing the section number for this
 * fragment.
 * Фрагмент заказа.
 */

public class MakeOrderItemsHolder extends Fragment implements AdapterView.OnItemSelectedListener,
        RadioGroup.OnCheckedChangeListener, TextWatcher, View.OnClickListener, View.OnTouchListener,
        View.OnFocusChangeListener, CompoundButton.OnCheckedChangeListener, OnDateSelectedListener {
    private static final String ARG_SECTION_NUMBER = "section_number";

    private ArrayList<PriceTypeTemplate> priceTypes;
    private MadeOrderTemplate madeOrderTemplate;
    private LinearLayout contentCompInfo;
    private LinearLayout orderCreditInfo;
    private View touchedView;
    private Dialog warningDialog;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MakeOrderItemsHolder newInstance(int sectionNumber) {
        L.info(String.format("newInstance id: %s", sectionNumber));

        MakeOrderItemsHolder fragment = new MakeOrderItemsHolder();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Прогрузка типов цен с бд
     */
    public void loadPriceTypes() {
        priceTypes = AppDB.getInstance(getContext()).getPriceTypeList();
    }

    /**
     * Создаёт новые поля для конкурентной разведки
     *
     * @param values info about competitor: [0] - name; [1] - goods; [2] - price
     */
    @SuppressLint("InflateParams")
    private void createCompetitorItems(String... values) {
        final int id = contentCompInfo.getChildCount();
        View view = LayoutInflater.from(contentCompInfo.getContext()).inflate(R.layout.competitor_items, null);

        EditText etName = (EditText) view.findViewById(R.id.orderCompetitor);
        EditText etGoods = (EditText) view.findViewById(R.id.orderCompetitorsGoods);
        EditText etPrice = (EditText) view.findViewById(R.id.orderCompetitorsGoodsPrice);

        etName.setTag(id);
        etName.setOnTouchListener(this);
        etName.addTextChangedListener(this);
        etName.setOnFocusChangeListener(this);
        etGoods.setTag(id);
        etGoods.setOnTouchListener(this);
        etGoods.addTextChangedListener(this);
        etGoods.setOnFocusChangeListener(this);
        etPrice.setTag(id);
        etPrice.setOnTouchListener(this);
        etPrice.addTextChangedListener(this);
        etPrice.setOnFocusChangeListener(this);

        View delete = view.findViewById(R.id.compItemDelete);
        delete.setTag(id);
        delete.setOnClickListener(this);

        if (values.length > 0) {
            etName.setText(values[0]);
            etGoods.setText(values[1]);
            etPrice.setText(values[2]);
        } else {
            CompetitorScoutingTemplate competitor = new CompetitorScoutingTemplate();
            madeOrderTemplate.addNewCompetitor(competitor);
        }

        contentCompInfo.addView(view);
    }

    /**
     * Создаёт новые поля для ввода информации по визиту сбора денег за заказ,
     * который был создан в кредит.
     */
    @SuppressLint("InflateParams")
    private void createCreditVisit(String... values) {
        final int id = orderCreditInfo.getChildCount();
        if (id == 3)
            return;
        View view = LayoutInflater.from(getContext()).inflate(R.layout.credit_visit_item, null);

        EditText etTakeSum = (EditText) view.findViewById(R.id.creditSum);
        etTakeSum.setOnTouchListener(this);
        etTakeSum.addTextChangedListener(this);
        etTakeSum.setOnFocusChangeListener(this);
        etTakeSum.setTag(id);

        final OnDateSelectedListener onDateSelectedListener = new OnDateSelectedListener() {
            @Override
            public void onDateSelected(String view, String values) {
                L.info(view + " " + values);
                CreditVisitTemplate creditVisitTemplate = madeOrderTemplate.getCreditVisit(id);
                creditVisitTemplate.setVisitDate(view);
                creditVisitTemplate.setVisitDateValue(values);

                View v = orderCreditInfo.getChildAt(id);
                ((EditText) v.findViewById(R.id.creditVisitDate)).setText(view);
            }
        };

        View delete = view.findViewById(R.id.creditCalendar);
        delete.setTag(id);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseActivity.showCalendarDialog(getContext(), onDateSelectedListener);
            }
        });

        if (!TextUtils.isEmpty(madeOrderTemplate.getOrderCode())) {
            etTakeSum.setEnabled(false);
            delete.setEnabled(false);
        }

        if (values.length > 0) {
            ((EditText) view.findViewById(R.id.creditVisitDate)).setText(values[0]);
            etTakeSum.setText(values[1]);
        } else {
            touchedView = null;
            double sum = 0;
            for (int ind = 0; ind < orderCreditInfo.getChildCount(); ind++) {
                View child = orderCreditInfo.getChildAt(ind);
                sum += Double.parseDouble(((EditText) child.findViewById(R.id.creditSum)).getText().toString());
            }

            etTakeSum.setText((sum == 0 ? String.format("%s", madeOrderTemplate.getTotalPrice()) : String.format("%s", (madeOrderTemplate.getTotalPrice() - sum))));
            CreditVisitTemplate creditVisitTemplate = new CreditVisitTemplate();
            creditVisitTemplate.setTakeSum(etTakeSum.getText().toString());
            madeOrderTemplate.addCreditVisits(creditVisitTemplate);
        }
        orderCreditInfo.addView(view);
    }

    /**
     * Устанавливает выбранный раннее обьект из списка
     *
     * @param spinner тот или иной выпадающий список
     * @param value   значение
     */
    private void selectSpinnerItem(AppCompatSpinner spinner, String value) {
        SpinnerAdapter adapter = spinner.getAdapter();
        for (int ind = 0; ind < adapter.getCount(); ind++) {
            PriceTypeTemplate priceType = (PriceTypeTemplate) adapter.getItem(ind);
            if (priceType.getCode().equals(value)) {
                spinner.setSelection(ind);
                ind = adapter.getCount();
            }
        }
    }

    /**
     * Устанавливает выбранный раннее обьект из списка
     *
     * @param radioGroup radio group
     * @param value      данные, по которому выбирается item из radio group
     */
    private void selectGroup(RadioGroup radioGroup, String value) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            RadioButton childRb = (RadioButton) radioGroup.getChildAt(i);
            if (childRb.getTag().equals(value)) {
                childRb.setChecked(true);
                i = radioGroup.getChildCount();
            }
        }
    }

    /**
     * Диалоговое окно, запрашивающее подтверждение на удаление
     * информации о конкуренте
     */
    private void dialogAskBeforeDel(int textId, final int ind) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialogbox);
        dialog.setCanceledOnTouchOutside(false);

        ((TextView) dialog.findViewById(R.id.dialogText)).setText(textId);

        View.OnClickListener event = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.dialogBtn2) {
                    contentCompInfo.removeViewAt(ind);
                    madeOrderTemplate.removeCopetitor(ind);
                    for (int i = 0; i < contentCompInfo.getChildCount(); i++) {
                        View childView = contentCompInfo.getChildAt(i);
                        childView.findViewById(R.id.compItemDelete).setTag(i);
                    }
                }
                dialog.cancel();
            }
        };

        Button btnCancel = (Button) dialog.findViewById(R.id.dialogBtn1),
                btnOk = (Button) dialog.findViewById(R.id.dialogBtn2);

        btnCancel.setText(R.string.dialog_btn_cancel);
        btnCancel.setOnClickListener(event);
        btnOk.setText(R.string.dialog_btn_ok);
        btnOk.setOnClickListener(event);

        dialog.show();
    }

    /**
     * Диалоговое окно с выбором подарка
     * по акции, если она имеет место быть
     */
    private void ChooseGiftList() {
        final Dialog dialog = new Dialog(getContext());
        dialog.setTitle(R.string.gift_list);
        dialog.setContentView(R.layout.dialog_goods_list);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        if (window != null)
            window.setLayout(-1, -2);

        ListView giftList = (ListView) dialog.findViewById(R.id.lvDialogGoodsList);
        giftList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                dialog.cancel();
            }
        });
        giftList.setAdapter(new GiftListAdapter(getContext(), madeOrderTemplate.getGiftList()));
        dialog.show();
    }

    /**
     * Сообщение об какой-л. ошибки, возникшей во время отправки заказа
     */
    private void warningDialog(double limit, double total) {
        warningDialog = new Dialog(getContext());
        warningDialog.setTitle(R.string.app_name);
        warningDialog.setContentView(R.layout.dialogbox);
        warningDialog.setCanceledOnTouchOutside(false);

        ((TextView) warningDialog.findViewById(R.id.dialogText)).setText(getString(R.string.hint_reach_limit_credit,
                Util.getParsedPrice(limit), Util.getParsedPrice(total)));

        Button button1 = (Button) warningDialog.findViewById(R.id.dialogBtn1);
        button1.setOnClickListener(this);
        button1.setText(R.string.dialog_btn_cancel);

        Button button2 = (Button) warningDialog.findViewById(R.id.dialogBtn2);
        button2.setOnClickListener(this);
        button2.setText(R.string.btn_change_goods);

        warningDialog.show();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onDateSelected(String view, String value) {
        // @author MrJ
        // получаю дату с календаря
        int year = Integer.parseInt(view.substring(6, 10));
        int month = Integer.parseInt(view.substring(3, 5));
        int day = Integer.parseInt(view.substring(0, 2));
        Date date = new Date(year-1900,month-1,day,0,0,0);
        // получаю текущую дату + ограничения даты отгрузки
        Date curTime = new Date(System.currentTimeMillis());
        Calendar instance = Calendar.getInstance();
        instance.setTime(curTime); //устанавливаем дату, с которой будет производить операции
        int LastSellDate = AppDB.getInstance(getContext()).getLastSellDate();
        instance.add(Calendar.DAY_OF_MONTH, LastSellDate);// прибавляем количествоДней
        Date curDate = instance.getTime();
        if(curDate.before(date)){
            Toast.makeText(getActivity(),"Дата отгрузки товара не может превышать "+ LastSellDate +
                    " дней! Измените дату отгрузки!",Toast.LENGTH_LONG).show();
            view = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(curDate);
            value = new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(curDate) + "000000";
        }
        //

        L.info(view + " " + value);
        madeOrderTemplate.setUploadDateView(view);
        madeOrderTemplate.setUploadDateValue(value);
        ((EditText) getView().findViewById(R.id.orderUploadDate)).setText(view);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case C.REQUEST_CODES.GET_CHOSEN_PRODUCT_RES:
                TextView tvProductsAmount = (TextView) getView().findViewById(R.id.orderGoodsAmount);
                TextView tvTotalPrice = (TextView) getView().findViewById(R.id.orderTotalPrice);
                TextView tvGiftInfo = (TextView) getView().findViewById(R.id.orderGiftInfo);

                tvProductsAmount.setText(String.format("%s %s", getString(R.string.label_quantity_of_goods),
                        madeOrderTemplate.getGoodsList().size()));

                tvTotalPrice.setText(String.format("%s %s", getString(R.string.label_order_total_price),
                        Util.getParsedPrice(madeOrderTemplate.getTotalPrice())));

                if (madeOrderTemplate.getGiftList().size() > 0) {
                    tvGiftInfo.setVisibility(View.VISIBLE);
                    int giftCount = 0;
                    for (GoodsByBrandTemplate gift : madeOrderTemplate.getGiftList())
                        giftCount += gift.getAmount();
                    tvGiftInfo.setText(String.format("%s %s", getString(R.string.label_gave_gifts), giftCount));
                } else
                    tvGiftInfo.setVisibility(View.GONE);

                if (madeOrderTemplate.isOnCredit() && TextUtils.isEmpty(madeOrderTemplate.getOrderCode())) {
                    final double limit = MakeOrderNewActivity.tradingPoint.getCreditLimit() -
                            MakeOrderNewActivity.tradingPoint.getAccumulatedCredit();
                    if (limit < madeOrderTemplate.getTotalPrice())
                        warningDialog(limit, madeOrderTemplate.getTotalPrice());
                }
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        madeOrderTemplate = MakeOrderNewActivity.orderItems.get(getArguments().getInt(ARG_SECTION_NUMBER));
        if (madeOrderTemplate.getPriceType().isEmpty()) {
            if (priceTypes != null && !priceTypes.isEmpty()) {
                madeOrderTemplate.setPriceType(priceTypes.get(0).getCode());
                madeOrderTemplate.setPriceTypeName(priceTypes.get(0).getName());
            }
        }

        @SuppressLint("InflateParams")
        View view = inflater.inflate(R.layout.order_content, null, false);
        touchedView = null;

        contentCompInfo = (LinearLayout) view.findViewById(R.id.orderCompetitorItems);
        orderCreditInfo = (LinearLayout) view.findViewById(R.id.orderCreditsInfo);

        /* тип заказа */
        RadioGroup rgOrderType = (RadioGroup) view.findViewById(R.id.orderRGroupTypes);
        rgOrderType.setOnCheckedChangeListener(this);
        selectGroup(rgOrderType, madeOrderTemplate.getOrderType() + "");

        if (!TextUtils.isEmpty(madeOrderTemplate.getDescription()))
            ((TextView) view.findViewById(R.id.orderAttention)).setText(madeOrderTemplate.getDescription());
        else if (!TextUtils.isEmpty(madeOrderTemplate.getInfo()))
            ((TextView) view.findViewById(R.id.orderAttention)).setText(madeOrderTemplate.getInfo());

        if (MakeOrderNewActivity.tradingPoint.getCreditLimit() > 0) {
            CheckBox chbOnCredit = (CheckBox) view.findViewById(R.id.orderCredit);
            chbOnCredit.setChecked(madeOrderTemplate.isOnCredit());
            chbOnCredit.setEnabled((TextUtils.isEmpty(madeOrderTemplate.getOrderCode())));
            chbOnCredit.setOnClickListener(this);

            if (madeOrderTemplate.isOnCredit()) {
                orderCreditInfo.setVisibility(View.VISIBLE);
                view.findViewById(R.id.tvCreditInfo).setVisibility(View.VISIBLE);
                View creditContentLayout = view.findViewById(R.id.creditInfoLayout);
                creditContentLayout.setVisibility(View.VISIBLE);

                View btnNewCreditVisit = view.findViewById(R.id.btnAddNewVisitDay);
                btnNewCreditVisit.setVisibility(View.VISIBLE);
                btnNewCreditVisit.setEnabled((TextUtils.isEmpty(madeOrderTemplate.getOrderCode())));

                ((TextView) creditContentLayout.findViewById(R.id.tvCredit)).setText(getString(R.string.label_credit,
                        Util.getParsedPrice(MakeOrderNewActivity.tradingPoint.getCreditLimit())));
                ((TextView) creditContentLayout.findViewById(R.id.tvAccumulatedCredit)).setText(getString(R.string.label_accumulated_credit,
                        Util.getParsedPrice(MakeOrderNewActivity.tradingPoint.getAccumulatedCredit())));
                ((TextView) creditContentLayout.findViewById(R.id.tvCreditSum)).setText(getString(R.string.label_available_credit_sum,
                        Util.getParsedPrice(MakeOrderNewActivity.tradingPoint.getCreditLimit() - MakeOrderNewActivity.tradingPoint.getAccumulatedCredit())));

                for (CreditVisitTemplate creditVisit : madeOrderTemplate.getCreditVisits())
                    createCreditVisit(creditVisit.getVisitDate(), creditVisit.getTakeSum());
            } else {
                orderCreditInfo.setVisibility(View.GONE);
                view.findViewById(R.id.tvCreditInfo).setVisibility(View.GONE);
                view.findViewById(R.id.creditInfoLayout).setVisibility(View.GONE);
                view.findViewById(R.id.btnAddNewVisitDay).setVisibility(View.GONE);
            }
        }

        view.findViewById(R.id.btnAddNewVisitDay).setOnClickListener(this);

        // * Price type
        AppCompatSpinner priceTypeSpinner = (AppCompatSpinner) view.findViewById(R.id.orderPriceType);
        priceTypeSpinner.setAdapter(new SpinnerBaseAdapter(getContext(), priceTypes));
        priceTypeSpinner.setOnItemSelectedListener(this);
        priceTypeSpinner.setOnTouchListener(this);
        selectSpinnerItem(priceTypeSpinner, madeOrderTemplate.getPriceType());
        // * Comment destination
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.orderRGroup);
        radioGroup.setOnCheckedChangeListener(this);
        selectGroup(radioGroup, madeOrderTemplate.getCommentTo());
        // * Comment
        EditText etComment = (EditText) view.findViewById(R.id.orderComment);
        etComment.setText(madeOrderTemplate.getComment());
        etComment.setEnabled(TextUtils.isEmpty(madeOrderTemplate.getOrderCode()));
        etComment.setOnTouchListener(this);
        etComment.addTextChangedListener(this);
        etComment.setOnFocusChangeListener(this);
        // * Upload date
        EditText etUploadDate = (EditText) view.findViewById(R.id.orderUploadDate);
        etUploadDate.setText(madeOrderTemplate.getUploadDateView());
        // * Selected products count
        TextView tvProductsAmount = (TextView) view.findViewById(R.id.orderGoodsAmount);
        tvProductsAmount.setText(String.format("%s %s", getString(R.string.label_quantity_of_goods),
                madeOrderTemplate.getGoodsList().size()));
        // * Total price
        TextView tvTotalPrice = (TextView) view.findViewById(R.id.orderTotalPrice);
        tvTotalPrice.setText(String.format("%s %s", getString(R.string.label_order_total_price),
                Util.getParsedPrice(madeOrderTemplate.getTotalPrice())));
        // * Кол-во выдаваемых подарков
        TextView tvGiftInfo = (TextView) view.findViewById(R.id.orderGiftInfo);
        tvGiftInfo.setOnClickListener(this);
        if (madeOrderTemplate.getGiftList().size() > 0) {
            tvGiftInfo.setVisibility(View.VISIBLE);
            int giftCount = 0;
            for (GoodsByBrandTemplate gift : madeOrderTemplate.getGiftList())
                giftCount += gift.getAmount();
            tvGiftInfo.setText(String.format("%s %s", getString(R.string.label_gave_gifts), giftCount));
        } else
            tvGiftInfo.setVisibility(View.GONE);
        // * Icon for choose upload date
        View ivCal = view.findViewById(R.id.ivMyCalendar);
        ivCal.setEnabled(TextUtils.isEmpty(madeOrderTemplate.getOrderCode()));
        ivCal.setOnClickListener(this);
        // * Add new goods;
        View btnAddNewProducts = view.findViewById(R.id.btnAddNewGoods);
        btnAddNewProducts.setEnabled(TextUtils.isEmpty(madeOrderTemplate.getOrderCode()));
        btnAddNewProducts.setOnClickListener(this);
        view.findViewById(R.id.ivViewSelGoods).setOnClickListener(this);
        // * Add new competitor info
        View btnAddNewComp = view.findViewById(R.id.btnAddNewCompItem);
        btnAddNewComp.setEnabled(TextUtils.isEmpty(madeOrderTemplate.getOrderCode()));
        btnAddNewComp.setOnClickListener(this);
        // * Save locale or not
        CheckBox saveLocale = (CheckBox) view.findViewById(R.id.orderSaveLocal);
        if (OfflineManager.INSTANCE.getGoOffline()) {
            saveLocale.setChecked(true);
            madeOrderTemplate.setSaveLocal(true);
            saveLocale.setOnCheckedChangeListener(this);
        } else {
            saveLocale.setChecked(madeOrderTemplate.isSaveLocal());
            saveLocale.setOnCheckedChangeListener(this);
        }
        saveLocale.setEnabled(TextUtils.isEmpty(madeOrderTemplate.getOrderCode()));

        for (CompetitorScoutingTemplate competitor : madeOrderTemplate.getCompetitorList())
            createCompetitorItems(competitor.getName(), competitor.getGoods(), competitor.getPrice());

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (touchedView != null) {
            View selectedPrice = view.findViewById(R.id.labelText);
            madeOrderTemplate.setPriceType(selectedPrice.getTag().toString());
            madeOrderTemplate.setPriceTypeName(((TextView) selectedPrice).getText().toString());
            // @author MrJ
            HashMap<String, PriceTemplate> priceList = AppDB.getInstance(getContext()).getPriceListByPriceType(selectedPrice.getTag().toString());

            for (MadeOrderTemplate orderItem : MakeOrderNewActivity.orderItems) {
                Map<String, GoodsByBrandTemplate> goods = orderItem.getGoodsList();
                double totalPrice = 0;
                for (Map.Entry<String, GoodsByBrandTemplate> goodItem : goods.entrySet()) {
                    Log.d("TAG", goodItem.getKey() + " " + goodItem.getValue());
                    GoodsByBrandTemplate subItem = goodItem.getValue();
                    if (priceList.containsKey(subItem.getProductCode())) {
                        PriceTemplate price = priceList.get(subItem.getProductCode());
                        subItem.setOriginalPrice(price.getPrice());
                        subItem.setDiscountValue(price.getDiscount());
                        subItem.setPrice((price.getDiscount() == 0 ? price.getPrice() : price.getNewPrice()));
                        subItem.setTotal(subItem.getAmount() * subItem.getPrice());
                        totalPrice += subItem.getTotal();
                    }
                }
                orderItem.setTotalPrice(totalPrice);
            }

            Log.d("asd", priceList.size() + " asd");
            //
        }
        updateTextView();
    }

    // @author MrJ
    public void updateTextView() {
        TextView tvTotalPrice = (TextView) getView().findViewById(R.id.orderTotalPrice);
        tvTotalPrice.setText(String.format("%s %s", getString(R.string.label_order_total_price),
                Util.getParsedPrice(madeOrderTemplate.getTotalPrice())));
    }
    //

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (group.getId()) {
            case R.id.orderRGroup:
                madeOrderTemplate.setCommentTo(group.findViewById(checkedId).getTag().toString());
                break;
            case R.id.orderRGroupTypes:
                madeOrderTemplate.setOrderType(Integer.parseInt(group.findViewById(checkedId).getTag().toString()));
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.orderSaveLocal:
                madeOrderTemplate.setSaveLocal(isChecked);
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence text, int start, int before, int count) {
        L.info("onTextChanged " + text);
        if (touchedView != null) {
            switch (touchedView.getId()) {
                case R.id.orderComment:
                    madeOrderTemplate.setComment(text.toString());
                    break;
                case R.id.orderCompetitor:
                    madeOrderTemplate.getCompetitor((Integer) touchedView.getTag()).setName(text.toString());
                    break;
                case R.id.orderCompetitorsGoods:
                    madeOrderTemplate.getCompetitor((Integer) touchedView.getTag()).setGoods(text.toString());
                    break;
                case R.id.orderCompetitorsGoodsPrice:
                    madeOrderTemplate.getCompetitor((Integer) touchedView.getTag()).setPrice(text.toString());
                    break;
                case R.id.creditSum:
                    madeOrderTemplate.getCreditVisit((Integer) touchedView.getTag()).setTakeSum(text.toString());
                    break;
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        touchedView = view;
        return view.performClick();
    }

    @Override
    public void onFocusChange(View view, boolean hasFocus) {
        if (hasFocus)
            touchedView = view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnAddNewVisitDay:
                createCreditVisit();
                break;
            case R.id.orderCredit:
                CheckBox checkBox = (CheckBox) view;
                madeOrderTemplate.setOnCredit(checkBox.isChecked());
                orderCreditInfo.setVisibility((checkBox.isChecked() ? View.VISIBLE : View.GONE));

                View content = getView();
                if (content != null) {
                    content.findViewById(R.id.tvCreditInfo).setVisibility((checkBox.isChecked() ? View.VISIBLE : View.GONE));
                    View creditContentLayout = content.findViewById(R.id.creditInfoLayout);
                    creditContentLayout.setVisibility((checkBox.isChecked() ? View.VISIBLE : View.GONE));
                    content.findViewById(R.id.btnAddNewVisitDay).setVisibility((checkBox.isChecked() ? View.VISIBLE : View.GONE));

                    if (!checkBox.isChecked()) {
                        orderCreditInfo.removeAllViews();
                        madeOrderTemplate.getCreditVisits().clear();
                    } else {
                        if (!priceTypes.isEmpty())
                            ((AppCompatSpinner) content.findViewById(R.id.orderPriceType)).setSelection((priceTypes.size() > 1 ? 1 : 0));
                        new CheckAccumulatedCredit(MakeOrderNewActivity.tradingPoint.getTpCode()).execute();
                    }
                }
                break;
            case R.id.orderGiftInfo:
                ChooseGiftList();
                break;
            case R.id.dialogBtn1:
                warningDialog.cancel();
                break;
            case R.id.dialogBtn2:
                warningDialog.cancel();
            case R.id.btnAddNewGoods:
                //Intent intent = new Intent(getContext(), ProductListActivity.class);
                Intent intent = new Intent(getContext(), ProductListNewActivity.class);
                intent.putExtra(C.KEYS.EXTRA_DATA_ID, getArguments().getInt(ARG_SECTION_NUMBER));
                intent.putExtra(C.KEYS.EXTRA_DATA_PT, madeOrderTemplate.getPriceType());
                startActivityForResult(intent, C.REQUEST_CODES.GET_CHOSEN_PRODUCT_RES);
                break;
            case R.id.ivViewSelGoods:
                Intent iViewSelGoods = new Intent(getContext(), SelectedGoodsActivity.class);
                iViewSelGoods.putExtra(C.KEYS.EXTRA_DATA_CAN_CHANGE, (TextUtils.isEmpty(madeOrderTemplate.getOrderCode())));
                iViewSelGoods.putExtra(C.KEYS.EXTRA_DATA_ID, getArguments().getInt(ARG_SECTION_NUMBER));
                iViewSelGoods.putExtra(C.KEYS.EXTRA_DATA_PT, madeOrderTemplate.getPriceType());
                startActivityForResult(iViewSelGoods, C.REQUEST_CODES.GET_CHOSEN_PRODUCT_RES);
                break;
            case R.id.ivMyCalendar:
                BaseActivity.showCalendarDialog(view.getContext(), this);
                break;
            case R.id.btnAddNewCompItem:
                createCompetitorItems();
                break;
            case R.id.compItemDelete:
                if (contentCompInfo.getChildCount() > 0)
                    dialogAskBeforeDel(R.string.dialog_text_ask_to_delete_comp,
                            Integer.parseInt(view.getTag() + ""));
                break;
        }
    }

    /**
     * Проверка накопленного кредита
     */
    @SuppressLint("StaticFieldLeak")
    public class CheckAccumulatedCredit extends AsyncTask<Void, Void, Double> {
        private Dialog dialog;
        private String tpCode;

        private CheckAccumulatedCredit(String tpCode) {
            this.tpCode = tpCode;
        }

        @Override
        protected void onPreExecute() {
            dialog = BaseActivity.getInformDialog(getContext(), getString(R.string.dialog_text_check_credit));
            dialog.show();
        }

        @Override
        protected Double doInBackground(Void... params) {
            Double res = ReqCheckCredit.getBalance(tpCode);
            AppDB.getInstance(getContext()).updateClientCredit(tpCode, res);
            return res;
        }

        @Override
        protected void onPostExecute(Double result) {
            dialog.cancel();
            MakeOrderNewActivity.tradingPoint.setAcumulatedCredit(result);

            View content = getView();
            if (content != null) {
                double limit = MakeOrderNewActivity.tradingPoint.getCreditLimit() - MakeOrderNewActivity.tradingPoint.getAccumulatedCredit();
                View creditContentLayout = content.findViewById(R.id.creditInfoLayout);
                ((TextView) creditContentLayout.findViewById(R.id.tvCredit)).setText(getString(R.string.label_credit,
                        Util.getParsedPrice(MakeOrderNewActivity.tradingPoint.getCreditLimit())));
                ((TextView) creditContentLayout.findViewById(R.id.tvAccumulatedCredit)).setText(getString(R.string.label_accumulated_credit,
                        Util.getParsedPrice(MakeOrderNewActivity.tradingPoint.getAccumulatedCredit())));
                ((TextView) creditContentLayout.findViewById(R.id.tvCreditSum)).setText(getString(R.string.label_available_credit_sum,
                        Util.getParsedPrice(limit)));

                if (limit < madeOrderTemplate.getTotalPrice())
                    warningDialog(limit, madeOrderTemplate.getTotalPrice());
            }
        }
    }
}