package ru.isantur.santurshop.fragment

import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.alert_dialog.view.*
import kotlinx.android.synthetic.main.element_list.view.*
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.android.synthetic.main.fragment_cart.view.*
import kotlinx.android.synthetic.main.include_appbar.view.*
import kotlinx.android.synthetic.main.include_status.*
import moxy.InjectViewState
import moxy.MvpAppCompatFragment
import moxy.MvpPresenter
import moxy.MvpView
import moxy.presenter.InjectPresenter
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.isantur.santurshop.AppResponse
import ru.isantur.santurshop.Data.Cart
import ru.isantur.santurshop.R
import ru.isantur.santurshop.activity.Main
import ru.isantur.santurshop.di.BackendModule
import ru.isantur.santurshop.fragment.cart.Companion.carts
import ru.isantur.santurshop.fragment.cart.Companion.qty_item
import ru.isantur.santurshop.fragment.cart.Companion.sum
import ru.isantur.santurshop.fragment.cart.Companion.v
import ru.isantur.santurshop.fragment.cart.Companion.weight
import ru.isantur.santurshop.fragment.checkout.checkout
import ru.isantur.santurshop.fragment.products.product
import ru.isantur.santurshop.varApp
import javax.inject.Inject

@StateStrategyType(value = OneExecutionStateStrategy::class)
interface Cart_ViewState: MvpView {

    fun onSuccess(resp: Cart)
    fun noInternet ()
    fun isEmpty()


}



class cart : MvpAppCompatFragment(), Cart_ViewState {

    @InjectPresenter lateinit var cart_presenter: cart_Presenter

    companion object {

        fun instance (): Fragment {
            return cart()
        }

        lateinit var carts: Cart

        lateinit var v: View

        lateinit var presenter: cart_Presenter

        var qty_item = ""
        var weight = ""
        var sum = ""


    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        v = inflater.inflate(R.layout.fragment_cart, container, false)

        return v

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        presenter = cart_presenter

        varApp.nav_view.menu.getItem(3).setChecked(true)
        varApp.nav_view.visibility = View.VISIBLE



        onLoad(view)


        //TODO click update:
        status_update.setOnClickListener {
            onLoad(view)
        }


        //TODO click ОФОРМИТЬ ЗАКАЗ:
        cart_btn_send.setOnClickListener {
            varApp.replaceFragment(varApp.supportFragmentManager, checkout.instance(), addAnimation = false, addToBackStack = true)
        }



    }


    override fun onSuccess(resp: Cart) {

        cart_pb.visibility = View.GONE
        cart_status.visibility = View.GONE

        cart_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as AppCompatActivity).supportActionBar!!,
            customAppBar = cart_appbar,
            customAppBar_backgroundColor =  R.color.white,
            some_appbar_img_back = false,
            some_title_text =  "Корзина",
            some_title_textColor = R.color.type_black,
            some_btn_clear_text = "Очистить",
            some_btn_clear_textColor =  R.color.primary,
            some_btn_clear_visible = true

        )

        varApp.nav_view.visibility = View.VISIBLE


        carts = resp

        if (carts.items.size != 0) {

            cart_rv.visibility = View.VISIBLE
            cart_footer_ll.visibility = View.VISIBLE


            cart_rv.setHasFixedSize(true)
            //cart_rv.layoutManager =  FadeInLinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
            cart_rv.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
            cart_rv.adapter = AdapterCartList()
            cart_rv.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))

            presenter.update_sum()

            //TODO clear all cart:
            cart_appbar.some_appbar_btn_clear.setOnClickListener {

                val dlgView = LayoutInflater.from(it.context).inflate(R.layout.alert_dialog, null)


                varApp.changeCustomAppBar(
                    standartAppBar_visible = false,
                    standartAppBar = null,
                    customAppBar = dlgView.dialog_appbar,
                    customAppBar_backgroundColor = R.color.white,
                    some_appbar_img_back = false,
                    some_title_text = "Удаление товаров",
                    some_title_textColor = R.color.type_black,
                    some_btn_clear_text = "",
                    some_btn_clear_textColor = R.color.type_black,
                    some_btn_clear_visible = false
                )


                //show: dialog_title, dialog_text, dialog_ll, dialog_cancel, dialog_ok:
                dlgView.dialog_text.visibility = View.VISIBLE

                dlgView.dialog_text.text = "Вы точно хотите удалить товары из корзины?"
                dlgView.dialog_two_button.visibility = View.VISIBLE
                dlgView.dialog_cancel.text = "Отмена"
                dlgView.dialog_action.text = "Удалить"

                //hide: list view, bit button, timer
                dlgView.dialog_divider_end.visibility = View.INVISIBLE
                dlgView.dialog_big_button.visibility = View.GONE


                val builder = AlertDialog.Builder(it.context)
                with(builder) {
                    setView(dlgView)
                }
                val dlg = builder.show()

                dlgView.dialog_action.setOnClickListener {
                    dlg.dismiss()
                    presenter.clear_cart()

                }

                dlgView.dialog_cancel.setOnClickListener { dlg.dismiss() }


            }


        } else {
            isEmpty()
        }



    }

    override fun noInternet() {

        cart_pb.visibility = View.GONE
        cart_rv.visibility = View.GONE
        cart_footer_ll.visibility = View.GONE

        cart_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as Main).supportActionBar!!,
            customAppBar = cart_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = false,
            some_title_text = varApp.heading_no_internet,
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false
        )

        varApp.nav_view.visibility = View.GONE

        cart_status.visibility = View.VISIBLE

    }

    override fun isEmpty () {

        cart_pb.visibility = View.GONE
        cart_rv.visibility = View.GONE
        cart_footer_ll.visibility = View.GONE

        cart_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as AppCompatActivity).supportActionBar!!,
            customAppBar = cart_appbar,
            customAppBar_backgroundColor =  R.color.white,
            some_appbar_img_back = false,
            some_title_text =  "Корзина",
            some_title_textColor = R.color.type_black,
            some_btn_clear_text = "",
            some_btn_clear_textColor =  R.color.primary,
            some_btn_clear_visible = false

        )

        varApp.nav_view.visibility = View.VISIBLE

        cart_status.visibility = View.VISIBLE
        status_title.text = "В вашей корзине пусто"
        status_body.text = "Перейдите в каталог, чтобы найти необходимые товары."
        status_update.visibility = View.GONE
    }

    fun onLoad (view: View) {
        view.cart_pb.visibility = View.VISIBLE
        v.cart_appbar.visibility = View.GONE
        v.cart_status.visibility = View.GONE
        varApp.nav_view.visibility = View.GONE

        cart_presenter.onLoad()
    }





}




