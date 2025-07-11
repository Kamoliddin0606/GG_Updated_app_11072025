package com.kashtansystem.project.gloriyamarketing.activity.boss.adapter

import android.graphics.Color
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.kashtansystem.project.gloriyamarketing.R
import com.kashtansystem.project.gloriyamarketing.models.template.ExpenseTemplate
import com.kashtansystem.project.gloriyamarketing.utils.StyleTextUtils
import kotlinx.android.synthetic.main.history_expenses_item.view.*
import java.util.*

class HistoryExpenseListAdapter : RecyclerView.Adapter<HistoryExpenseListAdapter.ViewHolder>() {
    private var items = ArrayList<ExpenseTemplate>()




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_expenses_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val expenseTemplate = items[position]
        StyleTextUtils.colorSubSeq(expenseTemplate.info,searchText, Color.YELLOW,holder.tvInfo)
        if(expenseTemplate.isConfirmed){
            holder.tvStatus.text = "Подтверждено"
            holder.tvStatus.setTextColor(ResourcesCompat.getColor(holder.tvStatus.context.resources,R.color.colorAproved,null))

        }else{
            holder.tvStatus.text = "В ожидании"
            holder.tvStatus.setTextColor(ResourcesCompat.getColor(holder.tvStatus.context.resources,R.color.colorAccent,null))
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
    var searchText = ""
    fun setItems(list: ArrayList<ExpenseTemplate>, searchText: String) {
        this.searchText = searchText
        items = list
        notifyDataSetChanged()
    }

    fun getItem(position: Int): ExpenseTemplate {
        return items[position]
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvInfo: TextView = view.tvInfo
        var tvStatus: TextView = view.tvStatus

        init {
        }
    }

}