package ru.isantur.santurshop.fragment.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.findFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.element_item_rv.view.*
import kotlinx.android.synthetic.main.include_appbar.*
import kotlinx.android.synthetic.main.profile_info.*
import ru.isantur.santurshop.R
import ru.isantur.santurshop.anim
import ru.isantur.santurshop.fragment.info.info_about_company
import ru.isantur.santurshop.fragment.info.info_about_app
import ru.isantur.santurshop.fragment.info.info_delivery
import ru.isantur.santurshop.fragment.info.info_sale_rules
import ru.isantur.santurshop.fragment.map.map
import ru.isantur.santurshop.varApp

class profile_info : Fragment() {

    companion object {

        fun instance (): Fragment {
            return profile_info()
        }

        lateinit var v: View
        lateinit var fm: FragmentManager
        val arrInfo: ArrayList<String> = ArrayList<String>()




    }



    override fun onCreateView( inflater: LayoutInflater,  container: ViewGroup?, savedInstanceState: Bundle? ): View? {


        v = inflater.inflate(R.layout.profile_info, container, false)

        arrInfo.clear()
        //преобразуем в массив:
        arrInfo.add(0, "О компании")
        arrInfo.add(1, "Доставка")
        arrInfo.add(2, "Правила продажи")
        arrInfo.add(3, "Наши адреса")
        arrInfo.add(4, "Обратная связь")
        arrInfo.add(5, "О приложении")


        return v
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as AppCompatActivity).supportActionBar!!,
            customAppBar = profile_info_appBar,
            customAppBar_backgroundColor =  R.color.primary,
            some_appbar_img_back = true,
            some_title_text =  "Информация",
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false
        )


        //click back:
        some_addbar_view_back.setOnClickListener {

            varApp.clearBackStack(varApp.supportFragmentManager)


        }

        profile_info_rv.setHasFixedSize(true)
        profile_info_rv.layoutManager = LinearLayoutManager( this.context, LinearLayoutManager.VERTICAL, false)
        profile_info_rv.adapter = ProfileInfo()
        profile_info_rv.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))




    }


} //class







class ProfileInfo () : RecyclerView.Adapter<ProfileInfoVH>() {


    override fun getItemCount(): Int { return profile_info.arrInfo.size }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileInfoVH {
        return  ProfileInfoVH(  LayoutInflater.from(parent.context).inflate(R.layout.element_item_rv, parent, false) )
    }


    override fun onBindViewHolder(holder: ProfileInfoVH, position: Int) {

//        holder.itemView.tname.text = arrInfo!![position]
//        holder.itemView.tname2.visibility = View.GONE


        val iv = holder.itemView

        iv.name_rv.text = profile_info.arrInfo[position]
        iv.switch_rv.visibility = ViewGroup.GONE
        iv.name2_rv.visibility = ViewGroup.GONE

    }

}

class ProfileInfoVH(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {

    //навешиваем события
    init {
        v.setOnClickListener ( this )
    }


    override fun onClick(v: View?) {

        when(adapterPosition) {
            0 -> {
                    varApp.replaceFragment(varApp.supportFragmentManager, info_about_company.instance(), addAnimation = true, animation =  anim.from_right_in_left, addToBackStack = true)
                }
            1 -> {
                    varApp.replaceFragment(varApp.supportFragmentManager, info_delivery.instance(), addAnimation = true, animation = anim.from_right_in_left, addToBackStack = true)
                }
            2 -> {
                    varApp.replaceFragment(varApp.supportFragmentManager, info_sale_rules.instance(), addAnimation = true, animation = anim.from_right_in_left, addToBackStack = true)
                }
            3 -> {
                val map: Fragment = map.instance(
                    bundleOf("from" to "other")
                )
                varApp.supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .hide(v!!.findFragment())
                    .add(R.id.nav_host_fragment, map, "map")
                    .show(map)
                    .addToBackStack("map")
                    .commit()

//                    varApp.replaceFragment(varApp.supportFragmentManager, map.instance(
//                        bundleOf(
//                            "from" to "info"
//                        )
//                    ), addAnimation = true, addToBackStack = true)
                }
            4 -> null
            5 -> varApp.replaceFragment(varApp.supportFragmentManager, info_about_app.instance(), addAnimation = true, animation = anim.from_right_in_left, addToBackStack = true)

        }

    }


}

