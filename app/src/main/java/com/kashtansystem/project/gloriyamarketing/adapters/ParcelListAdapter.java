package com.kashtansystem.project.gloriyamarketing.adapters;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.models.template.ParcelTemplate;

import java.util.LinkedList;

/**
 * Created by FlameKaf on 05.12.2017.
 * ----------------------------------
 */

public class ParcelListAdapter extends RecyclerView.Adapter<ParcelListAdapter.ViewHolder>
{
    private LinkedList<ParcelTemplate> items = new LinkedList<>();
    private View.OnClickListener listener;

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView tvBRName, tvComment, tvCurrencyType, tvSumma, tvRate;

        ViewHolder(View view)
        {
            super(view);
            view.setOnClickListener(listener);
            tvBRName = (TextView) view.findViewById(R.id.tvPclBRegion);
            tvComment = (TextView) view.findViewById(R.id.tvPclComment);
            tvCurrencyType = (TextView) view.findViewById(R.id.tvPclCurrencyType);
            tvSumma = (TextView) view.findViewById(R.id.tvPclSumma);
            tvRate = (TextView) view.findViewById(R.id.tvPclRate);
        }
    }

    public ParcelListAdapter(View.OnClickListener listener)
    {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.parcel_item, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        ParcelTemplate item = items.get(position);
        holder.tvBRName.setText(item.getDealerName());
        holder.tvComment.setText(item.getComment());
        holder.tvCurrencyType.setText((item.getCurrencyType().equals("1") ? "sum" : "usd"));
        holder.tvSumma.setText(item.getSumma());
        holder.tvRate.setText(item.getRate());
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount()
    {
        return items.size();
    }

    public void addAll(LinkedList<ParcelTemplate> in)
    {
        items.addAll(in);
    }

    public void addItem(ParcelTemplate in)
    {
        items.add(in);
        notifyDataSetChanged();
    }

    public void setItem(ParcelTemplate in, int ind)
    {
        items.set(ind, in);
        notifyDataSetChanged();
    }

    public void removeRow(int ind)
    {
        items.remove(ind);
        notifyDataSetChanged();
    }

    public ParcelTemplate getItem(int ind)
    {
        return items.get(ind);
    }
}