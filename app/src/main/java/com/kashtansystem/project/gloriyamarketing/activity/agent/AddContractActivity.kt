package com.kashtansystem.project.gloriyamarketing.activity.agent

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import com.google.gson.Gson
import com.kashtansystem.project.gloriyamarketing.R
import com.kashtansystem.project.gloriyamarketing.adapters.BusinessRegAdapter
import com.kashtansystem.project.gloriyamarketing.core.BaseKActivity
import com.kashtansystem.project.gloriyamarketing.database.AppDB
import com.kashtansystem.project.gloriyamarketing.models.template.ContractTemplateFull
import com.kashtansystem.project.gloriyamarketing.models.template.ContractTypeTemplate
import com.kashtansystem.project.gloriyamarketing.models.template.PS_CitiesDistrictContract
import com.kashtansystem.project.gloriyamarketing.models.template.TradingPointTemplate
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqAddContract
import com.kashtansystem.project.gloriyamarketing.utils.AppCache
import com.redmadrobot.inputmask.MaskedTextChangedListener
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_add_contracts.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AddContractActivity : BaseKActivity() {
    lateinit var tradingPointTemplate: TradingPointTemplate
    var contractTypeTemplate: ContractTypeTemplate? = null
    var districtTypeTemplate: PS_CitiesDistrictContract? = null
    var districtTemplate: PS_CitiesDistrictContract? = null
    var contractTemplate: ContractTemplateFull? = null

    var simpleDateFormat = SimpleDateFormat("dd.MM.yyyy")
    var listOfContractTypeTemplate = arrayListOf<ContractTypeTemplate>()
    var listOfDistrictTemplate = arrayListOf<PS_CitiesDistrictContract>()

    companion object {
        const val TRADING_POINT = "TRADING_POINT"
        const val CONTRACT_TYPE = "CONTRACT_TYPE"
        const val DOCUMENT_TEMPLATE = "DOCUMENT_TEMPLATE"

        fun getInstance(activity: Activity, tradingPoint: TradingPointTemplate, contractType: ContractTypeTemplate? = null): Intent {
            var intent = Intent(activity, AddContractActivity::class.java)
            intent.putExtra(TRADING_POINT, tradingPoint)

            if (contractType != null)
                intent.putExtra(CONTRACT_TYPE, Gson().toJson(contractType))

            return intent
        }

        fun getInstanceWithDetails(activity: Activity, tradingPoint: TradingPointTemplate, contractTemplate: ContractTemplateFull, contractType: ContractTypeTemplate): Intent {
            var intent = Intent(activity, AddContractActivity::class.java)
            intent.putExtra(TRADING_POINT, tradingPoint)
            intent.putExtra(CONTRACT_TYPE, Gson().toJson(contractType))
            intent.putExtra(DOCUMENT_TEMPLATE, Gson().toJson(contractTemplate))
            return intent
        }
    }

    override fun init(bundle: Bundle?) {
        if (intent.extras != null) {
            tradingPointTemplate = intent?.getParcelableExtra(TRADING_POINT) ?: return
            if (intent?.hasExtra(CONTRACT_TYPE) == true) {
                contractTypeTemplate = Gson().fromJson(intent?.extras?.getString(CONTRACT_TYPE), ContractTypeTemplate::class.java)
            }
            if (intent?.hasExtra(DOCUMENT_TEMPLATE) == true) {
                contractTemplate = Gson().fromJson(intent?.extras?.getString(DOCUMENT_TEMPLATE), ContractTemplateFull::class.java)
            }
        } else {
            finish()
        }


        // <-- @author MrJ
        uploadesCompositeDisposable.add(
                Single.create<ArrayList<PS_CitiesDistrictContract>> { it.onSuccess(AppDB.getInstance(this).districts) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            listOfDistrictTemplate = it
                            val adapter = ArrayAdapter(this, R.layout.spinner_item_tv, it.map { it.nameDistrict })
                            spContractDistrict.adapter = adapter

                            if (districtTypeTemplate != null) {
                                it.forEachIndexed { index, districtTypeTemplate ->
                                    if (this@AddContractActivity.districtTypeTemplate?.nameDistrict == districtTypeTemplate.nameDistrict) {
                                        spContractDistrict.setSelection(index)
                                        spContractDistrict.isEnabled = false
                                    }
                                }
                            }

                        }, {
                            it.printStackTrace()
                        })
        )
        // -->


        uploadesCompositeDisposable.add(
                Single.create<ArrayList<ContractTypeTemplate>> { it.onSuccess(AppDB.getInstance(this).contractTypes) }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            listOfContractTypeTemplate = it
                            val adapter = ArrayAdapter(this, R.layout.spinner_item_tv, it.map { it.name })
                            spContractType.adapter = adapter

                            if (contractTypeTemplate != null) {
                                it.forEachIndexed { index, contractTypeTemplate ->
                                    if (this@AddContractActivity.contractTypeTemplate?.name == contractTypeTemplate.name) {
                                        spContractType.setSelection(index)
                                        spContractType.isEnabled = false
                                    }
                                }
                            }

                        }, {
                            it.printStackTrace()
                        })
        )



        spContractType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                if (listOfContractTypeTemplate.get(p2).shouldPassport) {
                    etPassportDate.visibility = View.VISIBLE
                    etPassportNumber.visibility = View.VISIBLE
                    tbL1.visibility = View.VISIBLE
                    tbL2.visibility = View.VISIBLE
                } else {
                    etPassportDate.visibility = View.GONE
                    etPassportNumber.visibility = View.GONE
                    tbL1.visibility = View.GONE
                    tbL2.visibility = View.GONE
                }
            }

        }

        spContractDistrict.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

            }

        }

        etDateContract.setText(simpleDateFormat.format(Date()))

        etReferenceDate.inputType = InputType.TYPE_CLASS_NUMBER;
        etReferenceDate.keyListener = DigitsKeyListener.getInstance("1234567890+-() .")

        etReferenceDate.addTextChangedListener(MaskedTextChangedListener("[00].[00].[0000]", etReferenceDate, object : MaskedTextChangedListener.ValueListener {
            override fun onTextChanged(maskFilled: Boolean, extractedValue: String, formattedValue: String) {

            }
        }))

        etSertificateDate.inputType = InputType.TYPE_CLASS_NUMBER;
        etSertificateDate.keyListener = DigitsKeyListener.getInstance("1234567890+-() .")

        etSertificateDate.addTextChangedListener(MaskedTextChangedListener("[00].[00].[0000]", etSertificateDate, object : MaskedTextChangedListener.ValueListener {
            override fun onTextChanged(maskFilled: Boolean, extractedValue: String, formattedValue: String) {

            }
        }))

        chbUnlimited.setOnCheckedChangeListener { compoundButton, checked ->
            if (checked) {
                tinSertificat.visibility = View.GONE
            } else
                tinSertificat.visibility = View.VISIBLE
        }

        cvCreateContract.setOnClickListener {

            if (isValid()) {
                showLoading("Создание...")
                Single.create<Array<String>> {
                    val dateString = etSertificateDate.text.toString()
                    val projectCode = AppCache.USER_INFO.getProjectCode().toString()
                    it.onSuccess(
                            ReqAddContract.send(
                                    Date(),
                                    tradingPointTemplate.tpCode,
                                    simpleDateFormat.parse(etReferenceDate.text.toString()),
                                    if(chbUnlimited.isChecked) Date() else simpleDateFormat.parse(dateString),
                                    etReferenceNumber.text.toString(),
                                    etSertificateNumber.text.toString(),
                                    listOfContractTypeTemplate[spContractType.selectedItemPosition].name,
                                    if (etPassportNumber.visibility == View.VISIBLE) etPassportNumber.text.toString() else null,
                                    if (etPassportDate.visibility == View.VISIBLE) simpleDateFormat.parse(etPassportDate.text.toString()) else null,
                                    chbUnlimited.isChecked,
                                    listOfDistrictTemplate[spContractDistrict.selectedItemPosition].codeDistrict.toString(),
                                    projectCode,
                                    spContractDistrict.selectedItem.toString()
                            )
                    )
                }.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            hideLoading()
                            if (it[0] == "1") {
                                Toast.makeText(this, "Добавлен договор под номером : " + it[2], Toast.LENGTH_SHORT).show()
                                finish()
                            } else {
                                showInformationDialog("Ошибка", it[1])
                            }
                        }, {
                            hideLoading()
                            it.printStackTrace()
                            showInformationDialog("Ошибка", "Не получил ответ")
                        })
            }
        }

        contractTemplate?.let {
            etSertificateNumber.setText(it.numbCertificate)
            etReferenceNumber.setText(it.numbReference)
            etReferenceDate.setText(simpleDateFormat.format(it.termReference))
            etPassportDate.setText(simpleDateFormat.format(it.termPassport))
            etPassportNumber.setText(it.numbPassport)
            etSertificateDate.setText(simpleDateFormat.format(it.termCertificate))

            chbUnlimited.isChecked = it.isUnlinTermCertificate

            if (it.isUnlinTermCertificate) {
                tinSertificat.visibility = View.GONE
            } else {
                tinSertificat.visibility = View.VISIBLE
            }

        }
    }

    private fun isValid(): Boolean {


        if (!chbUnlimited.isChecked && etSertificateDate.text.length != 10) {
            etSertificateDate.setError("Не заполнено")
            return false
        }

        if (etReferenceDate.text.length != 10) {
            etReferenceDate.setError("Не заполнено")
            return false
        }


        if (etReferenceNumber.text.isEmpty()) {
            etReferenceNumber.setError("Не заполнено")
            return false
        }

        if (etSertificateNumber.text.isEmpty()) {
            etSertificateNumber.setError("Не заполнено")
            return false
        }

        if (etPassportNumber.visibility == View.VISIBLE && etPassportNumber.text.isEmpty()) {
            etPassportNumber.setError("Не заполнено")
            return false
        }

        if (etPassportDate.visibility == View.VISIBLE && etPassportDate.text.length != 10) {
            etPassportDate.setError("Не заполнено")
            return false
        }

        // <-- @author MrJ
        if (etSertificateNumber.text.isEmpty()) {
            etSertificateNumber.setError("Не заполнено")
            return false
        }
        // -->


        return true
    }

    override fun getLayoutResource(): Int = R.layout.activity_add_contracts

    override fun initialSupportActionBar(): Boolean = true

    override fun getActionBarTitle(): String = "Добавить Договор"

    override fun getHomeButtonEnable(): Boolean = true
}