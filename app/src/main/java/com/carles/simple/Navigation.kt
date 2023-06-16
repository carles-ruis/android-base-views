package com.carles.simple

import androidx.navigation.NavController
import com.carles.simple.ui.common.safeNavigate
import com.carles.simple.ui.hyrule.MonstersFragmentDirections
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Navigation @Inject constructor() {

    private lateinit var navController: NavController

    fun init(navController: NavController) {
        this.navController = navController
    }

    fun toErrorDialog(message: String, retry: Boolean = false) {
        navController.safeNavigate {
            NavGraphDirections.toErrorDialog(message, retry)
        }
    }

    fun toMonsterDetail(id: Int) {
        navController.safeNavigate {
            MonstersFragmentDirections.toMonsterDetail(id)
        }
    }

    fun up() {
        navController.navigateUp()
    }

    fun upTo(destination: Int) {
        navController.popBackStack(destination, false)
    }
}