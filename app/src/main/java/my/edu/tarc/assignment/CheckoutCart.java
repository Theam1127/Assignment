package my.edu.tarc.assignment;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CheckoutCart extends AppCompatActivity {
    List<Item> cart;
    TextView textViewCheckoutTotal;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_cart);
        double total = 0.0;
        progressDialog = new ProgressDialog(this);
        Intent intent = getIntent();
        Bundle checkout_cart = intent.getBundleExtra(AddItem.CHECKOUT_CART);
        cart = new ArrayList<Item>();
        cart = (List<Item>)checkout_cart.getSerializable(AddItem.CHECKOUT_CART);
        List<String> cart_items = new ArrayList<String>();
        ListView listViewCheckOutCart = (ListView)findViewById(R.id.listViewCheckOutCart);
        for(int a=0;a<cart.size();a++) {
            cart_items.add(a + 1 + ". " + cart.get(a).toString());
            total+=cart.get(a).getPrice();
        }
        textViewCheckoutTotal = (TextView)findViewById(R.id.textViewCheckoutTotal);
        textViewCheckoutTotal.setText(String.format("RM %.2f", total));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, cart_items);
        listViewCheckOutCart.setAdapter(arrayAdapter);
    }

    public void confirmCheckout(View view){
        if(!progressDialog.isShowing())
            progressDialog.setMessage("Checking out...");
        progressDialog.show();
        for(int a=0;a<cart.size();a++){
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {}
            };
            ItemUpdate itemUpdate = new ItemUpdate(cart.get(a).getItemID(),cart.get(a).getQuantity(), responseListener);
            RequestQueue queue = Volley.newRequestQueue(this);
            queue.add(itemUpdate);
        }
        Intent intent = new Intent();
        intent.putExtra(AddItem.CHECKOUT_CART, "SUCCESS");
        setResult(AddItem.REQUEST_CHECKOUT, intent);
        Toast.makeText(this,"Checkout Successfully",Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
