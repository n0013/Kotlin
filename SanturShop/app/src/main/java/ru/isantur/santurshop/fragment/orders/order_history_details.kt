package ru.isantur.santurshop.fragment.orders

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.ExpandableListView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
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
import kotlinx.android.synthetic.main.element_expandable_footer.view.*
import kotlinx.android.synthetic.main.element_expandable_group.view.*
import kotlinx.android.synthetic.main.element_expandable_item.view.*
import kotlinx.android.synthetic.main.element_item_rv.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_order_history.*
import kotlinx.android.synthetic.main.include_appbar.*
import kotlinx.android.synthetic.main.include_appbar.view.*
import kotlinx.android.synthetic.main.include_status.*
import kotlinx.android.synthetic.main.order_history_details.*
import kotlinx.android.synthetic.main.order_history_details_rv.view.*
import kotlinx.android.synthetic.main.order_registered.*
import moxy.InjectViewState
import moxy.MvpAppCompatFragment
import moxy.MvpPresenter
import moxy.MvpView
import moxy.presenter.InjectPresenter
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.isantur.santurshop.Data.Details
import ru.isantur.santurshop.Data.OrderItem
import ru.isantur.santurshop.Data.OrderList
import ru.isantur.santurshop.Data.Orders
import ru.isantur.santurshop.R
import ru.isantur.santurshop.activity.Main
import ru.isantur.santurshop.di.BackendModule
import ru.isantur.santurshop.fragment.Popular
import ru.isantur.santurshop.fragment.catalog.catalog_sub
import ru.isantur.santurshop.fragment.orders.order_history.Companion.orderList
import ru.isantur.santurshop.fragment.orders.order_history_details.Companion.details
import ru.isantur.santurshop.fragment.products.*
import ru.isantur.santurshop.varApp
import javax.inject.Inject


@StateStrategyType(value = OneExecutionStateStrategy::class)
interface OrderHistoryDetails_ViewState: MvpView {

    fun onSuccess()
    fun noInternet()
    fun isEmpty()

}


class order_history_details : MvpAppCompatFragment(), OrderHistoryDetails_ViewState {

    @InjectPresenter lateinit var order_history_details_presenter: order_history_details_Presenter

    companion object {

        fun instance(args: Bundle? = null): Fragment {
            val ohd = order_history_details()
            ohd.arguments = args
            return ohd
        }


        lateinit var v: View
        var orderList: OrderList? = null
        lateinit var details: Details

    }




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        v = inflater.inflate(R.layout.order_history_details, container, false)

