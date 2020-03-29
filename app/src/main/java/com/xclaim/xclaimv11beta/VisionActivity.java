package com.xclaim.xclaimv11beta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.HashMap;
import java.util.Map;

public class VisionActivity extends AppCompatActivity {

    public static final String TAG = "TAG";
    SurfaceView cameraView;
    TextView textView;
    CameraSource cameraSource;
    final int RequestCameraPermissionID = 1001;
    FirebaseAuth fAuth;
    //FirebaseFirestore fStore;
    ProgressBar uploadProgressBar;

    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();



    //Receipts Database
    DocumentReference receiptsRef = fStore.document("Receipts/receiptList");
    CollectionReference receiptsbookRef = fStore.collection("Receipts");
    StringBuilder dataUpload = new StringBuilder();

    public static final String KEY_RECEIPTID = "receiptsID"; //actuallu not sure if its needed
    public static final String KEY_DESCRIPTION = "description";






    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case RequestCameraPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }

                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vision);


        //Firebase Setup
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        //Google Vision Setup
        cameraView = (SurfaceView) findViewById(R.id.surface_view);
        textView = (TextView) findViewById(R.id.text_view);


        uploadProgressBar= findViewById(R.id.uploadProgressBar);



        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Log.w("Main Activity", "Detector dependencies are not yet available");

        } else {
            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();
            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(VisionActivity.this, new String[]{Manifest.permission.CAMERA}, RequestCameraPermissionID);
                            return;
                        }

                        cameraSource.start(cameraView.getHolder());

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    cameraSource.stop();
                }

            });

            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0) {
                        textView.post(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder stringBuilder = new StringBuilder();
                                for (int x = 0; x < items.size(); ++x) {
                                    TextBlock item = items.valueAt(x);
                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("\n");
                                }

                                dataUpload = stringBuilder;

                                textView.setText(stringBuilder.toString());
                            }
                        });
                    }
                }
            });
        }
    }

    public void returnToMain(View view){
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }
    public void uploadData(View view){
       //todo upload data parsed to FireStore
        //Todo Obtain Data

        //todo need to figure out how to obtain unique id

        final String dataUploadFirebase = dataUpload.toString(); // data to be uploaded to firevbase - Value


        Receipts receipts = new Receipts("1", dataUploadFirebase);

        if(!TextUtils.isEmpty(receipts.getDescription())){ //check whether theres anything being uploaded

            uploadProgressBar.setVisibility(View.VISIBLE);
            //todo change the receiptID


            //Multiple Documents
            receiptsbookRef.add(receipts);
            Toast.makeText(VisionActivity.this,"Receipt Details Saved", Toast.LENGTH_SHORT).show();

            //how to individually store documents
            /*
            receiptsRef.set(receipts).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(VisionActivity.this,"Receipt Details Saved", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(VisionActivity.this,"Error !", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, e.toString());
                }
            });
            */


            //todo figure out a way how to count the number of receipts uploaded
            //todo figure out a way how to display the receipts




            uploadProgressBar.setVisibility(View.GONE);

        }






    }


}
