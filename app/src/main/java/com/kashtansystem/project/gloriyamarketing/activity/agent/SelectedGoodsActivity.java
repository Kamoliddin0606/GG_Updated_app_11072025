package com.kashtansystem.project.gloriyamarketing.activity.agent;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.activity.main.BaseActivity;
import com.kashtansystem.project.gloriyamarketing.database.AppDB;
import com.kashtansystem.project.gloriyamarketing.models.template.GoodsByBrandTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.MadeOrderTemplate;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.L;
import com.kashtansystem.project.gloriyamarketing.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by FlameKaf on 03.08.2017.
 * ----------------------------------
 */

public class SelectedGoodsActivity extends BaseActivity
{
    private MadeOrderTemplate madeOrder;
    private HashMap<String, ArrayList<GoodsByBrandTemplate>> giftList;
    private HashMap<String, GoodsByBrandTemplate> gaveGifts;
    private boolean isCanChange;

    @Override
    public String getActionBarTitle()
    {
        return getString(R.string.app_title_selected_goods);
    }

    @Override
    public boolean getHomeButtonEnable()
    {
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_goods_layout);
        setSupportActionBar((Toolbar)findViewById(R.id.appToolBar));

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        else
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        setResult(RESULT_CANCELED);

        madeOrder = MakeOrderNewActivity.orderItems.get(getIntent().getIntExtra(C.KEYS.EXTRA_DATA_ID, 0));
        isCanChange = getIntent().getBooleanExtra(C.KEYS.EXTRA_DATA_CAN_CHANGE, true);
        new Init().execute(getIntent().getStringExtra(C.KEYS.EXTRA_DATA_PT));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        if (menuItem.getItemId() == android.R.id.home)
            complete();
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onBackPressed()
    {
        complete();
    }

    /**
     * Расчитывает общую стоимость выбранных товаров
     * */
    private void complete()
    {
        if (isCanChange)
        {
            Dialog dialog = BaseActivity.getInformDialog(this, getString(R.string.dialog_text_calc_total_price));
            dialog.show();

            float totalPrice = 0;
            for (GoodsByBrandTemplate product : madeOrder.getGoodsList().values())
                totalPrice += product.getTotal();
            madeOrder.setTotalPrice(totalPrice);

            ((TextView) dialog.findViewById(R.id.dialogSLabel)).setText(R.string.counting_gifts);

            madeOrder.clearGiftList();
            Set<String> keys = gaveGifts.keySet();
            for (String key : keys)
            {
                GoodsByBrandTemplate gift = gaveGifts.get(key);
                //gift.setForProduct(key);
                if (gift.getAmountBySeries() >= gift.getMinAmount() && gift.getMinAmount() != 0)
                {
                    int count = (gift.getAmountBySeries() / gift.getMinAmount()) * gift.getCount();
                    gift.setAmount(count);
                    madeOrder.getGiftList().add(gift);
                }
            }

            dialog.cancel();

            giftList.clear();
            gaveGifts.clear();

            setResult(RESULT_OK);
        }
        finish();
    }

    /**
     * Диалоговое окно с выбором подарка
     * по акции, если она имеет место быть
     * */
    /*private void ChooseGiftList(final String forProduct)
    {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle(R.string.gift_list);
        dialog.setContentView(R.layout.dialog_goods_list);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        if (window != null)
            window.setLayout(-1, -2);

        ListView giftList = (ListView)dialog.findViewById(R.id.lvDialogGoodsList);
        giftList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id)
            {
                gaveGifts.put(forProduct, SelectedGoodsActivity.giftList.get(pos));
                dialog.cancel();
            }
        });
        giftList.setAdapterExpenses(new GiftListAdapter(this, SelectedGoodsActivity.giftList));
        dialog.show();
    }*/

