package com.example.admin.smarthomemain;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
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

public class TempActivity extends AppCompatActivity {

    public Button tempbut, gasbut;
    public LinearLayout use;
    public CrystalSeekbar temprange, gasrange, motor1range, motor2range;
    public TextView temptext, gastext, motor1text, motor2text, temperature, humidity, gasstatus;
    public Switch ventcontswi;

    public String mqtemp="0", mqhum="0", mqgas="0";

    private String server_uri = "tcp://io.adafruit.com:1883";
    private String id = "0e5ed63a00884f63b14900234cbcfb93";
    private String username = "mirakram";
    private String publish_topic5 = "mirakram/feeds/garage";
    private String publish_topic4 = "mirakram/feeds/elevator";
    private String publish_topic3 = "mirakram/feeds/Motors";
    private String publish_topic2 = "mirakram/feeds/LightDetections";
    private String publish_topic1 = "mirakram/feeds/generalinfo";
    private String subscribe_topic;
    private String subscribe_topic1 = "mirakram/feeds/generalsub";
    private String subscribe_topic2 = "mirakram/feeds/lightsub";
    private String subscribe_topic3 = "mirakram/feeds/motorsub";
    private String subscribe_topic4 = "mirakram/feeds/elevatorsub";
    private String subscribe_topic5 = "mirakram/feeds/garagesub";
    private int qos = 0;
    public String tempseekvalue="0", gasseekvalue="0", motor1value="0", motor2value="0";
    private MqttAndroidClient client;
    TempActivity.MyMqttCallBack mqtt = new  TempActivity.MyMqttCallBack();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        client = new MqttAndroidClient(getApplicationContext(), server_uri, id);
        connectServer();

        ventcontswi=(Switch) findViewById(R.id.ventcontswi);

        tempbut=(Button) findViewById(R.id.send1);
        gasbut=(Button) findViewById(R.id.send2);

        temperature=(TextView) findViewById(R.id.temperature);
        humidity=(TextView) findViewById(R.id.humidity);
        gasstatus=(TextView) findViewById(R.id.gasstatus);

        temptext=(TextView) findViewById(R.id.seekbar1);
        gastext=(TextView) findViewById(R.id.seekbar2);
        motor1text=(TextView) findViewById(R.id.motor1text);
        motor2text=(TextView) findViewById(R.id.motor2text);

        temprange=(CrystalSeekbar) findViewById(R.id.rangeSeekbar3);
        gasrange=(CrystalSeekbar) findViewById(R.id.rangeSeekbar4);
        motor1range=(CrystalSeekbar) findViewById(R.id.rangeSeekbar5);
        motor2range=(CrystalSeekbar) findViewById(R.id.rangeSeekbar6);

        use=(LinearLayout) findViewById(R.id.use);

        tempbut.setClickable(false);
        gasbut.setClickable(false);

        ventcontswi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    publishData("4AUTO_MOTOR",publish_topic3);
                    tempbut.setClickable(true);
                    gasbut.setClickable(true);
                }
                else {
                    publishData("4MANU_MOTOR", publish_topic3);
                    tempbut.setClickable(false);
                    gasbut.setClickable(false);
                }
            }
        });

        tempbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishData("5"+tempseekvalue, publish_topic3);
            }
        });

        gasbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        use.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishData("6"+motor1value+"&"+motor2value, publish_topic3 );
            }
        });

        temprange.setOnSeekbarChangeListener(new OnSeekbarChangeListener() {
            @Override
            public void valueChanged(Number value) {
                tempseekvalue=String.valueOf(value);
                temptext.setText("Temperature treshold: "+tempseekvalue+"%");
            }
        });


        gasrange.setOnSeekbarChangeListener(new OnSeekbarChangeListener() {
            @Override
            public void valueChanged(Number value) {
                gasseekvalue=String.valueOf(value);
                gastext.setText("Gas treshold: "+gasseekvalue+"%");
            }
        });


        motor1range.setOnSeekbarChangeListener(new OnSeekbarChangeListener() {
            @Override
            public void valueChanged(Number value) {
                motor1value=String.valueOf(value);
                motor1text.setText("Second Floor:\n"+motor1value+"%");
            }
        });


        motor2range.setOnSeekbarChangeListener(new OnSeekbarChangeListener() {
            @Override
            public void valueChanged(Number value) {
                motor2value=String.valueOf(value);
                motor2text.setText("Third Floor:"+motor2value+"%");
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
            Log.d("Wotah", topic + " " + message);
        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {
        }

    }

}
