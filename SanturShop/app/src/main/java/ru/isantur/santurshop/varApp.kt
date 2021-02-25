package ru.isantur.santurshop

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color
import android.location.LocationManager
import android.net.ConnectivityManager
import android.os.StrictMode
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.multidex.MultiDexApplication
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
//import com.yandex.mapkit.MapKitFactory
import khttp.get
import kotlinx.android.synthetic.main.alert_dialog.view.*
import kotlinx.android.synthetic.main.include_appbar.view.*
import kotlinx.android.synthetic.main.profile_registration.*
import org.json.JSONArray
import org.json.JSONObject
import ru.isantur.santurshop.Data.*
import ru.isantur.santurshop.di.AppComponent
import ru.isantur.santurshop.di.DaggerAppComponent
import ru.isantur.santurshop.fragment.map.map
import ru.isantur.santurshop.fragment.profile.profile_data
import java.util.concurrent.TimeUnit
import kotlin.coroutines.coroutineContext
import kotlin.math.roundToInt

//class varApp : Application() {
class varApp : MultiDexApplication() {


    //TODO jsonObject
    //        val js =  khttp.get("${varApp.url}GetOrders/?sid=${varApp.SID}").jsonObject
    //        Gson().fromJson(js.toString(), OrderList::class.java)

    //TODO jsonArra
    //         get("${varApp.url}getbrends/").jsonArray
    //        Json.decodeFromString(response.fifth.toString())


