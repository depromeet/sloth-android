package com.depromeet.sloth.extensions

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.depromeet.sloth.R
import com.depromeet.sloth.ui.custom.DialogState
import com.depromeet.sloth.ui.custom.SlothDialog
import com.depromeet.sloth.ui.login.LoginActivity
import kotlinx.coroutines.Job


fun showForbiddenDialog(context: Context, removeAuthToken: () -> Job) {
    val dlg = SlothDialog(context, DialogState.FORBIDDEN)
    dlg.onItemClickListener = object : SlothDialog.OnItemClickedListener {
        override fun onItemClicked() {
            logout(context) { removeAuthToken() }
        }
    }
    dlg.show()
}

fun logout(context: Context, removeAuthToken: () -> Job) {
    removeAuthToken()
    Toast.makeText(context, context.getString(R.string.logout_complete), Toast.LENGTH_SHORT).show()
    startActivity(
        context,
        Intent(context, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        },
        null
    )
}

fun showWithdrawalDialog(context: Context, removeAuthToken: () -> Job) {
    val dlg = SlothDialog(context, DialogState.FORBIDDEN)
    dlg.onItemClickListener = object : SlothDialog.OnItemClickedListener {
        override fun onItemClicked() {
            withdrawal(context) { removeAuthToken() }
        }
    }
    dlg.show()
}

// 회원 탈퇴 api 필요
fun withdrawal(context: Context, removeAuthToken: () -> Job) {
    removeAuthToken()
    Toast.makeText(context, context.getString(R.string.withdrawal_complete), Toast.LENGTH_SHORT)
        .show()
    startActivity(
        context,
        Intent(context, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        },
        null
    )
}

fun showWaitDialog(context: Context) {
    val dlg = SlothDialog(context, DialogState.WAIT)
    dlg.show()
}




