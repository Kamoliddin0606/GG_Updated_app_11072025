package com.kashtansystem.project.gloriyamarketing.activity.agent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.kashtansystem.project.gloriyamarketing.R
import com.kashtansystem.project.gloriyamarketing.activity.main.BaseActivity
import com.kashtansystem.project.gloriyamarketing.adapters.ContractsAdapter
import com.kashtansystem.project.gloriyamarketing.core.BaseKActivity
import com.kashtansystem.project.gloriyamarketing.models.listener.OnDateSelectedListener
import com.kashtansystem.project.gloriyamarketing.models.template.ContractTemplate
import com.kashtansystem.project.gloriyamarketing.models.template.TradingPointTemplate
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqChangeContract
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqGetContract
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqGetOldContract
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_old_contracts.*
import java.text.SimpleDateFormat
import java.util.*

class OldContractsActivity: BaseKActivity() {
    lateinit var tradingPointTemplate: TradingPointTemplate
    lateinit var adapter: ContractsAdapter
    var simpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

    companion object{
        const val TRADING_POINT = "TRADING_POINT"
        fun getInstance(activity: Activity,  tradingPoint: TradingPointTemplate): Intent {
            var intent =  Intent(activity, OldContractsActivity::class.java)
            intent.putExtra(TRADING_POINT, tradingPoint)
            return intent
        }
    }

    override fun init(bundle: Bundle?) {
        if(intent.extras != null) {
            tradingPointTemplate = intent?.getParcelableExtra(TRADING_POINT) ?: return
        }else{
            finish()
        }

        adapter = ContractsAdapter()

        adapter.listener = object : ContractsAdapter.Listener{
            override fun contractPicked(contractTemplate: ContractTemplate) {
                BaseActivity.showCalendarDialog(this@OldContractsActivity) { view, values ->
                    uploadesCompositeDisposable.add(
                            Single.create<Array<String>> { emitter ->
                                emitter.onSuccess(ReqChangeContract.load(contractTemplate.codeContract,simpleDateFormat.parse(view),contractTemplate.nameContract))
                            }.subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({ result ->
                                        if (result[0] == "1"){
                                            updateList()
                                        }
                                        Toast.makeText(this@OldContractsActivity, result[1], Toast.LENGTH_LONG).show()
                                    },{
                                        it.printStackTrace()
                                    })
                    )

                }

            }
        }

        rvContracts.apply {
            this.adapter = this@OldContractsActivity.adapter
            layoutManager = LinearLayoutManager(this@OldContractsActivity, LinearLayoutManager.VERTICAL, false)
        }

    }

    override fun getLayoutResource(): Int = R.layout.activity_old_contracts

    override fun initialSupportActionBar(): Boolean = true

    override fun getActionBarTitle(): String = "Продления договора"

    override fun getHomeButtonEnable(): Boolean = true

    var disposable: Disposable? = null
    override fun onStart() {
        super.onStart()
        showLoading("Загрузка данных...")

        updateList()
    }

    fun updateList(){
        disposable = Single.create<List<ContractTemplate>> {
            it.onSuccess(ReqGetOldContract.load(this, tradingPointTemplate.tpCode))
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe({
                    adapter.setItems(it)
                    hideLoading()
                },{
                    hideLoading()
                    it.printStackTrace()
                })
    }
}