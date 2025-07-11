package com.kashtansystem.project.gloriyamarketing.activity.agent;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.activity.main.BaseActivity;
import com.kashtansystem.project.gloriyamarketing.activity.main.LoginActivity;
import com.kashtansystem.project.gloriyamarketing.adapters.MakeOrderItemsAdapter;
import com.kashtansystem.project.gloriyamarketing.adapters.PS_orderListAdapter;
import com.kashtansystem.project.gloriyamarketing.adapters.holders.MakeOrderItemsHolder;
import com.kashtansystem.project.gloriyamarketing.database.AppDB;
import com.kashtansystem.project.gloriyamarketing.models.listener.OnDialogBtnsClickListener;
import com.kashtansystem.project.gloriyamarketing.models.template.CreditVisitTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.GoodsByBrandTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.MadeOrderTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.OrderTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.PriceTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.PriceTypeTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.SendOrderResponseTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.TradingPointTemplate;
import com.kashtansystem.project.gloriyamarketing.net.soap.PS_ReqGetPriceListUpdateTime;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqLogin;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqOrderForEdit;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqPriceList;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqPriceType;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqSendOrder;
import com.kashtansystem.project.gloriyamarketing.service.MainService;
import com.kashtansystem.project.gloriyamarketing.service.ReloadProductIntentService;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.AppPermissions;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.GPS;
import com.kashtansystem.project.gloriyamarketing.utils.GPStatus;
import com.kashtansystem.project.gloriyamarketing.utils.OrderStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by FlameKaf on 17.07.2017.
 * ----------------------------------
 * New Activity for Creating Order
 */

