package com.kashtansystem.project.gloriyamarketing.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.models.template.ForwarderBodyTemplate;
import com.kashtansystem.project.gloriyamarketing.models.template.ForwarderHeaderTemplate;
import com.kashtansystem.project.gloriyamarketing.utils.Util;

import java.util.ArrayList;

/**
 * Created by FlameKaf on 14.12.2017.
 * ----------------------------------
 * Адаптер списка экспедиторов, которые должны сдать собранные деньги
 */

public class ForwarderListAdapter extends BaseExpandableListAdapter {
    private LayoutInflater layoutInflater;
    private Listener listener;
    private ArrayList<ForwarderHeaderTemplate> items;

    public class IdHoder {
        public int groupId = -1, childId = -1;
    }

    public ForwarderListAdapter(Context context, Listener listener) {
        layoutInflater = LayoutInflater.from(context);
        this.listener = listener;
    }
    public interface Listener{
        void onForwarderPicked(ForwarderHeaderTemplate forwarderHeaderTemplate);
        void onIncomePicked(ForwarderBodyTemplate forwarderBodyTemplate);
    }
    @Override
    public int getGroupCount() {
        return (items == null ? 0 : items.size());
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return (items.get(groupPosition).getDetails() == null ? 0 : items.get(groupPosition).getDetails().size());
    }

    @Override
    public Object getGroup(int groupPosition) {
        return items.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return items.get(groupPosition).getDetails().get(childPosition);
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
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = layoutInflater.inflate(R.layout.forwarder_header_item, null);

        final ForwarderHeaderTemplate item = items.get(groupPosition);

        ((TextView) convertView.findViewById(R.id.tvFrwdName)).setText(item.getUserName());
        ((TextView) convertView.findViewById(R.id.tvFrwdTotalCash)).setText(Util.getParsedPrice(item.getTotalCash()));
        CheckBox chbForwarder = convertView.findViewById(R.id.chbSelectForwarder);
        chbForwarder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //todo check forwarder
                }
            }
        );

        return convertView;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.forwarders_item, null);
        }

        final ForwarderBodyTemplate childItem = items.get(groupPosition).getDetails().get(childPosition);

        ((TextView) convertView.findViewById(R.id.tvFrwdTradePoint)).setText(childItem.getTpName());
        ((TextView) convertView.findViewById(R.id.tvFrwdCash)).setText(Util.getParsedPrice(childItem.getCash()));

//        View agree = convertView.findViewById(R.id.btnAgree);
//        IdHoder idHoder = (IdHoder) agree.getTag();
//        if (idHoder == null) {
//            idHoder = new IdHoder();
//            agree.setTag(idHoder);
//        }
//        idHoder.groupId = groupPosition;
//        idHoder.childId = childPosition;

//        agree.setEnabled(!childItem.isConfirmed());

        CheckBox chbForwarder = convertView.findViewById(R.id.chbIncome);
        chbForwarder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //todo check income
            }
            }
        );

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public void setItems(ArrayList<ForwarderHeaderTemplate> in) {
        this.items = new ArrayList<>(in.size());
        this.items.addAll(in);
        notifyDataSetChanged();
    }
}