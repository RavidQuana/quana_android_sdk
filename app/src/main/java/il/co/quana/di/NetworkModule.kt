package il.co.quana.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import il.co.quana.network.ApiSettings
import il.co.quana.network.AuthInterceptor
import il.co.quana.network.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

const val BASE_SERVER_URL = "http://quana-server-staging.herokuapp.com/ml/"

val networkModule = module {

    single { provideSharedPreferences(get()) }
    factory { ApiSettings(get()) }

    single { provideDefaultOkhttpClient(get()) }
    single { provideRetrofit(get()) }
    single { provideApiService(get()) }
}

fun provideSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences("User", Context.MODE_PRIVATE)
}

fun provideDefaultOkhttpClient(apiSettings: ApiSettings): OkHttpClient {
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BODY

    val authInterceptor = AuthInterceptor(apiSettings)

    val httpClient = OkHttpClient.Builder().addInterceptor(logging).addInterceptor(authInterceptor)
    return httpClient.build()
}

fun provideRetrofit(client: OkHttpClient): Retrofit {
    val gson = GsonBuilder()
    gson.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
    return Retrofit.Builder()
        .baseUrl(BASE_SERVER_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(gson.create()))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
}

fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
