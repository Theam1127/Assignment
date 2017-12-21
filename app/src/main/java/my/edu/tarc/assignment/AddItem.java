package my.edu.tarc.assignment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
public class AddItem extends AppCompatActivity {
    public static final int REQUEST_CODE_CONTENT = 1;

    public static final String NEW_ITEM = "my.edu.tarc.assignment.PRODUCTID";
    public static final String EDIT_ITEM = "my.edu.tarc.assignment.EDITITEM";
    public static final String DB_ITEM = "my.edu.tarc.assignment.DBITEM";
    public static final int REQUEST_CHECKOUT = 3;
    public static final int REQUEST_ITEM_DETAIL = 2;
    public static final String EDITED_ITEM = "my.edu.tarc.assignment.EDITEDITEM";
    public static final String CHECKOUT_CART = "my.edu.tarc.assignment.CHECKOUT";
    private static String SAVE_ITEM_LIST = "my.edu.tarc.assignment.SAVE_ITEM_LIST";
    private static String EXIT_TIME = "my.edu.tarc.assignment.EXIT_TIME";
    TextView textViewTotalPrice;
    ProgressDialog progressDialog;
    SharedPreferences cartPreferences, userPreferences;
    SharedPreferences.Editor editor;
    double total_price = 0.0;
    ListView cart;
    List<Item> cart_list = null;
    CartAdapter arrayAdapter = null;
    Item item = new Item();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        cart = (ListView)findViewById(R.id.listViewCart);
        Intent intent = getIntent();
        userPreferences = getSharedPreferences("CURRENT_USER", MODE_PRIVATE);
        String username = userPreferences.getString("LOGIN_USER", "");
        cartPreferences = getSharedPreferences(username, MODE_PRIVATE);

        Long od = cartPreferences.getLong(EXIT_TIME, 0L);
        if(od!=0){
            Date oldDate = new Date(od);
            Date currentDate = new Date();
            int hours = currentDate.getHours() - oldDate.getHours();
            int minutes = (currentDate.getMinutes()+(hours*60)) - oldDate.getMinutes();
            if (currentDate.getDay() == oldDate.getDay() && minutes < 30) {
                Gson gson = new Gson();
                String fromJson = cartPreferences.getString(SAVE_ITEM_LIST, "");
                cart_list = gson.fromJson(fromJson, new TypeToken<List<Item>>(){}.getType());
            } else if(currentDate.getDay()>oldDate.getDay() || minutes >= 30){
                SharedPreferences.Editor editor = cartPreferences.edit();
                editor.clear();
                editor.commit();
                AlertDialog.Builder builder = new AlertDialog.Builder(AddItem.this);
                builder.setMessage("Cart expired! You have left app closed more than 30 minutes!").setNegativeButton("Ok", null).create().show();
            }
        }
        if(cart_list == null)
            cart_list = new ArrayList<Item>();
        arrayAdapter = new CartAdapter(cart_list, this);
        cart.setAdapter(arrayAdapter);
        progressDialog = new ProgressDialog(this);
        Button buttonAddItem = (Button)findViewById(R.id.buttonAddItem);
        buttonAddItem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getBaseContext(), CodeScanner.class);
                startActivityForResult(intent, REQUEST_CODE_CONTENT);
        }
        });

        cart.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(!progressDialog.isShowing())
                    progressDialog.setMessage("Loading...");
                progressDialog.show();
                final Item editItem = cart_list.get(position);
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Item item = new Item();
                            item.setItemID(jsonResponse.getString("productID"));
                            item.setItemName(jsonResponse.getString("productName"));
                            item.setPrice(jsonResponse.getDouble("productPrice"));
                            item.setQuantity(jsonResponse.getInt("productQty"));
                            Intent intent = new Intent(getBaseContext(), ItemDetail.class);
                            intent.putExtra(AddItem.EDIT_ITEM, editItem);
                            intent.putExtra(AddItem.DB_ITEM,item);
                            startActivityForResult(intent, REQUEST_ITEM_DETAIL);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                ItemRequest itemRequest = new ItemRequest(editItem.getItemID(), responseListener);
                RequestQueue queue = Volley.newRequestQueue(getBaseContext());
                queue.add(itemRequest);
                if(progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        });

        Button buttonCheckOut = (Button)findViewById(R.id.buttonCheckout);
        buttonCheckOut.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(cart_list.isEmpty())
                    Toast.makeText(getBaseContext(),"You have no items in your cart!", Toast.LENGTH_SHORT).show();
                else {
                    if(!progressDialog.isShowing())
                        progressDialog.setMessage("Please wait...");
                    progressDialog.show();
                    Intent intent = new Intent(getBaseContext(), CheckoutCart.class);
                    Bundle checkout_cart = new Bundle();
                    checkout_cart.putSerializable(CHECKOUT_CART, (Serializable) cart_list);
                    intent.putExtra(CHECKOUT_CART, checkout_cart);
                    startActivityForResult(intent,REQUEST_CHECKOUT);
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();

                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        total_price = 0.0;
        for (int a = 0; a < cart_list.size(); a++)
            total_price += cart_list.get(a).getPrice();
        textViewTotalPrice = (TextView)findViewById(R.id.textViewTotalPrice);
        textViewTotalPrice.setText(String.format("%.2f", total_price));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode!=RESULT_CANCELED) {
            if (requestCode == REQUEST_CODE_CONTENT) {
                item = (Item) data.getSerializableExtra(NEW_ITEM);
                if (item != null) {
                    boolean exist = false;
                    for (int a = 0; a < cart_list.size(); a++)
                        if (cart_list.get(a).getItemID().equals(item.getItemID())) {
                            exist = true;
                            cart_list.get(a).setQuantity(cart_list.get(a).getQuantity() + item.getQuantity());
                            cart_list.get(a).setPrice(cart_list.get(a).getPrice() + item.getPrice());
                        }
                    if (exist == false)
                        cart_list.add(item);
                    arrayAdapter.notifyDataSetChanged();
                }
            } else if (requestCode == REQUEST_ITEM_DETAIL) {
                item = (Item) data.getSerializableExtra(EDITED_ITEM);
                for (int a = 0; a < cart_list.size(); a++) {
                    if (cart_list.get(a).getItemID().equals(item.getItemID())) {
                        cart_list.get(a).setQuantity(item.getQuantity());
                        cart_list.get(a).setPrice(item.getPrice());
                    }
                }
                arrayAdapter.notifyDataSetChanged();
            } else if (requestCode == REQUEST_CHECKOUT) {
                String status = data.getStringExtra(CHECKOUT_CART);
                if (status.equals("SUCCESS")) {
                    editor = cartPreferences.edit();
                    cart_list.clear();
                    editor.clear();
                    editor.commit();
                    arrayAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
            finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return true;
    }



    @Override
    public void onStop() {
        if(cart_list!=null) {
            editor = cartPreferences.edit();
            Date exitTime = new Date();

            Gson gson = new Gson();
            String toJson = gson.toJson(cart_list);
            editor.putString(SAVE_ITEM_LIST, toJson);
            editor.putLong(EXIT_TIME, exitTime.getTime());
            editor.commit();
        }
        super.onStop();

    }
}
