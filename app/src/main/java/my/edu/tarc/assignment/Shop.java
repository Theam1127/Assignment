package my.edu.tarc.assignment;

import java.io.Serializable;

/**
 * Created by Yeap Theam on 24/12/2017.
 */

public class Shop{
    String shopID;
    String shopName;

    public Shop(){}

    public Shop(String shopID, String shopName) {
        this.shopID = shopID;
        this.shopName = shopName;
    }

    public String getShopID() {
        return shopID;
    }

    public void setShopID(String shopID) {
        this.shopID = shopID;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }
}