        return v
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onLoad()
        onEvents()

    }


    fun onEvents() {

        //TODO click update:
        status_update.setOnClickListener {
            onLoad()
            onEvents()
        }

        //TODO click back:
        some_addbar_view_back.setOnClickListener {
            varApp.delete_step_from_BackStack(varApp.supportFragmentManager, this::class.java.simpleName)
        }

    }

    fun onLoad() {
        oh_page.visibility = View.GONE
        oh_page_appbar.visibility = View.GONE
        oh_page_status.visibility = View.GONE
        varApp.nav_view.visibility = View.GONE
        oh_page_pb.visibility = View.VISIBLE

        order_history_details_presenter.ord ( arguments?.getString("id")!! )

    }

    @SuppressLint("SetTextI18n", "UseCompatLoadingForDrawables")
    override fun onSuccess() {

        oh_page_pb.visibility = View.GONE
        oh_page_status.visibility = View.GONE

        oh_page.visibility = View.VISIBLE

        oh_page_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as AppCompatActivity).supportActionBar!!,
            customAppBar = oh_page_appbar,
            customAppBar_backgroundColor =  R.color.white,
            some_appbar_img_back = true,
            some_title_text =  "",
            some_title_textColor = R.color.type_black,
            some_btn_clear_text = "",
            some_btn_clear_textColor =  R.color.type_black,
            some_btn_clear_visible = false

        )

        varApp.nav_view.visibility = View.VISIBLE

        oh_page_appbar.some_appbar_img_back.setImageDrawable(oh_page_appbar.resources.getDrawable(R.drawable.ic_close))


        oh_title.text = "Заказ №${details.id} от ${details.regdate}"
        oh_status.text = varApp.firstUpperCase( word = details.status )
        oh_product_title.text = "Товары (${details.qty_goodies})"

        //list adapter:
        oh_product_rv.setHasFixedSize(true)
        oh_product_rv.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        oh_product_rv.adapter = Adapter_OHD()
        oh_product_rv.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))


        oh_product_n.text = varApp.RoundToInteger(details.summary.toString(), true).toString()
        if (details.dcballs == "") {
            oh_balls_n.text = varApp.RoundToInteger(0.toString(), true).toString()
        } else {
            oh_balls_n.text = varApp.RoundToInteger(details.dcballs, true).toString()
        }
        oh_discount_n.text = details.dcskid
        oh_total_n.text = varApp.RoundToInteger(details.summary.toString(), true).toString()
        oh_delivery_points_txt.text = varApp.firstUpperCase( word =  details.receiving_method )
        oh_delivery_points.text = details.receiving_address



    }

    override fun noInternet() {

        oh_page_pb.visibility = View.GONE
        oh_page.visibility = View.GONE

        oh_page_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as Main).supportActionBar!!,
            customAppBar = oh_page_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = false,
            some_title_text = varApp.heading_no_internet,
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false
        )

        varApp.nav_view.visibility = View.GONE
        oh_page_status.visibility = View.VISIBLE

    }

    override fun isEmpty() {

        oh_page_pb.visibility = View.GONE
        oh_page.visibility = View.GONE

        oh_page_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as AppCompatActivity).supportActionBar!!,
            customAppBar = oh_page_appbar,
            customAppBar_backgroundColor =  R.color.primary,
            some_appbar_img_back = false,
            some_title_text =  "История заказов",
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor =  R.color.white,
            some_btn_clear_visible = false

        )

        varApp.nav_view.visibility = View.VISIBLE

        oh_page_status.visibility = View.VISIBLE
        status_title.text = "По данному заказу позиции не найдены"
        status_body.text = "По данному заказу позиции не найдены."
        status_update.visibility = View.GONE

    }




}





//region Adapter

class Adapter_OHD : RecyclerView.Adapter<Adapter_OHD_VH>() {

    override fun getItemCount(): Int { return details.items.size }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Adapter_OHD_VH {
        return  Adapter_OHD_VH(LayoutInflater.from(parent.context).inflate(R.layout.order_history_details_rv, parent, false))
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Adapter_OHD_VH, position: Int) {

        val v = holder.itemView

        v.oh_rv_name.text = details.items[position].name
        v.oh_rv_prices.text = varApp.RoundToInteger(details.items[position].price.toString(), true).toString()
        v.oh_rv_received.text = "получено: ${details.items[position].realized.toInt()} из ${details.items[position].qty.toInt()} ${details.items[position].ed} "
        Glide.with(v.context).load(details.items[position].imgpath).into(v.oh_rv_img)

    }

}

class Adapter_OHD_VH(v: View) : RecyclerView.ViewHolder(v) {

    init {
        v.setOnClickListener {
            val product: Fragment = product.instance(
                bundleOf(
                    "code" to details.items[adapterPosition].code
                )
            )

            varApp.supportFragmentManager.beginTransaction()
                //.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_in_up)
                .hide(it.findFragment())
                .add(R.id.nav_host_fragment, product, "product")
                .show(product)
                .addToBackStack("product")
                .commit()
        }
    }

}



//endregion



//region PRESENTER

@InjectViewState
class order_history_details_Presenter : MvpPresenter<OrderHistoryDetails_ViewState>() {

    private val disposeBag = CompositeDisposable()

    @Inject lateinit var backendModule: BackendModule

    fun ord (id: String) {

        varApp.appComponent.inject(this)

        disposeBag.add(
            backendModule.provideMyGet().getord(id, varApp.SID)
                .delay(varApp.delay_default, varApp.typeUnit)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    if (it.items.size != 0) {
                        order_history_details.details = it
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
