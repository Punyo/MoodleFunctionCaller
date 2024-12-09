package com.punyo.moodlefunctioncaller.main

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.HttpResponse
import io.ktor.http.parameters
import io.ktor.http.plus
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.time.ZonedDateTime

class MoodleRemoteDataSource(private val token: String) {
    private var userInfo: UserInfo? = null
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(
                Json {
                    ignoreUnknownKeys = true
                }
            )
        }
    }
    private val moodleWebServiceURLWithQueries =
        "https://cms7.ict.nitech.ac.jp/moodle40a/webservice/rest/server.php?moodlewsrestformat=json&wsfunction="

    suspend fun getUserInfo(returnAcquiredUserInfoIfAvailable: Boolean = true): UserInfo {
        if (userInfo == null || !returnAcquiredUserInfoIfAvailable) {
            val response: HttpResponse = client.submitForm(
                url = moodleWebServiceURLWithQueries + "core_webservice_get_site_info",
                formParameters = defaultParameters()
            )
            userInfo = response.body<UserInfo>()
        }
        return userInfo!!
    }

    suspend fun getUserCourses(
        excludeHiddenCourses: Boolean = true,
        excludeCourseNotModifiedAfter: ZonedDateTime? = null
    ): List<Course> {
        val userInfo = getUserInfo()
        val response: HttpResponse = client.submitForm(
            url = moodleWebServiceURLWithQueries + "core_enrol_get_users_courses",
            formParameters = defaultParameters().plus(
                parameters {
                    append("userid", userInfo.userid.toString())
                    append("returnusercount", "0")
                }
            )
        )
        val courses = response.body<MutableList<Course>>()
        if (excludeHiddenCourses) {
            courses.removeIf(Course::hidden)
        }
        if (excludeCourseNotModifiedAfter != null) {
            courses.removeIf { course ->
                course.timemodified < excludeCourseNotModifiedAfter.toEpochSecond()
            }
        }
        val response1: HttpResponse = client.submitForm(
            url = moodleWebServiceURLWithQueries + "mod_assign_get_assignments",
            formParameters = defaultParameters().plus(
                parameters {
                    for (i in 1..courses.size) {
                        append("courseids[$i]", courses[i - 1].id.toString())
                    }
                }
            )
        )
        return response1.body<GetAssignmentsResponse>().courses
    }

    suspend fun getSubmissionStatus(assignmentId: Int): SubmissionStatus {
        val userInfo = getUserInfo()
        val response: HttpResponse = client.submitForm(
            url = moodleWebServiceURLWithQueries + "mod_assign_get_submission_status",
            formParameters = defaultParameters().plus(
                parameters {
                    append("userid", userInfo.userid.toString())
                    append("assignid", assignmentId.toString())
                }
            )
        )
        return response.body<GetSubmissionStatusResponse>().lastattempt
    }

    private fun defaultParameters() = parameters {
        append("moodlewssettingfilter", "true")
        append("moodlewssettingfileurl", "true")
        append("moodlewssettinglang", "ja")
        append("wstoken", token)
    }
}

@Serializable
private data class GetAssignmentsResponse(
    val courses: List<Course>
)

@Serializable
private data class GetSubmissionStatusResponse(
    val lastattempt: SubmissionStatus
)

@Serializable
data class SubmissionStatus(
    val cansubmit: Boolean,
    val blindmarking: Boolean,
    val submission: Submission
)

@Serializable
data class Submission(
    val status: String,
    val timemodified: Long
)

@Serializable
data class UserInfo(
    val fullname: String,
    val userid: Int
)

@Serializable
data class Course(
    val fullname: String,
    val id: Int,
    val hidden: Boolean = false,
    val timemodified: Long,
    val assignments: List<Assignment> = emptyList()
)

@Serializable
data class Assignment(
    val id: Int,
    val course: Int,
    val name: String,
    val duedate: Long,
)

