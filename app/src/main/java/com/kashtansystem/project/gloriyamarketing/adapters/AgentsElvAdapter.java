package com.kashtansystem.project.gloriyamarketing.adapters;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.models.template.AgentTemplate;

import java.util.ArrayList;

/**
 * Created by FlameKaf on 04.07.2017.
 * ----------------------------------
 */

public class AgentsElvAdapter extends BaseExpandableListAdapter
{
    private ArrayList<AgentTemplate> items;
    private LayoutInflater layoutInflater;
    private View.OnClickListener clickListener;

    public AgentsElvAdapter(Context context, ArrayList<AgentTemplate> items,
        View.OnClickListener clickListener)
    {
        layoutInflater = LayoutInflater.from(context);
        this.items = new ArrayList<>(items.size());
        this.items.addAll(items);
        this.clickListener = clickListener;
    }

    @Override
    public int getGroupCount()
    {
        return items.size();
    }

    @Override
    public int getChildrenCount(int groupPosition)
    {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition)
    {
        return items.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        return items.get(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    @Override
    public boolean hasStableIds()
    {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        if (convertView == null)
            convertView = layoutInflater.inflate(R.layout.trading_points_elv_group_item, null);

        ((TextView)convertView.findViewById(R.id.elvGroupItem)).setText(items.get(groupPosition)
            .getAgentName());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        if (convertView == null)
        {
            convertView = layoutInflater.inflate(R.layout.agents_elv_child_item, null);
            convertView.findViewById(R.id.elvAgentsAllTp).setOnClickListener(clickListener);
            convertView.findViewById(R.id.tvElvAgentsLastAddress).setOnClickListener(clickListener);
        }

        final AgentTemplate agentTemplate = items.get(groupPosition);

        TextView allTp = (TextView)convertView.findViewById(R.id.elvAgentsAllTp);
        allTp.setText(Html.fromHtml(String.format("<u>%s</u>", allTp.getContext().getString(
            R.string.label_all_agents_tp))));
        allTp.setTag(groupPosition);

        TextView lastKnownAddress = (TextView)convertView.findViewById(R.id.tvElvAgentsLastAddress);
        lastKnownAddress.setText(agentTemplate.getLastKnownAddress());
        lastKnownAddress.setTag(groupPosition);

        ((TextView)convertView.findViewById(R.id.tvElvAgentsAmTakenOrders)).setText(String.format("%s",
            agentTemplate.getAmountOfTakenOrders()));
        ((TextView)convertView.findViewById(R.id.tvElvAgentsVisTp)).setText(String.format("%s",
            agentTemplate.getAmountOfVisitedTp()));
        ((TextView)convertView.findViewById(R.id.tvElvAgentComment)).setText(
            agentTemplate.getCommentFromAgent());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return false;
    }
}