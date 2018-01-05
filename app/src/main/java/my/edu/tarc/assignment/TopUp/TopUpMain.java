package my.edu.tarc.assignment.TopUp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.zxing.Result;

import org.json.JSONException;
import org.json.JSONObject;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import my.edu.tarc.assignment.CheckConnection;
import my.edu.tarc.assignment.R;
import my.edu.tarc.assignment.DatabaseRequest.getQRDetailRequest;

public class TopUpMain extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    Button scan;
    Button bankCard;
    private ZXingScannerView zXingScannerView;
    boolean cameraStatus=false;
    String qrScanCode;
    int topUpCode;
    SharedPreferences userPreferences;
    String username;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up_main);

        progressDialog = new ProgressDialog(this);
        bankCard = (Button)findViewById(R.id.BankCard);
        scan = (Button) findViewById(R.id.TopUpCodeScan);
        userPreferences = getSharedPreferences("CURRENT_USER",MODE_PRIVATE);
        username = userPreferences.getString("LOGIN_USER","");

        bankCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!CheckConnection.isConnected(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(TopUpMain.this,BankCardActivity.class);
                startActivity(intent);
                finish();
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(TopUpMain.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
                    ActivityCompat.requestPermissions(TopUpMain.this, new String[] {Manifest.permission.CAMERA}, 0);
                zXingScannerView = new ZXingScannerView(getApplicationContext());
                setContentView(zXingScannerView);
                zXingScannerView.setResultHandler(TopUpMain.this);
                zXingScannerView.setFocusableInTouchMode(true);
                zXingScannerView.setAutoFocus(true);
                zXingScannerView.startCamera();
                cameraStatus = true;
            }
        });

    }


    @Override
    public void handleResult(Result result) {
        qrScanCode = result.getText();
        if(!progressDialog.isShowing())
            progressDialog.setMessage("Processing .....");
        progressDialog.show();
        progressDialog.setCancelable(false);
        if(!CheckConnection.isConnected(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            zXingScannerView.resumeCameraPreview(TopUpMain.this);
            return;
        }
        try {
            topUpCode = Integer.parseInt(qrScanCode);
        }
        catch(Exception e){
            AlertDialog.Builder ad = new AlertDialog.Builder(TopUpMain.this);
            ad.setMessage("Top Up Fail. Please use Valid Code").setNegativeButton("Retry", null).setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    zXingScannerView.resumeCameraPreview(TopUpMain.this);
                    progressDialog.dismiss();
                }
            }).create().show();
            return;
        }

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jsonResponse = new JSONObject(response);
                    boolean success = jsonResponse.getBoolean("success");
                    if (success){
                        int jsonCredit = jsonResponse.getInt("credit");
                        double credit = Double.parseDouble(userPreferences.getString("CURRENT_CREDIT","0.00"));
                        credit+=jsonCredit;
                        SharedPreferences.Editor editor = userPreferences.edit();
                        editor.putString("CURRENT_CREDIT",String.format("%.2f",credit));
                        editor.commit();
                        Toast.makeText(getApplicationContext(),"Top Up Success", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        AlertDialog.Builder ad = new AlertDialog.Builder(TopUpMain.this);
                        ad.setMessage("Top Up Fail. Please use Valid Code").setNegativeButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                zXingScannerView.resumeCameraPreview(TopUpMain.this);
                            }
                        }).create().show();
                    }
                }catch (JSONException je){
                    je.printStackTrace();
                }
                if(progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        };
        getQRDetailRequest qrRequest = new getQRDetailRequest(topUpCode,username,responseListener);
        RequestQueue queue = Volley.newRequestQueue(TopUpMain.this);
        queue.add(qrRequest);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(cameraStatus==true)
            zXingScannerView.stopCamera();
    }

    @Override
    public void onBackPressed() {
        if(cameraStatus==true){
            Intent intent = new Intent(this,TopUpMain.class);
            startActivity(intent);
            finish();
        }
        else{
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
