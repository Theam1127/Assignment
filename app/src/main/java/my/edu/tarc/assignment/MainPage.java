package my.edu.tarc.assignment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainPage extends AppCompatActivity{

    public static final int REQUEST_CODE_CONTENT = 0;
    public static final String NEW_ITEM = "my.edu.tarc.assignment.PRODUCTID";
    public static final String NEW_ITEM_QUANTITY = "my.edu.tarc.assignment.QTY";
    ListView cart;
    String[] addCart = new String[]{};
    List<String> cart_list = null;
    ArrayAdapter<String> arrayAdapter = null;
    Item item = new Item();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        cart = (ListView)findViewById(R.id.listViewCart);
        cart_list = new ArrayList<String>(Arrays.asList(addCart));
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,cart_list);
        cart.setAdapter(arrayAdapter);
    }

    public void addItem(View view) {
        Intent intent = new Intent(this,CodeScanner.class);
        startActivityForResult(intent, REQUEST_CODE_CONTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_CONTENT){
            String productName = data.getStringExtra(NEW_ITEM);
            int quantity = data.getIntExtra(NEW_ITEM_QUANTITY,0);
            item = new Item("A0001",productName,quantity,10.50);
            cart_list.add(item.toString());
            arrayAdapter.notifyDataSetChanged();
        }
    }
}
