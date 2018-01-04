package my.edu.tarc.assignment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class TQActivity extends AppCompatActivity {

    private TextView t1,t2,t3,t4;
    private EditText tacInput;
    private int tacInputCode,cardNo,cv,amount,tac;
    private String cardType,username;
    private Button confirm;
    ProgressDialog progressDialog;
    SharedPreferences userPreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tq);

        progressDialog = new ProgressDialog(this);
        t1 =(TextView)findViewById(R.id.cardTypeIn);
        t2 =(TextView)findViewById(R.id.cardNoIn);
        t3 =(TextView)findViewById(R.id.cvCodeIn);
        t4 =(TextView)findViewById(R.id.amountIn);
        confirm =(Button)findViewById(R.id.backtoMain);


        Intent intent = getIntent();
        username = intent.getStringExtra("1");
        amount = intent.getIntExtra("2",0);
        cardType = intent.getStringExtra("3");
        cardNo = intent.getIntExtra("4",0);
        cv = intent.getIntExtra("5",0);

        t1.setText(cardType);
        t2.setText(""+cardNo);
        t3.setText(""+cv);
        t4.setText(""+amount);

        confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!progressDialog.isShowing())
                        progressDialog.setMessage("Processing .....");
                    progressDialog.show();
                    userPreference = getSharedPreferences("CURRENT_USER",MODE_PRIVATE);
                    double credit = Double.parseDouble(userPreference.getString("CURRENT_CREDIT","0.00"));
                    credit+=amount;
                    SharedPreferences.Editor editor = userPreference.edit();
                    editor.putString("CURRENT_CREDIT",String.format("%.2f",credit));
                    editor.commit();
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try{
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("success");
                                if(success==true){
                                    Toast.makeText(getApplicationContext(),"Top Up Success",Toast.LENGTH_LONG).show();
                                    finish();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(),"Top Up Fail",Toast.LENGTH_LONG).show();
                                    finish();
                                }
                            }catch (JSONException je){
                                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                            }
                            if(progressDialog.isShowing())
                                progressDialog.dismiss();
                        }
                    };
                    BankTopUpRequest bankRequest = new BankTopUpRequest(username,amount,responseListener);
                    RequestQueue queue = Volley.newRequestQueue(TQActivity.this);
                    queue.add(bankRequest);
                }
            });

    }

    public void backToHome(View view){
        Intent intent = new Intent(this,TopUpMain.class);
        startActivity(intent);
        finish();
    }

}
