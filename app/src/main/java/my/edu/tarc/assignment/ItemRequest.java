package my.edu.tarc.assignment;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.Request;
import com.android.volley.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yeap Theam on 9/12/2017.
 */

public class ItemRequest extends StringRequest{
    private static final String ITEM_REQUEST_URL = "https://yongjin97.000webhostapp.com/SelectProduct.php";
    private Map<String, String> params;

    public ItemRequest(String itemID, Response.Listener<String> listener){
        super(Request.Method.POST, ITEM_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("productID", itemID);
    }

    @Override
    public Map<String, String> getParams(){
        return params;
    }


}
