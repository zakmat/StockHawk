package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarLineChartBase;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.Utility;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by mateusz.zak on 17.05.2017.
 */

public class StockActivity extends AppCompatActivity {
    @BindView(R.id.tv_symbol)
    TextView mSymbol;
    @BindView(R.id.lc_history)
    LineChart mChart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock);
        ButterKnife.bind(this);
        Intent incomingIntent = getIntent();
        if (incomingIntent.hasExtra(Intent.EXTRA_TEXT)) {
            String symbol = incomingIntent.getStringExtra(Intent.EXTRA_TEXT);
            mSymbol.setText(symbol);
            Uri stockHistoryUri = Contract.Quote.URI.buildUpon().appendPath(symbol).build();
            String[] projection = {Contract.Quote.COLUMN_HISTORY};
            Cursor cursor = getContentResolver().query(stockHistoryUri, projection, null, null, null);

            if (cursor != null && cursor.getCount() != 0) {
                setChartData(cursor);
            }
        }
    }

    private void setChartData(Cursor cursor) {
        cursor.moveToFirst();
        String history = cursor.getString(0);
        String historyArray[] = history.split("\n");
        Collections.reverse(Arrays.asList(historyArray));

        List<Entry> entries = new ArrayList<Entry>();
        for (String historicalPrice : historyArray) {
            String dataPiece[] = historicalPrice.split(",");
            entries.add(new Entry(Float.valueOf(dataPiece[0]), Float.valueOf(dataPiece[1])));
            Timber.d("Entry x: " + dataPiece[0] + " y: " + dataPiece[1]);

        }
        LineDataSet dataSet = new LineDataSet(entries, getResources().getString(R.string.chart_label_price));

        dataSet.setDrawCircles(false);

        LineData lineData = new LineData(dataSet);
        lineData.setValueTextSize(10f);
        lineData.setDrawValues(true);
        lineData.setValueTextColor(Color.WHITE);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return Utility.formatDate((long) value);
            }
        });

        mChart.getAxisLeft().setDrawLabels(false);
        mChart.getAxisRight().setTextColor(Color.WHITE);
        mChart.getLegend().setEnabled(false);
        mChart.setData(lineData);
        mChart.setDescription(null);
        mChart.invalidate();
    }
}
