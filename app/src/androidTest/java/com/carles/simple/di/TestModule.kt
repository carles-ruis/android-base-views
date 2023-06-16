package com.carles.simple.di

import android.content.Context
import androidx.room.Room
import com.carles.simple.AppModule
import com.carles.simple.fakes.FakeHyruleApi
import com.carles.simple.AppSchedulers
import com.carles.simple.data.local.HyruleDatabase
import com.carles.simple.data.local.MonsterDao
import com.carles.simple.data.remote.HyruleApi
import dagger.Module
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Singleton

@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [AppModule::class]
)
@Module
object TestModule {

    @Provides
    @Singleton
    fun provideAppSchedulers(): AppSchedulers {
        return AppSchedulers(
            io = AndroidSchedulers.mainThread(),
            ui = AndroidSchedulers.mainThread(),
            new = AndroidSchedulers.mainThread()
        )
    }

    @Provides
    @Singleton
    fun provideHyruleDatabase(@ApplicationContext context: Context): HyruleDatabase {
        return Room.inMemoryDatabaseBuilder(context, HyruleDatabase::class.java)
            .fallbackToDestructiveMigration()
            .allowMainThreadQueries()
            .build()
    }

    @Provides
    @Singleton
    fun provideMonsterDao(database: HyruleDatabase): MonsterDao {
        return database.monsterDao()
    }

    @Provides
    @Singleton
    fun provideHyruleApi(): HyruleApi {
        return FakeHyruleApi()
    }
}