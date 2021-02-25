package ru.isantur.santurshop.fragment.info

import android.app.PendingIntent.getActivity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.element_item_rv.view.*
import kotlinx.android.synthetic.main.include_appbar.*
import kotlinx.android.synthetic.main.info_about_app.*
import ru.isantur.santurshop.R
import ru.isantur.santurshop.activity.Main
import ru.isantur.santurshop.varApp


class info_about_app : Fragment() {

    companion object {

        fun instance (): Fragment {
            return info_about_app()
        }

        lateinit var v: View
        val arr_iaa: ArrayList<String> = ArrayList()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        v = inflater.inflate(R.layout.info_about_app, container, false)


        arr_iaa.clear()
        arr_iaa.add(0, "Написать в поддержку")
        arr_iaa.add(1, "Политика обработки персональных\n" + "данных")


        return v
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        varApp.changeCustomAppBar(
            standartAppBar_visible = false,
            standartAppBar = (activity as AppCompatActivity).supportActionBar!!,
            customAppBar = iaa_appbar,
            customAppBar_backgroundColor = R.color.primary,
            some_appbar_img_back = true,
            some_title_text = "О приложении",
            some_title_textColor = R.color.white,
            some_btn_clear_text = "",
            some_btn_clear_textColor = R.color.white,
            some_btn_clear_visible = false)


        iaa_rv.setHasFixedSize(true)
        iaa_rv.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        iaa_rv.adapter = AdapterIaa()
        iaa_rv.addItemDecoration(DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL))


        some_addbar_view_back.setOnClickListener {
            varApp.delete_step_from_BackStack(varApp.supportFragmentManager, step = this::class.java.simpleName)
        }

    } //onViewCreated








} //class



class AdapterIaa : RecyclerView.Adapter<AdapterIaaVH>() {

    override fun getItemCount(): Int { return info_about_app.arr_iaa.size }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterIaaVH {
        return  AdapterIaaVH(LayoutInflater.from(parent.context).inflate(R.layout.element_item_rv, parent, false))
    }


    override fun onBindViewHolder(holder: AdapterIaaVH, position: Int) {

        val v = holder.itemView


        v.name_rv.text = info_about_app.arr_iaa[position]

        v.img_rv.visibility = View.GONE
        v.img_right_rv.visibility = View.VISIBLE



    }

} //Class Popular

class AdapterIaaVH(v: View) : RecyclerView.ViewHolder(v) {

    //навешиваем события
    init {
        v.setOnClickListener {
            when (adapterPosition) {
                0 -> {
                }
                1 -> {
                }
                2 -> {
                    val intt = Intent(Intent.ACTION_VIEW, Uri.parse("https://msantehnik.ru/soglasie-na-obrabotku-personalnih-dannih"))
//                    val intent = Intent(getActivity(), otherActivity::class.java)




                }
            }
        }
    }





} //Class PopularVH
