package my.edu.tarc.assignment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yeap Theam on 30/12/2017.
 */

public class CheckoutRequest extends StringRequest {
    private static final String CHECKOUT_REQUEST_URL = "https://yongjin97.000webhostapp.com/CheckOut.php";
    private Map<String, String> params;

    public CheckoutRequest(double amount, String username, Response.Listener<String> listener){
        super(Request.Method.POST, CHECKOUT_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("amount", Double.toString(amount));
        params.put("username", username);
    }

    @Override
    public Map<String, String> getParams(){
        return params;
    }
}
