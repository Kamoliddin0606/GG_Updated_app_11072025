package com.kashtansystem.project.gloriyamarketing.activity.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.kashtansystem.project.gloriyamarketing.R
import com.kashtansystem.project.gloriyamarketing.core.BaseKActivity

class AgentNewActivity: BaseKActivity(){

    override fun init(bundle: Bundle?) {

    }

    override fun getLayoutResource(): Int = R.layout.activity_agent

    override fun initialSupportActionBar(): Boolean = true


    override fun getActionBarTitle(): String = ""

    override fun getHomeButtonEnable(): Boolean = false

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.agent_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
//        when (menuItem.itemId) {
//            R.id.mSettings -> startActivity(Intent(this, SettingsActivity::class.java))
//            R.id.mLogout -> BaseActivity.DialogBox(this, getString(R.string.dialog_text_ask_to_logout),
//                    getString(R.string.dialog_btn_cancel), getString(R.string.dialog_btn_exit),
//                    Intent(this, LoginActivity::class.java), onExitListener)
//        }
        return super.onOptionsItemSelected(menuItem)
    }
}