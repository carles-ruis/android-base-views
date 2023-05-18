package com.carles.simple.data

import com.carles.simple.model.Monster
import com.carles.simple.model.MonsterDetail
import com.carles.simple.model.mapper.MonsterDetailMapper
import com.carles.simple.model.mapper.MonstersMapper
import com.carles.simple.data.local.HyruleLocalDatasource
import com.carles.simple.data.remote.HyruleRemoteDatasource
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HyruleRepository @Inject constructor(
    private val localDatasource: HyruleLocalDatasource,
    private val remoteDatasource: HyruleRemoteDatasource,
    private val monstersMapper: MonstersMapper,
    private val monsterDetailMapper: MonsterDetailMapper
) {

    fun getMonsters(): Single<List<Monster>> {
        return localDatasource.getMonsters().map { entity ->
            monstersMapper.fromEntity(entity)
        }.onErrorResumeNext {
            refreshMonsters()
        }
    }

    fun refreshMonsters(): Single<List<Monster>> {
        return remoteDatasource.getMonsters().map { dto ->
            monstersMapper.toEntity(dto)
        }.flatMap {
            localDatasource.persist(it).andThen(
                Single.defer {
                    localDatasource.getMonsters().map { entity ->
                        monstersMapper.fromEntity(entity)
                    }
                }
            )
        }
    }

    fun getMonsterDetail(id: Int): Single<MonsterDetail> {
        return localDatasource.getMonsterDetail(id).map { entity ->
            monsterDetailMapper.fromEntity(entity)
        }.onErrorResumeNext {
            refreshMonsterDetail(id)
        }
    }

    fun refreshMonsterDetail(id: Int): Single<MonsterDetail> {
        return remoteDatasource.getMonsterDetail(id).map { dto ->
            monsterDetailMapper.toEntity(dto)
        }.flatMap {
            localDatasource.persist(it).andThen(
                Single.defer {
                    localDatasource.getMonsterDetail(id).map { entity ->
                        monsterDetailMapper.fromEntity(entity)
                    }
                }
            )
        }
    }
}