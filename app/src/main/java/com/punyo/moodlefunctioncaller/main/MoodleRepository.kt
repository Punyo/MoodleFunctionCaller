package com.punyo.moodlefunctioncaller.main

import java.time.ZonedDateTime

class MoodleRepository(private val moodleRemoteDataSource: MoodleRemoteDataSource) {
    suspend fun getUserInfo(returnAcquiredUserInfoIfAvailable: Boolean = true): UserInfo {
        return moodleRemoteDataSource.getUserInfo(returnAcquiredUserInfoIfAvailable)
    }

    suspend fun getUserCourses(
        excludeHiddenCourses: Boolean = true,
        excludeCourseNotModifiedAfter: ZonedDateTime? = null
    ): List<Course> {
        return moodleRemoteDataSource.getUserCourses(
            excludeHiddenCourses,
            excludeCourseNotModifiedAfter
        )
    }

    suspend fun getSubmissionStatus(assignmentId: Int): SubmissionStatus {
        return moodleRemoteDataSource.getSubmissionStatus(assignmentId)
    }
}