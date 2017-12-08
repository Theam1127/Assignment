package my.edu.tarc.assignment;

import android.content.Intent;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ItemDetail extends AppCompatActivity {
    TextView textViewItemID;
    EditText editTextQuantity;
    Item item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        textViewItemID = (TextView)findViewById(R.id.textViewItemID);
        editTextQuantity = (EditText)findViewById(R.id.editTextQuantity);
        item = (Item)getIntent().getSerializableExtra(CodeScanner.GET_QUANTITY);
        textViewItemID.setText(item.getItemName());
    }

    public void confirmAddCart(View view){
        int qtyPurchase = Integer.parseInt(editTextQuantity.getText().toString());
        if(qtyPurchase <= item.getQuantity()) {
            Intent intent = new Intent();
            intent.putExtra(CodeScanner.QUANTITY, qtyPurchase);
            setResult(CodeScanner.REQUEST_ITEM_QUANTITY, intent);
            finish();
        }
    }

    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(this,AddItem.class);
        ItemDetail.this.finish();
        startActivity(intent);
    }
}
