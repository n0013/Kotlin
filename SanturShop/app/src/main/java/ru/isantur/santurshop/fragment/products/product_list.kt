package ru.isantur.santurshop.fragment.products

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.element_list.view.*
import kotlinx.android.synthetic.main.include_appbar.*
import kotlinx.android.synthetic.main.include_sort_filter.*
import kotlinx.android.synthetic.main.include_sort_filter.view.*
import kotlinx.android.synthetic.main.include_status.*
import kotlinx.android.synthetic.main.list_prod.*
import kotlinx.android.synthetic.main.list_prod.view.*
import moxy.InjectViewState
import moxy.MvpAppCompatFragment
import moxy.MvpPresenter
import moxy.MvpView
import moxy.presenter.InjectPresenter
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.isantur.santurshop.AppResponse
import ru.isantur.santurshop.Data.ListGoods
import ru.isantur.santurshop.Data.Products
import ru.isantur.santurshop.R
import ru.isantur.santurshop.Retrofit.myGET
import ru.isantur.santurshop.di.BackendModule
import ru.isantur.santurshop.fragment.filters
import ru.isantur.santurshop.fragment.products.product_list.Companion.loading
import ru.isantur.santurshop.fragment.products.product_list.Companion.presenter
import ru.isantur.santurshop.fragment.products.product_list.Companion.products
import ru.isantur.santurshop.fragment.products.product_list.Companion.v
import ru.isantur.santurshop.varApp
import javax.inject.Inject


@StateStrategyType(value = OneExecutionStateStrategy::class)
interface ProductList_ViewState: MvpView {

    fun onSuccess()
    fun noInternet ()
    fun isEmpty()


    fun success_getgoodlist_page(resp: Products)

    fun output_to_adapter ()


}







class product_list:  MvpAppCompatFragment(), ProductList_ViewState {

    @InjectPresenter lateinit var product_list_presenter: product_list_Presenter

    companion object {

        fun instance(): Fragment {
            return product_list()
        }

        fun instance(args: Bundle?): Fragment {
            val product_list = product_list()
            product_list.arguments = args
            return product_list
        }

        var id: Int = 0
        var from: String = ""

        fun instance(id: Int, from: String): Fragment {
            val product_list = product_list()
            this.id = id
            this.from = from
            return product_list
        }

        lateinit var v: View


        lateinit var products: Products

        var TITLE = ""

        lateinit var presenter: product_list_Presenter

        var loading = true

    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        v = inflater.inflate(R.layout.list_prod, container, false)

        return v

    }

    @SuppressLint("UseRequireInsteadOfGet")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = product_list_presenter


        loading = true
        product_list_presenter.backand_onLoad(view)



        Thread(Runnable {
            this@product_list.activity!!.runOnUiThread(java.lang.Runnable {
                analysis()
            })
        }).start()







        //TODO Sort click:
        sort_filter_sort_ll.setOnClickListener {
            val pop = PopupMenu(v.context, it)
            pop.inflate(R.menu.popup_sort)

            pop.setOnMenuItemClickListener { item ->

                when (item.itemId) {
                    R.id.sort_byName -> {
                        v.sort_filter_sort_btn.text = item.title
                        val ar = products.ListGoods.sortedWith(compareBy(ListGoods::name))
                        products.ListGoods.clear()
                        products.ListGoods.addAll(ar)
                    }

                    R.id.sortedByPrice -> {
                        v.sort_filter_sort_btn.text = item.title
                        val ar = products.ListGoods.sortedWith(compareBy(ListGoods::price))
                        products.ListGoods.clear()
                        products.ListGoods.addAll(ar)
                    }

                    R.id.sortedByDescendingPrice -> {
                        v.sort_filter_sort_btn.text = item.title
                        val ar = products.ListGoods.sortedWith(compareByDescending(ListGoods::price))
                        products.ListGoods.clear()
                        products.ListGoods.addAll(ar)
                    }
                } //when:

                product_list_rv.adapter!!.notifyDataSetChanged()

                true
            }
            pop.show()
        }


