package com.depromeet.sloth.ui.login

interface LoginListener {
    fun onRegisteredMemberLoginSuccess()
    fun onNewMemberLoginSuccess()
    fun onLoginError()
}
