package com.kashtansystem.project.gloriyamarketing.adapters;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.models.template.OrderTemplate;
import com.kashtansystem.project.gloriyamarketing.utils.OrderStatus;
import com.kashtansystem.project.gloriyamarketing.utils.Util;

import java.util.LinkedList;

/**
 * Created by FlameKaf on 30.05.2017.
 * ----------------------------------
 */

public class OrdersListAdapter extends Adapter<OrdersListAdapter.ViewHolder>
{
    private LinkedList<OrderTemplate> originList = new LinkedList<>();
    private LinkedList<OrderTemplate> contentList = new LinkedList<>();
    private View.OnClickListener listener;

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvTpName, tvOrder, tvPriceType, tvComment, tvStatus, tvTotalPrice, tvAttention;
        ImageView ivEdit;

        ViewHolder(View view)
        {
            super(view);
            tvTpName = (TextView)view.findViewById(R.id.ordersTpName);
            tvOrder = (TextView)view.findViewById(R.id.ordersTitle);
            tvPriceType = (TextView)view.findViewById(R.id.ordersItemPriceType);
            tvComment = (TextView)view.findViewById(R.id.ordersItemComment);
            ivEdit = (ImageView)view.findViewById(R.id.ordersItemEdit);
            tvTotalPrice = (TextView)view.findViewById(R.id.ordersItemTotalPrice);
            tvStatus = (TextView)view.findViewById(R.id.ordersItemStatus);
            tvAttention = (TextView)view.findViewById(R.id.ordersItemAttention);

            ivEdit.setOnClickListener(listener);
        }
    }

    public OrdersListAdapter(View.OnClickListener listener)
    {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orders_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        OrderTemplate orderTemplate = contentList.get(position);

        holder.tvTpName.setText(orderTemplate.getTpName());
        holder.tvOrder.setText(orderTemplate.getTitle());
        holder.tvPriceType.setText(orderTemplate.getPriceTypeName());
        holder.tvComment.setText(orderTemplate.getComment());
        holder.tvTotalPrice.setText(Util.getParsedPrice(orderTemplate.getTotalPrice()));
        holder.tvStatus.setText(OrderStatus.getOrderStatusNameByValue(holder.tvStatus.getContext(),
            orderTemplate.getOrderStatus()));

        if (!orderTemplate.getAttention().equals(""))
        {
            holder.tvAttention.setVisibility(View.VISIBLE);
            holder.tvAttention.setText(holder.tvAttention.getContext().getString(R.string.hint_attention,
                orderTemplate.getAttention()));
        }
        else
            holder.tvAttention.setVisibility(View.GONE);

        if (!TextUtils.isEmpty(orderTemplate.getOrderCode()))
            holder.ivEdit.setImageResource(R.mipmap.ic_eye);
        else
            holder.ivEdit.setImageResource(R.drawable.btn_order_edit_selector);

        holder.ivEdit.setTag(position);
    }

    @Override
    public int getItemCount()
    {
        return contentList.size();
    }

    /**
     * @param items список заказов
     * @param addToExists если <b>true</b>, то входящие данные добавляются к существующим.
     */
    public void setitems(LinkedList<OrderTemplate> items, boolean addToExists)
    {
        if (!addToExists)
        {
            if (!this.originList.isEmpty())
                this.originList.clear();
            if (!this.contentList.isEmpty())
                this.contentList.clear();
        }
        this.originList.addAll(items);
        this.contentList.addAll(items);
    }

    /**
     * @param position item index
     * @return item object from stored list
     */
    public OrderTemplate getItem(int position)
    {
        OrderTemplate item = contentList.get(position);
        item.setRowId(position);
        return item;
    }

    /**
     * фильтр данных по наименованию торговой точки
     * @param query выборка фильтрации
     */
    public void filterData(String query)
    {
        query = query.toLowerCase();
        contentList.clear();

        if (query.isEmpty())
            contentList.addAll(originList);
        else
        {
            for (OrderTemplate order: originList)
            {
                if (order.getTpName().toLowerCase().contains(query))
                    contentList.add(order);
            }
        }
        notifyDataSetChanged();
    }

    /**
     * фильтрация данных по статусу
     * @param status статус заказа
     */
    public void filterDataByStatus(int status)
    {
        contentList.clear();
        if (status == 0)
            contentList.addAll(originList);
        else
        {
            for (OrderTemplate order: originList)
            {
                if (order.getOrderStatus().getValue() == status)
                    contentList.add(order);
            }
        }
        notifyDataSetChanged();
    }
}