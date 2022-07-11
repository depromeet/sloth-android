package com.depromeet.sloth.di

import com.depromeet.sloth.data.network.lesson.LessonRepository
import com.depromeet.sloth.data.network.lesson.LessonRepositoryImpl
import com.depromeet.sloth.data.network.login.LoginRepository
import com.depromeet.sloth.data.network.login.LoginRepositoryImpl
import com.depromeet.sloth.data.network.member.MemberRepository
import com.depromeet.sloth.data.network.member.MemberRepositoryImpl
import com.depromeet.sloth.data.network.notification.NotificationRepository
import com.depromeet.sloth.data.network.notification.NotificationRepositoryImpl
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