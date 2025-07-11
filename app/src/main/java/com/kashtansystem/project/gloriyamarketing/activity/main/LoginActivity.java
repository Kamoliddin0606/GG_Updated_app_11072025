package com.kashtansystem.project.gloriyamarketing.activity.main;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kashtansystem.project.gloriyamarketing.BuildConfig;
import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.activity.boss.ExpensesActivity;
import com.kashtansystem.project.gloriyamarketing.activity.collector.DebtorListActivity;
import com.kashtansystem.project.gloriyamarketing.activity.forwarder.DeliveryListActivity;
import com.kashtansystem.project.gloriyamarketing.activity.packer.PackagesListActivity;
import com.kashtansystem.project.gloriyamarketing.activity.warehousemanager.WarehouseManagerActivity;
import com.kashtansystem.project.gloriyamarketing.core.OfflineManager;
import com.kashtansystem.project.gloriyamarketing.core.SoapProject;
import com.kashtansystem.project.gloriyamarketing.models.template.UserDetial;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqLogin;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqLoginMultiUser;
import com.kashtansystem.project.gloriyamarketing.service.LocationForegroundService;
import com.kashtansystem.project.gloriyamarketing.service.MainService;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.L;
import com.kashtansystem.project.gloriyamarketing.utils.UpdateChecker;
import com.kashtansystem.project.gloriyamarketing.utils.UserType;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;

import java.util.Date;

