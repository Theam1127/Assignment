package my.edu.tarc.assignment.DatabaseRequest;

/**
 * Created by yongj on 31/12/2017.
 */

import java.util.HashMap;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ReceiveUserRequest extends StringRequest {
    private static String TRANSFER_REQUEST_URL = "http://yongjin97.000webhostapp.com/UpdateReceiveUser.php";
    private Map<String, String> params;

    public ReceiveUserRequest(String username, double credit, Response.Listener<String> listener){
        super(Request.Method.POST, TRANSFER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("credit", credit + "");
    }

    @Override
    public Map<String, String> getParams(){
        return params;
    }
}