        //TODO Filter click:
        sort_filter_filter_ll.setOnClickListener {

            varApp.replaceFragment(varApp.supportFragmentManager, filters.instance(null), addAnimation = false, addToBackStack = true)

        }


        //TODO back click:
        some_addbar_view_back.setOnClickListener {

            varApp.delete_step_from_BackStack(varApp.supportFragmentManager, step = this::class.java.simpleName)

        }


        //TODO update click:
        status_update.setOnClickListener {
            loading = true
            load()
            analysis()
        }


    }





    fun load () {

        varApp.nav_view.menu.getItem(1).setChecked(true)
        varApp.nav_view.visibility = View.GONE
        product_list_appbar.visibility = View.GONE
        product_list_sort_and_filter.visibility = View.GONE
        product_list_rv.visibility = View.GONE
        product_list_status.visibility = View.GONE
        product_list_pb.visibility = View.VISIBLE

    }

    fun loadButtom() {
        varApp.nav_view.menu.getItem(1).setChecked(true)
        product_list_pb_bottom.visibility = View.VISIBLE
    }

    fun analysis () {


        if (arguments?.getString("from") == "brands" || arguments?.getString("from") == "banners") {

            product_list_presenter.byLink(arguments)

        } else if ( arguments?.getString("from") == "catalog" || arguments?.getString("from") == "populars" ) {

            product_list_presenter.by_id(arguments, "tk")


        } else if (arguments?.getString("from") == "filters") {
            onSuccess()
            output_to_adapter()


        } else if (arguments?.getString("from") == "search") {
            if (arguments?.getString("kt") == "xtn") {
                product_list_presenter.by_id(arguments, "tn")
            } else {
                product_list_presenter.by_id(arguments, "tk")
            }
        }

    }


    override fun onSuccess() {

        product_list_pb.visibility = View.GONE
        product_list_pb_bottom.visibility = View.GONE

        product_list_status.visibility = View.GONE

        product_list_rv.visibility = View.VISIBLE
        product_list_sort_and_filter.visibility = View.VISIBLE

        product_list_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as AppCompatActivity).supportActionBar!!,
            customAppBar = product_list_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = true,
            some_appbar_btn_search = false,
            some_title_text = varApp.firstUpperCase(TITLE)!!,
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false

        )
        varApp.nav_view.visibility = View.VISIBLE


    }

    override fun noInternet() {

        product_list_pb.visibility = View.GONE
        product_list_pb_bottom.visibility = View.GONE
        product_list_rv.visibility = View.GONE
        product_list_sort_and_filter.visibility = View.GONE

        product_list_appbar.visibility = View.VISIBLE

        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as AppCompatActivity).supportActionBar!!,
            customAppBar = product_list_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = false,
            some_title_text = varApp.heading_no_internet,
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false

        )

        varApp.nav_view.visibility = View.GONE

        product_list_status.visibility = View.VISIBLE


    }

    override fun isEmpty() {

        product_list_pb.visibility = View.GONE
        product_list_pb_bottom.visibility = View.GONE
        product_list_rv.visibility = View.GONE
        product_list_sort_and_filter.visibility = View.GONE

        product_list_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as AppCompatActivity).supportActionBar!!,
            customAppBar = product_list_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = true,
            some_appbar_btn_search = true,
            some_title_text = TITLE,
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false


        )

        varApp.nav_view.visibility = View.VISIBLE

        product_list_status.visibility = View.VISIBLE
        status_title.text = "Товары отсутствую"
        status_body.text = "К сожалению, товары в категории отсутствуют."
        status_update.visibility = View.GONE
    }



    override fun success_getgoodlist_page(resp: Products) {

        onSuccess()

        val initialSize = products.ListGoods.size

        products.ListGoods = (products.ListGoods + resp.ListGoods).toMutableList()

        //sort:
        when (sort_filter_sort_btn.text) {
            "Сортировка" -> {
                products.ListGoods = products.ListGoods.sortedWith(compareBy(ListGoods::name)).toMutableList()
            }
            "По наименованию" -> {
                products.ListGoods = products.ListGoods.sortedWith(compareBy(ListGoods::name)).toMutableList()
            }
            "Цена по возрастанию" -> {
                products.ListGoods = products.ListGoods.sortedWith(compareBy(ListGoods::price)).toMutableList()
            }
            "Цена по убыванию" -> {
                products.ListGoods = products.ListGoods.sortedWith(compareByDescending(ListGoods::price)).toMutableList()
            }

        }

        products.current_page = products.current_page + 1

        product_list_rv.post {
            product_list_rv.adapter!!.notifyItemRangeInserted(initialSize, products.ListGoods.size)
        }


    }




    override fun output_to_adapter() {

        product_list_rv.setHasFixedSize(true)
        val lm = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        product_list_rv.layoutManager = lm
        product_list_rv.adapter = AdapterProducts()
        product_list_rv.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))



        var pastVisiblesItems: Int
        var visibleItemCount: Int
        var totalItemCount: Int

        //scrolling (get backend):
        product_list_rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    visibleItemCount = lm.getChildCount()
                    totalItemCount = lm.getItemCount()
                    pastVisiblesItems = lm.findFirstVisibleItemPosition()
                    if (loading) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {

                            if (products.current_page != products.qty_pages) {

                                loadButtom()

                                product_list_presenter.getgoodlist_page(products.current_page + 1)

                                loading = false

                            }

                        }
                    }
                }
            }
        })

    }




}




