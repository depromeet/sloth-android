package com.depromeet.domain.repository

import com.depromeet.domain.entity.MemberEntity
import com.depromeet.domain.entity.MemberUpdateEntity
import com.depromeet.domain.entity.MemberUpdateRequestEntity
import com.depromeet.domain.util.Result
import kotlinx.coroutines.flow.Flow


interface UserProfileRepository {

     fun fetchMemberInfo(): Flow<Result<MemberEntity>>

    fun updateMemberInfo(memberUpdateRequestEntity: MemberUpdateRequestEntity): Flow<Result<MemberUpdateEntity>>

    suspend fun checkTodayLessonOnBoardingStatus(): Boolean

    suspend fun updateTodayLessonOnBoardingStatus(flag: Boolean)

    suspend fun checkLessonListOnBoardingStatus(): Boolean

    suspend fun updateLessonListOnBoardingStatus(flag: Boolean)
}