package com.izubot.treinemais.data.di

import com.izubot.treinemais.data.remote.datasource.AuthRemoteDataSource
import com.izubot.treinemais.data.remote.datasource.AuthRemoteDataSourceImpl
import com.izubot.treinemais.data.repository.AuthRepositoryImpl
import com.izubot.treinemais.data.repository.ExerciseRepositoryImpl
import com.izubot.treinemais.data.repository.PrefsRepositoryImpl
import com.izubot.treinemais.data.repository.SyncRepositoryImpl
import com.izubot.treinemais.data.repository.TrainingHistoryRepositoryImpl
import com.izubot.treinemais.data.repository.TrainingRepositoryImpl
import com.izubot.treinemais.data.repository.UserRepositoryImpl
import com.izubot.treinemais.domain.repository.AuthRepository
import com.izubot.treinemais.domain.repository.ExerciseRepository
import com.izubot.treinemais.domain.repository.PrefsRepository
import com.izubot.treinemais.domain.repository.SyncRepository
import com.izubot.treinemais.domain.repository.TrainingHistoryRepository
import com.izubot.treinemais.domain.repository.TrainingRepository
import com.izubot.treinemais.domain.repository.UserRepository
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
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository

    @Binds
    @Singleton
    abstract fun bindAuthRemoteDataSource(
        impl: AuthRemoteDataSourceImpl
    ): AuthRemoteDataSource

    @Binds
    @Singleton
    abstract fun bindPrefsRepository(
        impl: PrefsRepositoryImpl
    ): PrefsRepository

    @Binds
    @Singleton
    abstract fun bindSyncRepository(
        impl: SyncRepositoryImpl
    ): SyncRepository

    @Binds
    @Singleton
    abstract fun bindTrainingRepository(
        impl: TrainingRepositoryImpl
    ): TrainingRepository

    @Binds
    @Singleton
    abstract fun bindExerciseRepository(
        impl: ExerciseRepositoryImpl
    ): ExerciseRepository

    @Binds
    @Singleton
    abstract fun bindTrainingHistoryRepository(
        impl: TrainingHistoryRepositoryImpl
    ): TrainingHistoryRepository
}
