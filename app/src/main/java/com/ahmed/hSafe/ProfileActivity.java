package com.ahmed.hSafe;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmed.hSafe.BluetoothConnection.Callback;
import com.ahmed.hSafe.Utilities.RecyclerAdapter;
import com.ahmed.hSafe.entities.Doctor;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private PendingIntent pendingIntent;
    private AlarmManager manager;
    private RecyclerView recyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_v2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView profileIc = findViewById(R.id.profile_info_tab_ic);
        ImageView coachIc = findViewById(R.id.coach_tab_ic);
        ImageView doctorIc = findViewById(R.id.doctor_tab_ic);
        TextView profileText = findViewById(R.id.profile_info_tab_text);
        TextView doctorText = findViewById(R.id.doctor_tab_text);
        TextView coachText = findViewById(R.id.coach_tab_text);

        int orange = Color.parseColor("#e98725");
        int black = Color.parseColor("#000000");

        //profileIc.setColorFilter(orange);
        //coachIc.setColorFilter(orange);
        doctorIc.setColorFilter(orange);
        profileText.setTextColor(black);
        doctorText.setTextColor(orange);
        //coachText.setTextColor(orange);

        ArrayList<Doctor> list;
        recyclerview = findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ProfileActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());

        list = new ArrayList<>();

        for (int i = 0; i < 1; i++) {
//            Doctor model = new Doctor("Samir Chaari","Cardiologist","SFAX");
//            list.add(model);
//            model = new Doctor("Hamdi Guermazi","Radiologist","SFAX");
//            list.add(model);
//            model = new Doctor("Hela Kallel","Neurologist","SFAX");
//            list.add(model);
            String pbAdress = "0x352b5e6c184dC9665D4aD1Fd2071197a01e49bA8";
            Doctor model = new Doctor("Walid Hbaieb", "Weight Management", "SFAX", pbAdress, "1");
            list.add(model);
            model = new Doctor("Melek Kammoun", "Athletics/Sports", "SFAX", pbAdress, "2");
            list.add(model);
            model = new Doctor("Karim Karray", "Mind-Body Fitness", "SFAX", pbAdress, "3");
            list.add(model);
            model = new Doctor("Zeineb Elleuch", "Management and Recovery", "SFAX", pbAdress, "4");
            list.add(model);
        }

        getDoctorsListFromDB(args -> {
            ArrayList<Doctor> doctors = (ArrayList<Doctor>) args[1];
            RecyclerAdapter adapter;
            String contractAddress = "0x06905967cA0C1f11C8aDb1FF17Cc7CD51DD29869";
            adapter = new RecyclerAdapter(ProfileActivity.this, doctors, contractAddress);
            recyclerview.setAdapter(adapter);
        });



        Intent alarmIntent = new Intent(this, TaskActivity.class);
        pendingIntent = PendingIntent.getActivity(this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //startAlarm();
    }

    public void startAlarm() {
        Log.d("MThesisLog", "Starting alarm...");
        manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 14400000;
        assert manager != null;
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
        //manager.cancel(pendingIntent);
    }

    private void getDoctorsListFromDB(Callback callback) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = database.child("doctors/");

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Doctor> list;
                list = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Doctor doctor = postSnapshot.getValue(Doctor.class);
                    list.add(doctor);
                }
                callback.invoke(null, list);
//                HashMap<String, Doctor> doctors = (HashMap<String, Doctor>) dataSnapshot.getValue();
//                ArrayList<Doctor> list;
//                list = new ArrayList<Doctor>(doctors.values());
//                String hello = list.get(0).getName();
//                for (int i=0; i<doctors.size();i++){
//                    Doctor doctor = doctors.
//                }
//                if (doctor != null) {
//                   // Log.d("MThesisLog", "Doctor Account : " + doctor.getName());
//                    callback.invoke(null, doctor);
//                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });
    }

}
