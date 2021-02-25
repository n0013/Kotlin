package ru.isantur.santurshop.fragment

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.slider.RangeSlider
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.element_item_rv.view.*
import kotlinx.android.synthetic.main.include_appbar.*
import kotlinx.android.synthetic.main.include_status.*
import kotlinx.android.synthetic.main.screen_filters.*
import moxy.InjectViewState
import moxy.MvpAppCompatFragment
import moxy.MvpPresenter
import moxy.MvpView
import moxy.presenter.InjectPresenter
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import org.florescu.android.rangeseekbar.RangeSeekBar
import org.json.JSONArray
import org.json.JSONObject
import ru.isantur.santurshop.Data.GoodFilters
import ru.isantur.santurshop.fragment.products.product_list
import ru.isantur.santurshop.R
import ru.isantur.santurshop.Retrofit.myGET
import ru.isantur.santurshop.activity.Main
import ru.isantur.santurshop.di.BackendModule
import ru.isantur.santurshop.varApp
import javax.inject.Inject

@StateStrategyType(value = OneExecutionStateStrategy::class)
interface Filters_ViewState: MvpView {

    fun onSuccess()
    fun noInternet ()
    fun onRedirectScreen()


}

class filters:  MvpAppCompatFragment(), Filters_ViewState {

    @InjectPresenter lateinit var filters_presenter: filters_Presenter


    lateinit var jsArray: JSONArray
    lateinit var jsObject: JSONObject


    companion object {

        fun instance(args: Bundle?): Fragment {
            val filters = filters()
            filters.arguments = args
            return filters
        }

        lateinit var n: List<GoodFilters>
        lateinit var s: List<GoodFilters>

        lateinit var v: View
        var bundle: Bundle? = null


    }




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        v = inflater.inflate(R.layout.screen_filters, container, false)

        return v

    }



    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)


            onSuccess()
            onEvents()


            bundle = savedInstanceState

            filter_btn_filter.text = "Показать   ${product_list.products!!.qty_records}  тов."

            //n:
            n = product_list.products.GoodFilters.filter { f -> f.IsNumeric }
            if (n.size != 0) {
                var nn = 0
                n.forEach { nfilter ->

                    //vertical linearLayout:
                    val llvertical = LinearLayout(v.context)
                    llvertical.orientation = LinearLayout.VERTICAL
                    val llverticalParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)

                    //TextView params:
                    val textViewP = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
                    textViewP.setMargins(30, 30, textViewP.rightMargin, textViewP.bottomMargin)




                        //TextView (Цена, Ду и др):
                        val textView = TextView(v.context)
                        textView.id = View.generateViewId()
                        textView.text = "${nfilter.Name}"

//                        textView.setTypeface(null, Typeface.BOLD)
                        textView.typeface = Typeface.createFromAsset(requireActivity().assets, "font/OpenSans_Bold.ttf")

                        textView.textSize = 16.0F
                        textView.setTextColor(Color.BLACK)

                        //add TextView in LinearLayout:
                        llvertical.addView(textView, textViewP)






                        //RangeSeekBar:
                        val rsbP = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT)

                        val rsbb = com.google.android.material.slider.RangeSlider(v.context)
                        rsbb.valueFrom = (varApp.RoundToInteger(nfilter.MinLimit, false, "Double") as Double).toFloat()
                        rsbb.valueTo = (varApp.RoundToInteger(nfilter.MaxLimit, false, "Double") as Double).toFloat()
//                        rsbb.stepSize = 1.0F

