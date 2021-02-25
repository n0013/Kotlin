package ru.isantur.santurshop.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.badge.BadgeDrawable
import kotlinx.android.synthetic.main.banner.view.*
import kotlinx.android.synthetic.main.element_brands.view.*
import kotlinx.android.synthetic.main.element_item_rv.view.*
import kotlinx.android.synthetic.main.element_sales.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.include_three_button.*
import kotlinx.android.synthetic.main.search.*
import moxy.*
import moxy.presenter.InjectPresenter
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.isantur.santurshop.R
import ru.isantur.santurshop.anim
import ru.isantur.santurshop.di.BackendModule
import ru.isantur.santurshop.fragment.catalog.catalog
import ru.isantur.santurshop.fragment.catalog.catalog_sub
import ru.isantur.santurshop.fragment.map.map
import ru.isantur.santurshop.fragment.map.ya
import ru.isantur.santurshop.fragment.orders.order_history
import ru.isantur.santurshop.fragment.products.product
import ru.isantur.santurshop.fragment.products.product_list
import ru.isantur.santurshop.varApp
import javax.inject.Inject

@StateStrategyType(value = OneExecutionStateStrategy::class)
interface Home_ViewState: MvpView {


}


class home : MvpAppCompatFragment(), Home_ViewState {

    @InjectPresenter lateinit var home_presenter: home_Presenter

    companion object {

        fun instance (): MvpAppCompatFragment {
            return home()
        }

        lateinit var v: View

    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        presenter = home_presenter

        //init parentFragmentManager in varApp:
//        varApp.supportFragmentManager = parentFragmentManager


        v = inflater.inflate(R.layout.fragment_home, container, false)

        return v
    }

    override fun onViewCreated(v: View, savedInstanceState: Bundle?) {
        super.onViewCreated(v, savedInstanceState)

        varApp.nav_view.menu.getItem(0).setChecked(true)

        varApp.nav_view.visibility = View.VISIBLE

        //init Badge (favorites, cart):
        home_presenter.onLoad()


        //TODO click on Search:
        _search.setOnClickListener {

            val search: Fragment = search.instance()
            varApp.supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                .hide(this)
                .add(R.id.nav_host_fragment, search, "search")
                .show(search)
                .addToBackStack("search")
                .commit()
        }

        //TODO focus on Search:
        _search.isFocusable = false
        _search.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus) {
                val search: Fragment = search.instance()
                varApp.supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    .hide(this)
                    .add(R.id.nav_host_fragment, search, "search")
                    .show(search)
                    .addToBackStack("search")
                    .commit()
            }
        }

        //TODO click by btn populars all:
        populars_all_btn.setOnClickListener {
            varApp.replaceFragment(parentFragmentManager, catalog.instance(), addAnimation = false)
        }

        //TODO click by btn catalog:
        btn_catalog.setOnClickListener {

//            val catalog: Fragment = catalog.instance()
//            varApp.supportFragmentManager.beginTransaction()
//                .hide(this)
//                .add(R.id.nav_host_fragment, catalog, "catalog")
//                .show(catalog)
//                .addToBackStack("catalog")
//                .commit()

            varApp.replaceFragment(parentFragmentManager, catalog.instance(), addAnimation = false, addToBackStack = false)

//            it.animate()
////                .alpha(0.5F) //прозрачность от 0 (полная прозрачность) до 1.
////                .scaleX(0.5F) //изменяет размер от 0 до 1
////                .scaleY(0.5F)
//                .translationX(100F) //изменяет движение
//                .translationY(-200F)
//                .setDuration(500).start()

        }

        //TODO click by btn history:
        btn_history.setOnClickListener {
            varApp.replaceFragment(parentFragmentManager, fragment = order_history.instance(), addAnimation = false, addToBackStack = true)
        }

        //TODO click by btn address:
        btn_address.setOnClickListener {

            val map: Fragment = map.instance(
                bundleOf("from" to "other")
            )
            varApp.supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .hide(it.findFragment())
                .add(R.id.nav_host_fragment, map, "map")
                .show(map)
                .addToBackStack("map")
                .commit()
        }


        sales.isFocusable = true
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            sales.isFocusedByDefault = true
        }


//region init (sales, populars, brands, banner to rv adapter):

        sales.setHasFixedSize(true)
        sales.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        sales.adapter = Sales()

        //transfer populars to rv adapter:
        populars.setHasFixedSize(true)
        populars.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        populars.adapter = Popular()
        populars.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))
        //populars.adapter = AdapterPopular(v.context)

        //transfer brands to rv adapter:
        brands.setHasFixedSize(true)
        brands.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false)
        brands.adapter = Brands()

        //transfer banners to view pager2:
        banner.adapter = Banners()
        banner_indicator.setViewPager2(banner)

//endregion

    }


    override fun onStart() {
        super.onStart()
        (activity as AppCompatActivity).supportActionBar!!.hide()

    }




}






//region ADAPTERS:


class Sales : RecyclerView.Adapter<SalesVH>(){


    override fun getItemCount(): Int { return varApp.sales.size }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalesVH {
        return  SalesVH(LayoutInflater.from(parent.context).inflate(R.layout.element_sales, parent, false))
    }

