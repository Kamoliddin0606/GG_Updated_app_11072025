package com.kashtansystem.project.gloriyamarketing.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.models.template.ExpensesReasonTemplate;

import java.util.ArrayList;



public class CashierSpinnerAdapter extends BaseAdapter
{
    private ArrayList<ExpensesReasonTemplate> items;
    private Context context;

    public CashierSpinnerAdapter(Context context, ArrayList<ExpensesReasonTemplate> in)
    {
        this.context = context;
        if (in != null)
        {
            items = new ArrayList<>(in.size());
            items.addAll(in);
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
            view = LayoutInflater.from(context).inflate(R.layout.spinner_item, null);

        TextView tvCashType = view.findViewById(R.id.labelText);
        tvCashType.setText(items.get(i).getName());
        tvCashType.setTag(items.get(i).getCode());

        return view;
    }
}