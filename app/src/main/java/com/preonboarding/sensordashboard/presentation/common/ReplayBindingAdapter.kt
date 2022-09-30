package com.preonboarding.sensordashboard.presentation.common

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.preonboarding.sensordashboard.domain.model.PlayType
import com.preonboarding.sensordashboard.domain.model.ViewType

@BindingAdapter("timeText")
fun applyMilliFormat(view: TextView, time: Int) {
    view.text = String.format("%.1f", time / 10f)
}

@BindingAdapter("stopVisibilityPlayType", "stopVisibilityViewType")
fun changeStopVisibility(view: ImageView, playType: PlayType?, viewType: ViewType?) {
    if (viewType == null || playType == null) return
    when (viewType) {
        ViewType.REPLAY -> {
            when (playType) {
                is PlayType.Stop -> {
                    view.visibility = View.VISIBLE
                }
                is PlayType.Play -> {
                    view.visibility = View.GONE
                }
            }
        }
        ViewType.VIEW -> {
            view.visibility = View.GONE
        }
        ViewType.INITIAL -> {
            // Error Status, when initial status, users cannot enter replay fragment
        }
    }
}

@BindingAdapter("playVisibilityPlayType", "playVisibilityViewType")
fun changePlayVisibility(view: ImageView, playType: PlayType?, viewType: ViewType?) {
    if (viewType == null || playType == null) return

    when (viewType) {
        ViewType.REPLAY -> {
            when (playType) {
                is PlayType.Stop -> {
                    view.visibility = View.GONE
                }
                is PlayType.Play -> {
                    view.visibility = View.VISIBLE
                }
            }
        }
        ViewType.VIEW -> {
            view.visibility = View.GONE
        }
        ViewType.INITIAL -> {
            // Error Status, when initial status, users cannot enter replay fragment
        }
    }
}

@BindingAdapter("timerVisibilityPlayType", "timerVisibilityViewType")
fun changeTimerVisibility(view: TextView, playType: PlayType?, viewType: ViewType?) {
    if (viewType == null || playType == null) return

    when (viewType) {
        ViewType.REPLAY -> {
            when (playType) {
                is PlayType.Stop -> {
                    view.visibility = View.GONE
                }
                is PlayType.Play -> {
                    view.visibility = View.VISIBLE
                }
            }
        }
        ViewType.VIEW -> {
            view.visibility = View.GONE
        }
        ViewType.INITIAL -> {
            // Error Status, when initial status, users cannot enter replay fragment
        }
    }
}