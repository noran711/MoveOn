package com.examplehjhk.moveon;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.examplehjhk.moveon.data.DBHelper;
import com.examplehjhk.moveon.domain.User;

import java.util.ArrayList;

public class TherapistDashboardActivity extends AppCompatActivity {

    private User currentUser;
    private DBHelper db;
    private final ArrayList<String> patientUsernames = new ArrayList<>();
    private final ArrayList<String> patientLabels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_therapist_dashboard);

        if (getSupportActionBar() != null) getSupportActionBar().hide();

        currentUser = (User) getIntent().getSerializableExtra("user");
        if (currentUser == null || !"Therapeut".equalsIgnoreCase(currentUser.role)) {
            startActivity(new Intent(this, Login.class));
            finish();
            return;
        }

        db = new DBHelper(this);

        ListView listPatients = findViewById(R.id.listPatients);
        Button btnLogout = findViewById(R.id.btnLogoutTherapist);

        loadPatients();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                patientLabels
        );
        listPatients.setAdapter(adapter);

        listPatients.setOnItemClickListener((parent, view, position, id) -> {
            String username = patientUsernames.get(position);
            Intent i = new Intent(this, PatientDetailActivity.class);
            i.putExtra("user", currentUser);
            i.putExtra("patient_username", username);
            startActivity(i);
        });

        btnLogout.setOnClickListener(v -> {
            Intent i = new Intent(this, Login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
        });
    }

    private void loadPatients() {
        patientUsernames.clear();
        patientLabels.clear();

        Cursor c = db.getAllPatients();
        if (c != null) {
            while (c.moveToNext()) {
                String u = c.getString(c.getColumnIndexOrThrow("username"));
                String fn = c.getString(c.getColumnIndexOrThrow("first_name"));
                String ln = c.getString(c.getColumnIndexOrThrow("last_name"));

                patientUsernames.add(u);
                patientLabels.add((ln + " " + fn).trim() + "  (" + u + ")");
            }
            c.close();
        }
    }
}
