package dev.kyu.domain.model

data class VilageFcstData(
    val resultCode: String,
    val resultMsg: String,
    val vilageFcstItems: List<Item>?,
) {
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
