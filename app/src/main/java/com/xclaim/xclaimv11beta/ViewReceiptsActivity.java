package com.xclaim.xclaimv11beta;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ViewReceiptsActivity extends AppCompatActivity {
    public static final String TAG = "TAG";

    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private DocumentReference receiptsRef = fStore.document("Receipts/receiptList");

    private TextView textViewData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_receipts);
        textViewData = (TextView) findViewById(R.id.ViewReceiptsField);



        //todo figure out how to display receipts saved
        loadNote(); //might result in some error here

        //textViewData = findViewById(R.id.text_view_data); //todo create a text view on the xml

    }



    public void loadNote(){
        receiptsRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){

                    Receipts receipts = documentSnapshot.toObject(Receipts.class);

                    String receiptID = receipts.getReceiptID();
                    String description = receipts.getDescription();

                    textViewData.setText("Receipts ID :" + receiptID + "\n" + "Description: " + description);

                }else{
                    Toast.makeText(ViewReceiptsActivity.this, "Receipt does not exist", Toast.LENGTH_SHORT);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ViewReceiptsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                Log.d(TAG, e.toString());
            }
        });
    }

    public void returnToMain(View view){
        startActivity(new Intent(getApplicationContext(),MainActivity.class));
        finish();
    }






    //Somehow need to load the data we stored from firestore db and display them
}
