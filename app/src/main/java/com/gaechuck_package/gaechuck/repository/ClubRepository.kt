package com.gaechuck_package.gaechuck.repository

import com.gaechuck_package.gaechuck.data.model.Club
import com.gaechuck_package.gaechuck.data.model.ClubDetail
import com.gaechuck_package.gaechuck.data.model.ClubScheduleItem
import com.gaechuck_package.gaechuck.data.model.ClubStatus

/**
 * UI-only placeholder data source. Method signatures mirror what a real
 * Retrofit-backed repository would look like so this can be swapped for
 * ApiConnection calls later without touching call sites.
 */
class ClubRepository {

    private val clubs = mutableListOf(
        Club(1, "FC 개척", null, "중앙동아리", "스포츠", "축구를 사랑하는 사람들의 모임", ClubStatus.RECRUITING, beginnerFriendly = true),
        Club(2, "코딩 개척자들", null, "단과대 동아리", "학술/스터디", "함께 성장하는 개발자 동아리", ClubStatus.ALWAYS, beginnerFriendly = true),
        Club(3, "밴드부 '울림'", null, "중앙 동아리", "문화/예술", "음악으로 하나되는 우리", ClubStatus.CLOSED, beginnerFriendly = true),
        Club(4, "농구 개척단", null, "중앙동아리", "스포츠", "매주 모여 농구를 즐기는 사람들", ClubStatus.URGENT),
        Club(5, "봉사하는 사람들", null, "중앙동아리", "봉사", "지역사회와 함께하는 봉사동아리", ClubStatus.RECRUITING, beginnerFriendly = true),
        Club(6, "독서 토론회", null, "단과대 동아리", "학술/스터디", "책을 읽고 생각을 나누는 모임", ClubStatus.ALWAYS),
        Club(7, "사진 동아리 찰칵", null, "단과대동아리", "문화/예술", "순간을 기록하는 사람들", ClubStatus.RECRUITING, beginnerFriendly = true),
        Club(8, "창업 연구회", null, "중앙동아리", "취업/창업", "함께 아이디어를 실현하는 모임", ClubStatus.URGENT),
        Club(9, "기독 동아리 은혜", null, "중앙동아리", "종교", "함께 신앙을 나누는 사람들", ClubStatus.ALWAYS),
        Club(10, "친목 도모회", null, "중앙동아리", "친목/취미", "다양한 사람들과 친해지는 모임", ClubStatus.CLOSED),
        Club(11, "댄스 동아리 무브", null, "중앙동아리", "문화/예술", "춤을 사랑하는 사람들의 모임", ClubStatus.RECRUITING, beginnerFriendly = true),
        Club(12, "등산 동아리 산행", null, "중앙동아리", "친목/취미", "매주 산을 오르는 사람들", ClubStatus.ALWAYS),
    )

    private val details: Map<Int, ClubDetail> = clubs.associate { club ->
        val detail = if (club.id == 1) {
            ClubDetail(
                id = club.id,
                name = club.name,
                coverImageUrl = club.imageUrl,
                orgType = club.orgType,
                category = club.category,
                summary = club.summary,
                status = club.status,
                beginnerFriendly = club.beginnerFriendly,
                introduction = "FC 개척은 축구를 사랑하는 학우들이 모여 매주 정기적으로 운동하며 친목을 다지는 중앙동아리입니다.\n\n실력과 상관없이 축구에 대한 열정만 있다면 누구나 환영합니다!",
                meetingInfo = "정기모임 : 매주 수요일 18:00 대운동장",
                fee = "회비: 학기당 30,000원",
                preparation = "준비물: 개인 축구화 및 운동복",
                contactLabel = "인스타그램",
                contactUrl = "https://instagram.com/fc_gaecheok",
                applyUrl = "https://form.google.com/fc-gaecheok-apply",
                recruitSchedule = listOf(
                    ClubScheduleItem("서류 접수중", "3.2 - 3.10", isCurrent = true),
                    ClubScheduleItem("면접 일정", "3.12 - 3.13"),
                    ClubScheduleItem("최종 발표", "3.15"),
                    ClubScheduleItem("신입생 환영회", "3.18")
                ),
                galleryImages = listOf("", "", "", "")
            )
        } else {
            ClubDetail(
                id = club.id,
                name = club.name,
                coverImageUrl = club.imageUrl,
                orgType = club.orgType,
                category = club.category,
                summary = club.summary,
                status = club.status,
                beginnerFriendly = club.beginnerFriendly,
                introduction = "${club.name}은(는) ${club.summary} 매주 정기적으로 모여 활동하며 친목을 다지는 ${club.orgType}입니다. 관심 있는 학우라면 누구나 환영합니다.",
                recruitSchedule = emptyList(),
                galleryImages = emptyList()
            )
        }
        club.id to detail
    }

    suspend fun getClubs(): List<Club> = clubs

    suspend fun getClubDetail(clubId: Int): ClubDetail? {
        val club = clubs.find { it.id == clubId } ?: return null
        return details[clubId]?.copy(isFavorite = club.isFavorite)
    }

    fun toggleFavorite(clubId: Int): Boolean {
        val club = clubs.find { it.id == clubId } ?: return false
        club.isFavorite = !club.isFavorite
        return club.isFavorite
    }

    fun getFavorites(): List<Club> = clubs.filter { it.isFavorite }

    companion object {
        @Volatile
        private var instance: ClubRepository? = null

        fun getInstance(): ClubRepository {
            return instance ?: synchronized(this) {
                instance ?: ClubRepository().also { instance = it }
            }
        }
    }
}
