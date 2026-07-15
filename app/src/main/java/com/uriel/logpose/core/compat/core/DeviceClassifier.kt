package com.uriel.logpose.core.compat.core

import com.uriel.logpose.domain.models.DeviceType

object DeviceClassifier {

    fun detect(deviceName: String): DeviceType {

        val name = deviceName.lowercase()

        return when {

            "cardo" in name ||
                    "sena" in name ||
                    "freedconn" in name ||
                    "lexin" in name ||
                    "fodsports" in name ||
                    "interphone" in name ->
                DeviceType.INTERCOM

            "buds" in name ||
                    "airpods" in name ||
                    "headset" in name ||
                    "earbuds" in name ->
                DeviceType.HEADPHONES

            "car" in name ||
                    "vw" in name ||
                    "ford" in name ||
                    "chevrolet" in name ||
                    "fiat" in name ||
                    "renault" in name ||
                    "peugeot" in name ||
                    "toyota" in name ->
                DeviceType.CAR

            "speaker" in name ||
                    "jbl" in name ||
                    "sony" in name ->
                DeviceType.SPEAKER

            else ->
                DeviceType.UNKNOWN

        }

    }

}