    override fun onBindViewHolder(holder: SalesVH, position: Int) {

        val view = holder.itemView
        view.sales_name.text = varApp.sales[position].name
        view.sales_prices.text = varApp.RoundToInteger(varApp.sales[position].prices, true).toString()

        Glide.with(view.context).load(varApp.sales[position].imgpath).into(view.sales_image)

    }

} //Class Sales

class SalesVH(v: View) : RecyclerView.ViewHolder(v) {

    init {

        v.setOnClickListener {

            val product: Fragment = product.instance(
                bundleOf(
                    "code" to varApp.sales[adapterPosition].code
                )
            )

            varApp.supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up)
                //.setCustomAnimations(R.anim.slide_in_down, R.anim.slide_out_up)
                .hide(it.findFragment())
                .add(R.id.nav_host_fragment, product, "product")
                .show(product)
                .addToBackStack("product")
                .commit()


        }
    }




} //Class SalesVH



class Popular : RecyclerView.Adapter<PopularVH>() {

    override fun getItemCount(): Int { return varApp.populars.size }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularVH {
        return  PopularVH(LayoutInflater.from(parent.context).inflate(R.layout.element_item_rv, parent, false))
    }

    override fun onBindViewHolder(holder: PopularVH, position: Int) {

        val v = holder.itemView


        (v.name_rv.layoutParams as ConstraintLayout.LayoutParams).marginStart = 90
        (v.name_rv.layoutParams as ConstraintLayout.LayoutParams).marginEnd = 70

        v.name_rv.text = varApp.populars[position].name

        v.img_rv.visibility = View.VISIBLE
        v.img_right_rv.visibility = View.VISIBLE

        Glide.with(holder.itemView.context).load(varApp.populars[position].imgpath).into(v.img_rv)

    }

}

class PopularVH(v: View) : RecyclerView.ViewHolder(v) {

    //навешиваем события
    init {
        v.setOnClickListener{

            product_list.TITLE = varApp.populars[adapterPosition].name

            //for back from catalog_sub:
            catalog.CatalogId = varApp.populars[adapterPosition].tn_id


            val product_list: Fragment = product_list.instance(
                bundleOf(
                    "id" to varApp.populars[adapterPosition].tk_id,
                    "from" to "populars"
                )
            )
            varApp.supportFragmentManager.beginTransaction()
                .hide(it.findFragment())
                .add(R.id.nav_host_fragment, product_list, "product_list")
                .show(product_list)
                .addToBackStack("product_list")
                .commit()

        }
    }


}



class Brands : RecyclerView.Adapter<BrandVH>() {

    override fun getItemCount(): Int { return varApp.brands.size }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BrandVH {
        return  BrandVH(LayoutInflater.from(parent.context).inflate(R.layout.element_brands, parent, false))
    }

    override fun onBindViewHolder(holder: BrandVH, position: Int) {

        Glide.with(holder.itemView.context).load(varApp.brands[position].img).into(holder.itemView.brands_img)

    }

}

class BrandVH(v: View) : RecyclerView.ViewHolder(v) {

    init {

        v.setOnClickListener {

            product_list.TITLE = varApp.brands[adapterPosition].title

            val product_list: Fragment = product_list.instance(
                bundleOf(
                    "brand" to varApp.brands[adapterPosition].title,
                    "link" to varApp.brands[adapterPosition].link,
                    "from" to "brands"
                )
            )
            varApp.supportFragmentManager.beginTransaction()
                .hide(it.findFragment())
                .add(R.id.nav_host_fragment, product_list, "product_list")
                .show(product_list)
                .addToBackStack("product_list")
                .commit()

        }
    }

}



class Banners : RecyclerView.Adapter<BannersVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannersVH {
        return BannersVH(LayoutInflater.from(parent.context).inflate(R.layout.banner, parent, false))
    }


    override fun getItemCount(): Int = varApp.banners.size


    override fun onBindViewHolder(holder: BannersVH, position: Int) {

        Glide .with(holder.itemView.context) .load(varApp.banners[position].img) .into(holder.itemView.banner_img)

    }
}

class BannersVH(v: View) : RecyclerView.ViewHolder(v) {

    init {

        v.setOnClickListener {

            product_list.TITLE = varApp.banners[adapterPosition].title

            val product_list: Fragment = product_list.instance(
                bundleOf(
                    "banners" to varApp.banners[adapterPosition].title,
                    "link" to varApp.banners[adapterPosition].link,
                    "from" to "banners"
                )
            )
            varApp.supportFragmentManager.beginTransaction()
                .hide(it.findFragment())
                .add(R.id.nav_host_fragment, product_list, "product_list")
                .show(product_list)
                .addToBackStack("product_list")
                .commit()

        }
    }
}


//endregion





//region PRESENTER

@InjectViewState
class home_Presenter : MvpPresenter<Home_ViewState>() {

    @Inject lateinit var backendModule: BackendModule

    fun onLoad () {
        varApp.appComponent.inject(this)
        backendModule.getCartBadge()
        backendModule.getFavoriteBadge()
    }


}

//endregion


