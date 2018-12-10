package com.example.edgar.tpdm_u5_ejercicio_1_edgarefrenpozasbogarin;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.os.Debug;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.Random;
import java.util.TreeMap;

public class Lienzo extends SurfaceView implements SurfaceHolder.Callback{

    private SurfaceHolder sh;
    private Canvas canvas;

    public Jugador jugador;
    public int codigo;
    public boolean correr;

    public Lienzo(Context context) {
        super(context);
        correr=true;
        jugador=new Jugador();

        sh = getHolder();
        sh.addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (correr) {
                    Canvas c = null;
                    try {
                        c = sh.lockCanvas(null);
                        synchronized (sh) {
                            c.drawColor(Color.WHITE);
                            jugador.dibujar(c);
                        }
                    }finally {
                        if (c != null) {
                            sh.unlockCanvasAndPost(c);
                        }
                    }
                }
            }
        }).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public abstract class Basico{
        public float posx,posy;
        protected abstract void dibujar(Canvas c);
    }
    public class Jugador extends Basico {

        private Barra_Superior barra_superior;
        private Elemento elemento_arriba,elemento_izquierda,elemento_abajo,elemento_derecha;
        public boolean seleccionado;
        public int puntos;
        public String operacion;
        public int ronda;
        public int pos_correcta;
        private boolean sensor_tapado;

        public int limite_abajo=800,limite_arriba=500,limite_der=500,limite_izq=100;

        public Jugador(){
            barra_superior=new Barra_Superior(this);
            ronda=1;
            puntos=0;
            operacion="Tapa sensor luz iniciar y seleccionar";
            elemento_arriba=new Elemento(0);
            elemento_abajo=new Elemento(1);
            elemento_izquierda=new Elemento(3);
            elemento_derecha=new Elemento(2);
            pos_correcta=-1;
            sensor_tapado=false;
        }

        @Override
        protected void dibujar(Canvas c) {
            Paint p=new Paint();
            p.setStyle(Paint.Style.FILL);
            p.setColor(Color.rgb(200,100,238));
            float mitad_x=c.getWidth()/2;
            float mitad_y=c.getHeight()/2;
            float px=mitad_x+50*-jugador.posx;
            float py=mitad_y-50*-jugador.posy;
            c.drawCircle(px, py, 50, p);

            if(px>limite_der){
                deseleccionar();
                elemento_derecha.seleccionado=true;
            }else if(px<limite_izq){
                deseleccionar();
                elemento_izquierda.seleccionado=true;
            }else if(py<limite_arriba){
                deseleccionar();
                elemento_arriba.seleccionado=true;
            }else if(py>limite_abajo){
                deseleccionar();
                elemento_abajo.seleccionado=true;
            }else{
                deseleccionar();
            }


            if(seleccionado){
                if(!sensor_tapado) {
                    if(ronda==1) {
                        nueva_ronda();
                    }else{
                        seleccionado(px,py);
                    }
                    sensor_tapado = true;
                }
            }else{
                sensor_tapado=false;
            }

            barra_superior.dibujar(c);
            elemento_abajo.dibujar(c);
            elemento_arriba.dibujar(c);
            elemento_derecha.dibujar(c);
            elemento_izquierda.dibujar(c);
        }
        private void deseleccionar(){
            elemento_derecha.seleccionado=false;
            elemento_izquierda.seleccionado=false;
            elemento_abajo.seleccionado=false;
            elemento_arriba.seleccionado=false;
        }
        private void seleccionado(float px,float py){

            if(px>limite_der){
                if(pos_correcta==2){
                    puntos+=100;
                    nueva_ronda();
                }
            }else if(px<limite_izq){
                if(pos_correcta==3){
                    puntos+=100;
                    nueva_ronda();
                }
            }else if(py<limite_arriba){
                if(pos_correcta==0){
                    puntos+=100;
                    nueva_ronda();
                }
            }else if(py>limite_abajo){
                if(pos_correcta==1){
                    puntos+=100;
                    nueva_ronda();
                }
            }
        }
        private void nueva_ronda(){
            Random r=new Random();
            int v1=r.nextInt(100);
            int v2=r.nextInt(100);
            int v=r.nextInt(4);
            reset_elementos();
            pos_correcta=v;
            if(v==0) {
                elemento_arriba.valor = v1 + v2;
            }else if(v==1){
                elemento_abajo.valor=v1+v2;
            }else if(v==2){
                elemento_derecha.valor=v1+v2;
            }else if(v==3){
                elemento_izquierda.valor=v1+v2;
            }
            operacion=v1+"+"+v2+"=";
            ronda++;
        }
        private void reset_elementos(){
            Random r=new Random();
            int v1=r.nextInt(100);
            int v2=r.nextInt(100);
            elemento_abajo.valor=v1+v2;
            v1=r.nextInt(100);
            v2=r.nextInt(100);
            elemento_derecha.valor=v1+v2;
            v1=r.nextInt(100);
            v2=r.nextInt(100);
            elemento_izquierda.valor=v1+v2;
            v1=r.nextInt(100);
            v2=r.nextInt(100);
            elemento_arriba.valor = v1+v2;
        }
    }
    public class Barra_Superior extends Basico{

        private Jugador j;
        public Barra_Superior(Jugador j){
            this.j=j;
        }

        @Override
        protected void dibujar(Canvas c) {
            Paint p=new Paint();
            p.setStyle(Paint.Style.FILL);
            p.setColor(Color.LTGRAY);
            c.drawRect(0,0,c.getWidth(),100,p);
            p.setColor(Color.BLACK);
            p.setTextSize(50);
            c.drawText("Puntos:          "+j.puntos,20,70,p);
            p.setColor(Color.BLACK);
            p.setTextSize((j.ronda==1)?30:50);
            c.drawText(j.operacion,c.getWidth()/2-240,200,p);

        }
    }
    public class Elemento extends Basico{
        public int valor;
        public boolean seleccionado;
        private int id;

        public Elemento(int id){
            this.id=id;
        }

        @Override
        protected void dibujar(Canvas c) {
            Paint p=new Paint();
            p.setStyle(Paint.Style.STROKE);
            p.setColor(Color.GRAY);
            int pos_x=0,pos_y=0;
            if(id==0){
                pos_x=c.getWidth()/2;
                pos_y=c.getHeight()/2-100;
                if(seleccionado)
                   c.drawCircle(pos_x,pos_y,40,p);
            }else if(id==1){
                pos_x=c.getWidth()/2;
                pos_y=c.getHeight()/2+100;
                if(seleccionado)
                    c.drawCircle(pos_x,pos_y,40,p);
            }else if(id==2){
                pos_x=c.getWidth()-100;
                pos_y=c.getHeight()/2;
                if(seleccionado)
                    c.drawCircle(pos_x,pos_y,40,p);
            }else if(id==3){
                pos_x=100;
                pos_y=c.getHeight()/2;
                if(seleccionado)
                    c.drawCircle(pos_x,pos_y,40,p);
            }
            p.setStyle(Paint.Style.FILL);
            p.setColor(Color.BLACK);
            p.setTextSize(50);
            c.drawText(valor+"",pos_x-20,pos_y-20,p);
        }
    }
}
