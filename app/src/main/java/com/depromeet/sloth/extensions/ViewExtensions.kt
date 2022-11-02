package com.depromeet.sloth.extensions

import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


fun <T> MediatorLiveData<T>.addSourceList(vararg liveDataArgument: MutableLiveData<*>, onChanged: () -> T) {
    liveDataArgument.forEach {
        this.addSource(it) { value = onChanged() }
    }
}

fun LifecycleOwner.repeatOnStarted(block: suspend CoroutineScope.() -> Unit) {
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED, block)
    }
}

//fun <T> Fragment.collectLatestLifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
//    viewLifecycleOwner.lifecycleScope.launch {
//        repeatOnLifecycle(Lifecycle.State.STARTED) {
//            flow.collectLatest(collect)
//        }
//    }
//}

// 계속 반복되는 함수이므로 재사용하기 위한 모듈화
//fun <T> Fragment.collectLifecycleFlow(flow: Flow<T>, collect: suspend (T) -> Unit) {
//    viewLifecycleOwner.lifecycleScope.launch {
//        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//            flow.collect(collect)
//        }
//    }
//}

