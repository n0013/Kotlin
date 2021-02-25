package ru.isantur.santurshop.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.checkbox.MaterialCheckBox
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.element_item_filters.view.*
import kotlinx.android.synthetic.main.screen_filters_child.*
import kotlinx.android.synthetic.main.screen_filters_child.view.*
import org.json.JSONArray
import org.json.JSONObject
import ru.isantur.santurshop.fragment.products.product_list
import ru.isantur.santurshop.R
import ru.isantur.santurshop.varApp
import kotlinx.android.synthetic.main.include_appbar.*
import kotlinx.android.synthetic.main.include_status.*
import moxy.InjectViewState
import moxy.MvpAppCompatFragment
import moxy.MvpPresenter
import moxy.MvpView
import moxy.presenter.InjectPresenter
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.isantur.santurshop.Retrofit.myGET
import ru.isantur.santurshop.activity.Main
import ru.isantur.santurshop.di.BackendModule
import ru.isantur.santurshop.fragment.filters_child.Companion.presenter
import ru.isantur.santurshop.fragment.filters_child.Companion.v
import ru.isantur.santurshop.fragment.products.product_list_Presenter
import javax.inject.Inject


@StateStrategyType(value = OneExecutionStateStrategy::class)
interface FiltersChild_ViewState: MvpView {

    fun onSuccess()
    fun noInternet ()

}

class filters_child:  MvpAppCompatFragment(), FiltersChild_ViewState {

    @InjectPresenter lateinit var filters_child_presenter: filters_child_Presenter


    companion object {

        fun instance(args: Bundle?): Fragment {
            val filters_child = filters_child()
            filters_child.arguments = args
            return filters_child
        }

        lateinit var v: View
        var position: Int = 0

        var bundle: Bundle? = null

        lateinit var filterName: String

        lateinit var presenter: filters_child_Presenter

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        v = inflater.inflate(R.layout.screen_filters_child, container, false)

        return v

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = filters_child_presenter

        onSuccess()
        onEvents()

    }

    fun onEvents() {
        //TODO show click:
        filter_child_btn_show.setOnClickListener {
            varApp.delete_step_from_BackStack(varApp.supportFragmentManager, this::class.java.simpleName)
        }

        //TODO Back click:
        some_addbar_view_back.setOnClickListener {
            varApp.delete_step_from_BackStack(varApp.supportFragmentManager, this::class.java.simpleName)
        }

        //TODO update click:
        status_update.setOnClickListener {
            onSuccess()
            onEvents()
        }
    }

    override fun onSuccess() {

        //if not internet, then show page no internet:
        if (! varApp.isNetwork( varApp.appcontext ) ) {
            noInternet()
            return
        }

        filter_child_pb.visibility = View.GONE
        filter_child_status.visibility = View.GONE

        //show base page:
        filter_child_page.visibility = View.VISIBLE

        //show base btn and hide progress btn:
        filter_child_btn_show.visibility = View.VISIBLE
        filter_child_btn_pb.visibility = View.INVISIBLE

        filter_child_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as AppCompatActivity).supportActionBar!!,
            customAppBar = filter_child_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = true,
            some_title_text = arguments?.getString("name").toString(),
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false
        )

        varApp.nav_view.visibility = View.VISIBLE

        val filter = product_list.products.GoodFilters.filter { f -> !f.IsNumeric }[arguments?.getInt("position")!!.toInt()]
        position = arguments?.getInt("position")!!.toInt()

        filterName = filter.Name

        filter_child_rv.setHasFixedSize(false)
        filter_child_rv.layoutManager = LinearLayoutManager( v.context, LinearLayoutManager.VERTICAL, false)
        filter_child_rv.adapter = AdapterFiltersChild_rv()
        filter_child_rv.addItemDecoration(DividerItemDecoration(v.context, DividerItemDecoration.VERTICAL))

    }

    override fun noInternet() {

        filter_child_pb.visibility = View.GONE
        filter_child_page.visibility = View.GONE

        filter_child_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as Main).supportActionBar!!,
            customAppBar = filter_child_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = false,
            some_title_text = varApp.heading_no_internet,
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false)


        varApp.nav_view.visibility = View.GONE
        filter_child_status.visibility = View.VISIBLE

    }




}




