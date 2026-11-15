package com.qawmierp.apk.core.storage

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.qawmierp.apk.core.network.QawmiApiService
import com.qawmierp.apk.core.network.dto.BulkAttendanceRequest
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.serialization.json.Json

@HiltWorker
class AttendanceSyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val pendingDao: PendingAttendanceDao,
    private val apiService: QawmiApiService,
    private val json: Json
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val pendingItems = pendingDao.getAll()
        if (pendingItems.isEmpty()) return Result.success()

        var allSucceeded = true
        for (item in pendingItems) {
            val request = runCatching {
                json.decodeFromString(BulkAttendanceRequest.serializer(), item.payloadJson)
            }.getOrNull()

            if (request == null) {
                pendingDao.deleteById(item.id)
                continue
            }

            val response = runCatching { apiService.submitBulkAttendance(request) }.getOrNull()
            if (response != null && response.isSuccessful && response.body()?.success == true) {
                pendingDao.deleteById(item.id)
            } else {
                allSucceeded = false
            }
        }

        return if (allSucceeded) Result.success() else Result.retry()
    }
}
