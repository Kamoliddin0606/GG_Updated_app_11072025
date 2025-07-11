package com.kashtansystem.project.gloriyamarketing.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kashtansystem.project.gloriyamarketing.R;
import com.kashtansystem.project.gloriyamarketing.models.template.SupervisorTemplate;

import java.util.ArrayList;

/**
 * Created by FlameKaf on 04.07.2017.
 * ----------------------------------
 * Не используется
 */

public class SupervisorListAdapter extends RecyclerView.Adapter<SupervisorListAdapter.ViewHolder>
{
    private View.OnClickListener clickListener;
    private ArrayList<SupervisorTemplate> items;

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        public View view;
        public TextView supervisorName, amountOfAgents;

        public ViewHolder(View view)
        {
            super(view);
            this.view = view;

            supervisorName = (TextView)view.findViewById(R.id.supervsrsItemName);
            amountOfAgents = (TextView)view.findViewById(R.id.supervsrsItemAgentAmount);
        }
    }

    public SupervisorListAdapter(View.OnClickListener clickListener)
    {
        this.clickListener = clickListener;
    }

    public SupervisorListAdapter(ArrayList<SupervisorTemplate> items)
    {
        this.items = new ArrayList<>(items.size());
        this.items.addAll(items);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.supervisors_list_item,
            parent, false);
        view.setOnClickListener(clickListener);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        final SupervisorTemplate supervisorTemplate = items.get(position);
        holder.supervisorName.setText(supervisorTemplate.getName());
        holder.amountOfAgents.setText(String.format("%s", supervisorTemplate.getAgents().size()));
        holder.view.setTag(position);
    }

    @Override
    public int getItemCount()
    {
        return items.size();
    }

    public void setItems(ArrayList<SupervisorTemplate> items)
    {
        if (this.items == null)
            this.items = new ArrayList<>(items.size());
        else
            this.items.clear();

        this.items.addAll(items);
        notifyDataSetChanged();
    }
}