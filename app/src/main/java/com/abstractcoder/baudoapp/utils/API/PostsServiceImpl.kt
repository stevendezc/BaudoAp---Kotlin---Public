package com.pereira.baudoapp.utils.API

import com.pereira.baudoapp.utils.wompi.HttpRoutes
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PostsServiceImpl() {

    fun createRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(HttpRoutes.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

}