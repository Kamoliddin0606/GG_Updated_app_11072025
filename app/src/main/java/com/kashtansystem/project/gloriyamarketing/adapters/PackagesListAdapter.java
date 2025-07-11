package com.kashtansystem.project.gloriyamarketing.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.models.template.PackageDetailTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.PackageTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FlameKaf on 11.05.2017.
 * ----------------------------------
 */

public class PackagesListAdapter extends BaseExpandableListAdapter
{
    private View.OnClickListener listener;
    private LayoutInflater layoutInflater;
    private List<PackageTemplate> items;

    public PackagesListAdapter(Context context, View.OnClickListener listener)
    {
        layoutInflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @Override
    public int getGroupCount()
    {
        return (items != null ? items.size() : 0);
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return (items.get(groupPosition).getDetails() != null ? items.get(groupPosition).getDetails().size() : 0);
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return items.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return items.get(groupPosition).getDetails().get(childPosition);
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

    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        if (convertView == null)
            convertView = layoutInflater.inflate(R.layout.package_group_item, null);

        ((TextView)convertView.findViewById(R.id.tvPkgDeliveryDate)).setText(items.get(groupPosition).getDeliveryDate());
        ((TextView)convertView.findViewById(R.id.tvPkgCustomer)).setText(items.get(groupPosition).getCustomer());
        ((TextView)convertView.findViewById(R.id.tvPkgTotalCapacity)).setText(String.format("%s", items.get(groupPosition).getCapacity()));
        ((TextView)convertView.findViewById(R.id.tvPkgTotalWeight)).setText(String.format("%s", items.get(groupPosition).getWeight()));

        View view = convertView.findViewById(R.id.btnPkgComplete);
        view.setOnClickListener(listener);
        view.setTag(groupPosition);

        return convertView;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        if (convertView == null)
            convertView = layoutInflater.inflate(R.layout.package_child_item, null);

        final PackageDetailTemplate childItem = items.get(groupPosition).getDetails().get(childPosition);

        ((TextView)convertView.findViewById(R.id.tvPkgVendorCode)).setText(childItem.getVendorCode());
        ((TextView)convertView.findViewById(R.id.tvPkgName)).setText(childItem.getName());
        ((TextView)convertView.findViewById(R.id.tvPckMeasure)).setText(childItem.getMeasure());
        ((TextView)convertView.findViewById(R.id.tvPkgCount)).setText(String.format("%s", childItem.getCount()));
        ((TextView)convertView.findViewById(R.id.tvPkgWeight)).setText(String.format("%s", childItem.getWeight()));
        ((TextView)convertView.findViewById(R.id.tvPkgCapacity)).setText(String.format("%s", childItem.getCapacity()));

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return false;
    }

    public void setItems(List<PackageTemplate> in)
    {
        if (items != null)
            items.clear();

        if (in != null)
        {
            items = new ArrayList<>(in.size());
            items.addAll(in);
        }
        notifyDataSetChanged();
    }
}