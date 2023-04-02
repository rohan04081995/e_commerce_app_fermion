package com.example.fermiontask;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.regex.Pattern;

public class PA extends AppCompatActivity {

    public static final String TAG = "dataCheck";
    private ImageView user_profile_iv;
    private ImageButton add_image_ib;
    private EditText user_phone_no_et;
    private EditText user_email_et;
    private EditText user_address_et;
    private Button submit_user_details_button;

    private ActivityResultLauncher<String> galleryPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user_profile_iv = findViewById(R.id.user_profile_iv);
        add_image_ib = findViewById(R.id.add_image_ib);
        user_phone_no_et = findViewById(R.id.user_phone_no_et);
        user_email_et = findViewById(R.id.user_email_et);
        user_address_et = findViewById(R.id.user_address_et);
        submit_user_details_button = findViewById(R.id.submit_user_details_button);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            galleryPermissionCheck();
//        }

        submit_user_details_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean email = validateEmailAddress();
                boolean phone = validatePhoneNumber();
                boolean address = validateAddress();

                Log.d(TAG, "onClick pa activity :email: " + email + " : phone: " + phone + " : address: " + address);

                if (!validateEmailAddress() | !validatePhoneNumber() | !validateAddress()) {
                    return;
                }
                Toast.makeText(PA.this, "all fields are valid", Toast.LENGTH_SHORT).show();
            }
        });

        user_profile_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        add_image_ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDialog();
            }

        });

        galleryPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                Log.d(TAG, "onActivityResult: pa: result: " + result.toString());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void galleryPermissionCheck() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "galleryPermissionCheck pa: permission granted");
        } else {
//            galleryPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
            Log.d(TAG, "galleryPermissionCheck: permission not granted");
            ActivityCompat.requestPermissions(PA.this,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_CODE) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission is granted, do something
//            } else {
//                // Permission denied, show an error message or disable functionality
//            }
//        }
        if (requestCode == 1 && grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onRequestPermissionsResult: permission granted");
        } else {
            Log.d(TAG, "onRequestPermissionsResult: permission not granted");
        }
    }

    private boolean validateEmailAddress() {
        if (user_email_et.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "please enter valid email address", Toast.LENGTH_SHORT).show();
            user_email_et.setError("please enter valid email address");
            return false;
        } else if (!Pattern.matches(Patterns.EMAIL_ADDRESS.pattern(), user_email_et.getText().toString().trim())) {
            user_email_et.setError("please enter valid email address");
            return false;
        } else {
            user_email_et.setError(null);
            return true;
        }
    }

    private boolean validatePhoneNumber() {
        if (user_phone_no_et.getText().toString().trim().length() != 10) {
            user_phone_no_et.setError("please enter valid phone number");
            return false;
        } else {
            user_phone_no_et.setError(null);
            return true;
        }
    }

    private boolean validateAddress() {
        if (user_address_et.getText().toString().trim().isEmpty()) {
            user_address_et.setError("please enter valid address");
            return false;
        } else {
            user_address_et.setError(null);
            return true;
        }
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("select your option");
//        String[] items = arrayOf("Gallery", "Camera");
        String[] items = new String[]{"Gallery", "camera"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        dialog.dismiss();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            galleryPermissionCheck();
                        }
                        break;
                    case 1:
                        break;
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}