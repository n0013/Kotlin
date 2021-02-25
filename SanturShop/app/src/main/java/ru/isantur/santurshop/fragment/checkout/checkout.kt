package ru.isantur.santurshop.fragment.checkout

import android.app.AlertDialog
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.alert_dialog.view.*
import kotlinx.android.synthetic.main.element_item_rv.view.*
import kotlinx.android.synthetic.main.fragment_checkout.*
import kotlinx.android.synthetic.main.fragment_checkout.view.*
import kotlinx.android.synthetic.main.include_appbar.*
import kotlinx.android.synthetic.main.include_appbar.view.*
import kotlinx.android.synthetic.main.include_status.*
import moxy.InjectViewState
import moxy.MvpAppCompatFragment
import moxy.MvpPresenter
import moxy.MvpView
import moxy.presenter.InjectPresenter
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.isantur.santurshop.R
import ru.isantur.santurshop.Retrofit.myGET
import ru.isantur.santurshop.activity.Main
import ru.isantur.santurshop.anim
import ru.isantur.santurshop.di.BackendModule
import ru.isantur.santurshop.fragment.cart
import ru.isantur.santurshop.fragment.checkout.checkout.Companion.dlg
import ru.isantur.santurshop.fragment.checkout.checkout.Companion.placeId
import ru.isantur.santurshop.fragment.checkout.checkout.Companion.presenter
import ru.isantur.santurshop.fragment.checkout.checkout.Companion.v
import ru.isantur.santurshop.fragment.map.map
//import ru.isantur.santurshop.fragment.map.map
import ru.isantur.santurshop.fragment.orders.order_registered
import ru.isantur.santurshop.fragment.profile.profile_quick_registration
import ru.isantur.santurshop.varApp
import javax.inject.Inject

@StateStrategyType(value = OneExecutionStateStrategy::class)
interface Checkout_ViewState: MvpView {

    fun noInternet ()
    fun isEmpty()
    fun CartToOrd(order: String)

}


class checkout : MvpAppCompatFragment(), Checkout_ViewState {

    @InjectPresenter lateinit var checkout_presenter: checkout_Presenter

    companion object {

        fun instance (): Fragment {
            return checkout()
        }

        lateinit var v: View


        var time: CountDownTimer? = null

        lateinit var Order: String
        lateinit var OrderSum: String

        lateinit var presenter: checkout_Presenter

        var placeId = 0

        lateinit var checkoutFragment: Fragment

        lateinit var dlg: AlertDialog


    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        presenter = checkout_presenter

        v = inflater.inflate(R.layout.fragment_checkout, container, false)

        checkoutFragment = this

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        onLoad()

        onEvents()

    }



    fun dialogTimerTick (dlgView: View, dlg: AlertDialog, hide_fragment: Fragment) {
        time = object : CountDownTimer(10000, 1000) {

            //Здесь обновляем текст счетчика обратного отсчета с каждой секундой
            override fun onTick(millisUntilFinished: Long) {
                dlgView.some_appbar_btn_clear.text = "${millisUntilFinished / 1000}"
            }

            //Задаем действия после завершения отсчета (close dialog):
            override fun onFinish() {
                dlg.dismiss()
                //go page order_registered:
                val profile_quick_registration: Fragment = profile_quick_registration.instance()
                varApp.supportFragmentManager.beginTransaction()
                    .hide(hide_fragment)
                    .add(R.id.nav_host_fragment, profile_quick_registration, "profile_quick_registration")
                    .show(profile_quick_registration)
                    .addToBackStack("profile_quick_registration")
                    .commit()
            }
        }.start()




    }

