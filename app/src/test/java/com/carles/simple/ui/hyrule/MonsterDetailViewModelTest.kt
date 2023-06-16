package com.carles.simple.ui.hyrule

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.SavedStateHandle
import com.carles.simple.Navigation
import com.carles.simple.ui.common.ERROR
import com.carles.simple.ui.common.SUCCESS
import com.carles.simple.domain.GetMonsterDetail
import com.carles.simple.model.MonsterDetail
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MonsterDetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val getMonsterDetail: GetMonsterDetail = mockk()
    private val navigation: Navigation = mockk()
    private val state = SavedStateHandle().apply {
        set(MonsterDetailFragment.EXTRA_ID, 1)
    }

    private val monsterDetail = MonsterDetail(1, "Monster", "here", "big monster", "https://url")
    private val errorMessage = "some error"

    private lateinit var viewModel: MonsterDetailViewModel

    @Test
    fun `given initialization, when monster is obtained successfully, then update monsterDetail properly`() {
        every { getMonsterDetail.execute(any()) } returns Single.just(monsterDetail)

        viewModel = MonsterDetailViewModel(state, getMonsterDetail, navigation)

        verify { getMonsterDetail.execute(1) }
        val result = viewModel.monsterDetail.value!!
        assertTrue(result.state is SUCCESS)
        assertEquals(result.data, monsterDetail)
        assertNull(result.message)
    }

    @Test
    fun `given initialization, when there is an error obtaining monster, then update monsterDetail properly`() {
        every { getMonsterDetail.execute(any()) } returns Single.error(Exception(errorMessage))

        viewModel = MonsterDetailViewModel(state, getMonsterDetail, navigation)

        verify { getMonsterDetail.execute(1) }
        val result = viewModel.monsterDetail.value!!
        assertTrue(result.state is ERROR)
        assertNull(result.data)
        assertEquals(result.message, errorMessage)
    }

    @Test
    fun `given retry, when called, then refresh monster`() {
        every { getMonsterDetail.execute(any()) } returns Single.just(monsterDetail)

        viewModel = MonsterDetailViewModel(state, getMonsterDetail, navigation)
        viewModel.retry()

        verify(exactly = 2) { getMonsterDetail.execute(1) }
    }

    @Test
    fun `given onErrorEvent, when called, then navigate to error dialog`() {
        every { getMonsterDetail.execute(any()) } returns Single.just(monsterDetail)
        every { navigation.toErrorDialog(any()) } just Runs

        viewModel = MonsterDetailViewModel(state, getMonsterDetail, navigation)
        viewModel.onErrorDisplayed(errorMessage)

        verify { navigation.toErrorDialog(errorMessage) }
    }
}