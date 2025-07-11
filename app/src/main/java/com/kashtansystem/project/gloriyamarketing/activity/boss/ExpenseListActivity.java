package com.kashtansystem.project.gloriyamarketing.activity.boss;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.activity.main.BaseActivity;
import com.kashtansystem.project.gloriyamarketing.activity.main.LoginActivity;
import com.kashtansystem.project.gloriyamarketing.models.listener.OnDialogBtnsClickListener;
import com.kashtansystem.project.gloriyamarketing.models.template.ExpenseTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.UserTemplate;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqGetExpenseList;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqSetExpenseApproved;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.MyLinearLayoutManager;

import java.util.ArrayList;

/**
 * Список РКО на подтверждение
 */

public class ExpenseListActivity extends BaseActivity implements View.OnClickListener,
    OnDialogBtnsClickListener
{
    private ExpenseListAdapter adapter;

    @Override
    public String getActionBarTitle()
    {
        return getString(R.string.app_title_boss);
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
        setContentView(R.layout.expenses);
        setSupportActionBar((Toolbar)findViewById(R.id.appToolBar));

        RecyclerView recyclerView = findViewById(R.id.expenseList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ExpenseListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, MyLinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        new Init().execute();
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
        getMenuInflater().inflate(R.menu.boss_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId())
        {
            case R.id.mRefresh:
                new Init().execute();
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
        switch (view.getId())
        {
            case R.id.btnApprove:
                new DoSomeAction().execute(adapter.getItem((Integer)view.getTag()).getCode());
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

    /**
     * Запрос списка РКО на подтверждение
     * */
    @SuppressLint("StaticFieldLeak")
    private class Init extends AsyncTask<String, Void, ArrayList<ExpenseTemplate>>
    {
        private Dialog dialog;

        @Override
        protected void onPreExecute()
        {
            dialog = BaseActivity.getInformDialog(ExpenseListActivity.this, getString(R.string.dialog_text_load_data));
            dialog.show();
        }

        @Override
        protected ArrayList<ExpenseTemplate> doInBackground(String... params)
        {
            return ReqGetExpenseList.load();
        }

        @Override
        protected void onPostExecute(ArrayList<ExpenseTemplate> result)
        {
            dialog.cancel();
            if (result != null)
                adapter.setItems(result);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DoSomeAction extends AsyncTask<String, Void, String[]>
    {
        private Dialog dialog;

        @Override
        protected void onPreExecute()
        {
            dialog = BaseActivity.getInformDialog(ExpenseListActivity.this, getString(R.string.dialog_text_sending_data));
            dialog.show();
        }

        @Override
        protected String[] doInBackground(String... params)
        {
            // Отправка подтверждения
            return ReqSetExpenseApproved.send(params[0]);
        }

        @Override
        protected void onPostExecute(String[] result)
        {
            dialog.cancel();
            if (result[0].equals("1"))
                new Init().execute();
            Toast.makeText(ExpenseListActivity.this, result[1], Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Адаптер списка РКО
     * */
    public class ExpenseListAdapter extends RecyclerView.Adapter<ExpenseListAdapter.ViewHolder>
    {
        private ArrayList<ExpenseTemplate> items = new ArrayList<>();
        private View.OnClickListener listener;

        class ViewHolder extends RecyclerView.ViewHolder
        {
            TextView tvInfo;
            Button btnApprove;

            ViewHolder(View view)
            {
                super(view);
                tvInfo = (TextView)view.findViewById(R.id.tvInfo);
                btnApprove = (Button)view.findViewById(R.id.btnApprove);
                btnApprove.setOnClickListener(listener);
            }
        }

        public ExpenseListAdapter(View.OnClickListener listener)
        {
            this.listener = listener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expenses_item, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void onBindViewHolder(ViewHolder holder, int position)
        {
            ExpenseTemplate expenseTemplate = items.get(position);

            holder.tvInfo.setText(expenseTemplate.getInfo());

            holder.btnApprove.setTag(position);
        }

        @Override
        public int getItemCount()
        {
            return items.size();
        }

        public void setItems(ArrayList<ExpenseTemplate> in)
        {
            items = new ArrayList<>(in.size());
            items.addAll(in);
            notifyDataSetChanged();
        }

        public ExpenseTemplate getItem(int position)
        {
            ExpenseTemplate item = items.get(position);
            return item;
        }
    }
}