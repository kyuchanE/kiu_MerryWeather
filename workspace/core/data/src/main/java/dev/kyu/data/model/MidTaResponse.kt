package dev.kyu.data.model

import dev.kyu.domain.model.MidTaData

data class MidTaResponse(
    val response: Response
) {
    data class Response(
        val header: Header,
        val body: Body,
    ) {
        data class Header(
            val resultCode: String,
            val resultMsg: String
        )

        data class Body(
            val dataType: String,
            val items: Items,
            val pageNo: Int,
            val numOfRous: Int,
            val totalCount: Int,
        ) {
            data class Items(
                val item: List<MidTaData.Item>
            )
        }
    }

    fun toDomain(): MidTaData {
        var doMainItem: MidTaData.Item? = null
        if (this.response.body.items.item.isNotEmpty()) {
            doMainItem = this.response.body.items.item[0]
        }

        return MidTaData(
            this.response.header.resultCode,
            this.response.header.resultMsg,
            doMainItem
        )
    }
}
