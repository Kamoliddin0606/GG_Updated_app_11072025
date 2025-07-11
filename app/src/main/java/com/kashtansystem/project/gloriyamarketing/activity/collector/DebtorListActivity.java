package com.kashtansystem.project.gloriyamarketing.activity.collector;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.activity.main.BaseActivity;
import com.kashtansystem.project.gloriyamarketing.activity.main.LoginActivity;
import com.kashtansystem.project.gloriyamarketing.adapters.CashTypeSpinnerAdapter;
import com.kashtansystem.project.gloriyamarketing.adapters.CashierSpinnerAdapter;
import com.kashtansystem.project.gloriyamarketing.adapters.ClientListToCollectAdapter;
import com.kashtansystem.project.gloriyamarketing.database.AppDB;
import com.kashtansystem.project.gloriyamarketing.models.listener.OnDialogBtnsClickListener;
import com.kashtansystem.project.gloriyamarketing.models.template.BossTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.ExpensesReasonTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.CollectCashTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.UserTemplate;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqDebitorList;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqGetBossList;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqGetExpensesReasonList;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqGetDilers;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqSetCashExpenses;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqСurrencyСonversion;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.Util;

import java.util.ArrayList;

/**
 * Created by FlameKaf on 19.06.2017.
 * ----------------------------------
 * Список кредиторов кассира
 */

