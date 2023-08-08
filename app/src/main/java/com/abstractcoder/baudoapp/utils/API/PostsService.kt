package com.abstractcoder.baudoapp.utils.API

import com.abstractcoder.baudoapp.utils.wompi.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface PostsService {

    @GET(HttpRoutes.TRANSACTIONS)
    fun getTransaction(@Path("transaction_id") transaction_id: String): Call<TransactionResponse>

}