package com.depromeet.data.di

import com.depromeet.data.repository.LessonRepositoryImpl
import com.depromeet.data.repository.UserAuthRepositoryImpl
import com.depromeet.data.repository.UserProfileRepositoryImpl
import com.depromeet.data.repository.NotificationRepositoryImpl
import com.depromeet.domain.repository.LessonRepository
import com.depromeet.domain.repository.UserAuthRepository
import com.depromeet.domain.repository.UserProfileRepository
import com.depromeet.domain.repository.NotificationRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//TODO 멀티 모듈 구조로 리팩토링 하면서 클래스의 변화가 존재
// 이전과 이후를 비교 및 학습
// di 패키지를 data 모듈에서 app 모듈로 이동시켰더니 빌드됨
// Repository 는 interface 이기 때문에 @Binds 를 사용 해서 Hilt 가 의존성 객체를 생성할 수 있게 설정 해줌

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun provideLessonRepository(lessonRepositoryImpl: LessonRepositoryImpl): LessonRepository

    @Binds
    @Singleton
    abstract fun provideUserProfileRepository(userProfileRepositoryImpl: UserProfileRepositoryImpl): UserProfileRepository

    @Binds
    @Singleton
    abstract fun provideLoginRepository(userAuthRepositoryImpl: UserAuthRepositoryImpl): UserAuthRepository

    @Binds
    @Singleton
    abstract fun provideNotificationRepository(notificationRepositoryImpl: NotificationRepositoryImpl): NotificationRepository
}