public class DebtorListActivity extends BaseActivity implements View.OnClickListener,
    OnDialogBtnsClickListener
{
    private final byte TRANSFER = 0;
    private final byte EXCHANGE = 1;
    private final String ALL = "upd_all_data";

    private ClientListToCollectAdapter adapter;
    private ArrayList<ExpensesReasonTemplate> expensesReason;
    private Dialog dialog;
    private int lastKnownGroupId = -1;
    private ArrayList<BossTemplate> bossList; // Список руководителей

    @Override
    public String getActionBarTitle()
    {
        return getString(R.string.app_title_collector);
    }

    @Override
    public boolean getHomeButtonEnable()
    {
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.delivery_list);
        setSupportActionBar((Toolbar)findViewById(R.id.appToolBar));
        findViewById(R.id.tvFrdTotalTakenSum).setVisibility(View.GONE);
        ( findViewById(R.id.llFilter)).setVisibility(View.GONE);

        adapter = new ClientListToCollectAdapter(this, this);
        ((ExpandableListView)findViewById(R.id.elvDeliveryList)).setAdapter(adapter);

        new Init().execute(ALL);
    }

    @Override
    public void onBackPressed()
    {
        DialogBox(this, getString(R.string.dialog_text_ask_to_exit),
            getString(R.string.dialog_btn_cancel), getString(R.string.dialog_btn_exit), null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.collector_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId()) {
            case R.id.mTakeCashForForwarder:
                startActivity(new Intent(this, TakeCashFromForwarderNewActivity.class));
                break;
            case R.id.mhistory:
                startActivity(new Intent(this, HistoryExpensesCollectorActivity.class));
                break;
            case R.id.mParcel:
                startActivity(new Intent(this, ParcelsListActivity.class));
                break;
            case R.id.mTransferCash:
                dialog = windowTransferCash();
                break;
            case R.id.mOtherIncome:
                startActivity(new Intent(this, OtherIncomeActivity.class));
                break;
            case R.id.mExchangeCash:
                dialog = windowExchange();
                break;
            case R.id.mRefresh:
                new Init().execute(ALL);
                break;
            case R.id.mRefreshDealers:
                new Init().execute("upd_only_dealers");
                break;
            case R.id.mLogout:
                DialogBox(this, getString(R.string.dialog_text_ask_to_exit),
                        getString(R.string.dialog_btn_cancel), getString(R.string.dialog_btn_exit),
                        new Intent(this, LoginActivity.class), this);
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onClick(View view)
    {
        lastKnownGroupId = (Integer)view.getTag();
        switch (view.getId())
        {
            case R.id.btnCCTake:
                final CollectCashTemplate data = (CollectCashTemplate)adapter.getGroup(lastKnownGroupId);
                Intent iTakeCash = new Intent(this, TakeCashActivity.class);
                iTakeCash.setAction(C.ACTIONS.COLLECTOR_TAKE_CASH);
                iTakeCash.putExtra(C.KEYS.EXTRA_DATA_TP, data.getTpCode());
                iTakeCash.putExtra(C.KEYS.EXTRA_DATA, data.getCash());
                startActivityForResult(iTakeCash, C.REQUEST_CODES.TAKE_CASH);
            break;
        }
    }

    @Override
    public void onBtnClick(View view, Intent intent)
    {
        if (view.getId() == R.id.dialogBtn2)
        {
            AppCache.USER_INFO = new UserTemplate();
            if (intent != null)
                startActivity(intent);
            finish();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == C.REQUEST_CODES.TAKE_CASH)
        {
            if (resultCode == RESULT_OK)
            {
                final CollectCashTemplate data = (CollectCashTemplate)adapter.getGroup(lastKnownGroupId);
                data.setCollected(intent.getBooleanExtra(C.KEYS.EXTRA_RES_DATA_BOOL, false));
                data.setCash(intent.getDoubleExtra(C.KEYS.EXTRA_RES_DATA_DOUBLE, 0) + "");
                adapter.notifyDataSetChanged();
            }
        }
    }

    /**
     * Сдача денежных средств кассиру
     * */
    private Dialog windowTransferCash()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_transfer_cash);

        ((AppCompatSpinner)dialog.findViewById(R.id.sTransferCashier)).setAdapter(new CashierSpinnerAdapter(this, expensesReason));
        ((AppCompatSpinner)dialog.findViewById(R.id.sTransferCashType)).setAdapter(new CashTypeSpinnerAdapter(this));
        // Заполним список руководителей
        ((AppCompatSpinner)dialog.findViewById(R.id.sBoss)).setAdapter(new BossSpinnerAdapter(this, bossList));

        Window window = dialog.getWindow();
        if (window != null)
            window.setLayout(-1, -2);

        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (view.getId() == R.id.btnDlgAct2)
                {
                    EditText editText = (EditText) dialog.findViewById(R.id.etTransferCash);
                    if (TextUtils.isEmpty(editText.getText()))
                        Toast.makeText(dialog.getContext(), getString(R.string.e_field_required3), Toast.LENGTH_SHORT).show();
                    else
                        new DoSomeAction(TRANSFER).execute(
                                ((AppCompatSpinner) dialog.findViewById(R.id.sTransferCashier)).getSelectedView().findViewById(R.id.labelText).getTag().toString(),
                                ((AppCompatSpinner) dialog.findViewById(R.id.sTransferCashType)).getSelectedView().findViewById(R.id.labelText).getTag().toString(),
                                ((EditText) dialog.findViewById(R.id.etTransferCash)).getText().toString(),
                                ((EditText) dialog.findViewById(R.id.etComment)).getText().toString(),
                                ((AppCompatSpinner) dialog.findViewById(R.id.sBoss)).getSelectedView().findViewById(R.id.labelText).getTag().toString());
                }
                else
                    dialog.cancel();
            }
        };

        dialog.findViewById(R.id.btnDlgAct1).setOnClickListener(onClickListener);
        dialog.findViewById(R.id.btnDlgAct2).setOnClickListener(onClickListener);

        dialog.show();
        return dialog;
    }

    /**
     * Обмен денежных средств
     * */
    private Dialog windowExchange()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_exchange_cash);

        Window window = dialog.getWindow();
        if (window != null)
            window.setLayout(-1, -2);

        final AppCompatSpinner convType = (AppCompatSpinner) dialog.findViewById(R.id.convType);
        convType.setAdapter(new CashTypeSpinnerAdapter(this));
        convType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                convType.setTag(view.findViewById(R.id.labelText).getTag());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {
            }
        });

        final EditText val = (EditText)dialog.findViewById(R.id.etExcBy);
        final EditText sum = (EditText)dialog.findViewById(R.id.etExcTo);
        final TextView tvTotal = (TextView)dialog.findViewById(R.id.tvExcTotalSum);

        TextWatcher textWatcher = new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
            }

            @Override
            public void afterTextChanged(Editable editable)
            {
                final double valuta = Double.parseDouble(TextUtils.isEmpty(val.getText()) ? "0" : val.getText().toString());
                final double summa = Double.parseDouble(TextUtils.isEmpty(sum.getText()) ? "0" : sum.getText().toString());

                if (valuta == 0 || summa == 0)
                {
                    tvTotal.setText(null);
                    return;
                }

                if (convType.getTag().equals("1"))
                    tvTotal.setText(Util.getParsedPrice(valuta * summa));
                else
                    tvTotal.setText(Util.getParsedPrice(summa / valuta));
            }
        };

        val.addTextChangedListener(textWatcher);
        sum.addTextChangedListener(textWatcher);

        View.OnClickListener onClickListener = new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (view.getId() == R.id.btnDlgAct2)
                {
                    if (TextUtils.isEmpty(val.getText()))
                    {
                        Toast.makeText(dialog.getContext(), getString(R.string.e_field_required2, "Курс"), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else
                    if (TextUtils.isEmpty(sum.getText()))
                    {
                        Toast.makeText(dialog.getContext(), getString(R.string.e_field_required2, "Сумма"), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    new DoSomeAction(EXCHANGE).execute(
                            dialog.findViewById(R.id.convType).getTag().toString(),
                            ((EditText) dialog.findViewById(R.id.etExcBy)).getText().toString(),
                            ((EditText) dialog.findViewById(R.id.etExcTo)).getText().toString()
                    );
                }
                else
                    dialog.cancel();
            }
        };

        dialog.findViewById(R.id.btnDlgAct1).setOnClickListener(onClickListener);
        dialog.findViewById(R.id.btnDlgAct2).setOnClickListener(onClickListener);

        dialog.show();
        return dialog;
    }

    /**
     * Адаптер списка руководителей
     * */
    public class BossSpinnerAdapter extends BaseAdapter
    {
        private ArrayList<BossTemplate> items;
        private Context context;

        public BossSpinnerAdapter(Context context, ArrayList<BossTemplate> in)
        {
            this.context = context;
            if (in != null)
            {
                items = new ArrayList<>(in.size());
                items.addAll(in);
            }
        }

        @Override
        public int getCount()
        {
            return (items == null ? 0 : items.size());
        }

        @Override
        public Object getItem(int i)
        {
            return items.get(i);
        }

        @Override
        public long getItemId(int i)
        {
            return i;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int i, View view, ViewGroup viewGroup)
        {
            if (view == null)
                view = LayoutInflater.from(context).inflate(R.layout.spinner_item, null);

            TextView tvBoss = view.findViewById(R.id.labelText);
            tvBoss.setText(items.get(i).getName());
            tvBoss.setTag(items.get(i).getCode());

            return view;
        }
    }

    /**
     * Запрос данных по сбору денежных средств
     * */
    @SuppressLint("StaticFieldLeak")
    private class Init extends AsyncTask<String, Void, ArrayList<CollectCashTemplate>>
    {
        private Dialog dialog;

        @Override
        protected void onPreExecute()
        {
            dialog = BaseActivity.getInformDialog(DebtorListActivity.this, getString(R.string.dialog_text_load_data));
            dialog.show();
        }

        @Override
        protected ArrayList<CollectCashTemplate> doInBackground(String... params)
        {
            if (params[0].equals(ALL))
            {
                if (AppDB.getInstance(DebtorListActivity.this).isNeedUpdDealers())
                    ReqGetDilers.load(DebtorListActivity.this);
                expensesReason = ReqGetExpensesReasonList.load();
                bossList = ReqGetBossList.load(); // Заполним список руководителей
                return ReqDebitorList.getData();
            }
            else
            {
                ReqGetDilers.load(DebtorListActivity.this);
                return null;
            }
        }

        @Override
        protected void onPostExecute(ArrayList<CollectCashTemplate> result)
        {
            dialog.cancel();
            if (result != null)
                adapter.setItems(result);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DoSomeAction extends AsyncTask<String, Void, String[]>
    {
        private byte action;

        DoSomeAction(Byte action)
        {
            this.action = action;
        }

        @Override
        protected void onPreExecute()
        {
            dialog.findViewById(R.id.llTransferInfo).setVisibility(View.VISIBLE);
            ((TextView)dialog.findViewById(R.id.tvTransferInfo)).setText(R.string.dialog_text_sending_data);
        }

        @Override
        protected String[] doInBackground(String... params)
        {
            switch (action)
            {
                case EXCHANGE:
                    return ReqСurrencyСonversion.send(params[0], params[1], params[2]);
                case TRANSFER:
                    return ReqSetCashExpenses.send(params[0], params[1], params[2], params[3], params[4]);
            }
            return new String[] {"-1" , "Nothing to Send"};
        }

        @Override
        protected void onPostExecute(String[] result)
        {
            dialog.cancel();
            Toast.makeText(DebtorListActivity.this, result[1], Toast.LENGTH_LONG).show();
        }
    }
}