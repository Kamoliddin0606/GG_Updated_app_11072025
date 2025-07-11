package com.kashtansystem.project.gloriyamarketing.activity.forwarder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.activity.main.BaseActivity;
import com.kashtansystem.project.gloriyamarketing.models.template.DeliveryTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.GoodsByBrandTemplate;
import com.kashtansystem.project.gloriyamarketing.utils.C;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by FlameKaf on 15.08.2017.
 * ----------------------------------
 */

public class SelectGoodsToReturn extends BaseActivity
{
    @Override
    public String getActionBarTitle()
    {
        return getString(R.string.app_title_selected_goods);
    }

    @Override
    public boolean getHomeButtonEnable()
    {
        return true;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selected_goods_layout);
        setSupportActionBar((Toolbar)findViewById(R.id.appToolBar));

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH)
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        else
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        ListView lvGoods = (ListView)findViewById(R.id.lvDialogGoodsList);
        GoodsForReturnAdapter adapter = new GoodsForReturnAdapter(this, getIntent().getIntExtra(C.KEYS.EXTRA_DATA_ID, 0));
        lvGoods.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        if (menuItem.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(menuItem);
    }

    private class GoodsForReturnAdapter extends BaseAdapter
    {
        private DeliveryTemplate deliveryTemplate;
        private LayoutInflater layoutInflater;
        private List<GoodsByBrandTemplate> items = null;

        private class ViewHolder
        {
            int pos = 0;
            TextView tvProduct;
            TextView tvAmountInfo;
            EditText etAmount;
        }

        GoodsForReturnAdapter(Context context, int orderId)
        {
            layoutInflater = LayoutInflater.from(context);
            deliveryTemplate = DeliveryListActivity.filteredDeliveryList.get(orderId);
            this.items = new ArrayList<>(deliveryTemplate.getGoodsList().size());
            this.items.addAll(deliveryTemplate.getGoodsList());
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
                convertView = layoutInflater.inflate(R.layout.select_goods_for_return, null);
                holder = new ViewHolder();
                holder.tvProduct = (TextView)convertView.findViewById(R.id.tvProductName);
                holder.tvAmountInfo = (TextView)convertView.findViewById(R.id.tvProductAmountInfo);
                holder.etAmount = (EditText) convertView.findViewById(R.id.etProductAmount);
                convertView.setTag(holder);
            }
            else
                holder = (ViewHolder)convertView.getTag();

            holder.pos = position;

            final GoodsByBrandTemplate item = items.get(position);

            holder.tvProduct.setText(item.getProductName());
            holder.tvAmountInfo.setText(String.format("%s", item.getAmount()));
            /*if (deliveryTemplate.getGoodsForReturn().containsKey(item.getProductCode()))
                holder.etAmount.setText(String.format("%s", deliveryTemplate.getGoodsForReturn().get(item.getProductCode()).getAmountToReturn()));
            else
                holder.etAmount.setText("");*/

            holder.etAmount.addTextChangedListener(new TextWatcher()
            {
                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
                {
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3)
                {
                }

                @Override
                public void afterTextChanged(Editable s)
                {
                    final GoodsByBrandTemplate product = items.get(holder.pos);
                    if (s.length() > 0 && !s.toString().equals("0"))
                    {
                        product.setAmountToReturn(Integer.parseInt(s.toString()));
                        if (!deliveryTemplate.getGoodsForReturn().containsKey(product.getProductCode()))
                            deliveryTemplate.getGoodsForReturn().put(product.getProductCode(), product);
                        else
                        {
                            final GoodsByBrandTemplate chosenProduct = deliveryTemplate.getGoodsForReturn().get(product.getProductCode());
                            chosenProduct.setAmountToReturn(product.getAmountToReturn());
                        }
                    }
                    else
                    {
                        product.setAmountToReturn(0);
                        if (deliveryTemplate.getGoodsForReturn().containsKey(product.getProductCode()))
                            deliveryTemplate.getGoodsForReturn().remove(product.getProductCode());
                    }
                }
            });
            return convertView;
        }
    }
}