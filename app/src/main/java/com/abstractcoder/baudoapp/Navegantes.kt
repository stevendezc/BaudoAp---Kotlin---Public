package com.pereira.baudoapp

data class NavegantesLinks(
    val aporte_anual: String? = "",
    val aporte_libre: String? = "",
    val aporte_mensual: String? = "",
) {
    operator fun get(propertyName: String): String? {
        return when (propertyName) {
            "aporte_anual" -> aporte_anual
            "aporte_libre" -> aporte_libre
            "aporte_mensual" -> aporte_mensual
            else -> null
        }
    }
}
