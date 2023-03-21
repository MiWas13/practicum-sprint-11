package ru.yandex.practicum.sprint11

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.annotations.SerializedName
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*


data class NewsResponse(
    val result: String,
    val data: Data
)

data class Data(
    val title: String,
    val items: List<NewsItem>
)

sealed class NewsItem {

    abstract val id: String
    abstract val title: String
    abstract val type: String
    abstract val created: Date

    data class Sport(
        override val id: String,
        override val title: String,
        override val type: String,
        override val created: Date,
        val specificPropertyForSport: String,
    ) : NewsItem()

    data class Science(
        override val id: String,
        override val title: String,
        override val type: String,
        override val created: Date,
        @SerializedName("specific_property_for_science")
        val specificPropertyForScience: String
    ) : NewsItem()


    object Unknown : NewsItem() {
        override val id: String
            get() = ""
        override val title: String
            get() = ""
        override val type: String
            get() = ""
        override val created: Date
            get() = Date()
    }

    enum class Type {
        @SerializedName("sport")
        SPORT,

        @SerializedName("science")
        SCIENCE
    }
}


class CustomDateTypeAdapter : TypeAdapter<Date>() {

    // https://ru.wikipedia.org/wiki/ISO_8601
    companion object {
        const val FORMAT_PATTERN = "yyyy-MM-DD'T'hh:mm:ss:SSS"
    }

    private val formatter = SimpleDateFormat(FORMAT_PATTERN, Locale.getDefault())
    override fun write(out: JsonWriter, value: Date) {
        out.value(formatter.format(value))
    }

    override fun read(`in`: JsonReader): Date {
        return formatter.parse(`in`.nextString())
    }

}

class NewsItemTypeAdapter : JsonDeserializer<NewsItem> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): NewsItem {
        val type = context.deserialize<NewsItem.Type>(
            json.asJsonObject.get("type"),
            NewsItem.Type::class.java
        )
        return when (type) {
            NewsItem.Type.SPORT -> context.deserialize<NewsItem.Sport>(
                json,
                NewsItem.Sport::class.java
            )
            NewsItem.Type.SCIENCE -> context.deserialize<NewsItem.Science>(
                json,
                NewsItem.Science::class.java
            )
            else -> NewsItem.Unknown
        }
    }

}