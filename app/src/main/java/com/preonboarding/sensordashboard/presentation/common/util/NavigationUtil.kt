package com.preonboarding.sensordashboard.presentation.common.util

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController

object NavigationUtil {

    fun View.navigate(action: Int) {
        Navigation.findNavController(this).navigate(action)
    }

    fun Fragment.navigate(action: Int) {
        this.findNavController().navigate(action)
    }

    fun Fragment.navigateWithBundle(action: Int, bundle: Bundle) {
        this.findNavController().navigate(action, bundle)
    }

    fun Fragment.navigateUp() {
        this.findNavController().navigateUp()
    }

    fun Fragment.popBackStack() {
        this.findNavController().popBackStack()
    }
}