//region Adapter

class AdapterProducts : RecyclerView.Adapter<AdapterProductsVH>() {

    override fun getItemCount(): Int { return products.ListGoods.size }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterProductsVH {
        return  AdapterProductsVH(LayoutInflater.from(parent.context).inflate(R.layout.element_list, parent, false))
    }


    override fun onBindViewHolder(holder: AdapterProductsVH, position: Int) {

        val v = holder.itemView


        //show\hide other view:
        v.list_constrCart.visibility = View.GONE
        v.list_more.visibility = View.GONE

        v.list_add_del_cart_active.visibility = View.VISIBLE
        v.list_favorite.visibility = View.VISIBLE


        v.list_name.text = products.ListGoods[position].name
        v.list_prices.text = varApp.RoundToInteger(products.ListGoods[position].prices, true).toString()
        v.list_ed.text = "за ${products.ListGoods[position].ed}"

        Glide.with(v.context).load(products.ListGoods[position].imgpath).into(v.list_img)


        //уже в избранном:
        if (products.ListGoods[position].isfavority) {
            v.list_favorite.setImageDrawable(v.resources.getDrawable(R.drawable.ic_fav_active))
            v.list_favorite.alpha = 1.0F
            v.list_favorite.tag = "В избранном"

        } else {
            //нет в избранном
            v.list_favorite.setImageDrawable(v.resources.getDrawable(R.drawable.ic_fav))
            v.list_favorite.alpha = 0.3F
            v.list_favorite.tag = "В избранное"

        }

        //уже в корзине:
        if (products.ListGoods[position].qty_incart.toInt() != 0) {
            v.list_add_del_cart_active.background = v.resources.getDrawable(R.drawable.btn_gray)
            v.list_add_del_cart_active.setTextColor(v.resources.getColor(R.color.primary))
            v.list_add_del_cart_active.text = "В корзине"

        } else {
            //нет в корзине:
            v.list_add_del_cart_active.background = v.resources.getDrawable(R.drawable.btn_active)
            v.list_add_del_cart_active.setTextColor(v.resources.getColor(R.color.white))
            v.list_add_del_cart_active.text = "В корзину"
        }




    }

}

