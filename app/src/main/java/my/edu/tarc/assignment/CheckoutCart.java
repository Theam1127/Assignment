package my.edu.tarc.assignment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class CheckoutCart extends AppCompatActivity {
    List<Item> cart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_cart);
        Intent intent = getIntent();
        Bundle checkout_cart = intent.getBundleExtra(AddItem.CHECKOUT_CART);
        cart = new ArrayList<Item>();
        cart = (List<Item>)checkout_cart.getSerializable(AddItem.CHECKOUT_CART);
        List<String> cart_items = new ArrayList<String>();
        ListView listViewCheckOutCart = (ListView)findViewById(R.id.listViewCheckOutCart);
        for(int a=0;a<cart.size();a++)
            cart_items.add(a+1+". "+cart.get(a).toString());
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, cart_items);
        listViewCheckOutCart.setAdapter(arrayAdapter);
    }

    public void confirmCheckout(View view){
        for(int a=0;a<cart.size();a++){

        }
    }
}
