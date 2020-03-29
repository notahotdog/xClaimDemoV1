package com.xclaim.xclaimv11beta;

import com.google.firebase.firestore.Exclude;

public class Receipts {


    private String documentID;
    private  String receiptID;
    private String description;

    public Receipts(){
        //No arg constructors needed
    }

    @Exclude
    public String getDocumentID() {
        return documentID;
    }

    public void setDocumentID(String documentID) {
        this.documentID = documentID;
    }

    public Receipts(String receiptId, String description){
        this.receiptID = receiptId;
        this.description = description;
    }

    public String getReceiptID(){
        return receiptID;
    }

    public String getDescription(){
        return description;
    }





}
