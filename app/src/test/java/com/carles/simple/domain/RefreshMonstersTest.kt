package com.carles.simple.domain

import com.carles.simple.AppSchedulers
import com.carles.simple.data.HyruleRepository
import com.carles.simple.model.Monster
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Test

class RefreshMonstersTest {

    private val scheduler = TestScheduler()
    private val schedulers = AppSchedulers(scheduler, scheduler, scheduler)
    private val repository: HyruleRepository = mockk()
    private lateinit var usecase: RefreshMonsters

    @Before
    fun setup() {
        usecase = RefreshMonsters(repository, schedulers)
    }

    @Test
    fun `given usecase, when called, then refresh monsters from repository`() {
        val monsters = listOf(Monster(1, "Monster"))
        every { repository.refreshMonsters() } returns Single.just(monsters)
        val observer = usecase.execute().test()
        scheduler.triggerActions()

        verify { repository.refreshMonsters() }
        observer.assertValue(monsters)
        observer.assertComplete()
    }
}