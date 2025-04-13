package com.neuromancers.personalfinancemanager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AssetsActivity extends AppCompatActivity {

    private DatabaseHelper dbHelper;
    private long currentUserId;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assets);

        dbHelper = new DatabaseHelper(this);
        currentUserId = getIntent().getLongExtra("USER_ID", -1);

        TextView title = findViewById(R.id.txt_assets);
        title.setText("Manage FDs / Loans / Other Assets");

        Button btnCreateFD = findViewById(R.id.btn_create_fd);
        btnCreateFD.setOnClickListener(v -> showCreateFDDialog());

        recyclerView = findViewById(R.id.fd_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<FDModel> fdList = dbHelper.getFDsForUser(currentUserId);
        FDAdapter adapter = new FDAdapter(fdList);
        recyclerView.setAdapter(adapter);

    }

    private void showCreateFDDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_create_fd, null);
        builder.setView(view);

        EditText edtPrincipal = view.findViewById(R.id.edt_principal);
        EditText edtRate = view.findViewById(R.id.edt_rate);
        EditText edtTime = view.findViewById(R.id.edt_time);
        EditText edtStartDate = view.findViewById(R.id.edt_start_date);
        TextView txtResult = view.findViewById(R.id.txt_fd_result);
        Button btnCalculate = view.findViewById(R.id.btn_calculate_fd);
        Button btnSave = view.findViewById(R.id.btn_save_fd);

        btnSave.setVisibility(View.GONE); // Hide initially

        AlertDialog dialog = builder.create(); // Create the dialog here

        Calendar calendar = Calendar.getInstance();
        edtStartDate.setOnClickListener(v -> {
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dpd = new DatePickerDialog(this, (datePicker, y, m, d) -> {
                edtStartDate.setText(String.format(Locale.getDefault(), "%04d-%02d-%02d", y, m + 1, d));
            }, year, month, day);

            dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
            dpd.show();
        });

        btnCalculate.setOnClickListener(v -> {
            try {
                double principal = Double.parseDouble(edtPrincipal.getText().toString());
                double rate = Double.parseDouble(edtRate.getText().toString());
                int timeMonths = Integer.parseInt(edtTime.getText().toString());

                double timeYears = timeMonths / 12.0;
                double interest = (principal * rate * timeYears) / 100.0;
                double maturityAmount = principal + interest;

                // Calculate maturity date
                Calendar maturityCalendar = Calendar.getInstance();
                String[] dateParts = edtStartDate.getText().toString().split("-");
                int year = Integer.parseInt(dateParts[0]);
                int month = Integer.parseInt(dateParts[1]) - 1;
                int day = Integer.parseInt(dateParts[2]);

                maturityCalendar.set(year, month, day);
                maturityCalendar.add(Calendar.MONTH, timeMonths);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String maturityDate = sdf.format(maturityCalendar.getTime());

                txtResult.setText("Maturity Amount: ₹" + String.format("%.2f", maturityAmount)
                        + "\nInterest Earned: ₹" + String.format("%.2f", interest)
                        + "\nMaturity Date: " + maturityDate);

                btnSave.setVisibility(View.VISIBLE);

                // Store values for saving
                final double finalPrincipal = principal;
                final double finalRate = rate;
                final int finalTimeMonths = timeMonths;
                final String finalStartDate = edtStartDate.getText().toString();
                final String finalMaturityDate = maturityDate;
                final double finalInterest = interest;
                final double finalMaturityAmount = maturityAmount;

                btnSave.setOnClickListener(view1 -> {
                    if (dbHelper != null) {
                        boolean success = dbHelper.insertFD(
                                currentUserId,
                                finalPrincipal,
                                finalRate,
                                finalTimeMonths,
                                finalStartDate,
                                finalMaturityDate,
                                finalInterest,
                                finalMaturityAmount
                        );

                        if (success) {
                            Toast.makeText(this, "FD saved successfully!", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            // Refresh RecyclerView
                            List<FDModel> updatedList = dbHelper.getFDsForUser(currentUserId);
                            recyclerView.setAdapter(new FDAdapter(updatedList));

                        } else {
                            Toast.makeText(this, "Failed to save FD", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } catch (Exception e) {
                txtResult.setText("Please fill all fields correctly.");
            }
        });

        dialog.show(); // show after all setup
    }
}
