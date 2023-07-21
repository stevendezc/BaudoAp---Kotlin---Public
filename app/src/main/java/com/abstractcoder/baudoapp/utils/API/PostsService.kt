package com.abstractcoder.baudoapp.utils.API

import com.abstractcoder.baudoapp.utils.wompi.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface PostsService {

    @GET(HttpRoutes.MERCHANT_TOKEN)
    fun getMerchantToken(@Path("merchantPublicKey") merchantPublicKey: String): Call<AcceptanceTokenResponse>

    @POST(HttpRoutes.CC_TOKENIZE)
    fun tokenizeCreditCard(@Body postRequest: CcTokenRequestData): Call<CcTokenResponse>

    @POST(HttpRoutes.TRANSACTIONS)
    fun makeTransaction(@Body postRequest: CcTxRequestData): Call<CcTxResponse>

}