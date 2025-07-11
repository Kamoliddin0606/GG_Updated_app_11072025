package com.kashtansystem.project.gloriyamarketing.activity.collector;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.activity.main.BaseActivity;
import com.kashtansystem.project.gloriyamarketing.adapters.BusinessRegAdapter;
import com.kashtansystem.project.gloriyamarketing.adapters.CashTypeSpinnerAdapter;
import com.kashtansystem.project.gloriyamarketing.adapters.ParcelListAdapter;
import com.kashtansystem.project.gloriyamarketing.database.AppDB;
import com.kashtansystem.project.gloriyamarketing.database.tables.ParcelTemp;
import com.kashtansystem.project.gloriyamarketing.models.template.BusinessRegionsTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.PackageTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.ParcelTemplate;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqPackage;
import com.kashtansystem.project.gloriyamarketing.utils.MyLinearLayoutManager;

import java.util.ArrayList;

/**
 * Created by FlameKaf on 11.10.2017.
 * ----------------------------------
 */

public class ParcelsListActivity extends BaseActivity implements View.OnClickListener
{
    private ParcelListAdapter adapter;
    private Dialog dialog;
    private ArrayList<BusinessRegionsTemplate> dealers;

    @Override
    public String getActionBarTitle()
    {
        return getString(R.string.app_title_packages);
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
        setContentView(R.layout.parcel_layout);
        setSupportActionBar((Toolbar)findViewById(R.id.appToolBar));

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.packagesList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ParcelListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, MyLinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        findViewById(R.id.btnNewParcel).setOnClickListener(this);

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
        switch (view.getId())
        {
            case R.id.btnNewParcel:
                dialog = newParcel(null, -1);
            break;
            case R.id.parcelItem:
                dialog = newParcel(adapter.getItem((Integer) view.getTag()), (Integer) view.getTag());
            break;
        }
    }

    private Dialog newParcel(final ParcelTemplate in, final int toInd)
    {
        final Dialog dialog = new Dialog(this);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.new_parcel_layout);

        final AppCompatSpinner bRegions = (AppCompatSpinner) dialog.findViewById(R.id.nPclBr);
        bRegions.setAdapter(new BusinessRegAdapter(this, dealers));
        final AppCompatSpinner currencyTypes = (AppCompatSpinner)dialog.findViewById(R.id.nPclCurrencyType);
        currencyTypes.setAdapter(new CashTypeSpinnerAdapter(this));
        final EditText summa = (EditText) dialog.findViewById(R.id.nPclSumma);
        final EditText rate = (EditText) dialog.findViewById(R.id.nPclRate);
        final EditText comment = (EditText) dialog.findViewById(R.id.nPclComment);

        if (in != null)
        {
            selectSpinnerItem(bRegions, in.getDealerCode());
            selectSpinnerItem(currencyTypes, in.getCurrencyType());
            comment.setText(in.getComment());
            summa.setText(in.getSumma());
            rate.setText(in.getRate());
        }

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
                    if (TextUtils.isEmpty(summa.getText()))
                        Toast.makeText(dialog.getContext(), getString(R.string.e_field_required3), Toast.LENGTH_SHORT).show();
                    else
                    if (currencyTypes.getSelectedView().findViewById(R.id.labelText).getTag().equals("2") && TextUtils.isEmpty(rate.getText()))
                        Toast.makeText(dialog.getContext(), getString(R.string.e_field_required4), Toast.LENGTH_SHORT).show();
                    else
                    {
						// добавить проверку, пуст ли список дилеров.
                        TextView brRegionItem = (TextView) bRegions.getSelectedView().findViewById(R.id.labelText);

                        ParcelTemplate newItem = new ParcelTemplate();
                        newItem.setRowId((in != null ? in.getRowId() : -1));
                        newItem.setDealerCode(brRegionItem.getTag().toString());
                        newItem.setDealerName(brRegionItem.getText().toString());
                        newItem.setComment(comment.getText().toString());
                        newItem.setSumma(summa.getText().toString());
                        newItem.setCurrencyType(currencyTypes.getSelectedView().findViewById(R.id.labelText).getTag().toString());
                        newItem.setRate(rate.getText().toString());

                        if (((CheckBox)dialog.findViewById(R.id.nPclSaveLocale)).isChecked())
                        {
                            dialog.findViewById(R.id.llTransferInfo).setVisibility(View.VISIBLE);
                            ((TextView)dialog.findViewById(R.id.tvTransferInfo)).setText(R.string.dialog_text_saving_data);

                            if (in == null)
                            {
                                newItem.setRowId(AppDB.getInstance(ParcelsListActivity.this).saveTempParcel(newItem));
                                adapter.addItem(newItem);
                            }
                            else
                            {
                                AppDB.getInstance(ParcelsListActivity.this).saveTempParcel(in);
                                adapter.setItem(newItem, toInd);
                            }
                            dialog.cancel();
                        }
                        else
                            new AgreeTakenCash(newItem, toInd).execute();
                    }
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