    /**
     * Загрузка данных
     * */
    @SuppressLint("StaticFieldLeak")
    private class Init extends AsyncTask<String, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            findViewById(R.id.appProgressBar).setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... params)
        {
            giftList = AppDB.getInstance(SelectedGoodsActivity.this).getGiftListNew(params[0]);
            gaveGifts = new HashMap<>(giftList.size());
            for (GoodsByBrandTemplate gift: madeOrder.getGiftList())
                gaveGifts.put(gift.getForProduct(), gift);
            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            ListView lvGoods = (ListView)findViewById(R.id.lvDialogGoodsList);
            SelectedGoodsAdapter adapter = new SelectedGoodsAdapter();
            lvGoods.setAdapter(adapter);
            findViewById(R.id.appProgressBar).setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Адаптер выбранных товаров
     * */
    private class SelectedGoodsAdapter extends BaseAdapter
    {
        private List<GoodsByBrandTemplate> items;

        private class ViewHolder
        {
            int pos = 0;
            TextView tvProduct;
            TextView tvPrice;
            TextView tvProductNotif;
            TextView tvGiftInfo;
            EditText etAmount;
            ImageView ivGift;
            // Количество подарков по акции
            EditText etNewGiftAmount;
        }

        SelectedGoodsAdapter()
        {
            if (!madeOrder.getGoodsList().isEmpty())
                this.items = new ArrayList<>(madeOrder.getGoodsList().values());
        }

        @Override
        public int getCount()
        {
            return (items == null ? 0 : items.size());
        }

        @Override
        public Object getItem(int position)
        {
            return items.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        @SuppressLint("InflateParams")
        public View getView(int position, View convertView, ViewGroup parent)
        {
            final ViewHolder holder;
            if (convertView == null)
            {
                convertView = LayoutInflater.from(SelectedGoodsActivity.this).inflate(R.layout.selected_goods_item, null);
                holder = new ViewHolder();
                holder.tvProduct = (TextView)convertView.findViewById(R.id.tvProductName);
                holder.tvPrice = (TextView)convertView.findViewById(R.id.tvProductPrice);
                holder.tvProductNotif = (TextView) convertView.findViewById(R.id.tvProductNotification);
                holder.tvGiftInfo = (TextView) convertView.findViewById(R.id.tvGiftInfo);
                holder.ivGift = (ImageView) convertView.findViewById(R.id.ivGift);
                holder.etAmount = (EditText) convertView.findViewById(R.id.etProductAmount);
                holder.etAmount.setEnabled(isCanChange);
                holder.etAmount.addTextChangedListener(new TextWatcher()
                {
                    @Override
                    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
                    {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
                    {
                    }

                    @Override
                    public void afterTextChanged(Editable s)
                    {
                        L.info(s.toString());
                        final GoodsByBrandTemplate product = items.get(holder.pos);
                        if (s.length() > 0 && !s.toString().equals("0"))
                        {
                            int count = Integer.parseInt(s.toString());
                            if (gaveGifts.containsKey(product.getProductCode()))
                                gaveGifts.get(product.getProductCode()).setAmountBySeries(product.getAmount());
                            else
                            if (gaveGifts.containsKey(product.getSeries()))
                            {
                                GoodsByBrandTemplate gift = gaveGifts.get(product.getSeries());
                                if (gift.getAmountBySeries() <= 0)
                                    gift.setAmountBySeries(count);
                                else
                                    gift.setAmountBySeries((gift.getAmountBySeries() - product.getAmount()) + count);
                            }

                            //product.setAmountBySeries((product.getAmountBySeries() - product.getAmount()) + count);
                            product.setAmount(count);
                            product.setTotal(product.getAmount() * product.getPrice());

                            if (!madeOrder.getGoodsList().containsKey(product.getProductCode()))
                                madeOrder.getGoodsList().put(product.getProductCode(), product);
                            else
                            {
                                final GoodsByBrandTemplate chosenProduct = madeOrder.getGoodsList().get(product.getProductCode());
                                chosenProduct.setAmount(product.getAmount());
                                chosenProduct.setTotal(product.getTotal());
                            }
                        }
                        else
                        {
                            if (gaveGifts.containsKey(product.getProductCode()))
                                gaveGifts.get(product.getProductCode()).setAmountBySeries(0);
                            else
                            if (gaveGifts.containsKey(product.getSeries()))
                            {
                                GoodsByBrandTemplate gift = gaveGifts.get(product.getSeries());
                                gift.setAmountBySeries((gift.getAmountBySeries() - product.getAmount()));
                            }

                            //product.setAmountBySeries(product.getAmountBySeries() - product.getAmount());
                            product.setAmount(0);
                            product.setTotal(0);
                            // Если количество товара и подарков 0, то удалим
                            if (madeOrder.getGoodsList().containsKey(product.getProductCode())
                                    && madeOrder.getGoodsList().get(product.getProductCode()).getGiftAmount() == 0)
                                madeOrder.getGoodsList().remove(product.getProductCode());
                        }
                    }
                });

                convertView.setTag(holder);
            }
            else
                holder = (ViewHolder)convertView.getTag();

            holder.pos = position;
            final GoodsByBrandTemplate item = items.get(position);

            // * Отображает уведомление о нехватке товара на складе
            if (madeOrder.getNotEnoughGoods().containsKey(item.getProductCode()))
            {
                holder.tvProductNotif.setVisibility(View.VISIBLE);
                holder.tvProductNotif.setText(holder.tvProductNotif.getContext()
                    .getString(R.string.hint_reach_limit, item.getAmount() + "", madeOrder.getNotEnoughGoods()
                        .get(item.getProductCode())));
            }
            else
                holder.tvProductNotif.setVisibility(View.GONE);

            /*if (item.getMinAmount() != 0)
            {
                holder.tvGiftInfo.setText(holder.tvGiftInfo.getContext().getString(R.string.gift_info, item.getMinAmount()));
                holder.ivGift.setVisibility((item.getAmount() >= item.getMinAmount() ? View.VISIBLE : View.GONE));
            }*/

            holder.tvProduct.setText(item.getProductName());
            holder.tvPrice.setText(Util.getParsedPrice(item.getPrice()));
            holder.tvPrice.setText((item.getDiscountValue() == 0 ? item.getPrice() + "" : Html.fromHtml(String
                    .format("<s>%s</s> <i>%s -%s%%</i>", item.getOriginalPrice(), Util.getDoubleToString(item.getPrice()), item.getDiscountValue()))));

            holder.etAmount.setText((item.getAmount() != 0 ? String.format("%s", item.getAmount()) : ""));

            // Количество подарков по акции
            holder.etNewGiftAmount = (EditText) convertView.findViewById(R.id.etNewGiftAmount);
            holder.etNewGiftAmount.setText((item.getGiftAmount() != 0 ? String.format("%s", item.getGiftAmount()) : ""));
            holder.etNewGiftAmount.setEnabled(isCanChange);
            holder.etNewGiftAmount.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }
                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) { }
                @Override
                public void afterTextChanged(Editable s) {
                    final GoodsByBrandTemplate product = items.get(holder.pos);
                    if (s.length() > 0 && !s.toString().equals("0")) {
                        int count = Integer.parseInt(s.toString());
                        product.setGiftAmount(count);
                        if (!madeOrder.getGoodsList().containsKey(product.getProductCode()))
                            madeOrder.getGoodsList().put(product.getProductCode(), product);
                        else
                        {
                            final GoodsByBrandTemplate chosenProduct = madeOrder.getGoodsList().get(product.getProductCode());
                            chosenProduct.setGiftAmount(product.getGiftAmount());
                        }
                    }
                    else {
                        product.setGiftAmount(0);
                        // Если количество товара и подарков 0, то удалим
                        if (madeOrder.getGoodsList().containsKey(product.getProductCode())
                                && madeOrder.getGoodsList().get(product.getProductCode()).getAmount() == 0)
                            madeOrder.getGoodsList().remove(product.getProductCode());
                    }
                }
            });

            return convertView;
        }
    }
}