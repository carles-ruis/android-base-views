package com.carles.simple.data.local

import com.carles.simple.data.cache.Cache
import com.carles.simple.data.cache.CacheItems
import com.carles.simple.data.cache.CacheKey
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HyruleLocalDatasource @Inject constructor(
    private val dao: MonsterDao,
    private val cache: Cache,
) {

    fun getMonsters(): Single<List<MonsterEntity>> {
        return if (cache.isCached(CacheKey(CacheItems.MONSTERS))) {
            dao.loadMonsters()
        } else {
            Single.error(ItemNotCachedException)
        }
    }

    fun getMonsterDetail(id: Int): Single<MonsterDetailEntity> {
        return if (cache.isCached(CacheKey(CacheItems.MONSTER_DETAIL, id))) {
            dao.loadMonsterDetail(id)
        } else {
            Single.error(ItemNotCachedException)
        }
    }

    fun persist(monsters: List<MonsterEntity>): Completable {
        return dao.deleteMonsters().flatMapCompletable {
            dao.insertMonsters(monsters).flatMapCompletable {
                Completable.fromAction {
                    cache.set(CacheKey(CacheItems.MONSTERS))
                }
            }
        }
    }

    fun persist(monster: MonsterDetailEntity): Completable {
        return dao.insertMonsterDetail(monster).flatMapCompletable {
            Completable.fromAction {
                cache.set(CacheKey(CacheItems.MONSTER_DETAIL, monster.id))
            }
        }
    }
}