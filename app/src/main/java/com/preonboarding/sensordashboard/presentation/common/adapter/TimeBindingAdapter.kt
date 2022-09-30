package com.preonboarding.sensordashboard.presentation.common.adapter

import android.widget.TextView
import androidx.databinding.BindingAdapter

@BindingAdapter("bindTime")
fun TextView.bindTime(time: Double) {
    this.text = String.format("%.1f", time)
}