package my.edu.tarc.assignment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class CodeScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler  {
    private ZXingScannerView zXingScannerView;
    public static final int REQUEST_ITEM_QUANTITY = 0;
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

        //connect database to check existence of product
        //if not exist, display error message and jump back to AddItem
        //check cart database
        //If same product already in the cart, make user add quantity instead.

        //else
        //store product details in Item
        item = new Item(content, "Dutch Lady Milk 300ml", 100, 5.50);
        Intent intent = new Intent(this,ItemDetail.class);
        intent.putExtra(GET_QUANTITY,item);
        startActivityForResult(intent, REQUEST_ITEM_QUANTITY);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(this,AddItem.class);
        CodeScanner.this.finish();
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_ITEM_QUANTITY){
            int quantity;
            quantity = data.getIntExtra(QUANTITY,0);
            Item newItem = new Item(content, item.getItemName(), quantity, quantity*item.getPrice());
            Intent intent = new Intent();
            intent.putExtra(AddItem.NEW_ITEM,newItem);
            setResult(AddItem.REQUEST_CODE_CONTENT,intent);
            finish();
        }
    }
}
