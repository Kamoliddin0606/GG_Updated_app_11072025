package com.kashtansystem.project.gloriyamarketing.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.models.template.OrderTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.OrdersHisTemplate;

import java.util.ArrayList;

/**
 * Created by FlameKaf on 02.06.2017.
 * ----------------------------------
 */

public class OrdersHistoryElvAdapter extends BaseExpandableListAdapter
{
    private LayoutInflater layoutInflater;
    private ArrayList<OrdersHisTemplate> items;

    public OrdersHistoryElvAdapter(Context context, ArrayList<OrdersHisTemplate> items)
    {
        layoutInflater = LayoutInflater.from(context);
        this.items = new ArrayList<>(items.size());
        this.items.addAll(items);
    }

    @Override
    public int getGroupCount()
    {
        return this.items.size();
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return this.items.get(groupPosition).getOrdersByTp().size();
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return this.items.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return this.items.get(groupPosition).getOrdersByTp().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        if (convertView == null)
            convertView = layoutInflater.inflate(R.layout.trading_points_elv_group_item, null);

        ((TextView)convertView.findViewById(R.id.elvGroupItem)).setText(items.get(groupPosition).getTradingPoint());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = layoutInflater.inflate(R.layout.orders_item, null);
            convertView.setBackgroundColor(Color.parseColor("#dfdfdf"));
            convertView.findViewById(R.id.ordersItemEdit).setVisibility(View.GONE);
            convertView.findViewById(R.id.ordersItemStatus).setVisibility(View.GONE);
        }

        OrderTemplate orderTemplate = this.items.get(groupPosition).getOrdersByTp().get(childPosition);

        ((TextView)convertView.findViewById(R.id.ordersTitle)).setText(orderTemplate.getTitle());
        ((TextView)convertView.findViewById(R.id.ordersItemPriceType)).setText(orderTemplate.getPriceTypeName());
        ((TextView)convertView.findViewById(R.id.ordersItemComment)).setText(orderTemplate.getComment());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return false;
    }
}
