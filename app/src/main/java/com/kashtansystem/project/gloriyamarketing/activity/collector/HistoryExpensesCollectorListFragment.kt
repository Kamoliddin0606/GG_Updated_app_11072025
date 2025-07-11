package com.kashtansystem.project.gloriyamarketing.activity.collector

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kashtansystem.project.gloriyamarketing.R
import com.kashtansystem.project.gloriyamarketing.activity.boss.HistoryExpensesListFragment
import com.kashtansystem.project.gloriyamarketing.activity.boss.adapter.HistoryExpenseListAdapter
import com.kashtansystem.project.gloriyamarketing.models.template.ExpenseTemplate
import com.kashtansystem.project.gloriyamarketing.models.template.UserDetial
import com.kashtansystem.project.gloriyamarketing.net.soap.GetExpenseListForThePeriod
import com.kashtansystem.project.gloriyamarketing.utils.AppCache
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_history_expenses.*

class HistoryExpensesCollectorListFragment : Fragment() {

    lateinit var adapterExpenses: HistoryExpenseListAdapter
    lateinit var userDetial: UserDetial
    var disposable: Disposable? = null

    var filteredList: ArrayList<ExpenseTemplate> = ArrayList()
    var allList: ArrayList<ExpenseTemplate> = ArrayList()

    var DATE_FROM = -1L
    var DATE_TO = -1L


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_history_expenses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (AppCache.USERS_DETAIL.size == 0) {
            activity?.finish()
            return
        }
        val indexProject = (arguments?.get(HistoryExpensesListFragment.PROJECT_INDEX)
                ?: throw Throwable("Not have Index?")) as Int
        userDetial = AppCache.USERS_DETAIL[indexProject]
        adapterExpenses = HistoryExpenseListAdapter()

        rvExpenseList.apply {
            layoutManager = LinearLayoutManager(rvExpenseList.context)
            adapter = adapterExpenses
            hasFixedSize()
        }
        srlContainer.setOnRefreshListener {
            getUpdateList()
        }
        srlContainer.isRefreshing = true
    }

    fun getUpdateList() {
        disposable = Single
                .create<ArrayList<ExpenseTemplate>> {
                    it.onSuccess(GetExpenseListForThePeriod.load(userDetial, DATE_FROM, DATE_TO))
                }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ remoteList ->
                    //hardcore filter cashier
                    allList = remoteList
                    filter()
                    srlContainer.isRefreshing = false
                }, {
                    it.printStackTrace()
                })
    }



    fun updateList(fromDate: Long, toDate: Long) {
        DATE_FROM = Math.min(fromDate, toDate)
        DATE_TO = Math.max(fromDate, toDate)
        getUpdateList()
    }

    var searchedText: String = ""

    fun searchTyped(searchText: String){
        this.searchedText = searchText
        filter()
    }

    fun filter(){
        filteredList.clear()
        filteredList.addAll(ArrayList(allList.filter { it.info.toUpperCase().contains(searchedText.toUpperCase())}))
        adapterExpenses.setItems(filteredList, searchedText)

    }

    companion object {
        val PROJECT_INDEX = "PROJECT_INDEX"
    }

    override fun onDetach() {
        disposable?.dispose()
        super.onDetach()
    }
}