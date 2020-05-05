package com.ahmed.hSafe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.ahmed.hSafe.entities.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EnquiryActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private static final String[] cities = new String[]{"Sfax","Tozeur","Nabeul","Tunis","Gabes",
                                                        "Monastir","Mahdia","Bizerte","Sousse","Kef",
                                                        "Beja","Siliana","Tatouin","Sidi Bouzid","Kebili",
                                                        "Gafsa","Manouba","Ben Arous","Jendouba","Ariana",
                                                        "Kairouan","Zaghouen","Mednine","Kasserine"
    };

    RadioButton radioButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquiry);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        AutoCompleteTextView autoCompleteTextView = findViewById(R.id.act1);
        autoCompleteTextView.setThreshold(1);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, cities);
        autoCompleteTextView.setAdapter(adapter);

        autoCompleteTextView.setOnClickListener((v) -> autoCompleteTextView.showDropDown());

        RadioGroup radioGroup = findViewById(R.id.radioGroupGender);
        EditText editTextFullName = findViewById(R.id.editTextFullName);
        EditText editTextAge = findViewById(R.id.editTextAge);
        Button btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(v -> {

            String city = autoCompleteTextView.getText().toString();
            Log.d("MThesisLog", "City : " + city);
            int selectedId = radioGroup.getCheckedRadioButtonId();

            // find the radiobutton by returned id
            radioButton =  findViewById(selectedId);
            String gender = radioButton.getText().toString();
            Log.d("MThesisLog", "Gender : " + gender);

            int age = Integer.parseInt(editTextAge.getText().toString());
            Log.d("MThesisLog", "Age : " + age);

            String fullName = editTextFullName.getText().toString();
            Log.d("MThesisLog", "FullName : " + fullName);

            FirebaseUser user = mAuth.getCurrentUser();
            assert user != null;
            Log.d("MThesisLog", "user : " + user.getDisplayName() + user.getEmail());

            User mohamed = new User(fullName,gender,age,city);
            //User mohamed = new User("Mohamed Trabelsi","Male",29,"Pensylvania");
            mDatabase.child("users").child(user.getUid()).setValue(mohamed).addOnCompleteListener(this, task -> {
                Intent intent = new Intent(EnquiryActivity.this, CreateEthAccountActivity.class);
                startActivity(intent);
            });

                    });





    }
}
