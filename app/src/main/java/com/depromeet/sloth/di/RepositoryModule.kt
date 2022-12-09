package com.depromeet.sloth.di

import com.depromeet.sloth.domain.repository.LessonRepository
import com.depromeet.sloth.data.repository.LessonRepositoryImpl
import com.depromeet.sloth.domain.repository.LoginRepository
import com.depromeet.sloth.data.repository.LoginRepositoryImpl
import com.depromeet.sloth.domain.repository.MemberRepository
import com.depromeet.sloth.data.repository.MemberRepositoryImpl
import com.depromeet.sloth.domain.repository.NotificationRepository
import com.depromeet.sloth.data.repository.NotificationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun provideLessonRepository(lessonRepositoryImpl: LessonRepositoryImpl): LessonRepository

    @Binds
    @Singleton
    abstract fun provideMemberRepository(memberRepositoryImpl: MemberRepositoryImpl): MemberRepository

    @Binds
    @Singleton
    abstract fun provideLoginRepository(loginRepositoryImpl: LoginRepositoryImpl): LoginRepository

    @Binds
    @Singleton
    abstract fun provideNotificationRepository(notificationRepositoryImpl: NotificationRepositoryImpl): NotificationRepository
}