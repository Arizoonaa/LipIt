package com.ssafy.lipit_app.ui.screens.edit_call.reschedule

data class EditCallState (
    var isFreeModeSelected: Boolean = false,
    var isCategoryModeSelected: Boolean = true,
    val callScheduleId: Long = -1,
    val scheduledDay: String = "",
    val scheduledTime: String = "08:00:00",
    val selectedCategory: String? = null
)