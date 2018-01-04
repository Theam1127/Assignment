package my.edu.tarc.assignment.DatabaseRequest;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yeap Theam on 11/12/2017.
 */

public class ItemUpdate extends StringRequest {
        private static final String ITEM_REQUEST_URL = "https://yongjin97.000webhostapp.com/UpdateProduct.php";
        private Map<String, String> params;

        public ItemUpdate(String itemID, int itemQty, String shopID, Response.Listener<String> listener){
            super(Request.Method.POST, ITEM_REQUEST_URL, listener, null);
            params = new HashMap<>();
            params.put("productID", itemID);
            params.put("productQty", Integer.toString(itemQty));
            params.put("shopID", shopID);
        }

        @Override
        public Map<String, String> getParams(){
            return params;
        }
    }