    fun onLoad() {

        checkout_status.visibility = View.GONE
        checkout_pb.visibility = View.GONE

        checkout_page.visibility = View.VISIBLE

        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as AppCompatActivity).supportActionBar!!,
            customAppBar = checkout_appbar,
            customAppBar_backgroundColor = R.color.white,
            some_appbar_img_back = true,
            some_title_text = "Оформление заказа",
            some_title_textColor = R.color.type_black,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.type_black,
            some_btn_clear_visible = false
        )

        varApp.nav_view.visibility = View.VISIBLE


        //set value in name and phone and email:
        //first from iam:
        if (varApp.iam[0].name != "" ) {
            checkout_first_name.text = varApp.iam[0].name

            if (varApp.iam[0].phone != "") {
                checkout_first_name.text = "${varApp.iam[0].name} ${varApp.iam[0].phone}"
            }
            if (varApp.iam[0].email != "") {
                checkout_first_name.text = "${varApp.iam[0].name} ${varApp.iam[0].phone} ${varApp.iam[0].email}"
            }

        } else {
            //last from profile_quick_registration:
            if (profile_quick_registration.FirstName != "") {

                checkout_first_name.text = "${profile_quick_registration.FirstName} ${profile_quick_registration.LastName} ${profile_quick_registration.Phone} ${profile_quick_registration.Email} "

            }
        }


        //default Pickup (or can Delivery):
        checkout_method_receiving_Pickup_rb.isChecked = true

        //default PaymentOnDelivery (or online, or on account)
        checkout_payment_on_delivery.isChecked = true


        //set text for city by default from iam:
        checkout_address_delivery_page_1_city.text = varApp.iam[0].city


        // set products and summ:

        //number products:
        checkout_qty_weight.text = "Товаров: ${cart.qty_item} / ${cart.weight} кг."
        //summ:
        checkout_qty_sum.text = cart.sum

        OrderSum = cart.sum


    }

    fun onEvents () {

        //TODO EVENTS:
        //TODO click go quick registration:
        checkout_data_constr.setOnClickListener {
            if (varApp.iam[0].name == "Гость" || varApp.iam[0].name == "" ) {
                //переход на страницу быстрой регистрации
                varApp.replaceFragment(varApp.supportFragmentManager, profile_quick_registration.instance(), addAnimation = true, addToBackStack = true)

            }
        }



        /*TODO
           order_address_delivery_page_1 - самовывоз
           order_address_delivery_page_2 - доставка
         */
        //TODO click choice method receiving (самовывоз):
        checkout_method_receiving_Pickup_rb.setOnClickListener {
            checkout_address_delivery_page_1.visibility = View.VISIBLE
            checkout_address_delivery_page_2.visibility = View.GONE
            checkout_pickup.text = "Самовывоз"
            checkout_delivery_sum.text = "0 Р"
        }

        //TODO click choice method receiving (доставка):
        checkout_method_receiving_Delivery_rb.setOnClickListener {
            checkout_address_delivery_page_1.visibility = View.GONE
            checkout_address_delivery_page_2.visibility = View.VISIBLE
            checkout_pickup.text = "Доставка"
            checkout_delivery_sum.text = "Уточнит менеджер"
        }


        //TODO click back:
        some_addbar_view_back.setOnClickListener {
            varApp.clearBackStack(varApp.supportFragmentManager)
        }


        //TODO click select cities (alert dialog for choice cities):
        checkout_address_delivery_page_1_part_1.setOnClickListener {


            val dlgView = LayoutInflater.from(it.context).inflate(R.layout.alert_dialog, null)

            dlgView.dialog_text.visibility = View.GONE
            dlgView.dialog_two_button.visibility = View.GONE
            dlgView.dialog_divider_end.visibility = View.GONE
            dlgView.dialog_big_button.visibility = View.GONE


            varApp.changeCustomAppBar(
                standartAppBar_visible = false,
                standartAppBar = null,
                customAppBar = dlgView.dialog_appbar,
                customAppBar_backgroundColor = R.color.white,
                some_appbar_img_back = false,
                some_title_text = "Выбор города",
                some_title_textColor = R.color.type_black,
                some_btn_clear_text = "",
                some_btn_clear_textColor = R.color.type_black,
                some_btn_clear_visible = false
            )




            val builder = AlertDialog.Builder(it.context)
            with(builder) {
                setView(dlgView)
            }
            dlg = builder.show()

            dlgView.dialog_rv.setHasFixedSize(true)
            dlgView.dialog_rv.layoutManager = LinearLayoutManager( it.context, LinearLayoutManager.VERTICAL, false)
            dlgView.dialog_rv.adapter = AdapterDialog()
            dlgView.dialog_rv.addItemDecoration(DividerItemDecoration( it.context, DividerItemDecoration.VERTICAL))

        }


        //TODO click select address (map):
        checkout_address_delivery_page_1_part_2.setOnClickListener {

            var map: Fragment = map.instance(  bundleOf("from" to "checkout" ) )

            varApp.supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .hide(this)
                .add(R.id.nav_host_fragment, map, "map")
                .show(map)
                .addToBackStack("map")
                .commit()

        }

        //TODO click update:
        status_update.setOnClickListener {
            if (varApp.isNetwork(it.context)){
                onLoad()
                onEvents()
            }
        }


        //TODO focus\click city, street, house, apartment:
        checkout_city.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) {checkout_city_txt.error = null } }
        checkout_city.setOnClickListener { checkout_city_txt.error = null }

        checkout_street.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) {checkout_street_txt.error = null } }
        checkout_street.setOnClickListener { checkout_street_txt.error = null }

        checkout_house.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) {checkout_house_txt.error = null } }
        checkout_house.setOnClickListener { checkout_house_txt.error = null }

        checkout_apartment.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) {checkout_apartment_txt.error = null } }
        checkout_apartment.setOnClickListener { checkout_apartment_txt.error = null }



        //TODO click ORDER CHECKOUT:
        checkout_checkout.setOnClickListener {

            when (checkout_first_name.text) {

                "Гость", "" -> {
                    // User not registered:
                    //display dialog:
                    val dlgView = LayoutInflater.from(it.context).inflate(R.layout.alert_dialog, null)

                    //settings for dlgView:
                    dlgView.some_addbar_view_back.visibility = View.INVISIBLE
                    dlgView.some_appbar_img_back.visibility = View.GONE

                    dlgView.dialog_appbar.setBackgroundColor(dlgView.dialog_appbar.resources.getColor(R.color.white))

                    // title:
                    dlgView.some_appbar_title.visibility = View.VISIBLE
                    dlgView.some_appbar_title.text = "Регистрация"
                    dlgView.some_appbar_title.setTextColor(dlgView.resources.getColor(R.color.type_black))

                    // timer:
                    dlgView.some_appbar_btn_clear.visibility = View.VISIBLE
                    dlgView.some_appbar_btn_clear.text = "10"
                    dlgView.some_appbar_btn_clear.setTextColor(dlgView.resources.getColor(R.color.error))

                    // body:
                    dlgView.dialog_text.text = "Для оформления заказов необходима регистрация.\n\nВ течение 10 сек. произойдет автоматический переход на страницу регистрации"
                    dlgView.dialog_big_button.visibility = View.GONE
                    dlgView.dialog_rv.visibility = View.GONE
                    dlgView.dialog_cancel.text = "Отмена"
                    dlgView.dialog_action.text = "Регистрация"



                    val builder = AlertDialog.Builder(it.context)
                    with(builder) {
                        setView(dlgView)
                    }
                    val dlg = builder.show()

                    //show timet:
                    dialogTimerTick( dlgView, dlg, v.findFragment() )

                    //TODO click cancel:
                    dlgView.dialog_cancel.setOnClickListener {
                        time?.cancel()
                        dlg.dismiss()
                    }

                    //TODO click action:
                    dlgView.dialog_action.setOnClickListener {
                        time?.cancel()
                        dlg.dismiss()

                        //go page quick registered:
                        val profile_quick_registration: Fragment = profile_quick_registration.instance()
                        varApp.supportFragmentManager.beginTransaction()
                            .hide(v.findFragment())
                            .add(R.id.nav_host_fragment, profile_quick_registration, "profile_quick_registration")
                            .show(profile_quick_registration)
                            .addToBackStack("profile_quick_registration")
                            .commit()

                    }

                }
                else -> {
                    //user registration:
                    if (checkout_method_receiving_Delivery_rb.isChecked) { //выбрана доставка

                        checkout_city.requestFocus()

                        if (checkout_city.text.toString() == "") { YoYo.with(Techniques.Shake).duration(1000).playOn(checkout_city_txt);  checkout_city_txt.error = "Введите город" }
                        if (checkout_street.text.toString() == "") { YoYo.with(Techniques.Shake).duration(1000).playOn(checkout_street_txt);  checkout_street_txt.error = "Введите улицу" }
                        if (checkout_house.text.toString() == "") { YoYo.with(Techniques.Shake).duration(1000).playOn(checkout_house_txt);  checkout_house_txt.error = "Введите дом" }
                        if (checkout_apartment.text.toString() == "") { YoYo.with(Techniques.Shake).duration(1000).playOn(checkout_apartment_txt);  checkout_apartment_txt.error = "Введите квартиру\\офис" }

                        if ( checkout_city.text.toString() != "" && checkout_street.text.toString() != "" && checkout_house.text.toString() != "" && checkout_apartment.text.toString() != "") {
                            checkout_presenter.CartToOrd()
                        }

                    } else { //выбрана самовывоз

                        if (checkout_address_delivery_points.text != "") {

                            checkout_presenter.CartToOrd()

                        } else {
                            //лучше сделать либо toast по середине экрана, либо alert dialog???
                            checkout_address_delivery_points.requestFocus()
                            Toast.makeText(it.context, "Необходимо выбрать пункт выдачи", Toast.LENGTH_SHORT).show()
                        }

                    }
                }

            }


//            if (checkout_first_name.text != "Гость" || checkout_first_name.text != "" ) {
//
//                if (checkout_method_receiving_Delivery_rb.isChecked) { //выбрана доставка
//
//                    checkout_city.requestFocus()
//
//                    if (checkout_city.text.toString() == "") { YoYo.with(Techniques.Shake).duration(1000).playOn(checkout_city_txt);  checkout_city_txt.error = "Введите город" }
//                    if (checkout_street.text.toString() == "") { YoYo.with(Techniques.Shake).duration(1000).playOn(checkout_street_txt);  checkout_street_txt.error = "Введите улицу" }
//                    if (checkout_house.text.toString() == "") { YoYo.with(Techniques.Shake).duration(1000).playOn(checkout_house_txt);  checkout_house_txt.error = "Введите дом" }
//                    if (checkout_apartment.text.toString() == "") { YoYo.with(Techniques.Shake).duration(1000).playOn(checkout_apartment_txt);  checkout_apartment_txt.error = "Введите квартиру\\офис" }
//
//                    if ( checkout_city.text.toString() != "" && checkout_street.text.toString() != "" && checkout_house.text.toString() != "" && checkout_apartment.text.toString() != "") {
//                        checkout_presenter.CartToOrd()
//                    }
//
//                } else { //выбрана самовывоз
//
//                    if (checkout_address_delivery_points.text != "") {
//
//                        checkout_presenter.CartToOrd()
//
//                    } else {
//                        //лучше сделать либо toast по середине экрана, либо alert dialog???
//                        checkout_address_delivery_points.requestFocus()
//                        Toast.makeText(it.context, "Необходимо выбрать пункт выдачи", Toast.LENGTH_SHORT).show()
//                    }
//
//                }
//
//
//
//
//
//            } else {
//                // User not registered:
//                /*if the user not registered:
//                    show alert dialog:
//                    process the result (click dialog):
//                */
//                //display dialog:
//                val dlgView = LayoutInflater.from(it.context).inflate(R.layout.alert_dialog, null)
//
//                //settings for dlgView:
//                dlgView.some_addbar_view_back.visibility = View.INVISIBLE
//                dlgView.some_appbar_img_back.visibility = View.GONE
//
//                dlgView.dialog_appbar.setBackgroundColor(dlgView.dialog_appbar.resources.getColor(R.color.white))
//
//                // title:
//                dlgView.some_appbar_title.visibility = View.VISIBLE
//                dlgView.some_appbar_title.text = "Регистрация"
//                dlgView.some_appbar_title.setTextColor(dlgView.resources.getColor(R.color.type_black))
//
//                // timer:
//                dlgView.some_appbar_btn_clear.visibility = View.VISIBLE
//                dlgView.some_appbar_btn_clear.text = "10"
//                dlgView.some_appbar_btn_clear.setTextColor(dlgView.resources.getColor(R.color.error))
//
//                // body:
//                dlgView.dialog_text.text = "Для оформления заказов необходима регистрация.\n\nВ течение 10 сек. произойдет автоматический переход на страницу регистрации"
//                dlgView.dialog_big_button.visibility = View.GONE
//                dlgView.dialog_rv.visibility = View.GONE
//                dlgView.dialog_cancel.text = "Отмена"
//                dlgView.dialog_action.text = "Регистрация"
//
//
//
//                val builder = AlertDialog.Builder(it.context)
//                with(builder) {
//                    setView(dlgView)
//                }
//                val dlg = builder.show()
//
//                //show timet:
//                dialogTimerTick( dlgView, dlg, v.findFragment() )
//
//                //TODO click cancel:
//                dlgView.dialog_cancel.setOnClickListener {
//                    time?.cancel()
//                    dlg.dismiss()
//                }
//
//                //TODO click action:
//                dlgView.dialog_action.setOnClickListener {
//                    time?.cancel()
//                    dlg.dismiss()
//
//                    //go page quick registered:
//                    val profile_quick_registration: Fragment = profile_quick_registration.instance()
//                    varApp.supportFragmentManager.beginTransaction()
//                        .hide(v.findFragment())
//                        .add(R.id.nav_host_fragment, profile_quick_registration, "profile_quick_registration")
//                        .show(profile_quick_registration)
//                        .addToBackStack("profile_quick_registration")
//                        .commit()
//
//                }
//
//
//            }

        }


    }



    override fun noInternet() {

        checkout_pb.visibility = View.GONE
        checkout_page.visibility = View.GONE

        checkout_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as Main).supportActionBar!!,
            customAppBar = checkout_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = false,
            some_title_text = varApp.heading_no_internet,
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false)


        varApp.nav_view.visibility = View.GONE
        checkout_status.visibility = View.VISIBLE


    }

    override fun isEmpty() {

    }

    override fun CartToOrd(order: String) {

        Order = order

        //go to page order_registered:
        varApp.clearBackStack(varApp.supportFragmentManager)
        varApp.replaceFragment(varApp.supportFragmentManager, order_registered.instance(), addAnimation = true, animation = anim.from_bottom_in_top, addToBackStack = false)

    }


}





