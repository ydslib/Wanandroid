package com.yds.base.net

import android.util.Log
import com.franmontiel.persistentcookiejar.PersistentCookieJar
import com.franmontiel.persistentcookiejar.cache.SetCookieCache
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor
import com.yds.base.net.convert.NullConverterFactory
import com.yds.base.util.Utils
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    lateinit var BASE_URL: String
    lateinit var mInterceptors:List<Interceptor>

    private val cookiePersistor by lazy {
        SharedPrefsCookiePersistor(Utils.getApp())
    }

    private val cookieJar:PersistentCookieJar by lazy {
        PersistentCookieJar(SetCookieCache(), cookiePersistor)
    }
    private val logger = HttpLoggingInterceptor.Logger {
        Log.i(this::class.simpleName,it)
    }

    private val logInterceptor = HttpLoggingInterceptor(logger).apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient by lazy {
        val builder = OkHttpClient.Builder()
            .callTimeout(30, TimeUnit.SECONDS)
            .cookieJar(cookieJar)
            .addNetworkInterceptor(logInterceptor)
        mInterceptors.forEach { interceptor ->
            builder.addInterceptor(interceptor)
        }
        builder.build()
    }

    private val retrofit by lazy {
        Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .addConverterFactory(NullConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> create(service:Class<T>?):T = retrofit.create(service!!)?:throw RuntimeException("Api service is null!")



    fun setUp(baseUrl:String,interceptors:List<Interceptor>){
        BASE_URL = baseUrl
        mInterceptors = interceptors
    }

    fun clearCookie() = cookieJar.clear()



}