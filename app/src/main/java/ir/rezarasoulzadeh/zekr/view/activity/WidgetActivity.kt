package ir.rezarasoulzadeh.zekr.view.activity

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import android.widget.Toast
import ir.rezarasoulzadeh.zekr.R
import ir.rezarasoulzadeh.zekr.service.utils.Days
import ir.rezarasoulzadeh.zekr.service.utils.Prays


class WidgetActivity : AppWidgetProvider() {

    private val MyOnClick = "myOnClickTag"
    private val prayClick = "prayClick"

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            deleteDayPref(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        val appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
        saveCounterPref(context, appWidgetId, "0")
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
        val appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
        deleteCounterPref(context, appWidgetId)
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if (MyOnClick == intent!!.action) {
            val views = RemoteViews(
                context!!.packageName,
                R.layout.widget_activity
            )
            val appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
            val previousCounter = loadCounterPref(context, appWidgetId)
            saveCounterPref(context, appWidgetId, (previousCounter.toInt() + 1).toString())
            views.setTextViewText(R.id.counterTextView, (previousCounter.toInt() + 1).toString())
            saveCounterPref(context, AppWidgetManager.INVALID_APPWIDGET_ID, (previousCounter.toInt() + 1).toString())
            AppWidgetManager.getInstance(context).updateAppWidget(
                ComponentName(context, WidgetActivity::class.java), views
            )
            updateAppWidget(
                context,
                AppWidgetManager.getInstance(context),
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
            Toast.makeText(context, "Click", Toast.LENGTH_SHORT).show()
        }

        if (prayClick == intent.action) {

        }

    }

}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val MyOnClick = "myOnClickTag"
    val prayClick = "prayClick"

    val days = Days()
    val prays = Prays()

    val today = days.getToday()
    val todayName = days.getTodayName(today)
    val pray = prays.getPrays(today)

    saveDayPref(context, appWidgetId, todayName)
    savePrayPref(context, appWidgetId, pray)

    val dayText = loadDayPref(context, appWidgetId)
    val prayText = loadPrayPref(context, appWidgetId)

    val views = RemoteViews(context.packageName, R.layout.widget_activity)

    views.setTextViewText(R.id.appwidget_text, dayText)
    views.setTextViewText(R.id.prayTextView, prayText)


    views.setOnClickPendingIntent(R.id.counterTextView, getPendingSelfIntent(context, MyOnClick));
    views.setOnClickPendingIntent(R.id.prayLayout, getPendingSelfIntent(context, prayClick));

    appWidgetManager.updateAppWidget(appWidgetId, views)
}

fun getPendingSelfIntent(context: Context?, action: String?): PendingIntent? {
    val intent = Intent(context, WidgetActivity::class.java)
    intent.action = action
    return PendingIntent.getBroadcast(context, 0, intent, 0)
}