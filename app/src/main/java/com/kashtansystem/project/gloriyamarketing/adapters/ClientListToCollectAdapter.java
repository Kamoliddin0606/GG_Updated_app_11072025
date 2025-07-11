package com.kashtansystem.project.gloriyamarketing.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.models.template.CollectCashTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by FlameKaf on 21.08.2017.
 * ----------------------------------
 */

public class ClientListToCollectAdapter extends BaseExpandableListAdapter
{
    private ArrayList<CollectCashTemplate> items;
    private LayoutInflater layoutInflater;
    private View.OnClickListener onClickListener;
    private String curDate;

    private class Holder
    {
        TextView address;
        TextView refPoint;
        TextView contPerson;
        TextView cash;
        Button btnTakeCash;
    }

    public ClientListToCollectAdapter(Context context, View.OnClickListener listener)
    {
        layoutInflater = LayoutInflater.from(context);
        this.onClickListener = listener;
        this.curDate = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(new Date(System.currentTimeMillis()));
    }

    @Override
    public int getGroupCount()
    {
        return (items == null ? 0 : items.size());
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return items.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return items.get(groupPosition);
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
            convertView = layoutInflater.inflate(R.layout.delivery_list_group_item, null);

        CollectCashTemplate groupItem = items.get(groupPosition);

        TextView tpName = (TextView)convertView.findViewById(R.id.elvDLGroupItem);
        TextView visitDate = (TextView) convertView.findViewById(R.id.elvDLGroupDateItem);
        convertView.findViewById(R.id.ivShowDirection).setVisibility(View.GONE);

        tpName.setText(groupItem.getTpName());

        final Context context = visitDate.getContext();
        visitDate.setTextColor((!curDate.equals(groupItem.getDate()) ?
            context.getResources().getColor(android.R.color.holo_red_dark) : context.getResources().getColor(android.R.color.holo_green_dark)));
        visitDate.setText(String.format("%s %s", context.getString(R.string.label_visit_date), groupItem.getDate()));

        return convertView;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        Holder holder;
        if (convertView == null)
        {
            convertView = layoutInflater.inflate(R.layout.collect_cach_elv_child, null);
            holder = new Holder();
            holder.address = (TextView)convertView.findViewById(R.id.elvCCChildAddress);
            holder.refPoint = (TextView)convertView.findViewById(R.id.elvCCChildRefPoint);
            holder.contPerson = (TextView)convertView.findViewById(R.id.elvCCChildContPerson);
            holder.cash = (TextView)convertView.findViewById(R.id.elvCCChildCash);
            holder.btnTakeCash = (Button)convertView.findViewById(R.id.btnCCTake);
            holder.btnTakeCash.setOnClickListener(onClickListener);
            convertView.setTag(holder);
        }
        else
            holder = (Holder)convertView.getTag();

        CollectCashTemplate item = items.get(groupPosition);

        holder.address.setText(item.getAddress());
        holder.refPoint.setText(item.getRefPoint());
        holder.contPerson.setText(item.getContactPerson());
        holder.cash.setText(String.format(Locale.getDefault(),"%,.2f", Double.parseDouble(item.getCash())));
        holder.contPerson.setTag(groupPosition);
        holder.btnTakeCash.setTag(groupPosition);
        holder.btnTakeCash.setEnabled(!item.isCollected());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return false;
    }

    public void setItems(ArrayList<CollectCashTemplate> in)
    {
        items = new ArrayList<>(in.size());
        items.addAll(in);
        notifyDataSetChanged();
    }
}