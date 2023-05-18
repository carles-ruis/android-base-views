package com.carles.simple.ui.hyrule

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.carles.hyrule.ui.MonstersState
import com.carles.hyrule.ui.MonstersViewModel
import com.carles.simple.Navigation
import com.carles.simple.domain.RefreshMonsters
import com.carles.simple.model.Monster
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class MonstersViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val refreshMonsters: RefreshMonsters = mockk()
    private val navigation: Navigation = mockk()
    private val monster = Monster(1, "Monster")
    private val monsters = listOf(monster)
    private val errorMessage = "damned error"

    private lateinit var viewModel: MonstersViewModel

    @Test
    fun `given initialization, when monsters are obtained successfully, then update ui properly`() {
        every { refreshMonsters.execute() } returns Single.just(monsters)

        viewModel = MonstersViewModel(refreshMonsters, navigation)

        val result = viewModel.uiState.value!! as MonstersState.Success
        assertEquals(result.monsters, monsters)
    }

    @Test
    fun `given initialization, when there is an error obtaining monsters, then update ui properly`() {
        every { refreshMonsters.execute() } returns Single.error(Exception(errorMessage))

        viewModel = MonstersViewModel(refreshMonsters, navigation)

        verify { refreshMonsters.execute() }
        val result = viewModel.uiState.value!! as MonstersState.Error
        assertEquals(result.message, errorMessage)
    }

    @Test
    fun `given retry, when called, then refresh monsters`() {
        every { refreshMonsters.execute() } returns Single.just(monsters)

        viewModel = MonstersViewModel(refreshMonsters, navigation)
        viewModel.retry()

        verify(exactly = 2) { refreshMonsters.execute() }
    }

    @Test
    fun `given onMonsterClicked, when called, then navigate to monster detail`() {
        every { refreshMonsters.execute() } returns Single.just(monsters)
        every { navigation.toMonsterDetail(any()) } just Runs

        viewModel = MonstersViewModel(refreshMonsters, navigation)
        viewModel.onMonsterClicked(monster)

        verify { navigation.toMonsterDetail(monster.id) }
    }

    @Test
    fun `given onErrorEvent, when called, then navigate to error dialog`() {
        every { refreshMonsters.execute() } returns Single.just(monsters)
        every { navigation.toErrorDialog(any(), any()) } just Runs

        viewModel = MonstersViewModel(refreshMonsters, navigation)
        viewModel.onErrorDisplayed(errorMessage)

        verify { navigation.toErrorDialog(errorMessage, true) }
    }
}