//package com.kashtansystem.project.gloriyamarketing.activity.collector.adapter
//
//import android.content.Context
//import android.support.v7.widget.RecyclerView
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.CheckBox
//import android.widget.LinearLayout
//import android.widget.TextView
//import com.kashtansystem.project.gloriyamarketing.R
//import com.kashtansystem.project.gloriyamarketing.models.template.ForwarderBodyTemplate
//import com.kashtansystem.project.gloriyamarketing.models.template.ForwarderHeaderTemplate
//import com.kashtansystem.project.gloriyamarketing.utils.formatDecimals
//import kotlinx.android.synthetic.main.item_forwarder.view.*
//
//class TakeCashAdapter(val context: Context) : RecyclerView.Adapter<TakeCashAdapter.TakeCashViewHolder>() {
//    var list: List<ForwarderHeaderTemplate> = ArrayList()
//    var listExpandabled = Array(1) { false }
//    var listSelected = ArrayList<Selecteds>()
//
//    override fun onCreateViewHolder(parent: ViewGroup, position: Int): TakeCashViewHolder {
//        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_forwarder, parent, false)
//        val itemCounts = list.get(position).details.size
//        if (itemCounts > 0) {
//            val child = LayoutInflater.from(parent.context).inflate(R.layout.item_sub_forwarder, parent, false)
//            view.findViewById<LinearLayout>(R.id.llSubItems).addView(child)
//            child.visibility = View.GONE
//        }
//        return TakeCashViewHolder(view)
//    }
//
//    override fun getItemCount(): Int = list.size
//
//    override fun getItemViewType(position: Int): Int {
//        return position
//    }
//
//    fun setContent(items: ArrayList<ForwarderHeaderTemplate>) {
//        list = items
//        listExpandabled = Array(list.size) { false }
//        listSelected.clear()
//        notifyDataSetChanged()
//    }
//
//
//    override fun onBindViewHolder(holder: TakeCashViewHolder, position: Int) {
//        val item = list[position]
//        holder.tvForwarderName.text = item.userName
//        holder.tvTotalForwarderAmount.text = item.totalCash.formatDecimals() + " uzs"
//        var count = 0
//        for (i in 0 until holder.llSubItems.childCount) {
//            holder.llSubItems.getChildAt(i).findViewById<TextView>(R.id.tvForwarderName).text = item.details[i].tpName
//            holder.llSubItems.getChildAt(i).findViewById<TextView>(R.id.tvTotalForwarderAmount).text = item.details[i].cash.formatDecimals() + " uzs"
//            if (listExpandabled[position]) {
//                holder.llSubItems.getChildAt(i).visibility = View.VISIBLE
//            }
//            if(listSelected.map { it.forwarderBodyTemplate }.indexOf(item.details[i])!=-1){
//                holder.llSubItems.getChildAt(i).findViewById<CheckBox>(R.id.chSubForwarder).isChecked = true
//                count++
//            }else{
//                holder.llSubItems.getChildAt(i).findViewById<CheckBox>(R.id.chSubForwarder).isChecked = false
//            }
//        }
//        holder.chForwarder.isChecked = count == holder.llSubItems.childCount
//        if (listExpandabled[position]) {
//            holder.ivForwarder.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp)
//        } else {
//            holder.ivForwarder.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp)
//        }
//
//    }
//
//    interface TakeCashAdapterListner {
//        fun onClick(item: ForwarderHeaderTemplate)
//    }
//
//    inner class TakeCashViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val llSubItems = view.llSubItems
//        val chForwarder = view.chForwarder
//        val tvForwarderName = view.tvForwarderName
//        val tvTotalForwarderAmount = view.tvTotalForwarderAmount
//        val ivForwarder = view.ivForwarder
//
//        init {
//            view.post {
//                view.setOnClickListener {
//                    listExpandabled[adapterPosition] = !listExpandabled[adapterPosition]
//                    notifyDataSetChanged()
//                }
//                for (i in 0 until llSubItems.childCount) {
//                    llSubItems.getChildAt(i).findViewById<CheckBox>(R.id.chSubForwarder)
//                            .setOnCheckedChangeListener { buttonView, isChecked ->
//                                val item = list.get(adapterPosition).details.get(i)
//                                if (listSelected.map { it.forwarderBodyTemplate }.indexOf(item) != -1) {
//                                    listSelected.removeAt(listSelected.map { it.forwarderBodyTemplate }.indexOf(item))
//
//                                    chForwarder.isChecked = false
//                                } else {
//                                    listSelected.add(Selecteds(list[adapterPosition],item))
//                                    list.get(adapterPosition).details.forEach {
//                                        if (listSelected.map { it.forwarderBodyTemplate }.indexOf(it) != -1) {
//                                            return@setOnCheckedChangeListener
//                                        }
//                                    }
//                                    chForwarder.isChecked = true
//                                }
//                            }
//                }
//                chForwarder.setOnCheckedChangeListener { buttonView, isChecked ->
//                    if (isChecked) {
//                        list.get(adapterPosition).details.forEach {
//                            listSelected.add(Selecteds(list[adapterPosition],it))
//                        }
//                        notifyDataSetChanged()
//                    } else {
//                        list.get(adapterPosition).details.forEach {
//                            if (listSelected.map { it.forwarderBodyTemplate }.indexOf(it) != -1) {
//                                return@setOnCheckedChangeListener
//                            }
//                        }
//                        list.get(adapterPosition).details.forEach {
//                            listSelected.removeAt(listSelected.map { it.forwarderBodyTemplate }.indexOf(it))
//                        }
//                        notifyDataSetChanged()
//                    }
//                }
//            }
//
//
//        }
//
//
//    }
//    data class Selecteds(
//            var forwarderHeader: ForwarderHeaderTemplate,
//            var forwarderBodyTemplate: ForwarderBodyTemplate
//    )
//    fun getSelectedItems():ArrayList<Selecteds>{
//        return listSelected
//    }
//}