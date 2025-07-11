package com.kashtansystem.project.gloriyamarketing.activity.agent;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.activity.main.BaseActivity;
import com.kashtansystem.project.gloriyamarketing.adapters.TradingPointsElvAdapter;
import com.kashtansystem.project.gloriyamarketing.database.AppDB;
import com.kashtansystem.project.gloriyamarketing.models.listener.OnDialogBtnsClickListener;
import com.kashtansystem.project.gloriyamarketing.models.template.RefusalTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.TradingPointTemplate;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqSetRefusal;
import com.kashtansystem.project.gloriyamarketing.service.MainService;
import com.kashtansystem.project.gloriyamarketing.utils.AppPermissions;
import com.kashtansystem.project.gloriyamarketing.utils.C;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by FlameKaf on 02.05.2017.
 * ----------------------------------
 * Список торговых точек (по заданию).
 */

public class TradingPointsActivity extends BaseActivity implements View.OnClickListener,
        SearchView.OnQueryTextListener, SearchView.OnCloseListener {
    private TradingPointsElvAdapter adapter;
    private Dialog dialog;
    private String tpCode = "";

    private Dialog refusalDialog;
    private RefusalSpinnerAdapter refusalAdapter;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action) && action.equals(C.ACTIONS.NOTIF_ABOUT_VISIT)) {
                if (dialog != null && dialog.isShowing())
                    dialog.cancel();
                Toast.makeText(TradingPointsActivity.this, intent.getStringArrayExtra(C.KEYS.EXTRA_DATA)[1], Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    public String getActionBarTitle() {
        return getString(R.string.app_title_tp);
    }

    @Override
    public boolean getHomeButtonEnable() {
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trading_points);
        setSupportActionBar((Toolbar) findViewById(R.id.appToolBar));

        ExpandableListView clients = (ExpandableListView) findViewById(R.id.elvTPoints);
        adapter = new TradingPointsElvAdapter(this, this);
        clients.setAdapter(adapter);

        refusalDialog = new Dialog(this, R.style.dialogWithTitle);
        refusalDialog.setCanceledOnTouchOutside(false);
        refusalDialog.setCancelable(false);
        refusalDialog.setContentView(R.layout.dialog_refusal);
        refusalDialog.setTitle(getString(R.string.dialog_refusal_hint));

        refusalAdapter = new RefusalSpinnerAdapter(this, null);
        ((AppCompatSpinner) refusalDialog.findViewById(R.id.sRefusal)).setAdapter(refusalAdapter);

        Window window = refusalDialog.getWindow();
        if (window != null)
            window.setLayout(-1, -2);

        View.OnClickListener onClickListenerRefusalDialog = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.btnDlgAct2) {
                    new SendRefusal(((TradingPointTemplate) adapter.getChild((Integer) view.getTag(), 0)).getTpCode(),
                            ((AppCompatSpinner) refusalDialog.findViewById(R.id.sRefusal))
                                    .getSelectedView().findViewById(R.id.labelText).getTag().toString(),
                            TradingPointsActivity.this).execute();
                } else
                    refusalDialog.cancel();
            }
        };

        refusalDialog.findViewById(R.id.btnDlgAct1).setOnClickListener(onClickListenerRefusalDialog);
        refusalDialog.findViewById(R.id.btnDlgAct2).setOnClickListener(onClickListenerRefusalDialog);

        findViewById(R.id.appProgressBar).setVisibility(View.VISIBLE);
        new Init().execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(C.ACTIONS.NOTIF_ABOUT_VISIT));
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case C.REQUEST_CODES.CREATE_NEW_CLIENT:
                    adapter.addItem((TradingPointTemplate) data.getParcelableExtra(C.KEYS.EXTRA_DATA));
                    break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.trading_point_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.mSearch));
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
            searchView.setOnQueryTextListener(this);
            searchView.setOnCloseListener(this);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.mOrders:
                startActivity(new Intent(this, CreatedOrdersActivity.class));
                break;
            case R.id.mNewClient:
                startActivityForResult(new Intent(this, NewTradingPointActivity.class), C.REQUEST_CODES.CREATE_NEW_CLIENT);
                break;
            case R.id.mOrdersHistory:
                startActivity(new Intent(this, OrdersHistoryActivity.class));
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onPermissionGrantResult(AppPermissions permission, boolean result) {
        if (result && permission == AppPermissions.GeoLocation) {
            dialog = getCancelableInformDialog(this, getString(R.string.dialog_text_sending2));
            dialog.show();
            if (isMyServiceRunning(MainService.class)) {
                stopService(new Intent(this, MainService.class));
            }
            Intent iNotif = new Intent(this, MainService.class);
            iNotif.setAction(C.ACTIONS.NOTIF_ABOUT_VISIT);
            iNotif.putExtra(C.KEYS.EXTRA_DATA_TP, tpCode);
            startService(iNotif);

        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private long mLastClickTime = 0;

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivInformVisited:
                if (isGPStatusOK()) {
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    tpCode = ((TradingPointTemplate) adapter.getChild((Integer) view.getTag(), 0)).getTpCode();
                    geoLocationPermission();
                }
                break;
            case R.id.tvElvContPersonPhone:
                makeCallAction(((TradingPointTemplate) adapter.getChild((Integer) view.getTag(), 0)).getContactPersonPhone());
                break;
            case R.id.btnElvWork:
                Intent intent = new Intent(this, PickWarehouseActivity.class);
                intent.setAction(C.ACTIONS.ACTION_CREATE_ORDER);
                intent.putExtra(C.KEYS.EXTRA_DATA_TP, (TradingPointTemplate) adapter.getChild((Integer) view.getTag(), 0));
                startActivity(intent);
                //todo delete
//                Intent iMakeOrder = new Intent(TradingPointsActivity.this, MakeOrderNewActivity.class);
//                iMakeOrder.setAction(C.ACTIONS.ACTION_CREATE_ORDER);
//                iMakeOrder.putExtra(C.KEYS.EXTRA_DATA_TP, (TradingPointTemplate) adapter.getChild((Integer) view.getTag(), 0));
//                startActivity(iMakeOrder);
                break;
            case R.id.btnContact:
                startActivity(ContractsActivity.Companion.getInstance(this, false, (TradingPointTemplate) adapter.getChild((Integer) view.getTag(), 0)));
                break;
            // Оформление отказа
            case R.id.btnRefusal:
                // Перенесем код торговой точки как тег
                View vOK = refusalDialog.findViewById(R.id.btnDlgAct2);
                vOK.setTag(view.getTag());
                // Отобразим диалоговое окно
                refusalDialog.show();
                break;
        }
    }

    @Override
    public boolean onClose() {
        adapter.filterData("");
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        adapter.filterData(query);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        adapter.filterData(query);
        return false;
    }

    /**
     * Выполнение звонка
     */
    private void makeCallAction(String phoneNum) {
        if (TextUtils.isEmpty(phoneNum))
            Toast.makeText(this, "Номер телефона не указан", Toast.LENGTH_SHORT).show();
        else {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + phoneNum));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    /**
     * Инициализация:
     * 1) legacy Выгрузка торговых точек по заданию на текущий день
     * 2) Получение списка причин отказов из базы
     */
    @SuppressLint("StaticFieldLeak")
    private class Init extends AsyncTask<Void, Void, Pair<LinkedList<TradingPointTemplate>, ArrayList<RefusalTemplate>>> {
        @Override
        protected Pair<LinkedList<TradingPointTemplate>, ArrayList<RefusalTemplate>> doInBackground(Void... params) {
            return new Pair(AppDB.getInstance(TradingPointsActivity.this).getClientsList(),
                    AppDB.getInstance(TradingPointsActivity.this).getRefusals());
        }

        @Override
        protected void onPostExecute(Pair<LinkedList<TradingPointTemplate>, ArrayList<RefusalTemplate>> result) {
            if (!result.first.isEmpty()) {
                adapter.setContentList(result.first);
                adapter.notifyDataSetChanged();
            }
            // Заполним список причин отказов в диалоге
            refusalAdapter.setContentList(result.second);
            refusalAdapter.notifyDataSetChanged();
            // Получение данных завершено
            findViewById(R.id.appProgressBar).setVisibility(View.GONE);
        }
    }

    /**
     * Отправка отказа
     */
    @SuppressLint("StaticFieldLeak")
    private class SendRefusal extends AsyncTask<Void, Void, String[]> {
        private String codeAgent;
        private String codeClient;
        private String codeRefusal;
        private Context context;

        SendRefusal(String codeClient, String codeRefusal, Context context) {
            codeAgent = PreferenceManager.getDefaultSharedPreferences(TradingPointsActivity.this)
                    .getString(C.KEYS.USER_CODE, "0");
            this.codeClient = codeClient;
            this.codeRefusal = codeRefusal;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            refusalDialog.findViewById(R.id.llTransferInfo).setVisibility(View.VISIBLE);
            ((TextView) refusalDialog.findViewById(R.id.tvTransferInfo)).setText(R.string.dialog_text_sending_data);
        }

        @Override
        protected String[] doInBackground(Void... params) {
            return ReqSetRefusal.send(codeAgent, codeClient, codeRefusal);
        }

        @Override
        protected void onPostExecute(String[] result) {
            refusalDialog.findViewById(R.id.llTransferInfo).setVisibility(View.GONE);
            //if (result[0].equals("1"))
            refusalDialog.cancel();
            DialogBox(context, result[1], getString(R.string.dialog_btn_cancel), getString(R.string.dialog_btn_ok), null, null);
        }
    }

    /**
     * Адаптер списка причин отказов
     */
    public class RefusalSpinnerAdapter extends BaseAdapter {
        private ArrayList<RefusalTemplate> items;
        private Context context;

        public RefusalSpinnerAdapter(Context context, ArrayList<RefusalTemplate> in) {
            this.context = context;
            if (in != null) {
                items = new ArrayList<>(in.size());
                items.addAll(in);
            }
        }

        @Override
        public int getCount() {
            return (items == null ? 0 : items.size());
        }

        @Override
        public Object getItem(int i) {
            return items.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null)
                view = LayoutInflater.from(context).inflate(R.layout.spinner_item, null);

            TextView tvRefusal = view.findViewById(R.id.labelText);
            tvRefusal.setText(items.get(i).getName());
            tvRefusal.setTag(items.get(i).getCode());

            return view;
        }

        public void setContentList(ArrayList<RefusalTemplate> in) {
            if (in != null) {
                items = new ArrayList<>(in.size());
                items.addAll(in);
            }
        }
    }

}