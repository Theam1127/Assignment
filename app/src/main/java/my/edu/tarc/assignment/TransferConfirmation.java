package my.edu.tarc.assignment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer_confirmation);

        pDialog = new ProgressDialog(this);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            finalUsername = extras.getString("username");
            finalAmount = extras.getInt("amount");
        }

        TextView tvTUsername = (TextView)findViewById(R.id.tvTUsername);
        TextView tvTAmount = (TextView)findViewById(R.id.tvTAmount);
        Button btnConfirm = (Button)findViewById(R.id.btnConfirm);
        Button btnCancel = (Button)findViewById(R.id.btnCancel);

        tvTUsername.setText(finalUsername);
        tvTAmount.setText("RM " + finalAmount + "");
    }

    public void confirmTransfer(View v){
       pDialog.setCancelable(false);
       userPreference = getSharedPreferences("CURRENT_USER", MODE_PRIVATE);
       double userCredit = Double.parseDouble(userPreference.getString("CURRENT_CREDIT", "0.00"));
       String currentUser = userPreference.getString("LOGIN_USER", "");

       userCredit -= finalAmount;

        if (!pDialog.isShowing())
            pDialog.setMessage("Transferring....");
        pDialog.show();

        Response.Listener<String> responseListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(getApplicationContext(), "Transfer Successful! Please check email for receipt!", Toast.LENGTH_LONG).show();

                Intent mainIntent = new Intent(TransferConfirmation.this, MainPage.class);
                startActivity(mainIntent);

                if (pDialog.isShowing())
                    pDialog.dismiss();

            }
        };

        SendUserRequest sendUserRequest = new SendUserRequest(currentUser, userCredit, responseListener);
        RequestQueue queue1 = Volley.newRequestQueue(TransferConfirmation.this);
        queue1.add(sendUserRequest);

        ReceiveUserRequest receiveUserRequest = new ReceiveUserRequest(finalUsername, finalAmount, responseListener);
        RequestQueue queue2 = Volley.newRequestQueue(TransferConfirmation.this);
        queue2.add(receiveUserRequest);
    }

    public void cancelAction(View v){
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle("Are you sure?");
        adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface diaglog, int which){
                Intent mainIntent = new Intent(TransferConfirmation.this, TransferActivity.class);
                startActivity(mainIntent);
            }
        });

        adb.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface diaglog, int which){
                return;
            }
        });

        adb.show();
    }
}
