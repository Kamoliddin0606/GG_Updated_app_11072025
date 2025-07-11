package com.kashtansystem.project.gloriyamarketing.activity.agent

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.kashtansystem.project.gloriyamarketing.R
import com.kashtansystem.project.gloriyamarketing.core.BaseKActivity
import com.kashtansystem.project.gloriyamarketing.database.AppDB
import com.kashtansystem.project.gloriyamarketing.models.template.OrganizationTemplate
import com.kashtansystem.project.gloriyamarketing.models.template.TradingPointTemplate
import com.kashtansystem.project.gloriyamarketing.utils.C
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_pick_warehouse.*

class PickWarehouseActivity : BaseKActivity() {
    var organizationTemplates: ArrayList<OrganizationTemplate>? = null
    override fun init(bundle: Bundle?) {

        var orgPosition = PreferenceManager.getDefaultSharedPreferences(this@PickWarehouseActivity).getInt("spOrg", 0)
        var warehousePosition = PreferenceManager.getDefaultSharedPreferences(this@PickWarehouseActivity).getInt("spWarehouse", 0)

        uploadesCompositeDisposable.add(
                Single.create<ArrayList<OrganizationTemplate>> {
                    val organizations = AppDB.getInstance(this).organization
                    it.onSuccess(organizations)
                }
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            organizationTemplates = it

                            if (organizationTemplates?.isEmpty() == true) {
                                showInformationDialog(getString(R.string.emptyOrganization), getString(R.string.admin_not_add_organizaton_yet))
                                return@subscribe
                            }

                            spOrganization.adapter = ArrayAdapter<String>(this, R.layout.spinner_item_tv, it.map { it.name })
                            try {
                                if (it.map { it.name }.size > orgPosition)
                                    spOrganization.setSelection(orgPosition)
                            } catch (e: Exception) {
                            }

                            spWarehouse.adapter = ArrayAdapter<String>(this, R.layout.spinner_item_tv, it[0].warehouses.map { it.name })
                            try {
                                if (it[0].warehouses.map { it.name }.size > warehousePosition)
                                    spWarehouse.setSelection(warehousePosition)
                            } catch (e: Exception) {
                            }

                        }, {
                            it.printStackTrace()
                        })
        )
        spOrganization.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val map = organizationTemplates?.get(position)?.warehouses?.map { it.name }
                spWarehouse.adapter = ArrayAdapter<String>(this@PickWarehouseActivity, R.layout.spinner_item_tv, map
                        ?: return)
                try {
                    if (warehousePosition < map.size) {
                        spWarehouse.setSelection(warehousePosition)
                    }
                    warehousePosition = 0
                }catch (ex: Exception){}
                PreferenceManager.getDefaultSharedPreferences(this@PickWarehouseActivity).edit().putInt("spOrg", position).apply()
            }

        }
        spWarehouse.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                PreferenceManager.getDefaultSharedPreferences(this@PickWarehouseActivity).edit().putInt("spWarehouse", position).apply()
            }

        }
        cvTask.setOnClickListener {
            if (organizationTemplates?.isEmpty() == true) {
                showInformationDialog(getString(R.string.emptyOrganization), getString(R.string.admin_not_add_organizaton_yet))
                return@setOnClickListener
            }


            if (organizationTemplates?.get(spOrganization.selectedItemPosition)?.warehouses?.isEmpty() == true ) {
                showInformationDialog(getString(R.string.sklad_empty), getString(R.string.sklad_empty_body))
                return@setOnClickListener
            }


            try {
                val warehouseCode = organizationTemplates?.get(spOrganization.selectedItemPosition)?.warehouses?.get(spWarehouse.selectedItemPosition)?.code
                val iMakeOrder = Intent(this, MakeOrderNewActivity::class.java)
                iMakeOrder.action = C.ACTIONS.ACTION_CREATE_ORDER
                iMakeOrder.putExtra(C.KEYS.EXTRA_DATA_TP, intent.getParcelableExtra<TradingPointTemplate>(C.KEYS.EXTRA_DATA_TP) as TradingPointTemplate)
                iMakeOrder.putExtra(C.KEYS.EXTRA_DATA_CT, intent.getStringExtra(C.KEYS.EXTRA_DATA_CT))

                iMakeOrder.putExtra(C.KEYS.EXTRA_DATA_W, warehouseCode
                        ?: return@setOnClickListener)
                startActivity(iMakeOrder)
                finish()
            }catch (ex: Exception){
                showInformationDialog(getString(R.string.sklad_empty), getString(R.string.sklad_empty_body))
                return@setOnClickListener
            }


        }
    }

    override fun getLayoutResource(): Int = R.layout.activity_pick_warehouse

    override fun initialSupportActionBar(): Boolean = true

    override fun getActionBarTitle(): String = "Выбор Склада"

    override fun getHomeButtonEnable(): Boolean = true


}