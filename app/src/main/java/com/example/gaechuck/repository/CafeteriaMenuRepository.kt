package com.example.gaechuck.repository

import android.util.Log
import com.example.gaechuck.api.ApiConnection
import com.example.gaechuck.data.response.BaseListResponse
import com.example.gaechuck.data.response.FoodMenuItem
import com.example.gaechuck.data.response.GetFoodDataResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CafeteriaMenuRepository {
    fun getFoodMenu(
        cafeteriaSeq: Int,
        onSuccess: (List<FoodMenuItem>) -> Unit,
        onError: (String) -> Unit
    ) {
        val calendar = Calendar.getInstance(Locale.getDefault())
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        val startDateList = (0..6).map {
            val date = dateFormat.format(calendar.time)
            calendar.add(Calendar.DAY_OF_WEEK, 1)
            date
        }

        val allMenus = mutableListOf<FoodMenuItem>()

        startDateList.forEach { startDate ->
            ApiConnection.getRetrofitService.getFoodData(cafeteriaSeq, startDate)
                .enqueue(object : Callback<BaseListResponse<GetFoodDataResponse>> {
                    override fun onResponse(
                        call: Call<BaseListResponse<GetFoodDataResponse>>,
                        response: Response<BaseListResponse<GetFoodDataResponse>>
                    ) {
                        if (response.isSuccessful && response.body() != null) {
                            val baseResponse = response.body()!!
                            if (baseResponse.isSuccess) {
                                val menuList = baseResponse.result.flatMap { responseItem ->
                                    responseItem.menu.split(", ").map { menuItem ->
                                        FoodMenuItem(
                                            menu = menuItem,
                                            menuDivision = responseItem.menuDivision,
                                            date = responseItem.date,
                                            menuSeq = responseItem.menuSeq
                                        )
                                    }
                                }
                                allMenus.addAll(menuList)
                                Log.d("API_SUCCESS", "날짜: $startDate, 데이터: $menuList")

                                if (allMenus.size >= startDateList.size) {
                                    onSuccess(allMenus)
                                }
                            } else {
                                onError("API 응답 실패: ${baseResponse.message}")
                            }
                        } else {
                            onError("서버 응답 오류: ${response.errorBody()?.string()}")
                        }
                    }

                    override fun onFailure(call: Call<BaseListResponse<GetFoodDataResponse>>, t: Throwable) {
                        onError("네트워크 오류: ${t.message}")
                    }
                })
        }
    }
}