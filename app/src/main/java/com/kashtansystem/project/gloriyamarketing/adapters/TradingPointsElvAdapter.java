package com.kashtansystem.project.gloriyamarketing.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.models.template.TradingPointTemplate;

import java.util.LinkedList;

/**
 * Created by FlameKaf on 11.05.2017.
 * ----------------------------------
 * Адаптер списка торговых точек
 */

public class TradingPointsElvAdapter extends BaseExpandableListAdapter
{
    private View.OnClickListener listener;
    private LayoutInflater layoutInflater;
    private LinkedList<TradingPointTemplate> originList = new LinkedList<>();
    private LinkedList<TradingPointTemplate> contentList = new LinkedList<>();

    public TradingPointsElvAdapter(Context context, View.OnClickListener listener)
    {
        layoutInflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    @Override
    public int getGroupCount()
    {
        return contentList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return contentList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        TradingPointTemplate childItem = contentList.get(groupPosition);
        childItem.setRowId(groupPosition);
        return childItem;
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
            convertView = layoutInflater.inflate(R.layout.trading_points_elv_group_item, null);

        final TradingPointTemplate item = contentList.get(groupPosition);

        ((TextView)convertView.findViewById(R.id.tpName)).setText(item.getTitle());
        ((TextView)convertView.findViewById(R.id.tpSignboard)).setText(item.getSignboard());

        View view = convertView.findViewById(R.id.ivInformVisited);
        view.setOnClickListener(listener);
        view.setTag(groupPosition);

        return convertView;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = layoutInflater.inflate(R.layout.trading_points_elv_child_item, null);
            convertView.findViewById(R.id.btnContact).setOnClickListener(listener);
            convertView.findViewById(R.id.btnElvWork).setOnClickListener(listener);
            convertView.findViewById(R.id.btnRefusal).setOnClickListener(listener); // Оформление отказа
            convertView.findViewById(R.id.tvElvContPersonPhone).setOnClickListener(listener);
        }

        final TradingPointTemplate tradingPointTemplate = contentList.get(groupPosition);

        ((TextView)convertView.findViewById(R.id.tvElvInn)).setText(tradingPointTemplate.getInn());

        TextView address = (TextView)convertView.findViewById(R.id.tvElvAddress);
        address.setText(tradingPointTemplate.getAddress());
        address.setTag(groupPosition);
        ((TextView)convertView.findViewById(R.id.tvElvRefPoint)).setText(tradingPointTemplate.getReferencePoint());

        ((TextView)convertView.findViewById(R.id.tvElvContPerson)).setText(tradingPointTemplate.getContactPerson());

        TextView tvPhone = (TextView)convertView.findViewById(R.id.tvElvContPersonPhone);
        if (!TextUtils.isEmpty(tradingPointTemplate.getContactPersonPhone()))
            tvPhone.setText(Html.fromHtml(String.format("<u>%s</u>", tradingPointTemplate.getContactPersonPhone())));
        tvPhone.setTag(groupPosition);

        ((TextView)convertView.findViewById(R.id.tvElvTpType)).setText(tradingPointTemplate.getTpType());

        View work =  convertView.findViewById(R.id.btnElvWork);
        work.setTag(groupPosition);

        // Кнопка отказа
        View vRefusal = convertView.findViewById(R.id.btnRefusal);
        vRefusal.setTag(groupPosition);

        View btnContact = convertView.findViewById(R.id.btnContact);
        btnContact.setTag(groupPosition);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return false;
    }

    public void addItem(TradingPointTemplate in)
    {
        originList.add(0, in);
        contentList.add(0, in);
        notifyDataSetChanged();
    }

    public void setContentList(LinkedList<TradingPointTemplate> in)
    {
        originList.addAll(in);
        if (in.size() > 100)
        {
            for (int i = 0; i < 100; i++)
                contentList.add(in.get(i));
            return;
        }
        contentList.addAll(in);
    }

    public void filterData(String query)
    {
        query = query.toLowerCase();
        contentList.clear();

        if (query.isEmpty())
        {
            if (originList.size() > 100)
            {
                for (int i = 0; i < 100; i++)
                    contentList.add(originList.get(i));
            }
            else
                contentList.addAll(originList);
        }
        else
        {
            for (TradingPointTemplate client: originList)
            {
                if (client.getTitle().toLowerCase().contains(query) || client.getInn().toLowerCase().contains(query))
                    contentList.add(client);
            }
        }
        notifyDataSetChanged();
    }
}