package ru.isantur.santurshop.fragment.profile

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.alert_dialog.view.*
import kotlinx.android.synthetic.main.element_item_rv.view.*
import kotlinx.android.synthetic.main.include_appbar.*
import kotlinx.android.synthetic.main.include_status.*
import kotlinx.android.synthetic.main.profile_my.*
import moxy.InjectViewState
import moxy.MvpAppCompatFragment
import moxy.MvpPresenter
import moxy.MvpView
import moxy.presenter.InjectPresenter
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.isantur.santurshop.R
import ru.isantur.santurshop.activity.Main
import ru.isantur.santurshop.anim
import ru.isantur.santurshop.di.BackendModule
import ru.isantur.santurshop.fragment.discount_card
import ru.isantur.santurshop.fragment.favorite
import ru.isantur.santurshop.fragment.orders.order_history
import ru.isantur.santurshop.fragment.profile.profile_my.Companion.arr_name
import ru.isantur.santurshop.fragment.profile.profile_my.Companion.arr_name2
import ru.isantur.santurshop.fragment.profile.profile_my.Companion.dlg
import ru.isantur.santurshop.fragment.profile.profile_my.Companion.pr_my_rv
import ru.isantur.santurshop.fragment.profile.profile_my.Companion.presenter
import ru.isantur.santurshop.varApp
import ru.isantur.santurshop.varApp.Companion.iam
import javax.inject.Inject


@StateStrategyType(value = OneExecutionStateStrategy::class)
interface ProfileMy_ViewState: MvpView {

    fun onSuccess ()
    fun noInternet ()

}





class profile_my : MvpAppCompatFragment(), ProfileMy_ViewState {

    @InjectPresenter lateinit var profile_my_presenter: profile_my_Presenter


    companion object {

        fun instance (): Fragment {
            return profile_my()
        }

        lateinit var v: View

        lateinit var dlg: AlertDialog
        lateinit var pr_my_rv: RecyclerView

        val arr_name: ArrayList<String> = ArrayList<String>()
        val arr_name2: ArrayList<String> = ArrayList<String>()


        lateinit var presenter: profile_my_Presenter

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        presenter = profile_my_presenter

        v = inflater.inflate(R.layout.profile_my, container, false)

        return v

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        varApp.nav_view.menu.getItem(4).setChecked(true)

        profile_my_name_txt.text = iam[0].name
        profile_my_email_txt.text = iam[0].email

        varApp.nav_view.visibility = View.VISIBLE

        arr_name.clear()
        arr_name.add(0, "История заказов")
        arr_name.add(1, "Дисконтная карта")
        arr_name.add(2, "Избранные товары")
        arr_name.add(3, "Мой город")
        arr_name.add(4, "Информация")


        //TODO click update:
        status_update.setOnClickListener {
            onLoad()
        }


        onLoad()



        //go settings profile:
        profile_my_header.setOnClickListener {
            varApp.replaceFragment(varApp.supportFragmentManager, profile_settings.instance(), addAnimation = true, addToBackStack = true)
        }



    }




    fun onLoad () {

        profile_my_pb.visibility = View.VISIBLE
        some_appbar_title.text = "Мой профиль"
        profile_my_appbar.visibility = View.VISIBLE
        profile_my_status.visibility = View.GONE
        profile_my_page.visibility = View.GONE
        varApp.nav_view.visibility = View.GONE

        profile_my_presenter.getOrders()

    }

    override fun onSuccess() {

        profile_my_pb.visibility = View.GONE
        profile_my_status.visibility = View.GONE

        profile_my_page.visibility = View.VISIBLE

        profile_my_appbar.visibility = View.VISIBLE

        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as AppCompatActivity).supportActionBar!!,
            customAppBar = profile_my_appbar,
            some_appbar_img_back = false,
            some_title_text = "Мой профиль",
            some_btn_clear_text = "",
            some_btn_clear_visible = false)
        varApp.nav_view.visibility = View.VISIBLE


        pr_my_rv = profile_my_rv

        profile_my_rv.setHasFixedSize(true)
        profile_my_rv.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        profile_my_rv.adapter = AdapterProfileMy()
        profile_my_rv.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))

    }

    override fun noInternet() {
        profile_my_pb.visibility = View.GONE
        profile_my_page.visibility = View.GONE

        profile_my_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as Main).supportActionBar!!,
            customAppBar = profile_my_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = false,
            some_title_text = varApp.heading_no_internet,
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false)
        profile_my_status.visibility = View.VISIBLE
//        varApp.nav_view.visibility = View.VISIBLE
    }

}



//region Adapter

class AdapterProfileMy : RecyclerView.Adapter<AdapterProfileMyVH>() {

    override fun getItemCount(): Int { return arr_name.size }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterProfileMyVH {
        return  AdapterProfileMyVH(LayoutInflater.from(parent.context).inflate(R.layout.element_item_rv, parent, false))
    }


    override fun onBindViewHolder(holder: AdapterProfileMyVH, position: Int) {

        val iv = holder.itemView

        iv.name_rv.text = arr_name[position]
        iv.switch_rv.visibility = ViewGroup.GONE

        iv.name2_rv.visibility = View.VISIBLE
        iv.name2_rv.text = arr_name2[position]

    }

}

