package com.kashtansystem.project.gloriyamarketing.activity.agent;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.activity.main.BaseActivity;
import com.kashtansystem.project.gloriyamarketing.adapters.BrandsAdapter;
import com.kashtansystem.project.gloriyamarketing.database.AppDB;
import com.kashtansystem.project.gloriyamarketing.models.template.GoodsByBrandTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.MadeOrderTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.ProductTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.SeriesTemplate;
import com.kashtansystem.project.gloriyamarketing.utils.C;
import com.kashtansystem.project.gloriyamarketing.utils.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by FlameKaf on 06.06.2017.
 * ----------------------------------
 * Список всех товаров.
 * Здесь выбираются товары для заказа.
 * В данное активити передаётся ид заказа и код цены.
 * По коду цены сортируется стоимость товаров.
 */

public class ProductListNewActivity extends BaseActivity implements View.OnClickListener
{
    public static MadeOrderTemplate madeOrder;
    public static HashMap<String, ArrayList<GoodsByBrandTemplate>> giftList;
    public static HashMap<String, GoodsByBrandTemplate> gaveGifts = new HashMap<>();

    private TabLayout tabLayout;
    private ViewPager vpBrands;
    private TextView tvTotalPrice;
    private EditText etFilter;

    @Override
    public String getActionBarTitle()
    {
        return getString(R.string.app_title_goods_list);
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
        setContentView(R.layout.products_layout);
        setSupportActionBar((Toolbar)findViewById(R.id.appToolBar));

        tabLayout = (TabLayout)findViewById(R.id.brandTabs);
        vpBrands = (ViewPager)findViewById(R.id.vpBrands);
        vpBrands.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tvTotalPrice = (TextView)findViewById(R.id.tvProductPrice);
        etFilter = (EditText)findViewById(R.id.etFilter);
        etFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ((BrandsAdapter)vpBrands.getAdapter()).filterBy(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        etFilter.setOnTouchListener(new View.OnTouchListener() {
            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_BOTTOM = 3;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    int leftEdgeOfRightDrawable = etFilter.getRight()
                            - etFilter.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width();
                    // when EditBox has padding, adjust leftEdge like
                    // leftEdgeOfRightDrawable -= getResources().getDimension(R.dimen.edittext_padding_left_right);
                    if (event.getRawX() >= leftEdgeOfRightDrawable) {
                        // clicked on clear icon
                        etFilter.setText("");
                        return true;
                    }
                }
                return false;
            }
        });

        madeOrder = MakeOrderNewActivity.orderItems.get(getIntent().getIntExtra(C.KEYS.EXTRA_DATA_ID, 0));
        new LoadProducts(this, getIntent().getStringExtra(C.KEYS.EXTRA_DATA_PT)).execute();

        calcTotalPrice();

        setResult(RESULT_CANCELED);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId())
        {
            case android.R.id.home:
                complete();
            break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onClick(View view)
    {
        if (view.getId() == R.id.tvTabTitle)
        {
            TabLayout.Tab tab1 = (TabLayout.Tab) view.getTag();
            vpBrands.setCurrentItem(tab1.getPosition(), true);
        }
    }

    @Override
    public void onBackPressed()
    {
        complete();
    }

    // Считает сумму заказа и выводит в соответствующее поле
    public void calcTotalPrice()
    {
        if (tvTotalPrice == null)
            return;
        float totalPrice = 0;
        for (GoodsByBrandTemplate product: madeOrder.getGoodsList().values())
            totalPrice += product.getTotal();
        tvTotalPrice.setText(Util.getParsedPrice(totalPrice));
    }

    // Скрывает/отображает вкладки при изменении фильтра
    public void resetTabVisibility(ArrayList<ProductTemplate> filtered_items) {

    }

    /**
     * Расчитывает общую стоимость выбранных товаров
     * */
    private void complete()
    {
        Dialog dialog = BaseActivity.getInformDialog(this, getString(R.string.dialog_text_calc_total_price));
        dialog.show();

        float totalPrice = 0;
        for (GoodsByBrandTemplate product: madeOrder.getGoodsList().values())
            totalPrice += product.getTotal();
        madeOrder.setTotalPrice(totalPrice);

        ((TextView)dialog.findViewById(R.id.dialogSLabel)).setText(R.string.counting_gifts);

        madeOrder.clearGiftList();
        Set<String> keys = gaveGifts.keySet();
        for (String key : keys)
        {
            GoodsByBrandTemplate gift = gaveGifts.get(key);
            //gift.setForProduct(key);
            int count = (gift.getAmountBySeries() / gift.getMinAmount()) * gift.getCount();
            gift.setAmount(count);
            madeOrder.getGiftList().add(gift);
        }

        dialog.cancel();

        giftList.clear();
        gaveGifts.clear();

        setResult(RESULT_OK);
        finish();
    }

    /**
     * Загрузка товаров по коду цены
     * */
    private class LoadProducts extends AsyncTask<Void, Void, ArrayList<ProductTemplate>>
    {
        private Context context;
        private String ptCode = "";

        LoadProducts(Context context, String ptCode)
        {
            this.context = context;
            this.ptCode = ptCode;
        }

        @Override
        protected void onPreExecute()
        {
            findViewById(R.id.appProgressBar).setVisibility(View.INVISIBLE);
        }

        @Override
        protected ArrayList<ProductTemplate> doInBackground(Void... params)
        {
            giftList = AppDB.getInstance(context).getGiftListNew(ptCode);
            ArrayList<ProductTemplate> res = AppDB.getInstance(context).getProducts(ptCode,MakeOrderNewActivity.warehouseCode);

            for (GoodsByBrandTemplate gift: madeOrder.getGiftList())
                gaveGifts.put(gift.getForProduct(), gift);

            //if (madeOrder.getGiftList().isEmpty())
            //{
                Map<String, Integer> forSeries = new HashMap<>();
                for (GoodsByBrandTemplate goods : madeOrder.getGoodsList().values())
                    forSeries.put(goods.getSeries(),
                        (forSeries.containsKey(goods.getSeries()) ? forSeries.get(goods.getSeries()) + goods.getAmount() : goods.getAmount()));

                if (!forSeries.isEmpty())
                {
                    for (ProductTemplate products : res)
                    {
                        for (SeriesTemplate series : products.getSeries())
                        {
                            if (forSeries.containsKey(series.getName()))
                                series.setAmount(forSeries.get(series.getName()));
                        }
                    }
                }
                forSeries.clear();
            //}

            return res;
        }

        @Override
        protected void onPostExecute(ArrayList<ProductTemplate> result)
        {
            for (ProductTemplate productTemplate: result)
            {
                @SuppressLint("InflateParams")
                View view = LayoutInflater.from(context).inflate(R.layout.custome_tab, null);
                TextView textView = view.findViewById(R.id.tvTabTitle);
                textView.setText(productTemplate.getBrand());
                textView.setOnClickListener(ProductListNewActivity.this);

                View delTab = view.findViewById(R.id.ibTabDel);
                delTab.setVisibility(View.GONE);

                TabLayout.Tab newTab = tabLayout.newTab();
                newTab.setTag(productTemplate.getBrand());
                newTab.setCustomView(view);
                tabLayout.addTab(newTab, tabLayout.getTabCount());

                textView.setTag(newTab);
            }

            BrandsAdapter brandsAdapter = new BrandsAdapter(getSupportFragmentManager(), result);
            vpBrands.setAdapter(brandsAdapter);

            findViewById(R.id.appProgressBar).setVisibility(View.INVISIBLE);
        }
    }
}