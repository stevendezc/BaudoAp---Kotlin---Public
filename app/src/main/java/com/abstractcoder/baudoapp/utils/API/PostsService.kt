package com.abstractcoder.baudoapp.utils.API

import com.abstractcoder.baudoapp.utils.wompi.*
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface PostsService {

    @GET(HttpRoutes.MERCHANT_TOKEN)
    suspend fun getMerchantToken(@Path("merchantPublicKey") merchantPublicKey: String): Response<AcceptanceTokenResponse>

    @POST(HttpRoutes.CC_TOKENIZE)
    suspend fun tokenizeCreditCard(@Body postRequest: CcTokenRequestData): Response<CcTokenResponse>

    @POST(HttpRoutes.PAYMENT_SOURCE)
    suspend fun paymentSource(@Body postRequest: PaymentSourceRequestData): Response<PaymentSourceResponse>

    @POST(HttpRoutes.TRANSACTIONS)
    suspend fun makeTransaction(@Body postRequest: CcTxRequestData): Response<CcTxResponse>

}