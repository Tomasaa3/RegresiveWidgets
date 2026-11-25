package com.ejemplo.diaswidget

import android.content.Context

object Store {
    const val COUNTDOWN = "countdown"
    const val COUNTER = "counter"

    private fun p(c: Context) = c.getSharedPreferences("widgets", Context.MODE_PRIVATE)

    fun mode(c: Context, id: Int) = p(c).getString("mode_$id", COUNTDOWN)!!
    fun label(c: Context, id: Int) = p(c).getString("label_$id", "")!!
    fun target(c: Context, id: Int) = p(c).getLong("target_$id", 0L)   // epochDay
    fun count(c: Context, id: Int) = p(c).getInt("count_$id", 0)
    fun last(c: Context, id: Int) = p(c).getLong("last_$id", -1L)      // epochDay del último +1

    fun saveConfig(c: Context, id: Int, mode: String, label: String, target: Long) {
        p(c).edit().putString("mode_$id", mode).putString("label_$id", label)
            .putLong("target_$id", target).putInt("count_$id", 0).putLong("last_$id", -1L).apply()
    }

    fun saveCounter(c: Context, id: Int, count: Int, last: Long) {
        p(c).edit().putInt("count_$id", count).putLong("last_$id", last).apply()
    }

    fun clear(c: Context, id: Int) {
        p(c).edit().remove("mode_$id").remove("label_$id").remove("target_$id")
            .remove("count_$id").remove("last_$id").apply()
    }
}
