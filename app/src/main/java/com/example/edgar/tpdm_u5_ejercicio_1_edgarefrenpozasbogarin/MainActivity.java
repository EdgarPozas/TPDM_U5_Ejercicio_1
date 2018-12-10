package com.example.edgar.tpdm_u5_ejercicio_1_edgarefrenpozasbogarin;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private Lienzo lienzo;
    private SensorManager sensorManager;
    private Sensor acelerometro,iluminacion;
    private SensorEventListener listener_acelerometro,listener_iluminacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lienzo=new Lienzo(this);
        setContentView(lienzo);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        iluminacion=sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        if(acelerometro==null&&iluminacion==null){
            lienzo.codigo=400;
            return;
        }

        listener_acelerometro=new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float ac_x=(event.values[0]);
                float ac_y=(event.values[1]);
                lienzo.jugador.posx=ac_x;
                lienzo.jugador.posy=ac_y;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        listener_iluminacion=new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                lienzo.jugador.seleccionado= (event.values[0])==0;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }
    @Override
    protected void onResume(){
        super.onResume();
        if(acelerometro!=null)
            sensorManager.registerListener(listener_acelerometro,acelerometro,SensorManager.SENSOR_DELAY_NORMAL);
        if(iluminacion!=null)
            sensorManager.registerListener(listener_iluminacion,iluminacion,SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onPause(){
        super.onPause();
        if(acelerometro!=null)
            sensorManager.unregisterListener(listener_acelerometro);
        if(iluminacion!=null)
            sensorManager.unregisterListener(listener_iluminacion);
    }

}
