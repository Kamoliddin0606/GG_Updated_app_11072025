package com.kashtansystem.project.gloriyamarketing.activity.boss.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.kashtansystem.project.gloriyamarketing.R
import com.kashtansystem.project.gloriyamarketing.models.template.ExpenseTemplate
import kotlinx.android.synthetic.main.expenses_item.view.*
import java.util.*

class ExpenseListAdapter(private val listener: ExpenseListAdapter.Listener) : RecyclerView.Adapter<ExpenseListAdapter.ViewHolder>() {
    private var items = ArrayList<ExpenseTemplate>()

    interface Listener {
        fun onApprove(expense: ExpenseTemplate)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.expenses_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val expenseTemplate = items[position]

        holder.tvInfo.text = expenseTemplate.info

        holder.btnApprove.tag = position
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun setItems(list: ArrayList<ExpenseTemplate>) {
        items = list
        notifyDataSetChanged()
    }

    fun getItem(position: Int): ExpenseTemplate {
        return items[position]
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var tvInfo: TextView = view.tvInfo
        var btnApprove: Button = view.btnApprove

        init {
            btnApprove.setOnClickListener { listener.onApprove(items[adapterPosition]) }
        }
    }

}