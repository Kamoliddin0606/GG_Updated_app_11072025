package com.kashtansystem.project.gloriyamarketing.activity.boss

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.kashtansystem.project.gloriyamarketing.R
import com.kashtansystem.project.gloriyamarketing.activity.boss.adapter.ExpenseListAdapter
import com.kashtansystem.project.gloriyamarketing.activity.main.BaseActivity
import com.kashtansystem.project.gloriyamarketing.core.MultiProjectActivity.Companion.PROJECT_INDEX
import com.kashtansystem.project.gloriyamarketing.models.template.ExpenseTemplate
import com.kashtansystem.project.gloriyamarketing.models.template.UserDetial
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqGetExpenseListForUser
import com.kashtansystem.project.gloriyamarketing.net.soap.ReqSetExpenseApprovedForUser
import com.kashtansystem.project.gloriyamarketing.utils.AppCache
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_expenses.*

class ExpensesListFragment : Fragment() {

    lateinit var adapterExpenses: ExpenseListAdapter
    var list: ArrayList<ExpenseTemplate> = ArrayList()
    lateinit var userDetial: UserDetial
    var disposable: Disposable? = null
    var aproveDisposable: Disposable? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        return inflater.inflate(R.layout.fragment_expenses, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(AppCache.USERS_DETAIL.size == 0) {
            activity?.finish()
            return
        }
        val indexProject = (arguments?.get(PROJECT_INDEX) ?: throw Throwable("Not have Index?")) as Int
        userDetial = AppCache.USERS_DETAIL[indexProject]
        adapterExpenses = ExpenseListAdapter(object : ExpenseListAdapter.Listener {
            override fun onApprove(expense: ExpenseTemplate) {
                approve(expense)
            }
        })

        rvExpenseList.apply {
            layoutManager = LinearLayoutManager(rvExpenseList.context)
            adapter = adapterExpenses
            hasFixedSize()
        }
        srlContainer.setOnRefreshListener {
            getUpdateList()
        }
        srlContainer.isRefreshing = true
        getUpdateList()

    }

    fun getUpdateList(){
        disposable = Single
                .create<ArrayList<ExpenseTemplate>> {
                    it.onSuccess(ReqGetExpenseListForUser.load(userDetial))
                }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ remoteList ->
                    list = remoteList
                    adapterExpenses.setItems(list)
                    srlContainer.isRefreshing = false
                }, {
                    it.printStackTrace()
                })
    }

    private var dialog: Dialog? = null
    fun approve(expenseTemplate: ExpenseTemplate){
        aproveDisposable = Single
                .create<Array<String>> {
                    it.onSuccess(ReqSetExpenseApprovedForUser.send(expenseTemplate.code,userDetial))
                }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe{
                    dialog = BaseActivity.getInformDialog(context, getString(R.string.dialog_text_sending_data))
                    dialog?.show()
                }
                .doFinally{
                    dialog?.cancel()
                    dialog = null
                }
                .subscribe({ result ->
                    if (result[0] == "1"){
                        list.remove(expenseTemplate)
                        adapterExpenses.notifyDataSetChanged()
                    }
                    Toast.makeText(context, result[1], Toast.LENGTH_LONG).show()
                }, {
                    it.printStackTrace()
                })
    }



    override fun onDetach() {
        disposable?.dispose()
        aproveDisposable?.dispose()
        super.onDetach()
    }
}