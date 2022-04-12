package com.depromeet.sloth.extensions

import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.depromeet.sloth.ui.DialogState
import com.depromeet.sloth.ui.SlothDialog
import com.depromeet.sloth.ui.login.LoginActivity
import com.depromeet.sloth.util.LoadingDialogUtil
import kotlinx.coroutines.Job

fun handleLoadingState(context: Context) {
    LoadingDialogUtil.showProgress(context)
}

fun showLogoutDialog(context: Context, activity: Activity, removeAuthToken: () -> Job ) {
    val dlg = SlothDialog(context, DialogState.FORBIDDEN)
    dlg.onItemClickListener = object : SlothDialog.OnItemClickedListener {
        override fun onItemClicked() {
            logout(context, activity) { removeAuthToken() }
        }
    }
    dlg.start()
}

fun logout(context: Context, activity: Activity, removeAuthToken: () -> Job) {
    removeAuthToken()
    Toast.makeText(context, "로그아웃 되었어요", Toast.LENGTH_SHORT).show()
    startActivity(context, LoginActivity.newIntent(activity), null)
}
