package ru.isantur.santurshop.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import kotlinx.android.synthetic.main.fragment_favorite.*
import kotlinx.android.synthetic.main.fragment_favorite.view.*
import kotlinx.android.synthetic.main.include_status.*
import moxy.InjectViewState
import moxy.MvpAppCompatFragment
import moxy.MvpPresenter
import moxy.MvpView
import moxy.presenter.InjectPresenter
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import org.json.JSONArray
import ru.isantur.santurshop.AppResponse
import ru.isantur.santurshop.Data.Favorites
import ru.isantur.santurshop.R
import ru.isantur.santurshop.activity.Main
import ru.isantur.santurshop.di.BackendModule

import ru.isantur.santurshop.fragment.favorite.Companion.favorites
import ru.isantur.santurshop.fragment.favorite.Companion.presenter
import ru.isantur.santurshop.fragment.favorite.Companion.v
import ru.isantur.santurshop.fragment.products.product
import ru.isantur.santurshop.varApp
import javax.inject.Inject

@StateStrategyType(value = OneExecutionStateStrategy::class)
interface Favorite_ViewState: MvpView {

    fun onSuccess()
    fun noInternet ()
    fun isEmpty()


}


class favorite : MvpAppCompatFragment(), Favorite_ViewState {

    @InjectPresenter   lateinit var favorite_presenter: favorite_Presenter

    companion object {

        fun instance (): Fragment {
            return favorite()
        }

        var favorites: MutableList<Favorites> = ArrayList()


        var jsArray: JSONArray? = null

        lateinit var v: View

        lateinit var presenter: favorite_Presenter



    }




    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        v = inflater.inflate(R.layout.fragment_favorite, container, false)



        return v


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = favorite_presenter
        varApp.nav_view.menu.getItem(2).setChecked(true)
        varApp.nav_view.visibility = View.VISIBLE


        onLoad()


        //TODO click update:
        status_update.setOnClickListener {
            onLoad()
        }


    }


    fun onLoad () {

        favorite_pb.visibility = View.VISIBLE
        favorite_appbar.visibility = View.GONE
        favorite_status.visibility = View.GONE
        varApp.nav_view.visibility = View.GONE

        favorite_presenter.onLoad()
    }

    override fun isEmpty () {

        favorite_pb.visibility = View.GONE
        favorite_rv.visibility = View.GONE

        favorite_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as AppCompatActivity).supportActionBar!!,
            customAppBar = favorite_appbar,
            customAppBar_backgroundColor =  R.color.primary,
            some_appbar_img_back = false,
            some_title_text =  "Избранные товары",
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor =  R.color.white,
            some_btn_clear_visible = false

        )

        varApp.nav_view.visibility = View.VISIBLE

        favorite_status.visibility = View.VISIBLE
        status_title.text = "В избранном пусто"
        status_body.text = "Перейдите в каталог, чтобы добавить необходимые товары."
        status_update.visibility = View.GONE
    }

    override fun onSuccess() {

        favorite_pb.visibility = View.GONE
        favorite_status.visibility = View.GONE

        favorite_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as AppCompatActivity).supportActionBar!!,
            customAppBar = favorite_appbar,
            some_title_text = "Избранные товары",
            some_btn_clear_visible = false,
            some_btn_clear_text = "Очистить")

        varApp.nav_view.visibility = View.VISIBLE


        favorite_rv.visibility = View.VISIBLE
        favorite_rv.setHasFixedSize(true)
        favorite_rv.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        favorite_rv.adapter = Adapter_Favorites()
        favorite_rv.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))



    }

    override fun noInternet() {

        favorite_pb.visibility = View.GONE
        favorite_rv.visibility = View.GONE

        favorite_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as Main).supportActionBar!!,
            customAppBar = favorite_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = false,
            some_title_text = varApp.heading_no_internet,
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false)


        varApp.nav_view.visibility = View.GONE
        favorite_status.visibility = View.VISIBLE



    }







}



//region Adapter

class Adapter_Favorites : RecyclerView.Adapter<Adapter_Favorites_VH>() {


    override fun getItemCount(): Int { return favorites.size }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Adapter_Favorites_VH {
        return  Adapter_Favorites_VH(LayoutInflater.from(parent.context).inflate(R.layout.element_list, parent, false))
    }



    override fun onBindViewHolder(holder: Adapter_Favorites_VH, position: Int) {

        val view = holder.itemView

        //show\hide other view:
        view.list_constrCart.visibility = View.GONE
        view.list_more.visibility = View.GONE

        view.list_add_del_cart_active.visibility = View.VISIBLE
        view.list_favorite.visibility = View.VISIBLE


        //Заполняем listview:
        view.list_name.text = favorites[position].name
        view.list_prices.text = varApp.RoundToInteger(favorites[position].prices, true).toString()
        view.list_ed.text = "за ${favorites[position].ed}"
        Glide.with(view.context).load(favorites[position].imgpath).into(view.list_img)


        //уже в избранном:
        if (favorites[position].isfavority) {
            view.list_favorite.setImageDrawable(view.resources.getDrawable(R.drawable.ic_fav_active))
            view.list_favorite.tag = "В избранном"
            favorites[position].isfavority = true
        } else {
            //нет в избранном
            view.list_favorite.setImageDrawable(view.resources.getDrawable(R.drawable.ic_fav))
            view.list_favorite.tag = "В избранное"
            favorites[position].isfavority = false
        }


        //уже в корзине:
        if (favorites[position].qty_incart.toInt() != 0) {
            view.list_add_del_cart_active.background = view.resources.getDrawable(R.drawable.btn_gray)
            view.list_add_del_cart_active.setTextColor(view.resources.getColor(R.color.primary))
            view.list_add_del_cart_active.text = "В корзине"
        } else {
            //нет в корзине:
            view.list_add_del_cart_active.background = view.resources.getDrawable(R.drawable.btn_active)
            view.list_add_del_cart_active.setTextColor(view.resources.getColor(R.color.white))
            view.list_add_del_cart_active.text = "В корзину"

        }

    }

}

