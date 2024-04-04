package dev.kyu.data.model

import dev.kyu.domain.model.VilageFcstData

data class VilageFcstResponse(
    val response: Response
) {

    data class Response(
        val header: Header,
        val body: Body,
    ) {
        data class Header(
            val resultCode: String,
            val resultMsg: String,
        )

        data class Body(
            val dataType: String,
            val items: Items,
            val pageNo: Int,
            val numOfRows: Int,
            val totalCount: Int,
        ) {
            data class Items(
                val item: List<VilageFcstData.Item>,
            )
        }
    }

    fun toDomain(): VilageFcstData {
        var doMainItems: List<VilageFcstData.Item>? = null
        if (this.response.body.items.item.isNotEmpty()) {
            doMainItems = this.response.body.items.item
        }

        return VilageFcstData(
            this.response.header.resultCode,
            this.response.header.resultMsg,
            doMainItems
        )
    }

}
