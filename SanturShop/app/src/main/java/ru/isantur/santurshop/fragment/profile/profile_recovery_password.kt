package ru.isantur.santurshop.fragment.profile

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.alert_dialog.view.*
import kotlinx.android.synthetic.main.fragment_cart.*
import kotlinx.android.synthetic.main.include_appbar.*
import kotlinx.android.synthetic.main.include_appbar.view.*
import kotlinx.android.synthetic.main.include_big_button.*
import kotlinx.android.synthetic.main.include_big_button.view.*
import kotlinx.android.synthetic.main.include_status.*
import kotlinx.android.synthetic.main.main.*
import kotlinx.android.synthetic.main.profile_home.*
import kotlinx.android.synthetic.main.profile_home.view.*
import kotlinx.android.synthetic.main.profile_recovery_password.*
import kotlinx.android.synthetic.main.profile_recovery_password.view.*
import kotlinx.android.synthetic.main.profile_registration.*
import moxy.InjectViewState
import moxy.MvpAppCompatFragment
import moxy.MvpPresenter
import moxy.MvpView
import moxy.presenter.InjectPresenter
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.isantur.santurshop.Data.Favorites
import ru.isantur.santurshop.R
import ru.isantur.santurshop.Retrofit.myGET
import ru.isantur.santurshop.Utils
import ru.isantur.santurshop.activity.Main
import ru.isantur.santurshop.di.BackendModule
import ru.isantur.santurshop.fragment.products.product_list
import ru.isantur.santurshop.fragment.profile.profile_recovery_password.Companion.v
import ru.isantur.santurshop.varApp
import javax.inject.Inject

@StateStrategyType(value = OneExecutionStateStrategy::class)
interface PRP_ViewState: MvpView {

    fun noInternet ()
    fun onSuccess()
    fun onSuccessRecovery(resp: String)

}


class profile_recovery_password : MvpAppCompatFragment(), PRP_ViewState {

    @InjectPresenter lateinit var prp_presenter: prp_Presenter

    companion object {

        fun instance (): Fragment {
            return profile_recovery_password()
        }

        lateinit var v: View
        var js = ""
    }



    override fun onCreateView( inflater: LayoutInflater,  container: ViewGroup?, savedInstanceState: Bundle? ): View? {

        v = inflater.inflate(R.layout.profile_recovery_password, container, false)

        return v

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onSuccess()
        onEvents()


    }

    fun recovery_password () {

        if (rcv_email.text.toString() != "") {

            rcv_text.text = "Пожалуйста, подождите. Идет отправка пароля на эл.почту ..."

            // TODO show and hide btn send email (progress):
            some_big_btn_active.visibility = View.INVISIBLE
            some_big_btn_deactive.visibility = View.VISIBLE

            prp_presenter.recovery( rcv_email.text.toString() )



        } else {
            YoYo.with(Techniques.Shake).duration(1000).playOn(v.rcv_text_edit);
            rcv_text_edit.error = "Обязательное поле для заполнения"
        }


    }