    private void selectSpinnerItem(AppCompatSpinner spinner, String value)
    {
        SpinnerAdapter adapter = spinner.getAdapter();
        for (int ind = 0; ind < adapter.getCount(); ind++)
        {
            Object object = adapter.getItem(ind);
            if (object instanceof BusinessRegionsTemplate)
            {
                BusinessRegionsTemplate bRegion = (BusinessRegionsTemplate) object;
                if (bRegion.getCode().equals(value))
                {
                    spinner.setSelection(ind);
                    ind = adapter.getCount();
                }
            }
            else
            if (object instanceof String[])
            {
                String[] currencyType = (String[]) object;
                if (currencyType[0].equals(value))
                {
                    spinner.setSelection(ind);
                    ind = adapter.getCount();
                }
            }
        }
    }

    /**
     * Загрузка данных по экспедиторам, которые должны сдать собранные деньги
     * */
    @SuppressLint("StaticFieldLeak")
    private class LoadData extends AsyncTask<Void, Void, ArrayList<PackageTemplate>>
    {
        private Dialog progress;

        @Override
        protected void onPreExecute()
        {
            progress = BaseActivity.getInformDialog(ParcelsListActivity.this, getString(R.string.dialog_text_load_data));
            progress.show();
        }

        @Override
        protected ArrayList<PackageTemplate> doInBackground(Void... voids)
        {
            dealers = AppDB.getInstance(ParcelsListActivity.this).getDealers();
            adapter.addAll(AppDB.getInstance(ParcelsListActivity.this).getTempSavedParcels());
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<PackageTemplate> result)
        {
            progress.cancel();
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * Подтверждение получения денежных средств от экспедитора
     * */
    @SuppressLint("StaticFieldLeak")
    private class AgreeTakenCash extends AsyncTask<Void, Void, String[]>
    {
        /** отсылаемые данные */
        private ParcelTemplate dataToSend;
        /** порядковый номер записи в списке */
        private int indToRem;

        AgreeTakenCash(ParcelTemplate in, int ind)
        {
            dataToSend = in;
            indToRem = ind;
        }

        @Override
        protected void onPreExecute()
        {
            if (dialog != null && dialog.isShowing())
            {
                dialog.findViewById(R.id.llTransferInfo).setVisibility(View.VISIBLE);
                ((TextView)dialog.findViewById(R.id.tvTransferInfo)).setText(R.string.dialog_text_sending_data);
            }
        }

        @Override
        protected String[] doInBackground(Void... params)
        {
            return ReqPackage.send(dataToSend);
        }

        @Override
        protected void onPostExecute(String[] result)
        {
            dialog.cancel();
            if (result[0].equals("0") && dataToSend.getRowId() != -1)
            {
                AppDB.getInstance(ParcelsListActivity.this).deleteTempParcel(dataToSend.getRowId());
                adapter.removeRow(indToRem);
            }
            Toast.makeText(ParcelsListActivity.this, result[1], Toast.LENGTH_LONG).show();
        }
    }
}