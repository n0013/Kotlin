package ru.isantur.santurshop.fragment.map


import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.element_item_map_rv.view.*
import kotlinx.android.synthetic.main.fragment_checkout.view.*
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.android.synthetic.main.fragment_map.view.*
import kotlinx.android.synthetic.main.include_status.*
import moxy.InjectViewState
import moxy.MvpAppCompatFragment
import moxy.MvpPresenter
import moxy.MvpView
import moxy.presenter.InjectPresenter
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.isantur.santurshop.Data.Company
import ru.isantur.santurshop.R
import ru.isantur.santurshop.activity.Main
import ru.isantur.santurshop.di.BackendModule
import ru.isantur.santurshop.fragment.checkout.checkout
import ru.isantur.santurshop.fragment.map.map.Companion.arguments
import ru.isantur.santurshop.fragment.map.map.Companion.arrayCompany
import ru.isantur.santurshop.fragment.map.map.Companion.sel_company
import ru.isantur.santurshop.fragment.map.map.Companion.v
import ru.isantur.santurshop.fragment.map.map.Companion.ya_view
import ru.isantur.santurshop.varApp
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject


interface show_hide_write_page {

    fun show_hide(company: Company) {


        //hide 1 window:
        v.map_page_1.visibility = View.GONE

        //show 2 window:
        v.map_page_2.animate().alpha(1F).setDuration(500).start()
        v.map_page_2.visibility = View.VISIBLE

        v.map_title_2.text = company.address
        v.map_payvariants_2.text = company.payvariants
        v.map_opening_hours_2.text = company.times
        v.map_phone_2.text = company.phones

        //show button select:
        if (arguments?.getString("from") == "checkout") {
            v.map_select_btn_2.visibility = View.VISIBLE
        } else {
            v.map_select_btn_2.visibility = View.GONE
        }


    }

}


class JavaScriptInterface internal constructor(context: Context) {
    var mContext: Context
    @JavascriptInterface
    fun showToast(coord: String? = null) {


        sel_company = arrayCompany.filter { f -> f.gpscoords == coord }[0]

        val intent = Intent()
            .putExtra("from", "network")
            .putExtra("sign", false)
        val pi = PendingIntent.getBroadcast(this.mContext, 0, intent, 0);
        pi.send(this.mContext, 1, intent)



    }

    init {

        mContext = context
    }

}




@StateStrategyType(value = OneExecutionStateStrategy::class)
interface Map_ViewState: MvpView {

    fun onSuccess()
    fun noInternet()
    fun isEmpty()

}



class map : MvpAppCompatFragment(), Map_ViewState {


    @InjectPresenter lateinit var map_presenter: map_Presenter

    companion object {

        fun instance (): MvpAppCompatFragment {
            return map()
        }

        fun instance(args: Bundle?): MvpAppCompatFragment {
            val Map = map()
            arguments = args
            Map.arguments = args
            return Map
        }


        lateinit var v: View

        lateinit var ya_view: WebView;


        var sel_company: Company? = null

        var arrayCompany = ArrayList<Company>()



        lateinit var bsb: BottomSheetBehavior<ConstraintLayout>

        var current_placeId = 0

        lateinit var mapFragment: Fragment

        var arguments: Bundle? = null

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        println("ok")
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        mapFragment = this

        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        v = inflater.inflate(R.layout.fragment_map, container, false)

        return v

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onLoad()

        onEvents()

    }

    fun refreshList() {
        println("refreshList")

    }

    fun onEvents() {

        //TODO click back:
        map_appbar_view_back.setOnClickListener {

            if (arguments?.getString("from") == "other"  ) {

                if ( map_page_1.visibility == View.GONE ) {

                    ya_view.loadUrl("javascript:backMoveCamera()");

                    map_page_1.visibility = View.VISIBLE
                    bsb.state = BottomSheetBehavior.STATE_COLLAPSED

                    map_page_1.animate().alpha(1F).setDuration(500).start()
                    map_page_2.animate().alpha(0F).setDuration(500).start()



                } else {

                    varApp.delete_step_from_BackStack(varApp.supportFragmentManager, this::class.java.simpleName)

                }

            }

            if (arguments?.getString("from") == "checkout") {

                if ( map_page_1.visibility == View.GONE ) {

                    ya_view.loadUrl("javascript:backMoveCamera()");

                    map_page_1.visibility = View.VISIBLE
                    bsb.state = BottomSheetBehavior.STATE_COLLAPSED

                    map_page_1.animate().alpha(1F).setDuration(500).start()
                    map_page_2.animate().alpha(0F).setDuration(500).start()

                    //                    mapview.map.move(
                    //                        CameraPosition(Point(mapCoord[0].split(":")[0].toDouble(), mapCoord[0].split(":")[1].toDouble() ), 11.0f, 0.0f, 0.0f), Animation(Animation.Type.SMOOTH, duration), null)

                } else {

                    varApp.supportFragmentManager.beginTransaction()
                        .detach(mapFragment)
                        .hide(mapFragment)
                        .show(checkout.checkoutFragment)
                        .commit()

                    //                    varApp.delete_step_from_BackStack(varApp.supportFragmentManager, this::class.java.simpleName)

                }
            }



        }

        //TODO click map_img_UpDown (развернуть, свернуть список):
        map_img_UpDown.setOnClickListener {

            if (bsb.state == BottomSheetBehavior.STATE_EXPANDED) {
                bsb.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                bsb.state = BottomSheetBehavior.STATE_EXPANDED
            }

        }

        //TODO click update:
        status_update.setOnClickListener {
            onLoad()
            onEvents()
        }

        //TODO click select:
        map_select_btn_2.setOnClickListener {

            checkout.v.checkout_address_delivery_points.text = sel_company!!.address
            checkout.placeId = sel_company!!.id
            varApp.supportFragmentManager.beginTransaction()
                .detach(mapFragment)
                .hide(mapFragment)
                .show(checkout.checkoutFragment)
                .commit()
        }

    }

