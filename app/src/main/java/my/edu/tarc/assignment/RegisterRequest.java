package my.edu.tarc.assignment;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {
    private static String REGISTER_REQUEST_URL = "https://yongjin97.000webhostapp.com/Register.php";
    private Map<String, String> params;

    public RegisterRequest(String username, String password, int age, String email, String gender, Response.Listener<String> listener){
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
        params.put("age", age + "");
        params.put("email", email);
        params.put("gender", gender);
    }

    @Override
    public Map<String, String> getParams(){
        return params;
    }
}
