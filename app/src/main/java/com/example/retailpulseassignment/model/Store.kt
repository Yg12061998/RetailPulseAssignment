package com.example.retailpulseassignment.model

import kotlinx.serialization.Serializable

data class Store(
    val storeId: String,
    val address: String,
    val area: String,
    val name: String,
    val route: String,
    val type: String
) : java.io.Serializable
