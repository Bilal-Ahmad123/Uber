package com.example.uber.data.remote.api.backend.authentication.mapper

import com.example.uber.data.remote.api.backend.authentication.models.responseModels.CheckRiderExistsResponse
import com.example.uber.domain.remote.authentication.model.response.CheckRiderExists

fun CheckRiderExistsResponse.toDomain(): CheckRiderExists {
    return CheckRiderExists(userExists)
}