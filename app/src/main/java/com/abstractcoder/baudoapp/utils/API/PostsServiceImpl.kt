package com.abstractcoder.baudoapp.utils.API

import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.abstractcoder.baudoapp.utils.wompi.CcTokenRequestData
import com.abstractcoder.baudoapp.utils.wompi.CcTokenResponse
import com.abstractcoder.baudoapp.utils.wompi.HttpRoutes
import com.abstractcoder.baudoapp.utils.wompi.WompiKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL

class PostsServiceImpl() {

    private fun getClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HeaderInterceptor())
            .build()
    }
    fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(HttpRoutes.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(getClient())
            .build()
    }

}