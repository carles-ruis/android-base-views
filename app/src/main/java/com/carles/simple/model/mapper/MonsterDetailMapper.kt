package com.carles.simple.model.mapper

import com.carles.simple.data.local.MonsterDetailEntity
import com.carles.simple.data.remote.MonsterDetailResponseDto
import com.carles.simple.model.MonsterDetail
import javax.inject.Inject

class MonsterDetailMapper @Inject constructor() {

    fun toEntity(dto: MonsterDetailResponseDto): MonsterDetailEntity =
        with(dto.data) {
            MonsterDetailEntity(
                id = id,
                name = name,
                commonLocations = commonLocations?.joinToString(", ") ?: "",
                description = description,
                image = image
            )
        }

    fun fromEntity(entity: MonsterDetailEntity): MonsterDetail =
        MonsterDetail(
            id = entity.id,
            name = entity.name,
            commonLocations = entity.commonLocations,
            description = entity.description,
            image = entity.image
        )
}