package com.neuromancers.personalfinancemanager;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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


        FloatingActionButton fab = findViewById(R.id.fab_add_transaction);
        fab.setOnClickListener(v -> showAddTransactionDialog());


    }

    private void showAddTransactionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_transaction, null);
        builder.setView(view);

        EditText edtAmount = view.findViewById(R.id.edt_amount);
        Spinner spinnerType = view.findViewById(R.id.spinner_type);
        EditText edtDescription = view.findViewById(R.id.edt_description);
        EditText edtPerson = view.findViewById(R.id.edt_person);
        EditText edtAccount = view.findViewById(R.id.edt_account);
        Button btnSubmit = view.findViewById(R.id.btn_submit);

        AlertDialog dialog = builder.create();

        btnSubmit.setOnClickListener(v -> {
            // collect values and insert into DB
            double amount = Double.parseDouble(edtAmount.getText().toString());
            String type = spinnerType.getSelectedItem().toString();
            String description = edtDescription.getText().toString();
            String person = edtPerson.getText().toString();
            String account = edtAccount.getText().toString();
            String datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

            boolean success = dbHelper.insertTransaction(currentUserId, amount, type, datetime, description, person, account);

            if (success) {
                updateChart();
                Toast.makeText(this, "Transaction success", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(this, "Transaction failed", Toast.LENGTH_SHORT).show();
            }

            updateChart(); // refresh pie chart
            dialog.dismiss();
        });

        dialog.show();
    }

    private void updateChart() {
        PieChart pieChart = findViewById(R.id.pieChart);

        User user = dbHelper.getUser(currentUserId);
        List<BankAccount> accounts = dbHelper.getBankAccounts(currentUserId);

        double total = user.getCash();
        for (BankAccount acc : accounts) {
            total += acc.balance;
        }

        List<PieEntry> entries = new ArrayList<>();
        for (BankAccount acc : accounts) {
            entries.add(new PieEntry((float) acc.balance, acc.bankName));
        }

        if (user.getCash() > 0) {
            entries.add(new PieEntry((float) user.getCash(), "Cash in Hand"));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setValueTextSize(14f);
        dataSet.setValueTextColor(Color.BLACK);

        // Formatter for amount + percentage
        double finalTotal = total; // effectively final for lambda
        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                float percent = (float) ((value / finalTotal) * 100);
                return String.format("₹%.0f (%.1f%%)", value, percent);
            }
        });

        PieData data = new PieData(dataSet);
        pieChart.setData(data);

        pieChart.setCenterText("Total: ₹" + (int) total);
        pieChart.setCenterTextSize(18f);
        pieChart.setCenterTextColor(Color.DKGRAY);

        pieChart.setUsePercentValues(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(45f);
        pieChart.setTransparentCircleRadius(50f);
        pieChart.animateY(1000, Easing.EaseInOutQuad);

        Legend legend = pieChart.getLegend();
        legend.setTextSize(14f);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setFormSize(14f);
        legend.setXEntrySpace(20f);
        legend.setYEntrySpace(15f);
        legend.setWordWrapEnabled(true);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);

        pieChart.getDescription().setEnabled(false); // remove default label
        pieChart.invalidate(); // refresh chart
    }


}