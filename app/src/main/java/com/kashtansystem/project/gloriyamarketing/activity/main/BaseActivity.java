package com.kashtansystem.project.gloriyamarketing.activity.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import com.kashtansystem.project.gloriyamarketing.BuildConfig;
import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.core.OfflineManager;
import com.kashtansystem.project.gloriyamarketing.models.listener.OnDateSelectedListener;
import com.kashtansystem.project.gloriyamarketing.models.listener.OnDialogBtnsClickListener;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.AppPermissions;
import com.kashtansystem.project.gloriyamarketing.utils.GPS;
import com.kashtansystem.project.gloriyamarketing.utils.L;
import com.kashtansystem.project.gloriyamarketing.utils.LoadProductsAlarm;
import com.kashtansystem.project.gloriyamarketing.utils.UserType;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by FlameKaf on 07.02.2017.
 * ----------------------------------
 * Родительский класс, от которого наследуются все остальные activity.
 * Реализует запрос permission-ов для версии андроида Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
 */

public abstract class BaseActivity extends AppCompatActivity
{
    public abstract String getActionBarTitle();
    public abstract boolean getHomeButtonEnable();
    public boolean checkLogin() { return true;}
    private OnDialogBtnsClickListener dialogBtnsClickListener = new OnDialogBtnsClickListener()
    {
        @Override
        public void onBtnClick(View view, Intent intent)
        {
            if (view.getId() == R.id.dialogBtn2)
                startActivity(intent);
        }
    };

    @Override
    public void onCreate(Bundle bundle)
    {

        super.onCreate(bundle);
        if (AppCache.USER_INFO == null && !(this instanceof LoginActivity))
        {
            L.info("AgentActivity changed app's permissions while it was runned, therefore app was stopped! Go to LoginActivity activity");
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
    ActionBar actionBar;
    @Override
    public void onPostCreate(Bundle bundle)
    {
        super.onPostCreate(bundle);
        actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            //final View view = getLayoutInflater().inflate(R.layout.app_action_bar_title, null);
            //((TextView)view.findViewById(R.id.tvAbText)).setText(getActionBarTitle());
            //actionBar.setDisplayShowCustomEnabled(true);
            //actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setTitle(getActionBarTitle());
            actionBar.setDisplayHomeAsUpEnabled(getHomeButtonEnable());
            //actionBar.setCustomView(view);
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(!checkLogin()) return;
        if(AppCache.USER_INFO.isLogined() && actionBar!=null){
            String userNameAndVersion = "";
            if(OfflineManager.INSTANCE.getGoOffline()){
                userNameAndVersion = "OFFLINE  ";
            }
            userNameAndVersion += "v:" + BuildConfig.VERSION_NAME + " ";

            if(AppCache.USERS_DETAIL.size()!=0){
                userNameAndVersion += AppCache.USERS_DETAIL.get(0).getName();
            }else{
                userNameAndVersion += AppCache.USER_INFO.getUserName();
            }
            actionBar.setSubtitle(userNameAndVersion);
        }
        if (!(this instanceof LoginActivity) && !AppCache.USER_INFO.isLogined())
        {
            Intent iReLogin = new Intent(getApplicationContext(), LoginActivity.class);
            iReLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
            DialogBox(this, getString(R.string.dialog_text_need_relogin), getString(R.string.dialog_btn_cancel),
                getString(R.string.dialog_btn_ok), iReLogin, new OnDialogBtnsClickListener()
                {
                    @Override
                    public void onBtnClick(View view, Intent intent)
                    {
                        if (view.getId() == R.id.dialogBtn2)
                            startActivity(intent);
                        //ActivityCompat.finishAffinity(BaseActivity.this);
                    }
                });
        }
        else
            storagePermission();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void checkPermission(String permission, int requestCode)
    {
        if (shouldShowRequestPermissionRationale(permission))
        {
            String permissionDesc = "";
            try
            {
                PermissionInfo permissionInfo = getPackageManager().getPermissionInfo(permission, PackageManager.GET_META_DATA);
                permissionDesc = String.format("<font color='#4d6376'>\"%s\"</font>",
                    permissionInfo.loadLabel(getPackageManager()).toString());
            }
            catch (PackageManager.NameNotFoundException e)
            {
                L.exception(e);
            }

            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            DialogBox(this, getString(R.string.dialog_text_permission, permissionDesc),
                getString(R.string.dialog_btn_cancel), getString(R.string.dialog_btn_setting), intent, dialogBtnsClickListener);
        }
        else
        {
            if (checkSelfPermission(permission) == PackageManager.PERMISSION_DENIED)
                requestPermissions(new String[]{ permission }, requestCode);
            else
                onPermissionGrantResult(AppPermissions.getValueByInt(requestCode), true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull int[] grantResult)
    {
        for (int res: grantResult)
            onPermissionGrantResult(AppPermissions.getValueByInt(requestCode),
                (res == PackageManager.PERMISSION_GRANTED));
    }

    public void storagePermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, AppPermissions.Storage.getValue());
        else
            onPermissionGrantResult(AppPermissions.Storage, true);
    }

    public void geoLocationPermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, AppPermissions.GeoLocation.getValue());
        else
            onPermissionGrantResult(AppPermissions.GeoLocation, true);
    }

    public void cameraPermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            checkPermission(Manifest.permission.CAMERA, AppPermissions.Camera.getValue());
        else
            onPermissionGrantResult(AppPermissions.Camera, true);
    }

