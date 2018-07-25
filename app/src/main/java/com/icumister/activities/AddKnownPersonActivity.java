package com.icumister.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.widget.EditText;
import android.widget.ImageView;

import com.icumister.Constants;
import com.icumister.icumisterapp.R;
import com.icumister.network.CallAPI;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class AddKnownPersonActivity extends AppCompatActivity {
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int PICK_IMAGE = 2;
    private static final String TAG = "AddKnownPerson";
    private static final DialogInterface.OnClickListener DO_NOTHIN_LISTENER = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // Do nothing
        }
    };

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

    public byte[] drawableToBytes(String boundary, Drawable drawable) throws IOException {
        if (!(drawable instanceof BitmapDrawable)) {
            Log.w(TAG, "Picture is not BitmapDrawable");
            return null;
        }

        BitmapDrawable bitmapDrawable = ((BitmapDrawable) drawable);
        Bitmap bitmap = bitmapDrawable.getBitmap();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stream.write(("--" + boundary + "\r\n").getBytes());
        stream.write("Content-Type: image/jpeg\r\n".getBytes());
        stream.write("Content-Disposition: form-data; name=\"file\";filename=\"known_person.jpeg\"\r\n".getBytes());
        stream.write("\r\n".getBytes());
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        stream.write("\r\n".getBytes());
        stream.write(("--" + boundary + "--\r\n").getBytes());

        return stream.toByteArray();
    }

    public void sendDetailsToServer(View view) {
        String name = ((EditText) findViewById(R.id.your_name)).getText().toString();

        AsyncTask asyncTask;
        Map<String, List<String>> headers;

        asyncTask = new CallAPI(Constants.BACKEND_URL_PERSON_CREATE).execute(("name=" + name).getBytes());
        try {
            headers = (Map<String, List<String>>) asyncTask.get(60, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
            showMessage("Network Error", "Failed to send add person");
            return;
        }

        if (headers == null) {
            showMessage("Network Error", "Failed to send add person");
            return;
        }

        String personId = headers.get("X-Person-ID").get(0);

        Map<String, String> reqProps = new HashMap<>();
        reqProps.put("Connection", "Keep-Alive");
        reqProps.put("Cache-Control", "no-cache");
        String boundary = UUID.randomUUID().toString();
        reqProps.put("Content-Type", "multipart/form-data;boundary=" + boundary);
        try {
            byte[] data = drawableToBytes(boundary, personImageView.getDrawable());

            if (data == null) {
                return;
            }

            asyncTask = new CallAPI(Constants.BACKEND_URL_FACE_CREATE + "?id=" + personId, reqProps).execute(data);
            headers = (Map<String, List<String>>) asyncTask.get(60, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException | IOException e) {
            e.printStackTrace();
            showMessage("Error", "Failed to send add person");
            return;
        }

        if (headers == null || (headers.get(null) != null && headers.get(null).get(0).equalsIgnoreCase("HTTP/1.1 500 INTERNAL SERVER ERROR"))) {
            showMessage("Network Error", "Failed to send add person");
            return;
        }
        showMessage("Info", "Person was successfully added");
    }

    private void showMessage(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Close", DO_NOTHIN_LISTENER)
                .show();
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
