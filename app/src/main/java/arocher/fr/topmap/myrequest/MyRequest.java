package arocher.fr.topmap.myrequest;

import android.content.Context;
import android.util.Log;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class MyRequest {

    private Context context;
    private RequestQueue queue;

    public MyRequest(Context context, RequestQueue queue){
        this.context = context;
        this.queue = queue;
    }

    public void register(final String name, final String firstname, final String dateNaiss, final String mail, final String tel, final String pseudo, final String password, final String passwordVerif, final RegisterCallback callback) {
        String url = "https://topmap.alwaysdata.net/register.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Map<String, String>errors = new HashMap<>();
                    try {
                        JSONObject json = new JSONObject(response);
                        boolean error = json.getBoolean("error");
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
                    if(error instanceof NetworkError) {
                        callback.onError("Impossible de se connecter au reseau");
                    }
                    else if (error instanceof VolleyError) {
                        callback.onError("Une erreur est survenue");
                    }
                }
            }){
                @Override
                protected Map<String, String>getParams() {
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

    public void connexion(final String mail, final String password, final LoginCallback callback) {
        String url = "https://topmap.alwaysdata.net/login.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response)
            {
                try {
                    JSONObject json = new JSONObject(response);
                    boolean error = json.getBoolean("error");
                    if(!error) {
                        String id = json.getString("id");
                        String mail = json.getString("mail");
                        String pseudo = json.getString("pseudo");
                        callback.onSuccess(id, mail, pseudo);
                    }
                    else {
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
                if(error instanceof NetworkError) {
                    callback.onError("Impossible de se connecter au reseau");
                }
                else if (error instanceof VolleyError) {
                    callback.onError("Une erreur est survenue");
                }
            }
        }){
            @Override
            protected Map<String, String>getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("mail", mail);
                map.put("password", password);
                return map ;
            }
        };
        queue.add(request);
    }

    public interface LoginCallback {
        void onSuccess(String id, String mail, String pseudo);
        void onError(String message);
    }

    public void creerGroupe(final String nom,final String id, final CreerGroupeCallback callback) {
        String url = "https://topmap.alwaysdata.net/creerGroupe.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    boolean error = json.getBoolean("error");
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
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("nom", nom);
                map.put("id", id);
                return map;
            }
        };
        queue.add(request);
    }

    public interface CreerGroupeCallback{
        void onSuccess(String message);
        void onError(String message);
    }

    public void envoyerCoordonnee(final String lat, final String lng, final String id) {
        String url = "https://topmap.alwaysdata.net/position.php";

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
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("lat", lat);
                map.put("lng", lng);
                map.put("id", id);
                return map;
            }
        };
        queue.add(request);
    }

    public void recupGroupe (final String id,final recupGroupeCallback callback) {
        String url = "https://topmap.alwaysdata.net/recupGroupe.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    int nbGroupe = 0;
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray nomArray = jsonObject.getJSONArray("nom");
                    for (int i = 0; i < nomArray.length(); i++) {
                        callback.onSuccess(String.valueOf(nomArray.get(i)), nbGroupe);
                        nbGroupe++;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("id", id);
                return map;
            }
        };
        queue.add(request);
    }

    public interface recupGroupeCallback {
        void onSuccess(String nom, int nbGroupe);
    }

    public void recevoirCoordonnee(final String nom, final recevoirCoordonneeCallback callback) {
        String url = "https://topmap.alwaysdata.net/recupPos.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray latArray = jsonObject.getJSONArray("lat");
                    JSONArray lngArray = jsonObject.getJSONArray("lng");
                    JSONArray pseudoArray = jsonObject.getJSONArray("pseudo");
                    for (int i = 0; i < latArray.length(); i++) {
                        callback.onSuccess(Double.parseDouble((String) latArray.get(i)), Double.parseDouble((String) lngArray.get(i)), (String) pseudoArray.get(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String>getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("nom", nom);
                return map ;
            }
        };
        queue.add(request);
    }

    public interface recevoirCoordonneeCallback {
        void onSuccess(double lat, double lng, String pseudo);
    }

    public void maPos(final String id, final maPosCallback callback) {
        String url = "https://topmap.alwaysdata.net/maPos.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String lat = jsonObject.getString("lat");
                    String lng = jsonObject.getString("lng");
                    callback.onSuccess(Double.parseDouble(lat), Double.parseDouble(lng));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("id", id);
                return map;
            }
        };
        queue.add(request);
    }

    public interface maPosCallback {
        void onSuccess(double lat, double lng);
    }

    public void recupMembres (final String nom,final recupMembresCallback callback) {
        String url = "https://topmap.alwaysdata.net/membreGroupe.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    int nbMembres = 0;
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray membresArray = jsonObject.getJSONArray("pseudo");
                    for (int i = 0; i < membresArray.length(); i++) {
                        Log.d("APP", String.valueOf(membresArray.get(i)) + " " + nbMembres);
                        callback.onSuccess(String.valueOf(membresArray.get(i)), nbMembres);
                        nbMembres++;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("nom", nom);
                return map;
            }
        };
        queue.add(request);
    }

    public interface recupMembresCallback {
        void onSuccess(String pseudo, int nbGroupe);
    }

    public void quitterGroupe (final String nom, final String id,final quitterGroupeCallback callback) {
        String url = "https://topmap.alwaysdata.net/quitterGroupe.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    callback.onSucces(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> map = new HashMap<>();
                map.put("nom", nom);
                map.put("id", id);
                return map;
            }
        };
        queue.add(request);
    }

    public interface quitterGroupeCallback {
        void onSucces(String message);
    }
}
