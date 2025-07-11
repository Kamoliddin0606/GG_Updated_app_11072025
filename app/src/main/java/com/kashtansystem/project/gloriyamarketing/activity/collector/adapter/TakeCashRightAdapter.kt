package com.kashtansystem.project.gloriyamarketing.activity.collector.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.TextView
import com.kashtansystem.project.gloriyamarketing.R
import com.kashtansystem.project.gloriyamarketing.models.template.ForwarderBodyTemplate
import com.kashtansystem.project.gloriyamarketing.models.template.ForwarderHeaderTemplate
import com.kashtansystem.project.gloriyamarketing.utils.formatDecimals
import kotlinx.android.synthetic.main.item_forwarder.view.*
import kotlinx.android.synthetic.main.item_sub_forwarder.view.*
import java.lang.IllegalStateException

class TakeCashRightAdapter(val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var listSource: ArrayList<ForwarderHeaderTemplate> = ArrayList()
    var list: ArrayList<Any> = ArrayList()
    var listExpandabled = Array(1) { false }
    var listChecked = Array(1) { false }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_forwarder, parent, false)
            return TakeCashViewHolder(view)
        } else {
            val child = LayoutInflater.from(parent.context).inflate(R.layout.item_sub_forwarder, parent, false)
            return ChildViewHolder(child)
        }
    }

    override fun getItemCount(): Int = list.size

    override fun getItemViewType(position: Int): Int {
        return if (list[position] is ForwarderHeaderTemplate) 0 else 1
    }

    fun setContent(items: ArrayList<ForwarderHeaderTemplate>) {
        list.clear()
        listSource = items
        listExpandabled = Array(listSource.size) { false }
        listChecked = Array(listSource.size) { false }
        updateList()
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TakeCashViewHolder) {
            val item = list[position] as ForwarderHeaderTemplate
            holder.initCbForwarderLisntener(listChecked[listSource.indexOf(item)])
            holder.tvForwarderName.text = item.userName
            holder.tvTotalForwarderAmount.text = item.totalCash.formatDecimals()
            if (listExpandabled[listSource.indexOf(item)]) {
                holder.ivForwarder.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp)
            } else {
                holder.ivForwarder.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp)
            }
        } else if (holder is ChildViewHolder) {
            val item = list[position] as ForwarderBodyTemplate
            holder.initCbForwarderLisntener(item.isConfirmed)
            holder.tvForwarderName.text = item.tpName
            holder.tvTotalForwarderAmount.text = item.cash.formatDecimals()
        }

    }

    private fun updateList() {
        list.clear()
        listExpandabled.forEachIndexed { index, isExpand ->
            list.add(listSource[index])
            if (isExpand) {
                list.addAll(listSource[index].details)
            }
        }
        notifyDataSetChanged()
    }

    inner class TakeCashViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val chForwarder = view.chForwarder
        val tvForwarderName = view.tvForwarderName
        val tvTotalForwarderAmount = view.tvTotalForwarderAmount
        val llClickClack = view.llClickClack
        val ivForwarder = view.ivForwarder

        val chForwarderListener: CompoundButton.OnCheckedChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            listChecked[listSource.indexOf((list[adapterPosition] as ForwarderHeaderTemplate))] = isChecked
            (list[adapterPosition] as ForwarderHeaderTemplate).details.forEach { it.isConfirmed = isChecked }
            notifyDataSetChanged()
        }

        fun initCbForwarderLisntener(isChecked: Boolean) {
            chForwarder.setOnCheckedChangeListener(null);
            chForwarder.isChecked = isChecked;
            chForwarder.setOnCheckedChangeListener(chForwarderListener);
        }

        init {
            chForwarder.setOnCheckedChangeListener(chForwarderListener)
            view.setOnClickListener {
                val position = listSource.indexOf((list[adapterPosition] as ForwarderHeaderTemplate))
                listExpandabled[position] =
                        !listExpandabled[position]
                updateList()
            }

        }
    }

    inner class ChildViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val chForwarder = view.chSubForwarderChild
        val tvForwarderName = view.tvForwarderNameChild
        val tvTotalForwarderAmount = view.tvTotalForwarderAmountChild
        val chForwarderListener: CompoundButton.OnCheckedChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            (list[adapterPosition] as ForwarderBodyTemplate).isConfirmed = isChecked
            for (i in adapterPosition downTo 0) {
                if (list[i] is ForwarderHeaderTemplate) {
                    listChecked[listSource.indexOf((list[i] as ForwarderHeaderTemplate))] = false
                    notifyItemChanged(i)
                    return@OnCheckedChangeListener
                }
            }
        }

        fun initCbForwarderLisntener(isChecked: Boolean) {
            chForwarder.setOnCheckedChangeListener(null);
            chForwarder.isChecked = isChecked;
            chForwarder.setOnCheckedChangeListener(chForwarderListener);
        }

        init {
            chForwarder.setOnCheckedChangeListener(chForwarderListener)
            view.setOnClickListener {
                chForwarder.toggle()
            }
        }
    }

    data class Selected(
            var forwarderCode: String,
            var tpCode: String
    )

    fun getSelectedList(): ArrayList<Selected> {
        val listResult = ArrayList<Selected>()
        listSource.forEach { header ->
            header.details.forEach {
                if(it.isConfirmed)
                    listResult.add(Selected(header.userCode,it.tpCode))
            }
        }
        return listResult
    }

}