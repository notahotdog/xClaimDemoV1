package com.xclaim.xclaimv11beta;

public class Receipts {


    private  String receiptID;
    private String description;

    public Receipts(){
        //No arg constructors needed
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
