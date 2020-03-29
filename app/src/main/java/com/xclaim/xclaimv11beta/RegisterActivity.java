package com.xclaim.xclaimv11beta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {


    public static final String TAG = "TAG";
    EditText mFullName,mEmail,mPassword,mPhone;
   Button mRegisterBtn;
   TextView mLoginBtn;
   FirebaseAuth fAuth;
   ProgressBar progressBar;

   FirebaseFirestore fstore;
   String userID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFullName =findViewById(R.id.fullName);
        mEmail = findViewById(R.id.Email);
        mPassword = findViewById(R.id.password);
        mPhone = findViewById(R.id.profilePhone);
        mRegisterBtn = findViewById(R.id.loginBtn);
        mLoginBtn = findViewById(R.id.createBtn);


        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        progressBar = findViewById(R.id.progressBar);


        //Check if the user is already logged in, send them to mainActivity
        if(fAuth.getCurrentUser() != null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }


        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final String email = mEmail.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                final String fullname = mFullName.getText().toString().trim();
                final String phone = mPhone.getText().toString().trim();


                if(TextUtils.isEmpty(email)){
                   mEmail.setError("Email is required");
                   return;
                }

                if(TextUtils.isEmpty(password)){
                    mPassword.setError("Password is Required");
                    return;
                }

                if(password.length() < 6){
                    mPassword.setError(" Password must be >= 6 Characters");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //register the user in firebase
                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(RegisterActivity.this, "User Created." , Toast.LENGTH_SHORT).show();


                            //User ID using firebase reference
                            userID = fAuth.getCurrentUser().getUid();

                            //Processing data to and from firestore
                            DocumentReference documentReference = fstore.collection("users").document(userID); //will auto create the collection if not present
                            Map<String, Object> user = new HashMap<>();
                            user.put("fName", fullname);
                            user.put("Email", email);
                            user.put("phone", phone);
                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() { //checks if uploading to database is succesful
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "onSuccess: userProfile is created for " + userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure:" + e.toString());
                                }
                            });



                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            
                        } else{
                            Toast.makeText(RegisterActivity.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            }
        });
    }
}
