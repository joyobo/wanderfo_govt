package com.example.wanderfo_govt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ConfirmPage extends AppCompatActivity {
    TextView statusTV;
    LinearLayout linearLay;
    Button donebutton;

    ArrayList<String> pair;
    ArrayList<String> name = new ArrayList<>();
    ArrayList<String> oldstatus= new ArrayList<>();
    String response="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_page);

        statusTV=findViewById(R.id.status);
        linearLay = findViewById(R.id.linearLayConfirm);
        donebutton = findViewById(R.id.donebutton);

        final Intent intentMainToConfirm = getIntent();
        response = intentMainToConfirm.getStringExtra(MainActivity.RESPONSE_KEY);
        if(response.equals("\"\"")){
            statusTV.setText("There is no conflict for shop permission. Click DONE to return back to main page.");
        }else{
            statusTV.setText("There is/are conflict(s) for shops with multiple categories. Manually adjust their permission. Click DONE to submit.");
            //Example: response = "daiso,1,fairprice,0";
            Log.i("jo","response: "+response);
            pair = new ArrayList<>(Arrays.asList(response.replaceAll("\"","").split(",")));
            for(int i =0; i<pair.size();i+=2){
                name.add(pair.get(i));
                oldstatus.add(pair.get(i+1));
                LinearLayout element = (LinearLayout) View.inflate(ConfirmPage.this,R.layout.category_component,null);
                TextView elementText = (TextView) element.getChildAt(0);
                ToggleButton elementSwitch = (ToggleButton) element.getChildAt(1);
                elementText.setText(pair.get(i));
                if(pair.get(i+1).equals("1")){
                    Log.i("jo","1");
                    elementSwitch.setChecked(true);
                    element.setSelected(true);
                }
                else if(pair.get(i+1).equals("0")){
                    Log.i("jo","0");
                    elementSwitch.setChecked(false);
                    element.setSelected(false);
                }
                else{
                    Log.i("jo","set up error");
                }
                linearLay.addView(element);
            }
            Log.i("jo","name: "+name+" status: "+oldstatus);

        }

        // done button
        donebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(response.equals("\"\"")){
                    Intent intentToMainNoChanges = new Intent(ConfirmPage.this,MainActivity.class);
                    startActivity(intentToMainNoChanges);
                }else {
                    ArrayList<String> newstate = new ArrayList<>();
                    for (int i = 0; i < pair.size() / 2; i++) {
                        ToggleButton tb = (ToggleButton) ((LinearLayout) linearLay.getChildAt(i)).getChildAt(1);
                        if (tb.isChecked()) {
                            newstate.add("1");
                        } else {
                            newstate.add("0");
                        }
                    }
                    Log.i("jo", "new state: " + newstate + " state list: " + oldstatus);
                    if (newstate.equals(oldstatus)) {
                        Toast.makeText(ConfirmPage.this, "No additional changes made", Toast.LENGTH_SHORT).show();
                        Intent intentToMainNoChanges = new Intent(ConfirmPage.this, MainActivity.class);
                        startActivity(intentToMainNoChanges);
                    } else {
                        for (int i = 0; i < name.size(); i++) {
                            // POST
                            Log.i("jo", "invoke api set permission for: shopname - " + name.get(i) + " with permission " + newstate.get(i));
                            try {
                                JSONObject jsonBody = new JSONObject();
                                // {
                                //    "target": "setpermission",
                                //    "shopname": "breadtalk",
                                //    "value": "1"
                                //  }
                                jsonBody.put("target", "setpermission");
                                jsonBody.put("shopname", name.get(i));
                                jsonBody.put("value", newstate.get(i));

                                Log.i("jo", jsonBody.toString());

                                String url = "https://9oro70kdf5.execute-api.us-east-1.amazonaws.com/gov/gov/user";
                                RequestQueue queue = Volley.newRequestQueue(ConfirmPage.this);
                                final JSONObject[] returnObj = {new JSONObject()};

                                JsonObjectRequest jsonObjectRequestt = new JsonObjectRequest(Request.Method.POST, url, jsonBody, new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.i("jo", "Response: " + response);
                                        Toast.makeText(ConfirmPage.this, response.toString(), Toast.LENGTH_SHORT).show();
                                        Intent intentToMainChanges = new Intent(ConfirmPage.this, MainActivity.class);
                                        startActivity(intentToMainChanges);
                                    }
                                }, new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        //                    Log.i("ZW error", error.getMessage());
                                        Log.i("jo", "error");
                                    }
                                }) {
                                    @Override
                                    public Map<String, String> getHeaders() throws AuthFailureError {
                                        HashMap<String, String> headers = new HashMap<String, String>();
                                        headers.put("Content-Type", "application/json; charset=utf-8");
                                        return headers;
                                    }
                                };
                                queue.add(jsonObjectRequestt);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        });
    }
}
