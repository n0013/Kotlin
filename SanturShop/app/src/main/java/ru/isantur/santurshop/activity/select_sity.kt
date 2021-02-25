package ru.isantur.santurshop.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.alert_dialog.*
import kotlinx.android.synthetic.main.alert_dialog.view.*
import kotlinx.android.synthetic.main.element_item_rv.view.*
import kotlinx.android.synthetic.main.include_status.*
import moxy.*
import moxy.presenter.InjectPresenter
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.isantur.santurshop.activity.select_city.Companion.btn_click
import ru.isantur.santurshop.activity.select_city.Companion.itemSelectCity
import ru.isantur.santurshop.activity.select_city.Companion.v
import ru.isantur.santurshop.R
import ru.isantur.santurshop.di.BackendModule
import ru.isantur.santurshop.varApp
import javax.inject.Inject


@StateStrategyType(value = OneExecutionStateStrategy::class)
interface SelectCity_ViewState: MvpView {

    fun onLoad ()
    fun success_select_city(resp: String)
    fun onError ()

}


class select_city : MvpAppCompatActivity(), SelectCity_ViewState  {



    companion object {


        lateinit var v: View

        var itemSelectCity = 0

        var btn_click = false

        lateinit var presenter: select_city_Presenter

    }

    @InjectPresenter lateinit var select_city_presenter: select_city_Presenter



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = select_city_presenter

        setContentView(R.layout.alert_dialog)

        v = dialog_view

        onLoad()

        status_update.setOnClickListener {
            onLoad()
        }


    } //onCreate




    override fun onStart() {
        super.onStart()
        //убираем верхнее меню
        supportActionBar!!.hide()
    }

    override fun onLoad() {
        dialog_text.visibility = View.GONE
        dialog_two_button.visibility = View.GONE
        dialog_divider_end.visibility = View.GONE

        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = supportActionBar!!,
            customAppBar = dialog_appbar,
            customAppBar_backgroundColor = R.color.white,
            some_appbar_img_back = false,
            some_title_text = "Выбор города",
            some_title_textColor = R.color.type_black,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.type_black,
            some_btn_clear_visible = false
        )


        dialog_big_button.text = "Выбрать"
        dialog_big_button.visibility = View.VISIBLE
        dialog_big_button.setBackgroundResource(R.drawable.btn_disable)


        //Выводим в адаптер:
        dialog_rv.setHasFixedSize(true)
        dialog_rv.layoutManager = LinearLayoutManager( this, LinearLayoutManager.VERTICAL, false)
        dialog_rv.adapter = AdapterSelectSity()
        dialog_rv.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))


        //Подтверждение выбора города:
        dialog_big_button.setOnClickListener {

            if (btn_click ) {
                select_city_presenter.select_city()

            } else {
                varApp.Toast(this, "1.Выберите город.\n\n 2.Далее нажмите кнопку Выбрать")
            }

        }
    }


    override fun success_select_city(resp: String) {

        varApp.setiam()

        if (resp.contains("ok")) {

            startActivity(Intent(applicationContext, Main::class.java))
            finish()

        } else {
            varApp.Toast(this, "Запрос на выбор прошел с ошибкой!")
        }
    }

    override fun onError() {

        dialog_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = supportActionBar!!,
            customAppBar = dialog_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = false,
            some_title_text = varApp.heading_no_internet,
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false
        )
        dialog_status.visibility = View.VISIBLE

    }


}


//region Adapter

class AdapterSelectSity : RecyclerView.Adapter<AdapterSelectSityVH>() {

    override fun getItemCount(): Int { return varApp.cities.size }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterSelectSityVH {
        return  AdapterSelectSityVH(  LayoutInflater.from(parent.context).inflate(R.layout.element_item_rv, parent, false) )
    }


    override fun onBindViewHolder(holder: AdapterSelectSityVH, position: Int) {

        val v = holder.itemView

        v.name_rv.text = varApp.cities[position].name
        v.img_right_rv.visibility = View.GONE


        if (!varApp.firstLoad) {
            //устанавливаем по умолчанию из iam:
            if (varApp.cities[position].id == varApp.iam[0].cityid) {
                v.img_select_rv.visibility = View.VISIBLE
            } else {
                v.img_select_rv.visibility = View.GONE
            }
        } else {
            v.img_select_rv.visibility = View.GONE
        }


    }

}


class AdapterSelectSityVH (v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

    init {
        v.setOnClickListener( this )
    }


    override fun onClick(view: View?) {


        itemSelectCity = adapterPosition

        btn_click = true

        v.dialog_big_button.setBackgroundResource(R.drawable.btn_active)

        //hide all select:
        for (i in 0 until ((view as ConstraintLayout).parent as RecyclerView).adapter!!.itemCount ) {
            ((view.parent as RecyclerView).getChildAt(i) as ConstraintLayout).findViewWithTag<AppCompatImageView>("img_select_rv").visibility = View.GONE
        }

        //show select:
        view.img_select_rv.visibility = View.VISIBLE




    }


}

//endregion




//region PRESENTER

@InjectViewState
class select_city_Presenter : MvpPresenter<SelectCity_ViewState>() {

    private val disposeBag = CompositeDisposable()

    @Inject
    lateinit var backendModule: BackendModule


    fun select_city () {

        varApp.appComponent.inject(this)


        disposeBag.add(
            backendModule.provideMyGet().selectCity( varApp.cities[itemSelectCity].id.toString(), varApp.SID )
                .delay(varApp.delay_default, varApp.typeUnit)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    viewState.success_select_city(it)
                }, {
                    viewState.onError()
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
