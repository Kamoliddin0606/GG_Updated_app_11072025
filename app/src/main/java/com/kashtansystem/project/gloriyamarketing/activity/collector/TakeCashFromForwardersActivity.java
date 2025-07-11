package com.kashtansystem.project.gloriyamarketing.activity.collector;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.activity.main.BaseActivity;
import com.kashtansystem.project.gloriyamarketing.adapters.ForwarderListAdapter;
import com.kashtansystem.project.gloriyamarketing.models.template.ForwarderBodyTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.ForwarderHeaderTemplate;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqGetShippingCash;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqShippingCashList;

import java.util.ArrayList;

/**
 * Created by FlameKaf on 11.10.2017.
 * ----------------------------------
 * Список экспедиторов, которые должны сдать кассиру собранные денежные суммы
 */

public class TakeCashFromForwardersActivity extends BaseActivity implements View.OnClickListener
{
    private ForwarderListAdapter adapter;

    @Override
    public String getActionBarTitle()
    {
        return getString(R.string.app_title_forwarders);
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
        setContentView(R.layout.forwarders_layout);
        setSupportActionBar((Toolbar)findViewById(R.id.appToolBar));

        ExpandableListView forwarders = (ExpandableListView) findViewById(R.id.forwarders);
        adapter = new ForwarderListAdapter(this, new ForwarderListAdapter.Listener() {
            @Override
            public void onForwarderPicked(ForwarderHeaderTemplate forwarderHeaderTemplate) {

            }

            @Override
            public void onIncomePicked(ForwarderBodyTemplate forwarderBodyTemplate) {

            }
        });
        forwarders.setAdapter(adapter);

        new LoadData().execute();
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
//        if (view.getId() == R.id.btnAgree)
//        {
//            ForwarderListAdapter.IdHoder idHoder = (ForwarderListAdapter.IdHoder) view.getTag();
//            new AgreeTakenCash().execute(idHoder.groupId, idHoder.childId);
//        }
    }

    /**
     * Загрузка данных по экспедиторам, которые должны сдать собранные деньги
     * */
    @SuppressLint("StaticFieldLeak")
    private class LoadData extends AsyncTask<Void, Void, ArrayList<ForwarderHeaderTemplate>>
    {
        private Dialog progress;

        @Override
        protected void onPreExecute()
        {
            progress = getInformDialog(TakeCashFromForwardersActivity.this, getString(R.string.dialog_text_load_data));
            progress.show();
        }

        @Override
        protected ArrayList<ForwarderHeaderTemplate> doInBackground(Void... voids)
        {
            return ReqShippingCashList.load();
        }

        @Override
        protected void onPostExecute(ArrayList<ForwarderHeaderTemplate> result)
        {
            progress.cancel();
            if (result != null)
                adapter.setItems(result);
        }
    }

    /**
     * Подтверждение получения денежных средств от экспедитора
     * */
    @SuppressLint("StaticFieldLeak")
    private class AgreeTakenCash extends AsyncTask<Integer, Void, String[]>
    {
        private Dialog progress;

        @Override
        protected void onPreExecute()
        {
            progress = getInformDialog(TakeCashFromForwardersActivity.this, getString(R.string.dialog_text_sending_data));
            progress.show();
        }

        @Override
        protected String[] doInBackground(Integer... params)
        {
            ForwarderHeaderTemplate item = (ForwarderHeaderTemplate) adapter.getGroup(params[0]);
            ForwarderBodyTemplate childItem = item.getDetails().get(params[1]);

            String[] result = ReqGetShippingCash.send(item.getUserCode(), childItem.getTpCode());
            if (result[0].equals("0"))
                childItem.setConfirmed(true);
            return result;
        }

        @Override
        protected void onPostExecute(String[] result)
        {
            progress.cancel();
            if (result[0].equals("0"))
                adapter.notifyDataSetChanged();
            Toast.makeText(TakeCashFromForwardersActivity.this, result[1], Toast.LENGTH_LONG).show();
        }
    }
}