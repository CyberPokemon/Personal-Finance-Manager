package com.neuromancers.personalfinancemanager;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


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
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Calendar;


public class HomeActivity extends AppCompatActivity {

    DatabaseHelper dbHelper;
    long currentUserId;
    Toolbar toolbar;
    TextView infotext;
    RecyclerView recyclerView;
    TransactionAdapter transactionAdapter;
    List<Transaction> transactionList = new ArrayList<>();

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

        recyclerView = findViewById(R.id.recycler_transactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        transactionList = dbHelper.getAllTransactions(currentUserId); // method must return list sorted by datetime DESC
        transactionAdapter = new TransactionAdapter(transactionList);
        recyclerView.setAdapter(transactionAdapter);


    }

    private void showAddTransactionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_add_transaction, null);
        builder.setView(view);

        EditText edtAmount = view.findViewById(R.id.edt_amount);
        Spinner spinnerType = view.findViewById(R.id.spinner_type);
        EditText edtDescription = view.findViewById(R.id.edt_description);
        EditText edtPerson = view.findViewById(R.id.edt_person);
        Spinner spinnerAccount = view.findViewById(R.id.spinner_account);
        EditText edtDateTime = view.findViewById(R.id.edt_datetime);
        Button btnSubmit = view.findViewById(R.id.btn_submit);


        List<String> accountList = new ArrayList<>();
        accountList.add("Cash");
        List<BankAccount> userAccounts = dbHelper.getBankAccounts(currentUserId);
        for (BankAccount acc : userAccounts) {
            accountList.add(acc.getBankName());
        }

        ArrayAdapter<String> accountAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, accountList);
        accountAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAccount.setAdapter(accountAdapter);


        edtDateTime.setFocusable(false);

        edtDateTime.setOnClickListener(v -> {
            Calendar calendar;
            calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    (view1, selectedYear, selectedMonth, selectedDay) -> {
                        // Set up TimePickerDialog after Date is selected
                        Calendar selectedCalendar = Calendar.getInstance();
                        selectedCalendar.set(selectedYear, selectedMonth, selectedDay);

                        // Prevent selecting future dates
                        if (selectedCalendar.after(Calendar.getInstance())) {
                            Toast.makeText(this, "Cannot select future dates", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Time picker
                        int hour = calendar.get(Calendar.HOUR_OF_DAY);
                        int minute = calendar.get(Calendar.MINUTE);

                        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                                (view2, selectedHour, selectedMinute) -> {
                                    selectedCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                                    selectedCalendar.set(Calendar.MINUTE, selectedMinute);

                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                                    String dateTime = dateFormat.format(selectedCalendar.getTime());
                                    edtDateTime.setText(dateTime);
                                }, hour, minute, true);

                        timePickerDialog.show();
                    }, year, month, day);

            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());

            datePickerDialog.show();
        });

        AlertDialog dialog = builder.create();

        btnSubmit.setOnClickListener(v -> {
            double amount = Double.parseDouble(edtAmount.getText().toString());
            String type = spinnerType.getSelectedItem().toString();
            String description = edtDescription.getText().toString();
            String person = edtPerson.getText().toString();
            String account = spinnerAccount.getSelectedItem().toString();
            String datetime = edtDateTime.getText().toString();  // Get selected datetime

            // Check if date is selected
            if (datetime.isEmpty()) {
                Toast.makeText(this, "Please select a valid date and time.", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = dbHelper.insertTransaction(currentUserId, amount, type, datetime, description, person, account);

            if (success) {
                updateChart();
                Toast.makeText(this, "Transaction success", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                transactionList.clear();
                transactionList.addAll(dbHelper.getAllTransactions(currentUserId));
                transactionAdapter.notifyDataSetChanged();

            } else {
                Toast.makeText(this, "Transaction failed", Toast.LENGTH_SHORT).show();
            }
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