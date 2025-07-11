package com.kashtansystem.project.gloriyamarketing.adapters.holders;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.activity.agent.ProductListNewActivity;
import com.kashtansystem.project.gloriyamarketing.adapters.GiftListAdapter;
import com.kashtansystem.project.gloriyamarketing.models.template.GoodsByBrandTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.ProductTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.SeriesTemplate;
import com.kashtansystem.project.gloriyamarketing.utils.L;
import com.kashtansystem.project.gloriyamarketing.utils.StyleTextUtils;

import java.util.ArrayList;

/**
 * Created by FlameKaf on 17.07.2017.
 * ----------------------------------
 * The fragment argument representing the section number for this
 * fragment.
 * Фрагмент заказа.
 */

public class ProductsHolder extends Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private ArrayList<SeriesTemplate> series;
    ProductListAdapter adapter;

    /**
     * @param sectionNumber ид страницы
     * @return a new instance of this fragment for the given section
     * number.
     */
    public static ProductsHolder newInstance(int sectionNumber) {
        L.info(String.format("newInstance id: %s", sectionNumber));
        ProductsHolder fragment = new ProductsHolder();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);

        return fragment;
    }

    public void setSeries(ArrayList<SeriesTemplate> in) {
        series = in;
    }

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.product_list_layout, null, false);

        ExpandableListView expListProduct = (ExpandableListView) view.findViewById(R.id.expListProducts);
        adapter = new ProductListAdapter(getContext());
        expListProduct.setAdapter(adapter);

        return view;
    }

    /**
     * Диалоговое окно с выбором подарка
     * по акции, если она имеет место быть
     */
    private void ChooseGiftList(String series, final String forProduct, final int amount) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setTitle(R.string.gift_list);
        dialog.setContentView(R.layout.dialog_goods_list);
        dialog.setCanceledOnTouchOutside(false);
        Window window = dialog.getWindow();
        if (window != null)
            window.setLayout(-1, -2);

        final ArrayList<GoodsByBrandTemplate> gifts = ProductListNewActivity.giftList.get(series);

        ListView giftList = (ListView) dialog.findViewById(R.id.lvDialogGoodsList);
        giftList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long id) {
                GoodsByBrandTemplate goods = gifts.get(pos);
                /* временно записываю в setAmount, чтобы в дальнешем расчитать реальное количество
                   выдаваемых подарков*/
                goods.setAmountBySeries(amount);
                goods.setForProduct(forProduct);
                ProductListNewActivity.gaveGifts.put(forProduct, goods);
                dialog.cancel();
            }
        });
        giftList.setAdapter(new GiftListAdapter(getContext(), gifts));
        dialog.show();
    }

    String searchedText = "";

    public void setSearchedText(String searchedText) {
        this.searchedText = searchedText;
    }

    /**
     * Адаптер списка товаров по сериям
     */
    private class ProductListAdapter extends BaseExpandableListAdapter {
        private Context mContext;

        public ProductListAdapter(Context context) {
            this.mContext = context;
        }

        //private SparseArrayCompat<View> groupIvGiftsHolder = new SparseArrayCompat<>();
        private class GHolder {
            int id = 0;
            TextView name;
            TextView info;
            TextView stock;
            ImageView attention;
            ImageView gift;
        }

        private class Holder {
            int groupId = 0;
            int childId = 0;
            TextView tvProduct;
            TextView tvAvailable;
            TextView tvPrice;
            TextView tvProductNotif;
            TextView tvGiftInfo;
            ImageView ivGift;
            EditText etAmount;
            TextView tvLabelCount;
            // Количество подарков по акции
            EditText etNewGiftAmount;
        }

        @Override
        public int getGroupCount() {
            return series.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return series.get(groupPosition).getGoods().size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return series.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return series.get(groupPosition).getGoods().get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup viewGroup) {
            final GHolder gHolder;
            if (view == null) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.product_elv_group_item, null);
                gHolder = new GHolder();
                gHolder.name = (TextView) view.findViewById(R.id.elvGroupItem);
                gHolder.attention = (ImageView) view.findViewById(R.id.elvGroupItemAttention);
                gHolder.info = (TextView) view.findViewById(R.id.tvSeriesInfo);
                gHolder.gift = (ImageView) view.findViewById(R.id.ivSeriesGift);
                /*gHolder.gift.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        SeriesTemplate seriya1 = series.get(gHolder.id);
                        ChooseGiftList(seriya1.getName(), seriya1.getAmount());
                    }
                });*/
                view.setTag(gHolder);
                //groupIvGiftsHolder.put(i, gHolder.gift);
            } else
                gHolder = (GHolder) view.getTag();

            gHolder.id = groupPosition;
            SeriesTemplate seriya = series.get(groupPosition);

            //if (ProductListNewActivity.gaveGifts.containsKey(seriya.getName()))
            //    seriya.setAmount(ProductListNewActivity.gaveGifts.get(seriya.getName()).getAmountBySeries());

            gHolder.name.setText(seriya.getName());
            if (series.get(groupPosition).getMinAmount() != 0) {
                gHolder.info.setVisibility(View.VISIBLE);
                gHolder.info.setText(getString(R.string.gift_info, seriya.getMinAmount()));
                //gHolder.gift.setVisibility((seriya.getAmount() >= seriya.getMinAmount() ? View.VISIBLE : View.GONE));
            } else {
                gHolder.info.setVisibility(View.GONE);
                gHolder.gift.setVisibility(View.GONE);
            }

            gHolder.attention.setVisibility((ProductListNewActivity.madeOrder.getNotEnoughByBrand()
                    .contains(seriya.getName())) ? View.VISIBLE : View.GONE);
            return view;
        }


        @SuppressLint("InflateParams")
        @Override
        public View getChildView(int i, int i1, boolean b, View convertView, ViewGroup viewGroup) {
            final Holder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.product_item, null);
                holder = new Holder();
                holder.tvProduct = (TextView) convertView.findViewById(R.id.tvProductName);
                holder.tvAvailable = (TextView) convertView.findViewById(R.id.tvProductAvailable);
                holder.tvPrice = (TextView) convertView.findViewById(R.id.tvProductPrice);
                holder.tvProductNotif = (TextView) convertView.findViewById(R.id.tvProductNotification);
                holder.tvGiftInfo = (TextView) convertView.findViewById(R.id.tvGiftInfo);
                holder.ivGift = (ImageView) convertView.findViewById(R.id.ivGift);
                holder.tvLabelCount = (TextView) convertView.findViewById(R.id.tvLabelCount);
                holder.ivGift.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SeriesTemplate seriya = series.get(holder.groupId);
                        GoodsByBrandTemplate goods = seriya.getGoods().get(holder.childId);

                        if (seriya.getMinAmount() == 0 && goods.getMinAmount() != 0) {
                            if (goods.getMinAmount() <= goods.getAmount())
                                ChooseGiftList(goods.getSeries(), goods.getProductCode(), goods.getAmount());
                        } else {
                            if (seriya.getMinAmount() <= seriya.getAmount())
                                ChooseGiftList(seriya.getName(), seriya.getName(), seriya.getAmount());
                        }
                    }
                });
                holder.etAmount = (EditText) convertView.findViewById(R.id.etProductAmount);
                holder.etNewGiftAmount = (EditText) convertView.findViewById(R.id.etNewGiftAmount);

                holder.etAmount.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        L.info(s.toString());
                        final SeriesTemplate seriya = series.get(holder.groupId);
                        final GoodsByBrandTemplate product = seriya.getGoods().get(holder.childId);
                        if (s.length() > 0 && !s.toString().equals("0")) {
                            int count;
                            try {
                                count = Integer.parseInt(s.toString());
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                count = product.getAvailable();
                            }

                            if (count > product.getAvailable()) {
                                holder.etAmount.setText(String.format("%s", product.getAvailable()));
                                holder.etAmount.setSelection(holder.etAmount.getText().length());
                                return;
                            }


                            /*if (product.getAmount() < count)
                                seriya.setAmount((seriya.getAmount() - product.getAmount()) + count);
                            else
                            if (product.getAmount() > count)
                                seriya.setAmount((seriya.getAmount() - product.getAmount()) + count);*/
                            seriya.setAmount((seriya.getAmount() - product.getAmount()) + count);

                            product.setAmount(count);
                            product.setTotal(product.getAmount() * product.getPrice());
                            //product.setAmountBySeries(seriya.getAmount());

                            if (!ProductListNewActivity.madeOrder.getGoodsList().containsKey(product.getProductCode()))
                                ProductListNewActivity.madeOrder.getGoodsList().put(product.getProductCode(), product);
                            else {
                                final GoodsByBrandTemplate chosenProduct = ProductListNewActivity.madeOrder.getGoodsList().get(product.getProductCode());
                                chosenProduct.setAmount(product.getAmount());
                                chosenProduct.setTotal(product.getTotal());
                            }

                            if (seriya.getMinAmount() == 0 && product.getMinAmount() != 0) {
                                if (product.getAmount() >= product.getMinAmount()) {
                                    holder.ivGift.setVisibility(View.VISIBLE);
                                    if (ProductListNewActivity.gaveGifts.containsKey(product.getProductCode()))
                                        ProductListNewActivity.gaveGifts.get(product.getProductCode()).setAmountBySeries(product.getAmount());
                                } else {
                                    holder.ivGift.setVisibility(View.GONE);
                                    if (ProductListNewActivity.gaveGifts.containsKey(product.getProductCode()))
                                        ProductListNewActivity.gaveGifts.remove(product.getProductCode());
                                }
                            } else {
                                if (seriya.getMinAmount() != 0) {
                                    if (seriya.getAmount() >= seriya.getMinAmount()) {
                                        holder.ivGift.setVisibility(View.VISIBLE);
                                        //groupIvGiftsHolder.get(holder.groupId).setVisibility(View.VISIBLE);
                                        if (ProductListNewActivity.gaveGifts.containsKey(seriya.getName()))
                                            ProductListNewActivity.gaveGifts.get(seriya.getName()).setAmountBySeries(seriya.getAmount());
                                    } else {
                                        holder.ivGift.setVisibility(View.GONE);
                                        //groupIvGiftsHolder.get(holder.groupId).setVisibility(View.GONE);
                                        if (ProductListNewActivity.gaveGifts.containsKey(seriya.getName()))
                                            ProductListNewActivity.gaveGifts.remove(seriya.getName());
                                    }
                                }
                            }
                        } else {
                            seriya.setAmount(seriya.getAmount() - product.getAmount());
                            //product.setAmountBySeries(seriya.getAmount());
                            product.setAmount(0);
                            product.setTotal(0);

                            // Если количество товара и подарков 0, то удалим
                            if (ProductListNewActivity.madeOrder.getGoodsList().containsKey(product.getProductCode())
                                    && ProductListNewActivity.madeOrder.getGoodsList().get(product.getProductCode()).getGiftAmount() == 0)
                                ProductListNewActivity.madeOrder.getGoodsList().remove(product.getProductCode());

                            // * на тот случай, если содержимое поля было выделено и удалено
                            if (seriya.getMinAmount() == 0 && product.getMinAmount() != 0) {
                                if (product.getAmount() >= product.getMinAmount())
                                    holder.ivGift.setVisibility(View.VISIBLE);
                                else {
                                    holder.ivGift.setVisibility(View.GONE);
                                    if (ProductListNewActivity.gaveGifts.containsKey(product.getProductCode()))
                                        ProductListNewActivity.gaveGifts.remove(product.getProductCode());
                                }
                            } else {
                                if (seriya.getMinAmount() != 0) {
                                    if (seriya.getAmount() >= seriya.getMinAmount())
                                        holder.ivGift.setVisibility(View.VISIBLE);
                                        //groupIvGiftsHolder.get(holder.groupId).setVisibility(View.VISIBLE);
                                    else {
                                        holder.ivGift.setVisibility(View.GONE);
                                        //groupIvGiftsHolder.get(holder.groupId).setVisibility(View.GONE);
                                        if (ProductListNewActivity.gaveGifts.containsKey(seriya.getName()))
                                            ProductListNewActivity.gaveGifts.remove(seriya.getName());
                                    }
                                }
                            }
                        }
                        L.info(String.format("%s chosen goods amount %s", seriya.getName(), seriya.getAmount()));

                        if (mContext instanceof ProductListNewActivity) {
                            ((ProductListNewActivity) mContext).calcTotalPrice();
                        }
                    }
                });

                holder.etNewGiftAmount.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                    }

                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        final SeriesTemplate seriya = series.get(holder.groupId);
                        final GoodsByBrandTemplate product = seriya.getGoods().get(holder.childId);
                        if (s.length() > 0 && !s.toString().equals("0")) {
                            int count;
                            try {
                                count = Integer.parseInt(s.toString());
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                count = 0;//product.getAvailable();
                            }

                        /*if (count > product.getAvailable()) {
                            holder.etNewGiftAmount.setText(String.format("%s", product.getAvailable()));
                            holder.etNewGiftAmount.setSelection(holder.etNewGiftAmount.getText().length());
                            return;
                        }*/

                            //seriya.setAmount((seriya.getAmount() - product.getGiftAmount()) + count);

                            product.setGiftAmount(count);

                            if (!ProductListNewActivity.madeOrder.getGoodsList().containsKey(product.getProductCode()))
                                ProductListNewActivity.madeOrder.getGoodsList().put(product.getProductCode(), product);
                            else {
                                final GoodsByBrandTemplate chosenProduct = ProductListNewActivity.madeOrder.getGoodsList().get(product.getProductCode());
                                chosenProduct.setGiftAmount(product.getGiftAmount());
                            }
                        } else {
                            //seriya.setAmount(seriya.getAmount() - product.getGiftAmount());
                            product.setGiftAmount(0);
                            // Если количество товара и подарков 0, то удалим
                            if (ProductListNewActivity.madeOrder.getGoodsList().containsKey(product.getProductCode())
                                    && ProductListNewActivity.madeOrder.getGoodsList().get(product.getProductCode()).getAmount() == 0)
                                ProductListNewActivity.madeOrder.getGoodsList().remove(product.getProductCode());
                        }

                    }
                });



                convertView.setTag(holder);
            } else {
                holder = (Holder) convertView.getTag();

            }

            holder.groupId = i;
            holder.childId = i1;

            SeriesTemplate seriya = series.get(i);
            GoodsByBrandTemplate item = seriya.getGoods().get(i1);
            StyleTextUtils.Companion.colorSubSeq(item.getProductName(), searchedText, Color.YELLOW, holder.tvProduct);

            if (item.getAvailable() < 0) {
                holder.tvAvailable.setTextColor(Color.RED);
                holder.tvLabelCount.setTextColor(Color.RED);
            } else {
                holder.tvAvailable.setTextColor(ResourcesCompat.getColor(getContext().getResources(), R.color.colorText1, null));
                holder.tvLabelCount.setTextColor(ResourcesCompat.getColor(getContext().getResources(), R.color.colorPrimary, null));
            }
            holder.tvAvailable.setText(String.format("%s", item.getAvailable()));

            holder.tvPrice.setText((item.getDiscountValue() == 0 ? item.getPrice() + "" : Html.fromHtml(String
                    .format("<s>%s</s> <i>%s -%s%%</i>", item.getOriginalPrice(), item.getPrice(), item.getDiscountValue()))));

            if (series.get(i).getMinAmount() == 0 && item.getMinAmount() != 0) {
                holder.tvGiftInfo.setVisibility(View.VISIBLE);
                holder.tvGiftInfo.setText(getString(R.string.gift_info, item.getMinAmount()));
            } else
                holder.tvGiftInfo.setVisibility(View.GONE);

            if (seriya.getMinAmount() != 0)
                holder.ivGift.setVisibility((seriya.getAmount() >= seriya.getMinAmount() ? View.VISIBLE : View.GONE));
            else if (item.getMinAmount() != 0)
                holder.ivGift.setVisibility((item.getAmount() >= item.getMinAmount() ? View.VISIBLE : View.GONE));
            else
                holder.ivGift.setVisibility(View.GONE);

            // * Отображает уведомление о нехватке товара на складе
            if (ProductListNewActivity.madeOrder.getNotEnoughGoods().containsKey(item.getProductCode())) {
                holder.tvProductNotif.setVisibility(View.VISIBLE);
                holder.tvProductNotif.setText(getString(R.string.hint_reach_limit, item.getAmount() + "",
                        ProductListNewActivity.madeOrder.getNotEnoughGoods().get(item.getProductCode())));
            } else
                holder.tvProductNotif.setVisibility(View.GONE);

            if (ProductListNewActivity.madeOrder.getGoodsList().containsKey(item.getProductCode()))
                item.setAmount(ProductListNewActivity.madeOrder.getGoodsList().get(item.getProductCode()).getAmount());


            holder.etAmount.setText((item.getAmount() != 0 ? String.format("%s", item.getAmount()) : ""));



            // Количество подарков по акции
            if (ProductListNewActivity.madeOrder.getGoodsList().containsKey(item.getProductCode()))
                item.setGiftAmount(ProductListNewActivity.madeOrder.getGoodsList().get(item.getProductCode()).getGiftAmount());
            holder.etNewGiftAmount.setText((item.getGiftAmount() != 0 ? String.format("%s", item.getGiftAmount()) : ""));



            return convertView;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return false;
        }
    }
}