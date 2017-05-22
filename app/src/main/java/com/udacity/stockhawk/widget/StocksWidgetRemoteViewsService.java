package com.udacity.stockhawk.widget;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.Utility;
import com.udacity.stockhawk.data.Contract;

import timber.log.Timber;

/**
 * Created by mateusz.zak on 21.05.2017.
 */

public class StocksWidgetRemoteViewsService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {

            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(Contract.Quote.URI,
                        Contract.Quote.QUOTE_COLUMNS.toArray(new String[]{}),
                        null, null, Contract.Quote.COLUMN_SYMBOL);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION || data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.list_item_quote);
                String symbol = data.getString(Contract.Quote.POSITION_SYMBOL);
                double price = data.getFloat(Contract.Quote.POSITION_PRICE);
                double percentageChange = data.getFloat(Contract.Quote.POSITION_PERCENTAGE_CHANGE);
                String formattedPrice = Utility.formatDollar(price);
                String formattedChange = Utility.formatPercentage(percentageChange / 100);
                views.setTextViewText(R.id.symbol, symbol);
                views.setTextViewText(R.id.price, formattedPrice);
                views.setTextViewText(R.id.change, formattedChange);

                if (percentageChange < 0) {
                    Timber.d(symbol + " price and change is: " +  formattedPrice + " " + percentageChange);
                    views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                } else {
                    views.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
                }

                final Intent fillInIntent = new Intent();
                fillInIntent.putExtra(Intent.EXTRA_TEXT, symbol);
                views.setOnClickFillInIntent(R.id.stock_list_item, fillInIntent);
                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.list_item_quote);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position)) {
                    return data.getLong(Contract.Quote.POSITION_ID);
                }
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
