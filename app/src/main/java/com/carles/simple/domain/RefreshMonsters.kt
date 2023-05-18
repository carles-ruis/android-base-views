package com.carles.simple.domain

import com.carles.simple.model.Monster
import com.carles.simple.common.AppSchedulers
import com.carles.simple.data.HyruleRepository
import io.reactivex.Single
import javax.inject.Inject

class RefreshMonsters @Inject constructor(
    private val repository: HyruleRepository,
    private val schedulers: AppSchedulers
) {

    fun execute(): Single<List<Monster>> {
        return repository.refreshMonsters()
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
    }
}