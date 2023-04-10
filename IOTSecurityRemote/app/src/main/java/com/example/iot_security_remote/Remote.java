package com.example.iot_security_remote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Remote extends AppCompatActivity {
    private Switch aSwitch;
    private static final String seed = "09011506NTTNNPMQ";
    private TextView tvStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote);
        aSwitch = (Switch) findViewById(R.id.swFLight);
        tvStatus = (TextView) findViewById(R.id.twStatus);
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("Config");
                try {
                if(isChecked){

                    Map<String, Object> values = new HashMap<>();


                    AESHelper.SecretKeys keys = AESHelper.generateKey();
                    AESHelper.CipherTextIvMac cipherTextIvMac = AESHelper.encrypt("1", keys);
                    //store or send to server
                    String ciphertextString = cipherTextIvMac.toString();

                    AESHelper.CipherTextIvMac cipherTextIvMac1 = new AESHelper.CipherTextIvMac(ciphertextString);
                    String plainText = AESHelper.decryptString(cipherTextIvMac1, keys);
                    String strkey = AESHelper.keyString(keys);
                    values.put(strkey, ciphertextString);
                    tvStatus.setText("Trạng thái: bật");
                    myRef.setValue(values);
                }
                else {
                    AESHelper.SecretKeys keys = AESHelper.generateKey();
                    AESHelper.CipherTextIvMac cipherTextIvMac = AESHelper.encrypt("1", keys);
                    //store or send to server
                    String ciphertextString = cipherTextIvMac.toString();

                    AESHelper.CipherTextIvMac cipherTextIvMac1 = new AESHelper.CipherTextIvMac(ciphertextString);
                    String plainText = AESHelper.decryptString(cipherTextIvMac1, keys);
                    String strkey = AESHelper.keyString(keys);
                    Map<String, Object> values1 = new HashMap<>();
                    values1.put(strkey, ciphertextString);
                    tvStatus.setText("Trạng thái: tắt");
                    myRef.setValue(values1);
                }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}