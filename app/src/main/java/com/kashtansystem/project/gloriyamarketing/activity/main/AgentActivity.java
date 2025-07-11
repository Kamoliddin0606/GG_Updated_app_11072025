package com.kashtansystem.project.gloriyamarketing.activity.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.activity.agent.TradingPointsActivity;
import com.kashtansystem.project.gloriyamarketing.activity.agent.TradingPointsByInnActivity;
import com.kashtansystem.project.gloriyamarketing.adapters.PS_orderListAdapter;
import com.kashtansystem.project.gloriyamarketing.core.OfflineManager;
import com.kashtansystem.project.gloriyamarketing.database.AppDB;
import com.kashtansystem.project.gloriyamarketing.models.listener.OnDialogBtnsClickListener;
import com.kashtansystem.project.gloriyamarketing.models.template.GoodsByBrandTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.MadeOrderTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.OrderTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.PS_CitiesDistrictContract;
import com.kashtansystem.project.gloriyamarketing.models.template.PriceTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.PriceTypeTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.UserTemplate;
import com.kashtansystem.project.gloriyamarketing.net.soap.PS_CitiesDistrictContractingList;
import com.kashtansystem.project.gloriyamarketing.net.soap.PS_ReqGetPriceListUpdateTime;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqBusinessRegions;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqDocumentTypes;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqGetClients;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqGetKPI;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqGetOrganization;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqGetRefusalList;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqGetWarehouse;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqPriceList;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqPriceType;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqProductBalance;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqSpecEvents;
import com.kashtansystem.project.gloriyamarketing.service.MainService;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.AppPermissions;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.L;
import com.kashtansystem.project.gloriyamarketing.utils.UserType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by FlameKaf on 10.05.2017.
 * ----------------------------------
 * Реализует интерфейс Агента, Экспедитора, Супервайзера и Руководителя.
 */

public class AgentActivity extends BaseActivity implements View.OnClickListener {

    // Массив элементов для вывода в диалог пользователью
    List<MadeOrderTemplate> changedOrders = new ArrayList<>();

    private OnDialogBtnsClickListener onExitListener = new OnDialogBtnsClickListener() {
        @Override
        public void onBtnClick(View view, Intent intent) {
            if (view.getId() == R.id.dialogBtn2) {
                if (intent != null)
                    startActivity(intent);
                AppCache.USER_INFO = new UserTemplate();
                finish();
            }
        }
    };

    @Override
    public String getActionBarTitle() {
        return UserType.getTypeNameByValue(AppCache.USER_INFO.getUserType(), this);
    }

