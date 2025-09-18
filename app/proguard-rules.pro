############################################################
# ✅ Gson (리플렉션 기반이므로 클래스/필드 유지 필요)
############################################################
-keep class com.google.gson.** { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken
-dontwarn sun.misc.**

# Gson이 DTO 필드를 찾을 수 있도록 속성 유지
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses
-keepattributes Exceptions

############################################################
# ✅ Retrofit / OkHttp (네트워크 계층)
############################################################
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

# Retrofit 어노테이션 (GET, POST 등) 유지
-keep class retrofit2.** { *; }
-keep class retrofit2.converter.gson.** { *; }
-keepattributes RuntimeVisibleAnnotations

############################################################
# ✅ API 인터페이스
############################################################
-keep interface com.gaechuck_package.gaechuck.api.** { *; }
-keep class com.gaechuck_package.gaechuck.repository.** { *; }
-keep class com.gaechuck_package.gaechuck.ui.**ViewModel { *; }
-keepclassmembers class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}
-keep class * implements androidx.navigation.NavDirections { *; }
-keepclassmembers class * implements androidx.navigation.NavArgs {
    public static androidx.navigation.NavArgs fromBundle(android.os.Bundle);
}

############################################################
# ✅ 데이터 모델 (Request / Response DTO) - 수정된 부분
############################################################
-keep class com.gaechuck_package.gaechuck.data.request.** { *; }
-keep class com.gaechuck_package.gaechuck.data.response.** { *; }
-keep class com.gaechuck_package.gaechuck.data.model.** { *; }

# 제네릭 Response Wrapper - 더 구체적으로 보호
-keep class com.gaechuck_package.gaechuck.data.response.BaseResponse { *; }
-keep class com.gaechuck_package.gaechuck.data.response.BaseListResponse { *; }
-keep class com.gaechuck_package.gaechuck.data.response.PagenatedResponse { *; }

# Retrofit Response 래퍼 보호
-keep class retrofit2.Response { *; }
-keep class retrofit2.Response$* { *; }

# 모든 데이터 클래스의 필드와 메소드 보호
-keepclassmembers class com.gaechuck_package.gaechuck.data.** {
    <fields>;
    <methods>;
}

# 특히 중요한 응답 클래스들 개별 보호
-keep class com.gaechuck_package.gaechuck.data.response.LoginResponse { *; }
-keep class com.gaechuck_package.gaechuck.data.response.GetLoseDataResponse { *; }
-keep class com.gaechuck_package.gaechuck.data.response.GetLoseDetailResponse { *; }
-keep class com.gaechuck_package.gaechuck.data.response.PatchLoseResponse { *; }
-keep class com.gaechuck_package.gaechuck.data.response.GetRentDataResponse { *; }
-keep class com.gaechuck_package.gaechuck.data.response.GetRentDetailResponse { *; }
-keep class com.gaechuck_package.gaechuck.data.response.PostRentCreateResponse{ *; }
-keep class com.gaechuck_package.gaechuck.data.response.PostRentDeleteResponse{ *; }
-keep class com.gaechuck_package.gaechuck.data.response.GetBusinessDataResponse { *; }
-keep class com.gaechuck_package.gaechuck.data.response.GetBusinessDetailResponse { *; }
-keep class com.gaechuck_package.gaechuck.data.response.PatchBusinessResponse { *; }
-keep class com.gaechuck_package.gaechuck.data.response.GetAllNoticeDataResponse { *; }
-keep class com.gaechuck_package.gaechuck.data.response.GetCouncilNoticeDataResponse { *; }
-keep class com.gaechuck_package.gaechuck.data.response.GetCouncilNoticeDetailResponse { *; }
-keep class com.gaechuck_package.gaechuck.data.response.DeleteCouncilNoticeResponse { *; }
-keep class com.gaechuck_package.gaechuck.data.response.GetAllNoticeDataResponse { *; }
-keep class com.gaechuck_package.gaechuck.data.response.GetFoodDataResponse { *; }
-keep class com.gaechuck_package.gaechuck.data.response.PostUrlResponse { *; }

############################################################
# ✅ 제네릭 타입 처리를 위한 추가 규칙
############################################################
# TypeToken 관련 클래스 보호
-keep class * implements java.lang.reflect.Type { *; }
-keep class * implements java.lang.reflect.ParameterizedType { *; }
-keep class * implements java.lang.reflect.GenericArrayType { *; }
-keep class * implements java.lang.reflect.TypeVariable { *; }
-keep class * implements java.lang.reflect.WildcardType { *; }

# 제네릭 시그니처 보호
-keepattributes Signature,RuntimeVisibleAnnotations,AnnotationDefault

############################################################
# ✅ Kotlin (코루틴, data class 등) 관련 안정성
############################################################
-keepclassmembers class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**
-dontwarn kotlin.**
-dontwarn kotlin.jvm.internal.**

# Kotlin data class 보호
-keep @kotlin.Metadata class *
-keepclassmembers class **$WhenMappings {
    <fields>;
}
-keepclassmembers class kotlin.Metadata {
    public <methods>;
}

############################################################
# ✅ 추가 디버깅용 (필요시 주석 해제)
############################################################
# -printmapping mapping.txt
# -printseeds seeds.txt
# -printusage usage.txt