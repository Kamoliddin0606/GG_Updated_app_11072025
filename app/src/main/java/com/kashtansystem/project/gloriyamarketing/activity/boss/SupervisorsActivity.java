package com.kashtansystem.project.gloriyamarketing.activity.boss;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.activity.main.BaseActivity;
import com.kashtansystem.project.gloriyamarketing.adapters.SupervisorListAdapter;
import com.kashtansystem.project.gloriyamarketing.utils.MyLinearLayoutManager;

/**
 * Created by FlameKaf on 04.07.2017.
 * ----------------------------------
 * Список супервайзеров
 * Не используется
 */

public class SupervisorsActivity extends BaseActivity implements View.OnClickListener
{
    private SupervisorListAdapter adapter;

    @Override
    public String getActionBarTitle()
    {
        return getString(R.string.app_title_supervisors);
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
        setContentView(R.layout.supervisors);
        setSupportActionBar((Toolbar)findViewById(R.id.appToolBar));

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.supervisorsList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new SupervisorListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, MyLinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    @Override
    public void onClick(View view)
    {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        if (menuItem.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(menuItem);
    }
}