package com.prathamesh.videotoaudio;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;


public class Home extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    TextView TV_File_Path;
    MaterialButton BTN_Picker, BTN_Extract;
    public static final int PICK_FILE_RESULT_CODE = 1;
    Uri fileUri;
    String filePath;
    File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TV_File_Path = findViewById(R.id.TV_File_Picker);
        BTN_Picker = findViewById(R.id.BTN_Picker);
        BTN_Extract = findViewById(R.id.BTN_Extract);

        BTN_Picker.setOnClickListener(view -> {

            // taking permissions
            takePermissions();

            // creating folder
            file = getDisc();
            if (!file.exists() && !file.mkdirs()) {
                Toast.makeText(this, "Couldn't make directory try again", Toast.LENGTH_LONG).show();
            }

            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("video/*");
            chooseFile = Intent.createChooser(chooseFile, "Choose Video File");
            startActivityForResult(chooseFile, PICK_FILE_RESULT_CODE);

        });

        BTN_Extract.setOnClickListener(view -> {
            String video = TV_File_Path.getText().toString();
            String[] temp = TV_File_Path.getText().toString().split("/");
            String filename = temp[temp.length-1].substring(0,temp[temp.length-1].length()-4);
            String audio = "AudioExtractor"+filename+".mp3";
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PICK_FILE_RESULT_CODE:
                if (resultCode == -1) {
                    assert data != null;

                    fileUri = data.getData();
                    String realPath = "";
                    String wholeID = DocumentsContract.getDocumentId(fileUri);
                    // Split at colon, use second item in the array
                    String id = wholeID.split(":")[1];
                    String[] column = { MediaStore.Images.Media.DATA };
                    // where id is equal to
                    String sel = MediaStore.Images.Media._ID + "=?";
                    Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{ id }, null);
                    int columnIndex = 0;
                    if (cursor != null) {
                        columnIndex = cursor.getColumnIndex(column[0]);
                        if (cursor.moveToFirst()) {
                            realPath = cursor.getString(columnIndex);
                        }
                        cursor.close();
                    }
                    // getting file path
                    TV_File_Path.setText(realPath);
                }
        }
    }



    private File getDisc() {
        File file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
        return new File(file, "Audio Extractor");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "Permissions Denied!", Toast.LENGTH_SHORT).show();
    }


    // taking permissions
    private void takePermissions() {
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (!EasyPermissions.hasPermissions(this, permissions)) {
            EasyPermissions.requestPermissions(this, "Allow Permissions", 1, permissions);
        }
    }
}