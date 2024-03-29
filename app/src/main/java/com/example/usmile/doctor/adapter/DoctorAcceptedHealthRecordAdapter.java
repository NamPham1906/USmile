package com.example.usmile.doctor.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.usmile.R;
import com.example.usmile.account.AccountFactory;
import com.example.usmile.account.models.Doctor;
import com.example.usmile.doctor.fragment.DoctorDetailReceivedHealthRecordFragment;
import com.example.usmile.doctor.fragment.DoctorDetailWaitingHealthRecordFragment;
import com.example.usmile.doctor.fragment.DoctorGiveSpecificAdviceFragment;
import com.example.usmile.doctor.fragment.ReceivedHealthRecordListFragment;
import com.example.usmile.doctor.fragment.WaitingHealthRecordListFragment;
import com.example.usmile.user.models.HealthRecord;
import com.example.usmile.utilities.Constants;
import com.example.usmile.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;


public class DoctorAcceptedHealthRecordAdapter extends RecyclerView.Adapter<DoctorAcceptedHealthRecordAdapter.DoctorAcceptedHealthRecordViewHolder>{

    private List<HealthRecord> healthRecords;
    private Context context;
    PreferenceManager preferenceManager;
    Doctor doctor;

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Doctor getDoctor(){
        return this.doctor;
    }

    public DoctorAcceptedHealthRecordAdapter(List<HealthRecord> healthRecords) {
        this.healthRecords = healthRecords;
    }

