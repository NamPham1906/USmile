package com.example.usmile.doctor.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usmile.R;
import com.example.usmile.account.AccountFactory;
import com.example.usmile.account.models.Doctor;
import com.example.usmile.account.models.User;
import com.example.usmile.doctor.DoctorMainActivity;
import com.example.usmile.user.UserMainActivity;
import com.example.usmile.user.fragment.SettingAccountInfoFragment;
import com.example.usmile.user.fragment.SettingChangePasswordFragment;
import com.example.usmile.utilities.Constants;
import com.example.usmile.utilities.PreferenceManager;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;

public class SettingDoctorAccountInfoFragment extends Fragment implements View.OnClickListener {
    TextView changePasswordTextView;
    TextView confirmButton;
    TextView cancelButton;

    //ImageView avatarImageView;

    RoundedImageView avatarImageView;
    EditText userNameEditText;
    EditText fullNameEditText;
    EditText dobEditText;
    EditText genderEditText;
    EditText accountEditText;
    EditText workPlaceEditText;

    PreferenceManager preferenceManager;

    String encodedImage = "";

    Doctor doctor;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting_doctor_account_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.view = view;
        preferenceManager = new PreferenceManager(getContext());

        getBundle();

        bindingView(view);

        loadAccountDetails();

        setListeners();
    }

    private void setListeners() {
        confirmButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        changePasswordTextView.setOnClickListener(this);
        avatarImageView.setOnClickListener(this);
    }

    private void getBundle() {
        Bundle bundle = getArguments();

        if (bundle != null)
            doctor = (Doctor) bundle.getSerializable(AccountFactory.DOCTORSTRING);
    }

    private void bindingView(@NonNull View view) {
        changePasswordTextView = (TextView) view.findViewById(R.id.docChangePasswordTextView);
        confirmButton = (TextView) view.findViewById(R.id.docConfirmButton);
        cancelButton = (TextView) view.findViewById(R.id.docCancelButton);

        //avatarImageView = (ImageView) view.findViewById(R.id.avatarImageView);
        avatarImageView = (RoundedImageView) view.findViewById(R.id.docAvatarImageView);


        fullNameEditText = (EditText) view.findViewById(R.id.doctorNameEditText) ;
        dobEditText = (EditText) view.findViewById(R.id.doctorDOBEditText);
        genderEditText = (EditText) view.findViewById(R.id.doctorGenderEditText);
        accountEditText = (EditText) view.findViewById(R.id.doctorAccountEditText);
        workPlaceEditText = (EditText) view.findViewById(R.id.workPlaceText);

        dobEditText.addTextChangedListener(textWatcher);
        fullNameEditText.addTextChangedListener(textWatcher);
        genderEditText.addTextChangedListener(textWatcher);
        accountEditText.addTextChangedListener(textWatcher);
    }

    private Bitmap decodeImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }

    private void loadAccountDetails() {
        Bitmap bitmap = decodeImage(doctor.getAvatar());
        avatarImageView.setImageBitmap(bitmap);


        fullNameEditText.setText(doctor.getFullName());
        dobEditText.setText(doctor.getDOB());
        genderEditText.setText(doctor.getGender());
        accountEditText.setText(doctor.getAccount());

        workPlaceEditText.setText(doctor.getWorkPlace());

        fullNameEditText.addTextChangedListener(textWatcher);
        dobEditText.addTextChangedListener(datetimeTextWatcher);
        genderEditText.addTextChangedListener(textWatcher);
        accountEditText.addTextChangedListener(textWatcher);
        workPlaceEditText.addTextChangedListener(textWatcher);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {

        int id = view.getId();

        Fragment nextFragment = null;

        switch (id) {
            case R.id.docCancelButton:
                Bundle newbundle = new Bundle();
                newbundle.putSerializable(AccountFactory.DOCTORSTRING, doctor);
                Fragment newFragment = new SettingDoctorAccountInfoFragment();
                newFragment.setArguments(newbundle);
                openNewFragment(newFragment);
                break;
            case R.id.docConfirmButton:
                updateInfo();
                break;
            case R.id.docChangePasswordTextView:
                Bundle bundle = new Bundle();
                bundle.putString("TYPE", AccountFactory.DOCTORSTRING);

                bundle.putSerializable(AccountFactory.DOCTORSTRING, doctor);

                nextFragment = new SettingChangePasswordFragment();
                nextFragment.setArguments(bundle);
                break;
            case R.id.docAvatarImageView:
                selectImage();
                break;
        }

        if (id == R.id.docChangePasswordTextView) {
            openNewFragment(nextFragment);
        }

    }

    private void enableButton(View view){
        confirmButton.setVisibility(view.VISIBLE);
        cancelButton.setVisibility(view.VISIBLE);
    }

    private TextWatcher datetimeTextWatcher = new TextWatcher() {
        boolean nullString = false;
        private String current = "";
        private String ddmmyyyy = "________";
        private Calendar cal = Calendar.getInstance();

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            if(s.toString().equals("") || s.toString().equals(current))
            {
//                Log.d("224", "empty");
                nullString = true;

            }
            else
                nullString = false;
        }

        public void afterTextChanged(Editable s) {
            if(!nullString)
            {
                enableButton(view);
                Log.d("236", "enable button");
            }
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (!s.toString().equals(current)) {
                String clean = s.toString().replaceAll("[^\\d.]", "");
                String cleanC = current.replaceAll("[^\\d.]", "");

                int cl = clean.length();
                int sel = cl;
                for (int i = 2; i <= cl && i < 6; i += 2) {
                    sel++;
                }
                //Fix for pressing delete next to a forward slash
                if (clean.equals(cleanC)) sel--;

                if (clean.length() < 8){
                    clean = clean + ddmmyyyy.substring(clean.length());
                }else{
                    //This part makes sure that when we finish entering numbers
                    //the date is correct, fixing it otherwise
                    int day  = Integer.parseInt(clean.substring(0,2));
                    int mon  = Integer.parseInt(clean.substring(2,4));
                    int year = Integer.parseInt(clean.substring(4,8));

                    if(mon > 12) mon = 12;
                    cal.set(Calendar.MONTH, mon-1);

                    int todayYear = Calendar.getInstance().get(Calendar.YEAR);
                    year = (year<1900)?1900:(year>todayYear)?todayYear:year;
                    cal.set(Calendar.YEAR, year);
                    // ^ first set year for the line below to work correctly
                    //with leap years - otherwise, date e.g. 29/02/2012
                    //would be automatically corrected to 28/02/2012

                    day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                    clean = String.format("%02d%02d%02d",day, mon, year);
                }

                clean = String.format("%s/%s/%s", clean.substring(0, 2),
                        clean.substring(2, 4),
                        clean.substring(4, 8));

                sel = sel < 0 ? 0 : sel;
                current = clean;
                Log.d("duplicate?", String.valueOf(start) + " " + String.valueOf(before) +  " "  +  String.valueOf(count));
                dobEditText.setText(current);
                dobEditText.setSelection(sel < current.length() ? sel : current.length());

            }
        }




    };

    private TextWatcher textWatcher = new TextWatcher() {
        boolean nullString = false;
        public void afterTextChanged(Editable s) {
            if(!nullString)
                enableButton(view);
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            Log.d("560", s.toString());
            if (s.toString().equals(""))
            {
                nullString = true;
            }
            else {
                nullString = false;
            }
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {

        }
    };

    private boolean isFillEditText(EditText editText) {
        String input = editText.getText().toString().trim();

        if (input.isEmpty()) {
            editText.setError("Không được để trống");
            return false;
        } else {
            editText.setError(null);
            return true;
        }
    }
    private boolean isFillDoBEditText(EditText editText) {
        String input = editText.getText().toString().trim();
        input = input.replaceAll("[^\\d.]", "");
        Log.d("316", input + " " + String.valueOf(input.length()));
        if (input.length() < 8) {
            editText.setError("Chưa đủ ngày/tháng/năm");
            return false;
        } else {
            editText.setError(null);
            return true;
        }
    }

    private boolean isCompleted(){
        if (!isFillEditText(fullNameEditText))
            return false;
        if (!isFillDoBEditText(dobEditText))
            return false;
        if (!isFillEditText(genderEditText))
            return false;
        if (!isFillEditText(accountEditText))
            return false;
        if (!isFillEditText(workPlaceEditText))
            return false;
        return true;
    }

    private void updateInfo() {
        if(!isCompleted())
            return;

        String fullname = fullNameEditText.getText().toString();
        String dob = dobEditText.getText().toString();
        String gender = genderEditText.getText().toString();
        String accountStr = accountEditText.getText().toString();
        String workPlace = workPlaceEditText.getText().toString();

        doctor.setFullName(fullname);
        doctor.setDOB(dob);
        doctor.setGender(gender);
        doctor.setAccount(accountStr);
        doctor.setWorkPlace(workPlace);


        FirebaseFirestore database = FirebaseFirestore.getInstance();
        DocumentReference documentReference
                = database.collection(Constants.KEY_COLLECTION_ACCOUNT)
                .document(doctor.getId());

        HashMap<String, Object> updates = new HashMap<>();

        if (!encodedImage.equals("")) {
            updates.put(Constants.KEY_ACCOUNT_AVATAR, encodedImage);
            doctor.setAvatar(encodedImage);
        }

        updates.put(Constants.KEY_ACCOUNT_FULL_NAME, fullname);
        updates.put(Constants.KEY_ACCOUNT_DOB, dob);
        updates.put(Constants.KEY_ACCOUNT_GENDER, gender);
        updates.put(Constants.KEY_ACCOUNT_ACCOUNT, accountStr);
        updates.put(Constants.KEY_ACCOUNT_WORKPLACE, workPlace);

        documentReference.update(updates)
                .addOnSuccessListener(unused -> {

                    if (!encodedImage.equals(""))
                        preferenceManager.putString(Constants.KEY_ACCOUNT_AVATAR, encodedImage);

                    preferenceManager.putString(Constants.KEY_ACCOUNT_FULL_NAME, fullname);
                    preferenceManager.putString(Constants.KEY_ACCOUNT_DOB, dob);
                    preferenceManager.putString(Constants.KEY_ACCOUNT_GENDER, gender);
                    preferenceManager.putString(Constants.KEY_ACCOUNT_ACCOUNT, accountStr);
                    preferenceManager.putString(Constants.KEY_ACCOUNT_WORKPLACE, workPlace);

                    showToast("Updated successfully");

                    Bundle bundle= new Bundle();
                    bundle.putSerializable(AccountFactory.DOCTORSTRING, doctor);
                    Fragment nextFragment = new SettingDoctorAccountInfoFragment();
                    nextFragment.setArguments(bundle);
                    openNewFragment(nextFragment);

                })
                .addOnFailureListener(e -> {
                    showToast("Unable to update");
                });
    }

    private void selectImage() {
        // open gallery
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);

        // handle everything after picking
        pickImage.launch(intent);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {

                            InputStream inputStream = getContext().getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            avatarImageView.setImageBitmap(bitmap);
                            encodedImage = encodeImage(bitmap);
                            enableButton(view);

                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();

        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void openNewFragment(Fragment nextFragment) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(((ViewGroup)getView().getParent()).getId(), nextFragment, "findThisFragment")
                .addToBackStack(null)
                .commit();
    }

    private void showToast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }
}