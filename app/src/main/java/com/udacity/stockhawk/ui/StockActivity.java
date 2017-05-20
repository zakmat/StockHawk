package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mateusz.zak on 17.05.2017.
 */

class StockActivity extends AppCompatActivity {
    @BindView(R.id.tv_symbol)
    TextView mSymbol;
    @BindView(R.id.lc_history)
    LineChart mChart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        String symbol = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        ButterKnife.bind(this);
        mSymbol.setText(symbol);
        Uri stockHistoryUri = Contract.Quote.URI.buildUpon().appendPath(symbol).build();
        String [] projection = {Contract.Quote.COLUMN_HISTORY};
        Cursor cursor = getContentResolver().query(stockHistoryUri, projection, null, null,null);
        if (cursor != null && cursor.getCount() != 0) {
            setChartData(cursor);
        }

    }

    private void setChartData(Cursor cursor) {
        cursor.moveToFirst();
        String history = cursor.getString(0);
        String historyArray[] = history.split("\n");
        Collections.reverse(Arrays.asList(historyArray));

        List<Entry> entries = new ArrayList<Entry>();
        for (String historicalPrice: historyArray) {
            String dataPiece[] = historicalPrice.split(",");
            entries.add(new Entry(Float.valueOf(dataPiece[0]), Float.valueOf(dataPiece[1])));

        }
        //TODO: query data from QuoteSyncJob
        LineDataSet dataSet = new LineDataSet(entries, "Price");

        LineData lineData = new LineData(dataSet);
        mChart.setData(lineData);
        mChart.invalidate();
    }
}
