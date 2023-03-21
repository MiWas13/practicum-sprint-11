package ru.yandex.practicum.sprint11

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val TAG = "SPRINT_11"
    }

    private val adapter = NewsAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val itemsRv: RecyclerView = findViewById(R.id.items)
        itemsRv.adapter = adapter

        val retrofit = Retrofit.Builder()
            .baseUrl("https://raw.githubusercontent.com/avanisimov/practicum-sprint-11/")
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder()
                        .registerTypeAdapter(Date::class.java, CustomDateTypeAdapter())
                        .registerTypeAdapter(NewsItem::class.java, NewsItemTypeAdapter())
                        .create()
                )
            )
            .build()
        val serverApi = retrofit.create(Sprint11ServerApi::class.java)

        serverApi.getNews2().enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                Log.d(TAG, "onResponse: ${response.body()}")
                adapter.items =
                    response.body()?.data?.items?.filter { it !is NewsItem.Unknown } ?: emptyList()
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                Log.d(TAG, "onFailure: $t")
            }

        })
    }
}

// https://raw.githubusercontent.com/avanisimov/practicum-sprint-11/main/jsons/news_1.json

interface Sprint11ServerApi {


    @GET("main/jsons/news_1.json")
    fun getNews1(): Call<NewsResponse>

    @GET("main/jsons/news_2.json")
    fun getNews2(): Call<NewsResponse>
}
