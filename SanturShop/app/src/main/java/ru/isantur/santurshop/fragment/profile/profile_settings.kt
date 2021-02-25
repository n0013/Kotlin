package ru.isantur.santurshop.fragment.profile

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.element_item_rv.view.*
import kotlinx.android.synthetic.main.include_appbar.*
import kotlinx.android.synthetic.main.include_status.*
import kotlinx.android.synthetic.main.profile_settings.*
import moxy.InjectViewState
import moxy.MvpAppCompatFragment
import moxy.MvpPresenter
import moxy.MvpView
import moxy.presenter.InjectPresenter
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.isantur.santurshop.AppResponse
import ru.isantur.santurshop.Data.Iam
import ru.isantur.santurshop.R
import ru.isantur.santurshop.activity.Main
import ru.isantur.santurshop.anim
import ru.isantur.santurshop.di.BackendModule
import ru.isantur.santurshop.fragment.orders.order_history
import ru.isantur.santurshop.fragment.profile.profile_settings.Companion.arrProfileSessings
import ru.isantur.santurshop.fragment.profile.profile_settings.Companion.presenter
import ru.isantur.santurshop.varApp
import javax.inject.Inject

@StateStrategyType(value = OneExecutionStateStrategy::class)
interface ProfileSettings_ViewState: MvpView {

    fun onSuccess()
    fun noInternet ()
    fun isEmpty()
    fun Logout(sid: String)

}


class profile_settings : MvpAppCompatFragment(), ProfileSettings_ViewState {

    @InjectPresenter lateinit var profile_settings_presenter: profile_settings_Presenter

    companion object {

        fun instance (): Fragment {
            return profile_settings()
        }

        lateinit var v: View
        val arrProfileSessings: ArrayList<String> = ArrayList()

        lateinit var presenter: profile_settings_Presenter

    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        v = inflater.inflate(R.layout.profile_settings, container, false)

        return v
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onLoad()


        //TODO click back:
        some_addbar_view_back.setOnClickListener {
            varApp.delete_step_from_BackStack(varApp.supportFragmentManager, step = this::class.java.simpleName)
        }


        //TODO click update:
        status_update.setOnClickListener {
            onLoad()
        }

    }

    fun onLoad () {

        presenter = profile_settings_presenter

        arrProfileSessings.clear()
        arrProfileSessings.add(0, "Данные профиля")
        arrProfileSessings.add(1, "Изменить пароль")
        arrProfileSessings.add(2, "Получать push-уведомления")
        arrProfileSessings.add(3, "Выйти из аккаунта")

        onSuccess()

    }

    override fun onSuccess() {

        profile_settings_pb.visibility = View.GONE
        profile_settings_status.visibility = View.GONE

        profile_settings_appbar.visibility = View.VISIBLE

        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as AppCompatActivity).supportActionBar!!,
            customAppBar = profile_settings_appbar,
            customAppBar_backgroundColor =  R.color.primary,
            some_appbar_img_back = true,
            some_title_text =  "Настройки",
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false
        )


        profile_settings_rv.setHasFixedSize(true)
        profile_settings_rv.layoutManager = LinearLayoutManager( v.context, LinearLayoutManager.VERTICAL, false)
        profile_settings_rv.adapter = AdapterProfileSettings()
        profile_settings_rv.addItemDecoration(DividerItemDecoration(v.context, DividerItemDecoration.VERTICAL))

    }

    override fun noInternet() {
        profile_settings_pb.visibility = View.GONE
        profile_settings_rv.visibility = View.GONE

        profile_settings_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as Main).supportActionBar!!,
            customAppBar = profile_settings_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = false,
            some_title_text = varApp.heading_no_internet,
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false)


        varApp.nav_view.visibility = View.GONE
        profile_settings_status.visibility = View.VISIBLE
    }

    override fun isEmpty() {

    }

    override fun Logout(sid: String) {

        varApp.setSID(sid)
        varApp.setSessionId_Preferences(varApp.SID)
        varApp.clear_LoginPassword_Preferences()
        varApp.iam.clear()
        varApp.iam.add(Iam(0, "", "", "","","","",0, "Екатеринбург", 100000))



        varApp.clearBackStack(varApp.supportFragmentManager)
        varApp.replaceFragment(varApp.supportFragmentManager, profile_home.instance(), addAnimation = true, animation = anim.from_bottom_in_top, addToBackStack = false)

    }


}




class AdapterProfileSettings() : RecyclerView.Adapter<AdapterProfileSettingsVH>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterProfileSettingsVH =
        AdapterProfileSettingsVH( LayoutInflater.from(parent.context).inflate( R.layout.element_item_rv, parent, false) )

    override fun getItemCount(): Int = arrProfileSessings.size

    override fun onBindViewHolder(holder: AdapterProfileSettingsVH, position: Int): Unit = holder.itemView.run {

        name_rv.text = arrProfileSessings[position]
        name2_rv.visibility = View.GONE


        when (position) {
            0,1 -> {
                switch_rv.visibility = View.GONE
            }
            2 -> {
                img_right_rv.visibility = View.GONE
                switch_rv.visibility = View.VISIBLE

            }
            3 -> {
                name_rv.setTextColor(Color.RED)
                img_right_rv.visibility = View.GONE
                switch_rv.visibility = View.GONE
            }

        }

    }

}

class AdapterProfileSettingsVH(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

    //навешиваем события
    init {
        v.setOnClickListener(this)
    }


    override fun onClick(v: View?) {

        when (adapterPosition) {
            0 -> {
                varApp.replaceFragment(varApp.supportFragmentManager, fragment = profile_data.instance(), addAnimation = false, addToBackStack = true)
            }

            1 -> {
                varApp.replaceFragment(varApp.supportFragmentManager, fragment = profile_change_pass.instance(), addAnimation = false, addToBackStack = true)
            }

            2 -> {

            }

            3 -> {
                val builder = AlertDialog.Builder(v!!.context)
                builder
                    .setTitle("Вы хотите выйти?")
                    .setMessage("При выходе вы не сможете оформлять заказы.")
                    .setCancelable(false)
                    .setPositiveButton("Выйти") { dlg, id ->

                        presenter.Logout()
                        dlg.dismiss()

                    }
                    .setNegativeButton( "Отмена") { dlg, id ->
                        dlg.dismiss()
                    }

                val alert = builder.create()
                alert.show()
            }

        } //when



    } //onClick



}






//region PRESENTER

@InjectViewState
class profile_settings_Presenter : MvpPresenter<ProfileSettings_ViewState>() {

    private val disposeBag = CompositeDisposable()

    @Inject lateinit var backendModule: BackendModule


    fun Logout () {
        varApp.appComponent.inject(this)

        disposeBag.add(
            backendModule.provideMyGet().logout(varApp.SID)
                .delay(varApp.delay_default, varApp.typeUnit)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    if (it.equals("ok")) {
                        connect()
                    }

                }, {
                    viewState.noInternet()
                })
        )

    }


    fun connect () {
        varApp.appComponent.inject(this)

        disposeBag.add(
            backendModule.provideMyGet().connect()
                .delay(varApp.delay_default, varApp.typeUnit)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({

                    viewState.Logout(it)
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