    fun onLoad () {

        ya_view = ya_page


        map_pb.visibility = View.VISIBLE
        map_coordinator.visibility = View.GONE
        varApp.nav_view.visibility = View.GONE



        if ( arguments?.getString("from") == "other" ) {
            map_select_btn_2.visibility = View.GONE
        }


        //TODO init map (yandex), mapObject, bm for adapter:
        ya_view.settings.javaScriptEnabled = true
        ya_view.loadUrl("file:///android_asset/ya.html");

        ya_view.addJavascriptInterface(JavaScriptInterface(ya_view.context), "AndroidFunction")
        map_presenter.companyinfo()


    }
//
    override fun onSuccess() {

        map_pb.visibility = View.GONE
        map_appbar.visibility = View.GONE
        map_status.visibility = View.GONE

        map_coordinator.visibility = View.VISIBLE
        varApp.nav_view.visibility = View.VISIBLE



        //SET adapter:
        map_rv_1.setHasFixedSize(true)
        map_rv_1.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        map_rv_1.adapter = AdapterMap()
        map_rv_1.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))


        // настройка поведения нижнего экрана
        bsb  = BottomSheetBehavior.from(map_page_1)

        // настройка состояний нижнего экрана
        bsb.state = BottomSheetBehavior.STATE_COLLAPSED

        // настройка возможности скрыть элемент при свайпе вниз
        bsb.isHideable = false

        bsb.setBottomSheetCallback(bsbCallback())


    }

    override fun noInternet() {

        map_pb.visibility = View.GONE
        map_coordinator.visibility = View.GONE
        ya_view.visibility = View.GONE

        map_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as Main).supportActionBar!!,
            customAppBar = map_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = false,
            some_title_text = varApp.heading_no_internet,
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false)

        map_status.visibility = View.VISIBLE

        varApp.nav_view.visibility = View.GONE

    }

    override fun isEmpty() {

    }


}




class bsbCallback: BottomSheetBehavior.BottomSheetCallback() {
    override fun onStateChanged(bottomSheet: View, newState: Int) {
        if (BottomSheetBehavior.STATE_DRAGGING == newState) {
//            v.map_img.setImageDrawable(v.context.resources.getDrawable(R.mipmap.ic_down))
//            v.map_img.animate().scaleX(1F).scaleY(1F).setDuration(300).start()

        } else if (BottomSheetBehavior.STATE_COLLAPSED == newState) {   //скрыли и закрепили
            v.map_img_UpDown.setImageDrawable(v.context.resources.getDrawable(R.drawable.ic_up))

//            v.map_img.animate().scaleX(1F).scaleY(1F).setDuration(300).start();
//            v.map_constr_behavior.layoutParams = CoordinatorLayout.LayoutParams (ViewGroup.LayoutParams.MATCH_PARENT, (340 * varApp.convertPxDp).toInt())
//            bsb.setBottomSheetCallback(bsbCallback())
//
        } else if (BottomSheetBehavior.STATE_EXPANDED == newState) {    //показали и закрепили
            v.map_img_UpDown.setImageDrawable(v.context.resources.getDrawable(R.drawable.ic_down))
//            v.map_constr_behavior.layoutParams = CoordinatorLayout.LayoutParams (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
////            bsb.setBottomSheetCallback(bsbCallback())
        }

    }

    override fun onSlide(bottomSheet: View, slideOffset: Float) {
//        v.map_img.animate().scaleX(1 - slideOffset).scaleY(1 - slideOffset).setDuration(0).start();
    }

}   //class bsbCallback





//region Adapter

class AdapterMap : RecyclerView.Adapter<AdapterMapVH>() {

    override fun getItemCount(): Int { return arrayCompany.size }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterMapVH {
        return  AdapterMapVH(LayoutInflater.from(parent.context).inflate(R.layout.element_item_map_rv, parent, false))
    }

    override fun onBindViewHolder(holder: AdapterMapVH, position: Int) {

        val v = holder.itemView

        v.name_map_rv.text = arrayCompany[position].address
        v.name_map2_rv.text = arrayCompany[position].times


    }

}

class AdapterMapVH(v: View) : RecyclerView.ViewHolder(v), show_hide_write_page {

    //навешиваем события
    init {
        v.setOnClickListener {

            sel_company = arrayCompany[adapterPosition]
            ya_view.loadUrl("javascript:moveCamera('${ Gson().toJson( arrayCompany[adapterPosition] ) }')");

            show_hide(arrayCompany[adapterPosition])

        }
    }





}


//endregion




//region PRESENTER

@InjectViewState
class map_Presenter : MvpPresenter<Map_ViewState>() {

    private val disposeBag = CompositeDisposable()

    @Inject lateinit var backendModule: BackendModule


    fun companyinfo () {
        varApp.appComponent.inject(this)


        disposeBag.add(backendModule.provideMyGet().companyinfo(varApp.SID)
            .delay(5, TimeUnit.SECONDS)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
            if (it.size != 0) {
                arrayCompany = it
                arrayCompany.forEach { company ->
                    ya_view.loadUrl("javascript:initCity('${Gson().toJson(company)}')");
                }
                viewState.onSuccess()
            } else {
                viewState.isEmpty()
            }
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
