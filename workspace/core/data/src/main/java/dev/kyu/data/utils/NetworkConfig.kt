package dev.kyu.data.utils

object NetworkConfig {

    object Weather {
        const val weatherBaseUrl = "http://apis.data.go.kr"

        object Mid {
            const val midBaseUrl = "1360000/MidFcstInfoService/"
            const val midLandFcst = "getMidLandFcst"
        }
    }

}