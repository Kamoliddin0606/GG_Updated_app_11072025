package com.kashtansystem.project.gloriyamarketing.core

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.View
import com.kashtansystem.project.gloriyamarketing.R
import com.kashtansystem.project.gloriyamarketing.utils.AppCache
import kotlinx.android.synthetic.main.activity_vp_container.*

abstract class MultiProjectActivity : BaseKActivity() {

    companion object {
        val PROJECT_INDEX = "PROJECT_INDEX"
    }

    override fun init(bundle: Bundle?) {
        val adapter = SimpleFragmentPagerAdapter(this, supportFragmentManager)
        vpAll.adapter = adapter
        vpAll.offscreenPageLimit = 5
        tlProjectName.setupWithViewPager(vpAll)
        if(AppCache.USERS_DETAIL.size<2){
            tlProjectName.visibility = View.GONE
        }
    }

    override fun getLayoutResource(): Int = R.layout.activity_vp_container
    abstract fun getFragmentInstance(): Fragment

    inner class SimpleFragmentPagerAdapter(private val mContext: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            var fragment = getFragmentInstance()
            val bundle = Bundle()
            bundle.putInt(PROJECT_INDEX, position)
            fragment.arguments = bundle
            return fragment
        }

        override fun getCount(): Int = AppCache.USERS_DETAIL.size

        override fun getPageTitle(position: Int): CharSequence? {
            return AppCache.USERS_DETAIL.get(position).soapProject.projectName
        }

    }
}