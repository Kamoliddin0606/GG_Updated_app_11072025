package com.kashtansystem.project.gloriyamarketing.activity.warehousemanager;

import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.activity.main.BaseActivity;

/**
 * Created by FlameKaf on 06.12.2017.
 * ----------------------------------
 * Активити со вкладками зав.склада резделённые на сборки для подтверждения и принятия возврата
 * отменённых заказов
 */
public class WarehouseManagerActivity extends BaseActivity
{
    @Override
    public String getActionBarTitle()
    {
        return "";
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
        setContentView(R.layout.wh_manager_layout);
        createTabs(savedInstanceState);
    }

    private void createTabs(Bundle savedInstanceState)
    {
        //noinspection deprecation
        LocalActivityManager localActivityManager = new LocalActivityManager(this, false);
        localActivityManager.dispatchCreate(savedInstanceState);

        TabHost tabHost = (TabHost)findViewById(android.R.id.tabhost);
        tabHost.setup(localActivityManager);

        TabHost.TabSpec tabSpec;
        tabSpec = tabHost.newTabSpec("package_for_agree");
        tabSpec.setIndicator(getString(R.string.label_packages));
        tabSpec.setContent(new Intent(this, PackagesListForAgreeActivity.class));
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("orders_for_receiving");
        tabSpec.setIndicator(getString(R.string.label_receiving));
        tabSpec.setContent(new Intent(this, CanceledOrdersForReceivingActivity.class));
        tabHost.addTab(tabSpec);
    }
}