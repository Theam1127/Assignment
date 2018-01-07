package my.edu.tarc.assignment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import my.edu.tarc.assignment.DatabaseRequest.LoginRequest;
public class LoginActivity extends AppCompatActivity {
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tvRegister = (TextView)findViewById(R.id.tvRegister);
        final EditText etUsername = (EditText)findViewById(R.id.etUsername);
        final EditText etPassword = (EditText)findViewById(R.id.etPassword);
        Button btnLogin = (Button)findViewById(R.id.btnLogin);
        pDialog = new ProgressDialog(this);
        if(!CheckConnection.isConnected(getApplicationContext()))
            Toast.makeText(getApplicationContext(), "No network", Toast.LENGTH_LONG).show();

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!CheckConnection.isConnected(getApplicationContext())) {
                    Toast.makeText(getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                final String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();

                if(username.isEmpty()){
                    etUsername.setError(getString(R.string.error_username));
                    return;
                }
                else if(password.isEmpty()){
                    etPassword.setError(getString(R.string.error_password));
                    return;
                }
                else {
                    if(!CheckConnection.isConnected(getApplicationContext())) {
                        Toast.makeText(getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (!pDialog.isShowing())
                        pDialog.setMessage("Logging in...");
                    pDialog.show();
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");

                                if (success) {
                                    double credit = jsonResponse.getDouble("credit");
                                    Intent loginIntent = new Intent(LoginActivity.this, MainPage.class);
                                    SharedPreferences userPreference = getSharedPreferences("CURRENT_USER",MODE_PRIVATE);
                                    SharedPreferences.Editor editor = userPreference.edit();
                                    editor.putString("LOGIN_USER", username);
                                    editor.putString("CURRENT_CREDIT", String.format("%.2f",credit));
                                    editor.commit();
                                    LoginActivity.this.startActivity(loginIntent);
                                    finish();
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                    builder.setMessage("Login Failed. Incorrect username or password.").setNegativeButton("Retry", null).create().show();
                                }
                                if (pDialog.isShowing())
                                    pDialog.dismiss();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    };

                    LoginRequest loginRequest = new LoginRequest(username, password, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                    queue.add(loginRequest);
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        System.exit(0);
    }
}
