package my.edu.tarc.assignment;

import android.app.ProgressDialog;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import my.edu.tarc.assignment.DatabaseRequest.RegisterRequest;

public class RegisterActivity extends AppCompatActivity {
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText etRUsername = (EditText) findViewById(R.id.etUsername);
        final EditText etRPassword = (EditText) findViewById(R.id.etPassword);
        final EditText etRAge = (EditText) findViewById(R.id.etRAge);
        final EditText etREmail = (EditText) findViewById(R.id.etREmail);
        Button btnRegister = (Button) findViewById(R.id.btnRegister);
        final RadioGroup rgGender = (RadioGroup) findViewById(R.id.rgGender);
        pDialog = new ProgressDialog(this);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etRUsername.getText().toString();
                String password = etRPassword.getText().toString();
                String ageText = etRAge.getText().toString();
                String email = etREmail.getText().toString();
                String gender = ((RadioButton) findViewById(rgGender.getCheckedRadioButtonId())).getText().toString();
                String emailPattern = "[a-zA-z0-a._-]+@[a-z]+\\.+[a-z]+";
                String passwordPattern = ".{8,}";
                if (username.isEmpty()) {
                    etRUsername.setError(getString(R.string.error_username));
                    return;
                } else if (password.isEmpty()) {
                    etRPassword.setError(getString(R.string.error_password));
                    return;
                }
                else if(ageText.isEmpty()) {
                    etRAge.setError(getString(R.string.error_age));
                    return;
                } else if (email.isEmpty()) {
                    etREmail.setError(getString(R.string.error_email));
                    return;
                } else if(!email.matches(emailPattern)){
                    etREmail.setError(getString(R.string.error_email_format));
                    return;
                }
                else if(!password.matches(passwordPattern)){
                    etRPassword.setError(getString(R.string.error_password_format));
                    return;
                }
                else {
                    int age = Integer.parseInt(ageText);
                    if(age<18){
                        etRAge.setError(getString(R.string.error_age_minimum));
                        return;
                    }
                    if (!pDialog.isShowing())
                        pDialog.setMessage("Registering...");
                    pDialog.show();
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");

                                if (success == true) {
                                    Toast.makeText(getApplicationContext(), "Register successful", Toast.LENGTH_LONG).show();
                                    finish();
                                } else if (success == false) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                    builder.setMessage("Register Failed. Username/Email is already exist.").setNegativeButton("Retry", null).create().show();
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                    builder.setMessage("Register Failed").setNegativeButton("Retry", null).create().show();
                                }

                                if (pDialog.isShowing())
                                    pDialog.dismiss();
                            } catch (JSONException e) {
                                Toast.makeText(getApplicationContext(), "Error:" + e.getMessage(), Toast.LENGTH_LONG).show();
                            }

                        }
                    };

                    RegisterRequest registerRequest = new RegisterRequest(username, password, age, email, gender, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                    queue.add(registerRequest);

                }
            }
        });
    }

}
