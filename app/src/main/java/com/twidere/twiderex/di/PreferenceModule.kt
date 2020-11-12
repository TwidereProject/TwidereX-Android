package com.twidere.twiderex.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.createDataStore
import com.twidere.twiderex.preferences.proto.AppearancePreferences
import com.twidere.twiderex.preferences.proto.DisplayPreferences
import com.twidere.twiderex.preferences.serializer.AppearancePreferencesSerializer
import com.twidere.twiderex.preferences.serializer.DisplayPreferencesSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object PreferenceModule {
    @Singleton
    @Provides
    fun provideAppearances(@ApplicationContext context: Context): DataStore<AppearancePreferences> =
        context .createDataStore("appearances.pb", AppearancePreferencesSerializer)
    @Singleton
    @Provides
    fun provideDisplay(@ApplicationContext context: Context): DataStore<DisplayPreferences> =
        context .createDataStore("display.pb", DisplayPreferencesSerializer)
}