//region Adapter

class AdapterCartList : RecyclerView.Adapter<AdapterCartListVH>() {


    override fun getItemCount(): Int { return carts.items.size }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterCartListVH {
        return  AdapterCartListVH(LayoutInflater.from(parent.context).inflate(R.layout.element_list, parent, false))
    }



    override fun onBindViewHolder(holder: AdapterCartListVH, position: Int) {

        val view = holder.itemView

        //show\hide other view:
        view.list_add_del_cart_active.visibility = View.GONE
        view.list_add_del_cart_active.visibility = View.GONE
        view.list_favorite.visibility = View.GONE

        view.list_constrCart.visibility = View.VISIBLE
        view.list_more.visibility = View.VISIBLE

        //Заполняем listview:
        view.list_name.text = carts.items[position].name
        view.list_prices.text = varApp.RoundToInteger(carts.items[position].prices, true).toString()
        view.list_ed.text = "за ${carts.items[position].ed}"
        Glide.with(view.context).load(carts.items[position].imgpath).into(view.list_img)



        view.list_cart.setText("${varApp.RoundToInteger(carts.items[position].qty.toString(), false)} ")




    }

}

class AdapterCartListVH(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

    //навешиваем события
    init {

        //click more:
        v.list_more.setOnClickListener(this)

        // click add\remove cart
        v.list_Add.setOnClickListener(this)
        v.list_Remove.setOnClickListener(this)

        //CLICK (переход на страницу продукта):
        v.list_img.setOnClickListener ( this )
        v.list_name.setOnClickListener(this)

    }


    override fun onClick(v: View?) {
        when (v!!.id) {

            R.id.list_more -> {
                //click more
                val pop= PopupMenu(v.context, v)
                pop.inflate(R.menu.popup_cart)

                pop.setOnMenuItemClickListener { item->

                    when(item.itemId) {

                        R.id.delete_from_cart -> {

                            cart.presenter.delete_cart(adapterPosition)

                        } //delete_from_cart

                        R.id.add_to_favorites -> {

                            if (carts.items[ adapterPosition ].isfafority) {
                                //delete get query from favorites:
                                cart.presenter.remove_favorite(adapterPosition)

                            } else {
                                //add get query from favorites:
                                cart.presenter.add_favorite(adapterPosition)
                            }


                        } //add_to_favorites


                    } //when popup

                    true

                } //click popup-menu

                if (carts.items[ adapterPosition ].isfafority)
                    pop.menu.getItem(1).title = "Удалить из избранного"
                else
                    pop.menu.getItem(1).title = "Добавить в избранное"

                pop.show()

            } //TODO list_more

            R.id.list_Add -> { //click add cart:
                //TODO здесь нужно добавить проверку: сколько мы можем выгрузить???
                cart.presenter.change_cart(adapterPosition, carts.items[adapterPosition].qty + 1 )

            } //TODO cart add

            R.id.list_Remove -> { //click delete cart:

                cart.presenter.change_cart(adapterPosition, carts.items[adapterPosition].qty - 1 )

            } //TODO cart remove

            R.id.list_img, R.id.list_name -> {

                val product: Fragment = product.instance(
                    bundleOf(
                        "code" to carts.items[adapterPosition].code
                    )
                )

                varApp.supportFragmentManager.beginTransaction()
                    //.setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_up)
                    .hide(v.findFragment())
                    .add(R.id.nav_host_fragment, product, "product")
                    .show(product)
                    .addToBackStack("product")
                    .commit()


//                varApp.replaceFragment(varApp.supportFragmentManager,
//                    product.instance(
//                        bundleOf(
//                            "code" to lists[adapterPosition].code
//                        )
//                    ), addAnimation = false, addToBackStack = true
//                )


            } // go to product

        }



    }







}

