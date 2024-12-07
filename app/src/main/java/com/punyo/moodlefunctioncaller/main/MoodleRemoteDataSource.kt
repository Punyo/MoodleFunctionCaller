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

    suspend fun getUserInfo(): UserInfo {
        val response: HttpResponse = client.submitForm(
            url = moodleWebServiceURLWithQueries + "core_webservice_get_site_info",
            formParameters = defaultParameters()
        )
        userInfo = response.body<UserInfo>()
        return userInfo!!
    }

    suspend fun getUserCourses(): List<Course> {
        if (userInfo == null) {
            getUserInfo()
        }
        val response: HttpResponse = client.submitForm(
            url = moodleWebServiceURLWithQueries + "core_enrol_get_users_courses",
            formParameters = defaultParameters().plus(
                parameters {
                    append("userid", userInfo!!.userid.toString())
                    append("returnusercount", "0")
                }
            )
        )
        val courses = response.body<List<Course>>()

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
        return response1.body<MoodleResponse>().courses
    }

//    suspend fun getAssignmentsById(courseId: Int): List<Assignment> {
//        val response: HttpResponse = client.submitForm(
//            url = moodleWebServiceURLWithQueries + "mod_assign_get_submission_status",
//            formParameters = defaultParameters().plus(
//                parameters {
//                    append("courseids[0]", courseId.toString())
//                }
//            )
//        )
//        return response.body<MoodleResponse>().courses[0].assignments
//    }

    private fun defaultParameters() = parameters {
        append("moodlewssettingfilter", "true")
        append("moodlewssettingfileurl", "true")
        append("moodlewssettinglang", "ja")
        append("wstoken", token)
    }
}

@Serializable
private data class MoodleResponse(
    val courses: List<Course>
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
    val assignments: List<Assignment> = emptyList()
)

@Serializable
data class Assignment(
    val id: Int,
    val course: Int,
    val name: String,
    val duedate: Long,
)

@Serializable
data class Submission(
    val status: AssignmentStatus,
    val timemodified: Long
)

enum class AssignmentStatus {
    NOT_SUBMITTED,
    SUBMITTED,
    GRADED
}
