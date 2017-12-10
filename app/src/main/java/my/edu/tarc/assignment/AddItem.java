package my.edu.tarc.assignment;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
public class AddItem extends Fragment {
    public static final int REQUEST_CODE_CONTENT = 0;
    public static final int REQUEST_ITEM_DETAIL = 1;
    public static final String NEW_ITEM = "my.edu.tarc.assignment.PRODUCTID";
    public static final String EDIT_ITEM = "my.edu.tarc.assignment.EDITITEM";
    public static final String EDITED_ITEM = "my.edu.tarc.assignment.EDITEDITEM";
    TextView textViewTotalPrice;
    double total_price = 0.0;
    ListView cart;
    List<Item> cart_list = null;
    CartAdapter arrayAdapter = null;
    Item item = new Item();

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
        textViewTotalPrice = (TextView) getActivity().findViewById(R.id.textViewTotalPrice);
        textViewTotalPrice.setText(String.format("%.2f", total_price));
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == REQUEST_CODE_CONTENT){
            super.onActivityResult(requestCode, resultCode, data);
            item = (Item)data.getSerializableExtra(NEW_ITEM);
            boolean exist = false;
            for(int a=0;a<cart_list.size();a++)
                if(cart_list.get(a).getItemID().equals(item.getItemID())) {
                    exist = true;
                    cart_list.get(a).setQuantity(cart_list.get(a).getQuantity() + item.getQuantity());
                    cart_list.get(a).setPrice(cart_list.get(a).getPrice()+item.getPrice());
                }
                if(exist == false)
                    cart_list.add(item);
            arrayAdapter.notifyDataSetChanged();
        }
        else if(requestCode == REQUEST_ITEM_DETAIL){
            arrayAdapter.onActivityResult(requestCode, resultCode, data);
            item = (Item)data.getSerializableExtra(EDITED_ITEM);
            for(int a=0;a<cart_list.size();a++)
                if(cart_list.get(a).getItemID().equals(item.getItemID())) {
                    cart_list.get(a).setQuantity(item.getQuantity());
                    cart_list.get(a).setPrice(item.getPrice());
                }
                arrayAdapter.notifyDataSetChanged();
        }
    }


}