class AdapterFiltersChild_rv () : RecyclerView.Adapter<AdapterFiltersChildVH>() {


    override fun getItemCount(): Int {
        return product_list.products.GoodFilters.filter { f -> !f.IsNumeric && f.Name == filters_child.filterName }[0].Items.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterFiltersChildVH {
        return  AdapterFiltersChildVH(  LayoutInflater.from(parent.context).inflate(R.layout.element_item_filters, parent, false) )
    }


    override fun onBindViewHolder(vh: AdapterFiltersChildVH, position: Int) {

        vh.itemView.filter_name.text = product_list.products.GoodFilters.filter { f -> !f.IsNumeric && f.Name == filters_child.filterName }[0].Items[position].Name

        vh.itemView.filter_name.visibility = View.VISIBLE
        vh.itemView.filter_check.visibility = View.VISIBLE


//        if (filter.Items[position].Name == filter.Selected) {
        val lSelected = product_list.products.GoodFilters.filter { f -> !f.IsNumeric && f.Name == filters_child.filterName }[0].Selected.split(";".toRegex())

        if (lSelected.filter { f -> f == vh.itemView.filter_name.text }.size !=0) {
            //enable check:
            vh.itemView.filter_check.isChecked = true
            //show in button count filter:
            v.filter_child_btn_show.text = "Отфильтровать     ${product_list.products!!.qty_records}    тов."
        } else {
            vh.itemView.filter_check.isChecked = false
        }


        vh.itemView.filter_name2.text = product_list.products.GoodFilters.filter { f -> !f.IsNumeric && f.Name == filters_child.filterName }[0].Items[position].QtyRecords.toString()

    }

}

class AdapterFiltersChildVH(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {


    //навешиваем события
    init {
        v.filter_check.setOnClickListener( this )
        v.setOnClickListener( this )
    }



    override fun onClick(vv: View) {

        v.filter_child_btn_pb.visibility = View.VISIBLE
        v.filter_child_btn_show.visibility = View.INVISIBLE

        presenter.setfilter(vv)

    }



}






//region PRESENTER

@InjectViewState
class filters_child_Presenter : MvpPresenter<FiltersChild_ViewState>() {

    private val disposeBag = CompositeDisposable()

    @Inject lateinit var backendModule: BackendModule


    fun setfilter (vv: View) {

        varApp.appComponent.inject(this)


        var item = ""

        if (vv is MaterialCheckBox) { // нажали checkbox
            item = ((vv.parent as ViewGroup).get(1) as TextView).text.toString()
        } else {    //нажали constrantLayout (другое)
            item = (vv as ConstraintLayout).get(1).filter_name.text.toString()
        }



        disposeBag.add(
            backendModule.provideRetrofit(url = "${varApp.url}setfilter/?flt=${filters_child.filterName}&item=$item&sid=${varApp.SID}").create(myGET::class.java) .setOk()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    backendModule.provideMyGet().getGoodList("1", varApp.SID)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({

                            product_list.products = it

                            v.filter_child_btn_pb.visibility = View.INVISIBLE
                            v.filter_child_btn_show.visibility = View.VISIBLE

                            (v.filter_child_rv as RecyclerView).adapter!!.notifyDataSetChanged()
                            v.filter_child_btn_show.text = "Отфильтровать     ${product_list.products!!.qty_records}    тов."

                        }, {
                            viewState.noInternet()
                        })

                },{
                    viewState.noInternet()
                })


        )

    }

    override fun onDestroy() {
        super.onDestroy()
        disposeBag.dispose()
        disposeBag.clear()
    }


}

//endregion