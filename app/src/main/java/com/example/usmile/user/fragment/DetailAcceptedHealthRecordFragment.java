package com.example.usmile.user.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.usmile.R;
import com.example.usmile.user.models.HealthRecord;
import com.example.usmile.utilities.Constants;
import com.example.usmile.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class DetailAcceptedHealthRecordFragment extends Fragment {

    final int CAPTURE_FIRST_IMAGE = 1;
    final int CAPTURE_SECOND_IMAGE = 2;
    final int CAPTURE_THIRD_IMAGE = 3;
    final int CAPTURE_FOURTH_IMAGE = 4;

    final int LOAD_FIRST_IMAGE = 5;
    final int LOAD_SECOND_IMAGE = 6;
    final int LOAD_THIRD_IMAGE = 7;
    final int LOAD_FOURTH_IMAGE = 8;

    private TextView recordTimeTextView;
    private TextView doctorFullNameTextView;
    private TextView doctorWorkPlaceTextView;
    private TextView patientMessageTextView;

    ImageView firstImageView;
    ImageView secondImageView;
    ImageView thirdImageView;
    ImageView fourthImageView;

    CircleImageView doctorImage;

    TextView firstDetailAdvice;
    TextView secondDetailAdvice;
    TextView thirdDetailAdvice;
    TextView fourthDetailAdvice;
    TextView summaryAdviceTextView;

    String encodeImage1 = "";
    String encodeImage2 = "";
    String encodeImage3 = "";
    String encodeImage4 = "";

    PreferenceManager preferenceManager;


    public DetailAcceptedHealthRecordFragment() {

    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        preferenceManager = new PreferenceManager(getContext());

        recordTimeTextView = (TextView) view.findViewById(R.id.recordTimeTextView);
        doctorFullNameTextView = (TextView) view.findViewById(R.id.doctorFullNameTextView);
        doctorWorkPlaceTextView = (TextView) view.findViewById(R.id.doctorWorkPlaceTextView);
        patientMessageTextView = (TextView) view.findViewById(R.id.patientMessageTextView);

        firstDetailAdvice = (TextView) view.findViewById(R.id.firstDetailAdvice);
        secondDetailAdvice = (TextView) view.findViewById(R.id.secondDetailAdvice);
        thirdDetailAdvice = (TextView) view.findViewById(R.id.thirdDetailAdvice);
        fourthDetailAdvice = (TextView) view.findViewById(R.id.fourthDetailAdvice);
        summaryAdviceTextView = (TextView) view.findViewById(R.id.summaryAdviceTextView);

        firstImageView = (ImageView) view.findViewById(R.id.firstPicture);
        secondImageView = (ImageView) view.findViewById(R.id.secondPicture);
        thirdImageView = (ImageView) view.findViewById(R.id.thirdPicture);
        fourthImageView = (ImageView) view.findViewById(R.id.fourthPicture);

        doctorImage = (CircleImageView) view.findViewById(R.id.doctorImage);

        loadHealthRecordDetails();

    }

    private void getDentistInfo(String dentistId)
    {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference documentReference = db.collection(Constants.KEY_COLLECTION_ACCOUNT)
                .document(dentistId);
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot doc = task.getResult();
                    if (doc.exists()) {
                        preferenceManager.putString(Constants.KEY_GET_DENTIST_NAME,
                                doc.getString(Constants.KEY_ACCOUNT_FULL_NAME));
                        preferenceManager.putString(Constants.KEY_GET_DENTIST_WORKPLACE,
                                doc.getString(Constants.KEY_DENTIST_WORKPLACE));
//                        String d = doc.getString(Constants.KEY_ACCOUNT_AVATAR);
//                        Log.d("143",d);
                        preferenceManager.putString(Constants.KEY_GET_DENTIST_AVATAR,
                                doc.getString(Constants.KEY_ACCOUNT_AVATAR));

                    } else {
                        Log.d("DEN-ID", "No such document");
                    }
                } else {
                    Log.d("DEN-ID", "get failed with ", task.getException());
                }
            }
        });

    }

    private void loadHealthRecordDetails() {
        String healthRecordId = preferenceManager.getString(Constants.KEY_HEALTH_RECORD_ID);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(Constants.KEY_COLLECTION_HEALTH_RECORD)
                .whereEqualTo(Constants.KEY_HEALTH_RECORD_ID, healthRecordId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(Task<QuerySnapshot> task) {
                        DocumentSnapshot doc = task.getResult().getDocuments().get(0);

                        List<String> healthPictures = (ArrayList) doc.get(Constants.KEY_HEALTH_RECORD_PICTURES);
                        String description = doc.getString(Constants.KEY_HEALTH_RECORD_DESCRIPTION);
                        String sendDate = doc.getString(Constants.KEY_HEALTH_RECORD_DATE);
                        String dentistID = doc.getString(Constants.KEY_HEALTH_RECORD_DENTIST_ID);
                        getDentistInfo(dentistID);
                        String dentistName = preferenceManager.getString(Constants.KEY_GET_DENTIST_NAME);
                        String dentistWorkPlace = preferenceManager.getString(Constants.KEY_GET_DENTIST_WORKPLACE);
                        String dentistImg = preferenceManager.getString(Constants.KEY_GET_DENTIST_AVATAR);
//                        Log.d("177", dentistImg);
                        doctorFullNameTextView.setText("Bác sĩ " + dentistName);
                        doctorWorkPlaceTextView.setText("Phòng khám nha khoa " + dentistWorkPlace);
                        if(dentistImg != null)
                        {
                            Bitmap bm = decodeImage(dentistImg);
                            doctorImage.setImageBitmap(bm);
                        }
                        recordTimeTextView.setText("Hồ sơ ngày " + sendDate);
                        patientMessageTextView.setText(description);

                        encodeImage1 = healthPictures.get(0);
                        encodeImage2 = healthPictures.get(1);
                        encodeImage3 = healthPictures.get(2);
                        encodeImage4 = healthPictures.get(3);

                        firstImageView.setImageBitmap(getRoundBitmap(decodeImage(encodeImage1)));
                        secondImageView.setImageBitmap(getRoundBitmap(decodeImage(encodeImage2)));
                        thirdImageView.setImageBitmap(getRoundBitmap(decodeImage(encodeImage3)));
                        fourthImageView.setImageBitmap(getRoundBitmap(decodeImage(encodeImage4)));


                        firstImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        secondImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        thirdImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        fourthImageView.setScaleType(ImageView.ScaleType.FIT_XY);

                        firstImageView.setBackgroundResource(0);
                        secondImageView.setBackgroundResource(0);
                        thirdImageView.setBackgroundResource(0);
                        fourthImageView.setBackgroundResource(0);


                        List<String> advices = (ArrayList) doc.get(Constants.KEY_HEALTH_RECORD_ADVICES);

                        if(advices.get(0).equals(""))
                            firstDetailAdvice.setText("Không có tư vấn hình ảnh");
                        else
                            firstDetailAdvice.setText(advices.get(0));

                        if(advices.get(1).equals(""))
                            secondDetailAdvice.setText("Không có tư vấn hình ảnh này");
                        else
                            secondDetailAdvice.setText(advices.get(1));

                        if(advices.get(2).equals(""))
                            thirdDetailAdvice.setText("Không có tư vấn hình ảnh này");
                        else
                            thirdDetailAdvice.setText(advices.get(2));

                        if(advices.get(3).equals(""))
                            fourthDetailAdvice.setText("Không có tư vấn hình ảnh này");
                        else
                            fourthDetailAdvice.setText(advices.get(3));

                        summaryAdviceTextView.setText(advices.get(4));


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_accepted_health_record, container, false);
    }

    private void openNewFragment(View view, Fragment nextFragment) {
        AppCompatActivity activity = (AppCompatActivity) view.getContext();
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.mainFragmentHolder, nextFragment).addToBackStack(null).commit();
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

    private Bitmap decodeImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        return bitmap;
    }

    private String encodeImage(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();

        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public void openCamera(int requestCode) {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, requestCode);
    }


    public void openGallery(int requestCode) {

        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, requestCode);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // for capture
        if (requestCode < 5) {
            Bitmap bp;

            switch (requestCode) {
                case CAPTURE_FIRST_IMAGE:
                    bp = (Bitmap) data.getExtras().get("data");
                    encodeImage1 = encodeImage(bp);
                    firstImageView.setImageBitmap(bp);
                    firstImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    break;
                case CAPTURE_SECOND_IMAGE:
                    bp = (Bitmap) data.getExtras().get("data");
                    encodeImage2 = encodeImage(bp);
                    secondImageView.setImageBitmap(bp);
                    secondImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    break;
                case CAPTURE_THIRD_IMAGE:
                    bp = (Bitmap) data.getExtras().get("data");
                    encodeImage3 = encodeImage(bp);
                    thirdImageView.setImageBitmap(bp);
                    thirdImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    break;
                case CAPTURE_FOURTH_IMAGE:
                    bp = (Bitmap) data.getExtras().get("data");
                    encodeImage4 = encodeImage(bp);
                    fourthImageView.setImageBitmap(bp);
                    fourthImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    break;
            }
        }
        else {
            Uri selectedImage = data.getData();
            Bitmap bp = null;
            try {
                bp = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
            } catch (IOException e) {
                Log.i("GALERY", "Some exception " + e);
            }

            switch (requestCode) {
                case LOAD_FIRST_IMAGE:
                    encodeImage1 = encodeImage(bp);
                    firstImageView.setImageBitmap(bp);
                    firstImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    break;
                case LOAD_SECOND_IMAGE:
                    encodeImage2 = encodeImage(bp);
                    secondImageView.setImageBitmap(bp);
                    secondImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    break;
                case LOAD_THIRD_IMAGE:
                    encodeImage3 = encodeImage(bp);
                    thirdImageView.setImageBitmap(bp);
                    thirdImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    break;
                case LOAD_FOURTH_IMAGE:
                    encodeImage4 = encodeImage(bp);
                    fourthImageView.setImageBitmap(bp);
                    fourthImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    break;
            }
        }
    }

    private void openChooseSourceOfPictureDialog(int order) {

        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.choose_source_of_picture_layout);


        Window window = dialog.getWindow();

        if (window == null)
            return;

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.BOTTOM;

        dialog.setCancelable(true);

        // binding here
        ImageView cameraImageView = dialog.findViewById(R.id.fromCamImageView);
        ImageView galleryImageView = dialog.findViewById(R.id.fromGalleryImageView);

        cameraImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (order) {
                    case R.id.firstPicture:
                        openCamera(CAPTURE_FIRST_IMAGE);
                        break;
                    case R.id.secondPicture:
                        openCamera(CAPTURE_SECOND_IMAGE);
                        break;
                    case R.id.thirdPicture:
                        openCamera(CAPTURE_THIRD_IMAGE);
                        break;
                    case R.id.fourthPicture:
                        openCamera(CAPTURE_FOURTH_IMAGE);
                        break;
                }

                dialog.dismiss();
            }
        });

        galleryImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                switch (order) {
                    case R.id.firstPicture:
                        openGallery(LOAD_FIRST_IMAGE);
                        break;
                    case R.id.secondPicture:
                        openGallery(LOAD_SECOND_IMAGE);
                        break;
                    case R.id.thirdPicture:
                        openGallery(LOAD_THIRD_IMAGE);
                        break;
                    case R.id.fourthPicture:
                        openGallery(LOAD_FOURTH_IMAGE);
                        break;
                }

                dialog.dismiss();

            }
        });

        dialog.show();

    }

}