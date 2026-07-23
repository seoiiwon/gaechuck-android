package com.gaechuck_package.gaechuck.ui.onboarding

data class OnboardingItem(
    val title: String,
    val subtitle: String,
    val isLast: Boolean = false
) {
    companion object {
        val ITEMS = listOf(
            OnboardingItem(
                title = "모든 캠퍼스 정보,\n한 곳에서 확인하세요",
                subtitle = "학식, 캠퍼스맵, 셔틀, 공지까지\n경상국립대 생활의 모든 것을 개척에서"
            ),
            OnboardingItem(
                title = "셔틀버스 시간표,\n지금 바로 확인",
                subtitle = "가좌-칠암 셔틀 실시간 위치와\n막차 시간을 바로 확인하세요"
            ),
            OnboardingItem(
                title = "대학 근처 제휴사업,\n어떤 게 있을까?",
                subtitle = "경상국립대 총학생회와\n제휴된 가게들을 찾아보세요"
            ),
            OnboardingItem(
                title = "개척과 함께\n캠퍼스 생활을 시작하세요",
                subtitle = "비회원가입으로 시작해도\n핵심 기능을 이용할 수 있어요",
                isLast = true
            )
        )
    }
}
