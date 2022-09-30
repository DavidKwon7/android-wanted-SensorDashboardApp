package com.preonboarding.sensordashboard.domain.model

sealed class PlayType {
    object Play: PlayType()
    object Stop: PlayType()
}