    public void setData(List<HealthRecord> list) {
        this.healthRecords = list;
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public DoctorAcceptedHealthRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_accepted_health_record, parent, false);
        context = parent.getContext();
        return new DoctorAcceptedHealthRecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorAcceptedHealthRecordViewHolder holder, int position) {
        preferenceManager = new PreferenceManager(this.context);
        HealthRecord item = healthRecords.get(position);

        if (item == null)
            return;

        holder.patientMessage.setText(item.getDescription());
        holder.patientSendDate.setText(item.getSentDate());

        userInfor(holder, item);


        // from account id -> load user avatar and name from firestore
//        holder.patientAvatar.setImageResource(R.drawable.example_avatar);
//        holder.patientName.setText("Nam 7749");

        if (item.isAccepted() == true && !item.getDentistId().equals("")) {
            holder.statusTextView.setText("Đã tư vấn");
            holder.statusTextView.setTextColor(ContextCompat.getColor(context, R.color.primary_green));
        } else if (!item.isAccepted() && !item.getDentistId().equals("")) {
            holder.statusTextView.setText("Chờ tư vấn");
            holder.checkDetailHealthRecordButton.setText("Tư vấn");
            holder.archiveButton.setEnabled(false);
            holder.archiveButton.setBackground(ContextCompat.getDrawable(context, R.drawable.small_gray_button_shape));
            holder.statusTextView.setTextColor(ContextCompat.getColor(context, R.color.primary_yellow));
        }

    }
    private void userInfor(@NonNull DoctorAcceptedHealthRecordViewHolder holder, HealthRecord hr) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection(Constants.KEY_COLLECTION_ACCOUNT)
                .document(hr.getAccountId());
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        String userName = doc.getString(Constants.KEY_ACCOUNT_FULL_NAME);
                        String userAva = doc.getString(Constants.KEY_ACCOUNT_AVATAR);
                        try
                        {
                            Bitmap bitmap = decodeImage(userAva);
                            holder.patientAvatar.setImageBitmap(getRoundBitmap(bitmap));
                        }
                        catch (Exception e)
                        {
                            Log.e("ERR", e.getMessage());
                        }

                        holder.patientName.setText(userName);
                        notifyDataSetChanged();
                    } else {
                        Log.d("DEN-ID", "No such document");
                    }
                } else {
                    Log.d("DEN-ID", "get failed with ", task.getException());
                }
            }
        });
    }

    private Bitmap decodeImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }

    public Bitmap getRoundBitmap(Bitmap bitmap) {

        int min = Math.min(bitmap.getWidth(), bitmap.getHeight());

        Bitmap bitmapRounded = Bitmap.createBitmap(min, min, bitmap.getConfig());

        Canvas canvas = new Canvas(bitmapRounded);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawRoundRect((new RectF(0.0f, 0.0f, min, min)), min/8, min/8, paint);

        return bitmapRounded;
    }

    @Override
    public int getItemCount() {
        if (healthRecords == null)
            return 0;
        return healthRecords.size();
    }

    private void showToast(String msg) {
        Toast.makeText(context.getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public class DoctorAcceptedHealthRecordViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView patientAvatar;
        TextView patientName;
        TextView patientSendDate;
        TextView statusTextView;
        TextView patientMessage;

        TextView checkDetailHealthRecordButton;
        TextView archiveButton;

        AlertDialog cancelDialog;
        AlertDialog.Builder dialogBuilder;



        public DoctorAcceptedHealthRecordViewHolder(@NonNull View itemView) {
            super(itemView);

            patientAvatar = (ImageView) itemView.findViewById(R.id.patientAvatar);
            patientName = (TextView) itemView.findViewById(R.id.patientName);
            patientSendDate = (TextView) itemView.findViewById(R.id.patientSendDate);
            statusTextView = (TextView) itemView.findViewById(R.id.statusTextView);
            patientMessage = (TextView) itemView.findViewById(R.id.patientMessage);



            checkDetailHealthRecordButton = (TextView) itemView.findViewById(R.id.checkDetailHealthRecordButton);
            archiveButton = (TextView) itemView.findViewById(R.id.archiveButton);


            checkDetailHealthRecordButton.setOnClickListener(this);
            archiveButton.setOnClickListener(this);

        }

        private void openNewFragment(View view, Fragment nextFragment) {
            AppCompatActivity activity = (AppCompatActivity) view.getContext();
            activity.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.mainFragmentHolder, nextFragment).addToBackStack(null).commit();
        }

        @Override
        public void onClick(View view) {
            int id = view.getId();
            int position = getLayoutPosition();
            HealthRecord item = healthRecords.get(position);

            Fragment fragment = null;

            switch (id) {
                case R.id.checkDetailHealthRecordButton:
                    if (item.isAccepted() == true && !item.getDentistId().equals("")) {
                        preferenceManager.putString(Constants.KEY_HEALTH_RECORD_ID, item.getId());
                        preferenceManager.putString(Constants.KEY_GET_USER_ID, item.getAccountId());
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(AccountFactory.DOCTORSTRING, doctor);
                        fragment = new DoctorDetailReceivedHealthRecordFragment();
                        fragment.setArguments(bundle);
                        openNewFragment(view, fragment);
                    } else if (!item.isAccepted() && !item.getDentistId().equals("")) {
                        preferenceManager.putString(Constants.KEY_HEALTH_RECORD_ID, item.getId());
                        Bundle bundle = new Bundle();
                        bundle.putSerializable(AccountFactory.DOCTORSTRING, doctor);

                        fragment = new DoctorGiveSpecificAdviceFragment();
                        fragment.setArguments(bundle);
                        openNewFragment(view, fragment);

                    }

                    break;
                case R.id.archiveButton:
                    preferenceManager.putString(Constants.KEY_HEALTH_RECORD_ID, item.getId());
                    preferenceManager.putListString(Constants.KEY_HEALTH_RECORD_DELETED, item.getDeleted());
                    try{
                        createArchiveDialog(view.getContext());
                    }
                    catch (Exception e)
                    {
                        Log.e("ARCH DIALOG",e.getMessage());
                    }
                    break;
            }
        }

        private void archiveHealthRecord() {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference documentReference
                    = db.collection(Constants.KEY_COLLECTION_HEALTH_RECORD)
                    .document(preferenceManager.getString(Constants.KEY_HEALTH_RECORD_ID));

            List<String> del = preferenceManager.getListString(Constants.KEY_HEALTH_RECORD_DELETED);
            del.add(doctor.getId());
            HashMap<String, Object> updates = new HashMap<>();
            updates.put(Constants.KEY_HEALTH_RECORD_DELETED, del);

            documentReference.update(updates)
                    .addOnSuccessListener(unused -> {
                        showToast("Updated successfully");

                    })
                    .addOnFailureListener(e -> {
                        Log.e("update health record", e.getMessage());
                    });
        }

        public void createArchiveDialog(Context context) {
            dialogBuilder = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            final View cancelPopup = inflater.inflate( R.layout.popup_doctor_archive_health_record, null );

            Button yesBtn = (Button) cancelPopup.findViewById(R.id.btnQuit);
            Button noBtn = (Button) cancelPopup.findViewById(R.id.btnCancel);
            dialogBuilder.setView(cancelPopup);
            cancelDialog = dialogBuilder.create();
            cancelDialog.show();
            cancelDialog.setCanceledOnTouchOutside(false);
            cancelDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            yesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //dimiss dialog
                    archiveHealthRecord();
                    cancelDialog.dismiss();
                    //reload fragment
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(AccountFactory.DOCTORSTRING, doctor);

                    Fragment fragment = new ReceivedHealthRecordListFragment();
                    fragment.setArguments(bundle);

                    openNewFragment(view, fragment);


                }
            });

            noBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //dimiss dialog
                    cancelDialog.dismiss();

                }
            });
        }


    }
}