public class MakeOrderNewActivity extends BaseActivity implements GPS.OnCoordinatsReceiveListener,
        View.OnClickListener {

    // @author MrJ
    private List<MadeOrderTemplate> changedOrders;
    //

    public static TradingPointTemplate tradingPoint;
    public static String warehouseCode = "";
    public static String contractCode = "";

    public static LinkedList<MadeOrderTemplate> orderItems = new LinkedList<>();

    private OrderTemplate order;
    private TabLayout tabLayout;
    private ViewPager orderPages;
    private MakeOrderItemsAdapter orderAdapter;
    private GPS gps;
    private Dialog informDialog;
    private Location location;

    @Override
    public String getActionBarTitle() {
        return tradingPoint.getTitle();
    }

    @Override
    public boolean getHomeButtonEnable() {
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_order);
        setSupportActionBar((Toolbar) findViewById(R.id.appToolBar));
        findViewById(R.id.appTabs).setVisibility(View.VISIBLE);

        // @author MrJ
        changedOrders = new ArrayList<>();
        //

        gps = new GPS(this, this);
        tradingPoint = getIntent().getParcelableExtra(C.KEYS.EXTRA_DATA_TP);
        if (getIntent().hasExtra(C.KEYS.EXTRA_DATA_CT))
            contractCode = getIntent().getStringExtra(C.KEYS.EXTRA_DATA_CT);

        warehouseCode = getIntent().getStringExtra(C.KEYS.EXTRA_DATA_W);

        tabLayout = (TabLayout) findViewById(R.id.appTabs);
        orderPages = (ViewPager) findViewById(R.id.makeOrderContainer);
        orderPages.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        orderAdapter = new MakeOrderItemsAdapter(getSupportFragmentManager());
        orderPages.setAdapter(orderAdapter);

        findViewById(R.id.newTab).setOnClickListener(this);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabSendOrder);
        fab.setOnClickListener(this);

        if (!TextUtils.isEmpty(getIntent().getAction()) && getIntent().getAction().equals(C.ACTIONS.ACTION_EDIT_ORDER)) {
            //setResult(RESULT_CANCELED);
            order = getIntent().getParcelableExtra(C.KEYS.EXTRA_DATA_ORDER);
            if (!TextUtils.isEmpty(order.getOrderCode())) {
                findViewById(R.id.newTab).setEnabled(false);
                fab.setEnabled(false);
                fab.hide();
            }
            tradingPoint = AppDB.getInstance(this).getClient(order.getTpCode());
            new GetMadeOrderDetails().execute(order.getOrderCode(), order.getCreatedDate());
        } else
            createTab(getOrderTitle(), true, true, null);
    }

    @Override
    public void onPause() {
        super.onPause();
        if ((location == null) && (informDialog != null && informDialog.isShowing()))
            informDialog.cancel();
        gps.stopRequest();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        orderItems.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.order_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                // @author MrJ
                boolean haveOrdersWithGood = false;
                for (MadeOrderTemplate orderItem : orderItems) {
                    if (orderItem.getGoodsList().size() > 0 && (orderItem.getStatus() == OrderStatus.NewOrder) && orderItem.getOrderCode().isEmpty() )
                        haveOrdersWithGood = true;
                }
                if (!haveOrdersWithGood)
                    finish();
                else{
                    AlertDialog.Builder adb = new AlertDialog.Builder(this);
                    if(orderItems.size()==1)
                        adb.setTitle(R.string.dialog_title_save_document);
                    else
                        adb.setTitle(R.string.dialog_title_save_documents);
                    adb.setNeutralButton("Отмена", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    adb.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            for (MadeOrderTemplate orderItem : orderItems) {
                                orderItem.setSaveLocal(true);
                            }
                            attemptSendOrSaveOrder();
                        }
                    });
                    adb.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
                    adb.create().show();
                }
                //
                break;
            case R.id.mPhotoRep:
                cameraPermission();
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.newTab:
                createTab(getOrderTitle(), true, true, null);
                orderPages.setCurrentItem(orderItems.size() - 1, true);
                break;
            case R.id.tvTabTitle:
                TabLayout.Tab tab1 = (TabLayout.Tab) view.getTag();
                orderPages.setCurrentItem(tab1.getPosition(), true);
                break;
            case R.id.ibTabDel:
                attemptDelTab((TabLayout.Tab) view.getTag());
                break;
            case R.id.fabSendOrder:
                attemptSendOrSaveOrder();
                break;
        }
    }

    @Override
    public void onPermissionGrantResult(AppPermissions permission, boolean result) {
        if (result) {
            switch (permission) {
                case Camera:
                    Intent takePictureIntent = new Intent(this, PictureCaptureActivity.class);
                    takePictureIntent.putExtra(C.KEYS.EXTRA_DATA, tradingPoint.getTitle());
                    startActivityForResult(takePictureIntent, C.REQUEST_CODES.CAPTURE_PICTURE);
                    break;
                case GeoLocation:
                    informDialog = getInformDialog(this, getString(R.string.dialog_text_req_coordinates));
                    informDialog.show();
                    gps.startRequest();
                    break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //if (resultCode == RESULT_OK && requestCode == C.REQUEST_CODES.CAPTURE_PICTURE)
        //photoRep = data.getStringArrayExtra(C.KEYS.EXTRA_CONTENT);
    }

    @Override
    public void onReceiveCoordinats(GPStatus status, Location location, boolean fromMock) {
        switch (status) {
            case Success:
            case LastKnownLocation:
                if ((GPStatus.LastKnownLocation == status) && (location == null)) {
                    gps.stopRequest();
                    informDialog.cancel();
                    Toast.makeText(this, "Не удалось определить местоположение. Убедитесь что GPS на телефоне включён!",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                if (!location.getProvider().equals("fused")) {
                    gps.stopRequest();
                    if (fromMock) {
                        informDialog.cancel();
                        DialogBox(this, getString(R.string.dialog_text_mock_is_using),
                                getString(R.string.dialog_btn_cancel), getString(R.string.dialog_btn_ok), null, null);
                        return;
                    }

                    if (this.location == null) {
                        this.location = location;
                        new CompleteMakeOrder(this).execute();
                    }
                }
                break;
            case GpsProviderDisabled:
            case NetworkProviderDisabled:
                gps.stopRequest();
                informDialog.cancel();
                isGPStatusOK();
                break;
        }
    }

    /**
     * Диалоговое окно, которое оповещает пользователя о той
     * или иной ошибке.
     */
    private void InformDialog(String title, final int orderId) {
        final Dialog warningDialog = new Dialog(this);
        warningDialog.setTitle(R.string.app_name);
        warningDialog.setContentView(R.layout.dialogbox);
        warningDialog.setCanceledOnTouchOutside(false);

        ((TextView) warningDialog.findViewById(R.id.dialogText)).setText(title);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.dialogBtn2)
                    orderPages.setCurrentItem(orderId, true);
                warningDialog.cancel();
            }
        };

        Button button1 = (Button) warningDialog.findViewById(R.id.dialogBtn1);
        button1.setOnClickListener(listener);
        button1.setText(R.string.dialog_btn_cancel);

        Button button2 = (Button) warningDialog.findViewById(R.id.dialogBtn2);
        button2.setOnClickListener(listener);
        button2.setText(R.string.btn_change_goods);

        warningDialog.show();
    }

    private void InformDialogNew(String title, final int orderId) {
        final Dialog warningDialog = new Dialog(this);
        warningDialog.setTitle(R.string.app_name);
        warningDialog.setContentView(R.layout.ps_dialogbox);
        warningDialog.setCanceledOnTouchOutside(false);

        ((TextView) warningDialog.findViewById(R.id.dialogText)).setText(title);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                warningDialog.cancel();
            }
        };

        Button button1 = (Button) warningDialog.findViewById(R.id.ps_dialogBtn1);
        button1.setOnClickListener(listener);
        button1.setText(R.string.dialog_btn_ok);

        warningDialog.show();
    }

    /**
     * @return временное номерное название заказа
     */
    private String getOrderTitle() {
        int count = AppDB.getInstance(this).getOrderCount(tradingPoint.getTpCode());
        return String.format("%s %s", getString(R.string.order_number), ++count);
    }

    /**
     * Создаёт новую вкладку заказа
     *
     * @param title     Заголовок вкладки
     * @param canDelete будет ли возможность удалить вкладку
     */
    @SuppressLint("InflateParams")
    private void createTab(String title, boolean canDelete, boolean createEmptyPage, MadeOrderTemplate orderTemplate) {
        View view = LayoutInflater.from(this).inflate(R.layout.custome_tab, null);
        TextView textView = view.findViewById(R.id.tvTabTitle);
        textView.setText(title);
        textView.setOnClickListener(this);

        View delTab = view.findViewById(R.id.ibTabDel);
        delTab.setOnClickListener(this);
        delTab.setVisibility((canDelete ? View.VISIBLE : View.GONE));

        TabLayout.Tab newTab = tabLayout.newTab();
        newTab.setTag(title);
        newTab.setCustomView(view);
        tabLayout.addTab(newTab, tabLayout.getTabCount() - 1);

        textView.setTag(newTab);
        delTab.setTag(newTab);

        if (createEmptyPage) {
            MadeOrderTemplate madeOrderTemplate = new MadeOrderTemplate();
            madeOrderTemplate.setUserCode(AppCache.USER_INFO.getUserCode());
            madeOrderTemplate.setTpCode(tradingPoint.getTpCode());
            madeOrderTemplate.setTpName(tradingPoint.getTitle());
            madeOrderTemplate.setOrderTitle(title);
            orderItems.add(madeOrderTemplate);
        } else
            orderItems.add(orderTemplate);
        orderAdapter.notifyDataSetChanged();
    }

    /**
     * Попытка удалить вкладку и страницу заказа
     *
     * @param tab удаляемая вкладка
     */
    private void attemptDelTab(TabLayout.Tab tab) {
        final int id = tab.getPosition();
        if (tabLayout.getTabCount() > 1) {
            if (orderItems.get(id).getRowId() != -1) {
                DialogBox(this, getString(R.string.dialog_text_conf_del_order, orderItems.get(id).getOrderTitle()),
                        getString(R.string.dialog_btn_cancel), getString(R.string.dialog_btn_ok), null, new OnDialogBtnsClickListener() {
                            @Override
                            public void onBtnClick(View view, Intent intent) {
                                if ((view.getId() == R.id.dialogBtn2) && (AppDB.getInstance(MakeOrderNewActivity.this).deleteOrder(orderItems.get(id).getRowId())))
                                    deleteTab(id);
                            }
                        });
            } else
                deleteTab(id);
        }
    }

    /**
     * Удаляет вкладку и страницу заказа
     *
     * @param id удаляемая вкладка
     */
    private void deleteTab(int id) {
        int orderCounter = AppDB.getInstance(this).getOrderCount(tradingPoint.getTpCode());
        orderItems.remove(id);
        tabLayout.removeTabAt(id);

        for (int i = 0; orderItems.size() > i; i++) {
            MadeOrderTemplate order = orderItems.get(i);
            String title = order.getOrderTitle();

            if (TextUtils.isEmpty(order.getOrderCode()) && order.getRowId() == -1) {
                title = String.format("%s %s", getString(R.string.order_number), ++orderCounter);
                order.setOrderTitle(title);
            }

            TabLayout.Tab seqTab = tabLayout.getTabAt(i);
            if (seqTab != null && seqTab.getCustomView() != null)
                ((TextView) seqTab.getCustomView().findViewById(R.id.tvTabTitle)).setText(title);
        }

        orderAdapter = new MakeOrderItemsAdapter(getSupportFragmentManager());
        orderPages.setAdapter(orderAdapter);
        //orderAdapter.notifyDataSetChanged();
    }

    private void attemptSendOrSaveOrder() {
        if (orderItems.isEmpty()) {
            Toast.makeText(this, R.string.toast_nothing_to_send, Toast.LENGTH_SHORT).show();
            return;
        }

        int counter = 0;
        for (MadeOrderTemplate madeOrder : orderItems) {
            if (madeOrder.getGoodsList().isEmpty()) {
                //informDialog.cancel();
                InformDialog(String.format("%s. %s", madeOrder.getOrderTitle(), getString(R.string.dialog_text_goods_list_empty)), counter);
                return;
            }

            if (madeOrder.getUploadDateValue().equals("")) {
                //informDialog.cancel();
                InformDialog(String.format("%s. %s", madeOrder.getOrderTitle(), getString(R.string.dialog_text_delivery_date_empty)), counter);
                return;
            }

            if (madeOrder.isOnCredit()) {
                if (madeOrder.getCreditVisits().isEmpty()) {
                    InformDialog(String.format("%s. %s", madeOrder.getOrderTitle(), getString(R.string.dialog_text_visits_is_empty)), counter);
                    return;
                } else {
                    double sum = 0;
                    for (CreditVisitTemplate creditVisit : madeOrder.getCreditVisits()) {
                        if (TextUtils.isEmpty(creditVisit.getVisitDateValue())) {
                            InformDialog(String.format("%s. %s", madeOrder.getOrderTitle(),
                                    getString(R.string.dialog_text_visits_date_is_empty)), counter);
                            return;
                        }

                        if (TextUtils.isEmpty(creditVisit.getTakeSum())) {
                            InformDialog(String.format("%s. %s", madeOrder.getOrderTitle(),
                                    getString(R.string.dialog_text_visits_sum_is_empty)), counter);
                            return;
                        }

                        sum += Double.parseDouble(creditVisit.getTakeSum());
                    }

                    if (sum < madeOrder.getTotalPrice()) {
                        InformDialog(String.format("%s. %s %s", madeOrder.getOrderTitle(),
                                getString(R.string.dialog_text_visits_sum_mistake), (madeOrder.getTotalPrice() - sum)), counter);
                        return;
                    }
                }
            }
            counter++;
        }

        if ((orderItems.size() == 1 && orderItems.get(0).getLatitude() != 0) || (location != null)) {
            informDialog = getInformDialog(this, getString(R.string.label_prepare_to_send));
            informDialog.show();
            new CompleteMakeOrder(this).execute();
        } else
            geoLocationPermission();
    }

    /**
     * Отправляется запрос на получение деталей заказа,
     * либо загружает детали запроса с бд приложения, если заказ был сохранен на телефоне.
     */
    @SuppressLint("StaticFieldLeak")
    private class GetMadeOrderDetails extends AsyncTask<String, Void, MadeOrderTemplate> {
        @Override
        protected void onPreExecute() {
            informDialog = getInformDialog(MakeOrderNewActivity.this, getString(R.string.dialog_text_req_order_details));
            informDialog.show();
        }

        @Override
        protected MadeOrderTemplate doInBackground(String... params) {
            if (!TextUtils.isEmpty(order.getOrderCode())) {
                MadeOrderTemplate res = ReqOrderForEdit.load(MakeOrderNewActivity.this, params[0], params[1]);
                if (res != null) {
                    res.setOrderTitle(order.getTitle());
                    warehouseCode = res.getStockTemplate().getCode();
                }
                return res;
            } else {
                MadeOrderTemplate madeOrderForEdit = AppDB.getInstance(MakeOrderNewActivity.this).getMadeOrderForEdit(order.getId());
                warehouseCode = madeOrderForEdit.getStockTemplate().getCode();
                return madeOrderForEdit;
            }
        }

        @Override
        protected void onPostExecute(MadeOrderTemplate result) {
            informDialog.cancel();
            if (result != null)
                createTab(result.getOrderTitle(), (TextUtils.isEmpty(order.getOrderCode())), false, result);
            else {
                Toast.makeText(MakeOrderNewActivity.this, getString(R.string.toast_req_orders_det_fail), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    /**
     * Завершение создания заказов.
     * Отправляет на сервер либо сохраняет на телефоне
     */
    @SuppressLint("StaticFieldLeak")
    private class CompleteMakeOrder extends AsyncTask<Void, String, SendOrderResponseTemplate> {
        private Context context;
        private boolean successfyllySendOrSave = true;

        CompleteMakeOrder(Context context) {
            this.context = context;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            ((TextView) informDialog.findViewById(R.id.dialogSLabel)).setText(String.format("%s. %s", values[0], values[1]));
        }

        @Override
        protected SendOrderResponseTemplate doInBackground(Void... params) {
            int counter = 0;
            SendOrderResponseTemplate response = new SendOrderResponseTemplate();

            // @author MrJ
            String result = ReqLogin.authorization(AppCache.USER_INFO.getLogin(AppCache.USER_INFO.getProjectId(context), context),
                    getSharedPreferences(LoginActivity.mysettings, Context.MODE_PRIVATE).getString(LoginActivity.mpwdkey, ""));
            if (result.isEmpty()) {
                PS_ReqGetPriceListUpdateTime.load(context);
                boolean needToUpdatePrices = AppDB.getInstance(context).needToUpdatePriceTypes(0);
                if (needToUpdatePrices) {

                    publishProgress(getString(R.string.dialog_text_sending), "Загрузка вид цен");
                    ReqPriceType.load(context);
                    publishProgress(getString(R.string.dialog_text_sending), "Загрузка прайс лист");
                    ReqPriceList.load(context);


                    for (MadeOrderTemplate madeOrder : orderItems) {
                        // получаю новые цены товаров
                        HashMap<String, PriceTemplate> priceList = AppDB.getInstance(context).getPriceListByPriceType(madeOrder.getPriceType());

                        double totalPrice = 0;
                        if (madeOrder.getStatus() != OrderStatus.Sent) {
                            // список товаров заказа
                            Map<String, GoodsByBrandTemplate> goodList = madeOrder.getGoodsList();
                            // Обход список товаров заказа
                            for (Map.Entry<String, GoodsByBrandTemplate> goods : goodList.entrySet()) {
                                GoodsByBrandTemplate good = goodList.get(goods.getKey());

                                // Изменение цены и общей суммы
                                if (priceList.containsKey(good.getProductCode())) {
                                    PriceTemplate price = priceList.get(good.getProductCode());
                                    good.setOriginalPrice(price.getPrice());
                                    good.setDiscountValue(price.getDiscount());
                                    good.setPrice((price.getDiscount() == 0 ? price.getPrice() : price.getNewPrice()));
                                    good.setTotal(good.getAmount() * good.getPrice());
                                    totalPrice += good.getTotal();
                                }

                            }

                        }

                        if (madeOrder.getTotalPrice() != totalPrice) {
                            madeOrder.setTotalPrice(totalPrice);
                            changedOrders.add(madeOrder);
                        }
                    }
                }

                // получаю список сохранённых документов
                List<OrderTemplate> savedOrders = AppDB.getInstance(context).getSavedOrders();
                // Обход сохранённых документов
                for (OrderTemplate savedOrder : savedOrders) {
                    int valueOfStatus = savedOrder.getOrderStatus().getValue();
                    if (valueOfStatus != 299 || valueOfStatus != 300 || valueOfStatus != 301) {
                        continue;
                    }

                    // сохраняем текущую общую сумму
                    double oldTotalprice = savedOrder.getTotalPrice();
                    // получаю заказ по ID
                    MadeOrderTemplate madeOrderForEdit = AppDB.getInstance(context).getMadeOrderForEdit(savedOrder.getId());
                    // получаю типы цен товаров
                    List<PriceTypeTemplate> priceTypeTemplates = AppDB.getInstance(context).getPriceTypeList();
                    // Код типа цен текущего документа
                    String priceCode = "";
                    for (PriceTypeTemplate priceTypeTemplate : priceTypeTemplates) {
                        if (priceTypeTemplate.getName().equals(savedOrder.getPriceTypeName())) {
                            priceCode = priceTypeTemplate.getCode();
                            break;
                        }
                    }
                    // получаю новые цены товаров
                    HashMap<String, PriceTemplate> priceList = AppDB.getInstance(context).getPriceListByPriceType(priceCode);
                    // список товаров заказа
                    Map<String, GoodsByBrandTemplate> goodList = madeOrderForEdit.getGoodsList();
                    double totalPrice = 0;
                    // Обход список товаров заказа
                    for (Map.Entry<String, GoodsByBrandTemplate> goods : goodList.entrySet()) {
                        GoodsByBrandTemplate good = goodList.get(goods.getKey());

                        // Изменение цены и общей суммы
                        if (priceList.containsKey(good.getProductCode())) {
                            PriceTemplate price = priceList.get(good.getProductCode());
                            good.setOriginalPrice(price.getPrice());
                            good.setDiscountValue(price.getDiscount());
                            good.setPrice((price.getDiscount() == 0 ? price.getPrice() : price.getNewPrice()));
                            good.setTotal(good.getAmount() * good.getPrice());
                            totalPrice += good.getTotal();
                        }

                    }
                    madeOrderForEdit.setTotalPrice(totalPrice);
                    // сравнение итогов, если отличаются то тогда записываем в список
                    if (oldTotalprice != madeOrderForEdit.getTotalPrice()) {
                        changedOrders.add(madeOrderForEdit);
                        // Сохраняем документы в бд приложения
                        AppDB.getInstance(context).editOrder(madeOrderForEdit);
                    }
                }
                if (changedOrders.size() > 0) {
                    successfyllySendOrSave = false;
                    response.setResponseCode("-10");
                    response.setMessage("Ошибка! Произошла актуализация цен. Заново синхронизируйте заказ!");
                    return response;
                }
            }
            //

            for (MadeOrderTemplate madeOrder : orderItems) {
                if (madeOrder.getStatus() != OrderStatus.Sent) {
                    if (madeOrder.getLatitude() == 0) {
                        madeOrder.setLatitude(location.getLatitude());
                        madeOrder.setLongitude(location.getLongitude());
                    }
                    madeOrder.setStockTemplate(AppDB.getInstance(context).getWarehouse(warehouseCode));
                    madeOrder.setOrganizationTemplate(madeOrder.getStockTemplate().getOrg());
                    madeOrder.setCodeContract(contractCode);
                    if (madeOrder.isSaveLocal()) {
                        publishProgress(madeOrder.getOrderTitle(), getString(R.string.dialog_text_saving));
                        if (madeOrder.getRowId() == -1) {
                            madeOrder.setStatus(OrderStatus.SavedLocal);
                            madeOrder.setInfo("Сохранён на телефоне");
                            madeOrder.setRowId(AppDB.getInstance(context).saveOrder(madeOrder));
                            response.setMessage("Successfully created");
                        } else {
                            madeOrder.setInfo("Заказ отредактирован и сохранён на телефоне");
                            AppDB.getInstance(context).editOrder(madeOrder);
                            response.setMessage("Successfully edited");
                        }
                    } else {
                        // * Отправка заказа на сервер
                        publishProgress(getString(R.string.dialog_text_sending), madeOrder.getOrderTitle());
                        if (TextUtils.isEmpty(madeOrder.getOrderCode()))
                            response = ReqSendOrder.send(madeOrder);
                        else {
                            //response = ReqEditOrder.send(madeOrder);
                            response = new SendOrderResponseTemplate();
                            response.setResponseCode("-1");
                            response.setMessage("Нельзя отредактировать отправленный заказ!");
                        }

                        switch (response.getResponseCode()) {
                            // * неизвестная ошибка при отправке заказа
                            case "-1":
                                successfyllySendOrSave = false;
                                madeOrder.setStatus(OrderStatus.NeedToSend);
                                madeOrder.setInfo(response.getMessage());
                                break;
                            // * не хватает товара на складе
                            case "2":
                                successfyllySendOrSave = false;

                                response.setOrderId(counter);
                                response.setOrderTitle(madeOrder.getOrderTitle());

                                madeOrder.setNotEnoughGoods(response.getGoodsList());
                                madeOrder.setNotEnoughByBrand(response.getGoodsListByBrand());
                                madeOrder.setNotEnoughBySeries(response.getGoodsListBySeries());
                                madeOrder.setStatus(OrderStatus.NotEnoughGoods);
                                madeOrder.setInfo("Не хватает товара на складе");

                                return response;
                            case "0":
                                successfyllySendOrSave = false;
                                madeOrder.setInfo(response.getMessage());
                                return response;
                            default:
                                madeOrder.setInfo("Успешно отправлен");
                                if (madeOrder.getRowId() != -1)
                                    AppDB.getInstance(context).deleteOrder(madeOrder.getRowId());
                                madeOrder.setStatus(OrderStatus.Sent);
                                break;
                        }
                    }
                }
                counter++;
            }
            return response;
        }

        @Override
        protected void onPostExecute(SendOrderResponseTemplate result) {
            informDialog.cancel();
            if (!successfyllySendOrSave) {
                orderAdapter = new MakeOrderItemsAdapter(getSupportFragmentManager());
                orderPages.setAdapter(orderAdapter);

                if (result.getResponseCode().equals("2"))
                    // @author MrJ
                    // InformDialog(String.format("%s. %s", result.getOrderTitle(), result.getMessage()), result.getOrderId());
                    InformDialogNew(String.format("%s", result.getMessage()), result.getOrderId());
                    //
                else if (result.getResponseCode().equals("-10")) {
                    if (changedOrders.size() > 0) {
                        LayoutInflater lf = LayoutInflater.from(MakeOrderNewActivity.this);
                        AlertDialog.Builder adb = new AlertDialog.Builder(MakeOrderNewActivity.this);
                        ListAdapter adapter = new PS_orderListAdapter(lf, changedOrders);
                        adb.setAdapter(adapter, null);
                        adb.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                                changedOrders.clear();
                            }
                        });
                        adb.setTitle("Внимание! Обнаружено обновление цен!");
                        adb.create().show();
                        Toast.makeText(MakeOrderNewActivity.this, "Суммы сохраненных и открытых заказов актуализированы!\n" +
                                "Повторите отправку заказа!", Toast.LENGTH_LONG).show();
                    }
                }
                return;
            }

            Toast.makeText(context, result.getMessage(), Toast.LENGTH_SHORT).show();
            startService(new Intent(context, ReloadProductIntentService.class));
            finish();

            Intent iNotif = new Intent(context, MainService.class);
            iNotif.setAction(C.ACTIONS.NOTIF_ABOUT_VISIT);
            iNotif.putExtra(C.KEYS.EXTRA_DATA_TP, tradingPoint.getTpCode());
            startService(iNotif);
        }
    }
}