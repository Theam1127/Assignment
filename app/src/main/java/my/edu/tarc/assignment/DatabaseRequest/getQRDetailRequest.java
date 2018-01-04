package my.edu.tarc.assignment.DatabaseRequest;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JE on 12/27/2017.
 */

public class getQRDetailRequest extends StringRequest {
    private static String LOGIN_REQUEST_URL = "https://yongjin97.000webhostapp.com/UpdateTopUpCode.php";
    private Map<String,String> params;


    public getQRDetailRequest(int qrCode,String username,Response.Listener<String> listener){
        super(Method.POST,LOGIN_REQUEST_URL,listener,null);

        params = new HashMap<>();
        params.put("topUpCode",Integer.toString(qrCode));
        params.put("userName",username.toString());
    }


    @Override
    public Map<String, String> getParams(){
        return params;
    }


}
