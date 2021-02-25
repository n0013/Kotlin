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
import kotlinx.android.synthetic.main.fragment_catalog.*
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
import ru.isantur.santurshop.fragment.catalog.catalog.Companion.CatalogId
import ru.isantur.santurshop.fragment.catalog.catalog.Companion.CatalogName
import ru.isantur.santurshop.fragment.catalog.catalog.Companion.arr_catalog
import ru.isantur.santurshop.varApp
import javax.inject.Inject


@StateStrategyType(value = OneExecutionStateStrategy::class)
interface Catalog_ViewState: MvpView {

    fun onSuccess()
    fun noInternet ()
    fun isEmpty()

}


class catalog:  MvpAppCompatFragment(), Catalog_ViewState {

    @InjectPresenter lateinit var catalog_presenter: catalog_Presenter

    companion object {

        fun instance (): Fragment {
            return catalog()
        }

        lateinit var v: View
        lateinit var arr_catalog: ArrayList<Catalogs>;

        var CatalogName: String = ""
        var CatalogId: Int = 0


    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        v = inflater.inflate(R.layout.fragment_catalog, container, false)

        return v

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        varApp.nav_view.menu.getItem(1).setChecked(true)
        varApp.nav_view.visibility = View.VISIBLE

        onLoad()

        //TODO update click:
        status_update.setOnClickListener {
            onLoad()
        }


    }



    fun onLoad() {
        catalog_pb.visibility = View.VISIBLE
        catalog_appbar.visibility = View.GONE
        catalog_status.visibility = View.GONE
        varApp.nav_view.visibility = View.GONE
//        catalog_presenter.onLoad()
        catalog_presenter.onLoad()
    }

    override fun onSuccess() {


        catalog_pb.visibility = View.GONE
        catalog_status.visibility = View.GONE

        catalog_appbar.visibility = View.VISIBLE

        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as AppCompatActivity).supportActionBar!!,
            customAppBar = catalog_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = false,
            some_appbar_btn_search = false,
            some_title_text = "Каталог товаров",
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false

        )

        varApp.nav_view.visibility = View.VISIBLE

        catalog_rv.setHasFixedSize(true)
        catalog_rv.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        catalog_rv.adapter = AdapterCatalog()
        catalog_rv.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))


    }

    override fun noInternet() {

        catalog_pb.visibility = View.GONE

        catalog_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as Main).supportActionBar!!,
            customAppBar = catalog_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = false,
            some_title_text = "No Internet",
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false)

        catalog_status.visibility = View.VISIBLE
        varApp.nav_view.visibility = View.GONE

    }

    override fun isEmpty() {

        catalog_pb.visibility = View.GONE
        catalog_rv.visibility = View.GONE

        catalog_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as Main).supportActionBar!!,
            customAppBar = catalog_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = false,
            some_title_text = "Каталоги отсутствуют",
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false)


        varApp.nav_view.visibility = View.VISIBLE

        catalog_status.visibility = View.VISIBLE
        status_title.text = "Каталоги отсутствуют"
        status_body.text = "К сожалению каталоги отсутствуют."
        status_update.visibility = View.GONE



    }


} //class




//region Adapter

class AdapterCatalog : RecyclerView.Adapter<AdapterCatalogVH>() {

    override fun getItemCount(): Int { return arr_catalog.size }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterCatalogVH {
        return  AdapterCatalogVH(LayoutInflater.from(parent.context).inflate(R.layout.element_item_rv, parent, false))
    }

    override fun onBindViewHolder(holder: AdapterCatalogVH, position: Int) {

        val v = holder.itemView


        v.name_rv.text = arr_catalog[position].name

        v.switch_rv.visibility = View.GONE

        v.img_rv.visibility = View.GONE
        v.img_right_rv.visibility = View.GONE



    }

}

class AdapterCatalogVH(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

    //навешиваем события
    init {
        v.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        CatalogName = arr_catalog[adapterPosition].name
        CatalogId = arr_catalog[adapterPosition].id

        varApp.nav_view.menu.getItem(1).setChecked(true)
        varApp.nav_view.visibility = View.GONE


        val catalog_sub: Fragment = catalog_sub.instance(
            bundleOf(
                "name" to arr_catalog[adapterPosition].name,
                "id" to arr_catalog[adapterPosition].id,
                "from" to "catalog"
            )
        )

        varApp.supportFragmentManager.beginTransaction()
            .hide(v!!.findFragment())
            .add(R.id.nav_host_fragment, catalog_sub, "catalog_sub")
            .show(catalog_sub)
            .addToBackStack("catalog_sub")
            .commit()


//        varApp.replaceFragment(varApp.supportFragmentManager, catalog_sub.instance(
//            bundleOf(
//                "name" to arr_catalog[adapterPosition].name,
//                "id" to arr_catalog[adapterPosition].id,
//                "from" to "catalog"
//            )
//        ), addAnimation = false, addToBackStack = true)

    }

}

//endregion




//region PRESENTER

@InjectViewState
class catalog_Presenter : MvpPresenter<Catalog_ViewState>() {

    private val disposeBag = CompositeDisposable()

    @Inject lateinit var backendModule: BackendModule

    fun back_onLoad () {
        varApp.appComponent.inject(this)

        disposeBag.add(
            backendModule.provideMyGet().getCatalog(varApp.SID)
                .delay(varApp.delay_default, varApp.typeUnit)
                .map { m -> m.filter { f -> f.parent_id == 0 } }
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    if (it.size != 0) {
                        arr_catalog = it as ArrayList<Catalogs>
                        viewState.onSuccess()
                    } else {
                        viewState.isEmpty()
                    }

                }, {
                    viewState.noInternet()
                })
        )

    }

    fun onLoad () {
        varApp.appComponent.inject(this)

        disposeBag.add (
            backendModule.provideMyGet().getCatalog(varApp.SID)
                .map { m -> m.filter { ff -> ff.parent_id == 0 } }
                .delay(varApp.delay_default, varApp.typeUnit)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    if (it.size != 0) {
                        arr_catalog = it as ArrayList<Catalogs>
                        viewState.onSuccess()
                    } else {
                        viewState.isEmpty()
                    }
                }, {
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