package my.edu.tarc.assignment.StartShopping;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import my.edu.tarc.assignment.CheckConnection;
import my.edu.tarc.assignment.R;

public class SelectShop extends AppCompatActivity {
    AutoCompleteTextView autoCompleteTextViewShop;
    ProgressDialog progressDialog;
    Button buttonSelectShop;
    List<Shop> shop_list;
    List<String> shopNames;
    ArrayAdapter<String> arrayAdapter;
    public static final String INTENT_SHOPID = "my.edu.tarc.assignment.SHOPID";
    public static final String TAG = "my.edu.tarc.assignment";
    private static final String SHOP_REQUEST_URL = "https://yongjin97.000webhostapp.com/SelectShop.php";
    int selectedIndex = -9999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_shop);
        if(!CheckConnection.isConnected(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
            finish();
        }
        progressDialog = new ProgressDialog(this);
        autoCompleteTextViewShop = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextViewShopList);
        shop_list = new ArrayList<Shop>();
        shopNames = new ArrayList<String>();
        if(!progressDialog.isShowing())
            progressDialog.setMessage("Loading...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(SHOP_REQUEST_URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int a = 0; a < response.length(); a++) {
                        JSONObject jsonShops = (JSONObject) response.get(a);
                        String id = jsonShops.getString("shopID");
                        String name = jsonShops.getString("shopName");
                        Shop shop = new Shop(id, name);
                        shop_list.add(shop);
                    }
                    for (int a = 0; a < shop_list.size(); a++)
                        shopNames.add(shop_list.get(a).getShopName());
                    arrayAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_dropdown_item_1line, shopNames);
                    autoCompleteTextViewShop.setThreshold(1);
                    autoCompleteTextViewShop.setAdapter(arrayAdapter);
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();
                    autoCompleteTextViewShop.showDropDown();
                    autoCompleteTextViewShop.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            selectedIndex = position;
                        }
                    });
                    autoCompleteTextViewShop.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(autoCompleteTextViewShop.getText().toString().isEmpty())
                                autoCompleteTextViewShop.showDropDown();
                        }
                    });
                    autoCompleteTextViewShop.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if(s.length()==0)
                                autoCompleteTextViewShop.showDropDown();
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            selectedIndex = -9999;
                            if(!autoCompleteTextViewShop.getText().toString().isEmpty())
                                for(int a=0;a<shop_list.size();a++)
                                    if(autoCompleteTextViewShop.getText().toString().equals(shop_list.get(a).getShopName()))
                                        selectedIndex = a;
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        });
        jsonObjectRequest.setTag(TAG);
        queue.add(jsonObjectRequest);
    }

    public void onClickButtonSelectShop(View view) {
        if(selectedIndex==-9999)
            Toast.makeText(this,"Please select a valid shop name!",Toast.LENGTH_SHORT).show();
        else {
            String selectedShopID = shop_list.get(selectedIndex).getShopID();
            Intent intent = new Intent(SelectShop.this, AddItem.class);
            intent.putExtra(INTENT_SHOPID, selectedShopID);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
        }
        return true;
    }
}