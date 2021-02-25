package ru.isantur.santurshop.fragment.profile

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.alert_dialog.view.*
import kotlinx.android.synthetic.main.element_item_rv.view.*
import kotlinx.android.synthetic.main.include_appbar.view.*
import kotlinx.android.synthetic.main.include_status.*
import kotlinx.android.synthetic.main.profile_home.*
import kotlinx.android.synthetic.main.profile_home.view.*
import moxy.InjectViewState
import moxy.MvpAppCompatFragment
import moxy.MvpPresenter
import moxy.MvpView
import moxy.presenter.InjectPresenter
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.isantur.santurshop.Data.Favorites
import ru.isantur.santurshop.R
import ru.isantur.santurshop.activity.Main
import ru.isantur.santurshop.anim
import ru.isantur.santurshop.di.BackendModule
import ru.isantur.santurshop.fragment.favorite
import ru.isantur.santurshop.fragment.profile.profile_home.Companion.dlg
import ru.isantur.santurshop.fragment.profile.profile_home.Companion.pr_home_rv
import ru.isantur.santurshop.fragment.profile.profile_home.Companion.presenter
import ru.isantur.santurshop.varApp
import javax.inject.Inject

interface newinterf {

}



@StateStrategyType(value = OneExecutionStateStrategy::class)
interface ProfileHome_ViewState: MvpView {

    fun success_favorite(resp: ArrayList<Favorites>)
    fun noInternet ()
    fun onSuccess()

}





class profile_home : MvpAppCompatFragment(), ProfileHome_ViewState {

    @InjectPresenter lateinit var profile_home_presenter: profile_home_Presenter


    companion object {

        fun instance (): MvpAppCompatFragment {
            return profile_home()
        }

        
        lateinit var dlg: AlertDialog
        lateinit var pr_home_rv: RecyclerView

        val arr_name: ArrayList<String> = ArrayList<String>()
        val arr_name2: ArrayList<String> = ArrayList<String>()

        lateinit var from: String

        lateinit var presenter: profile_home_Presenter

    }

    override fun onCreateView( inflater: LayoutInflater,  container: ViewGroup?, savedInstanceState: Bundle? ): View? {

        presenter = profile_home_presenter

        return inflater.inflate(R.layout.profile_home, container, false)
        
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        varApp.nav_view.menu.getItem(4).setChecked(true)
        varApp.nav_view.visibility = View.VISIBLE

        arr_name.clear()
        arr_name.add(0, "Избранные товары")
        arr_name.add(1, "Мой город")
        arr_name.add(2, "Информация")


        //TODO click (go page input)
        profile_home_btn_input.setOnClickListener {
            varApp.replaceFragment(varApp.supportFragmentManager, profile_input.instance(), addAnimation = true, addToBackStack = true)
        }

        //TODO click (update status internet)
        status_update.setOnClickListener {
            onLoad(view)
        }

        onLoad(view)





    }


    fun onLoad (view: View) {
        profile_home_presenter.showOnLoad(view)
        profile_home_presenter.hideOnLoad(view)
        profile_home_presenter.favorites()
    }

    
    
    override fun success_favorite(resp: ArrayList<Favorites>) {

        onSuccess()

        arr_name2.clear()
        arr_name2.add(0, resp.size.toString())
        arr_name2.add(1, varApp.iam[0].city)
        arr_name2.add(2, "")


        pr_home_rv = profile_home_rv

        profile_home_rv.setHasFixedSize(true)
        profile_home_rv.layoutManager = LinearLayoutManager( this.context, LinearLayoutManager.VERTICAL, false)
        profile_home_rv.adapter = AdapterProfileHome()
        profile_home_rv.addItemDecoration(DividerItemDecoration( this.context, DividerItemDecoration.VERTICAL))

    }

    override fun noInternet() {

        profile_home_pb.visibility = GONE
        profile_home_page.visibility = GONE

        profile_home_appbar.visibility = VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as Main).supportActionBar!!,
            customAppBar = profile_home_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = false,
            some_title_text = varApp.heading_no_internet,
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false
        )
        profile_home_status.visibility = VISIBLE
        varApp.nav_view.visibility = VISIBLE

    }

    override fun onSuccess() {
        profile_home_pb.visibility = GONE
        profile_home_status.visibility = GONE

        profile_home_page.visibility = VISIBLE

        profile_home_appbar.visibility = VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as AppCompatActivity).supportActionBar!!,
            customAppBar = profile_home_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = false,
            some_title_text = "Мой профиль",
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false
        )
        varApp.nav_view.visibility = VISIBLE
    }


} //class profile_home



