package com.kashtansystem.project.gloriyamarketing.activity.agent;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.activity.main.BaseActivity;
import com.kashtansystem.project.gloriyamarketing.adapters.OrdersListAdapter;
import com.kashtansystem.project.gloriyamarketing.database.AppDB;
import com.kashtansystem.project.gloriyamarketing.models.template.OrderTemplate;
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqOrdersHeaders;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.MyLinearLayoutManager;

import java.util.LinkedList;

/**
 * Created by FlameKaf on 30.05.2017.
 * ----------------------------------
 * Заказы по торговой точке. Отображаются все заказы, кроме закрытых
 */

public class CreatedOrdersActivity extends BaseActivity implements View.OnClickListener,
    SearchView.OnQueryTextListener, SearchView.OnCloseListener
{
    private OrdersListAdapter adapter;
    private int filteredInd = R.id.rbOrdStatusAll;
    private int showedOrds = 0;

    @Override
    public String getActionBarTitle()
    {
        return getString(R.string.app_made_orders);
    }

    @Override
    public boolean getHomeButtonEnable()
    {
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
//        Toast toast = Toast.makeText(this, ,Toast.LENGTH_LONG);
//        toast.show();

        setContentView(R.layout.orders);
        setSupportActionBar((Toolbar)findViewById(R.id.appToolBar));

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.orderList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new OrdersListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, MyLinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        new LoadOrdersHeads().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.made_orders_menu, menu);
        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.mMadeOrdsSearch));
        if (searchView != null)
        {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            searchView.setIconifiedByDefault(false);
            searchView.setOnQueryTextListener(this);
            searchView.setOnCloseListener(this);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        if (menuItem.getItemId() == android.R.id.home)
            finish();
        else
        if (menuItem.getItemId() == R.id.mMadeOrdsFilter)
            showFilterDialog();
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public boolean onClose()
    {
        filteredInd = R.id.rbOrdStatusAll;
        showedOrds = 0;

        adapter.filterData("");
        return false;
    }

    @Override
    public boolean onQueryTextChange(String query)
    {
        filteredInd = R.id.rbOrdStatusAll;
        showedOrds = 0;

        adapter.filterData(query);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query)
    {
        filteredInd = R.id.rbOrdStatusAll;
        showedOrds = 0;

        adapter.filterData(query);
        return false;
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.ordersItemEdit:
                Intent iEditOrder = new Intent(this, MakeOrderNewActivity.class);
                iEditOrder.setAction(C.ACTIONS.ACTION_EDIT_ORDER);
                //iEditOrder.putExtra(C.KEYS.EXTRA_DATA_TP, "");
                iEditOrder.putExtra(C.KEYS.EXTRA_DATA_ORDER, adapter.getItem((Integer)view.getTag()));
                startActivityForResult(iEditOrder, C.REQUEST_CODES.EDIT_ORDER);
            break;
            case R.id.ordersItemStatus:
            break;
        }
    }

    /**
     * Фильтрация данных по статусам
     */
    private void showFilterDialog()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_made_orders_filter_variant);

        RadioGroup radioGroup = (RadioGroup) dialog.findViewById(R.id.rgFilterVariant);
        ((RadioButton) radioGroup.findViewById(filteredInd)).setChecked(true);
        RadioGroup.OnCheckedChangeListener listener = new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i)
            {
                filteredInd = i;
                showedOrds = Integer.parseInt(radioGroup.findViewById(i).getTag().toString());
                adapter.filterDataByStatus(showedOrds);
                dialog.cancel();
            }
        };
        radioGroup.setOnCheckedChangeListener(listener);
        dialog.show();
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadOrdersHeads extends AsyncTask<Void, Void, Void>
    {
        private LinkedList<OrderTemplate> orderList = new LinkedList<>();

        @Override
        protected void onPreExecute()
        {
            findViewById(R.id.appProgressBar).setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            orderList.addAll(AppDB.getInstance(CreatedOrdersActivity.this).getSavedOrders());
            Log.println(Log.DEBUG, "dightmerc", "Orders from db: " + String.valueOf(AppDB.getInstance(CreatedOrdersActivity.this).getSavedOrders().size()));

            LinkedList<OrderTemplate> data = ReqOrdersHeaders.load();
            orderList.addAll(data);
            Log.println(Log.DEBUG, "dightmerc", "Orders from server: " + String.valueOf(data.size()));


            return null;
        }

        @Override
        protected void onPostExecute(Void result)
        {
            adapter.setitems(orderList, false);
            adapter.filterDataByStatus(showedOrds);
            findViewById(R.id.appProgressBar).setVisibility(View.INVISIBLE);
        }
    }
}