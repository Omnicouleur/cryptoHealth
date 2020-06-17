package com.ahmed.hSafe;

import android.app.AlarmManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmed.hSafe.BluetoothConnection.Callback;
import com.ahmed.hSafe.Utilities.RecyclerAdapter;
import com.ahmed.hSafe.entities.CryptoAccount;
import com.ahmed.hSafe.entities.Doctor;
import com.ahmed.hSafe.entities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ProfileActivity extends AppCompatActivity {

    private AlarmManager manager;
    private RecyclerView recyclerview;

    View doctorTab;
    View coachTab;
    View personalInfoTab;
    View personalInfoContainer;
    View recyclerViewContainer;
    ImageView profileIc;
    TextView profileText;
    ImageView coachIc;
    TextView coachText;
    ImageView doctorIc;
    TextView doctorText;
    TextView userNameText;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_v2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        userNameText = findViewById(R.id.user_name_text);
        recyclerViewContainer = findViewById(R.id.recyclerview);
        personalInfoContainer = findViewById(R.id.personal_information_container);
        personalInfoTab = findViewById(R.id.personal_information_tab);
        profileIc = findViewById(R.id.profile_info_tab_ic);
        profileText = findViewById(R.id.profile_info_tab_text);

        coachTab = findViewById(R.id.coach_tab);
        coachIc = findViewById(R.id.coach_tab_ic);
        coachText = findViewById(R.id.coach_tab_text);

        doctorTab = findViewById(R.id.doctor_tab);
        doctorIc = findViewById(R.id.doctor_tab_ic);
        doctorText = findViewById(R.id.doctor_tab_text);

        // colors
        int orange = Color.parseColor("#e98725");
        int black = Color.parseColor("#000000");

        // Personal information is selected by default
        profileIc.setColorFilter(orange);
        profileText.setTextColor(orange);

        // Change Tabs :
        personalInfoTab.setOnClickListener(v -> {
            turnOffAllTabs(black);
            profileIc.setColorFilter(orange);
            profileText.setTextColor(orange);
            recyclerViewContainer.setVisibility(View.GONE);
            personalInfoContainer.setVisibility(View.VISIBLE);
        });
        doctorTab.setOnClickListener(v -> {
            turnOffAllTabs(black);
            doctorIc.setColorFilter(orange);
            doctorText.setTextColor(orange);
            loadRecyclerView("doctor");
            recyclerViewContainer.setVisibility(View.VISIBLE);
            personalInfoContainer.setVisibility(View.GONE);
        });
        coachTab.setOnClickListener(v -> {
            turnOffAllTabs(black);
            coachIc.setColorFilter(orange);
            coachText.setTextColor(orange);
            loadRecyclerView("coach");
            recyclerViewContainer.setVisibility(View.VISIBLE);
            personalInfoContainer.setVisibility(View.GONE);
        });

        //coachIc.setColorFilter(orange);
//        doctorIc.setColorFilter(orange);
//        doctorText.setTextColor(orange);
        //coachText.setTextColor(orange);

        recyclerview = findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(ProfileActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerview.setLayoutManager(layoutManager);
        recyclerview.setItemAnimator(new DefaultItemAnimator());

        getUserInfoFromDB();
//        for (int i = 0; i < 1; i++) {
////            Doctor model = new Doctor("Samir Chaari","Cardiologist","SFAX");
////            list.add(model);
////            model = new Doctor("Hamdi Guermazi","Radiologist","SFAX");
////            list.add(model);
////            model = new Doctor("Hela Kallel","Neurologist","SFAX");
////            list.add(model);
//            String pbAdress = "0x352b5e6c184dC9665D4aD1Fd2071197a01e49bA8";
//            Doctor model = new Doctor("Walid Hbaieb", "Weight Management", "SFAX", pbAdress, "1");
//            list.add(model);
//            model = new Doctor("Melek Kammoun", "Athletics/Sports", "SFAX", pbAdress, "2");
//            list.add(model);
//            model = new Doctor("Karim Karray", "Mind-Body Fitness", "SFAX", pbAdress, "3");
//            list.add(model);
//            model = new Doctor("Zeineb Elleuch", "Management and Recovery", "SFAX", pbAdress, "4");
//            list.add(model);
//        }

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    void loadRecyclerView(String job) {
        getDoctorsListFromDB(args -> {

            getContractAddressFromDB(args1 -> {
                ArrayList<Doctor> doctors = (ArrayList<Doctor>) args[1];
                doctors = (ArrayList<Doctor>) doctors.stream().filter(doctor -> doctor.getJob().equals(job)).collect(Collectors.toList());

                RecyclerAdapter adapter;
                //String contractAddress = "0x06905967cA0C1f11C8aDb1FF17Cc7CD51DD29869";
                adapter = new RecyclerAdapter(ProfileActivity.this, doctors, args1[1].toString());
                recyclerview.setAdapter(adapter);
            });
        });
    }

    void turnOffAllTabs(int color) {
        profileIc.setColorFilter(color);
        profileText.setTextColor(color);
        coachIc.setColorFilter(color);
        doctorIc.setColorFilter(color);
        doctorText.setTextColor(color);
        coachText.setTextColor(color);
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

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });
    }

    private void getContractAddressFromDB(Callback callback) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = database.child("cryptoAccounts/");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        myRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CryptoAccount cryptoAccount = dataSnapshot.getValue(CryptoAccount.class);
                if (cryptoAccount != null) {
                    Log.d("MThesisLog", "Account :  contract @ from DB : " + cryptoAccount.contractAddress);
                    callback.invoke(null, cryptoAccount.contractAddress);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });
    }

    private void getUserInfoFromDB() {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference myRef = database.child("users/");
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        myRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User userDB = dataSnapshot.getValue(User.class);
                //TODO
                userNameText.setText(userDB.name);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });
    }

}
