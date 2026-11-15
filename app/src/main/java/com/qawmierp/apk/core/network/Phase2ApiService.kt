package com.qawmierp.apk.core.network

import com.qawmierp.apk.core.network.dto.*
import retrofit2.Response
import retrofit2.http.*

interface Phase2ApiService {

    // Exams, Admissions, Leaves & Certificates
    @GET("exams")
    suspend fun getExams(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<ExamDto>>>

    @POST("exams")
    suspend fun createExam(@Body request: CreateExamRequest): Response<ApiResponse<ExamDto>>

    @GET("exam-results")
    suspend fun getExamResults(
        @Query("exam_id") examId: String? = null,
        @Query("student_id") studentId: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): Response<ApiResponse<List<ExamResultDto>>>

    @GET("exam-routines")
    suspend fun getExamRoutines(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 30
    ): Response<ApiResponse<List<ExamRoutineDto>>>

    @GET("questions")
    suspend fun getQuestions(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<QuestionDto>>>

    @GET("admissions")
    suspend fun getAdmissions(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<AdmissionDto>>>

    @GET("alumni")
    suspend fun getAlumni(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<AlumniDto>>>

    @GET("leaves")
    suspend fun getLeaves(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<LeaveDto>>>

    @POST("leaves")
    suspend fun createLeave(@Body request: CreateLeaveRequest): Response<ApiResponse<LeaveDto>>

    @GET("holidays")
    suspend fun getHolidays(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 30
    ): Response<ApiResponse<List<HolidayDto>>>

    // Finance, Expenses, Zakat Funds, Donors & Fundraising
    @GET("expenses")
    suspend fun getExpenses(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<ExpenseDto>>>

    @POST("expenses")
    suspend fun createExpense(@Body request: CreateExpenseRequest): Response<ApiResponse<ExpenseDto>>

    @GET("funds")
    suspend fun getFunds(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<FundDto>>>

    @GET("donors")
    suspend fun getDonors(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<DonorDto>>>

    @GET("donations")
    suspend fun getDonations(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<DonationDto>>>

    @POST("donations")
    suspend fun createDonation(@Body request: CreateDonationRequest): Response<ApiResponse<DonationDto>>

    @GET("fundraising")
    suspend fun getFundraising(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<ApiResponse<List<FundraisingDto>>>
}
