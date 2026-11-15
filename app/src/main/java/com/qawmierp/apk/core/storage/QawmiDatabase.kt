package com.qawmierp.apk.core.storage

import androidx.room.*

@Entity(tableName = "pending_attendance")
data class PendingAttendanceEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val date: String,
    val payloadJson: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "portal_cache")
data class PortalCacheEntity(
    @PrimaryKey val cacheKey: String,
    val payloadJson: String,
    val updatedAt: Long = System.currentTimeMillis()
)

@Dao
interface PendingAttendanceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: PendingAttendanceEntity)

    @Query("SELECT * FROM pending_attendance ORDER BY createdAt ASC")
    suspend fun getAll(): List<PendingAttendanceEntity>

    @Query("DELETE FROM pending_attendance WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM pending_attendance")
    suspend fun count(): Int
}

@Dao
interface PortalCacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(cache: PortalCacheEntity)

    @Query("SELECT * FROM portal_cache WHERE cacheKey = :key LIMIT 1")
    suspend fun get(key: String): PortalCacheEntity?
}

@Database(
    entities = [PendingAttendanceEntity::class, PortalCacheEntity::class],
    version = 1,
    exportSchema = false
)
abstract class QawmiDatabase : RoomDatabase() {
    abstract fun pendingAttendanceDao(): PendingAttendanceDao
    abstract fun portalCacheDao(): PortalCacheDao
}
