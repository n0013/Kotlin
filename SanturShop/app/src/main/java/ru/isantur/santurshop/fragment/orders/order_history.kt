package ru.isantur.santurshop.fragment.orders

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
import com.bumptech.glide.Glide
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.element_expandable_footer.view.*
import kotlinx.android.synthetic.main.element_expandable_group.view.*
import kotlinx.android.synthetic.main.element_expandable_item.view.*
import kotlinx.android.synthetic.main.element_item_rv.view.*
import kotlinx.android.synthetic.main.fragment_order_history.*
import kotlinx.android.synthetic.main.include_appbar.*
import kotlinx.android.synthetic.main.include_status.*
import moxy.InjectViewState
import moxy.MvpAppCompatFragment
import moxy.MvpPresenter
import moxy.MvpView
import moxy.presenter.InjectPresenter
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.isantur.santurshop.Data.OrderItem
import ru.isantur.santurshop.Data.OrderList
import ru.isantur.santurshop.Data.Orders
import ru.isantur.santurshop.R
import ru.isantur.santurshop.activity.Main
import ru.isantur.santurshop.di.BackendModule
import ru.isantur.santurshop.fragment.orders.order_history.Companion.orderList
import ru.isantur.santurshop.fragment.products.*
import ru.isantur.santurshop.varApp
import javax.inject.Inject


@StateStrategyType(value = OneExecutionStateStrategy::class)
interface OrderHistory_ViewState: MvpView {

    fun onSuccess()
    fun noInternet()
    fun isEmpty()

}


class order_history : MvpAppCompatFragment(), OrderHistory_ViewState {

    @InjectPresenter lateinit var order_history_presenter: order_history_Presenter

    companion object {

        fun instance (): Fragment {
            return order_history()
        }

        lateinit var v: View
        var orderList: OrderList? = null

    }




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        v = inflater.inflate(R.layout.fragment_order_history, container, false)

        return v
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onLoad()


        //TODO click update:
        status_update.setOnClickListener {
            onLoad()
        }

        //TODO click back:
        some_addbar_view_back.setOnClickListener {
            varApp.delete_step_from_BackStack(varApp.supportFragmentManager, this::class.java.simpleName)
        }


    }


    fun onLoad() {
        order_history_rv.visibility = View.GONE
        order_history_appbar.visibility = View.GONE
        order_history_status.visibility = View.GONE
        varApp.nav_view.visibility = View.GONE
        order_history_pb.visibility = View.VISIBLE

        order_history_presenter.onLoad()
    }


    override fun onSuccess() {

        order_history_pb.visibility = View.GONE
        order_history_status.visibility = View.GONE

        order_history_rv.visibility = View.VISIBLE

        order_history_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as AppCompatActivity).supportActionBar!!,
            customAppBar = order_history_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = true,
            some_title_text = "История заказов",
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false,
            some_appbar_btn_search = false

        )

        varApp.nav_view.visibility = View.VISIBLE


        order_history_exp.setAdapter(AdapterExpandableOrderHistory(v.context))

        //TODO go product page click:
        order_history_exp.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->

            //TODO проверка нужна, т.к. добавлен footer:
//            if ( orderList!!.Orders[groupPosition].Details.items.size >= childPosition + 1 ) {
            if ( childPosition < 4 && orderList!!.Orders[groupPosition].Details.items.size >= childPosition + 1 ) {

                val product: Fragment = product.instance(
                    bundleOf(
                        "code" to orderList!!.Orders[groupPosition].Details.items[childPosition].code))


                varApp.supportFragmentManager.beginTransaction()
//                    .setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_up)
                    .hide(this)
                    .add(R.id.nav_host_fragment, product, "product")
                    .show(product)
                    .addToBackStack("product")
                    .commit()




//                varApp.replaceFragment(varApp.supportFragmentManager,
//                    product.instance(
//                        bundleOf(
//                            "code" to orderList!!.Orders[groupPosition].Details.items[childPosition].code
//                        )
//                    ), addAnimation = true, animation = anim.from_bottom_in_top, addToBackStack = true
//                )




            }

            return@setOnChildClickListener true
        }


    }

    override fun noInternet() {

        order_history_pb.visibility = View.GONE
        order_history_rv.visibility = View.GONE

        order_history_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as Main).supportActionBar!!,
            customAppBar = order_history_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = false,
            some_title_text = varApp.heading_no_internet,
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false)

        varApp.nav_view.visibility = View.GONE
        order_history_status.visibility = View.VISIBLE

    }

    override fun isEmpty() {

        order_history_pb.visibility = View.GONE
        order_history_rv.visibility = View.GONE

        order_history_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as AppCompatActivity).supportActionBar!!,
            customAppBar = order_history_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = false,
            some_title_text = "История заказов",
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false

        )

        varApp.nav_view.visibility = View.VISIBLE

        order_history_status.visibility = View.VISIBLE
        status_title.text = "В истории пусто"
        status_body.text = "У вас не было еще оформленных заказов. Зарегистрируйтесь или войдите в профиль и оформите заказы, чтобы видеть историю."
        status_update.visibility = View.GONE

    }




}





