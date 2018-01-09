package my.edu.tarc.assignment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import my.edu.tarc.assignment.DatabaseRequest.RegisterRequest;

public class RegisterActivity extends AppCompatActivity {
    private ProgressDialog pDialog;
    private Calendar calendar, today;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText etRUsername = (EditText) findViewById(R.id.etUsername);
        final EditText etRPassword = (EditText) findViewById(R.id.etPassword);
        final EditText etRBirthday = (EditText) findViewById(R.id.etRBirthday);
        final EditText etREmail = (EditText) findViewById(R.id.etREmail);
        Button btnRegister = (Button) findViewById(R.id.btnRegister);
        final RadioGroup rgGender = (RadioGroup) findViewById(R.id.rgGender);
        pDialog = new ProgressDialog(this);
        calendar = Calendar.getInstance();
        today = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);

                String dateFormat = "MM/dd/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.US);
                etRBirthday.setText(sdf.format(calendar.getTime()));
            }
        };

        etRBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(RegisterActivity.this, date, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etRUsername.getText().toString();
                String password = etRPassword.getText().toString();
                String birthday = etRBirthday.getText().toString();
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
                else if(birthday.isEmpty()) {
                    etRBirthday.setError(getString(R.string.error_birthday));
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
                    int age = today.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
                    if(age<18){
                        android.app.AlertDialog.Builder age_limit = new android.app.AlertDialog.Builder(RegisterActivity.this);
                        age_limit.setMessage("You should be minimum 18 years old to use our application!").setNegativeButton("OK",null).create().show();
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

                    RegisterRequest registerRequest = new RegisterRequest(username, password, birthday, email, gender, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                    queue.add(registerRequest);

                }
            }
        });
    }
}
