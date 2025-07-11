package com.kashtansystem.project.gloriyamarketing.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.activity.forwarder.DeliveryListActivity;
import com.kashtansystem.project.gloriyamarketing.models.template.DeliveryTemplate;
import com.kashtansystem.project.gloriyamarketing.utils.Util;

/**
 * Created by FlameKaf on 19.06.2017.
 * ----------------------------------
 * Адаптер списка доставок заказа экспедитора
 */

public class DeliveryElvAdapter extends BaseExpandableListAdapter
{
    private LayoutInflater layoutInflater;
    private View.OnClickListener onClickListener;

    private class Holder
    {
        TextView address;
        TextView refPoint;
        TextView contPerson;
        TextView agent;
        TextView capacity;
        TextView weight;
        TextView cash;
        Button btnGoods, btnOnTheWay, btnEndDelivering, btnTakeCash, btnSendCheque;

        TextView chequeStatus;
    }

    public DeliveryElvAdapter(Context context, View.OnClickListener listener)
    {
        layoutInflater = LayoutInflater.from(context);
        onClickListener = listener;
    }

    @Override
    public int getGroupCount()
    {
        return (DeliveryListActivity.filteredDeliveryList == null ? 0 : DeliveryListActivity.filteredDeliveryList.size());
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return DeliveryListActivity.filteredDeliveryList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return DeliveryListActivity.filteredDeliveryList.get(groupPosition);
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

        TextView tpName = (TextView) convertView.findViewById(R.id.elvDLGroupItem);
        TextView tvDeliveryDate = (TextView) convertView.findViewById(R.id.elvDLGroupDateItem);

        final Context context = tpName.getContext();
        final DeliveryTemplate groupItem = DeliveryListActivity.filteredDeliveryList.get(groupPosition);

        tpName.setText(groupItem.getTpName());
        switch (groupItem.getStatus())
        {
            case 3:
                tpName.setTextColor(context.getResources().getColor(android.R.color.holo_red_dark));
            break;
            case 2:
            case 1:
                tpName.setTextColor(context.getResources().getColor(android.R.color.holo_green_dark));
            break;
            default:
                tpName.setTextColor(context.getResources().getColor(R.color.colorPrimary));
            break;
        }
        tvDeliveryDate.setText(String.format("%s %s", context.getString(R.string.label_delivery_date), groupItem.getDeliveryDate()));

        View view = convertView.findViewById(R.id.ivShowDirection);
        view.setOnClickListener(onClickListener);
        view.setTag(groupPosition);

        return convertView;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        Holder holder;

        if (convertView == null)
        {
            convertView = layoutInflater.inflate(R.layout.delivery_list_child_item, null);

            holder = new Holder();
            holder.address = (TextView)convertView.findViewById(R.id.elvDLChildAddress);
            holder.refPoint = (TextView)convertView.findViewById(R.id.elvDLChildRefPoint);
            holder.contPerson = (TextView)convertView.findViewById(R.id.elvDLChildContPerson);
            holder.agent = (TextView)convertView.findViewById(R.id.elvDLChildAgent);
            holder.capacity = (TextView)convertView.findViewById(R.id.elvDLChildCapacity);
            holder.weight = (TextView)convertView.findViewById(R.id.elvDLChildWeight);
            holder.cash = (TextView)convertView.findViewById(R.id.elvDLChildCash);
            holder.btnGoods = (Button)convertView.findViewById(R.id.btnDLGoods);
            holder.btnTakeCash = (Button)convertView.findViewById(R.id.btnDLTakeCash);
            holder.btnOnTheWay = (Button)convertView.findViewById(R.id.btnDLOnTheWay);
            holder.btnEndDelivering = (Button)convertView.findViewById(R.id.btnDLComplete);
            holder.btnSendCheque = (Button)convertView.findViewById(R.id.btnSendCheque);
            holder.chequeStatus = (TextView)convertView.findViewById(R.id.elvDLChildChequeStatus);

            holder.contPerson.setOnClickListener(onClickListener);
            holder.agent.setOnClickListener(onClickListener);
            holder.btnGoods.setOnClickListener(onClickListener);
            holder.btnTakeCash.setOnClickListener(onClickListener);
            holder.btnOnTheWay.setOnClickListener(onClickListener);
            holder.btnEndDelivering.setOnClickListener(onClickListener);
            holder.btnSendCheque.setOnClickListener(onClickListener);

            convertView.setTag(holder);
        }
        else
            holder = (Holder)convertView.getTag();

        DeliveryTemplate item = DeliveryListActivity.filteredDeliveryList.get(groupPosition);

        holder.address.setText(item.getAddress());
        holder.refPoint.setText(item.getRefPoint());
        holder.chequeStatus.setText(item.getChequeStatus());

        if (TextUtils.isEmpty(item.getContactPersonPhone()))
            holder.contPerson.setText(item.getContactPerson());
        else
            holder.contPerson.setText(Html.fromHtml(String.format("<u>%s</u>", item.getContactPerson())));

        if (TextUtils.isEmpty(item.getAgentPhone()))
            holder.agent.setText(item.getAgent());
        else
            holder.agent.setText(Html.fromHtml(String.format("<u>%s</u>", item.getAgent())));

        holder.capacity.setText(String.format("%s", item.getCapacity()));
        holder.weight.setText(String.format("%s", item.getWeight()));
        holder.cash.setText(Util.getParsedPrice(item.getCashToTake()));

        holder.contPerson.setTag(groupPosition);
        holder.agent.setTag(groupPosition);
        holder.btnGoods.setTag(groupPosition);
        holder.btnTakeCash.setTag(groupPosition);
        holder.btnOnTheWay.setTag(groupPosition);
        holder.btnEndDelivering.setTag(groupPosition);
        holder.btnSendCheque.setTag(groupPosition);

        holder.btnTakeCash.setEnabled(!(!item.isNeedTakeCash() || (item.getCashToTake() == 0)));

        if (item.getStatus() == 1 || item.getStatus() == 3)
        //{
            //holder.btnOnTheWay.setEnabled(false);
            holder.btnEndDelivering.setEnabled(false);
        //}
        else
        //{
            //if (!item.isOnTheWay() && item.isCompleted())
            //    holder.btnOnTheWay.setEnabled(false);
            //else
            //    holder.btnOnTheWay.setEnabled(!item.isOnTheWay());
            holder.btnEndDelivering.setEnabled(true);
        //}

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return false;
    }
}