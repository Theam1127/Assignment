package my.edu.tarc.assignment.TopUp;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import my.edu.tarc.assignment.CheckConnection;
import my.edu.tarc.assignment.R;

public class BankCardActivity extends AppCompatActivity {

    private RadioGroup cardType,amount;

    NotificationCompat.Builder notification;
    private static final int notiID=9999;
    String username;
    SharedPreferences userPreferences;
    private Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card);

        final EditText cardNo,cv;


        cardType = (RadioGroup)findViewById(R.id.cardType);
        amount = (RadioGroup)findViewById(R.id.amount);
        cardNo = (EditText)findViewById(R.id.cardNoInput);
        cv = (EditText)findViewById(R.id.cvCode);
        userPreferences = getSharedPreferences("CURRENT_USER",MODE_PRIVATE);
        username = userPreferences.getString("LOGIN_USER","");
        btn = (Button)findViewById(R.id.bankOK);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Radio Group to get Card Type
                int cardtypeid=0;
                cardtypeid = cardType.getCheckedRadioButtonId();

                //Edit Text to get Card Number
                String etcardno = cardNo.getText().toString();

                //Edit Text to get CV Code
                String etcv = cv.getText().toString();

                //Radio Group to get Amount
                int amountID = amount.getCheckedRadioButtonId();
                int topupAmount=0;
                if(amountID==R.id.amount10)
                    topupAmount=10;
                else if(amountID==R.id.amount20)
                    topupAmount=20;
                else if(amountID==R.id.amount50)
                    topupAmount=50;
                else if(amountID==R.id.amount100)
                    topupAmount=100;

                if( cardtypeid<=0){
                    AlertDialog.Builder adcard = new AlertDialog.Builder(BankCardActivity.this);
                    adcard.setMessage("Please Select Your Card Type").setNegativeButton("OK",null).create().show();
                }
                else if(etcardno.isEmpty()){
                    cardNo.setError(getString(R.string.error_cardno));
                    return;
                }
                else if(etcv.isEmpty()){
                    cv.setError(getString(R.string.error_cv));
                    return;
                }
                else  if(topupAmount<10){
                    AlertDialog.Builder adamt = new AlertDialog.Builder(BankCardActivity.this);
                    adamt.setMessage("Please Select Your Top Up Amount").setNegativeButton("OK",null).create().show();
                }
                else if(cardNo.length()!=16){
                    cardNo.setError(getString(R.string.error_cardnolenght));
                    return;
                }
                else if(etcv.length()!=3){
                    cv.setError(getString(R.string.error_cvlenght));
                    return;
                }
                else{
                    if(!CheckConnection.isConnected(getApplicationContext())) {
                        Toast.makeText(getApplicationContext(), "No network", Toast.LENGTH_LONG).show();
                        return;
                    }
                    Intent intent = new Intent(BankCardActivity.this,TACActivity.class);
                    RandomNumberGenerator tacGenerate = new RandomNumberGenerator();
                    int tac = tacGenerate.randomNumberGenerator();

                    String cardTypes = ((RadioButton)findViewById(cardType.getCheckedRadioButtonId())).getText().toString();
                    int cvCode = Integer.parseInt(cv.getText().toString());
                    //int cardno = Integer.parseInt(cardNo.getText().toString());

                    notification = new NotificationCompat.Builder(BankCardActivity.this);
                    //notification.setAutoCancel(true);
                    notification.setSmallIcon(R.drawable.logo);
                    notification.setTicker("Hello Ticker");
                    notification.setWhen(System.currentTimeMillis());
                    notification.setContentTitle("TAC Code");
                    notification.setContentText("Your TAC Code "+tac);
                    notification.setDefaults(Notification.DEFAULT_SOUND);

                    Intent i = new Intent(BankCardActivity.this,TACDisplayActivity.class);
                    i.putExtra("999",tac);

                    intent.putExtra("1",topupAmount);
                    intent.putExtra("2",username);
                    intent.putExtra("3",tac);
                    intent.putExtra("4",cardTypes);
                    intent.putExtra("5",etcardno);
                    intent.putExtra("6",cvCode);


                    PendingIntent pendingIntent = PendingIntent.getActivity(BankCardActivity.this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);
                    notification.setContentIntent(pendingIntent);

                    //Issue notification
                    NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    nm.notify(notiID,notification.build());

                    startActivity(intent);
                    finish();
                }
            }
        });


    }

    public void cancel(View v){
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,TopUpMain.class);
        startActivity(intent);
        finish();
        overridePendingTransition(R.xml.enter,R.xml.exit);
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
