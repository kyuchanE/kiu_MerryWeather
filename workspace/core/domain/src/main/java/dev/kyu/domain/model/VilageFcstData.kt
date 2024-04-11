package dev.kyu.domain.model

data class VilageFcstData(
    val resultCode: String,
    val resultMsg: String,
    val vilageFcstItems: List<Item>?,
) {

    companion object {
        const val CATEGORY_TEMP_HOUR = "T1H"            // 1시간 기온
        const val CATEGORY_SKY = "SKY"                  // 하늘 상태
        const val CATEGORY_RAIN_HOUR = "RN1"            // 1시간 강수량
        const val CATEGORY_PRECIPITATION_TYPE = "PTY"   // 강수 형태
    }

    data class Item(
        val baseDate: String,
        val baseTime: String,
        val category: String,
        val fcstDate: String,
        val fcstTime: String,
        val fcstValue: String,
        val nx: Int,
        val ny: Int,
    )
}
