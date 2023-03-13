package com.depromeet.domain.repository

import com.depromeet.domain.entity.MemberEntity
import com.depromeet.domain.entity.MemberUpdateEntity
import com.depromeet.domain.entity.MemberUpdateRequestEntity
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow


interface MemberRepository {

     fun fetchMemberInfo(): Flow<Result<MemberEntity>>

    fun updateMemberInfo(memberUpdateRequestEntity: MemberUpdateRequestEntity): Flow<Result<MemberUpdateEntity>>

    fun logout(): Flow<Result<String>>

    suspend fun fetchTodayLessonOnBoardingStatus(): Boolean

    suspend fun updateTodayLessonOnBoardingStatus(flag: Boolean)

    suspend fun fetchLessonListOnBoardingStatus(): Boolean

    suspend fun updateLessonListOnBoardingStatus(flag: Boolean)

    suspend fun deleteAuthToken()
}