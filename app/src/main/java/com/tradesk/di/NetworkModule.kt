package com.tradesk.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton
import com.tradesk.BuildConfig
import com.tradesk.network.EasyBuliderAPI
import com.tradesk.preferences.PreferenceConstants
import com.tradesk.preferences.PreferenceHelper
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InstallIn(SingletonComponent::class)
@Module
class NetworkModule  {

    private val READ_TIMEOUT = 30
    private val WRITE_TIMEOUT = 30
    private val CONNECTION_TIMEOUT = 10
    private val CACHE_SIZE_BYTES = 10 * 1024 * 1024L
    @Provides
    @Singleton
    fun provideOkHttpClient(headerInterceptor: Interceptor, cache: Cache): OkHttpClient {

        val okHttpClientBuilder = OkHttpClient().newBuilder()
        okHttpClientBuilder.connectTimeout(CONNECTION_TIMEOUT.toLong(), TimeUnit.SECONDS)
        okHttpClientBuilder.readTimeout(READ_TIMEOUT.toLong(), TimeUnit.SECONDS)
        okHttpClientBuilder.writeTimeout(WRITE_TIMEOUT.toLong(), TimeUnit.SECONDS)
        okHttpClientBuilder.cache(cache)
        okHttpClientBuilder.addInterceptor(headerInterceptor)
        return okHttpClientBuilder.build()
    }

    @Provides
    @Singleton
    fun getHeadersForApis(preferenceHelper: PreferenceHelper): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            if (preferenceHelper.isUserLoggedIn(PreferenceConstants.USER_LOGGED_IN)) {
                request = request.newBuilder()
                    .addHeader(
                        "Authorization",
                        "Bearer " + preferenceHelper.getKeyValue(PreferenceConstants.USER_TOKEN)
                    )
                    .build()
            }
            chain.proceed(request)
        }
    }
    @Provides
    @Singleton
    internal fun provideCache(@ApplicationContext context: Context): Cache {
        val httpCacheDirectory = File(context.cacheDir.absolutePath, "HttpCache")
        return Cache(httpCacheDirectory, CACHE_SIZE_BYTES)
    }

    @Singleton
    @Provides
    fun providesRetrofit(client: OkHttpClient) : Retrofit
    {
        return Retrofit
            .Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun providesFakerApi(retrofit: Retrofit) : EasyBuliderAPI
    {
        return retrofit.create(EasyBuliderAPI::class.java)
    }

}