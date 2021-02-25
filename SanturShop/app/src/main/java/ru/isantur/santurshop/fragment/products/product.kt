package ru.isantur.santurshop.fragment.products

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
import com.bumptech.glide.Glide
import com.google.gson.JsonObject
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.element_item_receipt_rv.view.*
import kotlinx.android.synthetic.main.element_item_rv.view.*
import kotlinx.android.synthetic.main.element_list.view.*
import kotlinx.android.synthetic.main.element_product.*
import kotlinx.android.synthetic.main.element_sales.view.*
import kotlinx.android.synthetic.main.fragment_cart.view.*
import kotlinx.android.synthetic.main.include_appbar.*
import kotlinx.android.synthetic.main.include_appbar.view.*
import kotlinx.android.synthetic.main.include_big_button.*
import kotlinx.android.synthetic.main.include_status.*
import moxy.InjectViewState
import moxy.MvpAppCompatFragment
import moxy.MvpPresenter
import moxy.MvpView
import moxy.presenter.InjectPresenter
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.isantur.santurshop.AppResponse
import ru.isantur.santurshop.Data.Company
import ru.isantur.santurshop.Data.ListGoods
import ru.isantur.santurshop.R
import ru.isantur.santurshop.activity.Main
import ru.isantur.santurshop.anim
import ru.isantur.santurshop.di.BackendModule
import ru.isantur.santurshop.fragment.cart
import ru.isantur.santurshop.fragment.favorite
import ru.isantur.santurshop.fragment.info.info_delivery
import ru.isantur.santurshop.fragment.map.map
import ru.isantur.santurshop.fragment.products.product.Companion.arr_img
import ru.isantur.santurshop.fragment.products.product.Companion.arr_name
import ru.isantur.santurshop.fragment.products.product.Companion.arr_name2
import ru.isantur.santurshop.fragment.products.product.Companion.arrayCompany
import ru.isantur.santurshop.fragment.products.product.Companion.chars
import ru.isantur.santurshop.fragment.products.product.Companion.product
import ru.isantur.santurshop.fragment.products.product.Companion.related

import ru.isantur.santurshop.varApp
import ru.isantur.santurshop.varApp.Companion.iam
import java.lang.Math.abs
import javax.inject.Inject


@StateStrategyType(value = OneExecutionStateStrategy::class)
interface Product_ViewState: MvpView {




    fun onSuccess()
    fun noInternet ()
    fun isEmpty()

}


class product:  MvpAppCompatFragment(), Product_ViewState {

    @InjectPresenter
    lateinit var product_presenter: product_Presenter