//endregion



//region PRESENTER

@InjectViewState
class cart_Presenter : MvpPresenter<Cart_ViewState>() {

    private val disposeBag = CompositeDisposable()

    @Inject lateinit var backendModule: BackendModule


    fun onLoad () {
        varApp.appComponent.inject(this)

        disposeBag.add(
            backendModule.provideMyGet().getCart(varApp.SID)
                .delay(varApp.delay_default, varApp.typeUnit)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    if (it.items.size !=0 ) {
                        viewState.onSuccess(it)
                    } else {
                        viewState.isEmpty()
                    }
                    if (backendModule.getCartBadge() == AppResponse.noInternet) viewState.noInternet()

                }, {
                    viewState.noInternet()
                })
        )

    }

    fun update_sum () {
        varApp.appComponent.inject(this)

        disposeBag.add(
            backendModule.provideMyGet().getCart(varApp.SID)
                .delay(varApp.delay_default, varApp.typeUnit)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({

                    qty_item = "${it.qtyitems}"
                    weight = "${it.weight}"
                    sum = "${varApp.RoundToInteger(it.summs, true) }"

                    v.cart_qty_weight.text = "Товаров: ${it.qtyitems} / ${it.weight} кг."
                    v.cart_sum.text = sum

                    if (backendModule.getCartBadge() == AppResponse.noInternet) viewState.noInternet()
//                    viewState.success_update_sum(it)
                }, {
                    viewState.noInternet()
                })
        )

    }

    fun clear_cart () {
        varApp.appComponent.inject(this)

        disposeBag.add(
            backendModule.provideMyGet().clearCart(varApp.SID)
                .delay(varApp.delay_default, varApp.typeUnit)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({

                    carts.items.clear()
                    v.cart_rv.removeAllViews()
                    varApp.removeBadge(R.id.navigation_cart)
                    viewState.isEmpty()


                }, {
                    viewState.noInternet()
                })
        )

    }

    fun delete_cart (adapterPosition: Int) {
        varApp.appComponent.inject(this)

        disposeBag.add(
            backendModule.provideMyGet().removeCartItem( carts.items[adapterPosition].code, varApp.SID)
                .delay(varApp.delay_default, varApp.typeUnit)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({

                    carts.items.remove(carts.items[adapterPosition])
                    v.cart_rv.adapter!!.notifyItemRemoved(adapterPosition)
                    update_sum()
//                    v.cart_rv.removeViewAt(adapterPosition)
//                    v.cart_rv.layoutManager!!.removeViewAt(adapterPosition)
                    if (carts.items.size <= 0) {
                        viewState.isEmpty()
                    }


                }, {
                    viewState.noInternet()
                })
        )

    }

    fun change_cart (adapterPosition: Int, qty: Float) {
        varApp.appComponent.inject(this)

        disposeBag.add(
            backendModule.provideMyGet().changeCartItem( carts.items[adapterPosition].code, qty.toString(), varApp.SID)
                .delay(varApp.delay_default, varApp.typeUnit)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({

                    if (qty <= 0) {
                        carts.items[adapterPosition].qty = 0.0F
                        delete_cart(adapterPosition)
                    } else {
                        carts.items[adapterPosition].qty = qty
                        v.cart_rv.adapter!!.getItemId(adapterPosition)
                        v.cart_rv.layoutManager!!.findViewByPosition(adapterPosition)!!
                            .list_cart.setText( "${varApp.RoundToInteger(carts.items[adapterPosition].qty.toString(), false)}" )
                        update_sum()
                    }



                }, {
                    viewState.noInternet()
                })
        )

    }

    fun remove_favorite (adapterPosition: Int) {
        varApp.appComponent.inject(this)

        disposeBag.add(
            backendModule.provideMyGet().removeFavorite( carts.items[adapterPosition].code, varApp.SID)
                .delay(varApp.delay_default, varApp.typeUnit)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    if (it.contains("ok")) {
                        carts.items[adapterPosition].isfafority = false
                        v.cart_rv.get(adapterPosition).list_favorite.tag = "В избранное"
                        if (backendModule.getFavoriteBadge() == AppResponse.noInternet) viewState.noInternet()
                    }
                }, {
                    viewState.noInternet()
                })
        )

    }

    fun add_favorite (adapterPosition: Int) {
        varApp.appComponent.inject(this)

        disposeBag.add(
            backendModule.provideMyGet().addFavorite( carts.items[adapterPosition].code, varApp.SID)
                .delay(varApp.delay_default, varApp.typeUnit)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    if (it.contains("ok")) {
                        carts.items[adapterPosition].isfafority = true
                        v.cart_rv.get(adapterPosition).list_favorite.tag = "В избранном"
                        if (backendModule.getFavoriteBadge() == AppResponse.noInternet) viewState.noInternet()
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
