package com.punyo.moodlefunctioncaller.main

import java.time.ZonedDateTime

class MoodleRepository(private val moodleRemoteDataSource: MoodleRemoteDataSource) {
    /**
     * ログイン済みのユーザーの情報（UserInfo）を取得します
     *
     * @param returnAcquiredUserInfoIfAvailable 既に取得済みのユーザー情報がある場合、それを返すかどうか
     * @return UserInfo
     */
    suspend fun getUserInfo(returnAcquiredUserInfoIfAvailable: Boolean = true): UserInfo {
        return moodleRemoteDataSource.getUserInfo(returnAcquiredUserInfoIfAvailable)
    }

    /**
     * ログイン済みのユーザーが登録しているコース（Course）のリストを取得します
     *
     * @param excludeHiddenCourses 非表示のコースを返り値から除外するかどうか
     * @param excludeCourseNotModifiedAfter 指定した日時より後の更新がないコースを返り値から除外するかどうか（除外しない場合nullを渡してください）
     * @return CourseのList
     */
    suspend fun getUserCourses(
        excludeHiddenCourses: Boolean = true,
        excludeCourseNotModifiedAfter: ZonedDateTime? = null
    ): List<Course> {
        return moodleRemoteDataSource.getUserCourses(
            excludeHiddenCourses,
            excludeCourseNotModifiedAfter
        )
    }

    /**
     * IDで指定された課題に紐づいている提出の情報（SubmissionStatus）を取得します
     *
     * @param assignmentId 課題のID（Assignment.id）
     * @return IDで指定された課題のSubmissionStatus
     */
    suspend fun getSubmissionStatus(assignmentId: Int): SubmissionInfo {
        return moodleRemoteDataSource.getSubmissionStatus(assignmentId)
    }
}