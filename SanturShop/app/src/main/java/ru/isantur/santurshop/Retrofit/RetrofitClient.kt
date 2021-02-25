package ru.isantur.santurshop.Retrofit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit


object RetrofitClient {
    private var ourInstance: Retrofit? = null
    const val uRL = "http://isantur.ru/mobile/"


    var client: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(60L, TimeUnit.SECONDS)
        .connectTimeout(60L, TimeUnit.SECONDS)
        .build()


    val instance: Retrofit?

        get() {
            if (ourInstance == null) {



                ourInstance = Retrofit.Builder()
                    .baseUrl("http://isantur.ru/mobile/")
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(client)
                    .build()
            }
            return ourInstance
        }




}

