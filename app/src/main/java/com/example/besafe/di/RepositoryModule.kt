package com.example.besafe.di
import com.example.besafe.data.repository.ChatRepositoryImpl
import com.example.besafe.domain.repository.ChatRepository

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
    abstract fun provideChatRepository(chatRepositoryImpl: ChatRepositoryImpl): ChatRepository
}