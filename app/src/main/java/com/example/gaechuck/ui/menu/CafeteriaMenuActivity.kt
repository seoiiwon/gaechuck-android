package com.example.gaechuck.ui.menu
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gaechuck.R
import com.example.gaechuck.api.ApiService
import com.example.gaechuck.data.response.BaseListResponse
import com.example.gaechuck.data.response.FoodMenuItem
import com.example.gaechuck.data.response.GetFoodDataResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CafeteriaMenuActivity : AppCompatActivity() {

    private val BASE_URL = "http://203.255.15.32:30001"
    private lateinit var apiService: ApiService
    private lateinit var campusSpinner: Spinner
    private lateinit var restaurantLayout: LinearLayout
    private lateinit var leftArrow: ImageView
    private lateinit var rightArrow: ImageView
    private lateinit var selectedCafeteriaSeq: List<Int>
    private var currentIndex = 0

    private val campusMap = mapOf(
        "가좌캠퍼스" to listOf("가좌 교직원식당", "가좌 중앙1식당", "가좌 교육문화1층식당"),
        "칠암캠퍼스" to listOf("칠암 교직원식당", "칠암 학생식당"),
        "통영캠퍼스" to listOf("통영 교직원식당", "통영 학생식당")
    )

    private val cafeteriaSeqMap = mapOf(
        "가좌캠퍼스" to listOf(1, 2, 3),
        "칠암캠퍼스" to listOf(4, 5),
        "통영캠퍼스" to listOf(6, 7)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cafeteria_menu)

        val backBtn: ImageView = findViewById(R.id.backBtn)
        backBtn.setOnClickListener { finish() }

        val homeBtn: ImageView = findViewById(R.id.homeBtn)
        homeBtn.setOnClickListener { /* 홈 이동 구현 */ }

        campusSpinner = findViewById(R.id.campusSpinner)
        restaurantLayout = findViewById(R.id.restaurantLayout)
        leftArrow = findViewById(R.id.leftArrow)
        rightArrow = findViewById(R.id.rightArrow)

        // Retrofit 초기화
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        val campusList = campusMap.keys.toList()
        val campusAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, campusList)
        campusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        campusSpinner.adapter = campusAdapter

        campusSpinner.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: android.view.View?, position: Int, id: Long) {
                val selectedCampus = campusList[position]
                val restaurantList = campusMap[selectedCampus] ?: emptyList()
                selectedCafeteriaSeq = cafeteriaSeqMap[selectedCampus] ?: emptyList()

                if (selectedCafeteriaSeq.isNotEmpty()) {
                    currentIndex = 0
                    updateRestaurantDisplay(restaurantList)
                    fetchMenuData()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        })

        leftArrow.setOnClickListener {
            if (currentIndex > 0) {
                currentIndex--
                updateRestaurantDisplay(campusMap[campusSpinner.selectedItem] ?: emptyList())
                fetchMenuData()
            }
        }

        rightArrow.setOnClickListener {
            if (currentIndex < selectedCafeteriaSeq.size - 1) {
                currentIndex++
                updateRestaurantDisplay(campusMap[campusSpinner.selectedItem] ?: emptyList())
                fetchMenuData()
            }
        }

        if (savedInstanceState == null) {
            val fragment = MenuItemFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.menuItemLayout, fragment)
                .commit()
        }
    }

    private fun updateRestaurantDisplay(restaurantList: List<String>) {
        restaurantLayout.removeAllViews()
        val textView = TextView(this)
        textView.text = restaurantList.getOrElse(currentIndex) { "식당 정보 없음" }
        textView.setPadding(16, 16, 16, 16)
        textView.textSize = 16f
        textView.gravity = android.view.Gravity.CENTER
        restaurantLayout.addView(textView)
    }

    private fun fetchMenuData() {
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val selectedSeq = selectedCafeteriaSeq.getOrElse(currentIndex) { 0 }

        val call: Call<BaseListResponse<GetFoodDataResponse>> = apiService.getFoodData(selectedSeq, "2024-12-15")

        call.enqueue(object : Callback<BaseListResponse<GetFoodDataResponse>> {
            override fun onResponse(
                call: Call<BaseListResponse<GetFoodDataResponse>>,
                response: Response<BaseListResponse<GetFoodDataResponse>>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val baseResponse = response.body()!!
                    if (baseResponse.isSuccess) {
                        val menuString = baseResponse.result.firstOrNull()?.menu ?: ""

                        // ✅ `menu`가 String이므로 가공하여 FoodMenuItem 리스트로 변환
                        val menuList = menuString.split(", ").map { menuItem ->
                            FoodMenuItem(
                                menu = menuItem,
                                menuDivision = "알 수 없음",  // 기본값 설정
                                date = todayDate,
                                menuSeq = 0
                            )
                        }

                        Log.d("API_SUCCESS", "받은 데이터: $menuList")
                        updateFragment(menuList)
                    } else {
                        Log.e("API_ERROR", "API 응답 실패: ${baseResponse.message}")
                    }
                } else {
                    Log.e("API_ERROR", "서버 응답 오류: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<BaseListResponse<GetFoodDataResponse>>, t: Throwable) {
                Log.e("API_ERROR", "네트워크 오류: ${t.message}")
            }
        })
    }






    private fun updateFragment(menuList: List<FoodMenuItem>) {
        val fragment = MenuItemFragment()
        val args = Bundle()
        args.putInt("cafeteriaSeq", selectedCafeteriaSeq[currentIndex])
        args.putSerializable("menuList", ArrayList(menuList)) // ✅ ArrayList로 변환하여 전달
        fragment.arguments = args

        supportFragmentManager.beginTransaction()
            .replace(R.id.menuItemLayout, fragment)
            .commit()
    }


}
