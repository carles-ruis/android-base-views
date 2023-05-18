package com.carles.simple.data.remote

import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HyruleRemoteDatasource @Inject constructor(private val api: HyruleApi) {

    fun getMonsters(): Single<MonstersResponseDto> {
        return api.getMonsters()
    }

    fun getMonsterDetail(id: Int): Single<MonsterDetailResponseDto> {
        return api.getMonsterDetail(id.toString())
    }
}