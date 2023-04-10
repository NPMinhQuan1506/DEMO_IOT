package com.example.iot_security;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.camera2.*;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;

public class MainActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private TextView twFLightStatus;
    private Camera camera;
    private CameraManager camManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        twFLightStatus = (TextView) findViewById(R.id.twFLightStatus);
        if (mDatabase != null) {
            mDatabase = null;
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Config").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // get all of the children at this level.
                try {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();

                // shake hands with each of them.'
                for (DataSnapshot child : children) {
                    String value = child.getValue(String.class);
                    String keysstr = child.getKey();
                    AESHelper.SecretKeys keys = AESHelper.keys(keysstr);

                    AESHelper.CipherTextIvMac cipherTextIvMac1 = new AESHelper.CipherTextIvMac(value);
                    String plainText = AESHelper.decryptString(cipherTextIvMac1, keys);
                    triggerFLight(plainText);

                }

                if (mDatabase != null) {
                    mDatabase.removeEventListener(this);
                    mDatabase = null;
                }
                } catch (InvalidKeyException e) {


                } catch (GeneralSecurityException e) {
                    throw new RuntimeException(e);
                } catch (UnsupportedEncodingException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    @Override
    protected void onStop() {
        super.onStop();

        if (camera != null) {
            camera.release();
        }
    }
    private void triggerFLight(String val){
        Context context = this;
        PackageManager pm = context.getPackageManager();
        // if device support camera?
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Log.e("err", "Device has no camera!");
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
            String cameraId = null; // Usually front camera is at 0 position and back camera is 1.
            try {

                if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
                    cameraId = camManager.getCameraIdList()[1];
                } else {
                    cameraId = camManager.getCameraIdList()[0];
                }

                if(val == "1"){
                    twFLightStatus.setText("Bật");

                    camManager.setTorchMode(cameraId, true);
                }
                else {
                    twFLightStatus.setText("Tắt");
                    camManager.setTorchMode(cameraId, false);
                }
            } catch (CameraAccessException e) {
                throw new RuntimeException(e);
            }

        } else {
            camera = Camera.open();
            final Parameters p = camera.getParameters();
            if(val == "1"){
                twFLightStatus.setText("Bật");

                Log.i("info", "torch is turn on!");

                p.setFlashMode(Parameters.FLASH_MODE_TORCH);

                camera.setParameters(p);
                camera.startPreview();
            }
            else {
                twFLightStatus.setText("Tắt");
                p.setFlashMode(Parameters.FLASH_MODE_OFF);
                camera.setParameters(p);
                camera.stopPreview();
            }
        }

    }
}