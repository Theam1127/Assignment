package my.edu.tarc.assignment;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText etRUsername = (EditText)findViewById(R.id.etUsername);
        final EditText etRPassword = (EditText)findViewById(R.id.etPassword);
        final EditText etRAge = (EditText)findViewById(R.id.etRAge);
        final EditText etREmail = (EditText)findViewById(R.id.etREmail);
        Button btnRegister = (Button)findViewById(R.id.btnRegister);
        final RadioGroup rgGender = (RadioGroup)findViewById(R.id.rgGender);



        btnRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String username = etRUsername.getText().toString();
                String password = etRPassword.getText().toString();
                int age = Integer.parseInt(etRAge.getText().toString());
                String email = etREmail.getText().toString();
                String gender = ((RadioButton)findViewById(rgGender.getCheckedRadioButtonId())).getText().toString();


                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("success");

                            if (success == true){
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                RegisterActivity.this.startActivity(intent);
                            }
                            else if(success == false){
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setMessage("Register Failed. Username/Email is already exist.").setNegativeButton("Retry", null).create().show();
                            }
                            else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                builder.setMessage("Register Failed").setNegativeButton("Retry", null).create().show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                };

                RegisterRequest registerRequest = new RegisterRequest(username, password, age, email, gender, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);
                }
        });
    }


}