    companion object {

        fun instance (): MvpAppCompatFragment {
            return product()
        }

        fun instance(args: Bundle?): MvpAppCompatFragment {
            val product = product()
            product.arguments = args
            return product
        }

        lateinit var v: View
        lateinit var product: ListGoods
        lateinit var chars: ArrayList<JsonObject>
        lateinit var related: ArrayList<JsonObject>

        val arr_name: ArrayList<String> = ArrayList()
        val arr_name2: ArrayList<String> = ArrayList()
        val arr_img: ArrayList<Int> = ArrayList()
        var arrayCompany: ArrayList<Company> = ArrayList()

    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        v = inflater.inflate(R.layout.element_product, container, false)

        return v

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onLoad()

        //TODO back:
        some_addbar_view_back.setOnClickListener {
            varApp.delete_step_from_BackStack(varApp.supportFragmentManager, this::class.java.simpleName)
        }

        //TODO add favorite:
        some_appbar_view_search.setOnClickListener {
            if (product.isfavority) {
                product_presenter.remove_favorite(product.code, view)
            } else {
                product_presenter.add_favorite(product.code, view)
            }
        }

        //TODO open all chars:
        product_item_all_chars.setOnClickListener {
            if (product_chars_rv.layoutParams.height == RecyclerView.LayoutParams.WRAP_CONTENT) {
                product_chars_rv.layoutParams.height = (250 * varApp.convertPxDp).toInt()
                product_all_chars_img.setImageDrawable(resources.getDrawable(R.drawable.ic_down))

            } else {
                product_chars_rv.layoutParams.height = RecyclerView.LayoutParams.WRAP_CONTENT
                product_all_chars_img.setImageDrawable(resources.getDrawable(R.drawable.ic_up))
            }

            product_chars_rv.adapter!!.notifyDataSetChanged()
        }

        //TODO click update:
        status_update.setOnClickListener {
            onLoad()
        }

        //TODO click add cart (first time):
        some_big_btn_active_page.setOnClickListener {
            product_presenter.add_to_cart(product.code, product.salekrat)
            some_big_btn_active_page.visibility = View.GONE
            some_big_btn_deactive_page.visibility = View.VISIBLE
            some_big_btn_deactive2.text = "Добавлено ${varApp.RoundToInteger(product.salekrat.toString(), false)} ${product.ed}"
            some_big_btn_deactive3.text = "+ ${varApp.RoundToInteger(product.salekrat.toString(), false)}"
        }

        //TODO click go cart:
        some_big_btn_deactive_go_cart.setOnClickListener {
            varApp.clearBackStack(varApp.supportFragmentManager)
            varApp.replaceFragment(varApp.supportFragmentManager, cart.instance(), addAnimation = true, anim.from_bottom_in_top, addToBackStack = false)
        }

        //TODO click add cart (next times)
        some_big_btn_deactive3.setOnClickListener {

            if (product.qty_incart + 1 <= product.incash) {
                product_presenter.add_to_cart(code = product.code, qty = product.qty_incart + product.salekrat)
                some_big_btn_deactive2.text = "Добавлено ${varApp.RoundToInteger((product.qty_incart + product.salekrat).toString(), false)} ${product.ed}"
            } else {
                varApp.Toast(it.context, "В наличии только ${varApp.RoundToInteger(product.incash.toString(), false)} ${product.ed}.\n\n Больше добавлять нельзя!")
            }
        }

    }




    fun onLoad () {

        product_page.visibility = View.GONE
        product_appbar.visibility = View.GONE
        product_status.visibility = View.GONE
        varApp.nav_view.visibility = View.GONE
        product_pb.visibility = View.VISIBLE
        product_presenter.onLoad(arguments)
    }

