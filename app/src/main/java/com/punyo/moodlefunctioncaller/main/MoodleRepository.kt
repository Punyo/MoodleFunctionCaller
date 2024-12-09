package com.punyo.moodlefunctioncaller.main

class MoodleRepository(private val moodleRemoteDataSource: MoodleRemoteDataSource) {
    suspend fun getUserInfo(): UserInfo {
        return moodleRemoteDataSource.getUserInfo()
    }

    suspend fun getUserCourses(): List<Course> {
        return moodleRemoteDataSource.getUserCourses()
    }

    suspend fun getSubmissionStatus(assignmentId: Int): SubmissionStatus {
        return moodleRemoteDataSource.getSubmissionStatus(assignmentId)
    }
}