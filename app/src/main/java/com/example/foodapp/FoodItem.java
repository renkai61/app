package com.example.foodapp;

public class FoodItem {
    public int id;
    public String name;
    public String type;
    public int quantity;
    public String expiryDate;
    public String photoUri;

    public FoodItem(int id, String name, String type, int quantity, String expiryDate, String photoUri) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.quantity = quantity;
        this.expiryDate = expiryDate;
        this.photoUri = photoUri;
    }
}
