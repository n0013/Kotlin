package ru.isantur.santurshop.fragment.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.include_appbar.view.*
import kotlinx.android.synthetic.main.order_registered.*
import ru.isantur.santurshop.R
import ru.isantur.santurshop.anim
import ru.isantur.santurshop.fragment.checkout.checkout
import ru.isantur.santurshop.fragment.home
import ru.isantur.santurshop.varApp


class order_registered : Fragment() {

    companion object {

        fun instance (): Fragment {
            return order_registered()
        }


        lateinit var v: View



    }



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {


        v = inflater.inflate(R.layout.order_registered, container, false)


        return v
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as AppCompatActivity).supportActionBar!!,
            customAppBar = order_reg_appbar,
            customAppBar_backgroundColor =  R.color.white,
            some_appbar_img_back = true,
            some_title_text =  "",
            some_title_textColor = R.color.type_black,
            some_btn_clear_text = "",
            some_btn_clear_textColor =  R.color.type_black,
            some_btn_clear_visible = false

        )

        varApp.nav_view.visibility = View.VISIBLE

        order_reg_appbar.some_appbar_img_back.setImageDrawable(order_reg_appbar.resources.getDrawable(R.drawable.ic_close))

        order_reg_title.text = "Заказ № ${checkout.Order} на сумму ${checkout.OrderSum}. оформлен"


        //TODO click close:
        order_reg_appbar.some_addbar_view_back.setOnClickListener {
            varApp.clearBackStack(varApp.supportFragmentManager)
            varApp.replaceFragment(varApp.supportFragmentManager, home.instance(), addAnimation = false)
        }




    }

}
