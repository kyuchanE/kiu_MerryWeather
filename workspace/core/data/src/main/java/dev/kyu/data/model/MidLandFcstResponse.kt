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
                val item: List<Item>,
            ) {
                data class Item(
                    val regId: String,
                    val rnSt3Am: Int,
                    val rnSt3Pm: Int,
                    val rnSt4Am: Int,
                    val rnSt4Pm: Int,
                    val rnSt5Am: Int,
                    val rnSt5Pm: Int,
                    val rnSt6Am: Int,
                    val rnSt6Pm: Int,
                    val rnSt7Am: Int,
                    val rnSt7Pm: Int,
                    val wf3Am: String,
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
        }
    }

    fun toDomain(): MidLandFcstData {
        var doMainItem: MidLandFcstData.Item? = null
        if (this.response.body.items.item.isNotEmpty()) {
            doMainItem = MidLandFcstData.Item(
                this.response.body.items.item[0].regId,
                this.response.body.items.item[0].rnSt3Am,
                this.response.body.items.item[0].rnSt3Pm,
                this.response.body.items.item[0].rnSt4Am,
                this.response.body.items.item[0].rnSt4Pm,
                this.response.body.items.item[0].rnSt5Am,
                this.response.body.items.item[0].rnSt5Pm,
                this.response.body.items.item[0].rnSt6Am,
                this.response.body.items.item[0].rnSt6Pm,
                this.response.body.items.item[0].rnSt7Am,
                this.response.body.items.item[0].rnSt7Pm,
                this.response.body.items.item[0].wf3Am,
                this.response.body.items.item[0].wf3Pm,
                this.response.body.items.item[0].wf4Am,
                this.response.body.items.item[0].wf4Pm,
                this.response.body.items.item[0].wf5Am,
                this.response.body.items.item[0].wf5Pm,
                this.response.body.items.item[0].wf6Am,
                this.response.body.items.item[0].wf6Pm,
                this.response.body.items.item[0].wf7Am,
                this.response.body.items.item[0].wf7Pm,
            )
        }

        return MidLandFcstData(
            this.response.header.resultCode,
            this.response.header.resultMsg,
            doMainItem,
        )
    }

}