//                        val rsb  = RangeSeekBar<Int>(v.context)
//                        //Min and Max (value):
//                        rsb.setRangeValues(  varApp.RoundToInteger(nfilter.MinLimit, false, "Int") as Int, varApp.RoundToInteger(nfilter.MaxLimit, false, "Int") as Int )


                        val numberList: MutableList<Float> = mutableListOf()
                        //Select min:
                        if (nfilter.MinSelect != "") {
//                            rsb.selectedMinValue = varApp.RoundToInteger(nfilter.MinSelect, false, "Int") as Int
                            numberList.add(0, (varApp.RoundToInteger(nfilter.MinSelect, false, "Double") as Double).toFloat() )
                        } else {
//                            rsb.selectedMinValue = varApp.RoundToInteger(nfilter.MinLimit, false, "Int") as Int
                            numberList.add(0, (varApp.RoundToInteger(nfilter.MinLimit, false, "Double") as Double).toFloat() )
                        }


                        //Select max:
                        if (nfilter.MaxSelect != "") {
//                            rsb.selectedMaxValue = varApp.RoundToInteger(nfilter.MaxSelect, false, "Int") as Int
                            numberList.add(1, (varApp.RoundToInteger(nfilter.MaxSelect, false, "Double") as Double).toFloat() )
                        } else {
//                            rsb.selectedMaxValue = varApp.RoundToInteger(nfilter.MaxLimit, false, "Int") as Int
                            numberList.add(1, (varApp.RoundToInteger(nfilter.MaxLimit, false, "Double") as Double).toFloat() )
                        }

                        rsbb.setValues(numberList)

//                        //TODO events RangeSeekBar:
//                        rsb.setOnRangeSeekBarChangeListener { bar, minValue, maxValue ->
//                            show_btn_progress_bar()
//                            filters_presenter.clearfilter_min_max( ((bar.parent as ViewGroup).get(0) as TextView).text.toString().toLowerCase(), minValue.toString(), maxValue.toString() )
//                        }

                        //TODO events RangeSeekBar:
                        rsbb.addOnSliderTouchListener(object : RangeSlider.OnSliderTouchListener {
                            override fun onStartTrackingTouch(slider: RangeSlider) {
                                // Responds to when slider's touch event is being started
                            }

                            override fun onStopTrackingTouch(slider: RangeSlider) {
                                show_btn_progress_bar()
                                filters_presenter.clearfilter_min_max(
                                    ((slider.parent as ViewGroup).get(0) as TextView).text.toString().toLowerCase(),
                                    slider.values[0].toString(),
                                    slider.values[1].toString()
                                )
                            }
                        })






                    llvertical.addView(rsbb, rsbP)

                    filter_ll.addView(llvertical, llverticalParams)

                    nn = nn + 1

                } //forEach

            } //if




            //s:

            s = product_list.products.GoodFilters.filter { f -> !f.IsNumeric }

            if (s.size != 0) {


                //lv params:
                val lvP = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT) //create lv params
                lvP.topMargin = 40

                val ar = s.map { m -> m.Name }.toTypedArray() //array for transfer in adapter



                val rv = RecyclerView(v.context)
                rv.setHasFixedSize(true)
                rv.layoutManager = LinearLayoutManager( home.v.context, LinearLayoutManager.VERTICAL, false)
                rv.adapter = AdapterFilters_rv(rv, ar)
                rv.addItemDecoration(DividerItemDecoration(home.v.context, DividerItemDecoration.VERTICAL))     //добавляет нижнюю черту для отделения элементов


                filter_ll.addView(rv, lvP)



            }






    }



    override fun onRedirectScreen() {
        onSuccess()

        filter_ll.removeAllViews()
        onViewCreated(v, bundle)
    }


    fun onEvents() {

        //TODO clear filters click:
        some_appbar_btn_clear.setOnClickListener { btn ->

            show_btn_progress_bar()

            filters_presenter.clearfilter()


        }

        // TODO show products lists click:
        filter_btn_filter.setOnClickListener {
            varApp.replaceFragment(varApp.supportFragmentManager, product_list.instance( bundleOf("from" to "filters") ), addAnimation = false, addToBackStack = true)
        }


        //TODO Back click:
        some_addbar_view_back.setOnClickListener {
            varApp.replaceFragment(varApp.supportFragmentManager, product_list.instance( bundleOf("from" to "filters") ), addAnimation = false, addToBackStack = true)
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

        filter_pb.visibility = View.GONE
        filter_status.visibility = View.GONE

        filter_page.visibility = View.VISIBLE
        filter_btn_filter.visibility = View.VISIBLE
        filter_btn_pb.visibility = View.GONE

        filter_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as Main).supportActionBar!!,
            customAppBar = filter_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = true,
            some_title_text = "Фильтры",
            some_title_textColor = R.color.white,
            some_btn_clear_text = "Очистить",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = true
        )

        varApp.nav_view.visibility = View.VISIBLE
    }


    override fun noInternet() {

        filter_pb.visibility = View.GONE
        filter_page.visibility = View.GONE

        filter_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as Main).supportActionBar!!,
            customAppBar = filter_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = false,
            some_title_text = varApp.heading_no_internet,
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false)


        varApp.nav_view.visibility = View.GONE
        filter_status.visibility = View.VISIBLE

    }


    fun show_btn_progress_bar () {

        filter_btn_filter.visibility = View.GONE
        filter_btn_pb.visibility = View.VISIBLE

    }



}




