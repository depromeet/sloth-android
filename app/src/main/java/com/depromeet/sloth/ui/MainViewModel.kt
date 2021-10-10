package com.depromeet.sloth.ui

import androidx.lifecycle.viewModelScope
import com.depromeet.sloth.data.network.HealthRepository
import com.depromeet.sloth.data.network.HealthResponse
import com.depromeet.sloth.data.network.ServiceGenerator
import com.depromeet.sloth.ui.base.BaseViewModel
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlin.coroutines.CoroutineContext

class MainViewModel : BaseViewModel() {
    private val repository = HealthRepository(ServiceGenerator())

    /**
     * Activity나 Fragment단에서 작업의 결과값을 리턴하여 State 분기를 편하게 처리할 수 있음
     *
     * @return Result<HealthResponse>
     */
    suspend fun processHealthWork(
        context: CoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
    ): com.depromeet.sloth.data.network.HealthState<HealthResponse> {
        return viewModelScope.async(context = context, start = start) {
            repository.getHealth()
        }.await()
    }
}