package ru.isantur.santurshop.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleExpandableListAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import com.jakewharton.rxbinding2.widget.RxPopupMenu
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.include_status.*
import kotlinx.android.synthetic.main.search_main.*
import moxy.InjectViewState
import moxy.MvpAppCompatFragment
import moxy.MvpPresenter
import moxy.MvpView
import moxy.presenter.InjectPresenter
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.isantur.santurshop.Data.Search
import ru.isantur.santurshop.Data.tntks
import ru.isantur.santurshop.R
import ru.isantur.santurshop.Utils
import ru.isantur.santurshop.activity.Main
import ru.isantur.santurshop.di.BackendModule
import ru.isantur.santurshop.fragment.products.product
import ru.isantur.santurshop.fragment.products.product_list
import ru.isantur.santurshop.varApp
import java.util.concurrent.TimeUnit
import javax.inject.Inject


@StateStrategyType(value = OneExecutionStateStrategy::class)
interface Search_ViewState: MvpView {

    fun onSuccess()
    fun noInternet ()
    fun success_quickSearch(resp: tntks)

}


class search : MvpAppCompatFragment(), Search_ViewState {

    @InjectPresenter
    lateinit var search_presenter: search_Presenter

    companion object {

        fun instance(): MvpAppCompatFragment {
            return search()
        }

        lateinit var v: View
        var obSearch: ArrayList<Search> = ArrayList()

        lateinit var presenter: search_Presenter
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        presenter = search_presenter

        v = inflater.inflate(R.layout.search_main, container, false)
        return v

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        onLoad(view)

        //TODO click back:
        search_addbar_view_back.setOnClickListener {
            Utils.hideKeyboard(it.context, it)
            varApp.clearBackStack(varApp.supportFragmentManager)
        }

        //TODO click clean:
        search_clean.setOnClickListener {
            search.text.clear()
        }

        //TODO update page:
        status_update.setOnClickListener {
            onLoad(view)
        }

    }


    override fun onStart() {
        super.onStart()

        (activity as AppCompatActivity).supportActionBar!!.hide()

        varApp.nav_view.visibility = View.GONE

    }

    override fun onPause() {
        super.onPause()

        varApp.nav_view.visibility = View.VISIBLE

    }

    override fun onDestroy() {
        super.onDestroy()

        varApp.nav_view.visibility = View.VISIBLE

    }



    @SuppressLint("CheckResult")
    fun onLoad (view: View) {
        search_pb.visibility = View.GONE
        search_status.visibility = View.GONE
        search_appbar.visibility = View.GONE
        search_page.visibility = View.VISIBLE

        search.requestFocus()
        Utils.showKeyboard(view.context.applicationContext)


        RxTextView.textChanges(search)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {

                if (search_elv != null && it.isNotEmpty() && it != "" && it != " ") { //чтобы ошибки не было
                    search_elv.visibility = View.GONE
                    search_pb.visibility = View.VISIBLE
                }
            }


        RxTextView.afterTextChangeEvents(search)
            .debounce(1000, TimeUnit.MILLISECONDS)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {

                println(it.view().text)

                if (search_elv != null) {
                    search_elv.visibility = View.GONE
                    search_pb.visibility = View.VISIBLE

                    if (it.view().text.isNotEmpty() && it.view().text != "" && it.view().text != " ") {
                        search_presenter.quickSearch("${it.view().text}")

                    } else {
                        search_elv.visibility = View.GONE
                    }

                    search_pb.visibility = View.GONE
                }


            }


    }

    override fun onSuccess() {









    }