    companion object {

        //for dagger:
        lateinit var appComponent: AppComponent

        lateinit var appcontext: Context

        var firstLoad = true

        //общий адрес:
        const val url = "http://isantur.ru/mobile/"

        //for convert from px in dp:
        var convertPxDp: Float = 0.0F

        //for down navigation:
        lateinit var nav_view: BottomNavigationView

        //delay and type delay(ms, s ...) on show the page:
        const val delay_default = 0L
        val typeUnit =  TimeUnit.MILLISECONDS



        lateinit var supportFragmentManager: FragmentManager

        const val heading_no_internet = "No Internet"


        //TODO settings for load page:

        //  ========    profile home    ======================
        var profile_home_show_progress_bar = true





        //*TODO settings for load page



        const val APP_PREFERENCES = "MYSETTINGS"
        private const val COOKIE = "COOKIE"
        private var SPREF: SharedPreferences? = null


        var SID: String = ""

        var brands: ArrayList<Brands> = ArrayList<Brands>()
        var populars: ArrayList<Populars> = ArrayList<Populars>()
        var sales: ArrayList<Sales> = ArrayList<Sales>()
        var catalogs: ArrayList<Catalogs> = ArrayList<Catalogs>()
        var banners: ArrayList<Banners> = ArrayList<Banners>()
        var iam: ArrayList<Iam> = ArrayList<Iam>()
        var cities: ArrayList<Cities> = ArrayList<Cities>()
        var company: ArrayList<Company> = ArrayList<Company>()


        var catalogsGroup: ArrayList<Map<String, String>> = ArrayList()
        var catalogsGroupItem: ArrayList<ArrayList<Map<String, String>>> = ArrayList()

        var products: Products? = null




        fun isGPS(context: Context): Boolean {
            return (context.getSystemService(LOCATION_SERVICE) as LocationManager).isProviderEnabled(LocationManager.GPS_PROVIDER)
        }

        fun isNetwork(context: Context): Boolean {
            return (context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo != null
        }

        @JvmName("SID")
        fun setSID(sid: String) {
            this.SID = sid
        }


        @JvmName("sales")
        fun setSales(sales: ArrayList<Sales>) { this.sales = sales  }
        fun getSalesCount(): Int { return this.sales.size }

        @JvmName("catalogs")
        fun setCatalogs(catalogs: ArrayList<Catalogs>) { this.catalogs = catalogs  }
        fun getCatalogsCount(): Int { return this.catalogs.size }

        @JvmName("CatalogsGoup")
        fun setCatalogsGroup(catalogsGroup: ArrayList<Map<String, String>>) { this.catalogsGroup = catalogsGroup  }
        fun getCatalogsGroupCount(): Int { return this.catalogsGroup.size }

        @JvmName("CatalogsGoupItem")
        fun setCatalogsGroupItem(catalogsGroupItem: ArrayList<ArrayList<Map<String, String>>>) { this.catalogsGroupItem = catalogsGroupItem  }
        fun getCatalogsGroupItemCount(): Int { return this.catalogsGroupItem.size }

        @JvmName("populars")
        fun setPopulars(populars: ArrayList<Populars>) { this.populars = populars  }
        fun getPopularsCount(): Int { return this.populars.size }

        @JvmName("brands")
        fun setBrands(brands: ArrayList<Brands>) { this.brands = brands  }
        fun getBrandsCount(): Int { return this.brands.size }

        @JvmName("banners")
        fun setBanners(banners: ArrayList<Banners>) { this.banners = banners  }
        fun getBannersCount(): Int { return this.banners.size }

        @JvmName("iam")
        fun setiam() {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            val jsJSON = khttp.get("${url}getiam/?sid=${SID}").jsonObject
            iam.clear()
            iam.add(Gson().fromJson<Iam>(jsJSON.toString(), Iam::class.java))

//            if (getLogin_Preferences() == "") {
//                iam.clear()
//                if (cityId != 100000) {
//                    iam.add(Iam(0, "", "", "", "", "", "", 0, city, cityId))
//                } else {
//                    iam.add(Iam(0, "", "", "", "", "", "", 0, city, 100000))
//                }
//            } else {
//
//                val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
//                StrictMode.setThreadPolicy(policy)
//
//                val jsJSON = khttp.get("${url}getiam/?sid=${SID}").jsonObject
//                iam.clear()
//                iam.add(Gson().fromJson<Iam>(jsJSON.toString(), Iam::class.java))
//            }
        }
        fun getiam(): Int { return this.iam.size }

        @JvmName("cities")
        fun setCities(cities: ArrayList<Cities>) { this.cities = cities  }
        fun getCities(): Int { return this.cities.size }

        @JvmName("company")
        fun setCompany(company: ArrayList<Company>) { this.company = company  }
        fun getCompany(): Int { return this.company.size }




        fun RoundToInteger(price: String, needRuble: Boolean, type: String = "Any"): Any {
            var pr = price.replace(",", ".")
            pr = pr.replace(160.toChar().toString(), "").replace(34.toChar().toString(), "")

            if (type == "Double") {
                return pr.toDouble()
            } else if (type == "Int") {
                return pr.toDouble().roundToInt()
            }

            if (needRuble) {
                return String.format("%,d", pr.toDouble().toInt() ).replace(",", " ") + " Р"
            } else {
                return String.format("%,d", pr.toDouble().toInt() )
            }

        }


        fun getLogin_Preferences(): String {
            return SPREF!!.getString("login", "").toString().replace("[", "").replace("]", "")
        }

        fun getPassword_Preferences(): String {
            return SPREF!!.getString("password", "").toString().replace("[", "").replace("]", "")
        }

        fun setLoginPassword_Preferences(loginPassword: Map<String, String>) {
            //login
            SPREF!!
                .edit()
                .putString("login", loginPassword.keys.toString())
                .apply()

            //password
            SPREF!!
                .edit()
                .putString("password", loginPassword.values.toString())
                .apply()
        }

        fun clear_LoginPassword_Preferences () {
            SPREF!!.edit().putString("login", "").apply()
            SPREF!!.edit().putString("password", "").apply()

        }



        fun getSessionId_Preferences(): String {
            return SPREF!!.getString("SessionId", "").toString().replace("[", "").replace("]", "")
        }

        fun setSessionId_Preferences(SessionId: String) {
            SPREF!! .edit() .putString("SessionId", SessionId) .apply()
        }

        fun clear_SessionId_Preferences () {
            SPREF!!.edit().putString("SessionId", "").apply()
        }


        fun Toast(context: Context, text: String) {
            val toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
            toast.setGravity(Gravity.CENTER, 0, 0)
            toast.show()
        }



        fun ShowAppBar(bar: ActionBar, title: String, showUpButton: Boolean = true) {
            bar.title = title
            bar.show()

            if (showUpButton ) {
                bar.setDisplayHomeAsUpEnabled(true)
            } else {
                bar.setDisplayHomeAsUpEnabled(false)
            }

        }


        fun changeCustomAppBar(standartAppBar_visible: Boolean = false,
                               standartAppBar: ActionBar?,
                               customAppBar: View,
                               customAppBar_backgroundColor: Int = R.color.primary,
                               some_appbar_img_back: Boolean = false,
                               some_appbar_btn_search: Boolean = false,
                               some_title_text: String = "Some Title",
                               some_title_textColor: Int = R.color.white,
                               some_btn_clear_text: String = "Some text button",
                               some_btn_clear_textColor: Int = R.color.white,
                               some_btn_clear_visible: Boolean = false


        ) {

            //show\hide standartAppBar:
            if (standartAppBar_visible) {
                standartAppBar?.show()
            } else {
                standartAppBar?.hide()
            }


            if (some_appbar_img_back == true) {
                customAppBar.some_appbar_img_back.visibility = View.VISIBLE
                customAppBar.some_addbar_view_back.visibility = View.VISIBLE
            } else {
                customAppBar.some_appbar_img_back.visibility = View.GONE
                customAppBar.some_addbar_view_back.visibility = View.GONE
            }


            if (some_appbar_btn_search == true) {
                customAppBar.some_appbar_btn_search.visibility = View.VISIBLE
                customAppBar.some_appbar_view_search.visibility = View.VISIBLE
            } else {
                customAppBar.some_appbar_btn_search.visibility = View.GONE
                customAppBar.some_appbar_view_search.visibility = View.GONE
            }


            if (customAppBar_backgroundColor == R.color.white) {
                customAppBar.some_appbar_img_back.setImageDrawable(customAppBar.resources.getDrawable(R.drawable.ic_back))
            } else if (customAppBar_backgroundColor == R.color.primary) {
                customAppBar.some_appbar_img_back.setImageDrawable(customAppBar.resources.getDrawable(R.drawable.ic_back_additionally))
            }

            if (customAppBar_backgroundColor != R.color.primary) {
                customAppBar.setBackgroundColor(customAppBar.resources.getColor(R.color.white))
            }

            //изменили цвет текста (title)
            if (some_title_textColor != R.color.white) {
                customAppBar.some_appbar_title.setTextColor(customAppBar.resources.getColor(R.color.type_black))
            }

            if (some_btn_clear_textColor != R.color.white) {
                customAppBar.some_appbar_btn_clear.setTextColor(customAppBar.resources.getColor(R.color.primary))
            }

            if (some_btn_clear_visible) {
                customAppBar.some_appbar_btn_clear.visibility = View.VISIBLE
            } else {
                customAppBar.some_appbar_btn_clear.visibility = View.GONE
            }

            customAppBar.some_appbar_title.text = some_title_text
            customAppBar.some_appbar_btn_clear.text = some_btn_clear_text


        }

        fun delete_step_from_BackStack(fm: FragmentManager, step: String) {
            val backStackCount: Int = fm.getBackStackEntryCount()
            for (i in 0 until backStackCount) {

                if (fm.getBackStackEntryAt(i).name == step) {
                    val backStackId: Int = fm.getBackStackEntryAt(i).getId()
                    fm.popBackStack( backStackId, FragmentManager.POP_BACK_STACK_INCLUSIVE )
                }

            }
        }

        fun clearBackStack(fm: FragmentManager) {
            fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

        fun replaceFragment(
            fm: FragmentManager,
            fragment: Fragment,
            addAnimation: Boolean = true,
            animation: anim = anim.from_right_in_left,
            addToBackStack: Boolean = false
        ): Fragment {

            val fragmentTransaction = fm.beginTransaction()


            //TODO Annimation open window (fragment)
            //Эффект открытия и закрытия (снизу-вверх, и обратно)
            //            .setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up)         //поднимается снизу в верх
            //            .setCustomAnimations(R.anim.slide_out_down, R.anim.slide_in_down)     //обратно

            //Эффект открытия и закрытия (слева-направо, и обратно)
            //            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)    //выезжает справа
            //            .setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_right)    //выезжает слева на право
            //*TODO


            if (addAnimation) {

                when (animation) {
                    anim.from_bottom_in_top -> {
                        fragmentTransaction.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up)
                    }
                    anim.from_top_in_bottom -> {
                        fragmentTransaction.setCustomAnimations(R.anim.slide_out_down, R.anim.slide_in_down)
                    }
                    anim.from_right_in_left -> {
                        fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    }
                    anim.from_left_in_right -> {
                        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right, R.anim.slide_in_left, R.anim.slide_out_right)
                    }
                }
            }



            fragmentTransaction.replace(R.id.nav_host_fragment, fragment, fragment.javaClass.simpleName)


            if (addToBackStack) {
                fragmentTransaction.addToBackStack(fragment.javaClass.simpleName)
            }
            fragmentTransaction.commit()
            return fragment
        }


        fun addBadge(id: Int, count : Int) {
            val badge: BadgeDrawable = nav_view.getOrCreateBadge( id )
            badge.number = count
            badge.isVisible = true
            badge.backgroundColor = nav_view.resources.getColor(R.color.prymary_light)

        }

        fun removeBadge(id: Int) {
            nav_view.removeBadge(id)
        }

        fun get(query: String, type: types = types.text): String {

            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)

            var response = ""
            if (isNetwork(appcontext)) {

                if (type == types.text) {
                    response = khttp.get("${url}" + query).text
                } else if (type == types.jsonObject) {
                    response = khttp.get("${url}" + query).jsonObject.toString()
                } else {
                    response = khttp.get("${url}" + query).jsonArray.toString()
                }

            } else {
                response = "noInternet"
            }

            return response

        }


