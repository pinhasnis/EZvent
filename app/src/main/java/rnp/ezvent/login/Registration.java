package rnp.ezvent.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import rnp.ezvent.MainActivity;
import rnp.ezvent.R;
import server.Registration.Registration_AsyncTask;
import server.ServerAsyncResponse;
import utils.Constans.Constants;

/**
 * Created by Ravid on 08/10/2015.
 */
public class Registration extends AppCompatActivity implements ServerAsyncResponse {
    private EditText et_rg_your_name_ui;
    private EditText et_rg_phone_ui;
    private EditText et_rg_password_ui;
    private EditText et_rg_confirm_password_ui;
    private Button bt_rg_register_ui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);
        et_rg_your_name_ui = (EditText) findViewById(R.id.et_rg_your_name_ui);
        et_rg_phone_ui = (EditText) findViewById(R.id.et_rg_phone_ui);
        et_rg_password_ui = (EditText) findViewById(R.id.et_rg_password_ui);
        et_rg_confirm_password_ui = (EditText) findViewById(R.id.et_rg_confirm_password_ui);
        bt_rg_register_ui = (Button) findViewById(R.id.bt_rg_register_ui);

        bt_rg_register_ui.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                saveData();
            }

        });

    }

    public void processFinish(String... output) {
        if (output[0].equals(Constants.OK)) {
            SharedPreferences.Editor editor = getSharedPreferences(MainActivity.MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString(Constants.r_user_key, Constants.r_user);
            String User_ID = et_rg_phone_ui.getText().toString();
            if (User_ID.charAt(0) == '0') {
                User_ID = Constants.country_code + User_ID.substring(1);
            }
            editor.putString(Constants.MY_User_ID_Key, User_ID);
            editor.putString(Constants.MY_User_Nickname_Key, et_rg_your_name_ui.getText().toString());
            editor.putString(Constants.MY_User_Password_Key, et_rg_password_ui.getText().toString());
            editor.commit();
            Intent login = new Intent(this, rnp.ezvent.login.login.class);
            startActivity(login);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), output[0], Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public String currentLocation() {
        return null;
    }

    @Override
    public void closeActivity() {
        //TODO ?
    }

    private void saveData() {
        String Nickname = et_rg_your_name_ui.getText().toString();
        String User_ID = et_rg_phone_ui.getText().toString();
        String password = et_rg_password_ui.getText().toString();
        String confirm_password = et_rg_confirm_password_ui.getText().toString();
        if (User_ID.length() > 0 && Nickname.length() > 0 && password.length() > 0 && confirm_password.length() > 0) {
            if (password.equals(confirm_password)) {
                new Registration_AsyncTask(this, this).execute(User_ID, Nickname, password);
            } else {
                Toast.makeText(getApplicationContext(), "password are not much", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "All fields should be filed", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
