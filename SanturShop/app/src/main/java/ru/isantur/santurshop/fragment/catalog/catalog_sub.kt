package ru.isantur.santurshop.fragment.catalog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.element_item_rv.view.*
import kotlinx.android.synthetic.main.fragment_catalog_sub.*
import kotlinx.android.synthetic.main.include_appbar.*
import kotlinx.android.synthetic.main.include_status.*
import moxy.InjectViewState
import moxy.MvpAppCompatFragment
import moxy.MvpPresenter
import moxy.MvpView
import moxy.presenter.InjectPresenter
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.isantur.santurshop.Data.Catalogs
import ru.isantur.santurshop.R
import ru.isantur.santurshop.activity.Main
import ru.isantur.santurshop.di.BackendModule
import ru.isantur.santurshop.fragment.catalog.catalog_sub.Companion.arr_catalog_sub
import ru.isantur.santurshop.fragment.products.product_list
import ru.isantur.santurshop.varApp
import javax.inject.Inject


@StateStrategyType(value = OneExecutionStateStrategy::class)
interface CatalogSub_ViewState: MvpView {

    fun onSuccess()
    fun onError ()
    fun isEmpty ()


}




class catalog_sub: MvpAppCompatFragment(), CatalogSub_ViewState {

    @InjectPresenter    lateinit var catalog_sub_presenter: catalog_sub_Presenter


    companion object {

        fun instance(args: Bundle? = null): Fragment {
            val catalog_sub = catalog_sub()
            catalog_sub.arguments = args
            return catalog_sub
        }

        lateinit var v: View

        lateinit var arr_catalog_sub: ArrayList<Catalogs>;


    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)


        v = inflater.inflate(R.layout.fragment_catalog_sub, container, false)

        return v

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onLoad()

        //TODO update click
        status_update.setOnClickListener {
            onLoad()
        }

        //TODO back click:
        some_addbar_view_back.setOnClickListener {
            varApp.delete_step_from_BackStack(varApp.supportFragmentManager, step = this::class.java.simpleName)
        }


    }



    fun onLoad () {
        catalog_sub_pb.visibility = View.VISIBLE
        catalog_sub_appbar.visibility = View.GONE
        catalog_sub_status.visibility = View.GONE
        varApp.nav_view.visibility = View.GONE

        catalog_sub_presenter.onLoad(arguments)
    }

    override fun onSuccess() {

        catalog_sub_pb.visibility = View.GONE
        catalog_sub_status.visibility = View.GONE

        catalog_sub_appbar.visibility = View.VISIBLE

        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as AppCompatActivity).supportActionBar!!,
            customAppBar = catalog_sub_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = true,
            some_appbar_btn_search = false,
            some_title_text = "Каталог товаров",
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false

        )
        varApp.nav_view.visibility = View.VISIBLE

        catalog_sub_rv.setHasFixedSize(true)
        catalog_sub_rv.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        catalog_sub_rv.adapter = AdapterCatalogSub()
        catalog_sub_rv.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))




    }

    override fun onError() {

        catalog_sub_pb.visibility = View.GONE
        catalog_sub_rv.visibility = View.GONE

        catalog_sub_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as Main).supportActionBar!!,
            customAppBar = catalog_sub_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = false,
            some_title_text = varApp.heading_no_internet,
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false)

        catalog_sub_status.visibility = View.VISIBLE
        varApp.nav_view.visibility = View.GONE
    }

    override fun isEmpty() {

        catalog_sub_pb.visibility = View.GONE
        catalog_sub_rv.visibility = View.GONE

        catalog_sub_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as Main).supportActionBar!!,
            customAppBar = catalog_sub_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = false,
            some_title_text = "Каталоги отсутствуют",
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false)


        varApp.nav_view.visibility = View.VISIBLE

        catalog_sub_status.visibility = View.VISIBLE
        status_title.text = "Каталоги отсутствуют"
        status_body.text = "К сожалению в данной категории каталоги отсутствуют."
        status_update.visibility = View.GONE

    }


} //Class Catalog



//region Adapter

class AdapterCatalogSub : RecyclerView.Adapter<AdapterCatalogSubVH>() {

    override fun getItemCount(): Int { return arr_catalog_sub.size }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterCatalogSubVH {
        return  AdapterCatalogSubVH(LayoutInflater.from(parent.context).inflate(R.layout.element_item_rv, parent, false))
    }


    override fun onBindViewHolder(holder: AdapterCatalogSubVH, position: Int) {

        val v = holder.itemView


        v.name_rv.text = arr_catalog_sub[position].name

        v.switch_rv.visibility = View.GONE
        v.img_rv.visibility = View.GONE
        v.img_right_rv.visibility = View.GONE



    }

}

class AdapterCatalogSubVH(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {


    init {

        v.setOnClickListener(this)

    }


    override fun onClick(v: View?) {

        product_list.TITLE = arr_catalog_sub[adapterPosition].name


        val product_list: Fragment = product_list.instance(
            bundleOf(
                "id" to arr_catalog_sub[adapterPosition].id,
                "from" to "catalog"
            )
        )
        varApp.supportFragmentManager.beginTransaction()
            .hide(v!!.findFragment())
            .add(R.id.nav_host_fragment, product_list, "product_list")
            .show(product_list)
            .addToBackStack("product_list")
            .commit()


    }


}

//endregion





//region PRESENTER

@InjectViewState
class catalog_sub_Presenter : MvpPresenter<CatalogSub_ViewState>() {

    private val disposeBag = CompositeDisposable()

    @Inject lateinit var backendModule: BackendModule


    fun onLoad (arguments: Bundle?) {
        varApp.appComponent.inject(this)

        disposeBag.add (
            backendModule.provideMyGet().getCatalog(varApp.SID)
                .map { m -> m.filter { ff -> ff.parent_id == arguments?.getInt("id") } }
                .delay(varApp.delay_default, varApp.typeUnit)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    if (it.size != 0) {
                        arr_catalog_sub = it as ArrayList<Catalogs>
                        viewState.onSuccess()
                    } else {
                        viewState.isEmpty()
                    }
                }, {
                    viewState.onError()
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
