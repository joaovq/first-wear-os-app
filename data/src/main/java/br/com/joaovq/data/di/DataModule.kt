package br.com.joaovq.data.di

import android.content.Context
import br.com.joaovq.data.user.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    companion object {
        @Provides
        @Singleton
        fun provideUserRepository(
            @ApplicationContext context: Context
        ): UserRepository =
            UserRepository(context)
    }
}