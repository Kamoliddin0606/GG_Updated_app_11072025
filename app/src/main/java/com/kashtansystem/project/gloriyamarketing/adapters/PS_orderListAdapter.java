package com.kashtansystem.project.gloriyamarketing.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.models.template.MadeOrderTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.OrderTemplate;

import java.util.List;

public class PS_orderListAdapter extends BaseAdapter {

    private LayoutInflater layoutInflater;
    private List<MadeOrderTemplate> orderTemplates;

    public PS_orderListAdapter(LayoutInflater layoutInflater, List<MadeOrderTemplate> orderTemplates) {
        this.layoutInflater = layoutInflater;
        this.orderTemplates = orderTemplates;
    }

    @Override
    public int getCount() {
        return orderTemplates.size();
    }

    @Override
    public Object getItem(int position) {
        return orderTemplates.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View currentView = convertView;
        if (currentView == null) {
            currentView = layoutInflater.inflate(R.layout.ps_orders_item, parent, false);
        }

        MadeOrderTemplate order = orderTemplates.get(position);

        ((TextView) currentView.findViewById(R.id.ordersTpName)).setText(order.getTpName());
        ((TextView) currentView.findViewById(R.id.ordersTitle)).setText(order.getOrderTitle());
        ((TextView) currentView.findViewById(R.id.ordersItemPriceType)).setText(order.getPriceTypeName());
        ((TextView) currentView.findViewById(R.id.ordersItemComment)).setText(order.getComment());
        ((TextView) currentView.findViewById(R.id.ordersItemTotalPrice)).setText(String.valueOf(order.getTotalPrice()));
        int valueofStatus = order.getStatus().getValue();
        ((TextView) currentView.findViewById(R.id.ordersItemStatus)).setText((valueofStatus == 300) ?
                    R.string.order_status_saved_locale : R.string.order_status_wait_to_send);

        return currentView;
    }
}
