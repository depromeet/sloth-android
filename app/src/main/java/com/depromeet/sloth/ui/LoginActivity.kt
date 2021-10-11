package com.depromeet.sloth.ui

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.depromeet.sloth.R
import com.depromeet.sloth.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.auth.model.Prompt
import com.kakao.sdk.user.UserApiClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.depromeet.sloth.BuildConfig
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.android.gms.common.api.ApiException
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private lateinit var loginLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loginLauncher = registerForActivityResult(
            StartActivityForResult())
            { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    handleSignInResult(task)
                }
            }

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(BuildConfig.GOOGLE_CLIENT_ID)
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        binding.kakaoLogin.setOnClickListener {
            when (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                true -> loginWithKakaoTalk()
                false -> loginWithKakaoAccount()
            }
        }

        binding.googleLogin.setOnClickListener { view ->
            when(view.id) {
                R.id.google_login -> loginWithGoogle()
            }
        }
    }


    private fun loginWithKakaoTalk() {
        UserApiClient.instance.loginWithKakaoTalk(
            context = this,
        ) { token: OAuthToken?, error: Throwable? ->
            if (error != null) {
                Log.e("로그인 실패", error.message ?: "NULL")
            } else if (token != null) {
                Log.e("로그인 성공 -> accessToken ", token.toString())
                Toast.makeText(this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginWithKakaoAccount() {
        UserApiClient.instance.loginWithKakaoAccount(
            context = this,
            prompts = listOf(Prompt.LOGIN) //보안을 위해 기존의 로그인 여부와 상관없이 재인증 요청시 필요
        ) { token, error ->
            if (error != null) {
                Log.e("로그인 실패", error.message ?: "NULL")
            } else if (token != null) {
                Log.e("로그인 성공 -> accessToken ", token.toString())
                Toast.makeText(this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginWithGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        loginLauncher.launch(signInIntent)

    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            val idToken = account?.idToken.toString()
            val email = account?.email.toString()
            val displayName = account?.displayName.toString()

            Log.d("idToken", idToken)
            Log.d("account", email)
            Log.d("displayName", displayName)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("로그인 실패", "signInResult:failed code=" + e.statusCode)
        }
    }


    override fun onStart() {
        super.onStart()
        val gsa = GoogleSignIn.getLastSignedInAccount(this)

        //기존 로그인 사용자 확인
        if (gsa != null) {
            //updateUI(account)
        }
    }
}