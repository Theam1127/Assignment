package my.edu.tarc.assignment.StartShopping;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import my.edu.tarc.assignment.R;

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

        editTextQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextQuantity.selectAll();
            }
        });
        editTextQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(editTextQuantity.getText().toString().equals("")) {
                    editTextQuantity.setText("0");
                    editTextQuantity.selectAll();
                }
                int qty = Integer.parseInt(editTextQuantity.getText().toString());
                if(qty>item.getQuantity()) {
                    editTextQuantity.setText(Integer.toString(item.getQuantity()));
                    qty=item.getQuantity();
                    Toast.makeText(ItemDetail.this, "Quantity exceeded stock available! "+"("+item.getQuantity()+")", Toast.LENGTH_SHORT).show();
                }
                double price = item.getPrice();
                price *= qty;
                textViewItemTotalPrice.setText(String.format("%.2f", price));
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
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

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
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
