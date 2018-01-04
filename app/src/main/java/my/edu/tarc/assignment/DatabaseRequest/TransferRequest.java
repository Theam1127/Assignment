package my.edu.tarc.assignment.DatabaseRequest;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class TransferRequest extends StringRequest {
    private static String TRANSFER_REQUEST_URL = "http://yongjin97.000webhostapp.com/Transfer.php";
    private Map<String, String> params;

    public TransferRequest(String username, Response.Listener<String> listener){
        super(Request.Method.POST, TRANSFER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);

    }

    @Override
    public Map<String, String> getParams(){
        return params;
    }
}
