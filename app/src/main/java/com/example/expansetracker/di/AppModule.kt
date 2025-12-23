//package com.example.expansetracker.di
//
//import android.content.Context
//import com.example.expansetracker.data.local.UserSessionManager
//import com.example.expansetracker.data.repository.AuthRepository
//import dagger.Module
//import dagger.Provides
//import dagger.hilt.InstallIn
//import dagger.hilt.components.SingletonComponent
//import dagger.hilt.android.qualifiers.ApplicationContext
//import javax.inject.Singleton
//
//@Module
//@InstallIn(SingletonComponent::class)
//object AppModule {
//
//    @Provides
//    @Singleton
//    fun provideUserSessionManager(@ApplicationContext context: Context): UserSessionManager {
//        return UserSessionManager(context)
//    }
//
//    @Provides
//    @Singleton
//    fun provideAuthRepository(sessionManager: UserSessionManager): AuthRepository {
//        return AuthRepository()
//    }
//}
