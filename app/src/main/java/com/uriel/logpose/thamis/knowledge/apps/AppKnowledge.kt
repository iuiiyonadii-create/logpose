package com.uriel.logpose.thamis.knowledge.apps

import com.uriel.logpose.thamis.intent.Intent
import com.uriel.logpose.thamis.knowledge.KnowledgeRule

object AppKnowledge {

    val rules = listOf(
        KnowledgeRule(
            intent = Intent.OPEN_APP,
            phrases = setOf(
                "abrir whatsapp", "abrí whatsapp", "abri whatsapp", "abre whatsapp", "whatsapp", "guasap", "wasap", "wasa", "guasá",
                "abrir instagram", "abrí instagram", "abri instagram", "abre instagram", "instagram", "insta", "ig",
                "abrir facebook", "facebook", "face", "fase", "feisbuk",
                "abrir tiktok", "tiktok", "tito",
                "abrir mapas", "mapas", "google maps", "maps",
                "abrir spotify", "spotify", "spoty", "spoti", "espotifai",
                "abrir pedido ya", "abri pedido ya", "abrí peya", "abri peya", "peya", "pedidosya", "pedidos ya",
                "abrir rappi", "rappi", "rapi",
                "abrir uber", "uber",
                "abrir pedidos ya",
                "ver netflix", "poner netflix", "abrí netflix", "abrir netflix", "netflix", "nefli", "nesfli", "netfli", "netflixe",
                "ver youtube", "abrí youtube", "youtube", "yutub", "iutub", "yutu",
                "abrir navegador", "abrir cromo", "abrir brave", "navegador", "brave"
            )
        ),
        KnowledgeRule(
            intent = Intent.SWITCH_TAB,
            phrases = setOf(
                "cambiar pestaña",
                "cambiar de pestaña",
                "siguiente pestaña",
                "pestaña siguiente",
                "otra pestaña",
                "cambia de pestaña",
                "cambiá de pestaña",
                "abrir pestaña",
                "abrí pestaña",
                "abri pestaña",
                "la pestaña de",
                "pestaña de"
            )
        )
    )

}