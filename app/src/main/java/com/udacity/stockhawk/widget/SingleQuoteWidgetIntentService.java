package com.udacity.stockhawk.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.Utility;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.ui.MainActivity;

import timber.log.Timber;

/**
 * Created by mateusz.zak on 21.05.2017.
 */

class SingleQuoteWidgetIntentService extends IntentService {
    private static final String[] QUOTE_COLUMNS = {
            Contract.Quote.COLUMN_PRICE,
            Contract.Quote.COLUMN_PERCENTAGE_CHANGE,
    };

    private static final int INDEX_PRICE = 0;
    private static final int INDEX_CHANGE = 1;

    public SingleQuoteWidgetIntentService() {
        super("SingleQuoteWidgetIntentService");
    }


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Timber.d("onHandleIntent called");
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                SingleQuoteWidgetProvider.class));

        //TODO: use configured symbol instead of hardcoded one
        String symbol = getString(R.string.default_stocks_yahoo);
        Uri stockUri = Contract.Quote.makeUriForStock(symbol);
        Cursor data = getContentResolver().query(stockUri, QUOTE_COLUMNS, null,
                null, null);
        if (data == null) {
            return;
        }
        if (!data.moveToFirst()) {
            data.close();
            return;
        }

        // Extract the quote data from the Cursor
        double price = data.getFloat(INDEX_PRICE);
        String formattedPrice = Utility.formatDollar(price);
        double change = data.getFloat(INDEX_CHANGE);
        String formattedChange = Utility.formatPercentage(change/100);
        data.close();

        // Perform this loop procedure for each widget
        for (int appWidgetId : appWidgetIds) {
            int layoutId = R.layout.widget_quote;
            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            views.setTextViewText(R.id.widget_symbol, symbol);
            views.setTextViewText(R.id.widget_price, formattedPrice);
            views.setTextViewText(R.id.widget_change, formattedChange);

            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