    override fun onSuccess() {

        product_pb.visibility = View.GONE
        product_status.visibility = View.GONE

        product_page.visibility = View.VISIBLE
        product_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as AppCompatActivity).supportActionBar!!,
            customAppBar = product_appbar,
            customAppBar_backgroundColor = R.color.white,
            some_appbar_img_back = true,
            some_title_text = "",
            some_title_textColor = R.color.type_black,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.type_black,
            some_btn_clear_visible = false,
            some_appbar_btn_search = true

        )

        varApp.nav_view.visibility = View.VISIBLE




        //region Данные о продукте:

        product_code.text = "Код: ${product.code}"    //Код
        product_name.text = product.name      //Наменование

        Glide .with(v.context) .load(product.imgpath) .into(product_img)       //Фото

        product_category.text = "Категория: ${product.tk_name}"   //категория

        product_price.text = varApp.RoundToInteger(product.prices, true).toString()  // цена
        product_ed.text   = "Цена за ${product.ed}"   //ед. изм.

        product_info.text = product.incash_info    //"В наличии", "Не в наличии"
        product_incash.text = varApp.RoundToInteger(product.incash.toString(), false).toString() + " " + product.ed    //сколько в наличии

        //TODO active\deactive button:
        some_big_btn_active.visibility = View.GONE
        if ( product.qty_incart != 0.0F) {
            some_big_btn_deactive_page.visibility = View.VISIBLE
            some_big_btn_deactive2.text = "Добавлено ${varApp.RoundToInteger(product.qty_incart.toString(), false)} ${product.ed}"
            some_big_btn_deactive3.text = "+ ${varApp.RoundToInteger(product.salekrat.toString(), false)}"
        } else {
            some_big_btn_active_page.visibility = View.VISIBLE
            some_big_btn_active2.text = "по ${varApp.RoundToInteger(product.salekrat.toString(), false)} ${product.ed}"
        }





        //endregion


        //region Основные характеристики:

        if (chars.size != 0) {

            product_chars_rv.visibility = View.VISIBLE
            product_chars_rv.setHasFixedSize(true)
            product_chars_rv.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
            product_chars_rv.adapter = Adapter_Chars(chars)
            product_chars_rv.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
            product_chars_rv.layoutParams.height = (250 * varApp.convertPxDp).toInt()

        } else {
            product_chars_txt.visibility = View.GONE
            product_chars_rv.visibility = View.GONE
        }

        //endregion


        //region Сопутствующие товары:

        if (related.size != 0) {
            products_related_txt.visibility = View.VISIBLE
            products_related_rv.visibility = View.VISIBLE

            products_related_rv.setHasFixedSize(true)
            products_related_rv.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
            products_related_rv.adapter = Adapter_Related(related)

        } else {
            products_related_txt.visibility = View.GONE
            products_related_rv.visibility = View.GONE
        }

        //endregion



        //region Способы получения:

            arr_name.clear()
            arr_name.add(0, "Самовывоз")
            arr_name.add(1, "Доставка")


            arr_name2.clear()
            var number_points: String = choosePluralMerge(arrayCompany.size.toLong(), "пункт выдачи в", "пункта выдачи в", "пунктов выдачи в")
            var city: String = ""
            if (iam[0].city == "Екатеринбург") {
                number_points = "${number_points} Екатеринбурге"
                city = "По Екатеринбургу и области"
            } else {
                number_points = "${number_points} Нижнем Тагиле"
                city = "По Нижнему Тагилу и области"
            }
            arr_name2.add(0, number_points)
            arr_name2.add(1, city)


            arr_img.clear()
            arr_img.add(0, R.drawable.ic_place)
            arr_img.add(1, R.drawable.ic_shipping)


            product_receipt_txt.visibility = View.VISIBLE
            product_receipt_rv.visibility = View.VISIBLE

            product_receipt_rv.setHasFixedSize(true)
            product_receipt_rv.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
            product_receipt_rv.adapter = Adapter_Receipt ()
            product_receipt_rv.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))

        //endregion


        //set icon close:
        some_addbar_view_back.visibility = View.VISIBLE
        some_appbar_img_back.visibility = View.VISIBLE
        some_appbar_img_back.setImageDrawable(resources.getDrawable(R.drawable.ic_close))


        //set icon favorite:
        some_appbar_btn_search.visibility = View.VISIBLE
        if (product.isfavority) {
            some_appbar_btn_search.setImageDrawable(resources.getDrawable(R.drawable.ic_fav_active))
        } else {
            some_appbar_btn_search.setImageDrawable(resources.getDrawable(R.drawable.ic_fav))
            some_appbar_btn_search.alpha = 1.0F
        }







    }

    override fun noInternet() {

        product_pb.visibility = View.GONE
        product_page.visibility = View.GONE

        product_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as Main).supportActionBar!!,
            customAppBar = product_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = false,
            some_title_text = varApp.heading_no_internet,
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false)

        varApp.nav_view.visibility = View.GONE
        product_status.visibility = View.VISIBLE

    }

    override fun isEmpty() {

        product_pb.visibility = View.GONE
        product_page.visibility = View.GONE


        product_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as AppCompatActivity).supportActionBar!!,
            customAppBar = product_appbar,
            customAppBar_backgroundColor = R.color.white,
            some_appbar_img_back = true,
            some_title_text = "Нет в наличии",
            some_title_textColor = R.color.type_black,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.type_black,
            some_btn_clear_visible = false

        )

        varApp.nav_view.visibility = View.VISIBLE

        product_status.visibility = View.VISIBLE
        status_title.text = "Такого товара нет."
        status_body.text = "На данный момент товара нет в ниличии."
        status_update.visibility = View.GONE


        //set ic_close:
        some_addbar_view_back.visibility = View.VISIBLE
        some_appbar_img_back.visibility = View.VISIBLE
        some_appbar_img_back.setImageDrawable(resources.getDrawable(R.drawable.ic_close))

    }


    fun choosePluralMerge(number: Long, caseOne: String?, caseTwo: String?, caseFive: String?): String {
        /* Выбирает правильную форму существительного в зависимости от числа.
           Чтобы легко запомнить, в каком порядке указывать варианты, пользуйтесь мнемоническим правилом:
           один-два-пять - один гвоздь, два гвоздя, пять гвоздей.
           [url]http://pyobject.ru/blog/2006/09/02/pytils/[/url]

           in: число и слово в трёх падежах.
           out: строка (число + существительное в нужном падеже).
         */
        var number = number
        var str: String = java.lang.Long.toString(number) + " "
        number = abs(number)
        str += if (number % 10 == 1L && number % 100 != 11L) {
            caseOne
        } else if (number % 10 >= 2 && number % 10 <= 4 && (number % 100 < 10 || number % 100 >= 20)) {
            caseTwo
        } else {
            caseFive
        }
        return str
    }

}








