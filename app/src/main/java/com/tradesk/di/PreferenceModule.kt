package com.tradesk.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.tradesk.di.Qualifier.PreferenceInfo
import com.tradesk.preferences.AppPreferenceHelper
import com.tradesk.preferences.PreferenceConstants
import com.tradesk.preferences.PreferenceHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class PreferenceModule {

    @Singleton
    @Provides
    @PreferenceInfo
    internal fun provideprefFileName(): String = PreferenceConstants.SharedPrefenceName


//    @Provides
//    @ApplicationContext
//    internal fun provideContext(application: Application): Context =
//        application

    @Provides
    @Singleton
    internal fun providePrefHelper(appPreferenceHelper: AppPreferenceHelper): PreferenceHelper =
        appPreferenceHelper

}