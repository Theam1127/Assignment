package my.edu.tarc.assignment.DatabaseRequest;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JE on 12/31/2017.
 */

public class BankTopUpRequest extends StringRequest{
    private static String TopUpURL = "https://yongjin97.000webhostapp.com/BankUpdateUserCredit.php";
    private Map<String,String> params;

    public BankTopUpRequest(String username,int amount, Response.Listener<String> listener) {
        super(Method.POST,TopUpURL,listener,null);

        params = new HashMap<>();
        params.put("username",username.toString());
        params.put("amount",Integer.toString(amount));

    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}
