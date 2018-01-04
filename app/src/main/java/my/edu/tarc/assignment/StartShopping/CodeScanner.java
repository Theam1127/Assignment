package my.edu.tarc.assignment.StartShopping;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import my.edu.tarc.assignment.CheckConnection;
import my.edu.tarc.assignment.DatabaseRequest.ItemRequest;
import my.edu.tarc.assignment.R;
import my.edu.tarc.assignment.TopUp.TopUpMain;

public class CodeScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler  {
    private ZXingScannerView zXingScannerView;
    public static final int REQUEST_ITEM_QUANTITY = 3;
    public static final String GET_QUANTITY = "my.tarc.edu.assignment.GET_QUANTITY";
    public static final String QUANTITY = "my.tarc.edu.assignment.QUANTITY";
    ProgressDialog progressDialog;
    Item item;
    String content;
    String shopID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code__scanner);
        progressDialog = new ProgressDialog(this);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 0);
        Intent intent = getIntent();
        shopID = intent.getStringExtra(AddItem.PUT_SHOP);
        zXingScannerView = (ZXingScannerView)findViewById(R.id.zxingScanner);
        zXingScannerView.setResultHandler(this);
        zXingScannerView.startCamera();
        final EditText editTextBarcode = (EditText)findViewById(R.id.editTextBarcode);
        Button buttonBarCode = (Button)findViewById(R.id.buttonBarcode);
        buttonBarCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String barcode = editTextBarcode.getText().toString();
                if(barcode.isEmpty()){
                    editTextBarcode.setError("Please enter bar code");
                    return;
                }
                if(!CheckConnection.isConnected(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
                    return;
                }
                zXingScannerView.stopCameraPreview();
                if(!progressDialog.isShowing()){
                    progressDialog.setMessage("Scanning item....");
                }
                progressDialog.show();
                content = editTextBarcode.getText().toString();
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
                                //If same product already in the cart, make user add quantity instead.(DONE)

                                //else
                                //store product details in Item (DONE)
                                Intent intent = new Intent(CodeScanner.this,ItemDetail.class);
                                intent.putExtra(GET_QUANTITY,item);
                                CodeScanner.this.startActivityForResult(intent, REQUEST_ITEM_QUANTITY);
                            }
                            else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(CodeScanner.this);
                                builder.setMessage("Item not exist!").setNegativeButton("Retry",null).setOnDismissListener(new DialogInterface.OnDismissListener() {
                                    @Override
                                    public void onDismiss(DialogInterface dialog) {
                                        zXingScannerView.resumeCameraPreview(CodeScanner.this);
                                    }
                                }).create().show();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                };
                ItemRequest itemRequest = new ItemRequest(content, shopID,responseListener);
                RequestQueue queue = Volley.newRequestQueue(CodeScanner.this);
                queue.add(itemRequest);
            }
        });

    }

    @Override
    protected void onPause(){
        super.onPause();
        zXingScannerView.stopCamera();
    }


    @Override
    public void handleResult(Result result) {
        if(!progressDialog.isShowing()){
            progressDialog.setMessage("Scanning item....");
        }
        progressDialog.show();
        if(!CheckConnection.isConnected(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            zXingScannerView.resumeCameraPreview(CodeScanner.this);
            return;
        }
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
                        //If same product already in the cart, make user add quantity instead.(DONE)

                        //else
                        //store product details in Item (DONE)
                        Intent intent = new Intent(CodeScanner.this,ItemDetail.class);
                        intent.putExtra(GET_QUANTITY,item);
                        CodeScanner.this.startActivityForResult(intent, REQUEST_ITEM_QUANTITY);
                    }
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(CodeScanner.this);
                        builder.setMessage("Item not exist!").setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                zXingScannerView.resumeCameraPreview(CodeScanner.this);
                            }
                        }).create().show();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if(progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        };
        ItemRequest itemRequest = new ItemRequest(content, shopID,responseListener);
        RequestQueue queue = Volley.newRequestQueue(CodeScanner.this);
        queue.add(itemRequest);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_ITEM_QUANTITY && resultCode!=RESULT_CANCELED){
            int quantity;
            quantity = data.getIntExtra(QUANTITY,0);
            Item newItem = new Item(item.getItemID(), item.getItemName(), quantity, quantity*item.getPrice(), shopID);
            Intent intent = new Intent();
            intent.putExtra(AddItem.NEW_ITEM,newItem);
            setResult(AddItem.REQUEST_CODE_CONTENT,intent);
            finish();
        }
        else{
            onBackPressed();
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
}