//region Adapter

class AdapterExpandableOrderHistory internal constructor(private val context: Context) : BaseExpandableListAdapter()   {


    //region Group:
    override fun getGroup(groupPosition: Int): Orders {
        return orderList!!.Orders[groupPosition]
    }

    override fun getGroupCount(): Int {
        return orderList!!.Orders.size
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup): View {
        var v = convertView

        val group = getGroup(groupPosition)
        if (v == null) {
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            v = layoutInflater.inflate(R.layout.element_expandable_group, null)
        }

        (parent as ExpandableListView).expandGroup(groupPosition)

        v!!.group_status.text = group.StateTitleShort
        v.group_number_order.text = "№ ${group.ID} от ${group.RegDate}"



        v.group_sum_order.text = varApp.RoundToInteger(group.SummOrder, true).toString()

        return v
    }

    //endregion


    //region Items:

    override fun getChild(groupPosition: Int, childPosition: Int): OrderItem {
        return orderList!!.Orders[groupPosition].Details.items[childPosition]
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return orderList!!.Orders[groupPosition].Details.items.take(4).size + 1
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, view: View?, parent: ViewGroup): View {

        var v = view
        val currentParent = getGroup(groupPosition);

        val inflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


        if(childPosition < getChildrenCount(groupPosition) -1) {
            val item = getChild(groupPosition, childPosition)
            v = inflater.inflate(R.layout.element_expandable_item, null)
            v.item_name.text = item.name
            v.item_prices.text = varApp.RoundToInteger(item.summ_s, true).toString()
            v.item_received.text = "получено: ${item.realized.toInt()} из ${item.qty.toInt()} ${item.ed}."
            Glide.with(context).load("${item.imgpath}").into(v.item_img)
        }
        

        //TODO for last position out footer:
        if (childPosition == getChildrenCount(groupPosition) - 1) {

            v = inflater.inflate(R.layout.element_expandable_footer, null)
            //TODO save number order:
            v.item_number_order.text = orderList!!.Orders[groupPosition].ID.toString()

            if (orderList!!.Orders[groupPosition].Details.items.size > 4) {

                val num = orderList!!.Orders[groupPosition].Details.items.size -
                        orderList!!.Orders[groupPosition].Details.items.take(4).size
                v.item_more_products.visibility = View.VISIBLE
                v.item_more_products.text = "Еще ${num} тов. в заказе"
            } else {
//                val factor: Float = v.context.resources.displayMetrics.density
                v.item_more_products.visibility = View.GONE
                v.item_constr.layoutParams.height = (45 * varApp.convertPxDp).toInt()
            }


            //TODO click details:
            v.item_btn_details.setOnClickListener {

                val ohd: Fragment = order_history_details.instance(
                    bundleOf(
                        "id" to (it.parent as ConstraintLayout).item_number_order.text))
                varApp.supportFragmentManager.beginTransaction()
                    //.setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_up)
                    .hide(v!!.findFragment())
                    .add(R.id.nav_host_fragment, ohd, "order_history_details")
                    .show(ohd)
                    .addToBackStack("order_history_details")
                    .commit()

//                varApp.replaceFragment(varApp.supportFragmentManager, ohd, addAnimation = true, animation = anim.from_bottom_in_top, addToBackStack = true)
            }


//            //TODO click repeat:
//            v.item_btn_repeat.setOnClickListener {
//                println("repeat")
//            }

        }

        return v!!
    }


    //endregion


    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }



}








//endregion



//region PRESENTER

@InjectViewState
class order_history_Presenter : MvpPresenter<OrderHistory_ViewState>() {

    private val disposeBag = CompositeDisposable()

    @Inject
    lateinit var backendModule: BackendModule


    fun onLoad () {
        varApp.appComponent.inject(this)

        disposeBag.add(backendModule.provideMyGet().getOrders(varApp.SID).delay(varApp.delay_default, varApp.typeUnit).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                if (it.Orders.size != 0) {
                    orderList = it
                    orderList!!.Orders = it.Orders.sortedWith(compareByDescending(Orders::ID)) as MutableList<Orders>

                    val count = orderList!!.Orders.size
                    for (i in 0 until orderList!!.Orders.size) {
                        ord(item = i)

                        while (orderList!!.Orders[i].Details == null) {
                            println("ok")
                        }
                    }
                    viewState.onSuccess()
                } else {
                    viewState.isEmpty()
                }

            }, {
                viewState.noInternet()
            }))

    }

    fun ord(item: Int) {
        varApp.appComponent.inject(this)

        disposeBag.add(backendModule.provideMyGet().getord(orderList!!.Orders[item].ID.toString(), varApp.SID).delay(varApp.delay_default, varApp.typeUnit).subscribeOn(Schedulers.computation())
            .subscribe({
                orderList!!.Orders[item].Details = it
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
