package com.example.besafe.di
import com.example.besafe.data.repository.ChatRepositoryImpl
import com.example.besafe.data.repository.ContactRepositoryImpl
import com.example.besafe.data.repository.LocationTrackerImpl
import com.example.besafe.domain.repository.ChatRepository
import com.example.besafe.domain.repository.ContactRepository
import com.example.besafe.domain.repository.LocationTracker
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
    abstract fun provideContactRepository(contactRepositoryImpl: ContactRepositoryImpl): ContactRepository

    @Binds
    @Singleton
    abstract fun provideChatRepository(chatRepositoryImpl: ChatRepositoryImpl): ChatRepository
}