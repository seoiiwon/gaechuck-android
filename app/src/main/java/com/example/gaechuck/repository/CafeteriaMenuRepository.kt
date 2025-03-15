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
    private val apiService = ApiConnection.getRetrofitService

    fun getFoodMenu(
        cafeteriaSeq: Int,
        onSuccess: (List<FoodMenuItem>) -> Unit,
        onError: (String) -> Unit
    ) {
        val startDateList = getWeekDates()
        val allMenus = mutableListOf<FoodMenuItem>()

        startDateList.forEach { startDate ->
            apiService.getFoodData(cafeteriaSeq, startDate)
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

    fun getFoodMenuByDate(
        cafeteriaSeq: Int,
        selectedDate: String,
        onSuccess: (List<FoodMenuItem>) -> Unit,
        onError: (String) -> Unit
    ) {
        apiService.getFoodData(cafeteriaSeq, selectedDate)
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

                            Log.d("API_SUCCESS", "날짜: $selectedDate, 데이터: $menuList")
                            onSuccess(menuList)
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

    private fun getWeekDates(): List<String> {
        val calendar = Calendar.getInstance(Locale.getDefault())
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)

        return (0..6).map {
            val date = dateFormat.format(calendar.time)
            calendar.add(Calendar.DAY_OF_WEEK, 1)
            date
        }
    }
}