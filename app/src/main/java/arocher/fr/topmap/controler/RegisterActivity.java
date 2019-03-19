package arocher.fr.topmap.controler;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import java.util.Map;
import arocher.fr.topmap.R;
import arocher.fr.topmap.VolleySingleton;
import arocher.fr.topmap.myrequest.MyRequest;

public class RegisterActivity extends AppCompatActivity {

    private TextInputLayout til_name, til_firstname, til_dateNaiss, til_mail, til_tel, til_pseudo, til_password, til_password_verif;
    private ProgressBar pb_loader;
    private MyRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button btn_send = findViewById(R.id.btn_send);
        til_name = findViewById(R.id.til_name);
        til_firstname = findViewById(R.id.til_firstname);
        til_dateNaiss = findViewById(R.id.til_dateNaiss);
        til_mail = findViewById(R.id.til_mail);
        til_tel = findViewById(R.id.til_tel);
        til_pseudo = findViewById(R.id.til_pseudo);
        til_password = findViewById(R.id.til_password);
        til_password_verif = findViewById(R.id.til_password_verif);
        pb_loader = findViewById(R.id.pb_loader);

        RequestQueue queue = VolleySingleton.getInstance(this).getRequestQueue();
        request = new MyRequest(this, queue);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pb_loader.setVisibility(View.VISIBLE);
                String name = til_name.getEditText().getText().toString().trim();
                String firstname = til_firstname.getEditText().getText().toString().trim();
                String dateNaiss = til_dateNaiss.getEditText().getText().toString().trim();
                String mail = til_mail.getEditText().getText().toString().trim();
                String tel = til_tel.getEditText().getText().toString().trim();
                String pseudo = til_pseudo.getEditText().getText().toString().trim();
                String password = til_password.getEditText().getText().toString().trim();
                String passwordVerif = til_password_verif.getEditText().getText().toString().trim();

                if (name.length() > 0 && firstname.length() > 0 && dateNaiss.length() > 0 && mail.length() > 0 && tel.length() > 0 && pseudo.length() > 0 && password.length() > 0 && passwordVerif.length() > 0) {
                    request.register(name, firstname, dateNaiss, mail, tel, pseudo, password, passwordVerif, new MyRequest.RegisterCallback() {
                        @Override
                        public void onSuccess(String message) {
                            pb_loader.setVisibility(View.GONE);
                            Intent intent = new Intent(getApplicationContext(), ConnexionActivity.class);
                            intent.putExtra("REGISTER", message);
                            startActivity(intent);
                            finish();
                        }
                        @Override
                        public void inputErrors(Map<String, String> errors) {
                            pb_loader.setVisibility(View.GONE);
                            //name
                            if (errors.get("name") != null) {
                                til_name.setError(errors.get("name"));
                            } else {
                                til_name.setErrorEnabled(false);
                            }
                            //fisrtname
                            if (errors.get("firstname") != null) {
                                til_firstname.setError(errors.get("firstname"));
                            } else {
                                til_firstname.setErrorEnabled(false);
                            }
                            //dateNaiss
                            if (errors.get("dateNaiss") != null) {
                                til_dateNaiss.setError(errors.get("dateNaiss"));
                            } else {
                                til_dateNaiss.setErrorEnabled(false);
                            }
                            //mail
                            if (errors.get("mail") != null) {
                                til_mail.setError(errors.get("mail"));
                            } else {
                                til_mail.setErrorEnabled(false);
                            }
                            //tel
                            if (errors.get("tel") != null) {
                                til_tel.setError(errors.get("tel"));
                            } else {
                                til_tel.setErrorEnabled(false);
                            }
                            //pseudo
                            if (errors.get("pseudo") != null) {
                                til_pseudo.setError(errors.get("pseudo"));
                            } else {
                                til_pseudo.setErrorEnabled(false);
                            }
                            //password
                            if (errors.get("password") != null) {
                                til_password.setError(errors.get("password"));
                            } else {
                                til_password.setErrorEnabled(false);
                            }
                        }
                        @Override
                        public void onError(String message) {
                            pb_loader.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    Toast.makeText(getApplicationContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void retour(View view) { this.finish(); }
}
