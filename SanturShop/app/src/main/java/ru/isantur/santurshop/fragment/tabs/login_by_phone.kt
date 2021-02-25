package ru.isantur.santurshop.fragment.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import kotlinx.android.synthetic.main.profile_input_phone.view.*
import kotlinx.android.synthetic.main.profile_input_phone.view.cart_btn_send
import ru.isantur.santurshop.R
import ru.isantur.santurshop.varApp


class login_by_phone : Fragment() {

    lateinit var v: View

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {


        v = inflater.inflate(R.layout.profile_input_phone, container, false)




        //CLICK (Войти):
        v.cart_btn_send.setOnClickListener {

            //CHECK fields phone, psw
            if (v.loginPhone.text.toString() == "") {
                YoYo.with(Techniques.Shake).duration(1000).playOn(v.txtLoginPhone);
                v.txtLoginPhone.error = "Обязательное поле"
            }


            if (v.loginPhonePsw.text.toString() == "") {
                YoYo.with(Techniques.Shake).duration(1000).playOn(v.txtLoginPhonePsw);
                v.txtLoginPhonePsw.error = "Обязательное поле"
            }

            //sent get request:
            if ( v.loginPhone.text.toString() != "" && v.loginPhonePsw.text.toString() != "" ) {
                val login = khttp.get("http://isantur.ru/mobile/login/?u=${v.loginPhone.text}&p=${v.loginPhonePsw.text}").headers

                val map = mapOf((login.filter { it.key == "Set-Cookie" }).getValue("Set-Cookie").split(";")[0].split("=")[0] as String to
                        (login.filter { it.key == "Set-Cookie" }).getValue("Set-Cookie").split(";")[0].split("=")[1] as String)

                varApp.setLoginPassword_Preferences(map)

                println(login)

            }

        }


        //Переход на страницу восстановления пароля:
        v.txt_LoginPhone_RecoveryPassword.setOnClickListener {
            it.findNavController().navigate(R.id.go_fromProfileInput_inRecoveryPassword)
        }


        //Переход на страницу регистрации:
        v.txt_LoginPhone_Registration.setOnClickListener {
            it.findNavController().navigate(R.id.go_fromProfileInput_inRegistration)
        }


        //focus\click login:
        v.loginPhone.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) {this.v.txtLoginPhone.error = null } }
        v.loginPhone.setOnClickListener { this.v.txtLoginPhone.error = null }

        //focus\click psw:
        v.loginPhonePsw.setOnFocusChangeListener { v, hasFocus ->   if (hasFocus) { this.v.txtLoginPhonePsw.error = null } }
        v.loginPhonePsw.setOnClickListener { this.v.txtLoginPhonePsw.error = null }



        return v
    }





}