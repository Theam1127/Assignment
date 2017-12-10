package my.edu.tarc.assignment;

import android.content.Intent;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ItemDetail extends AppCompatActivity {
    TextView textViewItemID;
    EditText editTextQuantity;
    Item item;
    List<Item> cart;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        textViewItemID = (TextView) findViewById(R.id.textViewItemID);
        editTextQuantity = (EditText) findViewById(R.id.editTextQuantity);
        item = (Item) getIntent().getSerializableExtra(CodeScanner.GET_QUANTITY);
        textViewItemID.setText(item.getItemName());
        editTextQuantity.setText("0");
    }

    public void addQuantity(View view){
        int qty = Integer.parseInt(editTextQuantity.getText().toString());
        qty++;
        editTextQuantity.setText(Integer.toString(qty));
    }

    public void minusQuantity(View view){
        int qty = Integer.parseInt(editTextQuantity.getText().toString());
        if(qty!=0)
            qty--;
        editTextQuantity.setText(Integer.toString(qty));
    }

    public void confirmAddCart(View view){
        int qtyPurchase = Integer.parseInt(editTextQuantity.getText().toString());
        if(qtyPurchase <= item.getQuantity() && qtyPurchase != 0) {
            Intent intent = new Intent();
            intent.putExtra(CodeScanner.QUANTITY, qtyPurchase);
            setResult(CodeScanner.REQUEST_ITEM_QUANTITY, intent);
            finish();
        }
        else if(qtyPurchase ==0)
            Toast.makeText(this,"Quantity should not be 0!",Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Quantity exceeded stock available!", Toast.LENGTH_SHORT).show();
    }

    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(this,AddItem.class);
        ItemDetail.this.finish();
        startActivity(intent);
    }
}
