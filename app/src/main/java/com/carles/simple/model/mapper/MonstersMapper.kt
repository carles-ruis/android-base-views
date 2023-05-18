package com.carles.simple.model.mapper

import com.carles.simple.data.local.MonsterEntity
import com.carles.simple.data.remote.MonstersResponseDto
import com.carles.simple.model.Monster
import javax.inject.Inject

class MonstersMapper @Inject constructor() {

    fun toEntity(dto: MonstersResponseDto): List<MonsterEntity> =
        dto.data.map { MonsterEntity(it.id, it.name) }

    fun fromEntity(entity: List<MonsterEntity>): List<Monster> =
        entity.map { Monster(it.id, it.name) }
}