class AdapterProductsVH(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

    //навешиваем события
    init {
        v.list_img.setOnClickListener(this)
        v.list_name.setOnClickListener ( this )
        v.list_favorite.setOnClickListener(this)
        v.list_add_del_cart_active.setOnClickListener(this)
    }


    override fun onClick(view: View?) {
        when (view!!.id) {

            R.id.list_favorite -> {

                //click add\delete favorite:
                if (view.list_favorite.tag == "В избранное") {
                    presenter.add_favorite(adapterPosition, view)
                } else {
                    presenter.remove_favorite(adapterPosition, view)
                }

            }  //TODO favorite (add; delete)

            R.id.list_add_del_cart_active -> {

                (view.parent as ViewGroup).list_add_del_cart_active.visibility = View.INVISIBLE
                (view.parent as ViewGroup).list_add_del_cart_deactive.visibility = View.VISIBLE

                if ((view as TextView).text == "В корзину") {
                    presenter.add_to_cart(adapterPosition = adapterPosition, view = view)
                } else {
                    presenter.delete_from_cart(adapterPosition = adapterPosition, view = view)
                }


            } //TODO cart (add; delete)

            R.id.list_img, R.id.list_name -> {

                val product: Fragment = product.instance(
                    bundleOf(
                        "code" to products.ListGoods[adapterPosition].code
                    )
                )

                varApp.supportFragmentManager.beginTransaction()
//                    .setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_up)
                    .hide(v.findFragment())
                    .add(R.id.nav_host_fragment, product, "product")
                    .show(product)
                    .addToBackStack("product")
                    .commit()


//                varApp.replaceFragment(varApp.supportFragmentManager,
//                    product.instance(
//                        bundleOf(
//                            "code" to products.ListGoods[adapterPosition].code
//                        )
//                    ), addAnimation = false, addToBackStack = true
//                )



            }


        } //when

    }


}

//endregion



//region PRESENTER

@InjectViewState
class product_list_Presenter : MvpPresenter<ProductList_ViewState>() {

    private val disposeBag = CompositeDisposable()

    @Inject lateinit var backendModule: BackendModule


//region query quickSearch
//    fun quickSearch(search: String) {
//        varApp.appComponent.inject(this)
//
//        disposeBag.add(backendModule.provideMyGet()
//            .quickSearch(search, varApp.SID)
//            .delay(varApp.delay_default, varApp.typeUnit)
//            .subscribeOn(Schedulers.computation())
//            .observeOn(AndroidSchedulers.mainThread()).subscribe({
//                getGoodList(1)
//            }, {
//                viewState.noInternet()
//            }))
//    }
//
//
//    fun getGoodList(page: Int) {
//        varApp.appComponent.inject(this)
//
//        disposeBag.add(backendModule.provideMyGet().getGoodList(page.toString(), varApp.SID).delay(varApp.delay_default, varApp.typeUnit).subscribeOn(Schedulers.computation())
//            .observeOn(AndroidSchedulers.mainThread()).subscribe({
//                products = it
//                if (products.ListGoods.size != 0) {
//                    viewState.onSuccess()
//                    viewState.output_to_adapter()
//                } else {
//                    viewState.isEmpty()
//                }
//
//
//                //                    viewState.success_getGoodList(it)
//            }, {
//                viewState.noInternet()
//            }))
//    }
//endregion


