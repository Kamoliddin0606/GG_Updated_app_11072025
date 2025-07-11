package com.kashtansystem.project.gloriyamarketing.activity.collector

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.kashtansystem.project.gloriyamarketing.R
import com.kashtansystem.project.gloriyamarketing.activity.collector.adapter.TakeCashRightAdapter
import com.kashtansystem.project.gloriyamarketing.core.BaseKActivity
import com.kashtansystem.project.gloriyamarketing.models.template.ForwarderHeaderTemplate
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqGetShippingCash
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqShippingCashList
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_take_money_new.*

class TakeCashFromForwarderNewActivity : BaseKActivity() {
    lateinit var takeCashAdapter: TakeCashRightAdapter
    override fun init(bundle: Bundle?) {
        takeCashAdapter = TakeCashRightAdapter(this@TakeCashFromForwarderNewActivity)
        rvForwarders.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = takeCashAdapter
        }
        updateList()
        cvApprove.setOnClickListener { _ ->
            val approved = takeCashAdapter.getSelectedList()
            if(approved.size == 0){
                Toast.makeText(this,getString(R.string.nothing_picked), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val dialog = Dialog(this)
            dialog.setTitle(R.string.app_name)
            dialog.setContentView(R.layout.dialogbox)
            val window = dialog.window
            window?.setLayout(-1, -2)

            (dialog.findViewById<View>(R.id.dialogText) as TextView).text = getString(R.string.aprrove)+" "+ approved.size + "?"

            val button1 = dialog.findViewById<View>(R.id.dialogBtn1) as Button
            button1.setOnClickListener{
                dialog.dismiss()
            }
            button1.text = getString(R.string.cancel)

            val button2 = dialog.findViewById<View>(R.id.dialogBtn2) as Button
            button2.setOnClickListener{
                Single.create<Boolean> { emit ->
                    approved.forEach {
                        ReqGetShippingCash.send(it.forwarderCode, it.tpCode)
                    }
                    emit.onSuccess(true)
                }.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe {
                            showLoading(getString(R.string.dialog_text_load_data))
                        }
                        .doOnSuccess {
                            hideLoading()
                        }
                        .subscribe({
                            updateList()
                        }, {
                            it.printStackTrace()
                        })
                dialog.dismiss()
            }
            button2.text = getString(R.string.i_aprove)

            dialog.show()



        }

    }

    fun updateList() {
        val obs =
                Single.create<ArrayList<ForwarderHeaderTemplate>> {
                    it.onSuccess(ReqShippingCashList.load())
                }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe {
                            showLoading(getString(R.string.dialog_text_load_data))
                        }
                        .doOnSuccess {
                            hideLoading()
                        }
                        .subscribe({
                            takeCashAdapter.setContent(it)
                        }, {
                            it.printStackTrace()
                        })
        uploadesCompositeDisposable.add(obs)
    }

    override fun getLayoutResource(): Int = R.layout.activity_take_money_new

    override fun initialSupportActionBar(): Boolean = true

    override fun getActionBarTitle(): String = getString(R.string.app_title_forwarders)

    override fun getHomeButtonEnable(): Boolean = true
}