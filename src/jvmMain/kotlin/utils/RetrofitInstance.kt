package utils

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Server.Localhost.url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}