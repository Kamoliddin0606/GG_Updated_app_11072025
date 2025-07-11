package com.kashtansystem.project.gloriyamarketing.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kashtansystem.project.gloriyamarketing.R;

/**
 * Created by FlameKaf on 11.10.2017.
 * ----------------------------------
 * Адаптер денежных типов
 */

public class CashTypeSpinnerAdapter extends BaseAdapter
{
    private String[][] items = {{"1", "SUM"}, {"2", "USD"}};
    private Context context;

    public CashTypeSpinnerAdapter(Context context)
    {
        this.context = context;
    }

    @Override
    public int getCount()
    {
        return items.length;
    }

    @Override
    public Object getItem(int i)
    {
        return items[i];
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
        tvCashType.setText(items[i][1]);
        tvCashType.setTag(items[i][0]);

        return view;
    }
}