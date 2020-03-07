package com.example.admin.smarthomemain;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;

public class MenuActivity extends AppCompatActivity {


    private String server_uri = "tcp://io.adafruit.com:1883";
    private String id = "0e5ed63a00884f63b14900234cbcfb93";
    private String username = "mirakram";
    private String publish_topic;
    private String publish_topic5 = "mirakram/feeds/garagesub";
    private String publish_topic4 = "mirakram/feeds/elevatorsub";
    private String publish_topic3 = "mirakram/feeds/motorsub";
    private String publish_topic2 = "mirakram/feeds/lightsub";
    private String publish_topic1 = "mirakram/feeds/generalsub";
    private String subscribe_topic;
    private String subscribe_topic1 = "mirakram/feeds/generalinfo";
    private String subscribe_topic2 = "mirakram/feeds/lightDetections";
    private String subscribe_topic3 = "mirakram/feeds/motors";
    private String subscribe_topic4 = "mirakram/feeds/elevator";
    private String subscribe_topic5 = "mirakram/feeds/garage";
    private int qos = 0, switching;
    public String seekvalue;
    int tresh;
    private MqttAndroidClient client;
    MyMqttCallBack mqtt = new MyMqttCallBack();


    public TextView date;
    public TextView esastext;
    //public TextView tempingtext;
    //public TextView lightingtext;
    //public TextView gastext;
    public TextView geninfo;
    public LinearLayout lighting;
    public LinearLayout temping;
    public LinearLayout dataan;
    public LinearLayout gasing;
    public LinearLayout generalview;
    public LinearLayout elevatoring;
    public LinearLayout garaging;
    public LinearLayout refresss;
    public SharedPreferences info;
    public Intent i;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        info = getSharedPreferences("info", Context.MODE_PRIVATE);
        Log.d("Wotah", "acildim");
        client = new MqttAndroidClient(getApplicationContext(), server_uri, id);
        connectServer();
        Log.d("Wotah", "bilmirem connect oldu ya yo");
        /***********************************Widgets************************************************/
        generalview=(LinearLayout) findViewById(R.id.generalview);
        elevatoring=(LinearLayout) findViewById(R.id.elevatoring);
        temping=(LinearLayout) findViewById(R.id.lighting);
        dataan=(LinearLayout) findViewById(R.id.dataan);
        lighting=(LinearLayout) findViewById(R.id.temp);
        refresss=(LinearLayout) findViewById(R.id.refresss);
        garaging=(LinearLayout) findViewById(R.id.garaging);
        gasing=(LinearLayout) findViewById(R.id.gasing);

        geninfo=(TextView) findViewById(R.id.geninfo);
        date=(TextView)findViewById(R.id.date);
        esastext=(TextView) findViewById(R.id.esastext);

        /***********************************Widgets************************************************/
        Thread myThread = null;
        Runnable runnable = new CountDownRunner();
        myThread= new Thread(runnable);
        myThread.start();
        generalview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent("com.example.admin.smarthomemain.GeneralActivity");
                startActivity(i);
            }
        });

        garaging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switching=6;
                esastext.setText("Tap to see Garage Options");
            }
        });

        lighting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switching=2;
                esastext.setText("Tap to see Light Settings");
            }
        });

        temping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switching=1;
                esastext.setText("Tap to Change Climate settings");
            }
        });

        gasing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switching=3;
                esastext.setText("Tap to set Curtain positions");
            }
        });

        dataan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switching=4;
                esastext.setText("Not available");

            }
        });

        elevatoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switching=5;
            }
        });

        refresss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(switching){
                    case 0:
                        break;
                    case 2:
                        i=new Intent("com.example.admin.smarthomemain.LightActivity");
                        startActivity(i);

                        break;
                    case 1:
                        i=new Intent("com.example.admin.smarthomemain.TempActivity");
                        startActivity(i);
                        break;
                    case 3:
                        i=new Intent("com.example.admin.smarthomemain.GasActivity");
                        startActivity(i);
                        break;
                    case 4:
                        i=new Intent("com.example.admin.smarthomemain.DataActivity");
                        startActivity(i);
                        break;
                    case 5:
                        i=new Intent("com.example.admin.smarthomemain.ElevActivity");
                        startActivity(i);
                        break;
                    case 6:
                        i=new Intent("com.example.admin.smarthomemain.GarageActivity");
                        startActivity(i);

                    default:
                        break;
                }
            }
        });

    }

    /************************************Time and date settings****************************************/
    public void doWork() {
        runOnUiThread(new Runnable() {
            public void run() {
                try{
                    String curTime="-";
                    Calendar c = Calendar.getInstance();
                    String sDate = convertDate(c.get(Calendar.DAY_OF_MONTH)) + "/" + convertDate(c.get(Calendar.MONTH)+1)+"/"+c.get(Calendar.YEAR);
                    Date dt = new Date();
                    int hours = dt.getHours();
                    int minutes = dt.getMinutes();
                    int seconds = dt.getSeconds();
                    curTime = convertDate(hours) + ":" + convertDate(minutes) + ":" + convertDate(seconds);
                    date.setText(curTime+"\n"+sDate);
                }catch (Exception e) {}
            }
        });
    }
    public String convertDate(int input) {
        if (input >= 10) {
            return String.valueOf(input);
        } else {
            return "0" + String.valueOf(input);
        }
    }
    class CountDownRunner implements Runnable{
        // @Override
        public void run() {
            while(!Thread.currentThread().isInterrupted()){
                try {
                    doWork();
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }catch(Exception e){
                }
            }
        }
    }
