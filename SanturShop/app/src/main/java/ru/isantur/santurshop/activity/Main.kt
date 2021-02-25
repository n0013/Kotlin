package ru.isantur.santurshop.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.isantur.santurshop.R
import ru.isantur.santurshop.anim
import ru.isantur.santurshop.fragment.cart
import ru.isantur.santurshop.fragment.favorite
import ru.isantur.santurshop.fragment.home
import ru.isantur.santurshop.fragment.catalog.catalog
import ru.isantur.santurshop.fragment.profile.profile_home
import ru.isantur.santurshop.fragment.profile.profile_my
import ru.isantur.santurshop.varApp


class Main : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        //init supportFragmentManager:
        varApp.supportFragmentManager = supportFragmentManager

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

//        val navController = findNavController( R.id.nav_host_fragment)
//
//        val appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.navigation_home,
//                R.id.navigation_catalog,
//                R.id.navigation_favorite,
//                R.id.navigation_cart,
//                R.id.navigation_profile_
//
//            )
//        )
//
//
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)






        //default:
        varApp.replaceFragment(supportFragmentManager, home.instance(), addAnimation = false)


        varApp.nav_view = navView


        navView.setOnNavigationItemSelectedListener {

            varApp.clearBackStack(supportFragmentManager)

            when (it.itemId) {
                R.id.navigation_home -> {
                    varApp.nav_view.menu.getItem(0).setChecked(true)
                    varApp.replaceFragment(supportFragmentManager, home.instance(), addAnimation = true, animation = anim.from_left_in_right)
                }

                R.id.navigation_catalog -> {
                    varApp.nav_view.menu.getItem(1).setChecked(true)
                    varApp.replaceFragment(supportFragmentManager, catalog.instance(), addAnimation = false)
                }

                R.id.navigation_favorite -> {
                    varApp.nav_view.menu.getItem(2).setChecked(true)
//                    varApp.nav_view.menu.getItem(2).icon = resources.getDrawable(R.drawable.ic_fav_active_menu)
                    varApp.replaceFragment(supportFragmentManager, favorite.instance(), addAnimation = false)
                }

                R.id.navigation_cart -> {
                    varApp.nav_view.menu.getItem(3).setChecked(true)
//                    varApp.nav_view.menu.getItem(3).icon = resources.getDrawable(R.drawable.ic_cart_active)
                    varApp.replaceFragment(supportFragmentManager, cart.instance(), addAnimation = true, animation =  anim.from_bottom_in_top)
                }

                R.id.navigation_profile_ -> {
                    varApp.nav_view.menu.getItem(4).setChecked(true)
                    //залогинен:
                    if (varApp.iam[0].id != 0) {
                        varApp.replaceFragment(supportFragmentManager, profile_my.instance(), addAnimation = true, animation = anim.from_left_in_right)
                    } else { //не залогинен
                        varApp.replaceFragment(supportFragmentManager, profile_home.instance(), addAnimation = true, animation = anim.from_left_in_right)
                    }


                }

            }





            return@setOnNavigationItemSelectedListener true
        }





    }


    fun disabled_active_menus () {

        varApp.nav_view.menu.getItem(2).icon = resources.getDrawable(R.drawable.ic_fav)
        varApp.nav_view.menu.getItem(3).icon = resources.getDrawable(R.drawable.ic_cart)
    }


    override fun onBackPressed() {
        super.onBackPressed()
        println("onBackPressed")

        val fm = supportFragmentManager



        for(i in 0 until fm.backStackEntryCount){

//            if (fm.getBackStackEntryAt(i).name == "checkout") {
//                varApp.delete_step_from_BackStack(varApp.supportFragmentManager, "checkout")
//            }


            if (fm.getBackStackEntryAt(i).name == "map") {
//                varApp.clearBackStack(supportFragmentManager)
                varApp.delete_step_from_BackStack(varApp.supportFragmentManager, "map")
//            }else if (fm.getBackStackEntryAt(i).name == "checkout" ) {
//                varApp.delete_step_from_BackStack(varApp.supportFragmentManager, "checkout")
            }
        }





    }







}
