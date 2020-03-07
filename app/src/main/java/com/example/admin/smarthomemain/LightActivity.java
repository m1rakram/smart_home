package com.example.admin.smarthomemain;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.crystal.crystalrangeseekbar.interfaces.OnSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalSeekbar;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;

public class LightActivity extends AppCompatActivity {

    /***********************************Adafruit.io************************************************/

    private String server_uri = "tcp://io.adafruit.com:1883";
    private String id = "0e5ed63a00884f63b14900234cbcfb93";
    private String username = "mirakram";
    private String publish_topic5 = "mirakram/feeds/garagesub";
    private String publish_topic4 = "mirakram/feeds/elevatorsub";
    private String publish_topic3 = "mirakram/feeds/Motorsub";
    private String publish_topic2 = "mirakram/feeds/lightsub";
    private String publish_topic1 = "mirakram/feeds/generalsub";
    private String subscribe_topic;
    private String subscribe_topic1 = "mirakram/feeds/generalinfo";
    private String subscribe_topic2 = "mirakram/feeds/LightDetections";
    private String subscribe_topic3 = "mirakram/feeds/Motors";
    private String subscribe_topic4 = "mirakram/feeds/elevator";
    private String subscribe_topic5 = "mirakram/feeds/garage";
    private int qos = 0;
    public String seekvalue;
    private MqttAndroidClient client;
    LightActivity.MyMqttCallBack mqtt = new LightActivity.MyMqttCallBack();

    /**********************************************************************************************/


    public LinearLayout light21s, light22s, light31s, light32s;
    public CrystalSeekbar range4, range3;
    public TextView text21s, text22s, text31s, text32s, range2text, feedback;
    public TextView treshold;
    public Button send, send2;
    public Switch autom;
    public ImageView imagelight;
    public SharedPreferences info;
    int count=0;
    String tresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);


        client = new MqttAndroidClient(getApplicationContext(), server_uri, id);
        connectServer();

        info = getSharedPreferences("info", Context.MODE_PRIVATE);

        imagelight=(ImageView) findViewById(R.id.imagelight);


        light21s=(LinearLayout)findViewById(R.id.light21s);
        light22s=(LinearLayout)findViewById(R.id.light22s);
        light31s=(LinearLayout)findViewById(R.id.light31s);
        light32s=(LinearLayout)findViewById(R.id.light32s);

        text21s=(TextView) findViewById(R.id.text21s);
        text22s=(TextView) findViewById(R.id.text22s);
        text31s=(TextView) findViewById(R.id.text31s);
        text32s=(TextView) findViewById(R.id.text32s);
        range2text=(TextView) findViewById(R.id.seekbar1);
        treshold=(TextView) findViewById(R.id.treshold);
        feedback=(TextView) findViewById(R.id.feedback);


        text21s.setText("Left: "+info.getInt("2Left", 0)+"%");
        text22s.setText("Right: "+info.getInt("2Right", 0)+"%");
        text31s.setText("Left: "+info.getInt("3Left", 0)+"%");
        text32s.setText("Right: "+info.getInt("3Right", 0)+"%");
        feedback.setText("Outside brightness:"+info.getInt("exphoto", 0)+"%\nInside brightness:"+info.getInt("inphoto", 0)+"%");




        send=(Button) findViewById(R.id.send1);
        send2=(Button) findViewById(R.id.send2);

        autom=(Switch) findViewById(R.id.switch1);

        range4=(CrystalSeekbar) findViewById(R.id.rangeSeekbar4);
        range3=(CrystalSeekbar) findViewById(R.id.rangeSeekbar3);

        light21s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text21s.setTextColor(getResources().getColor(R.color.highlight));
                text22s.setTextColor(getResources().getColor(R.color.standard));
                text31s.setTextColor(getResources().getColor(R.color.standard));
                text32s.setTextColor(getResources().getColor(R.color.standard));
                imagelight.setImageResource(R.drawable.l21);
                count=4;
            }
        });

        light22s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text21s.setTextColor(getResources().getColor(R.color.standard));
                text22s.setTextColor(getResources().getColor(R.color.highlight));
                text31s.setTextColor(getResources().getColor(R.color.standard));
                text32s.setTextColor(getResources().getColor(R.color.standard));
                imagelight.setImageResource(R.drawable.l22);
                count=5;
            }
        });

        light31s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text21s.setTextColor(getResources().getColor(R.color.standard));
                text22s.setTextColor(getResources().getColor(R.color.standard));
                text31s.setTextColor(getResources().getColor(R.color.highlight));
                text32s.setTextColor(getResources().getColor(R.color.standard));
                imagelight.setImageResource(R.drawable.l31);
                count=2;
            }
        });

        light32s.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                text21s.setTextColor(getResources().getColor(R.color.standard));
                text22s.setTextColor(getResources().getColor(R.color.standard));
                text31s.setTextColor(getResources().getColor(R.color.standard));
                text32s.setTextColor(getResources().getColor(R.color.highlight));
                imagelight.setImageResource(R.drawable.l32);
                count=3;
            }
        });

        autom.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    publishData("AUTO", publish_topic2);
                }
                else {
                    publishData("MANU", publish_topic2);
                }
            }
        });

        range3.setOnSeekbarChangeListener(new OnSeekbarChangeListener() {
            @Override
            public void valueChanged(Number value) {
                seekvalue=String.valueOf(value);
                range2text.setText("Light intensity: "+seekvalue+"%");
            }
        });

        range4.setOnSeekbarChangeListener(new OnSeekbarChangeListener() {
            @Override
            public void valueChanged(Number value) {
                tresh=String.valueOf(value);
                treshold.setText("Brightness Treshold "+tresh+"%");
            }
        });

        send2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishData("tt"+String.valueOf(tresh) , publish_topic2);
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishData("2"+String.valueOf(count)+"#"+seekvalue, publish_topic2);
            }
        });
    }


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
            text21s.setText("Left: "+value+"%");
            editing.putInt("2Left", value);
        }
        else{
            if(n==2){
                text22s.setText("Right: "+value+"%");
                editing.putInt("2Right", value);
            }
            else{
                if(n==3){
                    text31s.setText("Left: "+value+"%");
                    editing.putInt("3Left", value);
                }
                else{
                    text32s.setText("Right: "+value+"%");
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
