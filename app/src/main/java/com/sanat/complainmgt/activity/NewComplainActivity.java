package com.sanat.complainmgt.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sanat.complainmgt.R;
import com.sanat.complainmgt.model.ComplainModel;
import com.sanat.complainmgt.model.ResponseLogin;
import com.sanat.complainmgt.model.ResponseSaveComplain;
import com.sanat.complainmgt.model.UserModel;
import com.sanat.complainmgt.network.APIs;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewComplainActivity extends AppCompatActivity {
    private static final String TAG = "NewComplainActivity";

    private ActionBar toolbar;
    Button btnCamera, btnSaveComplain;
    ImageView img;
    TextInputLayout etLocation, etTitle, etDescription;

    String currentPhotoPath = "";
    private Bitmap bitmap;
    byte[] bt;
    private String image_front_encode = "";
    String fileName = "";
    public  static final int PERMISSIONS_MULTIPLE_REQUEST = 123;
    private static final int CAMERA_REQUEST = 10;
    private int GALARY_IMAGE_REQUEST = 11;
    APIs apiURL = new APIs();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_complain);
        setupToolbar();
        initWidget();
    }

    private void initWidget() {
        Log.i(TAG, "initWidget: ");
        btnCamera = findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndroidVersion();
            }
        });

        img = findViewById(R.id.img);

        etLocation = findViewById(R.id.etLocation);
        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);

        btnSaveComplain = findViewById(R.id.btnSaveComplain);
        btnSaveComplain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ComplainModel model = new ComplainModel();
                model.setUserID(1);
                model.setTitle(etTitle.getEditText().getText().toString().trim());
                model.setLocation(etLocation.getEditText().getText().toString().trim());
                model.setDescription(etDescription.getEditText().getText().toString().trim());
                model.setImageName("img123.png");

                saveComplain(model);
            }
        });
    }

    private void setupToolbar() {
        Log.i(TAG, "setupToolbar: ");
        toolbar = getSupportActionBar();
        toolbar.setTitle("New Complain");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveComplain(ComplainModel model){

        String URL = apiURL.saveComplainURL();
        Log.i(TAG, "login: " + URL);
        final ProgressDialog dialog = ProgressDialog.show(this, "", "Please wait...", false, false);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("UserID", model.getUserID());
            jsonBody.put("Title", model.getTitle());
            jsonBody.put("Description", model.getDescription());
            jsonBody.put("Location", model.getLocation());
            jsonBody.put("ImageName", model.getImageName());
        } catch (Exception exception) {

        }
        final String requestBody = jsonBody.toString();
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "onResponse: " + response);
                GsonBuilder builder = new GsonBuilder();
                Gson gson = builder.create();
                ResponseSaveComplain res = gson.fromJson(response, ResponseSaveComplain.class);
                dialog.dismiss();
                if (res.getSuccess()) {
                    Toast.makeText(NewComplainActivity.this, "Save Success. " + res.getMsg(), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(NewComplainActivity.this, "Save Failed. " + res.getMsg(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
                Log.i(TAG, "onErrorResponse: " + error.getMessage());
            }
        }){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                    return null;
                }
            }

            @Override
            protected com.android.volley.Response<String> parseNetworkResponse(NetworkResponse response) {
                String responseString = "";
                if (response != null) {
                    responseString = String.valueOf(response.statusCode);
                }
                //return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                return super.parseNetworkResponse(response);
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
        request.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });
    }


    //////////////////***************** Image Section ********************////////////////////////

    private void checkAndroidVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //checkPermission();
            requestCameraStoragePermission();
        } else {
            //Toast.makeText(getApplicationContext(), "--------GO------------", Toast.LENGTH_SHORT).show();
            callCameraApp();
        }
    }

    private void callCameraApp() {
        selectImage();
    }

    private void requestCameraStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale
                (NewComplainActivity.this, Manifest.permission.CAMERA) ||
                ActivityCompat.shouldShowRequestPermissionRationale
                        (NewComplainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)){

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("To use this feature you need to grant these permissions. \n\n1. Camera permission for taking the new photo. \n2. Storage permission for PDF and photo upload")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(
                                        new String[]{Manifest.permission
                                                .CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                                        PERMISSIONS_MULTIPLE_REQUEST);
                            }
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        new String[]{Manifest.permission
                                .CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSIONS_MULTIPLE_REQUEST);
            }
        }
    }

    private void selectImage() {
        Log.i(TAG, "selectImage: calling");
        final CharSequence[] options = {
                "Take Photo",
                "Choose Image from Gallery",
                "Cancel"
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(
                NewComplainActivity.this);
        builder.setTitle("Please select photo.");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    openCamera();
                } else if (options[item].equals("Choose Image from Gallery")) {
                    showFileChooser();
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });

        builder.show();

    }


    private void openCamera(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = getImageFile();
            } catch (IOException ex) {
                Log.i(TAG, "Error");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.sanat.complainmgt.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST);
            }
        }
    }

    private File getImageFile() throws IOException{
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;

    }


    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GALARY_IMAGE_REQUEST);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_MULTIPLE_REQUEST:
                if (grantResults.length > 0) {
                    boolean cameraPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean readExternalFile = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (cameraPermission && readExternalFile) {
                        callCameraApp();
                    } else {
                        requestCameraStoragePermission();
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST){

            Log.i(TAG, "onActivityResult: Camera Section");
            try{
                if(currentPhotoPath != null || !currentPhotoPath.equalsIgnoreCase("")){
                    try {
                        cameraImageActionResultNew();
                    } catch (Exception ex) {
                        Log.i(TAG, "onActivityResult: Camera opening or capture problem");
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (requestCode == GALARY_IMAGE_REQUEST){
            Log.i(TAG, "onActivityResult: Galary Section");
            if(data != null) {
                galaryImageActionResultNew(data);
            }
        } else {
            Log.i(TAG, "onActivityResult: Dont support");
        }
    }

    private void galaryImageActionResultNew(Intent data){
        final Uri path = data.getData();

        try {
            if (path != null) {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                imageEncode(bitmap);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        fileName = path.getLastPathSegment();
                        img.setImageBitmap(bitmap);
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cameraImageActionResultNew(){
        try {
            final Bitmap bitmap = BitmapFactory.decodeFile(getRightAngleImage(currentPhotoPath));

            if(bitmap == null) {
                // Toast.makeText(this, "Photo not taken.", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "cameraImageActionResultNew: Photo not taken.");
            } else {
                imageEncode(bitmap);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        img.setImageBitmap(bitmap);
                    }
                });

            }

        } catch (Exception ex){
            Log.i(TAG, "cameraImageActionResultNew: ");
        }

    }

    private String getRightAngleImage(String photoPath) {

        try {
            ExifInterface ei = new ExifInterface(photoPath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int degree = 0;

            switch (orientation) {
                case ExifInterface.ORIENTATION_NORMAL:
                    degree = 0;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                case ExifInterface.ORIENTATION_UNDEFINED:
                    degree = 0;
                    break;
                default:
                    degree = 90;
            }

            return rotateImage(degree,photoPath);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return photoPath;
    }

    private String rotateImage(int degree, String imagePath){

        if(degree<=0){
            return imagePath;
        }
        try{
            Bitmap b= BitmapFactory.decodeFile(imagePath);

            Matrix matrix = new Matrix();
            if(b.getWidth()>b.getHeight()){
                matrix.setRotate(degree);
                b = Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(),
                        matrix, true);
            }

            FileOutputStream fOut = new FileOutputStream(imagePath);
            String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1);
            String imageType = imageName.substring(imageName.lastIndexOf(".") + 1);

            FileOutputStream out = new FileOutputStream(imagePath);
            if (imageType.equalsIgnoreCase("png")) {
                b.compress(Bitmap.CompressFormat.PNG, 100, out);
            }else if (imageType.equalsIgnoreCase("jpeg")|| imageType.equalsIgnoreCase("jpg")) {
                b.compress(Bitmap.CompressFormat.JPEG, 100, out);
            }
            fOut.flush();
            fOut.close();

            b.recycle();
        }catch (Exception e){
            e.printStackTrace();
        }
        return imagePath;
    }

    private void imageEncode(Bitmap bitmap){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        bt = bytes.toByteArray();
        image_front_encode = Base64.encodeToString(bt, Base64.DEFAULT);
    }
}