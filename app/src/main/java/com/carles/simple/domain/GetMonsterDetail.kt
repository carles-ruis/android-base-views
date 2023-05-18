package com.carles.simple.domain

import com.carles.simple.model.MonsterDetail
import com.carles.simple.common.AppSchedulers
import com.carles.simple.data.HyruleRepository
import io.reactivex.Single
import javax.inject.Inject

class GetMonsterDetail @Inject constructor(
    private val repository: HyruleRepository,
    private val schedulers: AppSchedulers
) {

    fun execute(id: Int): Single<MonsterDetail> {
        return repository.getMonsterDetail(id)
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
    }
}