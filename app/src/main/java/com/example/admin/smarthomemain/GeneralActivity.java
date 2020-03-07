package com.example.admin.smarthomemain;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.crystal.crystalrangeseekbar.interfaces.OnSeekbarChangeListener;
import com.crystal.crystalrangeseekbar.widgets.CrystalSeekbar;

import java.util.Date;

public class GeneralActivity extends AppCompatActivity {

    ImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);image=(ImageView) findViewById(R.id.generalimage);
        CrystalSeekbar seekbar = (CrystalSeekbar)findViewById(R.id.rangeSeekbar1);
        Date dt = new Date();
        final int hours = dt.getHours();
        seekbar.setOnSeekbarChangeListener(new OnSeekbarChangeListener() {
            @Override
            public void valueChanged(Number value) {

                String seekvalue=String.valueOf(value);
                int i=Integer.parseInt(seekvalue);
                if(hours<19&&hours>6) {
                    switch (i) {
                        case 1:
                            image.setImageResource(R.drawable.sunny1);
                            break;
                        case 2:
                            image.setImageResource(R.drawable.sunny2);
                            break;
                        case 3:
                            image.setImageResource(R.drawable.sunny3);
                            break;
                        case 4:
                            image.setImageResource(R.drawable.sunny4);
                            break;
                        case 5:
                            image.setImageResource(R.drawable.sunny5);
                            break;
                        case 6:
                            image.setImageResource(R.drawable.sunny6);
                            break;
                        case 7:
                            image.setImageResource(R.drawable.sunny7);
                            break;
                        case 8:
                            image.setImageResource(R.drawable.sunny8);
                            break;
                        case 9:
                            image.setImageResource(R.drawable.sunny9);
                            break;
                        default:
                            break;
                    }
                }
                else{
                    switch (i) {
                        case 1:
                            image.setImageResource(R.drawable.night1);
                            break;
                        case 2:
                            image.setImageResource(R.drawable.night2);
                            break;
                        case 3:
                            image.setImageResource(R.drawable.night3);
                            break;
                        case 4:
                            image.setImageResource(R.drawable.night4);
                            break;
                        case 5:
                            image.setImageResource(R.drawable.night5);
                            break;
                        case 6:
                            image.setImageResource(R.drawable.night6);
                            break;
                        case 7:
                            image.setImageResource(R.drawable.night7);
                            break;
                        case 8:
                            image.setImageResource(R.drawable.night8);
                            break;
                        case 9:
                            image.setImageResource(R.drawable.night9);
                            break;
                        default:
                            break;
                    }
                }
            }
        });
    }
}