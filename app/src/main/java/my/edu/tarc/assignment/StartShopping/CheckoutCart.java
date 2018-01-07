package my.edu.tarc.assignment.StartShopping;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
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

import java.util.ArrayList;
import java.util.List;

import my.edu.tarc.assignment.CheckConnection;
import my.edu.tarc.assignment.DatabaseRequest.CheckoutRequest;
import my.edu.tarc.assignment.DatabaseRequest.ItemUpdate;
import my.edu.tarc.assignment.R;
import my.edu.tarc.assignment.TopUp.TopUpMain;

public class CheckoutCart extends AppCompatActivity {
    List<Item> cart;
    TextView textViewCheckoutTotal, textViewAccountBalance;
    ProgressDialog progressDialog;
    SharedPreferences userPreference;
    double total;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout_cart);
        total = 0.0;
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
        textViewAccountBalance = (TextView)findViewById(R.id.textViewAccountBalance);
        textViewCheckoutTotal.setText(String.format("Total: RM %.2f", total));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, cart_items);
        listViewCheckOutCart.setAdapter(arrayAdapter);
    }

    public void confirmCheckout(View view){
        if(!CheckConnection.isConnected(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
            return;
        }
        progressDialog.setCancelable(false);
        userPreference = getSharedPreferences("CURRENT_USER", MODE_PRIVATE);
        double userCredit = Double.parseDouble(userPreference.getString("CURRENT_CREDIT", "0.00"));
        String username = userPreference.getString("LOGIN_USER", "");
        if(userCredit>=total) {
            if (!progressDialog.isShowing())
                progressDialog.setMessage("Checking out...");
            progressDialog.show();
            userCredit -= total;
            SharedPreferences.Editor editor = userPreference.edit();
            editor.putString("CURRENT_CREDIT", String.format("%.2f", userCredit));
            editor.commit();
            Response.Listener<String> responseListenerCheckout = new Response.Listener<String>(){
                @Override
                public void onResponse(String response) {
                    for (int a = 0; a < cart.size(); a++) {
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Intent intent = new Intent();
                                intent.putExtra(AddItem.CHECKOUT_CART, "SUCCESS");
                                setResult(AddItem.REQUEST_CHECKOUT, intent);
                                Toast.makeText(CheckoutCart.this, "Checkout Successfully", Toast.LENGTH_SHORT).show();
                                finish();
                                if(progressDialog.isShowing())
                                    progressDialog.dismiss();
                            }
                        };
                        ItemUpdate itemUpdate = new ItemUpdate(cart.get(a).getItemID(), cart.get(a).getQuantity(), cart.get(a).getShopID(), responseListener);
                        RequestQueue queue = Volley.newRequestQueue(CheckoutCart.this);
                        queue.add(itemUpdate);
                    }
                }
            };
            CheckoutRequest checkoutRequest = new CheckoutRequest(total, username, responseListenerCheckout);
            RequestQueue queue = Volley.newRequestQueue(CheckoutCart.this);
            queue.add(checkoutRequest);


        }
        else{
            AlertDialog.Builder builder = new AlertDialog.Builder(CheckoutCart.this);
            builder.setMessage("You have no enough credit to pay the purchase! Do you want to top up now?").setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(CheckoutCart.this, "Checkout failed", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if(!CheckConnection.isConnected(getApplicationContext())) {
                        Toast.makeText(getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Intent intent = new Intent(CheckoutCart.this, TopUpMain.class);
                        startActivity(intent);
                    }
                }
            }).create().show();
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
        userPreference = getSharedPreferences("CURRENT_USER", MODE_PRIVATE);
        double userCredit = Double.parseDouble(userPreference.getString("CURRENT_CREDIT", "0.00"));
        textViewAccountBalance.setText(String.format("Current Credit: RM %.2f", userCredit));
    }
}
