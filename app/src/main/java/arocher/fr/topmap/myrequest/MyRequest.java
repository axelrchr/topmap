package arocher.fr.topmap.myrequest;

import android.content.Context;
import android.util.Log;
import android.widget.RelativeLayout;

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

    /**
     * Fonction dans laquelle se trouve le lien vers le script php ou se trouve la requête qui permet de créer un compte
     * Les paramètres sont récupérés de l'activity RegisterActivity dans laquelle est appelée cette fonction
     * @param name : Nom de l'utilisateur
     * @param firstname : Prénom de l'utilisateur
     * @param dateNaiss : Date de naissance de l'utilisateur
     * @param mail : Adresse mail de l'utilisateur
     * @param tel : Numéro de téléphone de l'utilisateur
     * @param pseudo : Pseudo de l'utilisateur
     * @param password : Mot de passe de l'utilisateur
     * @param passwordVerif : Vérification du mot de passe, doit être identique au mot de passe
     * @param callback Interface qui retourne si la requête a connu des erreurs ou non
     */
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
        /**
         * Si le script retourne error = false
         * @param message : Retourne le message "Inscription réussie"
         */
        void onSuccess(String message);

        /** Si le script retourne error = true
         * @param errors : Retourne une map qui contient un message d'erreur en fonction du champs ou se trouve l'erreur
         *               exemple : "Ce pseudo est déjà utilisé"
         */
        void inputErrors(Map<String, String>errors);

        /** Dans le cas ou l'erreur provient de Volley
         * @param message :Affiche un message en fonction de l'erreur
         *                exemple : "Connexion au réseau impossible"
         */
        void onError(String message);
    }

    /**
     * Fonction dans laquelle se trouve le lien vers le script php ou se trouve la requête qui permet de se connecter
     * Les paramètres sont récupérés de l'activity ConnexionActivity dans laquelle est appelée cette fonction
     * @param mail : Adresse mail de l'utilisateur
     * @param password : Mot de passe de l'utilisateur
     * @param callback : Interface qui retourne si la requête a connu des erreurs ou non
     */
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
        /**
         * Si le script retourne error = false
         * Retourne l'ID, le mail et le pseudo de l'utilisateur
         * @param id : ID de l'utilisateur dans la base de données
         * @param mail : Mail de l'utilisateur
         * @param pseudo : Pseudo de l'utilisateur
         */
        void onSuccess(String id, String mail, String pseudo);

        /**
         * Dans le cas ou l'erreur provient de Volley
         * @param message : Affiche un message en fonction de l'erreur
         *                exemple : "Connexion au réseau impossible"
         */
        void onError(String message);
    }

    /**
     * Fonction dans laquelle se trouve le lien vers le script php ou se trouve la requête qui permet de se créer un groupe
     * Les paramètres sont récupérés de l'activity CreerGroupeActivity dans laquelle est appelée cette fonction
     * @param nom : Nom du groupe choisi par l'utilisateur
     * @param id : ID de l'utilisateur
     * @param callback : Interface qui retourne si la requête a connu des erreurs ou non
     */
    public void creerGroupe(final String nom,final String id, final CreerGroupeCallback callback) {
        String url = "https://topmap.alwaysdata.net/creerGroupe.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject json = new JSONObject(response);
                    boolean error = json.getBoolean("error");
                    String message = json.getString("message");
                    if(!error){
                        callback.onSuccess(message);
                    }
                    else
                    {
                        callback.onError(message);
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
        /**
         * Si le script retourne error = false
         * @param message : Retourne le message "Groupe créer avec succès"
         */
        void onSuccess(String message);

        /**
         * Dans le cas ou l'erreur provient de Volley
         * @param message : Affiche un message en fonction de l'erreur
         *                 exemple : "Connexion au réseau impossible"
         */
        void onError(String message);
    }

    /**
     * Fonction dans laquelle se trouve le lien vers le script php ou se trouve la requête qui permet d'envoyer ses coordonées
     * actuelles à la base de données
     * Les paramètres sont récupérés de l'activity CarteActivity dans laquelle est appelée cette fonction
     * @param lat : Latitude actuelle (coordonnée GPS)
     * @param lng : Longitude actuelle (coordonnée GPS)
     * @param id : ID de l'utilisateur
     */
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

    /**
     * Fonction dans laquelle se trouve le lien vers le script php ou se trouve la requête qui permet de récupérer la liste des
     * nom de groupe dans lesquels se trouve actuellement l'utilisateur
     * Les paramètres sont récupérés des activity CarteActivity et MesGroupesActivity dans lesquelles sont appelée cette fonction
     * @param id : ID de l'utilisateur
     * @param callback : Interface qui retourne si la requête a connu des erreurs ou non
     */
    public void recupGroupe (final String id,final recupGroupeCallback callback) {
        String url = "https://topmap.alwaysdata.net/recupGroupe.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    int nbGroupe = 0;
                    JSONObject jsonObject = new JSONObject(response);
                    if(String.valueOf(response).equals("{\"nom\":null}")){
                        callback.estVide();
                    }
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
        /**
         * Si le script retourne error = false
         * Dans la fonction on parcours tous les noms de groupe trouvé, a chaque itération la fonction callback retourne
         * le nom du groupe et son numéro dans la lise
         * @param nom : Nom du groupe
         * @param nbGroupe : Nombre de groupe
         */
        void onSuccess(String nom, int nbGroupe);
        void estVide();
    }

    /**
     * Fonction dans laquelle se trouve le lien vers le script php ou se trouve la requête qui permet de récupérer les coordonées
     * GPS de tous les membres d'un groupe
     * Les paramètres sont récupérés de l'activity CarteActivity dans laquelle est appelée cette fonction
     * @param nom : Nom du groupe
     * @param callback : Interface qui retourne si la requête a connu des erreurs ou non
     */
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
        /**
         * Si le script retourne error = false
         * Dans la fonction on parcours tous les couples lat/lng trouvés, a chaque itération la fonction callback retourne
         * la latitude, la longitude et le pseudo correspondant
         * @param lat : Latitude (coordonnée GPS)
         * @param lng : Longitude (coordonnée GPS)
         * @param pseudo : Pseudo de l'utilisateur correspondant a lat et lng
         */
        void onSuccess(double lat, double lng, String pseudo);
    }

    /**
     * Fonction dans laquelle se trouve le lien vers le script php ou se trouve la requête qui permet de récupérer ses coordonées
     * actuelles dans la base de donnée
     * Les paramètres sont récupérés de l'activity CarteActivity dans laquelle est appelée cette fonction
     * @param id : ID de l'utilisateur
     * @param callback : Interface qui retourne si la requête a connu des erreurs ou non
     */
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
        /**
         * Si le script retourne error = false
         * Retourne la latitude et longitude récupéré dans le script
         * @param lat : Latitude (coordonnée GPS)
         * @param lng : Longitude (coordonnée GPS)
         */
        void onSuccess(double lat, double lng);
    }

    /**
     * Fonction dans laquelle se trouve le lien vers le script php ou se trouve la requête qui permet de récupérer les pseudos des
     * membres d'un groupe
     * Les paramètres sont récupérés de l'activity MesGroupesActivity dans laquelle est appelée cette fonction
     * @param nom : Nom du groupe
     * @param callback : Interface qui retourne si la requête a connu des erreurs ou non
     */
    public void recupMembres (final String nom, final recupMembresCallback callback) {
        String url = "https://topmap.alwaysdata.net/membreGroupe.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    int nbMembres = 0;
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray membresArray = jsonObject.getJSONArray("pseudo");
                    JSONArray estChefArray = jsonObject.getJSONArray("estChef");
                    for (int i = 0; i < membresArray.length(); i++) {
                        callback.onSuccess(String.valueOf(membresArray.get(i)),String.valueOf(estChefArray.get(i)), nbMembres);
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
        /**
         * Si le script retourne error = false
         * Dans la fonction on parcours tous les pseudo trouvé, a chaque itération la fonction callback retourne
         * le pseudo et son numéro dans la liste
         * @param pseudo : Pseudo des membres du groupe
         * @param nbMembres : Nombre de membre
         */
        void onSuccess(String pseudo, String estChef, int nbMembres);
    }

    /**
     * Fonction dans laquelle se trouve le lien vers le script php ou se trouve la requête qui permet de quitter un groupe
     * Si l'utilisateur qui quitte le groupe était la derniere personne dedans alors ça supprime le groupe
     * Les paramètres sont récupérés de l'activity MesGroupesActivity dans laquelle est appelée cette fonction
     * @param nom : Nom du groupe
     * @param id : ID de l'utilisateur
     * @param callback : Interface qui retourne si la requête a connu des erreurs ou non
     */
    public void quitterGroupe (final String nom, final String id,final quitterGroupeCallback callback) {
        String url = "https://topmap.alwaysdata.net/quitterGroupe.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    boolean error = jsonObject.getBoolean("error");
                    if(!error) {
                        callback.onSucces(message);
                    }else{
                        callback.onError(message);
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
                map.put("id", id);
                return map;
            }
        };
        queue.add(request);
    }

    public interface quitterGroupeCallback {
        /**
         * Si le script retourne error = false
         * @param message : Si le groupe est supprimé le message sera : "Groupe supprimé"
         *                sinon : "Groupe quitté"
         */
        void onSucces(String message);
        void onError(String message);

    }

    /**
     * Fonction dans laquelle se trouve le lien vers le script php ou se trouve la requête qui permet d'ajouter un membre dans un groupe
     * Les paramètres sont récupérés de l'activity MesGroupesActivity dans laquelle est appelée cette fonction
     * @param pseudo : Pseudo de l'utilisateur qui va être ajouté au groupe
     * @param nom : Nom du groupe dans lequel l'utilisateur veut ajouter un membre
     * @param callback : Interface qui retourne si la requete a connu des erreurs ou non
     */
    public void ajouterMembre(final String pseudo, final String nom, final ajouterMembreCallback callback){
        String url = "https://topmap.alwaysdata.net/ajouterMembre.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    boolean error = jsonObject.getBoolean("error");
                    if(!error)
                    {
                        callback.onSuccess(message);
                    }
                    else
                    {
                        callback.onError(message);
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
                map.put("pseudo", pseudo);
                map.put("nom", nom);
                return map;

            }
        };
        queue.add(request);

    }

    public interface ajouterMembreCallback{
        /**
         * Si le script retourne error = false
         * @param message : Affiche le message " 'pseudo' a été ajouté au groupe 'groupe' "
         */
        void onSuccess(String message);

        /**
         * Si le script retourne error = true car l'erreur vient de la saisie du pseudo
         * @param message : Affiche le message "Ce pseudo n'existe pas"
         */
        void onError(String message);
    }

    /**
     * Supprime le membre selectionné du groupe selectionné
     * Seul le chef du groupe peut supprimer un membre
     * @param pseudo : Pseudo du membre a supprimer
     * @param nomGroupe : Nom du groupe concerné
     * @param callback
     */
    public void supprimerMembre(final String pseudo,final String nomGroupe,final supprimerMembreCallback callback){
        String url = "https://topmap.alwaysdata.net/supprimerMembre.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    boolean error = jsonObject.getBoolean("error");
                    Log.d("APP", String.valueOf(jsonObject));
                    if(!error)
                    {
                        callback.onSuccess(message);
                    }
                    else
                    {
                        callback.onError(message);
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
                map.put("pseudo", pseudo);
                map.put("nomGroupe", nomGroupe);
                Log.d("APP", String.valueOf(map));
                return map;

            }
        };
        queue.add(request);
    }

    public interface supprimerMembreCallback{
        void onSuccess(String message);
        void onError(String message);
    }

    /**
     * Récupère la liste d'amis de l'utilisateur connecté
     * @param id : Id de l'utilisateur connecté qui veut récupéré sa liste d'amis
     * @param callback
     */
    public void recupAmis (final String id,final recupAmisCallback callback) {
        String url = "https://topmap.alwaysdata.net/recupAmi.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    int nbAmis = 0;
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray pseudoArray = jsonObject.getJSONArray("pseudo");
                    for (int i = 0; i < pseudoArray.length(); i++) {
                        callback.onSuccess(String.valueOf(pseudoArray.get(i)), nbAmis);
                        nbAmis++;
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

    public interface recupAmisCallback {
        /**
         * Si le script retourne error = false
         * Dans la fonction on parcours tous les pseudos des amis trouvés, a chaque itération la fonction callback retourne
         * le pseudo et son numéro dans la lise
         * @param pseudo : Pseudo de l'ami
         * @param nbAmis : Nombre d'amis
         */
        void onSuccess(String pseudo, int nbAmis);
        void estVide();
    }

    /**
     * Ajoute un ami dans la liste d'ami
     * @param pseudo : Pseudo de la personne a ajouter dans la liste
     * @param id : Id de l'utilisateur connecté qui veut ajouter un ami
     * @param callback
     */
    public void ajouterAmi(final String pseudo, final String id, final ajouterAmiCallback callback){
        String url = "https://topmap.alwaysdata.net/ajouterAmi.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    boolean error = jsonObject.getBoolean("error");
                    if(!error)
                    {
                        callback.onSuccess(message);
                    }
                    else
                    {
                        callback.onError(message);
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
                map.put("pseudo", pseudo);
                map.put("idUsers1", id);
                return map;

            }
        };
        queue.add(request);

    }

    public interface ajouterAmiCallback{
        /**
         * Si le script retourne error = false
         * @param message : Affiche le message " 'pseudo' a été ajouté à votre liste d'ami"
         */
        void onSuccess(String message);

        /**
         * Si le script retourne error = true car l'erreur vient de la saisie du pseudo
         * @param message : Affiche le message "Ce pseudo n'existe pas"
         */
        void onError(String message);
    }

    /**
     * Supprime l'ami selectionné de la liste d'amis
     * @param pseudo : Pseudo de l'ami a supprimer
     * @param id : Id de l'utilisateur connecté
     * @param callback
     */
    public void supprimerAmi(final String pseudo,final String id,final supprimerAmiCallback callback){
        String url = "https://topmap.alwaysdata.net/supprimerAmi.php";

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String message = jsonObject.getString("message");
                    boolean error = jsonObject.getBoolean("error");
                    if(!error)
                    {
                        callback.onSuccess(message);
                    }
                    else
                    {
                        callback.onError(message);
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
                map.put("pseudo", pseudo);
                map.put("idUsers1", id);
                return map;

            }
        };
        queue.add(request);
    }

    public interface supprimerAmiCallback{
        void onSuccess(String message);
        void onError(String message);
    }
}

