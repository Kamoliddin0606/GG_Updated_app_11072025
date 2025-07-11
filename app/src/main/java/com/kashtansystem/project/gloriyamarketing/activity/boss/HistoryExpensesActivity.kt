package com.kashtansystem.project.gloriyamarketing.activity.boss

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.PagerAdapter
import android.view.View
import com.kashtansystem.project.gloriyamarketing.R
import com.kashtansystem.project.gloriyamarketing.core.BaseKActivity
import com.kashtansystem.project.gloriyamarketing.core.FilterDateType
import com.kashtansystem.project.gloriyamarketing.utils.AppCache
import com.kashtansystem.project.gloriyamarketing.utils.formatted
import com.kashtansystem.project.gloriyamarketing.utils.toBeginDay
import com.kashtansystem.project.gloriyamarketing.utils.toEndDay
import kotlinx.android.synthetic.main.activity_history_expenses.*
import java.util.*
import android.view.Menu
import android.support.v7.widget.SearchView
import android.widget.*

class HistoryExpensesActivity: BaseKActivity(){


    var DATE_FROM: Long = -1
    var DATE_TO: Long = -1
    var searchedText: String = ""

    override fun getLayoutResource(): Int = R.layout.activity_history_expenses

    override fun init(bundle: Bundle?) {
        val adapter = SimpleFragmentPagerAdapter(this, supportFragmentManager)
        vpAll.adapter = adapter
        vpAll.offscreenPageLimit = 5
        tlProjectName.setupWithViewPager(vpAll)


        llFilter.visibility = View.VISIBLE


        spDateType.adapter = ArrayAdapter<String>(this, R.layout.spinner_item_tv, FilterDateType.getWithoutAllTime(this))

        spDateType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val calendar = Calendar.getInstance() as Calendar
                (findViewById<View>(R.id.llPickInterval) as LinearLayout).visibility = View.GONE
                when (position) {
                    0 -> {
                        calendar.toBeginDay()
                        DATE_FROM = calendar.timeInMillis
                        calendar.toEndDay()
                        DATE_TO = calendar.timeInMillis
                    }
                    1 -> {
                        calendar.add(Calendar.DAY_OF_YEAR, -1)
                        calendar.toBeginDay()
                        DATE_FROM = calendar.timeInMillis
                        calendar.toEndDay()
                        DATE_TO = calendar.timeInMillis
                    }
                    2 -> {
                        calendar.add(Calendar.WEEK_OF_YEAR, -1)
                        calendar.toBeginDay()
                        DATE_FROM = calendar.timeInMillis
                        calendar.add(Calendar.WEEK_OF_YEAR, 1)
                        calendar.toEndDay()
                        DATE_TO = calendar.timeInMillis
                    }
                    3 -> {
                        calendar.add(Calendar.MONTH, -1)
                        calendar.toBeginDay()
                        DATE_FROM = calendar.timeInMillis
                        calendar.add(Calendar.MONTH, 1)
                        calendar.toEndDay()
                        DATE_TO = calendar.timeInMillis
                    }
                    4 -> {
                        (findViewById<View>(R.id.llPickInterval) as LinearLayout).visibility = View.VISIBLE
                        calendar.toBeginDay()
                        DATE_FROM = calendar.timeInMillis
                        calendar.toEndDay()
                        DATE_TO = calendar.timeInMillis
                        (findViewById<View>(R.id.etFromDate) as EditText).setText(calendar.time.formatted())
                        (findViewById<View>(R.id.etToDate) as EditText).setText(calendar.time.formatted())
                    }
                }
                updateFragments()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        spDateType.setSelection(3)
        etFromDate.setOnClickListener{
            val calendar = Calendar.getInstance()
            val date = Date()
            if (DATE_FROM != -1L)
                date.time = DATE_FROM
            calendar.time = date
            val datePickerDialog = DatePickerDialog(this@HistoryExpensesActivity, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                calendar.toBeginDay()
                DATE_FROM = calendar.timeInMillis
                etFromDate.setText(calendar.time.formatted())
                updateFragments()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_YEAR))
            datePickerDialog.show()
        }
        etToDate.setOnClickListener{
            val calendar = Calendar.getInstance()
            val date = Date()
            if (DATE_TO != -1L)
                date.time = DATE_TO
            calendar.time = date
            val datePickerDialog = DatePickerDialog(this@HistoryExpensesActivity, DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                calendar.toEndDay()
                DATE_TO = calendar.timeInMillis
                etToDate.setText(calendar.time.formatted())
                updateFragments()
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_YEAR))
            datePickerDialog.show()
        }

    }
    var searchView:SearchView? = null
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.expenses_history_search_menu, menu)

        val myActionMenuItem = menu.findItem(R.id.action_search)
        searchView = myActionMenuItem.getActionView() as SearchView
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                searchView?.let {
                    if (!it.isIconified) {
                        it.isIconified = true
                    }
                }
                myActionMenuItem.collapseActionView()
                return false
            }

            override fun onQueryTextChange(s: String): Boolean {
                searchedText = s
                searchedTyped()
                return false
            }
        })
        return true
    }

    fun updateFragments(){
        supportFragmentManager.fragments.forEach {
            if(it is HistoryExpensesListFragment){
                it.updateList(DATE_FROM,DATE_TO)
            }
        }
    }
    fun searchedTyped(){
        supportFragmentManager.fragments.forEach {
            if(it is HistoryExpensesListFragment){
                it.searchTyped(searchedText)
            }
        }
    }


    override fun initialSupportActionBar(): Boolean = true

    override fun getActionBarTitle(): String = "История РКО"

    override fun getHomeButtonEnable(): Boolean = true


    inner class SimpleFragmentPagerAdapter(private val mContext: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            var fragment = HistoryExpensesListFragment()
            val bundle = Bundle()
            bundle.putInt(HistoryExpensesListFragment.PROJECT_INDEX, position)
            fragment.arguments = bundle
            return fragment
        }

        override fun getItemPosition(obj: Any): Int {
            return PagerAdapter.POSITION_NONE
        }

        override fun getCount(): Int = AppCache.USERS_DETAIL.size

        override fun getPageTitle(position: Int): CharSequence? {
            return AppCache.USERS_DETAIL.get(position).soapProject.projectName
        }

    }

}