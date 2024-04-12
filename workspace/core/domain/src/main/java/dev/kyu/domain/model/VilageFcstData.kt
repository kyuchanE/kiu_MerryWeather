package dev.kyu.domain.model

data class VilageFcstData(
    val resultCode: String,
    val resultMsg: String,
    val vilageFcstItems: List<Item>?,
) {

    companion object {
        const val CATEGORY_ULTRA_TEMP_HOUR = "T1H"          // 1시간 기온
        const val CATEGORY_TEMP_HOUR = "TMP"                // 1시간 기온
        const val CATEGORY_SKY = "SKY"                      // 하늘 상태
        const val CATEGORY_ULTRA_RAIN_HOUR = "RN1"          // 1시간 강수량
        const val CATEGORY_RAIN_HOUR = "PCP"                // 1시간 강수량
        const val CATEGORY_PRECIPITATION_TYPE = "PTY"       // 강수 형태
        const val CATEGORY_REH = "REH"                      // 습도
        const val CATEGORY_POP = "POP"                      // 강수 확률
        const val CATEGORY_TMN = "TMN"                      // 일 최저 기온
        const val CATEGORY_TMX = "TMX"                      // 일 최고 기온
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
