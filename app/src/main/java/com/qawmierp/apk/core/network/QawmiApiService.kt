package com.qawmierp.apk.core.network

import com.qawmierp.apk.core.network.dto.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface QawmiApiService {

    // 1. Auth
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthDataDto>>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthDataDto>>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshRequest): Response<ApiResponse<RefreshDataDto>>

    @GET("auth/me")
    suspend fun getMe(): Response<ApiResponse<MeDataDto>>

    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<LogoutDataDto>>

    // 2. Dashboard & Search
    @GET("dashboard")
    suspend fun getDashboard(): Response<ApiResponse<DashboardDto>>

    @GET("search")
    suspend fun search(@Query("q") query: String): Response<ApiResponse<UniversalSearchDto>>

    // 3. Role Portals
    @GET("portal/student")
    suspend fun getStudentPortal(
        @Query("student_id") studentId: String? = null
    ): Response<ApiResponse<StudentPortalDto>>

    @GET("portal/teacher")
    suspend fun getTeacherPortal(): Response<ApiResponse<TeacherPortalDto>>

    // 4. Students, Teachers, Classes
    @GET("students")
    suspend fun getStudents(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("search") search: String? = null,
        @Query("class_id") classId: String? = null,
        @Query("status") status: String? = null
    ): Response<ApiResponse<List<StudentDto>>>

    @GET("students/{id}")
    suspend fun getStudent(@Path("id") id: String): Response<ApiResponse<StudentDto>>

    @GET("teachers")
    suspend fun getTeachers(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("search") search: String? = null
    ): Response<ApiResponse<List<TeacherDto>>>

    @GET("classes")
    suspend fun getClasses(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): Response<ApiResponse<List<ClassDto>>>

    // 5. Attendance, Hifz, Kitab, Noorani
    @GET("attendance")
    suspend fun getAttendance(
        @Query("student_id") studentId: String? = null,
        @Query("date") date: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): Response<ApiResponse<List<AttendanceDto>>>

    @POST("attendance/bulk")
    suspend fun submitBulkAttendance(
        @Body request: BulkAttendanceRequest
    ): Response<ApiResponse<List<AttendanceDto>>>

    @GET("hifz")
    suspend fun getHifzLogs(
        @Query("student_id") studentId: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<HifzLogDto>>>

    @POST("hifz")
    suspend fun createHifzLog(@Body request: CreateHifzLogRequest): Response<ApiResponse<HifzLogDto>>

    @GET("kitab")
    suspend fun getKitabLogs(
        @Query("student_id") studentId: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<KitabLogDto>>>

    @POST("kitab")
    suspend fun createKitabLog(@Body request: CreateKitabLogRequest): Response<ApiResponse<KitabLogDto>>

    @GET("noorani")
    suspend fun getNooraniProgress(
        @Query("student_id") studentId: String
    ): Response<ApiResponse<List<NooraniProgressDto>>>

    // 6. Fees, Notifications, Settings & Upload
    @GET("student-fees")
    suspend fun getStudentFees(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("student_id") studentId: String? = null
    ): Response<ApiResponse<List<StudentFeeDto>>>

    @GET("fee-payments")
    suspend fun getFeePayments(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<FeePaymentDto>>>

    @GET("notifications")
    suspend fun getNotifications(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<NotificationDto>>>

    @PATCH("notifications")
    suspend fun markNotifications(
        @Body request: MarkNotificationRequest
    ): Response<ApiResponse<Unit>>

    @GET("settings")
    suspend fun getSettings(): Response<ApiResponse<SettingsDataDto>>

    @Multipart
    @POST("upload")
    suspend fun uploadMedia(
        @Part file: MultipartBody.Part
    ): Response<ApiResponse<UploadResponseDto>>
}
