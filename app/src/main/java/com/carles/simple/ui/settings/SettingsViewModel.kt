package com.carles.simple.ui.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import com.carles.simple.common.extensions.addTo
import com.carles.simple.domain.ResetCacheExpiration
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val resetCacheExpiration: ResetCacheExpiration
) : ViewModel() {

    private val disposables = CompositeDisposable()

    fun onPreferenceCacheChanged() {
        resetCacheExpiration.execute().subscribe({
            // nothing to do here
        }, { error ->
            Log.w("SettingsViewModel", error)
        }).addTo(disposables)
    }

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }
}