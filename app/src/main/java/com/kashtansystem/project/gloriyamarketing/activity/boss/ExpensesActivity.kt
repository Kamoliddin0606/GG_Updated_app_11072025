package com.kashtansystem.project.gloriyamarketing.activity.boss

import android.content.Intent
import android.support.v4.app.Fragment
import android.view.Menu
import android.view.MenuItem
import com.kashtansystem.project.gloriyamarketing.R
import com.kashtansystem.project.gloriyamarketing.activity.collector.HistoryExpensesCollectorActivity
import com.kashtansystem.project.gloriyamarketing.activity.main.LoginActivity
import com.kashtansystem.project.gloriyamarketing.core.MultiProjectActivity
import com.kashtansystem.project.gloriyamarketing.models.template.UserTemplate
import com.kashtansystem.project.gloriyamarketing.utils.AppCache


class ExpensesActivity: MultiProjectActivity(){

    override fun initialSupportActionBar(): Boolean = true

    override fun getActionBarTitle(): String = getString(R.string.app_title_boss)

    override fun getHomeButtonEnable(): Boolean = true

    override fun getFragmentInstance(): Fragment = ExpensesListFragment()

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.boss_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        if(menuItem.itemId == R.id.mhistory){
            startActivity(Intent(this@ExpensesActivity, HistoryExpensesCollectorActivity::class.java))
        }

        if(menuItem.itemId == R.id.mLogout) {
            DialogBox(this, getString(R.string.dialog_text_ask_to_exit),
                    getString(R.string.dialog_btn_cancel), getString(R.string.dialog_btn_exit),
                    Intent (this, LoginActivity::class.java)
            ) { view, intent ->
                if (view.id == R.id.dialogBtn2) {
                    AppCache.USER_INFO = UserTemplate()
                    if (intent != null)
                        startActivity(intent)
                    finish()
                }
            }
        }
        return super.onOptionsItemSelected(menuItem)
    }
}