package com.carles.simple.domain

import com.carles.simple.AppSchedulers
import com.carles.simple.data.SettingsRepository
import io.reactivex.Completable
import javax.inject.Inject

class ResetCacheExpiration @Inject constructor(
    private val repository: SettingsRepository,
    private val schedulers: AppSchedulers
) {

    fun execute(): Completable {
        return repository.resetCacheExpiration()
            .subscribeOn(schedulers.io)
            .observeOn(schedulers.ui)
    }
}