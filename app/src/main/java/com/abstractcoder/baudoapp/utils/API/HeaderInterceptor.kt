package com.abstractcoder.baudoapp.utils.API

import com.abstractcoder.baudoapp.utils.wompi.WompiKeys
import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor:Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("Authorization:", "Bearer ${WompiKeys.TEST_PUBKEY}")
            .addHeader("Content-Type:", "application/json")
            .build()

        return chain.proceed(request)
    }
}