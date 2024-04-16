package dev.kyu.domain.model

data class MidTaData(
    val resultCode: String,
    val resultMsg: String,
    val midTaItems: Item?,
) {
    data class Item(
        val regId: String,
        val taMin3: Int,
        val taMax3: Int,
        val taMin4: Int,
        val taMax4: Int,
        val taMin5: Int,
        val taMax5: Int,
        val taMin6: Int,
        val taMax6: Int,
        val taMin7: Int,
        val taMax7: Int,
    )
}