//region Adapter

class AdapterDialog : RecyclerView.Adapter<AdapterDialogVH>() {


    override fun getItemCount(): Int { return varApp.cities.map { f -> f.name }.size }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterDialogVH {
        return  AdapterDialogVH(  LayoutInflater.from(parent.context).inflate(R.layout.element_item_rv, parent, false) )
    }


    override fun onBindViewHolder(holder: AdapterDialogVH, position: Int) {

        val v = holder.itemView

        v.name_rv.text = varApp.cities.map { f -> f.name }[position]
        v.img_right_rv.visibility = View.GONE

        //устанавливаем по умолчанию из iam:
        if (varApp.cities[position].id == varApp.iam[0].cityid) {
            v.img_select_rv.visibility = View.VISIBLE
        } else {
            v.img_select_rv.visibility = View.GONE
        }




    }

}

class AdapterDialogVH(v: View) : RecyclerView.ViewHolder(v) {

    init {
        v.setOnClickListener {
            presenter.selectCity(adapterPosition)
        }

    }

}

//endregion


//region PRESENTER

@InjectViewState
class checkout_Presenter : MvpPresenter<Checkout_ViewState>() {

    private val disposeBag = CompositeDisposable()

    @Inject lateinit var backendModule: BackendModule


    fun CartToOrd () {
        varApp.appComponent.inject(this)

        var query = "${varApp.url}CardToOrd/"

        //Кто просит (физ лицо, юр.лицо)
        if (v.checkout_payment_on_delivery.isChecked || v.checkout_online_payment.isChecked ) { //физ лица или онлайн оплата
            query = query + "?tip=p"
        } else {
            query = query + "?tip=s"
        }

        //Способ доставки (доставка, самовывоз):
        if (v.checkout_method_receiving_Pickup_rb.isChecked) { //выбран самовывоз
            query = query + "&place=${placeId}"
        } else { //выбрана доставка
            query = (query + "&address=${v.checkout_city.text} " +
                    "${v.checkout_street.text} " +
                    "${v.checkout_house.text} " +
                    "${v.checkout_apartment.text}").replace(" ", "_")
        }

        //Способ оплаты:
        query = query + "&pay=бн"

        //Комментарий:
        if (v.checkout_comment.text.toString() != "") {
            query = "${query}&cmnt=${v.checkout_comment.text}".replace(" ", "_")
        }

        //sid:
        query = query + "&sid=${varApp.SID}"

        disposeBag.add(
            backendModule.provideRetrofit(url = query )
                .create(myGET::class.java)
                .setOk()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it != "") {
                        //возвращает номер order:
                        viewState.CartToOrd(it)
                    } else {
                        //TODO по хорошему надо показать страницу, что что-то пошло не так:

                    }

                },{
                    viewState.noInternet()
                })
        )

    }


    fun selectCity (adapterPosition: Int) {
        varApp.appComponent.inject(this)

        disposeBag.add(
            backendModule.provideMyGet().selectCity( varApp.cities[adapterPosition].id.toString(), varApp.SID )
                .delay(varApp.delay_default, varApp.typeUnit)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    varApp.setiam()
                    if (it.contains("ok")) {
                        dlg.dismiss()
                    }
                    v.checkout_address_delivery_page_1_city.text = varApp.iam[0].city
                    v.checkout_address_delivery_points.hint = "Выберите пункт выдачи заказа"
                    v.checkout_address_delivery_points.text = ""
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
