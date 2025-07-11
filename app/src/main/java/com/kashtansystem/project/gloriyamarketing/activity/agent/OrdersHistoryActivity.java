package com.kashtansystem.project.gloriyamarketing.activity.agent;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.activity.main.BaseActivity;
import com.kashtansystem.project.gloriyamarketing.adapters.OrdersHistoryElvAdapter;
import com.kashtansystem.project.gloriyamarketing.models.listener.OnDateSelectedListener;
import com.kashtansystem.project.gloriyamarketing.models.template.OrderTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.OrdersHisTemplate;

import java.util.ArrayList;

/**
 * Created by FlameKaf on 02.06.2017.
 * ----------------------------------
 * История заказов (завершенных)
 */

public class OrdersHistoryActivity extends BaseActivity implements AbsListView.OnScrollListener,
    View.OnClickListener, OnDateSelectedListener
{
    private int prevVisibleItem = 0;
    private boolean bottomPanelHidden = false;
    private View viewForDate;

    @Override
    public String getActionBarTitle()
    {
        return getString(R.string.app_title_orders_history);
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
        setContentView(R.layout.orders_history);
        setSupportActionBar((Toolbar)findViewById(R.id.appToolBar));

        ((ExpandableListView)findViewById(R.id.elvOrdersHistory)).setOnScrollListener(this);

        findViewById(R.id.ordersHisReq).setOnClickListener(this);
        findViewById(R.id.ordersHisFromDate).setOnClickListener(this);
        findViewById(R.id.ordersHisToDate).setOnClickListener(this);

        new Init().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        if (menuItem.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.ordersHisFromDate:
                viewForDate = findViewById(R.id.ordersHisFrom);
                showCalendarDialog(this, this);
            break;
            case R.id.ordersHisToDate:
                viewForDate = findViewById(R.id.ordersHisTo);
                showCalendarDialog(this, this);
            break;
        }
    }

    @Override
    public void onDateSelected(String view, String value)
    {
        ((TextView)viewForDate).setText(value);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState)
    {
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        if (view.getId() == findViewById(R.id.elvOrdersHistory).getId())
        {
            final int currentFirstVisibleItem = view.getFirstVisiblePosition();
            if (currentFirstVisibleItem > prevVisibleItem)
            {
                if (!bottomPanelHidden)
                {
                    orderHistoryCtrlPanel(false);
                    bottomPanelHidden = true;
                }
            }
            else
            if (currentFirstVisibleItem < prevVisibleItem)
            {
                if (bottomPanelHidden)
                {
                    orderHistoryCtrlPanel(true);
                    bottomPanelHidden = false;
                }
            }
            prevVisibleItem = currentFirstVisibleItem;
        }
    }

    /**
     * Скрывает/Отображает нижнюю панель для ввода дат, по которым будет формироваться запрос
     * на получение истории заказа
     * @param show если <b>true</b> отображает нижнюю панель
     * */
    private void orderHistoryCtrlPanel(boolean show)
    {
        final LinearLayout orderHisCtrlPanel = (LinearLayout) findViewById(R.id.ordersHisCtrlPanel);

        if (!show)
        {
            Animation animation = AnimationUtils.loadAnimation(this, R.anim.order_his_ctrl_panel_hide);
            animation.setAnimationListener(new Animation.AnimationListener()
            {
                @Override
                public void onAnimationStart(Animation animation)
                {
                }

                @Override
                public void onAnimationEnd(Animation animation)
                {
                    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) orderHisCtrlPanel.getLayoutParams();
                    params.bottomMargin -= orderHisCtrlPanel.getHeight() * 0.9f;
                    orderHisCtrlPanel.setLayoutParams(params);
                }

                @Override
                public void onAnimationRepeat(Animation animation)
                {
                }
            });
            orderHisCtrlPanel.startAnimation(animation);
        }
        else
        {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) orderHisCtrlPanel.getLayoutParams();
            params.bottomMargin += orderHisCtrlPanel.getHeight() * 0.9f;
            orderHisCtrlPanel.setLayoutParams(params);
            orderHisCtrlPanel.startAnimation(AnimationUtils.loadAnimation(this, R.anim.order_his_ctrl_panel_show));
        }
    }

    private class Init extends AsyncTask<Void, Void, ArrayList<OrdersHisTemplate>>
    {
        private String[] tp = {"Life Market", "Korzinka Uz", "Makro Market"};
        private String[][][] orders =
        {
            {
                {"GoodsTemplate 2; Type: 2", "12", "Наличные",     ""},
                {"GoodsTemplate 1; Type: 1", "5",  "Перечисление", ""},
                {"GoodsTemplate 3; Type: 3", "7",  "Наличные",     ""},
                {"GoodsTemplate 1; Type: 8", "25", "Наличные",     "I'm old for this shit"},
                {"GoodsTemplate 6; Type: 2", "1",  "Перечисление", ""},
                {"GoodsTemplate 9; Type: 9", "30", "Наличные",     ""},
                {"GoodsTemplate 4; Type: 4", "15", "Наличные",     ""},
                {"GoodsTemplate 0; Type: 1", "75", "Бонус",        "Complete"}
            },
            {
                {"GoodsTemplate 2; Type: 2", "1",  "Бонус",        ""},
                {"GoodsTemplate 2; Type: 1", "55", "Перечисление", ""},
                {"GoodsTemplate 1; Type: 3", "17", "Перечисление", ""},
                {"GoodsTemplate 1; Type: 8", "5",  "Наличные",     ""},
                {"GoodsTemplate 0; Type: 1", "10", "Перечисление", ""},
                {"GoodsTemplate 7; Type: 8", "2",  "Перечисление", ""},
                {"GoodsTemplate 4; Type: 8", "15", "Перечисление", ""},
                {"GoodsTemplate 8; Type: 2", "12", "Наличные",     ""}
            },
            {
                {"GoodsTemplate 3; Type: 5", "10", "Перечисление", "At last"},
                {"GoodsTemplate 8; Type: 2", "5",  "Перечисление", ""},
                {"GoodsTemplate 4; Type: 4", "7",  "Перечисление", ""},
                {"GoodsTemplate 2; Type: 5", "59", "Перечисление", "Ok'ey"},
                {"GoodsTemplate 1; Type: 1", "2",  "Перечисление", ""},
                {"GoodsTemplate 5; Type: 7", "10", "Перечисление", ""},
                {"GoodsTemplate 4; Type: 6", "78", "Перечисление", ""},
                {"GoodsTemplate 0; Type: 0", "5",  "Перечисление", ""}
            }
        };

        @Override
        protected ArrayList<OrdersHisTemplate> doInBackground(Void... params)
        {
            ArrayList<OrdersHisTemplate> result = new ArrayList<>(tp.length);

            for (int id = 0; id < tp.length; id++)
            {
                OrdersHisTemplate ordersHis = new OrdersHisTemplate();
                ordersHis.setTradingPoint(tp[id]);

                ArrayList<OrderTemplate> ordersByTp = new ArrayList<>(orders[id].length);
                for (String[] order : orders[id])
                {
                    OrderTemplate madeOrderTemplate = new OrderTemplate();
                    madeOrderTemplate.setTitle(order[0]);
                    madeOrderTemplate.setPriceTypeName(order[2]);
                    madeOrderTemplate.setComment(order[3]);
                    ordersByTp.add(madeOrderTemplate);
                }

                ordersHis.setOrdersByTp(ordersByTp);
                result.add(ordersHis);
            }

            return result;
        }

        @Override
        protected void onPostExecute(ArrayList<OrdersHisTemplate> result)
        {
            ((ExpandableListView)findViewById(R.id.elvOrdersHistory)).setAdapter(
                new OrdersHistoryElvAdapter(OrdersHistoryActivity.this, result));
        }
    }
}