package com.levo.permissions;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    Button btCamera;
    ImageView ivPhoto;

    static final int REQUEST_CAMERA = 1;
    static final int REQUEST_TAKE_PICTURE = 2;
    String currentPathImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btCamera = findViewById(R.id.btCamera);
        ivPhoto = findViewById(R.id.ivPhoto);

        btCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePicture();
            }
        });
    }

    private void takePicture() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // validate the camera is available
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // check permission
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions();
            } else {

                File photoFile = null;
                try {
                    photoFile = createImage();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (photoFile != null) {
                    Uri photoUri = FileProvider.getUriForFile(this, "com.levo.permissions", photoFile);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(cameraIntent, REQUEST_TAKE_PICTURE);
                }
            }
        }
    }

    private File createImage() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timestamp + "_";

        // put a directory
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // create a temporal file
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        currentPathImage = image.getAbsolutePath();
        return image;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.READ_CONTACTS},
                REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                takePicture();
            }
            if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // code here
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_TAKE_PICTURE && resultCode == RESULT_OK)
        {
            // Bitmap photo = (Bitmap) data.getExtras().get("data");
            // ivPhoto.setImageBitmap(photo);
            Glide.with(this).load(currentPathImage).into(ivPhoto);
        }
    }
}
