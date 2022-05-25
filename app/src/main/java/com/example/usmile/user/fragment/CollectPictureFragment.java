package com.example.usmile.user.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.example.usmile.R;


public class CollectPictureFragment extends Fragment implements View.OnClickListener {

    ImageView firstImageView;
    ImageView secondImageView;
    ImageView thirdImageView;
    ImageView fourthImageView;

    final int CAPTURE_FIRST_IMAGE = 1;
    final int CAPTURE_SECOND_IMAGE = 2;
    final int CAPTURE_THIRD_IMAGE = 3;
    final int CAPTURE_FOURTH_IMAGE = 4;

    final int LOAD_FIRST_IMAGE = 5;
    final int LOAD_SECOND_IMAGE = 6;
    final int LOAD_THIRD_IMAGE = 7;
    final int LOAD_FOURTH_IMAGE = 8;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firstImageView = (ImageView) view.findViewById(R.id.firstImageView);
        secondImageView = (ImageView) view.findViewById(R.id.secondImageView);
        thirdImageView = (ImageView) view.findViewById(R.id.thirdImageView);
        fourthImageView = (ImageView) view.findViewById(R.id.fourthImageView);

        firstImageView.setOnClickListener(this);
        secondImageView.setOnClickListener(this);
        thirdImageView.setOnClickListener(this);
        fourthImageView.setOnClickListener(this);
    }


    public void openCamera(int requestCode) {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, requestCode);
    }


    public void openGallery(int requestCode) {

        Intent galleryIntent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent , requestCode );
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
                    firstImageView.setImageBitmap(bp);
                    firstImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    break;
                case CAPTURE_SECOND_IMAGE:
                    bp = (Bitmap) data.getExtras().get("data");
                    secondImageView.setImageBitmap(bp);
                    secondImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    break;
                case CAPTURE_THIRD_IMAGE:
                    bp = (Bitmap) data.getExtras().get("data");
                    thirdImageView.setImageBitmap(bp);
                    thirdImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    break;
                case CAPTURE_FOURTH_IMAGE:
                    bp = (Bitmap) data.getExtras().get("data");
                    fourthImageView.setImageBitmap(bp);
                    fourthImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    break;
            }
        }
        else {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            // Get the cursor
            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            // Move to first row
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imgDecodableString = cursor.getString(columnIndex);
            cursor.close();

            switch (requestCode) {
                case LOAD_FIRST_IMAGE:
                    firstImageView.setImageBitmap(BitmapFactory
                            .decodeFile(imgDecodableString));
                    firstImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    break;
                case LOAD_SECOND_IMAGE:
                    secondImageView.setImageBitmap(BitmapFactory
                            .decodeFile(imgDecodableString));
                    secondImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    break;
                case LOAD_THIRD_IMAGE:
                    thirdImageView.setImageBitmap(BitmapFactory
                            .decodeFile(imgDecodableString));
                    thirdImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    break;
                case LOAD_FOURTH_IMAGE:
                    fourthImageView.setImageBitmap(BitmapFactory
                            .decodeFile(imgDecodableString));
                    fourthImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    break;
            }
        }




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_collect_picture, container, false);
    }



    @Override
    public void onClick(View view) {

        int order = view.getId();
        openChooseSourceOfPictureDialog(order);

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
                    case R.id.firstImageView:
                        openCamera(CAPTURE_FIRST_IMAGE);
                        break;
                    case R.id.secondImageView:
                        openCamera(CAPTURE_SECOND_IMAGE);
                        break;
                    case R.id.thirdImageView:
                        openCamera(CAPTURE_THIRD_IMAGE);
                        break;
                    case R.id.fourthImageView:
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
                    case R.id.firstImageView:
                        openGallery(LOAD_FIRST_IMAGE);
                        break;
                    case R.id.secondImageView:
                        openGallery(LOAD_SECOND_IMAGE);
                        break;
                    case R.id.thirdImageView:
                        openGallery(LOAD_THIRD_IMAGE);
                        break;
                    case R.id.fourthImageView:
                        openGallery(LOAD_FOURTH_IMAGE);
                        break;
                }

                dialog.dismiss();

            }
        });

        dialog.show();

    }

}