package com.kashtansystem.project.gloriyamarketing.activity.agent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.kashtansystem.project.gloriyamarketing.R
import com.kashtansystem.project.gloriyamarketing.adapters.ContractsAdapter
import com.kashtansystem.project.gloriyamarketing.core.BaseKActivity
import com.kashtansystem.project.gloriyamarketing.database.AppDB
import com.kashtansystem.project.gloriyamarketing.models.template.ContractTemplate
import com.kashtansystem.project.gloriyamarketing.models.template.ContractTemplateFull
import com.kashtansystem.project.gloriyamarketing.models.template.TradingPointTemplate
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqGetContract
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_contracts.*

class ContractsActivity : BaseKActivity() {
    lateinit var adapter: ContractsAdapter
    var visiblePicker: Boolean = false

    lateinit var tradingPointTemplate: TradingPointTemplate

    companion object{
        const val VISIBLE_PICKER = "VISIBLE_PICKER"
        const val TRADING_POINT = "TRADING_POINT"
        fun getInstance(activity: Activity, visiblePicker: Boolean, tradingPoint: TradingPointTemplate): Intent{
            var intent =  Intent(activity, ContractsActivity::class.java)
            intent.putExtra(VISIBLE_PICKER, visiblePicker)
            intent.putExtra(TRADING_POINT, tradingPoint)
            return intent
        }
    }

    override fun init(bundle: Bundle?) {
        if(intent.extras != null) {
            visiblePicker = intent?.extras?.getBoolean(VISIBLE_PICKER) ?: false
            tradingPointTemplate = intent?.getParcelableExtra(TRADING_POINT) ?: return
        }else{
            finish()
        }

        adapter = ContractsAdapter()

        if(visiblePicker){
            adapter.listener = object : ContractsAdapter.Listener{
                override fun contractPicked(contractTemplate: ContractTemplate) {
                    //todo

                }
            }
        }

        rvContracts.apply {
            this.adapter = this@ContractsActivity.adapter
            layoutManager = LinearLayoutManager(this@ContractsActivity, LinearLayoutManager.VERTICAL, false)
        }


        cvCreateContract.setOnClickListener {
            startActivity(AddContractActivity.getInstance(this, tradingPointTemplate))
        }


    }

    override fun getLayoutResource(): Int = R.layout.activity_contracts

    override fun initialSupportActionBar(): Boolean = true

    override fun getActionBarTitle(): String = getString(R.string.contract)

    override fun getHomeButtonEnable(): Boolean = true

    var disposable: Disposable? = null
    override fun onStart() {
        super.onStart()
        showLoading("Загрузка данных...")

        disposable = Single.create<List<ContractTemplateFull>> {
            it.onSuccess(ReqGetContract.load(this, tradingPointTemplate.tpCode))
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({
                    adapter.setItems(it.map { ContractTemplate(it.dateOfContact,it.codeContract,it.sumOfContract,it.termsOfContract,it.contractType,"") })
                    hideLoading()
                },{
                    hideLoading()
                    it.printStackTrace()
                })
    }
}