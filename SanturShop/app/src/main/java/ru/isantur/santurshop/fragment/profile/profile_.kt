package ru.isantur.santurshop.fragment.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import ru.isantur.santurshop.R
import ru.isantur.santurshop.varApp


class profile_ : Fragment() {

    lateinit var v: View


    override fun onCreateView( inflater: LayoutInflater,  container: ViewGroup?, savedInstanceState: Bundle? ): View? {


        v = inflater.inflate(R.layout.profile_, container, false)

        //залогинен:
        if (varApp.iam[0].id != 0 ) {

//            container!!.findNavController().navigate(R.id.go_profileMy)


        } else {

//            container!!.findNavController().navigate(R.id.go_profileHome)


        }

        return v
    }




} //class