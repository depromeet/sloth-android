package com.depromeet.sloth.extensions

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import kotlinx.coroutines.Job


fun logout(context: Context, view: Fragment, deleteAuthToken: () -> Job) {
    Toast.makeText(context, context.getString(R.string.logout_complete), Toast.LENGTH_SHORT).show()
    deleteAuthToken()
    view.findNavController().navigate(R.id.action_global_to_login)
}

fun showExpireDialog(view: Fragment) {
    view.findNavController().navigate(R.id.action_global_to_expire_dialog)
}

// 회원 탈퇴 API 필요
fun withdrawal(context: Context, view: Fragment, deleteAuthToken: () -> Job) {
    Toast.makeText(context, context.getString(R.string.withdrawal_complete), Toast.LENGTH_SHORT).show()
    deleteAuthToken()
    view.findNavController().navigate(R.id.action_global_to_login)
}




