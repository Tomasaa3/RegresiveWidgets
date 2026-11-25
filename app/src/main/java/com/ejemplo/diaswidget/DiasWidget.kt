package com.ejemplo.diaswidget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import java.time.LocalDate

class DiasWidget : AppWidgetProvider() {

    override fun onUpdate(c: Context, m: AppWidgetManager, ids: IntArray) {
        ids.forEach { render(c, m, it) }
    }

    override fun onDeleted(c: Context, ids: IntArray) {
        ids.forEach { Store.clear(c, it) }
    }

    override fun onReceive(c: Context, i: Intent) {
        super.onReceive(c, i)
        val m = AppWidgetManager.getInstance(c)
        when (i.action) {
            ACTION_PLUS -> {
                val id = i.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1)
                if (id != -1) {
                    val today = LocalDate.now().toEpochDay()
                    normalize(c, id, today)
                    if (Store.last(c, id) == today) {
                        Toast.makeText(c, "Ya sumaste hoy. Volvé mañana.", Toast.LENGTH_SHORT).show()
                    } else {
                        Store.saveCounter(c, id, Store.count(c, id) + 1, today)
                    }
                    render(c, m, id)
                }
            }
            Intent.ACTION_DATE_CHANGED, Intent.ACTION_TIME_CHANGED, Intent.ACTION_TIMEZONE_CHANGED -> {
                m.getAppWidgetIds(ComponentName(c, DiasWidget::class.java)).forEach { render(c, m, it) }
            }
        }
    }

    companion object {
        const val ACTION_PLUS = "com.ejemplo.diaswidget.PLUS"

        /** Si pasó un día entero sin sumar (hoy - último >= 2), vuelve a 0. */
        private fun normalize(c: Context, id: Int, today: Long) {
            val last = Store.last(c, id)
            if (last >= 0 && today - last >= 2) Store.saveCounter(c, id, 0, -1L)
        }

        fun render(c: Context, m: AppWidgetManager, id: Int) {
            val today = LocalDate.now().toEpochDay()
            val label = Store.label(c, id)
            val views: RemoteViews

            if (Store.mode(c, id) == Store.COUNTDOWN) {
                views = RemoteViews(c.packageName, R.layout.widget_countdown)
                val days = maxOf(0L, Store.target(c, id) - today)
                views.setTextViewText(R.id.number, days.toString())
            } else {
                normalize(c, id, today)
                views = RemoteViews(c.packageName, R.layout.widget_counter)
                views.setTextViewText(R.id.number, Store.count(c, id).toString())
                val doneToday = Store.last(c, id) == today
                views.setTextViewText(R.id.btn, if (doneToday) "✓" else "+1")
                views.setInt(
                    R.id.btn, "setBackgroundResource",
                    if (doneToday) R.drawable.bg_button_done else R.drawable.bg_button
                )
                val intent = Intent(c, DiasWidget::class.java)
                    .setAction(ACTION_PLUS)
                    .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
                val pi = PendingIntent.getBroadcast(
                    c, id, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                views.setOnClickPendingIntent(R.id.btn, pi)
            }

            if (label.isNotBlank()) {
                views.setTextViewText(R.id.label, label)
                views.setViewVisibility(R.id.label, View.VISIBLE)
            } else {
                views.setViewVisibility(R.id.label, View.GONE)
            }
            m.updateAppWidget(id, views)
        }
    }
}
