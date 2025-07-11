package com.kashtansystem.project.gloriyamarketing.activity.collector;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.activity.main.BaseActivity;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqCollectCashierCash;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.Util;

/**
 * Created by FlameKaf on 21.08.2017.
 * ----------------------------------
 * Ввод информации по сбору денежных средств у кредиторов
 */

public class TakeCashActivity extends BaseActivity implements View.OnClickListener
{
    private LinearLayout content;
    private String tpCode;
    private double totalSum = 0;

    @Override
    public String getActionBarTitle()
    {
        return getString(R.string.app_title_input_cash);
    }

    @Override
    public boolean getHomeButtonEnable()
    {
        return true;
    }

    @Override
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.take_cash_layout);
        setSupportActionBar((Toolbar)findViewById(R.id.appToolBar));
        setResult(RESULT_CANCELED);

//        if (!AppCache.USER_INFO.isLogined())
//            return;

        tpCode = getIntent().getStringExtra(C.KEYS.EXTRA_DATA_TP);
        totalSum = Double.parseDouble(getIntent().getStringExtra(C.KEYS.EXTRA_DATA));

        ((TextView)findViewById(R.id.tvCashInfo)).setText(getString(R.string.hint_cash_info, Util.getParsedPrice(totalSum)));
        content = (LinearLayout)findViewById(R.id.llCashListContent);

        findViewById(R.id.btnAddNewCash).setOnClickListener(this);
        findViewById(R.id.btnSendCash).setOnClickListener(this);

        createItem();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        if (menuItem.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.btnAddNewCash:
                createItem();
            break;
            case R.id.cashItemDelete:
                if (content.getChildCount() > 0)
                    dialogAskBeforeDel(R.string.dialog_text_ask_to_delete_cash, Integer.parseInt(view.getTag().toString()));
            break;
            case R.id.btnSendCash:
                attemptSend();
            break;
        }
    }

    /**
     * Запрос подтверждения удаления информации о денежном средстве
     * */
    private void dialogAskBeforeDel(int textId, final int ind)
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialogbox);
        dialog.setCanceledOnTouchOutside(false);

        ((TextView)dialog.findViewById(R.id.dialogText)).setText(textId);

        View.OnClickListener event = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (view.getId() == R.id.dialogBtn2)
                {
                    content.removeViewAt(ind);
                    for (int i = 0; i < content.getChildCount(); i++)
                    {
                        View childView = content.getChildAt(i);
                        childView.findViewById(R.id.cashItemDelete).setTag(i);
                    }
                }
                dialog.cancel();
            }
        };

        Button btnCancel = (Button) dialog.findViewById(R.id.dialogBtn1);
        Button btnOk = (Button) dialog.findViewById(R.id.dialogBtn2);

        btnCancel.setText(R.string.dialog_btn_cancel);
        btnCancel.setOnClickListener(event);
        btnOk.setText(R.string.dialog_btn_ok);
        btnOk.setOnClickListener(event);

        dialog.show();
    }

    /**
     * Создаёт новые поле для ввода денежных средств
     * */
    @SuppressLint("InflateParams")
    private void createItem()
    {
        content.addView(LayoutInflater.from(this).inflate(R.layout.take_cash_item, null));
    }

    private void attemptSend()
    {
        for (int i = 0; i < content.getChildCount(); i++)
        {
            View view = content.getChildAt(i);
            EditText editText = (EditText) view.findViewById(R.id.etCash);

            editText.setError(null);
            if (TextUtils.isEmpty(editText.getText()))
            {
                editText.setError(getString(R.string.e_field_required));
                editText.requestFocus();
                return;
            }
        }
        new SendCash().execute();
    }

    /**
     * Отправка данных по собранным денежным средствам
     * */
    @SuppressLint("StaticFieldLeak")
    private class SendCash extends AsyncTask<Void, Void, String[]>
    {
        private Dialog dialog;

        @Override
        protected void onPreExecute()
        {
            dialog = BaseActivity.getInformDialog(TakeCashActivity.this, getString(R.string.dialog_text_sending_data));
            dialog.show();
        }

        @Override
        protected String[] doInBackground(Void... voids)
        {
            return ReqCollectCashierCash.send(tpCode, content);
        }

        @Override
        protected void onPostExecute(String[] result)
        {
            dialog.cancel();
            Toast.makeText(TakeCashActivity.this, result[1], Toast.LENGTH_LONG).show();
            if (result[0].equals("0"))
            {
                Intent iResult = new Intent();
                double total = totalSum - Double.parseDouble(result[2]);
                if (total == 0)
                    iResult.putExtra(C.KEYS.EXTRA_RES_DATA_BOOL, true);
                iResult.putExtra(C.KEYS.EXTRA_RES_DATA_DOUBLE, total);
                setResult(RESULT_OK, iResult);
                finish();
            }
        }
    }
}