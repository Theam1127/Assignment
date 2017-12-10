package my.edu.tarc.assignment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yeap Theam on 25/11/2017.
 */

public class CartAdapter extends BaseAdapter implements ListAdapter {
    private List<Item> cart = new ArrayList<Item>();
    private Context context;

    public CartAdapter(List<Item> cart, Context context){
        this.cart = cart;
        this.context = context;
    }

    @Override
    public int getCount() {
        return cart.size();
    }

    @Override
    public Object getItem(int pos){
        return cart.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        View view = convertView;
        if(view==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.cart_list,null);
        }
        TextView cartItem = (TextView)view.findViewById(R.id.textViewItem);
        TextView itemQty = (TextView)view.findViewById(R.id.textViewQuantity);
        TextView itemTotalPrice = (TextView)view.findViewById(R.id.textViewPrice);
        cartItem.setText(cart.get(position).getItemName());
        itemQty.setText(Integer.toString(cart.get(position).getQuantity()));
        itemTotalPrice.setText(String.format("%.2f",cart.get(position).getPrice()));
        Button deleteButton = (Button)view.findViewById(R.id.buttonDelete);
        deleteButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                TextView textViewPrice = (TextView)((Activity)context).findViewById(R.id.textViewTotalPrice);
                double price = Double.parseDouble(textViewPrice.getText().toString());
                price -= cart.get(position).getPrice();
                textViewPrice.setText(String.format("%.2f",price));
                cart.remove(position);
                notifyDataSetChanged();
            }
        });
        LinearLayout oneItem = view.findViewById(R.id.cartItemRow);
        oneItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Item item = cart.get(position);
                Intent intent = new Intent(context, ItemDetail.class);
                intent.putExtra(AddItem.EDIT_ITEM, item);
                ((Activity)context).startActivityForResult(intent, AddItem.REQUEST_ITEM_DETAIL);
            }
        });
        return view;
    }

    public void onActivityResult (int requestCode, int resultCode, Intent data){
        Log.d("CartAdapter", "onActivityResult");
    }

}
