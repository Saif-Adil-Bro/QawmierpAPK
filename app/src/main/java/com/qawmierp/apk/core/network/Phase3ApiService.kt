package com.qawmierp.apk.core.network

import com.qawmierp.apk.core.network.dto.*
import retrofit2.Response
import retrofit2.http.*

interface Phase3ApiService {

    // Boarding
    @GET("boarding/meals")
    suspend fun getBoardingMeals(@Query("page") page: Int = 1, @Query("limit") limit: Int = 20): Response<ApiResponse<List<BoardingMealDto>>>

    @GET("boarding/bazar")
    suspend fun getBoardingBazar(@Query("page") page: Int = 1, @Query("limit") limit: Int = 20): Response<ApiResponse<List<BoardingBazarDto>>>

    // Library
    @GET("library/books")
    suspend fun getLibraryBooks(@Query("page") page: Int = 1, @Query("limit") limit: Int = 20): Response<ApiResponse<List<LibraryBookDto>>>

    @GET("library/issues")
    suspend fun getBookIssues(@Query("page") page: Int = 1, @Query("limit") limit: Int = 20): Response<ApiResponse<List<BookIssueDto>>>

    // Inventory & Transport
    @GET("inventory")
    suspend fun getInventory(@Query("page") page: Int = 1, @Query("limit") limit: Int = 20): Response<ApiResponse<List<InventoryItemDto>>>

    @GET("transport")
    suspend fun getTransport(@Query("page") page: Int = 1, @Query("limit") limit: Int = 20): Response<ApiResponse<List<TransportDto>>>

    // Notices & Communication
    @GET("notices")
    suspend fun getNotices(@Query("page") page: Int = 1, @Query("limit") limit: Int = 20): Response<ApiResponse<List<NoticeDto>>>

    @POST("notices")
    suspend fun createNotice(@Body request: CreateNoticeRequest): Response<ApiResponse<NoticeDto>>

    @GET("sms-logs")
    suspend fun getSmsLogs(@Query("page") page: Int = 1, @Query("limit") limit: Int = 20): Response<ApiResponse<List<SmsLogDto>>>

    @GET("parent-communication")
    suspend fun getParentCommunications(@Query("page") page: Int = 1, @Query("limit") limit: Int = 20): Response<ApiResponse<List<ParentCommunicationDto>>>

    // Tarbiyah, Child Safety & Biometric
    @GET("tarbiyah/badges")
    suspend fun getTarbiyahBadges(): Response<ApiResponse<List<TarbiyahBadgeDto>>>

    @GET("tarbiyah/amal-tracker")
    suspend fun getAmalTracker(@Query("page") page: Int = 1, @Query("limit") limit: Int = 20): Response<ApiResponse<List<AmalTrackerDto>>>

    @GET("child-safety")
    suspend fun getChildSafety(): Response<ApiResponse<List<ChildSafetyDto>>>

    @GET("biometric")
    suspend fun getBiometricDevices(): Response<ApiResponse<List<BiometricDeviceDto>>>

    // Users, Roles & Security Audit Logs
    @GET("users")
    suspend fun getUsers(@Query("page") page: Int = 1, @Query("limit") limit: Int = 20): Response<ApiResponse<List<UserDto>>>

    @GET("roles")
    suspend fun getRoles(): Response<ApiResponse<List<RoleDto>>>

    @GET("audit-logs")
    suspend fun getAuditLogs(@Query("page") page: Int = 1, @Query("limit") limit: Int = 30): Response<ApiResponse<List<AuditLogDto>>>
}
