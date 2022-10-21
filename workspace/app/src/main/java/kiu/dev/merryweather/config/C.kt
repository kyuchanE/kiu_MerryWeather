package kiu.dev.merryweather.config

object C {
    object WeatherApi {
        const val BASE_URL: String = "http://apis.data.go.kr/1360000/"
        const val API_KEY: String = "imXXF1JRLGEMorO+ytbZBX46425hIjMF7TZX39op4qXMjei5lTNeIhJJXbIPobLmpMwr98+hrMHkLrHL726Zng=="
        const val WEATHER_NOW: String = "VilageFcstInfoService_2.0/getVilageFcst"
        const val WEATHER_ULTRA_NOW: String = "VilageFcstInfoService_2.0/getUltraSrtFcst"
        const val WEATHER_WEEK: String = "MidFcstInfoService/getMidTa"
    }

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

    var tempWidgetId = 0
}

