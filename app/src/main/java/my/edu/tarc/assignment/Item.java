package my.edu.tarc.assignment;

import java.io.Serializable;


/**
 * Created by Yeap Theam on 25/11/2017.
 */

public class Item implements Serializable {
    String itemID;
    String itemName;
    int quantity;
    double price;
    String shopID;

    public Item(){

    }

    public Item(String itemID, String itemName, int quantity, double price, String shopID) {
        this.itemID = itemID;
        this.itemName = itemName;
        this.quantity = quantity;
        this.price = price;
        this.shopID = shopID;
    }

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return String.format("Item Name: %s\n\t\t\tQuantity: %d\n\t\t\tTotal: RM %.2f", itemName, quantity, price);
    }
}
