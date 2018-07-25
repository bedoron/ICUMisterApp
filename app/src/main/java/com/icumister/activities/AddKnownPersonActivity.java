package com.icumister.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.icumister.Constants;
import com.icumister.icumisterapp.R;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddKnownPersonActivity extends AppCompatActivity {
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int PICK_IMAGE = 2;
    private static final String TAG = "AddKnownPerson";
    private ImageView personImageView = null;
    private static String picturePath;
    private AppCompatActivity addKnownPersonActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_known_person);
        addKnownPersonActivity = this;
        personImageView = findViewById(R.id.person_image);
        if (picturePath != null) {
            personImageView.setImageBitmap(getBitmapFromFile(picturePath));
        }
    }

    public void choosePicture(View view) {
        Intent intent = new Intent()
                .setType("image/*")
                .setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    public void captureFromCamera(View vew) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermissions();
            return;
        }

        Intent capturePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (capturePictureIntent.resolveActivity(getPackageManager()) == null) {
            new AlertDialog.Builder(this)
                    .setTitle(R.string.text_camera_error)
                    .setMessage(getString(R.string.text_failed_to_caputre_picture))
                    .show();
            return;
        }

        File photoFile;
        try {
            photoFile = createImageFile();
        } catch (IOException e) {
            Log.e(TAG, "Failed to save the picture to disk");
            return;
        }

        Uri photoURI = FileProvider.getUriForFile(this, "com.icumister.fileprovider", photoFile);
        capturePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        picturePath = photoFile.getAbsolutePath();
        startActivityForResult(capturePictureIntent, REQUEST_TAKE_PHOTO);
    }

    public void sendDetailsToServer(View view) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Bitmap picture = getBitmapFromFile(picturePath);
            personImageView.setImageBitmap(picture);
            return;
        }

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            try {
                if (data == null || data.getData() == null) {
                    Log.e(TAG, "Data is null after picking an image");
                    return;
                }

                InputStream inputStream = this.getContentResolver().openInputStream(data.getData());
                Bitmap picture = BitmapFactory.decodeStream(inputStream);
                personImageView.setImageBitmap(picture);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constants.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                Log.i(TAG, "Got access to storage!!!");
                break;
        }
    }

    private void requestStoragePermissions() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(this)
                    .setTitle("Storage permission")
                    .setMessage("We need to access storage to store pictures of you and other known people")
                    .setCancelable(false)
                    .setNegativeButton("No way", null)
                    .setPositiveButton("Sure", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(addKnownPersonActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }
    }

    @NotNull
    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File publicImageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", publicImageDir);
        picturePath = image.getAbsolutePath();
        return image;
    }

    private Bitmap getBitmapFromFile(@NotNull String absolutePath) {
        File absoluteFile = new File(absolutePath);
        if (!absoluteFile.exists()) {
            Log.e(TAG, "File " + absolutePath + " doesn't exist");
            return null;
        }

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.outWidth = 500;
        bitmapOptions.outHeight = 500;

        return BitmapFactory.decodeFile(absolutePath, bitmapOptions);
    }
}
