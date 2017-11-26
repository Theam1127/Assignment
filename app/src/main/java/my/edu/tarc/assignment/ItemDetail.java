package my.edu.tarc.assignment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ItemDetail extends AppCompatActivity {
    TextView textViewItemName;
    EditText editTextQuantity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        textViewItemName = (TextView)findViewById(R.id.textViewItemName);
        editTextQuantity = (EditText)findViewById(R.id.editTextQuantity);
        Intent intent = getIntent();
        String item = intent.getStringExtra(CodeScanner.GET_QUANTITY);
        textViewItemName.setText(item);
    }

    public void confirmAddCart(View view){
        Intent intent = new Intent();
        intent.putExtra(CodeScanner.QUANTITY,Integer.parseInt(editTextQuantity.getText().toString()));
        setResult(CodeScanner.REQUEST_ITEM_QUANTITY,intent);
        finish();
    }

    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(this,MainPage.class);
        ItemDetail.this.finish();
        startActivity(intent);
    }
}
