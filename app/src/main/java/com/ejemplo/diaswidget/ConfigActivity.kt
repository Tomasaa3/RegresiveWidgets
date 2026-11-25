package com.ejemplo.diaswidget

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.RadioButton
import java.time.LocalDate

class ConfigActivity : Activity() {
    private var widgetId = AppWidgetManager.INVALID_APPWIDGET_ID

    override fun onCreate(s: Bundle?) {
        super.onCreate(s)
        setResult(RESULT_CANCELED)
        setContentView(R.layout.activity_config)

        widgetId = intent?.extras?.getInt(
            AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
        ) ?: AppWidgetManager.INVALID_APPWIDGET_ID
        if (widgetId == AppWidgetManager.INVALID_APPWIDGET_ID) { finish(); return }

        val rbCountdown = findViewById<RadioButton>(R.id.rbCountdown)
        val rbCounter = findViewById<RadioButton>(R.id.rbCounter)
        val picker = findViewById<DatePicker>(R.id.picker)
        val label = findViewById<EditText>(R.id.etLabel)

        val toggle = View.OnClickListener {
            picker.visibility = if (rbCountdown.isChecked) View.VISIBLE else View.GONE
        }
        rbCountdown.setOnClickListener(toggle)
        rbCounter.setOnClickListener(toggle)

        findViewById<Button>(R.id.save).setOnClickListener {
            val mode = if (rbCountdown.isChecked) Store.COUNTDOWN else Store.COUNTER
            val target = LocalDate.of(picker.year, picker.month + 1, picker.dayOfMonth).toEpochDay()
            Store.saveConfig(this, widgetId, mode, label.text.toString().trim(), target)
            DiasWidget.render(this, AppWidgetManager.getInstance(this), widgetId)
            setResult(RESULT_OK, Intent().putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId))
            finish()
        }
    }
}
