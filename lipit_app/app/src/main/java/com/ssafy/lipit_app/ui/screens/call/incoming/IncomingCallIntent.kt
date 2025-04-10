package com.ssafy.lipit_app.ui.screens.call.incoming

sealed class IncomingCallIntent {
    object Accept : IncomingCallIntent()
    object Decline : IncomingCallIntent()
}