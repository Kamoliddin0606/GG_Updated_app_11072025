package com.kashtansystem.project.gloriyamarketing.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.kashtansystem.project.gloriyamarketing.activity.agent.MakeOrderNewActivity;
import com.kashtansystem.project.gloriyamarketing.adapters.holders.MakeOrderItemsHolder;

/**
 * Created by FlameKaf on 17.07.2017.
 * ----------------------------------
 * Адаптер заказов.
 */

public class MakeOrderItemsAdapter extends FragmentStatePagerAdapter
{
    public MakeOrderItemsAdapter(FragmentManager fm)
    {
        super(fm);
    }

    @Override
    public Fragment getItem(int position)
    {
        MakeOrderItemsHolder fragment = MakeOrderItemsHolder.newInstance(position);
        fragment.loadPriceTypes();
        return fragment;
    }

    @Override
    public int getCount()
    {
        return MakeOrderNewActivity.orderItems.size();
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return MakeOrderNewActivity.orderItems.get(position).getOrderTitle();
    }
}