    @Override
    public boolean getHomeButtonEnable() {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent);
        setSupportActionBar((Toolbar) findViewById(R.id.appToolBar));
        Init();
    }

    @Override
    public void onBackPressed() {
        DialogBox(this, getString(R.string.dialog_text_ask_to_exit),
                getString(R.string.dialog_btn_cancel), getString(R.string.dialog_btn_exit), null, onExitListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.agent_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.mSettings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.mLogout:
                DialogBox(this, getString(R.string.dialog_text_ask_to_logout),
                        getString(R.string.dialog_btn_cancel), getString(R.string.dialog_btn_exit),
                        new Intent(this, LoginActivity.class), onExitListener);
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onPermissionGrantResult(AppPermissions permission, boolean result) {
        if (result) {
            if (permission == AppPermissions.GeoLocation) {
                startService(new Intent(this, MainService.class));
                startActivity(new Intent(AgentActivity.this, TradingPointsActivity.class));
            }
        }
    }

    @Override
    public void onClick(View view) {
        Log.println(Log.DEBUG, "dightmerc1", String.valueOf(view.getId()));
        switch (view.getId()) {
            case R.id.btnUpdateKpi:
                new LoadKpiWithButton().execute();
                break;
            default:
                if (AppCache.USER_INFO.getUserType() == UserType.Agent) {
                    if (isGPStatusOK())
                        geoLocationPermission();
                    return;
                }
                Log.println(Log.DEBUG, "dightmerc1", view.getTag().toString());
                startActivity((Intent) view.getTag());
                break;
        }
    }

    private void Init() {
        if (TextUtils.isEmpty(AppCache.USER_INFO.getUserCode()))
            return;

        ((TextView) findViewById(R.id.tvUserName)).setText(AppCache.USER_INFO.getUserName());

        findViewById(R.id.cvTask).setOnClickListener(this);
        findViewById(R.id.cvInnTask).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AgentActivity.this, TradingPointsByInnActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btnUpdateKpi).setOnClickListener(this);

        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putString(C.KEYS.USER_CODE, AppCache.USER_INFO.getUserCode()).apply();

        if (!OfflineManager.INSTANCE.getGoOffline())
            new LoadAgentReferences(this).execute();

    }

    /**
     * Загрузка справочников агента.
     */
    @SuppressLint("StaticFieldLeak")
    private class LoadAgentReferences extends AsyncTask<Void, Integer, Void> {
        private final int[] msgs =
                {
                        R.string.dialog_text_load_bus_regions,
                        R.string.load_organization,
                        R.string.load_stocks,
                        R.string.dialog_text_load_price_type_updateTime,
                        R.string.dialog_text_load_price_type,
                        R.string.dialog_text_load_price,
                        R.string.dialog_text_load_product,
                        R.string.dialog_text_download_clients,
                        R.string.dialog_text_load_discount,
                        R.string.dialog_text_load_documents,
                        R.string.dialog_text_load_read_district_city

                };
        private Dialog dialog;
        private Context context;

        LoadAgentReferences(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            dialog = getInformDialog(AgentActivity.this, getString(msgs[0]));
            dialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            ((TextView) dialog.findViewById(R.id.dialogSLabel)).setText(getString(msgs[values[0]]));
        }

        @Override
        protected Void doInBackground(Void... params) {
            Log.println(Log.DEBUG, "dightmerc", "do reqs");
            ReqGetRefusalList.load(context); // Обновление списка причин отказов

            int days = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context)
                    .getString(C.KEYS.SYNC_FREQUENCY_PREF_REGIONS, "7"));
            L.info("sync frequency pref business regions: " + days);
            if (AppDB.getInstance(context).needToUpdateBusRegions(days))
                ReqBusinessRegions.load(context);

            days = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context)
                    .getString(C.KEYS.SYNC_FREQUENCY_PREF_PT, "7"));
            L.info("sync frequency pref price types: " + days);

            publishProgress(1);
            ReqGetOrganization.load(AgentActivity.this);

            publishProgress(2);
            ReqGetWarehouse.load(AgentActivity.this);

            // @author MrJ
            // TODO загружаю updateTime для виды цен
            publishProgress(3);
            PS_ReqGetPriceListUpdateTime.load(context);
            // -->

            if (AppDB.getInstance(context).needToUpdatePriceTypes(days)) {
                publishProgress(4);
                ReqPriceType.load(context);
                publishProgress(5);
                ReqPriceList.load(context);
            }

            publishProgress(6);

            ReqProductBalance.load(context);

            days = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context)
                    .getString(C.KEYS.SYNC_FREQUENCY_PREF_CLIENTS, "7"));
            L.info("sync frequency pref clients: " + days);
            if (AppDB.getInstance(context).needToUpdateClients(days)) {
                publishProgress(7);
                AppDB.getInstance(context).saveClients(ReqGetClients.load());
            }

            publishProgress(8);
            ReqSpecEvents.load(context);
            ReqDocumentTypes.load(context);

            // @author MrJ
            // TODO проверка - когда цена изменилось со стороны API, пересчитаем цены и выведем в список изменённые документы
            if (AppDB.getInstance(context).needToUpdatePriceTypes(days)) {
                publishProgress(9);

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
            }
            publishProgress(10);
            LinkedList<PS_CitiesDistrictContract> ct = PS_CitiesDistrictContractingList.load();
