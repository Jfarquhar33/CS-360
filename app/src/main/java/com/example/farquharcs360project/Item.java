package com.example.farquharcs360project;

public class Item {
    private int id;
    private String name;
    private int stock;

    public Item(int id, String name, int stock) {
        this.id = id;
        this.name = name;
        this.stock = stock;
    }

    public int getId() { return id; }

    public String getName() {
        return name;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}
