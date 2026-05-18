package app.thaita.di

import app.thaita.BuildConfig
import app.thaita.data.local.TokenManager
import app.thaita.data.remote.AuthInterceptor
import app.thaita.data.remote.StreamingService
import app.thaita.data.remote.ThaitaApiService
import app.thaita.data.repository.AnalysisRepositoryImpl
import app.thaita.data.repository.AuthRepositoryImpl
import app.thaita.domain.repository.AnalysisRepository
import app.thaita.domain.repository.AuthRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)

        if (BuildConfig.DEBUG) {
            builder.addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, json: Json): Retrofit {
        val contentType = "application/json".toMediaType()
        return Retrofit.Builder()
            .baseUrl(BuildConfig.API_BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ThaitaApiService =
        retrofit.create(ThaitaApiService::class.java)

    @Provides
    @Singleton
    fun provideStreamingService(
        client: OkHttpClient,
        tokenManager: TokenManager,
    ): StreamingService = StreamingService(client, tokenManager, BuildConfig.API_BASE_URL)

    @Provides
    @Singleton
    fun provideAuthRepository(
        api: ThaitaApiService,
        tokenManager: TokenManager,
    ): AuthRepository = AuthRepositoryImpl(api, tokenManager)

    @Provides
    @Singleton
    fun provideAnalysisRepository(
        api: ThaitaApiService,
        streamingService: StreamingService,
    ): AnalysisRepository = AnalysisRepositoryImpl(api, streamingService)
}
