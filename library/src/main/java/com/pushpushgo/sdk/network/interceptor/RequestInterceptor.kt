package com.pushpushgo.sdk.network.interceptor

import okhttp3.Interceptor
import okhttp3.Response

class RequestInterceptor(private val apiKey: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(
            chain.request()
                .newBuilder()
                .header("Content-Type", "application/json")
                .header("X-Token", apiKey)
                .build()
        )
    }
}