        fun showDialog (v: View, context: Context, actionBar: ActionBar, title: String, body: String) {

            Utils.hideKeyboard( appcontext, v)

            val dlgView = LayoutInflater.from( context ).inflate(R.layout.alert_dialog, null)
            dlgView.dialog_appbar.visibility = View.VISIBLE

            changeCustomAppBar(
                standartAppBar_visible = false,
                standartAppBar = actionBar,
                customAppBar = dlgView.dialog_appbar,
                customAppBar_backgroundColor =  R.color.white,
                some_appbar_img_back = false,
                some_appbar_btn_search = false,
                some_title_text =  title,
                some_title_textColor = R.color.type_black,
                some_btn_clear_text = "",
                some_btn_clear_textColor =  R.color.primary,
                some_btn_clear_visible = false

            )


            dlgView.dialog_text.text = body
            dlgView.dialog_big_button.visibility = View.GONE
            dlgView.dialog_rv.visibility = View.GONE
            dlgView.dialog_cancel.visibility = View.GONE
            dlgView.dialog_action.text = "Ок"


            val builder = AlertDialog.Builder( context )
            with(builder) { setView(dlgView) }

            val dlg = builder.show()

            //action:
            dlgView.dialog_action.setOnClickListener { dlg.dismiss() }


        }

