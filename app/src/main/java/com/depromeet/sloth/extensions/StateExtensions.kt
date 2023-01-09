package com.depromeet.sloth.extensions

import android.content.Context
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.depromeet.sloth.R
import com.depromeet.sloth.presentation.custom.DialogState
import com.depromeet.sloth.presentation.custom.SlothDialog
import kotlinx.coroutines.Job


fun showForbiddenDialog(context: Context, view: Fragment, deleteAuthToken: () -> Job) {
    val dlg = SlothDialog(context, DialogState.FORBIDDEN)
    dlg.onItemClickListener = object : SlothDialog.OnItemClickedListener {
        override fun onItemClicked() {
            logout(context, view) { deleteAuthToken() }
        }
    }
    dlg.show()
}

fun logout(context: Context, view: Fragment, deleteAuthToken: () -> Job) {
    Toast.makeText(context, context.getString(R.string.logout_complete), Toast.LENGTH_SHORT).show()
    deleteAuthToken()
    view.findNavController().navigate(R.id.action_global_logout)
}

fun showWithdrawalDialog(context: Context, view: Fragment, deleteAuthToken: () -> Job) {
    val dlg = SlothDialog(context, DialogState.FORBIDDEN)
    dlg.onItemClickListener = object : SlothDialog.OnItemClickedListener {
        override fun onItemClicked() {
            withdrawal(context, view) { deleteAuthToken() }
        }
    }
    dlg.show()
}

// 회원 탈퇴 api 필요
fun withdrawal(context: Context, view: Fragment, deleteAuthToken: () -> Job) {
    Toast.makeText(context, context.getString(R.string.withdrawal_complete), Toast.LENGTH_SHORT).show()
    deleteAuthToken()
    view.findNavController().navigate(R.id.action_global_logout)
}

fun showWaitDialog(context: Context) {
    val dlg = SlothDialog(context, DialogState.WAIT)
    dlg.show()
}




