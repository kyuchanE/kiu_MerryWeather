package dev.kyu.data.utils

object NetworkConfig {

    object Weather {
        const val weatherBaseUrl = "http://apis.data.go.kr"

        object Mid {
            const val midBaseUrl = "1360000/MidFcstInfoService/"
            const val midLandFcst = "getMidLandFcst"
            const val midTa = "getMidTa"
        }

        object Vilage {
            const val vilageBaseUrl = "1360000/VilageFcstInfoService_2.0/"
            const val vilageFcst = "getVilageFcst"
            const val ultraSrtFcst = "getUltraSrtFcst"
        }

    }

}