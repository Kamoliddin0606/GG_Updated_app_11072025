package com.kashtansystem.project.gloriyamarketing.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.models.template.GoodsByBrandTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FlameKaf on 01.10.2017.
 * ----------------------------------
 * Адаптер списка предоставляемых подарков
 */

public class GiftListAdapter extends BaseAdapter
{
    private Context ctx;
    private ArrayList<GoodsByBrandTemplate> items;
    public GiftListAdapter(Context context, List<GoodsByBrandTemplate> items)
    {
        this.ctx = context;
        if (items != null)
        {
            this.items = new ArrayList<>(items.size());
            this.items.addAll(items);
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
            view = LayoutInflater.from(ctx).inflate(R.layout.dialog_list_item, null);
        GoodsByBrandTemplate item = items.get(i);
        ((TextView)view.findViewById(R.id.tvGiftName)).setText(item.getProductName());

        if (item.getAmount() > 0)
        {
            /*((TextView) view.findViewById(R.id.tvGiftCount)).setText(view.getContext()
                .getString(R.string.gift_count_info2, item.getCount(), item.getMinAmount()));*/
            ((TextView) view.findViewById(R.id.tvGiftAmount)).setText(view.getContext()
                .getString(R.string.gift_amount, item.getAmount()));
        }
        else
            ((TextView)view.findViewById(R.id.tvGiftCount)).setText(view.getContext()
                .getString(R.string.gift_count_info1, item.getCount()));

        return view;
    }
}