package my.edu.tarc.assignment;

import android.content.Intent;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ItemDetail extends AppCompatActivity {
    TextView textViewItemID, textViewItemPrice, textViewItemTotalPrice;
    EditText editTextQuantity;
    Item item, editItem;
    List<Item> cart;
    String tvItemName;
    Double tvPrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);
        textViewItemID = (TextView) findViewById(R.id.textViewItemID);
        editTextQuantity = (EditText) findViewById(R.id.editTextQuantity);
        textViewItemPrice = (TextView) findViewById(R.id.textViewItemPrice);
        textViewItemTotalPrice = (TextView)findViewById(R.id.textViewTotalCheckOut);
        item = (Item) getIntent().getSerializableExtra(CodeScanner.GET_QUANTITY);
        int currentQuantity = 0;
        if(item!=null) {
            tvItemName = item.getItemName();
            tvPrice = item.getPrice();
        }
        else {
            editItem = (Item) getIntent().getSerializableExtra(AddItem.EDIT_ITEM);
            item = (Item) getIntent().getSerializableExtra(AddItem.DB_ITEM);
            currentQuantity = editItem.getQuantity();
            tvItemName = editItem.getItemName();
            tvPrice = item.getPrice();
        }
        textViewItemID.setText(tvItemName);
        textViewItemPrice.setText(String.format("%.2f",tvPrice));
        textViewItemTotalPrice.setText(String.format("%.2f", tvPrice*currentQuantity));
        editTextQuantity.setText(Integer.toString(currentQuantity));
    }

    public void addQuantity(View view){
        int qty = Integer.parseInt(editTextQuantity.getText().toString());
        double price = item.getPrice();
        qty++;
        price*=qty;
        editTextQuantity.setText(Integer.toString(qty));
        textViewItemTotalPrice.setText(String.format("%.2f",price));
    }

    public void minusQuantity(View view){
        int qty = Integer.parseInt(editTextQuantity.getText().toString());
        double price = item.getPrice();
        if(qty!=0)
            qty--;
        price*=qty;
        editTextQuantity.setText(Integer.toString(qty));
        textViewItemTotalPrice.setText(String.format("%.2f",price));
    }

    public void confirmAddCart(View view){
        int qtyPurchase = Integer.parseInt(editTextQuantity.getText().toString());
        if(qtyPurchase <= item.getQuantity() && qtyPurchase != 0) {
            Intent intent = new Intent();
            if(editItem!=null) {
                editItem.setPrice(Double.parseDouble(textViewItemTotalPrice.getText().toString()));
                editItem.setQuantity(qtyPurchase);
                intent.putExtra(AddItem.EDITED_ITEM, editItem);
                setResult(AddItem.REQUEST_ITEM_DETAIL, intent);
            }
            else {
                intent.putExtra(CodeScanner.QUANTITY, qtyPurchase);
                setResult(CodeScanner.REQUEST_ITEM_QUANTITY, intent);
            }
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
