package com.kashtansystem.project.gloriyamarketing.activity.agent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import com.google.gson.Gson
import com.kashtansystem.project.gloriyamarketing.R
import com.kashtansystem.project.gloriyamarketing.core.BaseKActivity
import com.kashtansystem.project.gloriyamarketing.database.AppDB
import com.kashtansystem.project.gloriyamarketing.models.template.ContractTemplate
import com.kashtansystem.project.gloriyamarketing.models.template.ContractTemplateFull
import com.kashtansystem.project.gloriyamarketing.models.template.ContractTypeTemplate
import com.kashtansystem.project.gloriyamarketing.models.template.TradingPointTemplate
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqGetByInnTp
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqGetContract
import com.kashtansystem.project.gloriyamarketing.utils.C
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_tp_by_inn.*
import java.util.*
import kotlin.collections.ArrayList

class TradingPointsByInnActivity : BaseKActivity() {
    var listOfContractTypeTemplate = arrayListOf<ContractTypeTemplate>()


    override fun init(bundle: Bundle?) {
        uploadesCompositeDisposable.add(
                Single.create<ArrayList<ContractTypeTemplate>> {
                    it.onSuccess(AppDB.getInstance(this).contractTypes)
                }.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            listOfContractTypeTemplate = it
                            val adapter = ArrayAdapter(this, R.layout.spinner_item_tv, it.map { it.name })
                            spInnTypes.adapter = adapter
                        }, {
                            it.printStackTrace()
                        })
        )

        cvFindTp.setOnClickListener {


            if (etAmountOfContract.text.isEmpty()) {
                etAmountOfContract.setError("Пустой")
                return@setOnClickListener
            }

            showLoading("Поиск по ИНН")
            uploadesCompositeDisposable.add(
                    Single.create<LinkedList<TradingPointTemplate>> {
                        it.onSuccess(
                                ReqGetByInnTp.load(etAmountOfContract.text.toString())
                        )
                    }.subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe({ tplist ->
                                hideLoading()


                                //if trading point not found
                                if (tplist.isEmpty()) {
                                    uploadesCompositeDisposable.add(
                                            Single.create<LinkedList<TradingPointTemplate>> {
                                                it.onSuccess(
                                                        ReqGetByInnTp.loadWithAnotherProject(etAmountOfContract.text.toString())
                                                )
                                            }.subscribeOn(Schedulers.io())
                                                    .observeOn(AndroidSchedulers.mainThread())
                                                    .subscribe({
                                                        if (it.isEmpty()) {

                                                            val intent = Intent(this, NewTradingPointActivity::class.java)
                                                            intent.putExtra(NewTradingPointActivity.TRADING_POINT_INN,etAmountOfContract.text.toString());
                                                            startActivityForResult(intent, C.REQUEST_CODES.CREATE_NEW_CLIENT)
                                                        } else {
                                                            val intentNewDetailed = Intent(this, NewTradingPointActivity::class.java)
                                                            intentNewDetailed.putExtra(NewTradingPointActivity.TRADING_POINT, Gson().toJson(it[0]))
                                                            startActivityForResult(intentNewDetailed, C.REQUEST_CODES.CREATE_NEW_CLIENT)
                                                        }
                                                    }, {
                                                        it.printStackTrace()
                                                    })
                                    )
                                    return@subscribe
                                }

                                showLoading("Поиск договор")

                                if (listOfContractTypeTemplate[spInnTypes.selectedItemPosition].name == "Продление") {
                                    hideLoading()

                                    startActivity(OldContractsActivity.getInstance(this, tplist[0]))

                                    return@subscribe
                                }

                                uploadesCompositeDisposable.add(Single.create<List<ContractTemplateFull>> {
                                    it.onSuccess(ReqGetContract.load(this, tplist[0].tpCode))
                                }.subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread()).subscribe({
                                            //
                                            hideLoading()
                                            val contractTypeTemplate = listOfContractTypeTemplate[spInnTypes.selectedItemPosition]

                                            it.forEachIndexed { index, contractTemplate ->
                                                if (contractTemplate.contractType == contractTypeTemplate.name) {
                                                    val intent = Intent(this, PickWarehouseActivity::class.java)
                                                    intent.action = C.ACTIONS.ACTION_CREATE_ORDER
                                                    intent.putExtra(C.KEYS.EXTRA_DATA_TP, tplist[0])
                                                    intent.putExtra(C.KEYS.EXTRA_DATA_CT, contractTemplate.codeContract)
                                                    startActivity(intent)
                                                    return@subscribe
                                                }
                                            }
                                            if(it.isEmpty())
                                                startActivity(AddContractActivity.getInstance(this, tplist[0], contractTypeTemplate))
                                            else
                                                startActivity(AddContractActivity.getInstanceWithDetails(this, tplist[0], it[0] ,contractTypeTemplate))


                                        }, {
                                            hideLoading()
                                            it.printStackTrace()
                                        })
                                )
                            }, {
                                it.printStackTrace()
                            })
            )

        }
    }


    override fun getLayoutResource(): Int = R.layout.activity_tp_by_inn

    override fun initialSupportActionBar(): Boolean = true

    override fun getActionBarTitle(): String = "Поиск точки по ИНН"

    override fun getHomeButtonEnable(): Boolean = true

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == C.REQUEST_CODES.CREATE_NEW_CLIENT) {
            if (resultCode == Activity.RESULT_OK)
                cvFindTp.performClick()
        }
    }

}