        fun firstUpperCase(word: String?): String? {
            return if (word == null || word.isEmpty()) "" else word.substring(0, 1).toUpperCase() + word.substring(1) //или return word;
        }




    } //companion object



    override fun onCreate() {
        convertPxDp  = applicationContext.resources.displayMetrics.density

        SPREF = applicationContext.getSharedPreferences("MYSETTINGS", Context.MODE_PRIVATE)

        appcontext = applicationContext


        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

//        MapKitFactory.setApiKey("6ae725c7-e941-4824-835f-f39cbd39916e")
//        MapKitFactory.initialize(applicationContext)

        super.onCreate()
        
        initDagger()

    }


    private fun initDagger (){
        appComponent = DaggerAppComponent.builder()
            .build()

    }


} //class varApp




enum class anim {
    from_bottom_in_top,
    from_top_in_bottom,
    from_right_in_left,
    from_left_in_right
}

enum class AppResponse {
    onSuccess,
    noInternet,
    isEmpty
}

enum class types {
    text,
    jsonObject,
    jsonArray
}


open class FadeInLinearLayoutManager : LinearLayoutManager {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(context, orientation, reverseLayout)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

    private val enterInterpolator = AnticipateOvershootInterpolator(2f)

    override fun addView(child: View, index: Int) {
        super.addView(child, index)
        val h = 400f
        // if index == 0 item is added on top if -1 it's on the bottom
        child.translationY = if(index == 0) -h else h
        // begin animation when view is laid out
        child.alpha = 0.3f
        child.animate().translationY(0f).alpha(1f)
            .setInterpolator(enterInterpolator)
            .setDuration(600L)
    }
}