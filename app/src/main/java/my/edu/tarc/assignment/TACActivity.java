package my.edu.tarc.assignment;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
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


public class TACActivity extends AppCompatActivity {


    private EditText tacInput;
    private int tacInputCode,cardNo,cv,amount,tac;
    private String cardType,username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tac);

        tacInput =(EditText)findViewById(R.id.tacCode);

        Intent intent = getIntent();
        amount = intent.getIntExtra("1",0);
        username = intent.getStringExtra("2");
        tac = intent.getIntExtra("3",0);
        cardType=intent.getStringExtra("4");
        cardNo = intent.getIntExtra("5",0);
        cv = intent.getIntExtra("6",0);
    }
    public void nextPage(View view){
        Intent intent = new Intent(this,TQActivity.class);

        if(tacInput.getText().toString().isEmpty())
            tacInput.setError(getResources().getString(R.string.error_tac));
        else
            tacInputCode = Integer.parseInt(tacInput.getText().toString());
        if(tacInputCode!=tac){
            AlertDialog.Builder ad = new AlertDialog.Builder(this);
            ad.setMessage("Incorrect TAC Code").setNegativeButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent1 = new Intent(TACActivity.this,TopUpMain.class);
                    startActivity(intent1);
                    finish();
                }
            }).create().show();
        }
        else{

            intent.putExtra("1",username);
            intent.putExtra("2",amount);
            intent.putExtra("3",cardType);
            intent.putExtra("4",cardNo);
            intent.putExtra("5",cv);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,TopUpMain.class);
        startActivity(intent);
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
