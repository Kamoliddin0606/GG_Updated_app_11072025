package com.kashtansystem.project.gloriyamarketing.activity.agent;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.activity.main.BaseActivity;
import com.kashtansystem.project.gloriyamarketing.adapters.BusinessRegAdapter;
import com.kashtansystem.project.gloriyamarketing.database.AppDB;
import com.kashtansystem.project.gloriyamarketing.models.listener.OnDialogBtnsClickListener;
import com.kashtansystem.project.gloriyamarketing.models.template.BusinessRegionsTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.TradingPointTemplate;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqGetMFO;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqGetTradePointTypeList;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqNewClient;
import com.kashtansystem.project.gloriyamarketing.utils.AppPermissions;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.GPS;
import com.kashtansystem.project.gloriyamarketing.utils.GPStatus;
import com.kashtansystem.project.gloriyamarketing.utils.L;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by FlameKaf on 28.05.2017.
 * ----------------------------------
 * Создание новой торговой точки
 */

public class NewTradingPointActivity extends BaseActivity implements View.OnClickListener,
        TextView.OnEditorActionListener, GPS.OnCoordinatsReceiveListener, TextWatcher {
    private EditText tpName;
    private EditText ntpMFO;
    private LatLng tpLoc;
    private GPS gps;
    private boolean attemptSave = false;
    private Dialog dialog;
    Disposable disposable;
    Disposable mfoDisposable;
    private TradingPointTemplate tradingPointTemplate;
    List<String> listTempTPType = new ArrayList<>();

    public static String TRADING_POINT = "TRADING_POINT_TP";
    public static String TRADING_POINT_INN = "TRADING_POINT_INN";

    String tpINN = "";

    @Override
    public String getActionBarTitle() {
        return getString(R.string.app_title_new_tp);
    }

    @Override
    public boolean getHomeButtonEnable() {
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_trading_point);
        setSupportActionBar((Toolbar) findViewById(R.id.appToolBar));

        setResult(RESULT_CANCELED);

        if(getIntent().hasExtra(TRADING_POINT_INN)){
            ((EditText) findViewById(R.id.ntpInn)).setText( getIntent().getExtras().getString(TRADING_POINT_INN));
        }else if (getIntent().hasExtra(TRADING_POINT)) {
            tradingPointTemplate = (new Gson()).fromJson(getIntent().getExtras().getString(TRADING_POINT), TradingPointTemplate.class);
        }

        disposable = Single.<List<String>>create(new SingleOnSubscribe<List<String>>() {
            @Override
            public void subscribe(SingleEmitter<List<String>> emitter) throws Exception {
                emitter.onSuccess(ReqGetTradePointTypeList.load(NewTradingPointActivity.this));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<List<String>>() {
                    @Override
                    public void accept(List<String> strings) throws Exception {
                        listTempTPType = strings;
                        ArrayAdapter adapter = new ArrayAdapter(NewTradingPointActivity.this, R.layout.spinner_item_tv, strings);
                        ((Spinner)findViewById(R.id.ntpType)).setAdapter(adapter);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                    }
                });

        gps = new GPS(this, this);
        gps.setInterval(1000);

        findViewById(R.id.ntpSave).setOnClickListener(this);
        findViewById(R.id.ntpCurLoc).setOnClickListener(this);
        ((EditText) findViewById(R.id.ntpResp)).setOnEditorActionListener(this);

        AppCompatSpinner priceTypeSpinner = (AppCompatSpinner) findViewById(R.id.businessReg);
        ArrayList<BusinessRegionsTemplate> businessRegions = AppDB.getInstance(this).getBusinessRegions();
        priceTypeSpinner.setAdapter(new BusinessRegAdapter(this, businessRegions));

        tpName = (EditText) findViewById(R.id.ntpLegalName);
        tpName.addTextChangedListener(this);

        ntpMFO = (EditText) findViewById(R.id.ntpMFO);

        ntpMFO.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(ntpMFO.getText().length() == 5){
                    if(mfoDisposable!=null){
                        mfoDisposable.dispose();
                    }

                    mfoDisposable = Single.create(new SingleOnSubscribe<String>() {
                        @Override
                        public void subscribe(SingleEmitter<String> emitter) throws Exception {
                            emitter.onSuccess(ReqGetMFO.load(NewTradingPointActivity.this,ntpMFO.getText().toString()));
                        }
                    }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<String>() {
                                @Override
                                public void accept(String strings) throws Exception {
                                    ((TextView)findViewById(R.id.tvMfo)).setText(strings);
                                    ((TextView)findViewById(R.id.tvMfo)).setVisibility(View.VISIBLE);
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    throwable.printStackTrace();
                                    ((TextView)findViewById(R.id.tvMfo)).setText("");
                                    ((TextView)findViewById(R.id.tvMfo)).setVisibility(View.GONE);
                                }
                            });
                }else {
                    ((TextView)findViewById(R.id.tvMfo)).setText("");
                    ((TextView)findViewById(R.id.tvMfo)).setVisibility(View.GONE);
                }
            }
        });

        if (tradingPointTemplate != null) {
            ((EditText) findViewById(R.id.ntpLegalName)).setText(tradingPointTemplate.getTitle());
            ((EditText) findViewById(R.id.ntpInn)).setText(tradingPointTemplate.getInn());
            ((EditText) findViewById(R.id.ntpSignboard)).setText(tradingPointTemplate.getSignboard());

            ((EditText) findViewById(R.id.ntpContact)).setText(tradingPointTemplate.getContactPerson());
            ((EditText) findViewById(R.id.ntpContactPhone)).setText(tradingPointTemplate.getContactPersonPhone());
            ((EditText) findViewById(R.id.ntpFio)).setText(tradingPointTemplate.getDirector());
            ((EditText) findViewById(R.id.ntpLegalAddress)).setText(tradingPointTemplate.getLegalAddress());
            ((EditText) findViewById(R.id.ntpAddress)).setText(tradingPointTemplate.getAddress());
            ((EditText) findViewById(R.id.ntpReferencePoint)).setText(tradingPointTemplate.getReferencePoint());
            ((EditText) findViewById(R.id.ntpResp)).setText(tradingPointTemplate.getRespPersonPhone());
            ((EditText) findViewById(R.id.ntpMFO)).setText(tradingPointTemplate.getMfo());
            ((EditText) findViewById(R.id.ntpRS)).setText(tradingPointTemplate.getRc());

            for (BusinessRegionsTemplate br : businessRegions) {
                if (tradingPointTemplate.getBusinessRegionsCode().equals(br.getCode())) {
                    priceTypeSpinner.setSelection(businessRegions.indexOf(br));
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if ((tpLoc == null) && (dialog != null && dialog.isShowing()))
            dialog.cancel();
        gps.stopRequest();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            ((EditText) findViewById(R.id.ntpAddress)).setText(data.getStringExtra(C.KEYS.EXTRA_DATA));
            tpLoc = data.getParcelableExtra(C.KEYS.EXTRA_LOC);
        }
    }

    @Override
    public void onPermissionGrantResult(AppPermissions permission, boolean result) {
        if (permission == AppPermissions.GeoLocation && result) {
            if (!attemptSave) {
                Intent intent = new Intent(this, NavigationActivity.class);
                startActivityForResult(intent, 0);
            } else
                gps.startRequest();
        }
    }

    @Override
    public void onReceiveCoordinats(GPStatus status, Location location, boolean fromMock) {
        switch (status) {
            case Success:
            case LastKnownLocation:
                if ((GPStatus.LastKnownLocation == status) && (location == null)) {
                    gps.stopRequest();
                    dialog.cancel();
                    Toast.makeText(this, "Не удалось определить местоположение. Убедитесь что GPS на телефоне включён!",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                L.info(String.format("latitude: %s; longitude: %s; location type: %s; fromMock: %s",
                        location.getLatitude(), location.getLongitude(), location.getProvider(), fromMock));

                if (!location.getProvider().equals("fused")) {
                    gps.stopRequest();
                    if (fromMock) {
                        dialog.cancel();
                        DialogBox(this, getString(R.string.dialog_text_mock_is_using),
                                getString(R.string.dialog_btn_cancel), getString(R.string.dialog_btn_ok), null, null);
                        return;
                    }
                    tpLoc = new LatLng(location.getLatitude(), location.getLongitude());
                    new SaveTradingPoint(this).execute();
                }
                break;
            case GpsProviderDisabled:
            case NetworkProviderDisabled:
                gps.stopRequest();
                dialog.cancel();
                isGPStatusOK();
                break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ntpSave:
                attemptSaveNewTp();
                break;
            case R.id.ntpCurLoc:
                attemptSave = false;
                geoLocationPermission();
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE)
            attemptSaveNewTp();
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable et) {
        String s = et.toString();
        if (!s.equals(s.toUpperCase()))
            tpName.setText(s.toUpperCase());
        tpName.setSelection(tpName.getText().length());
    }

    private void attemptSaveNewTp() {
        View view = ((AppCompatSpinner) findViewById(R.id.businessReg)).getSelectedView();
        if (view == null) {
            DialogBox(this, getString(R.string.dialog_text_bus_reg_empty), getString(R.string.dialog_btn_cancel),
                    getString(R.string.dialog_btn_ok), null, null);
            return;
        }
        if (check((EditText) findViewById(R.id.ntpLegalName)))
            return;
        if (check((EditText) findViewById(R.id.ntpInn)))
            return;
        if (check((EditText) findViewById(R.id.ntpLegalAddress)))
            return;
        if (check((EditText) findViewById(R.id.ntpAddress)))
            return;

        if (check((EditText) findViewById(R.id.ntpFio)))
            return;

        if(check(ntpMFO, 5))
            return;

        if(check((EditText) findViewById(R.id.ntpRS),20 ))
            return;

        dialog = getInformDialog(this, getString(R.string.dialog_text_req_coordinates));
        dialog.show();

        attemptSave = true;
        if (tpLoc == null)
            geoLocationPermission();
        else
            new SaveTradingPoint(this).execute();
    }

    /**
     * Проверяет, заполненно ли поле
     *
     * @return true, если поле пустое и его необходимо заполнить, в противном случае false
     */
    private boolean check(EditText editText) {
        editText.setError(null);
        if (TextUtils.isEmpty(editText.getText())) {
            editText.setError(getString(R.string.e_field_required));
            editText.requestFocus();
            return true;
        }
        return false;
    }

    private boolean check(EditText editText,int length) {
        editText.setError(null);
        if (editText.getText().length() != length) {
            editText.setError(getString(R.string.e_field_required));
            editText.requestFocus();
            return true;
        }
        return false;
    }

    /**
     * Отправка данных по новому клиенту
     */
    @SuppressLint("StaticFieldLeak")
    private class SaveTradingPoint extends AsyncTask<Void, Void, String[]> {
        private TradingPointTemplate sendData;
        private Context context;

        SaveTradingPoint(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            ((TextView) dialog.findViewById(R.id.dialogSLabel)).setText(getString(R.string.dialog_text_client_send));

            LinearLayout content = (LinearLayout) findViewById(R.id.ntpContentItems);

            sendData = new TradingPointTemplate();
            View view = ((AppCompatSpinner) content.findViewById(R.id.businessReg)).getSelectedView();
            sendData.setBusinessRegionsCode(view.findViewById(R.id.labelText).getTag().toString());
            sendData.setTitle(((EditText) content.findViewById(R.id.ntpLegalName)).getText().toString());
            sendData.setSignboard(((EditText) content.findViewById(R.id.ntpSignboard)).getText().toString());
            sendData.setInn(((EditText) content.findViewById(R.id.ntpInn)).getText().toString());
            String name = listTempTPType.get(((Spinner)findViewById(R.id.ntpType)).getSelectedItemPosition());
            sendData.setTpType(name);
            sendData.setContactPerson(((EditText) content.findViewById(R.id.ntpContact)).getText().toString());
            sendData.setContactPersonPhone(((EditText) content.findViewById(R.id.ntpContactPhone)).getText().toString());
            sendData.setLegalAddress(((EditText) content.findViewById(R.id.ntpLegalAddress)).getText().toString());
            sendData.setAddress(((EditText) content.findViewById(R.id.ntpAddress)).getText().toString());
            sendData.setDirector(((EditText) content.findViewById(R.id.ntpFio)).getText().toString());
            sendData.setReferencePoint(((EditText) content.findViewById(R.id.ntpReferencePoint)).getText().toString());
            sendData.setRespPersonPhone(((EditText) content.findViewById(R.id.ntpResp)).getText().toString());
            sendData.setMfo(((EditText) content.findViewById(R.id.ntpMFO)).getText().toString());
            sendData.setRc(((EditText) content.findViewById(R.id.ntpRS)).getText().toString());
            sendData.setLatitude(tpLoc.latitude);
            sendData.setLongitude(tpLoc.longitude);
        }

        @Override
        protected String[] doInBackground(Void... params) {
            return ReqNewClient.send(sendData);
        }

        @Override
        protected void onPostExecute(String[] result) {
            dialog.cancel();
            if (result[0].equals("1")) {
                sendData.setTpCode(result[2]);
                AppDB.getInstance(context).saveClient(sendData);

                Toast.makeText(NewTradingPointActivity.this,result[1],Toast.LENGTH_SHORT ).show();

                Intent iResult = new Intent();
                iResult.putExtra(C.KEYS.EXTRA_DATA, sendData);
                setResult(RESULT_OK, iResult);
                finish();
            } else
                DialogBox(context, result[1], getString(R.string.dialog_btn_cancel), getString(R.string.dialog_btn_ok), null, null);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) disposable.dispose();
    }
}

