package com.depromeet.sloth.extensions

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.depromeet.sloth.ui.custom.DialogState
import com.depromeet.sloth.ui.custom.SlothDialog
import com.depromeet.sloth.ui.login.LoginActivity
import com.depromeet.sloth.util.LoadingDialogUtil
import kotlinx.coroutines.Job

fun handleLoadingState(context: Context) {
    LoadingDialogUtil.showProgress(context)
}

fun showLogoutDialog(context: Context, removeAuthToken: () -> Job) {
    val dlg = SlothDialog(context, DialogState.FORBIDDEN)
    dlg.onItemClickListener = object : SlothDialog.OnItemClickedListener {
        override fun onItemClicked() {
            logout(context) { removeAuthToken() }
        }
    }
    dlg.start()
}

fun logout(context: Context, removeAuthToken: () -> Job) {
    removeAuthToken()
    Toast.makeText(context, "로그아웃 되었어요", Toast.LENGTH_SHORT).show()
    startActivity(
        context,
        Intent(context, LoginActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        },
        null
    )
}
