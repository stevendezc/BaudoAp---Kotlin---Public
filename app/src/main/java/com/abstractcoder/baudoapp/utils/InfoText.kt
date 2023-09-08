package com.pereira.baudoapp.utils

import android.content.Context
import com.pereira.baudoapp.R

class InfoText() {
    var title: String = ""
    var mainText: String = ""
    var complementary: String = ""
    var complementary2: String = ""

    fun assignValues(context: Context, infoType: String) {
        val resources = context.resources
        when (infoType) {
            "perfil" -> mainText = resources.getString(R.string.info_perfil_main_text)
            "navegantes" -> mainText = resources.getString(R.string.info_navegantes_main_text)
            "contenido" -> mainText = resources.getString(R.string.info_contenido_main_text)
            "comunidad" -> {
                title = resources.getString(R.string.info_comunidad_title)
                mainText = resources.getString(R.string.info_comunidad_main_text)
                complementary = resources.getString(R.string.info_comunidad_complementary_text)
            }
            "eventos" -> {
                complementary = resources.getString(R.string.info_eventos_complementary_text)
                complementary2 = resources.getString(R.string.info_eventos_complementary_text2)
            }
        }
    }
}