/**************************************************************************************************/

    /*************************************Connect Publish Subscribe************************************/
    public void connectServer()
    {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        char[] password =  id.toCharArray();
        options.setPassword(password);
        options.setUserName(username);


        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d("Wotah", "Connected");
                    subscribeData(subscribe_topic1);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("Wotah", "NOT Connected");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public void subscribeData(String topic)
    {
        subscribe_topic=topic;
        try {
            IMqttToken subToken = client.subscribe(subscribe_topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // The message was published
                    Log.d("Wotah","Subscribed");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    // The subscription could not be performed, maybe the user was not
                    // authorized to subscribe on the specified topic e.g. using wildcards
                    Log.d("Wotah","NOT Subscribed");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        client.setCallback(mqtt);

    }
    public void publishData(String data, String publish_topic)
    {

        String payload = data;
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = payload.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            message.setQos(0);
            message.setRetained(true);
            message.setPayload(encodedPayload);

            IMqttToken token = client.publish(publish_topic, message);

            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    Log.d("Wotah","Published");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Log.d("Wotah","NOT Published");
                }
            });
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
        }
    }
    /**********************************************************************************************/


    public class MyMqttCallBack implements MqttCallback {
        private String message;
        public String getMessage()
        {
            return message;
        }

        @Override
        public void connectionLost(Throwable cause) {
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) throws Exception {
            this.message = message.toString();
            identifytopic(topic.trim(), this.message);
            Log.d("Wotah", topic + " " + message);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
        }

    }
    public void identifytopic(String text, String mesaj){
        if(text.equals(subscribe_topic1)) {
            topic1work(mesaj);
        }

        if (text.equals(subscribe_topic2)) {
            StringBuilder builder = new StringBuilder(mesaj);
            int middle = builder.indexOf("%%");
            int end = mesaj.length();
            String number = builder.substring(0, middle).trim();
            String perc = builder.substring(middle + "%%".length(), end).trim();
            topic2work(Integer.parseInt(number), Integer.parseInt(perc));
        }
        if(text.equals(subscribe_topic4)) {
            topic4work(mesaj);
        }



    }

    public void topic1work(String mesaj){
        SharedPreferences.Editor editing=info.edit();
        StringBuilder builder = new StringBuilder(mesaj);
        if(mesaj.trim().contains("#")) {
            int middle = builder.indexOf("#");
            int end = mesaj.length();
            String number = builder.substring(0, middle).trim();
            String gas = builder.substring(middle + "#".length(), end).trim();
            editing.putInt("gas", Integer.parseInt(gas));
        }

        if(mesaj.trim().contains("&")){
            int middle = builder.indexOf("*&*");
            int end = builder.indexOf("(&)");
            String extemp = builder.substring(0, middle).trim();
            String exphoto = builder.substring(middle + "*&*".length(), end).trim();
            String inphoto=builder.substring(end+"(&)".length(), mesaj.length());
            editing.putInt("exphoto", Integer.parseInt(exphoto));
            editing.putInt("extemp", Integer.parseInt(extemp));
            editing.putInt("inphoto", Integer.parseInt(inphoto));
            if(Integer.parseInt(exphoto)<16){
                //geninfo.setText("Cloudy\n"+extemp+"°C");
            }

        }

        if(mesaj.trim().contains("$")){
            int middle = builder.indexOf("($)");
            int end = builder.indexOf("*$");
            String buf21 = builder.substring(0, middle).trim();
            String buf22 = builder.substring(middle + "($)".length(), end).trim();
            String buf31 = builder.substring(builder.indexOf("*$")+"*$".length(), builder.indexOf("$*") ).trim();
            String buf32 = builder.substring("$*".length()+builder.indexOf("$*"), mesaj.length() ).trim();
            editing.putInt("2Left", Integer.parseInt(buf21));
            editing.putInt("2Right", Integer.parseInt(buf22));
            editing.putInt("2Left", Integer.parseInt(buf31));
            editing.putInt("3Right", Integer.parseInt(buf32));
            int a=(Integer.parseInt(buf21)+Integer.parseInt(buf22)+Integer.parseInt(buf31)+Integer.parseInt(buf32))/4;
            //lightingtext.setText("Light Status\n"+a+"%");
        }

        editing.commit();
    }

    public void topic2work(int n, int value){
        SharedPreferences.Editor editing=info.edit();
        if(n==5){
            editing.putInt("2Left", value);
        }
        else{
            if(n==2){
                editing.putInt("2Right", value);
            }
            else{
                if(n==3){
                    editing.putInt("3Left", value);
                }
                else{
                    editing.putInt("3Right", value);
                }
            }
        }
        editing.commit();
    }

    public void topic3work(){

    }

    public void topic4work(String mesaj){
        SharedPreferences.Editor editing=info.edit();
        editing.putInt("floor", Integer.parseInt(mesaj));
        editing.commit();
    }

    public void topic5work(){

    }
}


