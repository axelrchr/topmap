package arocher.fr.topmap.myrequest;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MyRequest {

    private Context context;
    private RequestQueue queue;

    public MyRequest(Context context, RequestQueue queue)
    {
        this.context = context;
        this.queue = queue;
    }

    public void register(final String name, final String firstname, final String dateNaiss, final String mail, final String tel, final String pseudo, final String password, final String passwordVerif, final RegisterCallback callback)
    {

        // URL ANGLET
        String url = "http://192.168.1.14/topmap/register.php";
        // URL TARBES
        //String url = "http://192.168.1.16/topmap/register.php";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    Map<String, String>errors = new HashMap<>();

                    try {
                        JSONObject json = new JSONObject(response);
                        Boolean error = json.getBoolean("error");

                        if(!error){
                            // L'inscription s'est bien deroule
                            callback.onSuccess("Inscription réussi !");
                        }else{
                            JSONObject messages = json.getJSONObject("message");
                            if(messages.has("name")){
                                errors.put("name", messages.getString("name"));
                            }
                            if(messages.has("firstname")){
                                errors.put("firstname", messages.getString("firstname"));
                            }
                            if(messages.has("dateNaiss")){
                                errors.put("dateNaiss", messages.getString("dateNaiss"));
                            }
                            if(messages.has("mail")){
                                errors.put("mail", messages.getString("mail"));
                            }
                            if(messages.has("tel")){
                                errors.put("tel", messages.getString("tel"));
                            }
                            if(messages.has("pseudo")){
                                errors.put("pseudo", messages.getString("pseudo"));
                            }
                            if(messages.has("password")){
                                errors.put("password", messages.getString("password"));
                            }
                            callback.inputErrors(errors);

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                    if(error instanceof NetworkError)
                    {
                        callback.onError("Impossible de se connecter au reseau");
                    }
                    else if (error instanceof VolleyError)
                    {
                        callback.onError("Une erreur est survenue");
                    }


                }
            }){
                @Override
                protected Map<String, String>getParams() throws AuthFailureError {

                    Map<String, String> map = new HashMap<>();
                    map.put("name", name);
                    map.put("firstname", firstname);
                    map.put("dateNaiss", dateNaiss);
                    map.put("mail", mail);
                    map.put("tel", tel);
                    map.put("pseudo", pseudo);
                    map.put("password", password);
                    map.put("passwordVerif", passwordVerif);

                    return map ;
                }
            };

        queue.add(request);
    }

    public interface RegisterCallback{
        void onSuccess(String message);
        void inputErrors(Map<String, String>errors);
        void onError(String message);
    }

    public void connexion(final String mail, final String password, final LoginCallback callback)
    {
        // ANGLET
        String url = "http://192.168.1.14/topmap/login.php";
        // TARBES
        //String url = "http://192.168.1.16/topmap/login.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                JSONObject json = null;
                try {
                    json = new JSONObject(response);
                    Boolean error = json.getBoolean("error");

                    if(!error)
                    {
                        String id = json.getString("id");
                        String mail = json.getString("mail");
                        String pseudo = json.getString("pseudo");
                        // User user = new User(id, mail);
                        callback.onSuccess(id, mail, pseudo);
                    }
                    else
                    {
                        callback.onError(json.getString("message"));
                    }
                } catch (JSONException e) {
                    callback.onError("Une erreur est survenue");
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if(error instanceof NetworkError)
                {
                    callback.onError("Impossible de se connecter au reseau");
                }
                else if (error instanceof VolleyError)
                {
                    callback.onError("Une erreur est survenue");
                }


            }
        }){
            @Override
            protected Map<String, String>getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();
                map.put("mail", mail);
                map.put("password", password);

                return map ;
            }
        };

        queue.add(request);
    }

    public interface LoginCallback
    {
        void onSuccess(String id, String mail, String pseudo);
        //void onSucces(User user);
        void onError(String message);
    }

    public void creerGroupe(final String nom, final CreerGroupeCallback callback)
    {
        // ANGLET
        String url = "http://192.168.1.14/topmap/creerGroupe.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject json = new JSONObject(response);
                    Boolean error = json.getBoolean("error");

                    if(!error){
                        callback.onSuccess("Groupe créée avec succès");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                if(error instanceof NetworkError){
                    callback.onError("Impossible de se connecter au réseau");
                }else if(error instanceof VolleyError){
                    callback.onError("Une erreur s'est produite");
                }
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();
                map.put("nom", nom);

                return map;
            }
        };

        queue.add(request);
    }

    public interface CreerGroupeCallback{
        void onSuccess(String message);
        void onError(String message);
    }

    public void envoyerCoordonnee(final String lat, final String lng)
    {
        String url = "http://192.168.1.14/topmap/coordonnee.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> map = new HashMap<>();
                map.put("lat", lat);
                map.put("lng", lng);
                return map;
            }
        };

        queue.add(request);
    }


}