    fun getgoodlist_page(page: Int) {
        varApp.appComponent.inject(this)

        disposeBag.add(
            backendModule.provideMyGet().getGoodList(page.toString(), varApp.SID)
                .delay(varApp.delay_default, varApp.typeUnit)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    loading = true
                    viewState.success_getgoodlist_page(it)
            }, {
                viewState.noInternet()
            }))
    }

    fun add_to_cart (adapterPosition: Int, view: View) {
        varApp.appComponent.inject(this)

        disposeBag.add(
            backendModule.provideMyGet().addToCart(products.ListGoods[adapterPosition].code, "1", varApp.SID)
                .delay(varApp.delay_default, varApp.typeUnit)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({

                    if (it.contains(products.ListGoods[adapterPosition].code)) {

                        (view.parent as ViewGroup).list_add_del_cart_active.background = view.resources.getDrawable(R.drawable.btn_gray)
                        (view.parent as ViewGroup).list_add_del_cart_active.setTextColor(view.resources.getColor(R.color.primary))
                        (view.parent as ViewGroup).list_add_del_cart_active.text = "В корзине"
                        products.ListGoods[adapterPosition].qty_incart = 1.0F

                    }

                    (view.parent as ViewGroup).list_add_del_cart_active.visibility = View.VISIBLE
                    (view.parent as ViewGroup).list_add_del_cart_deactive.visibility = View.INVISIBLE

                    if (backendModule.getCartBadge() == AppResponse.noInternet) viewState.noInternet()

                }, {
                    viewState.noInternet()
                })
        )

    }

    fun delete_from_cart (adapterPosition: Int, view: View) {
        varApp.appComponent.inject(this)

        disposeBag.add(
            backendModule.provideMyGet().removeCartItem( products.ListGoods[adapterPosition].code, varApp.SID)
                .delay(varApp.delay_default, varApp.typeUnit)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({

                    if (it.contains("")) {

                        (view.parent as ViewGroup).list_add_del_cart_active.background = view.resources.getDrawable(R.drawable.btn_active)
                        (view.parent as ViewGroup).list_add_del_cart_active.setTextColor(view.resources.getColor(R.color.white))
                        (view.parent as ViewGroup).list_add_del_cart_active.text = "В корзину"
                        products.ListGoods[adapterPosition].qty_incart = 0.0F
                    }

                    (view.parent as ViewGroup).list_add_del_cart_active.visibility = View.VISIBLE
                    (view.parent as ViewGroup).list_add_del_cart_deactive.visibility = View.INVISIBLE

                    if (backendModule.getCartBadge() == AppResponse.noInternet) viewState.noInternet()

                }, {
                    viewState.noInternet()
                })
        )

    }

    fun add_favorite(adapterPosition: Int, v: View) {
        varApp.appComponent.inject(this)

        disposeBag.add(backendModule.provideMyGet().addFavorite(products.ListGoods[adapterPosition].code, varApp.SID)
            .delay(varApp.delay_default, varApp.typeUnit)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.contains("ok")) {
                    v.list_favorite.setImageDrawable(v.resources.getDrawable(R.drawable.ic_fav_active))
                    v.list_favorite.alpha = 1.0F
                    v.list_favorite.tag = "В избранном"
                    products.ListGoods[adapterPosition].isfavority = true

                    if (backendModule.getFavoriteBadge() == AppResponse.noInternet) viewState.noInternet()
                }
            }, {
                viewState.noInternet()
            }))

    }

    fun remove_favorite(adapterPosition: Int, v: View) {

        varApp.appComponent.inject(this)

        disposeBag.add(
            backendModule.provideMyGet().removeFavorite(products.ListGoods[adapterPosition].code, varApp.SID)
            .delay(varApp.delay_default, varApp.typeUnit)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.contains("ok")) {
                    v.list_favorite.setImageDrawable(v.resources.getDrawable(R.drawable.ic_fav))
                    v.list_favorite.alpha = 0.3F
                    v.list_favorite.tag = "В избранное"
                    products.ListGoods[adapterPosition].isfavority = false

                    if (backendModule.getFavoriteBadge() == AppResponse.noInternet) viewState.noInternet()
                }

            }, {
                viewState.noInternet()
            })
        )

    }


    fun backand_onLoad (view: View) {
        view.product_list_pb.visibility = View.VISIBLE
        varApp.nav_view.menu.getItem(1).setChecked(true)
        varApp.nav_view.visibility = View.GONE
        view.product_list_appbar.visibility = View.INVISIBLE
        view.product_list_sort_and_filter.visibility = View.INVISIBLE
        view.product_list_rv.visibility = View.INVISIBLE
        view.product_list_status.visibility = View.INVISIBLE

    }


    fun byLink(arguments: Bundle?) {
        varApp.appComponent.inject(this)


        /* Порядок:
        setGoodSearch c tn_id = 0
        clearFilter all
        setFilter (данные из brands)
        getGoodList
     */


        disposeBag.add(

            backendModule.provideRetrofit(url = "${varApp.url}setgoodsearch/?tn_id=0&sid=${varApp.SID}").create(myGET::class.java) .setOk()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    backendModule.provideRetrofit(url = "${varApp.url}clearfilter/?flt=all&sid=${varApp.SID}").create(myGET::class.java) .setOk()
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({

                            backendModule.provideRetrofit(url = "${varApp.url + arguments?.getString("link")}&sid=${varApp.SID}").create(myGET::class.java) .setOk()
                                .subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({

                                    backendModule.provideMyGet().getGoodList("1", varApp.SID)
                                        .subscribeOn(Schedulers.computation())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe({

                                            it.ListGoods.sortedWith(compareBy(ListGoods::name))
                                            loading = true
                                            result(it)
                                        }, {
                                            viewState.noInternet()
                                        })
                                },{
                                    viewState.noInternet()
                                })

                        },{
                            viewState.noInternet()
                        })

                },{
                    viewState.noInternet()
                })

        )


    }


    fun by_id(arguments: Bundle?, type: String) {
        varApp.appComponent.inject(this)

        var query = ""
        if (type == "tk") {
            query = "?tk_id=${arguments?.getInt("id")}"
        } else {
            query = "?tn_id=${arguments?.getInt("id")}"
        }


        disposeBag.add(

            backendModule.provideRetrofit(url = "${varApp.url}setgoodsearch/${query}&sid=${varApp.SID}").create(myGET::class.java) .setOk()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    backendModule.provideMyGet().getGoodList("1", varApp.SID)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            loading = true
                            result(it)
                        }, {
                            viewState.noInternet()
                        })
                },{
                    viewState.noInternet()
                })

        )



