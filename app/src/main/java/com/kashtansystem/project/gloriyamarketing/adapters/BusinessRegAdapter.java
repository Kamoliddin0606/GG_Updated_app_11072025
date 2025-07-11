package com.kashtansystem.project.gloriyamarketing.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.models.template.BusinessRegionsTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FlameKaf on 18.05.2017.
 * ----------------------------------
 */

public class BusinessRegAdapter extends BaseAdapter
{
    private LayoutInflater layoutInflater;
    private List<BusinessRegionsTemplate> items = null;

    private class Holder
    {
        TextView label;
    }

    public BusinessRegAdapter(Context context, ArrayList<BusinessRegionsTemplate> items)
    {
        layoutInflater = LayoutInflater.from(context);
        this.items = new ArrayList<>(items.size());
        this.items.addAll(items);
    }

    @Override
    public int getCount()
    {
        return items.size();
    }

    @Override
    public Object getItem(int position)
    {
        return items.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Holder holder;
        if (convertView == null)
        {
            holder = new Holder();
            convertView = layoutInflater.inflate(R.layout.spinner_item, null);
            holder.label = (TextView)convertView.findViewById(R.id.labelText);
            convertView.setTag(holder);
        }
        else
            holder = (Holder)convertView.getTag();

        BusinessRegionsTemplate item = items.get(position);

        holder.label.setTag(item.getCode());
        holder.label.setText(item.getName());

        return convertView;
    }
}