//region Adapter

class AdapterProfileHome : RecyclerView.Adapter<AdapterProfileHomeVH>() {

    override fun getItemCount(): Int { return profile_home.arr_name.size }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterProfileHomeVH {
        return  AdapterProfileHomeVH(  LayoutInflater.from(parent.context).inflate(R.layout.element_item_rv, parent, false) )
    }


    override fun onBindViewHolder(holder: AdapterProfileHomeVH, position: Int) {

        val v = holder.itemView

        v.name_rv.text = profile_home.arr_name[position]
        v.switch_rv.visibility = ViewGroup.GONE


        v.name2_rv.visibility = VISIBLE
        v.name2_rv.text = profile_home.arr_name2[position]

    }

}

class AdapterProfileHomeVH(v: View) : RecyclerView.ViewHolder(v), OnClickListener {

    //навешиваем события
    init {
        v.setOnClickListener(this)
    }


    override fun onClick(v: View?) {

        when(adapterPosition) {
            0 -> {
//                profile_home.v.findNavController().navigate(R.id.go_fromProfileHome_inFavorite)
                varApp.nav_view.menu.getItem(2).setChecked(true)
                varApp.replaceFragment(varApp.supportFragmentManager, favorite.instance(), addAnimation = false, addToBackStack = false)
            } //go favorite

            1 -> {
                //выбор города:
                val dlgView = LayoutInflater.from(v!!.context).inflate(R.layout.alert_dialog, null)


                dlgView.dialog_text.visibility = GONE
                dlgView.dialog_two_button.visibility = GONE
                dlgView.dialog_divider_end.visibility = GONE
                dlgView.dialog_big_button.visibility = GONE


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


                val builder = AlertDialog.Builder(v.context)
                with(builder) {
                    setView(dlgView)
                }
                dlg = builder.show()


                dlgView.dialog_rv.setHasFixedSize(true)
                dlgView.dialog_rv.layoutManager = LinearLayoutManager( v.context, LinearLayoutManager.VERTICAL, false)
                dlgView.dialog_rv.adapter = AdapterDialog()
                dlgView.dialog_rv.addItemDecoration(DividerItemDecoration( v.context, DividerItemDecoration.VERTICAL))



            } //select cities

            2 -> {

//                profile_home.v.findNavController().navigate(R.id.go_fromProfileHome_inProfileInfo)
                varApp.replaceFragment(varApp.supportFragmentManager, profile_info.instance(), addAnimation = true, animation =  anim.from_right_in_left, addToBackStack = true)


            } //go profile info

        } //when


    } //onClick


}


class AdapterDialog : RecyclerView.Adapter<AdapterDialogVH>() {


    override fun getItemCount(): Int { return varApp.cities.map { f -> f.name }.size }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterDialogVH {
        return  AdapterDialogVH(  LayoutInflater.from(parent.context).inflate(R.layout.element_item_rv, parent, false) )
    }


    override fun onBindViewHolder(holder: AdapterDialogVH, position: Int) {

        val v = holder.itemView

        v.name_rv.text = varApp.cities.map { f -> f.name }[position]
        v.img_right_rv.visibility = GONE

        //устанавливаем по умолчанию из iam:
        if (varApp.cities[position].id == varApp.iam[0].cityid) {
            v.img_select_rv.visibility = VISIBLE
        } else {
            v.img_select_rv.visibility = GONE
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
class profile_home_Presenter : MvpPresenter<ProfileHome_ViewState>() {

    private val disposeBag = CompositeDisposable()

    @Inject lateinit var backendModule: BackendModule


    fun showOnLoad (v: View) {

        v.profile_home_pb.visibility = VISIBLE

    }


    fun hideOnLoad (v: View) {
        v.some_appbar_title.text = "Мой профиль"
        v.profile_home_appbar.visibility = VISIBLE
        v.profile_home_status.visibility = GONE
        v.profile_home_page.visibility = GONE
        varApp.nav_view.visibility = GONE
    }


    fun favorites () {

        varApp.appComponent.inject(this)

        disposeBag.add(
            backendModule.provideMyGet().getFavorite(varApp.SID)
               .delay(varApp.delay_default, varApp.typeUnit)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    viewState.success_favorite(it)
                }, {
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
                    pr_home_rv[1].findViewWithTag<TextView>("name2_rv").text = varApp.iam[0].city
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