package com.kashtansystem.project.gloriyamarketing.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.widget.Filter;
import android.widget.Filterable;

import com.kashtansystem.project.gloriyamarketing.adapters.holders.ProductsHolder;
import com.kashtansystem.project.gloriyamarketing.models.template.GoodsByBrandTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.GoodsTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.PriceTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.ProductTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.SeriesTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BrandsAdapter extends FragmentStatePagerAdapter
{
    private ArrayList<ProductTemplate> items;
    private ArrayList<ProductTemplate> filtered_items;
    private String searchedText = "";
    public BrandsAdapter(FragmentManager fm, ArrayList<ProductTemplate> in)
    {
        super(fm);

        items = new ArrayList<>(in.size());
        items.addAll(in);
        filtered_items = items;
    }

    public void filterBy(String filterText) {
        searchedText = filterText;

        if(filterText.length()>1){
            ArrayList<ProductTemplate> result = new ArrayList<>();
            HashMap<String, HashMap<String, ArrayList<GoodsByBrandTemplate>>> hashMap = new HashMap<>();

            for (ProductTemplate productTemplate: items){
                for (SeriesTemplate seriesTemplate: productTemplate.getSeries()){
                    for (GoodsByBrandTemplate goodsByBrandTemplate: seriesTemplate.getGoods()){
                        if(goodsByBrandTemplate.getProductName().toUpperCase().contains(filterText.toUpperCase())){
                            HashMap<String, ArrayList<GoodsByBrandTemplate>> stringArrayListHashMap = hashMap.get(productTemplate.getBrand());
                            if(stringArrayListHashMap!=null){
                                ArrayList<GoodsByBrandTemplate> goodsByBrandTemplates = stringArrayListHashMap.get(goodsByBrandTemplate.getSeries());
                                if (goodsByBrandTemplates != null) {
                                    goodsByBrandTemplates.add(goodsByBrandTemplate);
                                } else {
                                    ArrayList<GoodsByBrandTemplate> goodsByBrandTemplatesNew = new ArrayList<>();
                                    goodsByBrandTemplatesNew.add(goodsByBrandTemplate);
                                    stringArrayListHashMap.put(goodsByBrandTemplate.getSeries(), goodsByBrandTemplatesNew);
                                }
                            }else {
                                HashMap<String, ArrayList<GoodsByBrandTemplate>> stringArrayListHashMapNew = new HashMap<>();
                                ArrayList<GoodsByBrandTemplate> goodsByBrandTemplatesNew = new ArrayList<>();
                                goodsByBrandTemplatesNew.add(goodsByBrandTemplate);
                                stringArrayListHashMapNew.put(goodsByBrandTemplate.getSeries(), goodsByBrandTemplatesNew);
                                hashMap.put(goodsByBrandTemplate.getBrand(),stringArrayListHashMapNew);
                            }
                        }
                    }
                }
            }
            for (Map.Entry<String, HashMap<String, ArrayList<GoodsByBrandTemplate>>> setBrand: hashMap.entrySet()) {
                ProductTemplate productTemplate = new ProductTemplate();
                productTemplate.setBrand(setBrand.getKey());
                productTemplate.setSeries(new ArrayList<SeriesTemplate>());
                for (Map.Entry<String, ArrayList<GoodsByBrandTemplate>> setSeries: setBrand.getValue().entrySet()) {
                    SeriesTemplate seriesTemplate = new SeriesTemplate();
                    seriesTemplate.setName(setSeries.getKey());
                    seriesTemplate.setGoods(setSeries.getValue());
                    productTemplate.getSeries().add(seriesTemplate);
                }
                result.add(productTemplate);
            }
            filtered_items = result;
            notifyDataSetChanged();
        }else {
            filtered_items = items;
            notifyDataSetChanged();
        }
    }

    @Override
    public Fragment getItem(int position)
    {
        final ProductsHolder productsHolder = ProductsHolder.newInstance(position);
        productsHolder.setSeries(filtered_items.get(position).getSeries());
        productsHolder.setSearchedText(searchedText);
        return productsHolder;
    }

    @Override
    public int getCount()
    {
        return filtered_items.size();
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        return filtered_items.get(position).getBrand();
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}