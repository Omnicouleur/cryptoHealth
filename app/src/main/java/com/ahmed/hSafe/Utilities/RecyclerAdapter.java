package com.ahmed.hSafe.Utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmed.hSafe.R;
import com.ahmed.hSafe.SmartContract.AddDoctorToSC;
import com.ahmed.hSafe.entities.Doctor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    Context context;
    String contractAddress;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private ArrayList<Doctor> list;

    public RecyclerAdapter(Context context, ArrayList<Doctor> list, String contractAddress) {
        this.context = context;
        this.list = list;
        this.contractAddress = contractAddress;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Doctor model = list.get(position);
//        holder.ps_image.setImageResource(model.getCity());
        holder.doctorName.setText(model.getName());
        holder.doctorSpec.setText(model.getSpecialty());
        holder.addDoctorBtn.setOnClickListener(v -> {
            new AddDoctorToSC(contractAddress, model.getPublicAddress(), context).execute();

            //Store the doctor/patient Association in DB
            FirebaseUser user = mAuth.getCurrentUser();

            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/doctor_patients/" + model.getUid() + "/" + user.getUid(), "true");
            childUpdates.put("/patient_doctors/" + user.getUid() + "/" + model.getUid(), "true");

            mDatabase.updateChildren(childUpdates);

        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView doctorName;
        TextView doctorSpec;
        ImageView addDoctorBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorName = itemView.findViewById(R.id.doctor_item_name);
            doctorSpec = itemView.findViewById(R.id.doctor_item_spec);
            addDoctorBtn = itemView.findViewById(R.id.add_doctor_btn);

        }
    }
}