    fun onEvents() {

        //TODO change email (write email):
        rcv_email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) {
                if (s.toString().contains("\n")) {
                    rcv_email.setText(s.toString().replace("\n", ""))

                    Utils.hideKeyboard(v.context, v)  //убираем стандартную клавиатуру
                    recovery_password()
                }
            }

        })


        //TODO click send:
        some_big_btn_active.setOnClickListener {
            recovery_password()
        }


        //TODO focus\click login:
        rcv_email.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) { rcv_text_edit.error = null } }
        rcv_email.setOnClickListener { rcv_text_edit.error = null }


        //TODO click back:
        some_addbar_view_back.setOnClickListener {
            varApp.delete_step_from_BackStack(varApp.supportFragmentManager, step = this::class.java.simpleName)
        }

        //TODO update:
        status_update.setOnClickListener {
            onSuccess()
            onEvents()
        }

    }


    override fun noInternet() {

        rcv_pb.visibility = View.GONE
        rcv_page.visibility = View.GONE

        rcv_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as Main).supportActionBar!!,
            customAppBar = rcv_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = false,
            some_title_text = varApp.heading_no_internet,
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false
        )

        varApp.nav_view.visibility = View.VISIBLE
        rcv_status.visibility = View.VISIBLE

    }

    override fun onSuccess() {

        //if not internet, then show page no internet:
        if (! varApp.isNetwork( varApp.appcontext ) ) {
            noInternet()
            return
        }


        rcv_pb.visibility = View.GONE
        rcv_page.visibility = View.VISIBLE
        some_big_btn_active.text = "Отправить"

        rcv_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as AppCompatActivity).supportActionBar!!,
            customAppBar = rcv_appbar,
            customAppBar_backgroundColor =  R.color.white,
            some_appbar_img_back = true,
            some_title_text =  "Восстановление пароля",
            some_title_textColor = R.color.type_black,
            some_btn_clear_text = "",
            some_btn_clear_textColor =  R.color.type_black,
            some_btn_clear_visible = false
        )

        varApp.nav_view.visibility = View.VISIBLE
        rcv_status.visibility = View.GONE



    }

    override fun onSuccessRecovery(resp: String) {

//        val builder = AlertDialog.Builder( v.context )
//        builder
//            .setTitle("Пароль\n")
//            .setMessage("${resp}")
//            .setCancelable(false)
//            .setPositiveButton("Ок") { dlg, _ ->
//                dlg.dismiss()
//            }
//
//        val alert = builder.create()
//        alert.show()

        Utils.hideKeyboard( varApp.appcontext, v)

        val dlgView = LayoutInflater.from( v.context ).inflate(R.layout.alert_dialog, null)
        dlgView.dialog_appbar.visibility = View.VISIBLE

        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as AppCompatActivity).supportActionBar!!,
            customAppBar = dlgView.dialog_appbar,
            customAppBar_backgroundColor =  R.color.white,
            some_appbar_img_back = false,
            some_appbar_btn_search = false,
            some_title_text =  "Восстановление пароля",
            some_title_textColor = R.color.type_black,
            some_btn_clear_text = "",
            some_btn_clear_textColor =  R.color.primary,
            some_btn_clear_visible = false

        )


        dlgView.dialog_text.text = resp
        dlgView.dialog_big_button.visibility = View.GONE
        dlgView.dialog_rv.visibility = View.GONE
        dlgView.dialog_cancel.visibility = View.GONE
        dlgView.dialog_action.text = "Ок"


        val builder = AlertDialog.Builder( v.context )
        with(builder) { setView(dlgView) }

        val dlg = builder.show()

        //action:
        dlgView.dialog_action.setOnClickListener { dlg.dismiss() }
    }


}



//region PRESENTER

@InjectViewState
class prp_Presenter : MvpPresenter<PRP_ViewState>() {

    private val disposeBag = CompositeDisposable()

    @Inject lateinit var backendModule: BackendModule


    fun recovery (email: String) {

        varApp.appComponent.inject(this)

//      js = khttp.get("${varApp.url}loginfogot/?u=${profile_recovery_password.v.rcv_email.text}&sid=${varApp.SID}").text




        disposeBag.add(
            backendModule.provideRetrofit(url = "${varApp.url}loginfogot/?u=${email}&sid=${varApp.SID}").create(myGET::class.java) .setOk()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    if (v.rcv_text != null) {

                        v.some_big_btn_active.visibility = View.VISIBLE
                        v.some_big_btn_deactive.visibility = View.INVISIBLE

                        v.rcv_email.clearFocus()
                        v.rcv_text.text = "Введите адрес эл.почты, указанный в профиле. На него придет инструкция по восстановлению пароля"

                    }


                    //Обработка результата запроса:
                    if (it.contains("not exist")) {
                        //display dialog (error):
                        viewState.onSuccessRecovery("Пользователь с такой эл.почтой не зарегистрирован")
                    } else {
                        //display dialog (success):
                        viewState.onSuccessRecovery(it)
                    }


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