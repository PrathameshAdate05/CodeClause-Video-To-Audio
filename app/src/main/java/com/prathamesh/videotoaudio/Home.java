package com.prathamesh.videotoaudio;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

public class Home extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    TextView TV_File_Path;
    MaterialButton BTN_Picker;
    public static final int PICK_FILE_RESULT_CODE = 1;
    Uri fileUri;
    String filePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TV_File_Path = findViewById(R.id.TV_File_Picker);
        BTN_Picker = findViewById(R.id.BTN_Picker);

        BTN_Picker.setOnClickListener(view -> {
            takePermissions();

            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.setType("video/*");
            chooseFile = Intent.createChooser(chooseFile,"Choose Video File");
            startActivityForResult(chooseFile, PICK_FILE_RESULT_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case PICK_FILE_RESULT_CODE:
                if (resultCode == -1){
                    assert data != null;
                    fileUri = data.getData();
                    filePath = fileUri.getPath();
                    TV_File_Path.setText(filePath);
                }
        }
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

    private void takePermissions(){
        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (!EasyPermissions.hasPermissions(this,permissions)){
            EasyPermissions.requestPermissions(this,"Allow Permissions",1,permissions);
        }
    }
}