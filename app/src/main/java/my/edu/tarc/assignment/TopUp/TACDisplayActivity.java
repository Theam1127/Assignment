package my.edu.tarc.assignment.TopUp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import my.edu.tarc.assignment.R;

public class TACDisplayActivity extends AppCompatActivity {

    private TextView t8;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tacdisplay);

        Intent intent = getIntent();

        t8 = (TextView)findViewById(R.id.tacDisplay);

        int tac = intent.getIntExtra("999",0);

        t8.setText("Your TAC Code "+tac);

    }
}
