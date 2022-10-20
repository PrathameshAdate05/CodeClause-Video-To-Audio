package com.prathamesh.videotoaudio;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;


public class Home extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    TextView TV_File_Path;
    MaterialButton BTN_Picker, BTN_Extract;
    public static final int PICK_FILE_RESULT_CODE = 1;
    Uri fileUri;
    String filePath, bit, vol;
    File file;

    TextView Bottom_Sheet_Done_OK, Bottom_sheet_Failed_OK;

    Spinner Spinner_Bitrate, Spinner_Volume;

    String[] BitRates = {"128K", "192K", "320K"};
    String[] Volumes = {"0.5x", "1x", "1.5x", "2x"};

    LinearLayout LLAttributes;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TV_File_Path = findViewById(R.id.TV_File_Picker);
        BTN_Picker = findViewById(R.id.BTN_Picker);
        BTN_Extract = findViewById(R.id.BTN_Extract);

        Spinner_Bitrate = findViewById(R.id.Spinner_BitRate);
        Spinner_Volume = findViewById(R.id.Spinner_Volume);

        LLAttributes = findViewById(R.id.LL_Attributes);
        LLAttributes.setVisibility(View.GONE);

        ArrayAdapter ad_bitrate = new ArrayAdapter(this, android.R.layout.simple_spinner_item, BitRates);
        ArrayAdapter ad_volume = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Volumes);

        ad_bitrate.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ad_volume.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner_Bitrate.setAdapter(ad_bitrate);
        Spinner_Volume.setAdapter(ad_volume);

        BottomSheetDialog bottomSheetDialogDone = new BottomSheetDialog(this, R.style.BottomSheetStyle);
        View bottomSheetDoneView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_done, null);
        Bottom_Sheet_Done_OK = bottomSheetDoneView.findViewById(R.id.Bottom_Sheet_Done_OK);
        bottomSheetDialogDone.setContentView(bottomSheetDoneView);

        BottomSheetDialog bottomSheetDialogFailed = new BottomSheetDialog(this, R.style.BottomSheetStyle);
        View bottomSheetFailedView = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_failed, null);
        Bottom_sheet_Failed_OK = bottomSheetFailedView.findViewById(R.id.Bottom_Sheet_Failed_OK);
        bottomSheetDialogFailed.setContentView(bottomSheetFailedView);

        Bottom_sheet_Failed_OK.setOnClickListener(view -> {
            bottomSheetDialogFailed.dismiss();
        });

        Bottom_Sheet_Done_OK.setOnClickListener(view -> {
            bottomSheetDialogDone.dismiss();
        });

        // spinner bitrate
        Spinner_Bitrate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                int tempBit = Integer.parseInt(BitRates[i].substring(0, BitRates[i].length() - 1));
                bit = String.valueOf(tempBit * 1000);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // spinner volume
        Spinner_Volume.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                vol = "volume=" + Volumes[i].substring(0, Volumes[i].length() - 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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

            ProgressDialog progressDialog = new ProgressDialog(Home.this);
            progressDialog.setMessage("Extracting...");
            progressDialog.show();

            String video = TV_File_Path.getText().toString();
            String[] temp = TV_File_Path.getText().toString().split("/");
            String filename = temp[temp.length - 1].substring(0, temp[temp.length - 1].length() - 4);
            String ext = ".mp3";

            File musicDir = Environment.getExternalStoragePublicDirectory("Music/Audio Extractor");

            int fileno = 0;
            File destination = new File(musicDir, filename + ext);

            while (destination.exists()) {
                fileno++;
                destination = new File(musicDir, filename + String.valueOf(fileno) + ext);
            }

            filePath = destination.getAbsolutePath();

            String[] command = {"-y", "-i", video, "-f", "mp3", "-ab", bit, "-af", vol, "-vn", filePath};

            FFmpeg.executeAsync(command, new ExecuteCallback() {

                @Override
                public void apply(long executionId, int returnCode) {

                    if (returnCode == Config.RETURN_CODE_SUCCESS) {
                        progressDialog.dismiss();
                        TV_File_Path.setText("");
                        LLAttributes.setVisibility(View.GONE);
                        bottomSheetDialogDone.show();

                    } else {
                        progressDialog.dismiss();
                        LLAttributes.setVisibility(View.GONE);
                        TV_File_Path.setText("");
                        bottomSheetDialogFailed.show();
                    }
                }
            });
        });


        TV_File_Path.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals("")) {
                    LLAttributes.setVisibility(View.GONE);
                } else {
                    LLAttributes.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
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
                    String[] column = {MediaStore.Images.Media.DATA};
                    // where id is equal to
                    String sel = MediaStore.Images.Media._ID + "=?";
                    Cursor cursor = getContentResolver().query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, column, sel, new String[]{id}, null);
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