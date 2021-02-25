package ru.isantur.santurshop.fragment.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import kotlinx.android.synthetic.main.include_appbar.*
import kotlinx.android.synthetic.main.include_status.*
import kotlinx.android.synthetic.main.profile_input.*
import kotlinx.android.synthetic.main.profile_input_email.*

import ru.isantur.santurshop.fragment.tabs.login_by_email
import ru.isantur.santurshop.fragment.tabs.login_by_phone
import ru.isantur.santurshop.R
import ru.isantur.santurshop.activity.Main
import ru.isantur.santurshop.varApp
import kotlin.collections.ArrayList

//import ru.isantur.santurshop.ui.profile.ProfileViewModel

class profile_input : Fragment() {

    companion object {

        fun instance (): Fragment {
            return profile_input()
        }


    }

    override fun onCreateView( inflater: LayoutInflater,  container: ViewGroup?, savedInstanceState: Bundle? ): View? {

        return inflater.inflate(R.layout.profile_input, container, false)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        onLoad(view)

        //TODO click back:
        some_addbar_view_back.setOnClickListener {
            varApp.clearBackStack(varApp.supportFragmentManager)
        }

        //TODO click update:
        status_update.setOnClickListener {
            onLoad(view)
        }

        //TODO tabs for login:
        val adapter = AdapterLogin(childFragmentManager)
//        adapter.addFragment(login_by_phone(), resources.getString(R.string.ByPhone))
        adapter.addFragment(login_by_email(), resources.getString(R.string.ByEmail))

        view_pager.adapter = adapter
        tabs.setupWithViewPager(view_pager)



    }

    fun onLoad(view: View) {
        if (varApp.isNetwork(view.context)) {
            onSuccess()
        }else{
            noInternet()
        }
    }

    fun onSuccess() {

        profile_input_pb.visibility = View.GONE

        profile_input_page.visibility = View.VISIBLE
        profile_input_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as AppCompatActivity).supportActionBar!!,
            customAppBar = profile_input_appbar,
            customAppBar_backgroundColor =  R.color.white,
            some_appbar_img_back = true,
            some_title_text =  "Вход",
            some_title_textColor = R.color.type_black,
            some_btn_clear_text = "",
            some_btn_clear_textColor =  R.color.type_black,
            some_btn_clear_visible = false

        )

        profile_input_status.visibility = View.GONE
        varApp.nav_view.visibility = View.VISIBLE

    }

    fun noInternet() {

        profile_input_pb.visibility = View.GONE
        profile_input_page.visibility = View.GONE

        profile_input_appbar.visibility = View.VISIBLE
        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as Main).supportActionBar!!,
            customAppBar = profile_input_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = false,
            some_title_text = varApp.heading_no_internet,
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false)

        profile_input_status.visibility = View.VISIBLE
        varApp.nav_view.visibility = View.GONE
    }

}



class AdapterLogin(supportFragmentManager: FragmentManager): FragmentStatePagerAdapter(supportFragmentManager) {

    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTitleList = ArrayList<String>()

    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList[position]
    }

    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }
} //Class