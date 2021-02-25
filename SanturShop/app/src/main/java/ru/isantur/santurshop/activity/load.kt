package ru.isantur.santurshop.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import khttp.get
import kotlinx.android.synthetic.main.include_status.*
import kotlinx.android.synthetic.main.screen_loading.*
import kotlinx.android.synthetic.main.screen_loading.view.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import moxy.InjectViewState
import moxy.MvpAppCompatActivity
import moxy.MvpPresenter
import moxy.MvpView
import moxy.presenter.InjectPresenter
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import nl.komponents.kovenant.combine.Tuple8
import nl.komponents.kovenant.combine.combine
import nl.komponents.kovenant.task

import org.json.JSONArray
import ru.isantur.santurshop.activity.load.Companion.arrayBanners
import ru.isantur.santurshop.activity.load.Companion.arrayBrands
import ru.isantur.santurshop.activity.load.Companion.arrayCities
import ru.isantur.santurshop.activity.load.Companion.arrayPopulars
import ru.isantur.santurshop.activity.load.Companion.arraySales

import ru.isantur.santurshop.Data.*
import ru.isantur.santurshop.R

import ru.isantur.santurshop.varApp
import java.util.*


@StateStrategyType(value = OneExecutionStateStrategy::class)
interface Load_ViewState: MvpView {

    fun go_to_Main()
    fun go_to_SelectCity()
    fun noInternet ()

}




class load : MvpAppCompatActivity(), Load_ViewState  {

    @InjectPresenter lateinit var load_presenter: load_Presenter

    companion object {

        var arraySales: ArrayList<Sales> = ArrayList<Sales>()
        var arrayPopulars: ArrayList<Populars> = ArrayList<Populars>()
        var arrayBrands: ArrayList<Brands> = ArrayList<Brands>()
        var arrayBanners: ArrayList<Banners> = ArrayList<Banners>()
        var iam: ArrayList<Iam> = ArrayList<Iam>()
        var arrayCities: ArrayList<Cities> = ArrayList<Cities>()

    }


    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)


        setContentView(R.layout.screen_loading)




    } //onCreate





    override fun onResume() {
        super.onResume()

        supportActionBar!!.hide()

        //click update:
        status_update.setOnClickListener {

            if (varApp.sales.size == 0) {

                if (varApp.isNetwork(this)) {  //have internet:

                    load_presenter.showOnLoad(load_view)
                    load_presenter.onLoad()

                } else {        //not internet:
                    noInternet()
                }

            } else {
                startActivity(Intent(applicationContext, Main::class.java))
            }

        } //click status_update


        if (varApp.sales.size == 0) {

            if (varApp.isNetwork(this)) {  //have internet:

                load_presenter.showOnLoad(load_view)
                load_presenter.onLoad()

            } else {        //not internet:

                noInternet()
                return
            }


        } else {
            startActivity(Intent(applicationContext, Main::class.java))
        }







    } //onResume







    override fun go_to_Main() {
        startActivity(Intent(applicationContext, Main::class.java))
    }

    override fun go_to_SelectCity() {
        startActivity(Intent(applicationContext, select_city::class.java))
    }

    override fun noInternet() {

        load_base_page.visibility = View.GONE

        load_second_page.visibility = View.VISIBLE

        load_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = supportActionBar!!,
            customAppBar = load_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = false,
            some_title_text = "No Internet",
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false)


    }





}






//region PRESENTER

@InjectViewState
class load_Presenter : MvpPresenter<Load_ViewState>() {
    
    fun showOnLoad (v: View) {
        v.load_base_page.visibility = View.VISIBLE
        v.load_second_page.visibility = View.GONE
//        YoYo.with(Techniques.RollIn).duration(1000).playOn(v.load_logo);
    }


    fun onLoad () {

        varApp.appComponent.inject(this)


        combine(
            //Connect:
            task {
                if (varApp.getSessionId_Preferences() != "") {
                    varApp.setSID(varApp.getSessionId_Preferences())
                    varApp.firstLoad = false
                } else {
                    varApp.firstLoad = true
                    val sessionId_new = get("${varApp.url}connect").text
                    varApp.setSID(sessionId_new)
                    varApp.setSessionId_Preferences(sessionId_new)
                }
            },
            //Login:
            task {
                if (varApp.getLogin_Preferences() != "") {
                    val js = khttp.get("${varApp.url}login/?u=${varApp.getLogin_Preferences()}&p=${varApp.getPassword_Preferences()}&sid=${varApp.SID}").text
                    if (js.contains("ok")) {
                        println("login and password correct!!!")
                    }
                }
            },
            task { get("${varApp.url}GetSales/").jsonArray },
            task { get("${varApp.url}GetRecomendTKs/").jsonArray },
            task { get("${varApp.url}getbrendsa/").jsonArray },
            task { get("${varApp.url}getbannersa/").jsonArray },
            task { varApp.setiam() },
            task { get("${varApp.url}getcities/").jsonArray }

        ) success {

                response: Tuple8<Unit, Unit, JSONArray, JSONArray, JSONArray, JSONArray, Unit, JSONArray> ->

            //1. Connect

            //2. Login


            //3 Sales:
            arraySales = Json.decodeFromString(response.third.toString())
            varApp.setSales(arraySales)

            //4 Populars:
            arrayPopulars = Json.decodeFromString(response.fourth.toString())
            varApp.setPopulars(arrayPopulars)

            //5 Brands:
            arrayBrands = Json.decodeFromString(response.fifth.toString())
            varApp.setBrands(arrayBrands)

            //6 Banners:
            arrayBanners = Json.decodeFromString(response.sixth.toString())
            varApp.setBanners(arrayBanners)

            //7 setIam:

            //8 Cities:
            arrayCities = Json.decodeFromString(response.eighth.toString())
            varApp.setCities(arrayCities)


            if (varApp.firstLoad) {
                if (varApp.iam[0].id == 0) { //не залогинен. start SelectSity
                    viewState.go_to_SelectCity()

                } else { //залогинен. start main:
                    viewState.go_to_Main()
                }
            } else {
                viewState.go_to_Main()
            }


        } fail { errors ->
            viewState.noInternet()
        }




    } //get_data_on_load


}

//endregion