//        disposeBag.addAll(
////            backendModule.provideRetrofit(url = "${varApp.url}setgoodsearch/?tn_id=0&sid=${varApp.SID}").create(myGET::class.java) .setOk()
////                .subscribeOn(Schedulers.computation())
////                .observeOn(AndroidSchedulers.mainThread())
////                .subscribe({},{viewState.noInternet() }),
////            backendModule.provideRetrofit(url = "${varApp.url}clearfilter/?flt=all&sid=${varApp.SID}").create(myGET::class.java) .setOk()
////                .subscribeOn(Schedulers.computation())
////                .observeOn(AndroidSchedulers.mainThread())
////                .subscribe({},{viewState.noInternet() }),
//            backendModule.provideRetrofit(url = "${varApp.url}setgoodsearch/${query}&sid=${varApp.SID}").create(myGET::class.java) .setOk()
//                .subscribeOn(Schedulers.computation())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({},{viewState.noInternet() }),
//            backendModule.provideMyGet().getGoodList("1", varApp.SID)
//                .subscribeOn(Schedulers.computation())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({
//                    loading = true
//                    result(it)
//                }, {
//                    viewState.noInternet()
//                }),
//        )


    }


    fun result(it: Any) {

        products = it as Products
        if (products.ListGoods.size != 0) {
            viewState.onSuccess()
            viewState.output_to_adapter()
        } else {
            viewState.isEmpty()
        }

    }




    override fun onDestroy() {
        super.onDestroy()
        disposeBag.dispose()
        disposeBag.clear()
    }


}

//endregion
