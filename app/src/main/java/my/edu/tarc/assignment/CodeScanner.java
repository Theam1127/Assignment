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
        Intent intent = new Intent(this,ItemDetail.class);
        intent.putExtra(GET_QUANTITY,content);
        startActivityForResult(intent, REQUEST_ITEM_QUANTITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_ITEM_QUANTITY){
            int quantity;
            quantity = data.getIntExtra(QUANTITY,0);
            Intent intent2 = new Intent();
            intent2.putExtra(MainPage.NEW_ITEM,content);
            intent2.putExtra(MainPage.NEW_ITEM_QUANTITY,quantity);
            setResult(MainPage.REQUEST_CODE_CONTENT,intent2);
            finish();
        }
    }
}
