package com.kashtansystem.project.gloriyamarketing.activity.forwarder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.activity.main.BaseActivity;
import com.kashtansystem.project.gloriyamarketing.models.template.GoodsByBrandTemplate;
import com.kashtansystem.project.gloriyamarketing.utils.C;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FlameKaf on 15.08.2017.
 * ----------------------------------
 */

public class DeliveryGoodsListActivity extends BaseActivity
{
    @Override
    public String getActionBarTitle()
    {
        return DeliveryListActivity.filteredDeliveryList.get(getIntent().getIntExtra(C.KEYS.EXTRA_DATA_ID, 0)).getTpName();
    }

    @Override
    public boolean getHomeButtonEnable()
    {
        return true;
    }

    @Override
    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(R.layout.selected_goods_layout);
        setSupportActionBar((Toolbar)findViewById(R.id.appToolBar));

        ListView lvGoods = (ListView)findViewById(R.id.lvDialogGoodsList);
        lvGoods.setAdapter(new DeliveryGoodsListAdapter(this, getIntent().getIntExtra(C.KEYS.EXTRA_DATA_ID, 0)));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        if (menuItem.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(menuItem);
    }

    private class DeliveryGoodsListAdapter extends BaseAdapter
    {
        private LayoutInflater layoutInflater;
        private List<GoodsByBrandTemplate> items = null;

        private class ViewHolder
        {
            TextView tvProduct;
            TextView tvPrice;
            TextView tvAmount;
        }

        DeliveryGoodsListAdapter(Context context, int orderGroupId)
        {
            layoutInflater = LayoutInflater.from(context);
            this.items = new ArrayList<>(DeliveryListActivity.filteredDeliveryList.get(orderGroupId).getGoodsList().size());
            this.items.addAll(DeliveryListActivity.filteredDeliveryList.get(orderGroupId).getGoodsList());
        }

        @Override
        public int getCount()
        {
            return (items == null ? 0 : items.size());
        }

        @Override
        public Object getItem(int position)
        {
            return items.get(position);
        }

        @Override
        public long getItemId(int position)
        {
            return position;
        }

        @Override
        @SuppressLint("InflateParams")
        public View getView(int position, View convertView, ViewGroup parent)
        {
            final ViewHolder holder;
            if (convertView == null)
            {
                convertView = layoutInflater.inflate(R.layout.delivery_goods_list_item, null);

                holder = new ViewHolder();
                holder.tvProduct = (TextView)convertView.findViewById(R.id.tvProductName);
                holder.tvAmount = (TextView)convertView.findViewById(R.id.tvProductAmount);
                convertView.setTag(holder);
            }
            else
                holder = (ViewHolder)convertView.getTag();

            final GoodsByBrandTemplate item = items.get(position);

            holder.tvProduct.setText(item.getProductName());
            holder.tvAmount.setText(String.format("%s", item.getAmount()));

            return convertView;
        }
    }
}