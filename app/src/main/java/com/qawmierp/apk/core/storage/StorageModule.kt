package com.qawmierp.apk.core.storage

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): QawmiDatabase =
        Room.databaseBuilder(
            context,
            QawmiDatabase::class.java,
            "qawmierp_offline.db"
        ).build()

    @Provides
    fun providePendingAttendanceDao(db: QawmiDatabase): PendingAttendanceDao =
        db.pendingAttendanceDao()

    @Provides
    fun providePortalCacheDao(db: QawmiDatabase): PortalCacheDao =
        db.portalCacheDao()
}