    override fun noInternet() {

        search_pb.visibility = View.GONE
        search_page.visibility = View.GONE
        Utils.hideKeyboard(search.context, search)

        search_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as Main).supportActionBar!!,
            customAppBar = search_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = false,
            some_title_text = varApp.heading_no_internet,
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false)

        search_status.visibility = View.VISIBLE

    }

    override fun success_quickSearch(resp: tntks) {


        if (resp.tntks.size != 0) {

            search_elv.visibility = View.VISIBLE

            //hide keyboard:
            Utils.hideKeyboard(search.context, search)

            search_elv.requestFocus()

            //Adapter:
            search_elv.setAdapter(AdapterSearch().init(v.context, resp.tntks))

            //TODO click sub
            search_elv.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->

                product_list.TITLE = resp.tntks[id.toInt()].name

                //                search_constr.visibility = View.GONE
                varApp.nav_view.visibility = View.GONE


                val product_list: Fragment = product_list.instance(
                    bundleOf(
                        "kt" to resp.tntks[id.toInt()].kt,
                        "id" to resp.tntks[id.toInt()].id.toString(),
                        "from" to "search")
                )

                varApp.supportFragmentManager.beginTransaction()
                    //.setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_up)
                    .hide(v.findFragment())
                    .add(R.id.nav_host_fragment, product_list, "product_list")
                    .show(product_list)
                    .addToBackStack("product_list")
                    .commit()

//                varApp.replaceFragment(
//                    varApp.supportFragmentManager, product_list.instance(
//                        bundleOf(
//                            "kt" to resp.tntks[id.toInt()].kt,
//                            "id" to resp.tntks[id.toInt()].id.toString(),
//                            "from" to "search")
//                    ), addAnimation = false, addToBackStack = true)


                false
            }   //elvSearch.setOnChildClickListener

            //Расскрываем группы:
            search_elv.expandGroup(0)
            val ela = search_elv.expandableListAdapter
            for (i in 0 until ela.groupCount) {
                search_elv.expandGroup(i)
            }
        }


    }

}


//region Adapter

class AdapterSearch {


    fun init(context: Context, obSearch: ArrayList<Search>): SimpleExpandableListAdapter {


        val group: ArrayList<Map<String, String>> = ArrayList()
        var groupItem: ArrayList<ArrayList<Map<String, String>>> = ArrayList()


        //Для подгрупп:
        //xtn - товарное направление
        //xtk - товарная категория

        val ar_xtn: ArrayList<Map<String, String>> = ArrayList()
        val ar_xtk: ArrayList<Map<String, String>> = ArrayList()

        //Для подгрупп цикл:
        for (el in obSearch) {
            if (el.kt == "xtn") {   //xtn
                ar_xtn.add(mapOf("groupItemName" to el.name, "groupItemId" to el.id.toString()))
            } else {    //xtk
                ar_xtk.add(mapOf("groupItemName" to el.name, "groupItemId" to el.id.toString()))
            }

        } //for


        //Для групп 2 группы:
        if (ar_xtn.size != 0) {
            group.add(mapOf("group" to "Направление"))
            groupItem.addAll(listOf(ar_xtn))

        }
        if (ar_xtk.size != 0) {
            group.add(mapOf("group" to "Категория"))
            groupItem.addAll(listOf(ar_xtk))
        }


        // список атрибутов групп для чтения
        val groupFrom = arrayOf("group")

        // список ID view-элементов, в которые будет помещены атрибуты групп
        val groupTo = intArrayOf(R.id.search_group_name_rv)


        // список атрибутов элементов для чтения
        val childFrom = arrayOf("groupItemName", "groupItemId")

        // список ID view-элементов, в которые будет помещены атрибуты элементов
        val childTo = intArrayOf(R.id.search_item_name_rv, R.id.search_item_name2_rv)



        return SimpleExpandableListAdapter(
            context, group, R.layout.element_expandable_group_search, groupFrom, groupTo, groupItem, R.layout.element_expandable_item_search, childFrom, childTo)

    }


}

//endregion


//region PRESENTER

@InjectViewState
class search_Presenter : MvpPresenter<Search_ViewState>() {

    private val disposeBag = CompositeDisposable()

    @Inject
    lateinit var backendModule: BackendModule


    fun quickSearch(search: String) {
        varApp.appComponent.inject(this)

        disposeBag.add(backendModule.provideMyGet().quickSearch(search, varApp.SID)
            .delay(varApp.delay_default, varApp.typeUnit)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                viewState.success_quickSearch(it)
            }, {
                viewState.noInternet()
            }))
    }

    override fun onDestroy() {
        super.onDestroy()
        disposeBag.dispose()
        disposeBag.clear()
    }

}



//endregion





