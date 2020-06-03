package com.example.wanderfo_govt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    final ArrayList<String> categories =  new ArrayList<>( Arrays. asList("food supply", "food manufacturing",
            "fnb outlets", "food caterers", "food delivery services", "food packaging", "laboratory food safety",
            "shelf life", "manufacturers of essential products", "semiconductor", "aerospace", "manufacturing of print",
            "distributor of essential products"));
    ArrayList<String> statelist=new ArrayList<>();
    ArrayList<String> newstate = new ArrayList<>();

    LinearLayout linearLayout;
    Button submitbutton;

    //to pass to ConfirmPage
    public final static String RESPONSE_KEY="response";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        submitbutton = findViewById(R.id.submitbutton);
        linearLayout = findViewById(R.id.linearLay);

        //submit button
        submitbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // check is api response has returned
                if(statelist.isEmpty()){
                    Toast.makeText(MainActivity.this, "Please wait for API to return values",Toast.LENGTH_SHORT).show();
                }else {
                    for (int i = 0; i < categories.size(); i++) {
                        ToggleButton tb = (ToggleButton) ((LinearLayout) linearLayout.getChildAt(i)).getChildAt(1);
                        if (tb.isChecked()) {
                            newstate.add("1");
                        } else {
                            newstate.add("0");
                        }
                    }
                    Log.i("jo", "new state: " + newstate+" state list: "+statelist);
                    if (newstate.equals(statelist)) {
                        Toast.makeText(MainActivity.this, "No changes made", Toast.LENGTH_SHORT).show();
                        newstate = new ArrayList<>();
                    } else {
                        String s = "";
                        for (int i = 0; i < newstate.size(); i++) {
                            s = s + newstate.get(i);
                        }
                        Log.i("jo", "string to api: " + s);
                        newstate = new ArrayList<>();
                        // inside on Response - need to pass the response String to the next activity
                        // GET: Instantiate the RequestQueue.
                        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
                        String url ="https://9oro70kdf5.execute-api.us-east-1.amazonaws.com/gov/gov/user/getgray/none/"+s;

                        // Request a string response from the provided URL.
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        // Display the first 500 characters of the response string.
                                        Log.i("jo","submit button: Response is: "+ response);
                                        String resString = response; //"daiso,1,fairprice,0"
                                        Intent intent = new Intent(MainActivity.this,ConfirmPage.class);
                                        intent.putExtra(RESPONSE_KEY,resString);
                                        startActivity(intent);
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.i("jo", "That didn't work!");
                                Log.i("jo", error.getMessage());

                            }
                        });
                        queue.add(stringRequest);
                    }
                }
            }
        });

        // get the list of states in a string
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
        String url ="https://9oro70kdf5.execute-api.us-east-1.amazonaws.com/gov/gov/user/getstatus/none/none";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.i("jo","Response is: "+ response);
                        response = response.substring(1, response.length() - 1);
                        // manipulate the string
                        String input =response;
                        //String input = returnResponse[0]; //"0101110110001"
                        for(int c=0; c<input.length();c++){
                            statelist.add(String.valueOf(input.charAt(c)));
                        }

                        Log.i("jo","statelist: "+statelist);
                        for(int i =0; i<categories.size();i++){
                            LinearLayout element = (LinearLayout) View.inflate(MainActivity.this,R.layout.category_component,null);
                            TextView elementText = (TextView) element.getChildAt(0);
                            ToggleButton elementSwitch = (ToggleButton) element.getChildAt(1);
                            elementText.setText(categories.get(i));
                            if(statelist.get(i).equals("1")){
                                //Log.i("jo","1");
                                elementSwitch.setChecked(true);
                                element.setSelected(true);
                            }
                            else if(statelist.get(i).equals("0")){
                                //Log.i("jo","0");
                                elementSwitch.setChecked(false);
                                element.setSelected(false);
                            }
                            else{
                                Log.i("jo","set up error");
                            }
                            linearLayout.addView(element);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("jo", "That didn't work!");
                Log.i("jo", error.getMessage());
            }
        });
        queue.add(stringRequest);
    }
}








