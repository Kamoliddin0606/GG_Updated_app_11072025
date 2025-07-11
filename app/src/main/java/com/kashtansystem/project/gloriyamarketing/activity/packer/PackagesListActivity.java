package com.kashtansystem.project.gloriyamarketing.activity.packer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.activity.main.BaseActivity;
import com.kashtansystem.project.gloriyamarketing.activity.main.LoginActivity;
import com.kashtansystem.project.gloriyamarketing.adapters.PackagesListAdapter;
import com.kashtansystem.project.gloriyamarketing.models.listener.OnDialogBtnsClickListener;
import com.kashtansystem.project.gloriyamarketing.models.template.PackageDetailTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.PackageTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.UserTemplate;
import com.kashtansystem.project.gloriyamarketing.utils.AppCache;
import com.kashtansystem.project.gloriyamarketing.utils.L;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FlameKaf on 05.12.2017.
 * ----------------------------------
 */

public class PackagesListActivity extends BaseActivity implements View.OnClickListener,
    OnDialogBtnsClickListener
{
    private PackagesListAdapter adapter;

    @Override
    public String getActionBarTitle()
    {
        return getString(R.string.app_title_packer);
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
        setContentView(R.layout.trading_points);
        setSupportActionBar((Toolbar)findViewById(R.id.appToolBar));

        adapter = new PackagesListAdapter(this, this);
        ((ExpandableListView) findViewById(R.id.elvTPoints)).setAdapter(adapter);

        // test begin
        List<PackageTemplate> items = new ArrayList<>(1);
        PackageTemplate packageTemplate = new PackageTemplate();
        packageTemplate.setDeliveryDate("05.12.2017");
        packageTemplate.setCustomer("OOO MIRO CRONE");
        packageTemplate.setCapacity(7.5);
        packageTemplate.setWeight(12.5);

            List<PackageDetailTemplate> childItems = new ArrayList<>(1);
            PackageDetailTemplate packageDetailTemplate = new PackageDetailTemplate();
            packageDetailTemplate.setVendorCode("T700-4563-801");
            packageDetailTemplate.setName("Test");
            packageDetailTemplate.setMeasure("------");
            packageDetailTemplate.setCount(15);
            packageDetailTemplate.setWeight(0.75);
            packageDetailTemplate.setCapacity(0.1);
            childItems.add(packageDetailTemplate);

        packageTemplate.setDetails(childItems);
        items.add(packageTemplate);

        PackageTemplate packageTemplate2 = new PackageTemplate();
        packageTemplate2.setDeliveryDate("05.12.2017");
        packageTemplate2.setCustomer("OOO MIRA PHONE");
        packageTemplate2.setCapacity(7.5);
        packageTemplate2.setWeight(12.5);

        List<PackageDetailTemplate> childItems2 = new ArrayList<>(2);
        PackageDetailTemplate packageDetailTemplate2 = new PackageDetailTemplate();
        packageDetailTemplate2.setVendorCode("T700-4563-801");
        packageDetailTemplate2.setName("Test");
        packageDetailTemplate2.setMeasure("------");
        packageDetailTemplate2.setCount(15);
        packageDetailTemplate2.setWeight(0.75);
        packageDetailTemplate2.setCapacity(0.1);
        childItems2.add(packageDetailTemplate2);

        PackageDetailTemplate packageDetailTemplate3 = new PackageDetailTemplate();
        packageDetailTemplate3.setVendorCode("T700-4563-801");
        packageDetailTemplate3.setName("Test");
        packageDetailTemplate3.setMeasure("------");
        packageDetailTemplate3.setCount(15);
        packageDetailTemplate3.setWeight(0.75);
        packageDetailTemplate3.setCapacity(0.1);
        childItems2.add(packageDetailTemplate3);

        packageTemplate2.setDetails(childItems2);

        items.add(packageTemplate2);


        adapter.setItems(items);
        // test end
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.forwarder_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId())
        {
            case R.id.mRefresh:
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
        L.info("Group Id: " + view.getTag());
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
    public void onBackPressed()
    {
        DialogBox(this, getString(R.string.dialog_text_ask_to_exit),
            getString(R.string.dialog_btn_cancel), getString(R.string.dialog_btn_exit), null, this);
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadData extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected Void doInBackground(Void... voids)
        {
            return null;
        }
    }
}