class AdapterProfileMyVH(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

    //навешиваем события
    init {
        v.setOnClickListener(this)
    }


    override fun onClick(v: View?) {

        when (adapterPosition) {
            0 -> {

                varApp.replaceFragment(varApp.supportFragmentManager, order_history.instance(), addAnimation = false, addToBackStack = true)

            }//order history

            1 -> {

                varApp.replaceFragment(varApp.supportFragmentManager, discount_card.instance(), addAnimation = false, addToBackStack = true)

            }//discount card

            2 -> {
                varApp.nav_view.menu.getItem(2).setChecked(true)
                varApp.replaceFragment(varApp.supportFragmentManager, favorite.instance(), addAnimation = false, addToBackStack = false)
            } //favorities

            3 -> {
                val dlgView = LayoutInflater.from(v!!.context).inflate(R.layout.alert_dialog, null)

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
                    some_btn_clear_visible = false)


                val builder = AlertDialog.Builder(v.context)
                with(builder) {
                    setView(dlgView)
                }
                dlg = builder.show()


                dlgView.dialog_rv.setHasFixedSize(true)
                dlgView.dialog_rv.layoutManager = LinearLayoutManager(v.context, LinearLayoutManager.VERTICAL, false)
                dlgView.dialog_rv.adapter = adapter_dialog_my()
                dlgView.dialog_rv.addItemDecoration(DividerItemDecoration(v.context, DividerItemDecoration.VERTICAL))


            } //select city

            4 -> {

                //                profile_my.v.findNavController().navigate( R.id.go_fromProfileMy_inProfileInfo )

                varApp.replaceFragment(varApp.supportFragmentManager, profile_info.instance(), addAnimation = true, animation = anim.from_right_in_left, addToBackStack = true)

            } //info
        }



    } //onClick


}



class adapter_dialog_my : RecyclerView.Adapter<adapter_dialog_my_vh>() {


    override fun getItemCount(): Int { return varApp.cities.map { f -> f.name }.size }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): adapter_dialog_my_vh {
        return  adapter_dialog_my_vh(LayoutInflater.from(parent.context).inflate(R.layout.element_item_rv, parent, false))
    }


    override fun onBindViewHolder(holder: adapter_dialog_my_vh, position: Int) {

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

class adapter_dialog_my_vh(v: View) : RecyclerView.ViewHolder(v) {

    init {
        v.setOnClickListener {
            presenter.selectCity(adapterPosition)
        }
    }

}


//endregion






//region PRESENTER

@InjectViewState
class profile_my_Presenter : MvpPresenter<ProfileMy_ViewState>() {

    private val disposeBag = CompositeDisposable()

    @Inject
    lateinit var backendModule: BackendModule

    fun getFavorite () {
        varApp.appComponent.inject(this)

        disposeBag.add(backendModule.provideMyGet().getFavorite(varApp.SID).delay(varApp.delay_default, varApp.typeUnit).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                arr_name2.add(2, it.size.toString())
                arr_name2.add(3, iam[0].city)
                arr_name2.add(4, "")

                viewState.onSuccess()

            }, {
                viewState.noInternet()
            }))

    }

    fun getOrders () {
        varApp.appComponent.inject(this)


        disposeBag.add(backendModule.provideMyGet().getOrders(varApp.SID)
            .delay(varApp.delay_default, varApp.typeUnit)
            .subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({

                arr_name2.clear()
                arr_name2.add(0, "${it.Orders.size}")
                getDicsountCart()


            }, {
                viewState.noInternet()
            }))

    }

    fun selectCity(adapterPosition: Int) {
        varApp.appComponent.inject(this)

        disposeBag.add(backendModule.provideMyGet().selectCity(varApp.cities[adapterPosition].id.toString(), varApp.SID).delay(varApp.delay_default, varApp.typeUnit)
            .subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe({
                varApp.setiam()
                if (it.contains("ok")) {
                    dlg.dismiss()
                }
                pr_my_rv[3].findViewWithTag<TextView>("name2_rv").text = varApp.iam[0].city
            }, {
                viewState.noInternet()
            }))

    }

    fun getDicsountCart() {
        varApp.appComponent.inject(this)

        disposeBag.add(backendModule.provideMyGet().GetDCInfo(varApp.iam[0].dc, varApp.SID).delay(varApp.delay_default, varApp.typeUnit).subscribeOn(Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread()).subscribe({

                val percent = Regex("""(\d%)""").find(it.vid)
                val bon_input = varApp.RoundToInteger(it.balance.toString(), false, "Int") as Int
                var bon_output = getNumberBonus( bon_input )

                if (percent == null) {
                    arr_name2.add(1, "0% / ${varApp.RoundToInteger(it.balance.toString(), false, "Int")} ${bon_output}")
                } else {
                    arr_name2.add(1, "${percent.value} / ${varApp.RoundToInteger(it.balance.toString(), false, "Int")} ${bon_output}")
                }
                getFavorite()

            }, {
                viewState.noInternet()
            }))

    }


    fun getNumberBonus(num: Int): String? {
        val preLastDigit = num % 100 / 10
        return if (preLastDigit == 1) {
            "бонусов"
        } else when (num % 10) {
            1 -> "бонус"
            2, 3, 4 -> "бонуса"
            else    -> "бонусов"
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        disposeBag.dispose()
        disposeBag.clear()
    }


}

//endregion