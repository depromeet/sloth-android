package com.depromeet.sloth.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.depromeet.sloth.databinding.ActivityLoginBinding
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.auth.model.Prompt
import com.kakao.sdk.user.UserApiClient

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.kakaoLogin.setOnClickListener {
            when (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                true -> loginWithKakaoTalk()
                false -> loginWithKakaoAccount()
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
}