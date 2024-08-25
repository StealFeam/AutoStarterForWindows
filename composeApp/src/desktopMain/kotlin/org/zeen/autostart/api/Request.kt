package org.zeen.autostart.api

import okhttp3.OkHttpClient
import org.zeen.autostart.bean.DailyProperty
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

interface Request {
    @Headers("User-Agent:Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/127.0.0.0 Mobile Safari/537.36")
    @GET("api/holiday/info/{date}")
    fun getDailyProperty(
        @Path("date") date: String =
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    ): Call<DailyProperty>
}

private val client = OkHttpClient.Builder()
    .build()

private val retrofit = Retrofit.Builder()
    .client(client)
    .baseUrl("https://timor.tech/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val request: Request = retrofit.create(Request::class.java)
