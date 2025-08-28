package com.example.newsinmycity

object CityConfig {

    fun getAvailableCities(): List<City> = listOf(
        City(
            id = 1,
            name = "Нижний Новгород",
            sources = getNizhnyNovgorodSources()
        ),
        City(
            id = 2,
            name = "Москва",
            sources = getMoscowSources()
        ),
        City(
            id = 3,
            name = "Санкт-Петербург",
            sources = getStPetersburgSources()
        )
    )

    fun getCityById(cityId: Int): City? {
        return getAvailableCities().find { it.id == cityId }
    }

    private fun getNizhnyNovgorodSources(): List<NewsSource> = listOf(
        NewsSource("NN.RU", SourceType.WEBSITE, "https://www.nn.ru/"),
        NewsSource("РБК НН", SourceType.WEBSITE, "https://nn.rbc.ru/"),
        NewsSource("МК НН", SourceType.WEBSITE, "https://nn.mk.ru/"),
        NewsSource("NewsNN", SourceType.WEBSITE, "https://newsnn.ru/"),
        NewsSource("В городе N", SourceType.WEBSITE, "https://www.vgoroden.ru/"),
        NewsSource("Мой НН", SourceType.TELEGRAM, "@mynnovgorod"),
        NewsSource("NN.RU TG", SourceType.TELEGRAM, "@nn_ru"),
        NewsSource("ЧП НН", SourceType.TELEGRAM, "@nn_obl"),
        NewsSource("Нижний №1", SourceType.TELEGRAM, "@nizhny01")
    )

    private fun getMoscowSources(): List<NewsSource> = listOf(
        NewsSource("РБК Москва", SourceType.WEBSITE, "https://moscow.rbc.ru/"),
        NewsSource("МК Москва", SourceType.WEBSITE, "https://moscow.mk.ru/"),
        NewsSource("Вечерняя Москва", SourceType.WEBSITE, "https://vm.ru/"),
        NewsSource("М24", SourceType.WEBSITE, "https://m24.ru/"),
        NewsSource("Москва 24", SourceType.TELEGRAM, "@moscow24"),
        NewsSource("Москва FM", SourceType.TELEGRAM, "@moskva_fm"),
        NewsSource("Столица", SourceType.TELEGRAM, "@stolica_S")
    )

    private fun getStPetersburgSources(): List<NewsSource> = listOf(
        NewsSource("РБК СПб", SourceType.WEBSITE, "https://spb.rbc.ru/"),
        NewsSource("МК СПб", SourceType.WEBSITE, "https://spb.mk.ru/"),
        NewsSource("Фонтанка.ру", SourceType.WEBSITE, "https://fontanka.ru/"),
        NewsSource("78.ru", SourceType.WEBSITE, "https://78.ru/"),
        NewsSource("СПб Новости", SourceType.TELEGRAM, "@spb_news"),
        NewsSource("Питер ТВ", SourceType.TELEGRAM, "@PiterTV"),
        NewsSource("Город 812", SourceType.TELEGRAM, "@gorod812")
    )
}

data class City(
    val id: Int,
    val name: String,
    val sources: List<NewsSource>
)
