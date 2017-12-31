package my.edu.tarc.assignment;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class TransferActivity extends AppCompatActivity {
    private ProgressDialog pDialog;

    SharedPreferences userPreference;
    public final int REQUEST_CODE_TRANSFER = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        final EditText etTransferUser = (EditText)findViewById(R.id.etTransferUser);
        final RadioGroup rgAmount = (RadioGroup)findViewById(R.id.rgAmount);
        Button btnNext = (Button)findViewById(R.id.btnNext);
        pDialog = new ProgressDialog(this);


        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                final String username = etTransferUser.getText().toString();
                userPreference = getSharedPreferences("CURRENT_USER", MODE_PRIVATE);
                double userCredit = Double.parseDouble(userPreference.getString("CURRENT_CREDIT", "0.00"));
                String transferUser = userPreference.getString("LOGIN_USER", "");
                final int amount = Integer.parseInt(((RadioButton)findViewById(rgAmount.getCheckedRadioButtonId())).getText().toString());

                if(username.isEmpty()){
                    etTransferUser.setError(getString(R.string.error_username));
                    return;
                }

                else if(userCredit < amount){
                    AlertDialog.Builder builder = new AlertDialog.Builder(TransferActivity.this);
                    builder.setMessage("You do not have sufficient amount to transfer! Top up now?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).setNegativeButton("Continue", null).create().show();
                }
                else if(transferUser.equals(username)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(TransferActivity.this);
                    builder.setMessage("You should not transfer to yourself!").setNegativeButton("Retry", null).create().show();
                }

                else {
                    if(!pDialog.isShowing())
                        pDialog.setMessage("Checking...");
                    pDialog.show();

                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");

                                if (success == true) {
                                    Intent confirmationIntent = new Intent(TransferActivity.this, TransferConfirmation.class);
                                    confirmationIntent.putExtra("username", username);
                                    confirmationIntent.putExtra("amount", amount);
                                    TransferActivity.this.startActivityForResult(confirmationIntent, REQUEST_CODE_TRANSFER);
                                }
                                else if (success == false) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(TransferActivity.this);
                                    builder.setMessage("Username not found. Please try again.").setNegativeButton("Retry", null).create().show();
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(TransferActivity.this);
                                    builder.setMessage("Username not found. Please try again.").setNegativeButton("Retry", null).create().show();
                                }

                                if (pDialog.isShowing())
                                    pDialog.dismiss();
                            } catch (JSONException e) {
                                Toast.makeText(getApplicationContext(), "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        }
                    };

                    TransferRequest transferRequest = new TransferRequest(username, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(TransferActivity.this);
                    queue.add(transferRequest);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
            finish();
    }

    @Override
    public void onBackPressed() {
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
