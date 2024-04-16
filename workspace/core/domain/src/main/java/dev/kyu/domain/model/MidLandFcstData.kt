package dev.kyu.domain.model

data class MidLandFcstData(
    val resultCode: String,
    val resultMsg: String,
    val midLandFcstItems: Item?,
) {
    data class Item(
        val regId: String,
        val rnSt3Am: Int,       // 강수확률
        val rnSt3Pm: Int,
        val rnSt4Am: Int,
        val rnSt4Pm: Int,
        val rnSt5Am: Int,
        val rnSt5Pm: Int,
        val rnSt6Am: Int,
        val rnSt6Pm: Int,
        val rnSt7Am: Int,
        val rnSt7Pm: Int,
        val wf3Am: String,          // 날씨예보
        val wf3Pm: String,
        val wf4Am: String,
        val wf4Pm: String,
        val wf5Am: String,
        val wf5Pm: String,
        val wf6Am: String,
        val wf6Pm: String,
        val wf7Am: String,
        val wf7Pm: String,
    )
}
