package com.abstractcoder.baudoapp

data class NavegantesLinks(
    val libre: String? = "",
    val quebrada_anual: String? = "",
    val quebrada_mes: String? = "",
    val riachuelo_anual: String? = "",
    val riachuelo_mes: String? = "",
    val rio_anual: String? = "",
    val rio_mes: String? = "",
) {
    operator fun get(propertyName: String): String? {
        return when (propertyName) {
            "libre" -> libre
            "quebrada_anual" -> quebrada_anual
            "quebrada_mes" -> quebrada_mes
            "riachuelo_anual" -> riachuelo_anual
            "riachuelo_mes" -> riachuelo_mes
            "rio_anual" -> rio_anual
            "rio_mes" -> rio_mes
            else -> null
        }
    }
}
