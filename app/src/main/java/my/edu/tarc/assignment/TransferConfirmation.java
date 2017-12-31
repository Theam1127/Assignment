package my.edu.tarc.assignment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class TransferConfirmation extends AppCompatActivity {
    SharedPreferences userPreference;
    String finalUsername;
    int finalAmount;
    private ProgressDialog pDialog;
    double userCredit;
    String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_confirmation);

        pDialog = new ProgressDialog(this);
        userPreference = getSharedPreferences("CURRENT_USER", MODE_PRIVATE);
        userCredit = Double.parseDouble(userPreference.getString("CURRENT_CREDIT", "0.00"));
        currentUser = userPreference.getString("LOGIN_USER", "");
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            finalUsername = extras.getString("username");
            finalAmount = extras.getInt("amount");
        }

        TextView tvTUsername = (TextView)findViewById(R.id.tvTUsername);
        TextView tvTAmount = (TextView)findViewById(R.id.tvTAmount);

        tvTUsername.setText(finalUsername);
        tvTAmount.setText("RM " + finalAmount + "");
    }

    public void confirmTransfer(View v){
       pDialog.setCancelable(false);
        userCredit -= finalAmount;
        SharedPreferences.Editor editor = userPreference.edit();
        editor.putString("CURRENT_CREDIT", String.format("%.2f", userCredit));
        editor.commit();

        if (!pDialog.isShowing())
            pDialog.setMessage("Transferring....");
        pDialog.show();


        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Transfer Successful! Please check email for receipt!", Toast.LENGTH_LONG).show();
                setResult(RESULT_OK);
                finish();

                if (pDialog.isShowing())
                    pDialog.dismiss();

            }
        };

        ReceiveUserRequest receiveUserRequest = new ReceiveUserRequest(finalUsername, finalAmount, responseListener);
        RequestQueue queue2 = Volley.newRequestQueue(TransferConfirmation.this);
        queue2.add(receiveUserRequest);

        SendUserRequest sendUserRequest = new SendUserRequest(currentUser, userCredit, responseListener);
        RequestQueue queue1 = Volley.newRequestQueue(TransferConfirmation.this);
        queue1.add(sendUserRequest);


    }

    public void cancelAction(View v){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Are you sure?");
        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface diaglog, int which){
                finish();
            }
        });
        adb.setNegativeButton("No",null);
        adb.show();
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