//region Adapter

class Adapter_Chars(chars: ArrayList<JsonObject>) : RecyclerView.Adapter<Chars_VH>() {

    var chars_main: ArrayList<JsonObject> = ArrayList()

    init {
        this.chars_main = chars
    }


    override fun getItemCount(): Int { return chars_main.size }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Chars_VH {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.element_item_rv, parent, false)

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            view.page_rv.foreground = null
        }

        val chars_vh =  Chars_VH(view)

        return chars_vh

    }


    override fun onBindViewHolder(holder: Chars_VH, position: Int) {

//        val dec = Json.decodeFromJsonElement<JsonObject>(js_decod[position])
        val v = holder.itemView

        v.img_right_rv.visibility = View.GONE

        v.name_rv.visibility = View.VISIBLE
//        v.name_rv.layoutParams.width = (100 * varApp.convertPxDp).toInt()
        v.name_rv.text = chars_main[position].get("Name").toString().replace(34.toChar().toString(), "")


        v.name3_rv.visibility = View.VISIBLE

        v.name3_rv.text = chars_main[position].get("Value").toString().replace(34.toChar().toString(), "")




    }

}

class Chars_VH(v: View) : RecyclerView.ViewHolder(v) {}




class Adapter_Related(related: ArrayList<JsonObject>) : RecyclerView.Adapter<Related_VH>() {

    var related: ArrayList<JsonObject> = ArrayList()

    init {
        this.related = related
    }


    override fun getItemCount(): Int { return related.size }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Related_VH {
        return  Related_VH(LayoutInflater.from(parent.context).inflate(R.layout.element_sales, parent, false))
    }


    override fun onBindViewHolder(holder: Related_VH, position: Int) {

        val v = holder.itemView

        v.sales_name.text = related.get(position).get("name").toString().replace(34.toChar().toString(), "")

        v.sales_prices.text = varApp.RoundToInteger(related.get(position).get("price").toString(), true).toString()

        Glide
            .with(v.context)
            .load(related.get(position).get("imgpath").toString().replace(34.toChar().toString(), ""))
            .into(holder.itemView.sales_image)



    }

}

class Related_VH(v: View) : RecyclerView.ViewHolder(v) {
    init {
        v.setOnClickListener {

            val product: Fragment = ru.isantur.santurshop.fragment.products.product.instance(
                bundleOf(
                    "code" to related.get(adapterPosition)["code"].toString().replace(34.toChar().toString(), "")
                )
            )

            varApp.supportFragmentManager.beginTransaction()
//                .setCustomAnimations(R.anim.slide_in_down, R.anim.slide_in_down)
                .hide(v.findFragment())
                .add(R.id.nav_host_fragment, product, "product")
                .show(product)
                .addToBackStack("product")
                .commit()
        }
    }
}




class Adapter_Receipt() : RecyclerView.Adapter<Receipt_VH>() {


    override fun getItemCount(): Int { return arr_name.size }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Receipt_VH {
        return  Receipt_VH(LayoutInflater.from(parent.context).inflate(R.layout.element_item_receipt_rv, parent, false))
    }


    override fun onBindViewHolder(holder: Receipt_VH, position: Int) {

        val v = holder.itemView

        v.receipt_name1.text = arr_name[position]
        v.receipt_name2.text = arr_name2[position]
        v.receipt_img.setImageDrawable(v.resources.getDrawable(arr_img[position]))

    }

}

class Receipt_VH(v: View) : RecyclerView.ViewHolder(v) {

