package com.ssproduction.shashank.network.Fragments;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ssproduction.shashank.network.R;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileBottomSheet extends BottomSheetDialogFragment {

    private RelativeLayout selectProfile,clickNewPhoto, addFrame, viewProfile, editProfile;

    private static final int GALLERY_PIC = 1;
    private ProgressDialog dialog;
    private DatabaseReference mDatabase;
    private StorageReference mImageStorage;
    private String mCurrentUser;

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {


        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }

        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog mDialog, int style) {
        super.setupDialog(mDialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_profile_bottom_sheet, null);
        mDialog.setContentView(contentView);

        dialog = new ProgressDialog(getActivity());
        mImageStorage = FirebaseStorage.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser);
        mDatabase.keepSynced(true);

        selectProfile = (RelativeLayout) mDialog.findViewById(R.id.bottom_sheet_select_profile);
        selectProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT PROFILE IMAGE"), GALLERY_PIC);
            }
        });

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if( behavior != null && behavior instanceof BottomSheetBehavior ) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PIC && resultCode == Activity.RESULT_OK){

            Uri imageUri = data.getData();

            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(getContext(), this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == Activity.RESULT_OK){

                dialog.setTitle("Updating Profile");
                dialog.setMessage("please wait while profile is updating");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                Uri resultUri = result.getUri();

                final File thumb_filePath = new File(resultUri.getPath());

                String user_uid = mCurrentUser;

                Bitmap thumb_bitmap = new Compressor(getContext())
                        .setMaxWidth(300)
                        .setMaxHeight(480)
                        .setQuality(75)
                        .compressToBitmap(thumb_filePath);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 75, baos);
                final byte[] thumb_byte = baos.toByteArray();

                final StorageReference filepath = mImageStorage.child("userImage").child(user_uid + ".jpg");
                final StorageReference thumb_filepath = mImageStorage.child("userImage").child("thumb")
                        .child(user_uid + ".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        if (task.isSuccessful()){

                            final String download_url = task.getResult().getDownloadUrl().toString();

                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumbTask) {

                                    String thumb_downloadUrl = thumbTask.getResult().getDownloadUrl().toString();

                                    if (thumbTask.isSuccessful()){

                                        Map update_hashMap = new HashMap();
                                        update_hashMap.put("user_image", download_url);
                                        update_hashMap.put("user_thumbImage", thumb_downloadUrl);

                                        mDatabase.updateChildren(update_hashMap).addOnCompleteListener
                                                (new OnCompleteListener() {
                                                    @Override
                                                    public void onComplete(@NonNull Task task) {


                                                        if (task.isSuccessful()){
                                                            dialog.dismiss();
                                                            Toast.makeText(getActivity(), "success", Toast.LENGTH_SHORT).show();

                                                        }

                                                    }
                                                });
                                    }
                                    else {
                                        dialog.hide();
                                        Toast.makeText(getActivity(), "error uploading", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }


                    }
                });
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){

                Exception error = result.getError();

            }
        }
    }
}