    public void callPhonePermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            checkPermission(Manifest.permission.CALL_PHONE, AppPermissions.CallPhone.getValue());
        else
            onPermissionGrantResult(AppPermissions.CallPhone, true);
    }

    public boolean isGPStatusOK()
    {
        if (!new GPS(this).isProviderEnabled())
        {
            DialogBox(this, getString(R.string.dialog_text_location),
                getString(R.string.dialog_btn_cancel), getString(R.string.dialog_btn_setting), new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), dialogBtnsClickListener);
            return false;
        }

        return true;
    }

    public void onPermissionGrantResult(AppPermissions permission, boolean result)
    {
    }

    /**
     * Диалоговое окно, запрашивающее у пользователя какое-либо действие.
     * @param ctx Context приложения
     * @param text сообщение, выводимое пользователю
     * @param btn1 текс кнопки слева
     * @param btn2 текс кнопки справа
     * */
    public static void DialogBox(Context ctx, String text, String btn1, String btn2, final Intent intent,
        final OnDialogBtnsClickListener listener)
    {
        final Dialog dialog = new Dialog(ctx);
        dialog.setTitle(R.string.app_name);
        dialog.setContentView(R.layout.dialogbox);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        if (window != null)
            window.setLayout(-1, -2);

        final View.OnClickListener dialogListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (listener != null)
                    listener.onBtnClick(view, intent);
                dialog.cancel();
            }
        };

        ((TextView)dialog.findViewById(R.id.dialogText))
            .setText(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
                ? Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT) : Html.fromHtml(text));

        Button button1 = (Button)dialog.findViewById(R.id.dialogBtn1);
        button1.setOnClickListener(dialogListener);
        button1.setText(btn1);

        Button button2 = (Button)dialog.findViewById(R.id.dialogBtn2);
        button2.setOnClickListener(dialogListener);
        button2.setText(btn2);

        dialog.show();
    }

    /**
     * Диалоговое окно, информирующее пользователя о происходящем
     * @param ctx Context приложения
     * @param text информативное сообщение
     * @return android.app.Dialog
     * */
    public static Dialog getInformDialog(Context ctx, String text)
    {
        final Dialog dialog = new Dialog(ctx);
        dialog.setTitle(R.string.app_name);
        dialog.setContentView(R.layout.dialogbox_save);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        if (window != null)
            window.setLayout(-1, -2);
        ((TextView)dialog.findViewById(R.id.dialogSLabel)).setText(text);
        return dialog;
    }

    public static Dialog getCancelableInformDialog(Context ctx, String text)
    {
        final Dialog dialog = new Dialog(ctx);
        dialog.setTitle(R.string.app_name);
        dialog.setContentView(R.layout.dialogbox_save);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        if (window != null)
            window.setLayout(-1, -2);
        ((TextView)dialog.findViewById(R.id.dialogSLabel)).setText(text);
        return dialog;
    }

    /**
     * Вызывается диалоговое окно с календарём, для выбора даты.
     * Чтобы получить выбранную дату нужно переопределить метод
     * <b>BaseActivity.onDateSelected(String date)</b>
     */
    public static void showCalendarDialog(Context ctx, final OnDateSelectedListener listener)
    {
        final Dialog dialog = new Dialog(ctx);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.calendar);

        Window window = dialog.getWindow();
        if (window != null)
            window.setLayout(-1, -2);

        ((CalendarView)dialog.findViewById(R.id.dialogCalendar)).setOnDateChangeListener(
            new CalendarView.OnDateChangeListener()
            {
                @Override
                public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth)
                {
                    final String m = ((++month) <= 9 ? "0" + month : "" + month);
                    final String d = (dayOfMonth <= 9 ? "0" + dayOfMonth : "" + dayOfMonth);
                    final String[] values =
                    {
                        String.format("%s.%s.%s", d, m, year),
                        String.format("%s%s%s000000", year, m, d)
                    };
                    calendarView.setTag(values);
                }
            }
        );
        dialog.findViewById(R.id.dialogBtnSelDate).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String[] values = (String[])dialog.findViewById(R.id.dialogCalendar).getTag();
                listener.onDateSelected(values[0], values[1]);
                dialog.cancel();
            }
        });

        final long curTime = System.currentTimeMillis();
        final String[] values =
        {
            new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date(curTime)),
            new SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(new Date(curTime)) + "000000"
        };
        dialog.findViewById(R.id.dialogCalendar).setTag(values);
        dialog.show();
    }
}