package com.kashtansystem.project.gloriyamarketing.core

import android.app.Dialog
import android.app.ProgressDialog
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.kashtansystem.project.gloriyamarketing.R
import com.kashtansystem.project.gloriyamarketing.activity.main.BaseActivity
import io.reactivex.disposables.CompositeDisposable

abstract class BaseKActivity : BaseActivity() {
    var uploadesCompositeDisposable: CompositeDisposable = CompositeDisposable()
    var dialog: Dialog? =  null
    abstract fun init(bundle: Bundle?)
    abstract fun getLayoutResource(): Int
    abstract fun initialSupportActionBar(): Boolean

    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(getLayoutResource())
        if (initialSupportActionBar())
            setSupportActionBar(findViewById(R.id.appToolBar))
        init(bundle)
    }

    override fun onDestroy() {
        uploadesCompositeDisposable.dispose()
        super.onDestroy()
    }

    fun showLoading(string: String){
        dialog = getInformDialog(this,string)
        dialog?.show()
    }
    fun hideLoading(){
        dialog?.cancel()
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(menuItem)
    }


    fun showInformationDialog(title: String,bodyMessage: String){
        val dialog = Dialog(this)
        dialog.setTitle(title)
        dialog.setContentView(R.layout.dialogbox)
        dialog.setCanceledOnTouchOutside(false)
        val window = dialog.window
        window?.setLayout(-1, -2)

        (dialog.findViewById<View>(R.id.dialogText) as TextView).text = bodyMessage

        val button1 = dialog.findViewById<View>(R.id.dialogBtn1) as Button
        button1.visibility = View.GONE
        button1.text = this.getString(R.string.cancell)

        val button2 = dialog.findViewById<View>(R.id.dialogBtn2) as Button
        button2.setOnClickListener {
            dialog.dismiss()
        }
        button2.text = this.getString(R.string.dialog_btn_ok)
        dialog.show()
    }
}