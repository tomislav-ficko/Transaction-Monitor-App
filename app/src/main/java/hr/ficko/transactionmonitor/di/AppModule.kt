package hr.ficko.transactionmonitor.di

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import hr.ficko.transactionmonitor.BuildConfig
import hr.ficko.transactionmonitor.network.ApiService
import hr.ficko.transactionmonitor.other.SHARED_PREFERENCES
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun sharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences(
            SHARED_PREFERENCES,
            MODE_PRIVATE
        )

    @Singleton
    @Provides
    fun apiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    @Singleton
    @Provides
    fun retrofit(
        okHttpClient: OkHttpClient,
        @ApiUrl apiUrl: String,
        moshiConverterFactory: MoshiConverterFactory
    ): Retrofit = Retrofit.Builder()
        .baseUrl(apiUrl)
        .addConverterFactory(moshiConverterFactory)
        .client(okHttpClient)
        .build()

    @Singleton
    @Provides
    fun okHttpClient(loggingInterceptor: Interceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

    @Singleton
    @Provides
    fun moshiConverterFactory(moshi: Moshi): MoshiConverterFactory =
        MoshiConverterFactory.create(moshi)

    @Singleton
    @Provides
    fun moshi(): Moshi = Moshi.Builder().build()

    @Singleton
    @Provides
    fun loggingInterceptor(): Interceptor = Interceptor { chain ->
        val request = chain.request()
        Timber.d(String.format("Sending request to " + request.url))

        val response = chain.proceed(request)
        Timber.d(String.format("Response: " + response.code + " " + response.message))

        response
    }

    @Singleton
    @Provides
    @ApiUrl
    fun apiUrl() = BuildConfig.API_URL
}
