package com.izubot.treinemais.data.di

import com.izubot.treinemais.data.remote.datasource.AuthRemoteDataSource
import com.izubot.treinemais.data.remote.datasource.AuthRemoteDataSourceImpl
import com.izubot.treinemais.data.repository.AuthRepositoryImpl
import com.izubot.treinemais.domain.repository.AuthRepository
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
    abstract fun bindAuthRepository(
        impl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindAuthRemoteDataSource(
        impl: AuthRemoteDataSourceImpl
    ): AuthRemoteDataSource

}