    init {
        v.setOnClickListener {
            if (adapterPosition == 0) {

                val map: Fragment = map.instance(
                    bundleOf("from" to "other"))
                varApp.supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .hide(it.findFragment())
                    .add(R.id.nav_host_fragment, map, "map")
                    .show(map)
                    .addToBackStack("map")
                    .commit()

            } else {

                val info_delivery: Fragment = info_delivery.instance()
                varApp.supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .hide(it.findFragment())
                    .add(R.id.nav_host_fragment, info_delivery, "info_delivery")
                    .show(info_delivery)
                    .addToBackStack("info_delivery")
                    .commit()

            }
        }
    }

}



//endregion






//region PRESENTER

@InjectViewState
class product_Presenter : MvpPresenter<Product_ViewState>() {

    private val disposeBag = CompositeDisposable()

    @Inject
    lateinit var backendModule: BackendModule

    fun onLoad(args: Bundle?) {
        varApp.appComponent.inject(this)

        //Данные продукта:
//        val product =  Gson().fromJson(khttp.get( "${varApp.url}GetGoodCart/?gcode=${arguments?.getString("code")}&sid=${varApp.SID}").text, ListGoods::class.java)

        disposeBag.add(backendModule.provideMyGet().getGoodCart(args?.getString("code"), varApp.SID)
            .delay(varApp.delay_default, varApp.typeUnit)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (!it.code.equals("")) {
                    product = it
                    chars(args)
                } else {
                    viewState.isEmpty()
                }
            }, {
                viewState.noInternet()
            }))

    }

    fun chars(args: Bundle?) {
        varApp.appComponent.inject(this)

        //Основные характеристики:

//        val Chars_Main = Json.decodeFromString<ArrayList<JsonObject>>(khttp.get( "${varApp.url}GetGoodChars/?gcode=${arguments?.getString("code")}&sid=${varApp.SID}").text )

        disposeBag.add(backendModule.provideMyGet().getGoodChars(args?.getString("code"), varApp.SID).delay(varApp.delay_default, varApp.typeUnit).subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({

                chars = it

                related(args)


            }, {
                viewState.noInternet()
            }))

    }

    fun related(args: Bundle?) {
        varApp.appComponent.inject(this)



        disposeBag.add(backendModule.provideMyGet().getGoodAnG(args?.getString("code"), varApp.SID)
            .delay(varApp.delay_default, varApp.typeUnit)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                related = it

                companyinfo()



            }, {
                viewState.noInternet()
            })
        )

    }

    fun add_favorite(code: String, view: View) {
        varApp.appComponent.inject(this)

        disposeBag.add(backendModule.provideMyGet().addFavorite(code, varApp.SID).delay(varApp.delay_default, varApp.typeUnit).subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({
                if (it.contains("ok")) {
                    view.some_appbar_btn_search.setImageDrawable(view.resources.getDrawable(R.drawable.ic_fav_active))
                    product.isfavority = true
                    if (backendModule.getFavoriteBadge() == AppResponse.noInternet) viewState.noInternet()
                }
            }, {
                viewState.noInternet()
            }))

    }

    fun remove_favorite(code: String, view: View) {

        varApp.appComponent.inject(this)

        disposeBag.add(backendModule.provideMyGet().removeFavorite(code, varApp.SID).delay(varApp.delay_default, varApp.typeUnit).subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({
                if (it.contains("ok")) {
                    view.some_appbar_btn_search.setImageDrawable(view.resources.getDrawable(R.drawable.ic_fav))
                    view.some_appbar_btn_search.alpha = 1.0F
                    product.isfavority = false
                    if (backendModule.getFavoriteBadge() == AppResponse.noInternet) viewState.noInternet()
                }

            }, {
                viewState.noInternet()
            }))

    }

    fun add_to_cart (code: String, qty: Float) {
        varApp.appComponent.inject(this)

        disposeBag.add(
            backendModule.provideMyGet().addToCart(code, qty.toString(), varApp.SID)
                .delay(varApp.delay_default, varApp.typeUnit)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    product.qty_incart = product.qty_incart + product.salekrat
                    if (backendModule.getCartBadge() == AppResponse.noInternet) viewState.noInternet()
                }, {
                    viewState.noInternet()
                })
        )

    }

    fun companyinfo () {
        varApp.appComponent.inject(this)

        disposeBag.add(
            backendModule.provideMyGet().companyinfo(varApp.SID)
                .delay(varApp.delay_default, varApp.typeUnit)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    if (it.size != 0) {
                        arrayCompany = it
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