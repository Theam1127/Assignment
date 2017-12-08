package my.edu.tarc.assignment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
public class AddItem extends Fragment {
    public static final int REQUEST_CODE_CONTENT = 0;
    public static final String NEW_ITEM = "my.edu.tarc.assignment.PRODUCTID";
    public static final String NEW_ITEM_QUANTITY = "my.edu.tarc.assignment.QTY";
    TextView textViewPrice;
    double total_price = 0.0;
    ListView cart;
    List<Item> cart_list = null;
    CartAdapter arrayAdapter = null;
    Item item = new Item();
    public AddItem(){};
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View v = inflater.inflate(R.layout.fragment_add_item,container,false);
        cart = (ListView)v.findViewById(R.id.listViewCart);
        cart_list = new ArrayList<Item>();
        arrayAdapter = new CartAdapter(cart_list,getActivity());
        cart.setAdapter(arrayAdapter);
        Button buttonAddItem = (Button)v.findViewById(R.id.buttonAddItem);
        buttonAddItem.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getActivity(),CodeScanner.class);
                startActivityForResult(intent, REQUEST_CODE_CONTENT);
            }
        });
        return v;
    }


    @Override
    public void onResume() {
        super.onResume();
        total_price = 0.0;
        for (int a = 0; a < cart_list.size(); a++)
            total_price += cart_list.get(a).getPrice();
        textViewPrice = (TextView) getActivity().findViewById(R.id.textViewPrice);
        textViewPrice.setText(String.format("%.2f", total_price));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_CONTENT){
            String productName = data.getStringExtra(NEW_ITEM);
            int quantity = data.getIntExtra(NEW_ITEM_QUANTITY,0);
            item = new Item("A0001",productName,quantity,10.50);
            cart_list.add(item);
            arrayAdapter.notifyDataSetChanged();
        }
    }
}