/**
 * Created by FlameKaf on 04.05.2017.
 * ----------------------------------
 * Форма авторизации пользователя
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener, TextView.OnEditorActionListener {
    private EditText etLogin, etPassword;
    private Dialog dialog;
    private AppCompatSpinner sProject;

    // @author MrJ
    public final static String mysettings = "mysettings";
    String mloginkey = "login";
    public final static String mpwdkey = "pwd";
    String mprojectkey = "project";
    //

    @Override
    public boolean getHomeButtonEnable() {
        return false;
    }

    @Override
    public String getActionBarTitle() {
        return getString(R.string.app_name);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setSupportActionBar((Toolbar) findViewById(R.id.appToolBar));
        new UpdateChecker().checkUpdate(new UpdateChecker.Listener() {
            @Override
            public void shouldUpdate(int versionCode) {
                startActivity(UpdateAppActivity.Companion.instance(LoginActivity.this, versionCode));
            }
        });
        etLogin = (EditText) findViewById(R.id.etLogin);
        etPassword = (EditText) findViewById(R.id.etPassword);
        etPassword.setOnEditorActionListener(this);


        findViewById(R.id.ivShowHidePas).setOnClickListener(this);
        findViewById(R.id.btnEnter).setOnClickListener(this);

        ((TextView) findViewById(R.id.appVer)).setText(String.format("Build ver. %s", BuildConfig.VERSION_NAME));

        // Выбор проекта
        sProject = (AppCompatSpinner) findViewById(R.id.sProject);
        ArrayAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, SoapProject.Companion.getProjectPickerSpinnerItems());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sProject.setAdapter(adapter);
        // Подстановка логина
        sProject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                AppCache.USER_INFO.setProjectId(position, LoginActivity.this);
                etLogin.setText(AppCache.USER_INFO.getLogin(position, LoginActivity.this));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        // Выбор запомненного проекта
        sProject.setSelection(AppCache.USER_INFO.getProjectId(LoginActivity.this));

        // @author MrJ
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        L.UpdateTimePriceList = sp.getString(L.keyUpdateTimePriceList,"");
        L.oldUpdateTimePriceList = sp.getString(L.keyoldUpdateTimePriceList,"");

        // Получение даты установки в заказах
        // sp.edit().putInt(L.key_date_of_edit_binned,5).apply();
        // int date_of_edit_binned = sp.getInt(L.key_date_of_edit_binned,5);
        // L.date_of_edit_binned = date_of_edit_binned;
        //start service recLocCurier
        //11072025 +
        //start service recLocCurier

        // 1. Location permission tekshirish
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
            Toast.makeText(this, "Lokatsiyadan foydalanish uchun ruxsat bering!", Toast.LENGTH_LONG).show();
            return;
        }

        // 2. Background location permission (Android 10+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);
                Toast.makeText(this, "Lokatsiyadan foydalanish uchun ruxsat bering!", Toast.LENGTH_LONG).show();
                return;
            }
        }

        // 3. GPS yoqilganini tekshirish
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isGpsEnabled) {
            // Foydalanuvchiga GPS’ni yoqishni so‘rab dialog ochish
            new AlertDialog.Builder(this)
                    .setMessage("GPS yoqilmagan. Iltimos, GPS’ni yoqing!")
                    .setPositiveButton("Sozlamalar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("Bekor qilish", null)
                    .show();
            return;
        }

        // 4. Hammasi yaxshi bo‘lsa, servisni ishga tushirish
        Intent serviceIntent = new Intent(this, LocationForegroundService.class);
        ContextCompat.startForegroundService(this, serviceIntent);
        //11072025 -
        Intent intent = getIntent();
        if (intent.getAction() == null) {
            etLogin.setText("");
            etPassword.setText("");
            saveSharedReferences();
            return;
        }
        String mLogin = getSharedPreferences(mysettings, Context.MODE_PRIVATE).getString(mloginkey, "");
        String mpwd = getSharedPreferences(mysettings, Context.MODE_PRIVATE).getString(mpwdkey, "");
        if (mLogin != "" && mpwd != "") {
            etLogin.setText(mLogin);
            etPassword.setText(mpwd);
            attemptLogIn();
        }
        //
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivShowHidePas:
                showHidePassword();
                break;
            case R.id.btnEnter:
                attemptLogIn();
                break;
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        switch (actionId) {
            case EditorInfo.IME_ACTION_DONE:
                attemptLogIn();
                break;
        }
        return false;
    }

    private void showHidePassword() {
        if (etPassword.getTransformationMethod() != null) {
            etPassword.setTransformationMethod(null);
            ((ImageView) findViewById(R.id.ivShowHidePas)).setImageResource(R.drawable.ic_password_visible);
        } else {
            etPassword.setTransformationMethod(new PasswordTransformationMethod());
            ((ImageView) findViewById(R.id.ivShowHidePas)).setImageResource(R.drawable.ic_password_hide);
        }

        if (etPassword.getText().length() > 0)
            etPassword.setSelection(etPassword.getText().length());
    }

    private void attemptLogIn() {
        if (checkField(etLogin))
            return;
        if (checkField(etPassword))
            return;

        if (sProject.getSelectedItemPosition() < SoapProject.values().length) {
            // Проект выбран
            AppCache.USER_INFO.setProjectURL(C.SOAP.URLs[sProject.getSelectedItemPosition()]);
            // Запомним логин
            AppCache.USER_INFO.setLogin(etLogin.getText().toString(), sProject.getSelectedItemPosition(),
                    LoginActivity.this);
            // Запомним, какой проект был выбран
            AppCache.USER_INFO.setProjectId(sProject.getSelectedItemPosition(), LoginActivity.this);
            new Login().execute(etLogin.getText().toString(), etPassword.getText().toString());
        } else {
            /* For all projects in one*/
            new LoginMultiUser().execute(etLogin.getText().toString(), etPassword.getText().toString());
        }
    }

    /**
     * Проверяет, заполненно ли поле
     *
     * @return true, если поле пустое и его необходимо заполнить, в противном случае false
     */
    private boolean checkField(EditText editText) {
        editText.setError(null);
        if (TextUtils.isEmpty(editText.getText())) {
            editText.setError(getString(R.string.e_field_required));
            editText.requestFocus();
            return true;
        }
        return false;
    }

    /**
     * Отправка запроса на авторизацию
     */
    @SuppressLint("StaticFieldLeak")
    private class Login extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            dialog = getInformDialog(LoginActivity.this, getString(R.string.dialog_text_authorization));
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            if (params[0].equals("test1")) {
                AppCache.USER_INFO.setLogined(true);
                AppCache.USER_INFO.setUserType(UserType.Packer);
                return "";
            } else if (params[0].equals("test2")) {
                AppCache.USER_INFO.setLogined(true);
                AppCache.USER_INFO.setUserType(UserType.WarehouseManager);
                return "";
            }

            return ReqLogin.authorization(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(final String result) {
            dialog.cancel();

            if (result.isEmpty()) {
                if (AppCache.USER_INFO.getUserType() != UserType.Agent) {
                    PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit()
                            .remove(C.KEYS.USER_CODE).apply();
                    stopService(new Intent(LoginActivity.this, MainService.class));
                }

                // @author MrJ
                saveSharedReferences();
                //

                switch (AppCache.USER_INFO.getUserType()) {
                    case Boss:
                        /*For new Activity, NEW LOGIC*/
                        UserDetial userDetial = new UserDetial(
                                AppCache.USER_INFO.getUserCode(),
                                AppCache.USER_INFO.getUserName(),
                                AppCache.USER_INFO.getProjectCode(),
                                AppCache.USER_INFO.getUserType(),
                                AppCache.USER_INFO.getWarehouseCode(),
                                SoapProject.Companion.getProjectByIndex(AppCache.USER_INFO.getProjectId(LoginActivity.this))
                        );
                        AppCache.USERS_DETAIL.add(userDetial);
                        startActivity(new Intent(LoginActivity.this, ExpensesActivity.class));
                        break;
                    case Agent:
                        //case Supervisor:
                        Log.println(Log.DEBUG, "dightmerc", "here");
                        startActivity(new Intent(LoginActivity.this, AgentActivity.class));
                        break;
                    case Forwarder: // Экспедитор
                        startActivity(new Intent(LoginActivity.this, DeliveryListActivity.class));
                        break;
                    case Collector: // Кассир
                        /*For new Activity, NEW LOGIC*/
                        UserDetial userDetial1 = new UserDetial(
                                AppCache.USER_INFO.getUserCode(),
                                AppCache.USER_INFO.getUserName(),
                                AppCache.USER_INFO.getProjectCode(),
                                AppCache.USER_INFO.getUserType(),
                                AppCache.USER_INFO.getWarehouseCode(),
                                SoapProject.Companion.getProjectByIndex(AppCache.USER_INFO.getProjectId(LoginActivity.this))
                        );
                        AppCache.USERS_DETAIL.add(userDetial1);

                        startActivity(new Intent(LoginActivity.this, DebtorListActivity.class));
                        break;
                    case Packer:
                        startActivity(new Intent(LoginActivity.this, PackagesListActivity.class));
                        break;
                    case WarehouseManager:
                        startActivity(new Intent(LoginActivity.this, WarehouseManagerActivity.class));
                        break;
                }
                finish();
            } else if (OfflineManager.INSTANCE.getGoOffline()) {
                final Dialog dialog = new Dialog(LoginActivity.this);
                dialog.setTitle("Нет подключении");
                dialog.setContentView(R.layout.dialogbox);
                dialog.setCanceledOnTouchOutside(false);
                Window window = dialog.getWindow();
                if (window != null)
                    window.setLayout(-1, -2);

                ((TextView) dialog.findViewById(R.id.dialogText))
                        .setText(LoginActivity.this.getString(R.string.server_not_available));

                Button button1 = (Button) dialog.findViewById(R.id.dialogBtn1);
                button1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(LoginActivity.this, result, Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });
                button1.setText(LoginActivity.this.getString(R.string.cancell));

                Button button2 = (Button) dialog.findViewById(R.id.dialogBtn2);
                button2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(LoginActivity.this, AgentActivity.class));
                        dialog.dismiss();
                    }
                });
                button2.setText(LoginActivity.this.getString(R.string.enter_as_offline));

                dialog.show();
            } else {
                if (result.contains("Не удалось пройти авторизацию по причине")) {
                    final Dialog dialog = new Dialog(LoginActivity.this);
                    dialog.setTitle("Нет подключении");
                    dialog.setContentView(R.layout.dialogbox);
                    dialog.setCanceledOnTouchOutside(false);
                    Window window = dialog.getWindow();
                    if (window != null)
                        window.setLayout(-1, -2);

                    ((TextView) dialog.findViewById(R.id.dialogText))
                            .setText(LoginActivity.this.getString(R.string.server_not_available_not_user));

                    Button button1 = (Button) dialog.findViewById(R.id.dialogBtn1);
                    button1.setVisibility(View.GONE);
                    button1.setText(LoginActivity.this.getString(R.string.cancell));

                    Button button2 = (Button) dialog.findViewById(R.id.dialogBtn2);
                    button2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(LoginActivity.this, result, Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        }
                    });
                    button2.setText(LoginActivity.this.getString(R.string.dialog_btn_ok));
                    dialog.show();
                } else {
                    Toast.makeText(LoginActivity.this, result, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void saveSharedReferences() {
        SharedPreferences mSettings = getSharedPreferences(mysettings, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(mloginkey, etLogin.getText().toString());
        editor.putString(mpwdkey, etPassword.getText().toString());
        editor.putInt(mprojectkey, sProject.getSelectedItemPosition());
        editor.apply();
    }

    /**
     * MultiUser
     */
    @SuppressLint("StaticFieldLeak")
    private class LoginMultiUser extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            dialog = getInformDialog(LoginActivity.this, getString(R.string.dialog_text_authorization));
            dialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            return ReqLoginMultiUser.authorization(params[0], params[1]);
        }

        @Override
        protected void onPostExecute(String result) {
            dialog.cancel();
            if (result.isEmpty()) {
                if (AppCache.userType != UserType.Agent) {
                    PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit()
                            .remove(C.KEYS.USER_CODE).apply();
                    stopService(new Intent(LoginActivity.this, MainService.class));
                }

                switch (AppCache.userType) {
                    case Boss: // Руководитель
                        /* begin hardcode for support old code part*/
                        AppCache.USER_INFO.setLogined(true);
                        AppCache.USER_INFO.setLogin(etLogin.getText().toString(), sProject.getSelectedItemPosition(), LoginActivity.this);
                        /*end hard code*/
                        startActivity(new Intent(LoginActivity.this, ExpensesActivity.class));
                        finish();
                        break;
                    case Collector:
                        /* begin hardcode for support old code part*/
                        if (!AppCache.USERS_DETAIL.isEmpty()) {
                            AppCache.USER_INFO.setLogined(true);
                            AppCache.USER_INFO.setUserCode(AppCache.USERS_DETAIL.get(0).getCodeUser());
                            AppCache.USER_INFO.setUserName(AppCache.USERS_DETAIL.get(0).getName());
                            AppCache.USER_INFO.setProjectCode(AppCache.USERS_DETAIL.get(0).getCodeProject());
                            AppCache.USER_INFO.setUserType(AppCache.USERS_DETAIL.get(0).getUserType());
                            AppCache.USER_INFO.setWarehouseCode(AppCache.USERS_DETAIL.get(0).getCodeSklada());
                            startActivity(new Intent(LoginActivity.this, DebtorListActivity.class));
                        }
                        startActivity(new Intent(LoginActivity.this, DebtorListActivity.class));
                        break;
                    case Agent:
                    case Forwarder:
                    case Packer:
                    case WarehouseManager:
                        Toast.makeText(LoginActivity.this, getString(R.string.this_type_user_cant_multiuser), Toast.LENGTH_LONG).show();
                }
            } else
                Toast.makeText(LoginActivity.this, result, Toast.LENGTH_LONG).show();
        }
    }
}