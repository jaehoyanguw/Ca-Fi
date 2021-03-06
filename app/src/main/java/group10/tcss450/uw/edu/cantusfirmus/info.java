package group10.tcss450.uw.edu.cantusfirmus;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.util.concurrent.TimeUnit;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/***
 * Class to display registration info. More information will be shown here later.
 * Currently it is here to show that the user login works.
 * @author Alec Walsh
 * @version Feb 10 2017
 */
public class info extends AppCompatActivity {
    /***
     * Handler to allow other threads to touch the UI thread.
     */
    private Handler handler;
    /***
     * An array holding all the display text fields.
     */
    private final TextView[] boxes = new TextView[4];

    /***
     * Immediately calls another function to grab the info from the server.
     * Creates a new thread to do the networking.
     * @param savedInstanceState required bundle.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        handler = new Handler();
        boxes[0] = (TextView) findViewById(R.id.info_firstName);
        boxes[1] = (TextView) findViewById(R.id.info_lastName);
        boxes[2] = (TextView) findViewById(R.id.info_userName);
        boxes[3] = (TextView) findViewById(R.id.info_email);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    get_info();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    /***
     * Uses okHttp to grab the user info from the server. A request is formed and sent to the server.
     * The response is checked to make sure that the request was successful. Then the EditTexts were filled in.
     * @throws IOException Thrown if there is a issue with the server.
     */
    private void get_info() throws IOException{
        OkHttpClient client = new OkHttpClient.Builder().cookieJar(new JavaNetCookieJar(login.getCookieManager()))
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS)
                .build();
        //Log.d("What cookie to use",login.getCookieManager().getCookieStore().getCookies().get(0).getValue());
        Request request = new Request.Builder()
                .url("https://damp-anchorage-73052.herokuapp.com/user_info")
                .get()
                .addHeader("cache-control", "no-cache")
                .addHeader("postman-token", "536e387f-52c2-946a-fdac-79006118dbc8")
                .build();
        Response response = client.newCall(request).execute();
        final String[] details = response.body().string().split(",");
        handler.post(new Runnable(){
            @Override
            public void run(){
                if(details[0].contains("error")){
                    Toast.makeText(info.this,"No User Signed In!",Toast.LENGTH_LONG).show();
                }else{
                    boxes[0].setText("First Name"+details[1].substring(details[1].indexOf(":")).replace("\"",""));
                    boxes[1].setText("Last Name"+details[2].substring(details[2].indexOf(":")).replace("\"",""));
                    boxes[2].setText("User Name"+details[3].substring(details[3].indexOf(":")).replace("\"",""));
                    boxes[3].setText("Email"+details[4].substring(details[4].indexOf(":")).replace("\"",""));
                }
            }
        });
    }
}