class AdapterFilters_rv (rv: RecyclerView, arr_name: Array<String>) : RecyclerView.Adapter<AdapterFiltersVH>() {
    var arr_name: Array<String>? = null

    init { this.arr_name = arr_name }


    override fun getItemCount(): Int { return arr_name!!.size }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterFiltersVH {
        return  AdapterFiltersVH(  LayoutInflater.from(parent.context).inflate(R.layout.element_item_rv, parent, false) )
    }


    override fun onBindViewHolder(holder: AdapterFiltersVH, position: Int) {

        holder.itemView.name_rv.text = arr_name!![position]

        holder.itemView.name2_rv.visibility = View.VISIBLE
        val plfilter = filters.s.filter { f -> f.Selected != "" }
        if (plfilter.size == 0) {
            holder.itemView.name2_rv.text = 0.toString()
        }

        for (i in  0 .. plfilter.size -1  ) {

            val lselect =  plfilter[i].Selected.split(";")

            if ( plfilter[i].Name == arr_name!![position] ) {
                holder.itemView.name2_rv.text = lselect.size.toString()
                break
            } else {
                holder.itemView.name2_rv.text = 0.toString()
            }

        }

        holder.itemView.switch_rv.visibility = View.GONE

    }


}

class AdapterFiltersVH(v: View) : RecyclerView.ViewHolder(v) {

    //навешиваем события
    init {

        v.setOnClickListener {

            val filter = product_list.products.GoodFilters.filter { f -> !f.IsNumeric }.sortedWith(compareBy(GoodFilters::Name))[adapterPosition]
            val items = filter.Items

            if (items.size != 0) {

                varApp.replaceFragment(varApp.supportFragmentManager, filters_child.instance(
                    bundleOf(
                        "position" to adapterPosition,
                        "name" to filter.Name
                    )
                ), addAnimation = false, addToBackStack = true)

            } else {
                Toast.makeText(v.context, "Для текущей позиции нет фильтра", Toast.LENGTH_SHORT).show()
            }

        }

    }



}







//region PRESENTER

@InjectViewState
class filters_Presenter : MvpPresenter<Filters_ViewState>() {

    private val disposeBag = CompositeDisposable()

    @Inject lateinit var backendModule: BackendModule


    fun clearfilter () {
        varApp.appComponent.inject(this)

        disposeBag.add(
            backendModule.provideRetrofit(url = "${varApp.url}clearfilter/?flt=all&sid=${varApp.SID}").create(myGET::class.java) .setOk()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    backendModule.provideMyGet().getGoodList("1", varApp.SID)
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({

                            product_list.products = it
                            viewState.onRedirectScreen()

                        }, {
                            viewState.noInternet()
                        })

                },{
                    viewState.noInternet()
                })


        )

    }

    fun clearfilter_min_max (what: String, minPrice: String, maxPrice: String) {
        varApp.appComponent.inject(this)


        disposeBag.add(
            backendModule.provideRetrofit(url = "${varApp.url}setfiltermin/?flt=${what}&val=${minPrice}&sid=${varApp.SID}").create(myGET::class.java) .setOk()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    backendModule.provideRetrofit(url = "${varApp.url}setfiltermax/?flt=${what}&val=${maxPrice}&sid=${varApp.SID}").create(myGET::class.java) .setOk()
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({

                            backendModule.provideMyGet().getGoodList("1", varApp.SID)
                                .subscribeOn(Schedulers.computation())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({

                                    product_list.products = it
                                    viewState.onRedirectScreen();

                                }, {
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

    override fun onDestroy() {
        super.onDestroy()
        disposeBag.dispose()
        disposeBag.clear()
    }

}

//endregion
