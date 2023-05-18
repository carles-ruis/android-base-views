package com.carles.simple.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.carles.simple.data.local.HyruleDatabase
import com.carles.simple.data.local.MonsterDao
import com.carles.simple.data.remote.HyruleApi
import com.carles.simple.common.AppSchedulers
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.facebook.stetho.okhttp3.StethoInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ProvideModule {

    private const val BASE_URL = "https://botw-compendium.herokuapp.com"

    @Provides
    @Singleton
    fun provideRetrofit(app: Application): Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            setLevel(HttpLoggingInterceptor.Level.BODY)
        }
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(ChuckerInterceptor.Builder(app.applicationContext).build())
            .addNetworkInterceptor(StethoInterceptor())
            .build()
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .baseUrl(BASE_URL)
            .build()
    }

    @Provides
    @Singleton
    fun provideAppSchedulers(): AppSchedulers {
        return AppSchedulers(
            io = Schedulers.io(),
            ui = AndroidSchedulers.mainThread(),
            new = Schedulers.newThread()
        )
    }

    @Provides
    @Singleton
    fun provideHyruleDatabase(
        @ApplicationContext
        context: Context
    ): HyruleDatabase {
        return Room.databaseBuilder(context, HyruleDatabase::class.java, "hyrule_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideMonsterDao(database: HyruleDatabase): MonsterDao {
        return database.monsterDao()
    }

    @Provides
    @Singleton
    fun provideHyruleApi(retrofit: Retrofit): HyruleApi {
        return retrofit.create(HyruleApi::class.java)
    }
}