package com.jzh.mvvm.httpUtils

import android.util.Log
import com.jzh.mvvm.base.BaseApplication
import com.jzh.mvvm.constant.HttpConstant
import com.jzh.mvvm.httpUtils.interceptor.CacheInterceptor
import com.jzh.mvvm.httpUtils.interceptor.HeaderInterceptor
import com.jzh.mvvm.httpUtils.interceptor.SaveCookieInterceptor
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

object RetrofitClient {

    //请求的地址
    val BASE_URL = "https://wanandroid.com/"

    //retrofit对象
    private var retrofit: Retrofit? = null

    //请求的api，可以根据不同的场景设置多个
    val service: ApiService by lazy {
        getRetrofit().create(ApiService::class.java)
    }

    private fun getRetrofit(): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
//                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build()
        }
        return retrofit!!
    }

    /**
     * 获取 OkHttpClient
     */
    private fun getOkHttpClient(): OkHttpClient {
        val builder = OkHttpClient().newBuilder()
        val loggingInterceptor: HttpLoggingInterceptor
//        if (BuildConfig.DEBUG) {
//            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
//        } else {
//            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.NONE
//        }

//        if (BuildConfig.DEBUG) {
        loggingInterceptor =
            HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    Log.d("接口请求log----->", message)
                }
            })
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
//        } else loggingInterceptor = HttpLoggingInterceptor()

        //设置 请求的缓存的大小跟位置
        val cacheFile = File(BaseApplication.mContext.cacheDir, "cache")
        val cache = Cache(cacheFile, HttpConstant.MAX_CACHE_SIZE)

        builder.run {
            addInterceptor(loggingInterceptor)
            addInterceptor(HeaderInterceptor())
            addInterceptor(SaveCookieInterceptor())
            addInterceptor(CacheInterceptor())
            cache(cache)  //添加缓存
            connectTimeout(HttpConstant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            readTimeout(HttpConstant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            writeTimeout(HttpConstant.DEFAULT_TIMEOUT, TimeUnit.SECONDS)
            retryOnConnectionFailure(true) // 错误重连
            // cookieJar(CookieManager())
        }
        return builder.build()
    }
}