//            Log.println(Log.DEBUG, "dightmerc", ct.toString());
            AppDB.getInstance(context).saveDistrictCities(ct);
            //

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (dialog.isShowing())
                dialog.cancel();

            new LoadKpi().execute();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadKpi extends AsyncTask<Void, Integer, Void> {
        private Dialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = getInformDialog(AgentActivity.this, getString(R.string.dlg_txt_kpi_load));
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ReqGetKPI.load();
            PS_ReqGetPriceListUpdateTime.loadLastSellDate(AgentActivity.this);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (dialog.isShowing())
                dialog.cancel();

            ((TextView) findViewById(R.id.kpiPlan)).setText(AppCache.USER_INFO.getPlan());
            ((TextView) findViewById(R.id.kpiInFact)).setText(AppCache.USER_INFO.getFact());
            ((TextView) findViewById(R.id.okb)).setText(String.format("%s", AppCache.USER_INFO.getOkb()));
            ((TextView) findViewById(R.id.akbPlan)).setText(Integer.toString(AppCache.USER_INFO.getAkbPlan()));
            ((TextView) findViewById(R.id.akbFact)).setText(Integer.toString(AppCache.USER_INFO.getAkbFact()));
            ((TextView) findViewById(R.id.akbPercent)).setText(AppCache.USER_INFO.getAkbPercent());
            ((TextView) findViewById(R.id.kpiUpdateDate)).setText(AppCache.USER_INFO.getKpiUpdatedDate());

            // Если существует изменённые документы пока выводим в логи
            if (changedOrders.size() > 0) {
                LayoutInflater lf = LayoutInflater.from(AgentActivity.this);
                AlertDialog.Builder adb = new AlertDialog.Builder(AgentActivity.this);
                ListAdapter adapter = new PS_orderListAdapter(lf, changedOrders);
                adb.setAdapter(adapter, null);
                adb.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                adb.setTitle("Цены товаров были изменены. Суммы следующих заказов были пересчитаны");
                adb.create().show();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadKpiWithButton extends AsyncTask<Void, Integer, Void> {
        private Dialog dialog;

        @Override
        protected void onPreExecute() {
            dialog = getInformDialog(AgentActivity.this, getString(R.string.dlg_txt_kpi_load));
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            ReqGetKPI.load();
            PS_ReqGetPriceListUpdateTime.loadLastSellDate(AgentActivity.this);
            // @author MrJ
            // TODO загружаю updateTime для виды цен
            publishProgress(3);
            PS_ReqGetPriceListUpdateTime.load(AgentActivity.this);
            //
            if (AppDB.getInstance(AgentActivity.this).needToUpdatePriceTypes(0)) {
                publishProgress(4);
                ReqPriceType.load(AgentActivity.this);
                publishProgress(5);
                ReqPriceList.load(AgentActivity.this);
            }
            AppDB.getInstance(AgentActivity.this).saveDistrictCities(PS_CitiesDistrictContractingList.load());
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (dialog.isShowing())
                dialog.cancel();

            ((TextView) findViewById(R.id.kpiPlan)).setText(AppCache.USER_INFO.getPlan());
            ((TextView) findViewById(R.id.kpiInFact)).setText(AppCache.USER_INFO.getFact());
            ((TextView) findViewById(R.id.okb)).setText(String.format("%s", AppCache.USER_INFO.getOkb()));
            ((TextView) findViewById(R.id.akbPlan)).setText(Integer.toString(AppCache.USER_INFO.getAkbPlan()));
            ((TextView) findViewById(R.id.akbFact)).setText(Integer.toString(AppCache.USER_INFO.getAkbFact()));
            ((TextView) findViewById(R.id.akbPercent)).setText(AppCache.USER_INFO.getAkbPercent());
            ((TextView) findViewById(R.id.kpiUpdateDate)).setText(AppCache.USER_INFO.getKpiUpdatedDate());

            // Если существует изменённые документы пока выводим в логи
            if (changedOrders.size() > 0) {
                LayoutInflater lf = LayoutInflater.from(AgentActivity.this);
                AlertDialog.Builder adb = new AlertDialog.Builder(AgentActivity.this);
                ListAdapter adapter = new PS_orderListAdapter(lf, changedOrders);
                adb.setAdapter(adapter, null);
                adb.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                adb.setTitle("Цены товаров были изменены. Суммы следующих заказов были пересчитаны");
                adb.create().show();
            }
        }
    }
}