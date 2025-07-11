package com.kashtansystem.project.gloriyamarketing.activity.collector

import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import com.kashtansystem.project.gloriyamarketing.R
import com.kashtansystem.project.gloriyamarketing.core.BaseKActivity
import com.kashtansystem.project.gloriyamarketing.models.constants.Currencies
import com.kashtansystem.project.gloriyamarketing.models.template.IncomeReasonTemplate
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqGetIncomeReasonList
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqSetCashOtherIncomes
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_other_income.*
import kotlinx.android.synthetic.main.app_actionbar.*
import java.util.*


class OtherIncomeActivity : BaseKActivity() {

    internal var incomeReasonList: ArrayList<IncomeReasonTemplate>? = null

    override fun getLayoutResource(): Int = R.layout.activity_other_income

    override fun init(bundle: Bundle?) {
        val disposable = Single
                .create<ArrayList<IncomeReasonTemplate>> { it ->
                    val responseCahers = ReqGetIncomeReasonList.load()
                    it.onSuccess(responseCahers)
                }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe {
                    showLoading(getString(R.string.dialog_text_load_data))
                }
                .subscribe({ remoteIncomeReason ->
                    incomeReasonList = remoteIncomeReason
                    spTypeIncome.adapter = ArrayAdapter<String>(this,
                            R.layout.spinner_item_tv, incomeReasonList?.map { it.name }?.toTypedArray()
                            ?: arrayOf<String>())
                    hideLoading()
                }, {
                    it.printStackTrace()
                })
        uploadesCompositeDisposable.add(disposable)


        spCurrencyTpe.adapter = ArrayAdapter<String>(this,
                R.layout.spinner_item_tv, Currencies.getCurrenciesList())

        btnComplete.setOnClickListener {
            var isValid = true

            if (etTransferCash.text.isEmpty()) {
                etTransferCash.error = getString(R.string.e_field_required)
                isValid = false
            }
            if (etComment.text.isEmpty()) {
                etComment.error = getString(R.string.e_field_required)
                isValid = false
            }

            if (isValid) {
                val disposable = Single
                        .create<Array<String>> { emit ->
                            val arrayResult = ReqSetCashOtherIncomes.send(
                                    incomeReasonList?.get(spTypeIncome.selectedItemPosition)?.code
                                            ?: throw Throwable("IncomeReasonIsEmpty"),
                                    Currencies.getCodeForPosition(spCurrencyTpe.selectedItemPosition).toString(),
                                    etTransferCash.text.toString(),
                                    etComment.text.toString())
                            emit.onSuccess(arrayResult)
                        }
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe { _ ->
                            showLoading(getString(R.string.dialog_text_load_data))
                        }
                        .subscribe({ remoteIncomeReason ->
                            Toast.makeText(this@OtherIncomeActivity, remoteIncomeReason[1], Toast.LENGTH_LONG).show()
                            hideLoading()
                            finish()
                        }, { throwable ->
                            throwable.printStackTrace()
                        })
                uploadesCompositeDisposable.add(disposable)
            }
        }
        btnCancel.setOnClickListener {
            finish()
        }

    }


    override fun getActionBarTitle(): String = getString(R.string.other_income)
    override fun getHomeButtonEnable(): Boolean = true
    override fun initialSupportActionBar(): Boolean = true
}