package dev.kyu.data.model

import dev.kyu.domain.model.MidLandFcstData

data class MidLandFcstResponse(
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
                val item: List<MidLandFcstData.Item>,
            )
        }
    }

    fun toDomain(): MidLandFcstData {
        var doMainItem: MidLandFcstData.Item? = null
        if (this.response.body.items.item.isNotEmpty()) {
            doMainItem = this.response.body.items.item[0]
        }

        return MidLandFcstData(
            this.response.header.resultCode,
            this.response.header.resultMsg,
            doMainItem,
        )
    }

}
