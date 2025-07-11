package com.kashtansystem.project.gloriyamarketing.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kashtansystem.project.gloriyamarketing.R
import com.kashtansystem.project.gloriyamarketing.models.template.ContractTemplate
import com.kashtansystem.project.gloriyamarketing.models.template.ContractTemplateFull
import kotlinx.android.synthetic.main.item_contract.view.*
import java.text.DecimalFormat
import java.text.SimpleDateFormat

class ContractsAdapter: RecyclerView.Adapter<ContractsAdapter.ContractsViewHolder>(){

    var listener: ContractsAdapter.Listener? = null

    var list: List<ContractTemplate> = listOf()

    val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy")
    var decimalFormat: DecimalFormat = DecimalFormat("#,###.##")
    fun setItems(list: List<ContractTemplate>){
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, p1: Int): ContractsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contract, parent, false)
        return ContractsViewHolder(view)
    }

    override fun getItemCount(): Int =list.size

    override fun onBindViewHolder(holder: ContractsViewHolder, position: Int) {
        val item = list[position]

        if (listener == null){
            holder.llPartOfPick.visibility = View.GONE
        }else{
            holder.llPartOfPick.visibility = View.VISIBLE
        }

        holder.tvContractDate.text = simpleDateFormat.format(item.dateOfContact)
        holder.tvTermOfContract.text = simpleDateFormat.format(item.termsOfContract)
        holder.tvContractSum.text = decimalFormat.format(item.sumOfContract)
        holder.tvContractName.text=  "Договор №" + item.codeContract + "  " + item.nameContract
    }

    inner class ContractsViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var tvContractName = view.tvContractName
        var tvContractDate = view.tvContractDate
        var tvContractSum = view.tvContractSum
        var tvTermOfContract = view.tvTermOfContract
        var llPartOfPick = view.llPartOfPick
        var btnContact = view.btnContact

        init {
            btnContact.setOnClickListener {
                if(adapterPosition == RecyclerView.NO_POSITION) return@setOnClickListener

                listener?.contractPicked(list[adapterPosition])
            }
        }
    }
    interface Listener{
        fun contractPicked(contractTemplate: ContractTemplate)
    }
}