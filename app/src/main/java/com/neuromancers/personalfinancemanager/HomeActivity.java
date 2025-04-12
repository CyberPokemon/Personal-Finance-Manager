package com.neuromancers.personalfinancemanager;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    long currentUserId;
    Toolbar toolbar;
    TextView infotext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        infotext=findViewById(R.id.infotext);
        infotext.setText("Money Distribution");
        dbHelper = new DatabaseHelper(this);
        currentUserId = dbHelper.getLastInsertedUserId();
        User user = dbHelper.getUser(currentUserId);
        List<BankAccount> accounts = dbHelper.getBankAccounts(currentUserId);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Welcome, " + user.getName());
        }

        PieChart pieChart = findViewById(R.id.pieChart);

        List<PieEntry> entries = new ArrayList<>();
        double total = user.getCash();

        for (BankAccount acc : accounts) {
            total += acc.balance;
        }
        final double finalTotal = total;

        for (BankAccount acc : accounts) {
            entries.add(new PieEntry((float) acc.balance, acc.bankName));
        }

        // Add cash in hand
        if (user.getCash() > 0) {
            entries.add(new PieEntry((float) user.getCash(), "Cash in Hand"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.BLACK);

        // Custom formatter: Show "₹amount (xx%)"
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                float percent = (value / (float) finalTotal) * 100;
                return String.format("₹%.0f (%.1f%%)", value, percent);
            }
        });

        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        // Center text
        pieChart.setCenterText("Total: ₹" + (int) total);
        pieChart.setCenterTextSize(18f);
        pieChart.setCenterTextColor(Color.DKGRAY);

        // Chart appearance
        pieChart.setUsePercentValues(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(45f);
        pieChart.setTransparentCircleRadius(50f);

        // Animate
        pieChart.animateY(1000, Easing.EaseInOutQuad);

        // Legend customization
        Legend legend = pieChart.getLegend();
        legend.setTextSize(14f);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setFormSize(14f);
        legend.setXEntrySpace(20f);
        legend.setYEntrySpace(15f);
        legend.setWordWrapEnabled(true);  // Useful if names are long
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        pieChart.getDescription().setEnabled(false);
        pieChart.invalidate(); // refresh


    }
}