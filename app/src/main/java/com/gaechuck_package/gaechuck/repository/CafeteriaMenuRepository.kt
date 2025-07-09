package com.gaechuck_package.gaechuck.repository

import com.gaechuck_package.gaechuck.api.ApiConnection
import com.gaechuck_package.gaechuck.api.ApiService
import com.gaechuck_package.gaechuck.data.response.FoodMenuItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


class CafeteriaMenuRepository(
    private val apiService: ApiService = ApiConnection.getRetrofitService
) {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

//    매주 월요일 기준 일주일 데이터 가져오기
    suspend fun fetchWeeklyMenu(seq: Int): List<FoodMenuItem> = withContext(Dispatchers.IO) {
    val cal = Calendar.getInstance()
    val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
    val offsetToMonday = if (dayOfWeek == Calendar.SUNDAY) {
        -6
    } else {
        Calendar.MONDAY - dayOfWeek
    }
    cal.add(Calendar.DAY_OF_MONTH, offsetToMonday)
    val mondayDate = dateFormat.format(cal.time)

    // 2) API를 한 번만 호출 (startDate = mondayDate)
    val resp = apiService.getFoodData(seq, mondayDate)
    if (!resp.isSuccess) throw IOException("API error: ${resp.message}")

    // 3) 돌려받은 result 리스트 안에서, date별로 분리된 항목들을 모두 FoodMenuItem으로 변환
    resp.result
        .flatMap { dto ->
            // dto.menu에 여러 개 식단이 공백 단위로 붙어 있다면 split("\\s+") 사용
            // (필요에 따라 split(", ") 등으로 조정)
            dto.menu
                .split("\\s+".toRegex())
                .map { dish ->
                    FoodMenuItem(
                        menu = dish,
                        menuDivision = dto.menuDivision,
                        date = dto.date,
                        menuSeq = dto.menuSeq
                    )
                }
        }
    }
}