class Adapter_Favorites_VH(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

    //навешиваем события
    init {

        // click add\remove cart
        v.list_add_del_cart_active.setOnClickListener(this)

        // click remove favorite:
        v.list_favorite.setOnClickListener(this)

        //CLICK (переход на страницу продукта):
        v.list_img.setOnClickListener ( this )
        v.list_name.setOnClickListener(this)

    }

    override fun onClick(view: View?) {

//        val animAlpha: Animation = AnimationUtils.loadAnimation(v!!.context, R.anim.alpha)

        when (view!!.id) {

            R.id.list_add_del_cart_active -> {

                (view.parent as ViewGroup).list_add_del_cart_active.visibility = View.INVISIBLE
                (view.parent as ViewGroup).list_add_del_cart_deactive.visibility = View.VISIBLE

                if ((view as TextView).text == "В корзину") {
                    presenter.add_to_cart(adapterPosition = adapterPosition, view)
                } else {
                    presenter.delete_from_cart(adapterPosition = adapterPosition, view)
                }

            }//click add\remove cart:

            R.id.list_favorite -> {
                presenter.delete_favorite(adapterPosition = adapterPosition)
            }//click remove favorite:

            R.id.list_img, R.id.list_name -> {

                val product: Fragment = product.instance(
                    bundleOf(
                        "code" to favorites[adapterPosition].code
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
//                            "code" to favorites[adapterPosition].code
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
class favorite_Presenter : MvpPresenter<Favorite_ViewState>() {


    private val disposeBag = CompositeDisposable()

    @Inject
    lateinit var backendModule: BackendModule


    fun onLoad () {
        varApp.appComponent.inject(this)

        disposeBag.add(
            backendModule.provideMyGet().getFavorite(varApp.SID)
                .delay(varApp.delay_default, varApp.typeUnit)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    if (it.size != 0) {
                        favorites = it
                        viewState.onSuccess()
                    } else {
                        viewState.isEmpty()
                    }
                    if (backendModule.getFavoriteBadge() == AppResponse.noInternet) viewState.noInternet()

                }, {
                    viewState.noInternet()
                })
        )


    }


    fun add_to_cart (adapterPosition: Int, view: View) {
        varApp.appComponent.inject(this)

        disposeBag.add(
            backendModule.provideMyGet().addToCart(favorites[adapterPosition].code, "1", varApp.SID)
                .delay(varApp.delay_default, varApp.typeUnit)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({

                    if (it.contains(favorites[adapterPosition].code)) {

                        (view.parent as ViewGroup).list_add_del_cart_active.background = view.resources.getDrawable(R.drawable.btn_gray)
                        (view.parent as ViewGroup).list_add_del_cart_active.setTextColor(view.resources.getColor(R.color.primary))
                        (view.parent as ViewGroup).list_add_del_cart_active.text = "В корзине"
                        favorites[adapterPosition].qty_incart = 1.0

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
            backendModule.provideMyGet().removeCartItem( favorites[adapterPosition].code, varApp.SID)
                .delay(varApp.delay_default, varApp.typeUnit)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({

                    if (it.contains("")) {

                        (view.parent as ViewGroup).list_add_del_cart_active.background = view.resources.getDrawable(R.drawable.btn_active)
                        (view.parent as ViewGroup).list_add_del_cart_active.setTextColor(view.resources.getColor(R.color.white))
                        (view.parent as ViewGroup).list_add_del_cart_active.text = "В корзину"
                        favorites[adapterPosition].qty_incart = 0.0
                    }

                    (view.parent as ViewGroup).list_add_del_cart_active.visibility = View.VISIBLE
                    (view.parent as ViewGroup).list_add_del_cart_deactive.visibility = View.INVISIBLE

                    if (backendModule.getCartBadge() == AppResponse.noInternet) viewState.noInternet()

                }, {
                    viewState.noInternet()
                })
        )

    }

    fun delete_favorite (adapterPosition: Int) {
        varApp.appComponent.inject(this)

        disposeBag.add(
            backendModule.provideMyGet().removeFavorite( favorites[adapterPosition].code, varApp.SID)
                .delay(varApp.delay_default, varApp.typeUnit)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({

                    if (it.contains("ok")) {
                        favorites.remove( favorites[adapterPosition] )
                        v.favorite_rv.adapter!!.notifyDataSetChanged()
                        if (favorites.size <= 0) {
                            viewState.isEmpty()
                        }
                    }

                    if (backendModule.getFavoriteBadge() == AppResponse.noInternet) viewState.noInternet()

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