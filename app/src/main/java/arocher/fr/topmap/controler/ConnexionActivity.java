package arocher.fr.topmap.controler;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import org.w3c.dom.Text;

import arocher.fr.topmap.R;
import arocher.fr.topmap.SessionManager;
import arocher.fr.topmap.VolleySingleton;
import arocher.fr.topmap.myrequest.MyRequest;

public class ConnexionActivity extends AppCompatActivity {

    private TextInputLayout til_mail, til_password;
    private Button btn_login, btn_register;
    private ProgressBar pb_loader;
    private Handler handler;
    private RequestQueue queue;
    private MyRequest request;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);

        til_mail = (TextInputLayout) findViewById(R.id.til_mail_log);
        til_password = (TextInputLayout) findViewById(R.id.til_password_log);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_register = (Button) findViewById(R.id.btn_register);
        pb_loader = (ProgressBar) findViewById(R.id.pb_loader);

        queue = VolleySingleton.getInstance(this).getRequestQueue();
        request = new MyRequest(this, queue);
        handler = new Handler();

        sessionManager = new SessionManager(this);
        if(sessionManager.isLogged())
        {
            Intent intent = new Intent(this, AccueilActivity.class);
            startActivity(intent);
            finish();
        }

        Intent intent = getIntent();
        if(intent.hasExtra("REGISTER"))
        {
            Toast.makeText(this, intent.getStringExtra("REGISTER"), Toast.LENGTH_SHORT).show();
        }

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String mail = til_mail.getEditText().getText().toString().trim();
                final String password = til_password.getEditText().getText().toString().trim();
                pb_loader.setVisibility(View.VISIBLE);
                if(mail.length() > 0 && password.length() > 0)
                {
                    handler.postDelayed(new Runnable()
                    {
                        @Override
                        public void run() {
                            request.connexion(mail, password, new MyRequest.LoginCallback()
                            {
                                @Override
                                public void onSuccess(String id, String mail, String pseudo) {



                                    pb_loader.setVisibility(View.GONE);
                                    sessionManager.insertUser(id, mail, pseudo);
                                    Intent intent = new Intent(getApplicationContext(), AccueilActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onError(String message) {
                                    pb_loader.setVisibility(View.GONE);
                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    },1000);

                }
                else
                {
                    pb_loader.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
