package com.example.garabatos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.print.PrintHelper;

import java.util.HashMap;
import java.util.Map;

public class VistaGarabato extends View {

    //Constante para determinar si el usuario movió para dibujar
    private static final float TOLERANCIA_TOQUE=10;

    private Bitmap mapaBits; //área donde se dibuja
    private Canvas lienzoMapaBits; //utilizada para dibujar en el mapa de bits

    private Paint pantallaPintura; //para pintar el dibujo en pantalla
    private Paint lineaPintura;  //para dibujar línea en el mapa de bits

    //Mapas de los caminos actuales que se estan dibujando y los puntos que siguen esos caminos
    private Map<Integer, Path> mapaCaminos=new HashMap<>();
    private Map<Integer, Point> mapaPuntoPrevio= new HashMap<>();


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        //El mapa de bits se crea con la configuracion de color Trans., Rojo, Verde y Azul de 8 bits
        mapaBits=Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        lienzoMapaBits= new Canvas(mapaBits);
        mapaBits.eraseColor(Color.WHITE); //establecer el color de fondo del dibujo

    }

    public void limpiarPantalla(){
        mapaCaminos.clear();
        mapaPuntoPrevio.clear();
        mapaBits.eraseColor(Color.WHITE);
        invalidate();

    }//limpiarPantalla


    public void establecerColorLinea(int color){
        lineaPintura.setColor(color);

    }//establecerColorLinea()

    public int obtenerColorLinea(){
        return lineaPintura.getColor();

    }//obtenerColorLinea()

    public void establecerGrosorLinea(int grosor){
        lineaPintura.setStrokeWidth(grosor);

    }//establecerGrosorLinea

    public int obtenerGrosorLinea(){
        return (int) lineaPintura.getStrokeWidth();
    }

    //hacer el dibujo cada vez que se refresque la pantalla
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawBitmap(mapaBits, 0, 0, pantallaPintura);

        //for mejorado para dibujar cada camino del dibujo
        for(Integer llave: mapaCaminos.keySet())
            canvas.drawPath(mapaCaminos.get(llave), lineaPintura);//dibujar una línea

    }//onDraw

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int accion = event.getAction();
        int indiceAccion=event.getActionIndex();

        if(accion== MotionEvent.ACTION_DOWN || accion==MotionEvent.ACTION_POINTER_DOWN){
            toqueIniciado(event.getX(indiceAccion), event.getY(indiceAccion), event.getPointerId(indiceAccion));

        }//Si se levanta algun dedo de la pantalla
        else if(accion == MotionEvent.ACTION_UP || accion== MotionEvent.ACTION_POINTER_UP){
            toqueTerminado(event.getPointerId(indiceAccion));

        }// Si no se esta presionando la pantalla o levantando un dedo, la accion es de movimiento
        else{

            toqueMovimiento(event);
        }

        invalidate(); //refrescar la pantalla
        return true;
    }//onTouchEvent


    private void toqueIniciado(float x, float y, int  idLinea){
        Path camino;
        Point punto;

        //Si ya existe un camino con esa llave
        if(mapaCaminos.containsKey(idLinea)){
            camino= mapaCaminos.get(idLinea);  //obtener el camino existente
            camino.reset();                        //limpiar el camino, para iniciar uno nuevo
            punto=  mapaPuntoPrevio.get(idLinea);  //se obtiene el ultimo punto que toco esta línea
        }
        else{ //se trata de un nuevo camino(nueva línea)
            camino= new Path();                 //se crea un camino nuevo
            mapaCaminos.put(idLinea, camino);   //se guarda el camino en el mapa de caminos
            punto= new Point();
            mapaPuntoPrevio.put(idLinea, punto);
        }
        camino.moveTo(x, y);        //colocar el camino a la posicion del clic
        punto.x=(int) x;            //el punto actual esta en la posicion  del clic
        punto.y= (int) y;
    }//toqueIniciado


    private void toqueMovimiento(MotionEvent evento){

        //Por cada puntero(dedo) que se este moviendo sobre la pantalla
        for(int i=0; i< evento.getPointerCount(); i++){
            //Obtener el ID  y el indice del puntero
            int idPuntero= evento.getPointerId(i);
            int indicePuntero= evento.findPointerIndex(idPuntero);

            if(mapaCaminos.containsKey(idPuntero)){
                //Obtener las coordenadas del puntero
                float nuevoX = evento.getX(indicePuntero);
                float nuevoY = evento.getY(indicePuntero);

                //Obtener el camino y el punto previo asociados con el puntero
                Path camino = mapaCaminos.get(idPuntero);
                Point punto = mapaPuntoPrevio.get(idPuntero);

                //Calcular la distancia que el usuario movió el dedo desde la última vez
                float deltaX = Math.abs(punto.x - nuevoX);
                float deltaY = Math.abs(punto.y - nuevoY);

                //Si el usuario movió lo suficiente el dedo
                if (deltaX > TOLERANCIA_TOQUE || deltaY > TOLERANCIA_TOQUE) {
                    //Se une el punto previo con el nuevo punto, promedio del toque real del dedo
                    camino.quadTo(punto.x, punto.y, (punto.x + nuevoX) / 2, (punto.y + nuevoY) / 2);

                }

                //El camino será el punto previo del siguiente evento
                punto.x = (int) nuevoX;
                punto.y = (int) nuevoY;

            }
        }

    }//toqueMovimiento

    //llamado cuando el usuario finaliza un toque
    private void toqueTerminado(int idLinea){
        Path camino=mapaCaminos.get(idLinea);             //obtener el camino para estas líneas
        lienzoMapaBits.drawPath(camino, lineaPintura);    //dibujar el camino
        camino.reset();                                   //el camino se puede reutilizar para otra linea

    }//toqueTerminado


    public VistaGarabato(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        pantallaPintura= new Paint();

        lineaPintura=new Paint();
        lineaPintura.setAntiAlias(true); //para que las líneas diagonales no se vean pixeleadas
        lineaPintura.setColor(Color.BLACK);
        lineaPintura.setStyle(Paint.Style.STROKE);
        lineaPintura.setStrokeWidth(5); //La línea tiene 5 dp de grosor de forma predeterminada
        lineaPintura.setStrokeCap(Paint.Cap.ROUND); //Los extremos de la linea redondeada

    }//VistaGarabato

    protected void guardarImagen(){

        //El nombre del archivo de imagen
        final String nombre="Garabatos"+ System.currentTimeMillis()+ ".png";
        final String nombre2="Garabatos"+ System.currentTimeMillis()+ ".jpg";

        //Guardar la imagen en la carpeta 'Fotos' del dispositivo
        String ubicacion= MediaStore.Images.Media.insertImage(getContext().getContentResolver(),mapaBits,
                nombre, "Dibujo de Garabatos");

        if(ubicacion !=null){  //Si se guardo exitosamente la imagen
            Toast mensaje=Toast.makeText(getContext(), R.string.mensaje_guardado, Toast.LENGTH_LONG);
            mensaje.setGravity(Gravity.CENTER, mensaje.getXOffset()/2, mensaje.getYOffset()/2);
            mensaje.show();

        }else{                  //Ocurrioun error al guardar la imagen
            Toast mensaje=Toast.makeText(getContext(), R.string.mensaje_error_guardar, Toast.LENGTH_LONG);
            mensaje.setGravity(Gravity.CENTER, mensaje.getXOffset()/2, mensaje.getYOffset()/2);
            mensaje.show();

        }


    }//guardarImagen

    protected void imprimirImagen(){
        if(PrintHelper.systemSupportsPrint()){

            PrintHelper ayudanteImpresion= new PrintHelper(getContext());
            //Ajustar la imagen a los limites de la pagina e imprimirla
            ayudanteImpresion.setScaleMode((PrintHelper.SCALE_MODE_FIT));
            ayudanteImpresion.printBitmap("Imagen de Garabatos", mapaBits);

        }else{ //El sistema no soporta impresión
            Toast mensaje=Toast.makeText(getContext(), R.string.mensaje_error_impresión, Toast.LENGTH_LONG);
            mensaje.setGravity(Gravity.CENTER, mensaje.getXOffset()/2, mensaje.getYOffset()/2);
            mensaje.show();

        }

    }//ImprimirImagen
}// Vista Garabato
