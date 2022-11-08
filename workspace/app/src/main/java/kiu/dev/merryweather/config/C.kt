package kiu.dev.merryweather.config

object C {
    object WeatherApi {
        const val BASE_URL: String = "http://apis.data.go.kr/1360000/"
        const val API_KEY: String = "imXXF1JRLGEMorO+ytbZBX46425hIjMF7TZX39op4qXMjei5lTNeIhJJXbIPobLmpMwr98+hrMHkLrHL726Zng=="
        const val WEATHER_NOW: String = "VilageFcstInfoService_2.0/getVilageFcst"
        const val WEATHER_RIGHT_NOW: String = "VilageFcstInfoService_2.0/getUltraSrtFcst"
        const val WEATHER_MID_TA: String = "MidFcstInfoService/getMidTa"
        const val WEATHER_MID_FCST: String = "MidFcstInfoService/getMidLandFcst"
    }

    object WeatherData {
        // TODO chan 단기예보 시간
        val WEATHER_NOW_GET_DATA_TIME = listOf(
            "0210",
            "0510",
            "0810",
            "1110",
            "1410",
            "1710",
            "2010",
            "2310"
        )

        object Location {
            const val LOCATION_SEOUL = "seoul"

            val Seoul = mapOf(
                "nx" to "60",
                "ny" to "127"
            )

            fun getLocationMapData(location: String): Map<String, String> {
                return when(location) {
                    LOCATION_SEOUL -> Seoul
                    else -> Seoul
                }
            }
        }
    }


    object RoomTableName {
        const val WIDGET_ID = "widget_id"           // 위젯 아이디 테이블
        const val WEATHER_NOW = "weather_now"     // 날씨 데이터 테이블
        const val WEATHER_MID = "weather_mid"
    }

}

