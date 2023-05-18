package com.carles.simple.domain

import com.carles.simple.common.AppSchedulers
import com.carles.simple.data.HyruleRepository
import com.carles.simple.model.MonsterDetail
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Single
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import org.junit.Test

class GetMonsterDetailTest {

    private val scheduler = TestScheduler()
    private val schedulers = AppSchedulers(scheduler, scheduler, scheduler)
    private val repository: HyruleRepository = mockk()
    private lateinit var usecase: GetMonsterDetail

    @Before
    fun setup() {
        usecase = GetMonsterDetail(repository, schedulers)
    }

    @Test
    fun `given usecase, when called, then get monster detail from repository`() {
        val monsterDetail = MonsterDetail(1, "Monster", "here", "big monster", "https://url")
        every { repository.getMonsterDetail(any()) } returns Single.just(monsterDetail)
        val observer = usecase.execute(1).test()
        scheduler.triggerActions()

        verify { repository.getMonsterDetail(1) }
        observer.assertValue(monsterDetail)
        observer.assertComplete()
    }
}