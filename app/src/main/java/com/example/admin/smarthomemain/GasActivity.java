package com.example.admin.smarthomemain;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class GasActivity extends AppCompatActivity {

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
    private int qos = 0;
    public String seekvalue;
    private MqttAndroidClient client;
    GasActivity.MyMqttCallBack mqtt = new  GasActivity.MyMqttCallBack();
    public SharedPreferences info;
    public VerticalSeekBar v1;
    public ImageView imagecur;
    public LinearLayout sf2, sf3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas);
        client = new MqttAndroidClient(getApplicationContext(), server_uri, id);
        connectServer();
        info = getSharedPreferences("info", Context.MODE_PRIVATE);
        imagecur=(ImageView)findViewById(R.id.imagecur);
        v1=(VerticalSeekBar) findViewById(R.id.mySeekBar);
        sf2=(LinearLayout)findViewById(R.id.sf2);
        sf3=(LinearLayout) findViewById(R.id.sf3);

        v1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                switch(progress){
                    case 1:
                        imagecur.setImageResource(R.drawable.c1);
                        seekvalue=String.valueOf(15*progress);
                        break;
                    case 2:
                        imagecur.setImageResource(R.drawable.c2);
                        seekvalue=String.valueOf(15*progress);
                        break;
                    case 3:
                        imagecur.setImageResource(R.drawable.c3);
                        seekvalue=String.valueOf(15*progress);
                        break;
                    case 4:
                        imagecur.setImageResource(R.drawable.c4);
                        seekvalue=String.valueOf(15*progress);
                        break;
                    case 5:
                        imagecur.setImageResource(R.drawable.c5);
                        seekvalue=String.valueOf(15*progress);
                        break;
                    case 6:
                        imagecur.setImageResource(R.drawable.c6);
                        seekvalue=String.valueOf(15*progress);
                        break;
                    case 7:
                        imagecur.setImageResource(R.drawable.c7);
                        seekvalue=String.valueOf(15*progress);
                        break;
                    case 8:
                        imagecur.setImageResource(R.drawable.c8);
                        seekvalue=String.valueOf(15*progress);
                        break;
                    case 9:
                        imagecur.setImageResource(R.drawable.c9);
                        seekvalue=String.valueOf(15*progress);
                        break;
                    case 10:
                        imagecur.setImageResource(R.drawable.c10);
                        seekvalue=String.valueOf(15*progress);
                        break;
                    case 11:
                        imagecur.setImageResource(R.drawable.c11);
                        seekvalue=String.valueOf(15*progress);
                        break;
                    case 12:
                        imagecur.setImageResource(R.drawable.c12);
                        seekvalue=String.valueOf(15*progress);
                        break;
                }
            }






            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        sf2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishData("942#"+seekvalue, publish_topic5);
            }
        });

        sf3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishData("943#"+seekvalue, publish_topic5);
            }
        });

    }



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
                    subscribeData(subscribe_topic2);
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
