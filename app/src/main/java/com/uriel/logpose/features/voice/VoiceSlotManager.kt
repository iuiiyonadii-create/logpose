package com.uriel.logpose.features.voice

import android.content.Context
import android.content.SharedPreferences

class VoiceSlotManager(context: Context) {
    private val prefs = context.getSharedPreferences("slots", Context.MODE_PRIVATE)
    
    data class Slot(val slotNumber: Int, val artistName: String, val spotifyQuery: String)
    
    private val slots = mutableMapOf<Int, Slot>()

    init {
        load()
        if (slots.isEmpty()) {
            setDefaults()
        }
    }

    fun setDefaults() {
        setSlot(1, "Duki", "duki")
        setSlot(2, "YSY A", "ysy a")
        setSlot(3, "Rockstar", "rockstar")
        setSlot(4, "Trueno", "trueno")
        setSlot(5, "Ozuna", "ozuna")
        for (i in 6..10) {
            setSlot(i, "", "")
        }
        save()
    }

    fun setSlot(number: Int, name: String, query: String) {
        slots[number] = Slot(number, name, query)
        save()
    }

    fun getSlot(number: Int): Slot? = slots[number]
    
    fun getAllSlots(): List<Slot> = slots.values.sortedBy { it.slotNumber }

    private fun save() {
        val ed = prefs.edit()
        slots.forEach { (k, v) ->
            ed.putString("slot_$k", "${v.artistName}|${v.spotifyQuery}")
        }
        ed.apply()
    }

    private fun load() {
        for (i in 1..20) {
            val s = prefs.getString("slot_$i", null) ?: continue
            val parts = s.split("|")
            if (parts.size == 2) slots[i] = Slot(i, parts[0], parts[1])
        }
    }
}
