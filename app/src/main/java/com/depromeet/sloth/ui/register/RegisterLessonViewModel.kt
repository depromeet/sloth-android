package com.depromeet.sloth.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.PreferenceManager
import com.depromeet.sloth.data.model.LessonCategory
import com.depromeet.sloth.data.model.LessonSite
import com.depromeet.sloth.data.network.lesson.LessonCategoryResponse
import com.depromeet.sloth.data.network.lesson.LessonRegisterRequest
import com.depromeet.sloth.data.network.lesson.LessonRepository
import com.depromeet.sloth.data.network.lesson.LessonState
import com.depromeet.sloth.data.network.member.MemberRepository
import com.depromeet.sloth.data.network.member.MemberUpdateInfoRequest
import com.depromeet.sloth.ui.DialogState
import com.depromeet.sloth.ui.SlothDialog
import com.depromeet.sloth.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class RegisterLessonViewModel @Inject constructor(
    //private var preferenceManager: PreferenceManager,
    private val lessonRepository: LessonRepository,
) : BaseViewModel() {

//    private val _lessonCategoryList = MutableLiveData<List<LessonCategory>>()
//    val lessonCategoryList: LiveData<List<LessonCategory>> = _lessonCategoryList
//
//    private val _lessonSiteList = MutableLiveData<List<LessonSite>>()
//    val lessonSiteList: LiveData<List<LessonSite>> = _lessonSiteList
//
//    init {
//        //viewModel 이 초기화 될때 호출되도록 (데이터를 요청)
//        fetchLessonCategoryList(preferenceManager.getAccessToken())
//        fetchLessonSiteList(preferenceManager.getAccessToken())
//    }

    suspend fun registerLesson(
        accessToken: String,
        request: LessonRegisterRequest,
    ) = withContext(viewModelScope.coroutineContext) {
        lessonRepository.registerLesson(accessToken, request)
    }

//    private fun fetchLessonCategoryList(accessToken: String) = viewModelScope.launch {
//        val response = lessonRepository.fetchLessonCategoryList(accessToken)
//        response.let {
//            when (it) {
//                is LessonState.Success<List<LessonCategory>> -> {
//                    Log.d("fetch Success", "${it.data}")
////                    setLessonCategoryList(it.data)
//                    _lessonCategoryList.value = it.data!!
//                }
//                is LessonState.Error -> {
//                    Log.d("fetch Error", "${it.exception}")
//                }
//
//                is LessonState.Unauthorized -> {
////                    val dlg = SlothDialog(requireContext(), DialogState.FORBIDDEN)
////                    dlg.onItemClickListener =
////                        object : SlothDialog.OnItemClickedListener {
////                            override fun onItemClicked() {
////                                preferenceManager.removeAuthToken()
////                                startActivity(LoginActivity.newIntent(requireActivity()))
////                            }
////                        }
////                    dlg.start()
//                }
//                is LessonState.NotFound -> {
//                    Log.d("Error", "NotFound")
//                }
//                is LessonState.Forbidden -> {
//                    Log.d("Error", "Forbidden")
//                }
//            }
//        }
//    }
//
//    private fun fetchLessonSiteList(accessToken: String) = viewModelScope.launch {
//        val response = lessonRepository.fetchLessonSiteList(accessToken)
//        response.let {
//            when (it) {
//                is LessonState.Success -> {
//                    Log.d("fetch Success", "${it.data}")
//                    //setLessonSiteList(it.data)
//                    _lessonSiteList.value = it.data!!
//                }
//                is LessonState.Error -> {
//                    Log.d("fetch Error", "${it.exception}")
//                }
//
//                is LessonState.Unauthorized -> {
////                    val dlg = SlothDialog(requireContext(), DialogState.FORBIDDEN)
////                    dlg.onItemClickListener =
////                        object : SlothDialog.OnItemClickedListener {
////                            override fun onItemClicked() {
////                                preferenceManager.removeAuthToken()
////                                startActivity(LoginActivity.newIntent(requireActivity()))
////                            }
////                        }
////                    dlg.start()
//                }
//                is LessonState.NotFound -> {
//                    Log.d("Error", "NotFound")
//                }
//                is LessonState.Forbidden -> {
//                    Log.d("Error", "Forbidden")
//                }
//            }
//        }
//    }

    suspend fun fetchLessonCategoryList(accessToken: String) =
        withContext(viewModelScope.coroutineContext) {
            lessonRepository.fetchLessonCategoryList(accessToken)
        }

    suspend fun fetchLessonSiteList(accessToken: String) =
        withContext(viewModelScope.coroutineContext) {
            lessonRepository.fetchLessonSiteList(accessToken)
        }
}