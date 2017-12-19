package my.edu.tarc.assignment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
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
    private static final String SAVE_ITEM_LIST = "my.edu.tarc.assignment.SAVEITEMLIST";
    TextView textViewTotalPrice;
    ProgressDialog progressDialog;
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
        if(savedInstanceState!=null)
            cart_list = (List<Item>)savedInstanceState.getSerializable(SAVE_ITEM_LIST);
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(SAVE_ITEM_LIST, (Serializable)cart_list);
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
                    cart_list.clear();
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
}
