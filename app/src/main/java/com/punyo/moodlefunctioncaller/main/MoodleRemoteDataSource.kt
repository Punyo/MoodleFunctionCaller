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
        return response.body()
    }

    private fun defaultParameters() = parameters {
        append("moodlewssettingfilter", "true")
        append("moodlewssettingfileurl", "true")
        append("moodlewssettinglang", "ja")
        append("wstoken", token)
    }
}

@Serializable
data class UserInfo(
    val fullname: String,
    val userid: Int
)

@Serializable
data class Course(
    val displayname: String,
    val id: Int
)
