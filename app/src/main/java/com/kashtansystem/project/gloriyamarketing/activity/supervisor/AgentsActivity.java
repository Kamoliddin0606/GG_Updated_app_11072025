package com.kashtansystem.project.gloriyamarketing.activity.supervisor;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.activity.main.BaseActivity;

/**
 * Created by FlameKaf on 04.07.2017.
 * ----------------------------------
 * Список агентов супервайзера.
 */

public class AgentsActivity extends BaseActivity implements View.OnClickListener
{
    @Override
    public String getActionBarTitle()
    {
        return getString(R.string.app_title_agents);
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
        setContentView(R.layout.agents);
        setSupportActionBar((Toolbar)findViewById(R.id.appToolBar));

        //ExpandableListView elvAgents = (ExpandableListView)findViewById(R.id.elvAgents);
        //elvAgents.setAdapterExpenses(new AgentsElvAdapter(this, /*[add items]*/, this));
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