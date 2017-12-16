package my.edu.tarc.assignment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class CodeScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler  {
    private ZXingScannerView zXingScannerView;
    public static final int REQUEST_ITEM_QUANTITY = 3;
    public static final String GET_QUANTITY = "my.tarc.edu.assignment.GET_QUANTITY";
    public static final String QUANTITY = "my.tarc.edu.assignment.QUANTITY";
    Item item;
    String content;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code__scanner);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 0);
        zXingScannerView = new ZXingScannerView(getApplicationContext());
        setContentView(zXingScannerView);
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();
    }

    @Override
    protected void onPause(){
        super.onPause();
        zXingScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        content = result.getText().toString();
        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if(success==true){
                        item = new Item();
                        item.setItemID(jsonResponse.getString("productID"));
                        item.setItemName(jsonResponse.getString("productName"));
                        item.setPrice(jsonResponse.getDouble("productPrice"));
                        item.setQuantity(jsonResponse.getInt("productQty"));

                        //connect database to check existence of product (DONE)
                        //if not exist, display error message and jump back to AddItem (DONE)
                        //check cart database
                        //If same product already in the cart, make user add quantity instead.

                        //else
                        //store product details in Item (DONE)
                        Intent intent = new Intent(CodeScanner.this,ItemDetail.class);
                        intent.putExtra(GET_QUANTITY,item);
                        CodeScanner.this.startActivityForResult(intent, REQUEST_ITEM_QUANTITY);
                    }
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(CodeScanner.this);
                        builder.setMessage("Item not exist!").setNegativeButton("Retry", null).create().show();
                        zXingScannerView.resumeCameraPreview(CodeScanner.this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        ItemRequest itemRequest = new ItemRequest(content,responseListener);
        RequestQueue queue = Volley.newRequestQueue(CodeScanner.this);
        queue.add(itemRequest);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_ITEM_QUANTITY){
            int quantity;
            quantity = data.getIntExtra(QUANTITY,0);
            Item newItem = new Item(item.getItemID(), item.getItemName(), quantity, quantity*item.getPrice());
            Intent intent = new Intent();
            intent.putExtra(AddItem.NEW_ITEM,newItem);
            setResult(AddItem.REQUEST_CODE_CONTENT,intent);
            finish();
        }
    }
}
