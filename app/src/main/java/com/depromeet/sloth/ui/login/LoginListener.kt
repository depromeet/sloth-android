package com.depromeet.sloth.ui.login

interface LoginListener {
    fun onSuccessWithRegisteredMember()
    fun onSuccessWithNewMember()
    fun onError()
}
