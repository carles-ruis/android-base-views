package com.carles.simple.ui.hyrule

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.carles.simple.Navigation
import com.carles.simple.R
import com.carles.simple.common.extensions.addTo
import com.carles.simple.common.ui.MutableResourceLiveData
import com.carles.simple.common.ui.ResourceLiveData
import com.carles.simple.common.ui.setError
import com.carles.simple.common.ui.setLoading
import com.carles.simple.common.ui.setSuccess
import com.carles.simple.domain.GetMonsterDetail
import com.carles.simple.model.MonsterDetail
import com.carles.simple.ui.hyrule.MonsterDetailFragment.Companion.EXTRA_ID
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

@HiltViewModel
class MonsterDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getMonsterDetail: GetMonsterDetail,
    private val navigation: Navigation
) : ViewModel() {

    private val disposables = CompositeDisposable()
    private val id = savedStateHandle.get<Int>(EXTRA_ID) ?: 0

    private val _monsterDetail = MutableResourceLiveData<MonsterDetail>()
    val monsterDetail: ResourceLiveData<MonsterDetail> = _monsterDetail

    init {
        getMonsterDetail()
    }

    private fun getMonsterDetail() {
        getMonsterDetail.execute(id)
            .doOnSubscribe {
                _monsterDetail.setLoading()
            }.subscribe({ monster ->
                _monsterDetail.setSuccess(monster)
            }, { error ->
                Log.w("MonsterDetailViewModel", error)
                _monsterDetail.setError(error.message)
            }).addTo(disposables)
    }

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }

    fun retry() {
        getMonsterDetail()
    }

    fun onErrorDisplayed(message: String) {
        navigation.toErrorDialog(message)
    }

    fun onErrorDismissed() {
        navigation.upTo(R.id.monsters_destination)
    }
}