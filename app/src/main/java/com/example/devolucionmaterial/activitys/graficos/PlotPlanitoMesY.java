package com.example.devolucionmaterial.activitys.graficos;

/**
 * Created by jonh on 04/05/18.
 */


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;



public class PlotPlanitoMesY extends View implements OnTouchListener {


    private Paint EjesSetup,Puntos1,Puntos2,Puntos3,Puntos4,Puntos5,Textx,Texty,Texty2,Titulo,TituloX,TituloY,TituloY2,Cuad,leyendas;
    private int ColorFondo=Color.rgb(0,0,0),ColorEjes=Color.rgb(255,255,255),ColorPuntos1=Color.GREEN,ColorPuntos2=Color.RED,ColorPuntos3=Color.MAGENTA,ColorPuntos4=Color.YELLOW,ColorPuntos5=Color.CYAN ,ColorTextx=Color.WHITE,ColorTexty=Color.WHITE,ColorTexty2=Color.WHITE,
            ColorTitulo=Color.rgb(68,138,255), ColorTituloX=Color.rgb(255,255,255),ColorTituloY=Color.rgb(255,255,255),ColorTituloY2=Color.rgb(255,255,255),ColorCuad=Color.rgb(55,55,55),ColorLeyendas=Color.rgb(255,255,255);
    private float[] x1,y1,x2,y2,x3,y3,x4,y4,x5,y5;
    private float maxx=10,maxy=10,minx=-10,miny=-10,maxy2=10,miny2=-10;
    private float alto,ancho,MarSup=40,MarInf=30,MarI=45,MarD=13;

    private boolean Ejey2=false, EscalaAutomatica=true,ActivaTouch=true,MaxMinFirstTime=true;
    private boolean UnirPuntos=true,UnirPuntos2=true,UnirPuntos3=true,UnirPuntos4=true,UnirPuntos5=true,GraficaSerie1=false,GraficaSerie2=false,GraficaSerie3=false,GraficaSerie4=false,GraficaSerie5=false;

    private int LengthPoints1,LengthPoints2,LengthPoints3,LengthPoints4,LengthPoints5;
    private int TamPunto=0,TamPunto2=3,TamPunto3=3,TamPunto4=3,TamPunto5=3,EjeSerie1=1,EjeSerie2=1,EjeSerie3=1,EjeSerie4=1,EjeSerie5=1,GruesoLinea=2;//tamaño de punto y eje "y" donde se grafica cada serie
    private int sizeTx=16,sizeTy=16,sizeTy2=16,sizeT=20,sizeTexX=18,sizeTexY1=18,sizeTexY2=11,sizeLeyend=14;
    private String Stitulo="X vs Y",Stitulox="X",Stituloy="Y1",Stituloy2="Y2",Tema,TituloSerie1,TituloSerie2,TituloSerie3,TituloSerie4,TituloSerie5;

    // variables touch
    private int dedo1x,dedo1y,dedo2x,dedo2y, xinicial,yinicial;
    private float minxInicial,maxxInicial,minyInicial,maxyInicial;


    //
    private String mes[]={"Oct","Nov","Dic","Ene","Feb","Mar","Abr","May","Jun","Jul","Ago","Sep"};

    public PlotPlanitoMesY(Context lienzo, String TitPrincipal, String TitX, String TitY)
    {
        super(lienzo);
        EjesSetup = new Paint();
        Puntos1 = new Paint();Puntos2 = new Paint();Puntos3 = new Paint();Puntos4 = new Paint();Puntos5 = new Paint();
        leyendas=new Paint();
        Texty=new Paint(); Texty2=new Paint();Textx = new Paint();Titulo = new Paint();
        TituloX = new Paint();TituloY = new Paint();TituloY2 = new Paint();Cuad = new Paint();
        this.Stitulo=TitPrincipal;this.Stitulox=TitX;this.Stituloy=TitY;

        //to make it focusable so that it will receive touch events properly
        setFocusable(true);
        //adding touch listener to this view
        this.setOnTouchListener(this);
        //  PuntosAjuste(x1, y1);

    }

    public void SetSerie1( float[] xvalues, float[] yvalues,String Titulo,int tp,boolean Unirpuntos){
        this.UnirPuntos=Unirpuntos; //EjeSerie1=eje; // esta linea nunca se usa la serie1 por default va en y1 siempre
        this.TamPunto=tp;
        GraficaSerie1=true;
        this.TituloSerie1=Titulo;
        LengthPoints1 = xvalues.length;
        x1= new float[LengthPoints1];
        y1=new float[LengthPoints1];
        x1=xvalues;
        y1=yvalues;
        if(EscalaAutomatica)
        {
            ObtMinMax(x1, y1);Ejey2=false;

        } else{SetEscalaAutomatica(false);}
    }
    public void SetSerie2(float[] xvalues, float[] yvalues,String Titulo ,int tp ,boolean Unirpuntos,int eje){
        this.TituloSerie2=Titulo;    this.UnirPuntos2=Unirpuntos; EjeSerie2=eje; GraficaSerie2=true;
        this.TamPunto2=tp;
        LengthPoints2 = xvalues.length;
        x2= new float[LengthPoints2];
        y2=new float[LengthPoints2];
        x2=xvalues;
        y2=yvalues;
        if(EscalaAutomatica)
        {
            if(eje==1) {ObtMinMax(x2, y2);}
            if(eje==2){ObtMinMaxY2(x2,y2);Ejey2=true;}
        } else{SetEscalaAutomatica(false);if(eje==2){Ejey2=true;}}
    }

    public void SetSerie3(float[] xvalues, float[] yvalues,String Titulo ,int tp,boolean Unirpuntos,int eje){
        this.TituloSerie3=Titulo;    this.UnirPuntos3=Unirpuntos;  EjeSerie3=eje;  GraficaSerie3=true;
        this.TamPunto3=tp;
        LengthPoints3 = xvalues.length;
        x3= new float[LengthPoints3];
        y3=new float[LengthPoints3];
        x3=xvalues;
        y3=yvalues;
        if(EscalaAutomatica)
        {
            if(eje==1) {ObtMinMax(x3, y3);}
            if(eje==2){ObtMinMaxY2(x3,y3);Ejey2=true;}
        } else{SetEscalaAutomatica(false);if(eje==2){Ejey2=true;}}
    }

    public void SetSerie4(float[] xvalues, float[] yvalues,String Titulo ,int tp,boolean Unirpuntos,int eje){
        this.TituloSerie4=Titulo;    this.UnirPuntos4=Unirpuntos;  EjeSerie4=eje;  GraficaSerie4=true;
        TamPunto4=tp;
        LengthPoints4 = xvalues.length;
        x4= new float[LengthPoints4];
        y4=new float[LengthPoints4];
        x4=xvalues;
        y4=yvalues;
        if(EscalaAutomatica)
        {
            if(eje==1) {ObtMinMax(x4, y4);}
            if(eje==2){ObtMinMaxY2(x4,y4);Ejey2=true;}
        } else{SetEscalaAutomatica(false);if(eje==2){Ejey2=true;}}
    }

    public void SetSerie5(float[] xvalues, float[] yvalues,String Titulo ,int tp,boolean Unirpuntos,int eje){
        this.TituloSerie5=Titulo;    this.UnirPuntos5=Unirpuntos;  EjeSerie5=eje;  GraficaSerie5=true;
        TamPunto5=tp;
        LengthPoints5 = xvalues.length;
        x5= new float[LengthPoints5];
        y5=new float[LengthPoints5];
        x5=xvalues;
        y5=yvalues;
        if(EscalaAutomatica)
        {
            if(eje==1) {ObtMinMax(x5, y5);}
            if(eje==2){ObtMinMaxY2(x5,y5);Ejey2=true;}
        } else{SetEscalaAutomatica(false);if(eje==2){Ejey2=true;}}
    }

    protected void onDraw(Canvas canvas) {
        alto=getHeight()-(MarSup+MarInf);
        ancho=getWidth()-(MarI+MarD);
        //canvas.drawRGB(0,0,0);
        Setup();
        canvas.drawColor(ColorFondo);

        /*
        ///////////////////      FONDO              ////////
        if (Tema=="claro") {canvas.drawRGB(255, 255,255);}// color de fondo	OBSCURO
        if(Tema=="obscuro"){{canvas.drawRGB(0, 0,0);}}
        if(Tema=="colorido"){
            Paint gradPaint = new Paint();
            gradPaint.setShader(new LinearGradient(0,0,0,getHeight(),Color.YELLOW,Color.GREEN, Shader.TileMode.CLAMP));
            canvas.drawPaint(gradPaint);
        }
        */

        ///////////////////////////////// CUADRICULA Y NUMEROS//////////////////////////////////////////////////
        canvas.drawLine(MarI,MarSup,MarI+ancho,MarSup,Cuad);canvas.drawLine(MarI, MarSup + alto, MarI + ancho, MarSup + alto, Cuad);//estas 4 lineas
        canvas.drawLine(MarI, MarSup, MarI, MarSup + alto, Cuad);canvas.drawLine(MarI + ancho, MarSup, MarI + ancho, MarSup + alto, Cuad);//son el cudrado que encierra toda la grafica

        canvas.drawText("" + Redondear((double) miny, 2), MarI - 30, MarSup + alto , Texty);  canvas.drawText("" + Redondear((double) maxy, 2), MarI - 30, MarSup + 5, Texty);

        float posx,posy;
        float [] intervalosY=Intervalos(miny,maxy,5);// el 5 son # de intervalos
        float [] intervalosX=Intervalos(minx,maxx,12);
        float [] intervalosY2=Intervalos(miny2,maxy2,5);

        for(int i=0;i<4;i++)
        {//no se imprime la ultima linea o valor
            if(intervalosY[i]==maxy){}//valores Y1
            else{posy =toPixelInt(alto,miny,maxy,intervalosY[i]);
                canvas.drawLine(MarI, (MarSup + alto) - posy, MarI + ancho, (MarSup + alto) - posy, Cuad);
                canvas.drawText("" + Redondear((double) intervalosY[i], 2), MarI-30, MarSup + alto - posy+5, Texty);}





        }





        ////////////////////////////////   EJES   X ,Y, titulo principal, tituloX tituloY1 tituloY2   //////////////////////////////////////////////////////////
        if(Ejey2==false)
        {
            if (miny < 0 && maxy > 0)//eje x
            {   int locxAxisInPixels = toPixelInt(alto, miny, maxy, 0);
                canvas.drawLine(MarI, (alto + MarSup) - locxAxisInPixels, (ancho + MarI), (alto + MarSup) - locxAxisInPixels, EjesSetup);//EJE X}
            }
        }
      //  if (minx < 0 && maxx > 0)//eje y
        //{   int locyAxisInPixels = toPixelInt(ancho, minx, maxx, 0);
          //  canvas.drawLine(locyAxisInPixels + MarI, MarSup, locyAxisInPixels + MarI, alto + MarSup, EjesSetup);//  EJE Y
        //}

        canvas.drawText(Stitulo,(0.33f)*(getWidth()), 25, Titulo);
        //canvas.drawText(Stitulox, MarI + (ancho / 2), (alto + MarSup) + 40, TituloX);
       // canvas.save();canvas.rotate(-90, 15, MarSup + (alto / 2));
        //ejes y1 i y2
        //canvas.drawText(Stituloy, 25, MarSup + (alto / 2), TituloY);canvas.restore();
        if(Ejey2) {  canvas.save();  canvas.rotate(-90, getWidth()-4, MarSup + (alto / 2)); canvas.drawText(Stituloy2,getWidth()-4,MarSup + (alto / 2),TituloY2);  canvas.restore(); }


        ////////////            PUNTOS Y LINEAS QUE UNEN LOS PUNTOS     GRAFICA     1               //////////////////////////////////////////
        if(GraficaSerie1)
        {
            int[] xvaluesInPixels = toPixel(ancho, minx, maxx, x1); // SE CONVIERTE A PIXELES LOS x1,y1
            int[] yvaluesInPixels= toPixel(alto, miny, maxy, y1);


            for (int i = 0; i < LengthPoints1; i++) {   //este if contrala que no se pinte fuera del magren
                if ((xvaluesInPixels[i] + MarI) < MarI || xvaluesInPixels[i] > (ancho + 1) || ((alto + MarSup) - yvaluesInPixels[i]) < (MarSup - 1) || ((alto + MarSup) - yvaluesInPixels[i]) > (MarSup + alto + 1)) {
                } else {
                    canvas.drawCircle((xvaluesInPixels[i] + MarI), (alto + MarSup) - yvaluesInPixels[i], TamPunto, Puntos1);
                    canvas.drawText(""+ Redondear((double) y1[i], 2),(xvaluesInPixels[i] + MarI),(alto + MarSup) - yvaluesInPixels[i]-5,leyendas);
                    canvas.drawText(mes[i],(xvaluesInPixels[i] + MarI),(alto + MarSup)+20,Textx);

                    canvas.drawLine((xvaluesInPixels[i] + MarI),MarSup,(xvaluesInPixels[i] + MarI),MarSup+alto,Cuad);

                    if (UnirPuntos) {
                        if (i > 0)// este if asegura dibujar la union de puntos hasta q se tienen dos puntos
                        {    // este if asegura  no se pinte fuera de los margenes
                            if ((-yvaluesInPixels[i - 1] + MarSup + alto) < MarSup || ((-yvaluesInPixels[i - 1] + (MarSup + alto))) > (MarSup + alto) ||
                                    (xvaluesInPixels[i - 1] + MarI) < MarI || (xvaluesInPixels[i - 1] > (ancho)) ||
                                    (-yvaluesInPixels[i] + MarSup + alto) < MarSup || ((-yvaluesInPixels[i] + (MarSup + alto))) > (MarSup + alto) ||
                                    (xvaluesInPixels[i] + MarI) < MarI || (xvaluesInPixels[i] > (ancho))
                                    ) {
                            } else {
                                canvas.drawLine((xvaluesInPixels[i - 1] + MarI), (alto + MarSup) - yvaluesInPixels[i - 1], (xvaluesInPixels[i] + MarI), (alto + MarSup) - yvaluesInPixels[i], Puntos1);
                            }
                        }
                    }

                }
            }

           // canvas.drawLine((0.2f)*ancho, alto + MarInf, 0.2f*ancho + 80, alto + MarInf, Puntos1);
            //canvas.drawText(TituloSerie1, (0.2f)*ancho, getHeight() - 20, leyendas);
        }

        ////////////          PUNTOS Y LINEAS QUE UNEN LOS PUNTOS   GRAFICA   2                  //////////////////////////////////////////
        if(GraficaSerie2) {
            int[] xvaluesInPixels2 = toPixel(ancho, minx, maxx, x2); // SE CONVIERTE A PIXELES LOS x1,y1
            int [] yvaluesInPixels2;
            if(EjeSerie2==2){yvaluesInPixels2 = toPixel(alto, miny2, maxy2, y2);}else{ yvaluesInPixels2 = toPixel(alto, miny, maxy, y2);}
            for (int i = 0; i < LengthPoints2; i++) {   //este if contrala que no se pinte fuera del magren
                if ((xvaluesInPixels2[i] + MarI) < MarI || xvaluesInPixels2[i] > (ancho + 1) || ((alto + MarSup) - yvaluesInPixels2[i]) < (MarSup - 1) || ((alto + MarSup) - yvaluesInPixels2[i]) > (MarSup + alto + 1)) {
                } else
                {
                    canvas.drawCircle((xvaluesInPixels2[i] + MarI), (alto + MarSup) - yvaluesInPixels2[i], TamPunto2, Puntos2);
                    if (UnirPuntos2) {
                        if (i > 0)// este if asegura dibujar la union de puntos hasta q se tienen dos puntos
                        {    // este if asegura  no se pinte fuera de los margenes
                            if ((-yvaluesInPixels2[i - 1] + MarSup + alto) < MarSup || ((-yvaluesInPixels2[i - 1] + (MarSup + alto))) > (MarSup + alto) ||
                                    (xvaluesInPixels2[i - 1] + MarI) < MarI || (xvaluesInPixels2[i - 1] > (ancho)) ||
                                    (-yvaluesInPixels2[i] + MarSup + alto) < MarSup || ((-yvaluesInPixels2[i] + (MarSup + alto))) > (MarSup + alto) ||
                                    (xvaluesInPixels2[i] + MarI) < MarI || (xvaluesInPixels2[i] > (ancho))
                                    ) {
                            } else {

                                canvas.drawLine((xvaluesInPixels2[i - 1] + MarI), (alto + MarSup) - yvaluesInPixels2[i - 1], (xvaluesInPixels2[i] + MarI), (alto + MarSup) - yvaluesInPixels2[i], Puntos2);
                            }
                        }
                    }

                }
            }
            canvas.drawLine(0.4f*ancho, alto + MarInf, 0.4f*ancho + 80, alto + MarInf, Puntos2);
            canvas.drawText(TituloSerie2, 0.4f*ancho, getHeight() - 20, leyendas);
        }

        ////////////   PUNTOS Y LINEAS QUE UNEN LOS PUNTOS GRAFICA 3      //////////////////////////////////////////
        if(GraficaSerie3) {
            int[] xvaluesInPixels3 = toPixel(ancho, minx, maxx, x3); // SE CONVIERTE A PIXELES LOS x1,y1
            int [] yvaluesInPixels3;
            if(EjeSerie3==2){ yvaluesInPixels3 = toPixel(alto, miny2, maxy2, y3);} else{ yvaluesInPixels3 = toPixel(alto, miny, maxy, y3);}
            for (int i = 0; i < LengthPoints3; i++) {   //este if contrala que no se pinte fuera del magren
                if ((xvaluesInPixels3[i] + MarI) < MarI || xvaluesInPixels3[i] > (ancho + 1) || ((alto + MarSup) - yvaluesInPixels3[i]) < (MarSup - 1) || ((alto + MarSup) - yvaluesInPixels3[i]) > (MarSup + alto + 1)) {
                } else {
                    canvas.drawCircle((xvaluesInPixels3[i] + MarI), (alto + MarSup) - yvaluesInPixels3[i], TamPunto3, Puntos3);
                    if (UnirPuntos3) {
                        if (i > 0)// este if asegura dibujar la union de puntos hasta q se tienen dos puntos
                        {    // este if asegura  no se pinte fuera de los margenes
                            if ((-yvaluesInPixels3[i - 1] + MarSup + alto) < MarSup || ((-yvaluesInPixels3[i - 1] + (MarSup + alto))) > (MarSup + alto) ||
                                    (xvaluesInPixels3[i - 1] + MarI) < MarI || (xvaluesInPixels3[i - 1] > (ancho)) ||
                                    (-yvaluesInPixels3[i] + MarSup + alto) < MarSup || ((-yvaluesInPixels3[i] + (MarSup + alto))) > (MarSup + alto) ||
                                    (xvaluesInPixels3[i] + MarI) < MarI || (xvaluesInPixels3[i] > (ancho))
                                    ) {
                            } else {
                                canvas.drawLine((xvaluesInPixels3[i - 1] + MarI), (alto + MarSup) - yvaluesInPixels3[i - 1], (xvaluesInPixels3[i] + MarI), (alto + MarSup) - yvaluesInPixels3[i], Puntos3);
                            }
                        }
                    }

                }
            }
            canvas.drawLine(0.6f*ancho, alto + MarInf, 0.6f*ancho + 80, alto + MarInf, Puntos3);
            canvas.drawText(TituloSerie3, 0.6f*ancho, getHeight() - 20, leyendas);
        }

        ////////////   PUNTOS Y LINEAS QUE UNEN LOS PUNTOS GRAFICA 4      //////////////////////////////////////////
        if(GraficaSerie4) {
            int[] xvaluesInPixels4 = toPixel(ancho, minx, maxx, x4); // SE CONVIERTE A PIXELES LOS x1,y1
            int[] yvaluesInPixels4;
            if (EjeSerie4 == 2) {
                yvaluesInPixels4 = toPixel(alto, miny2, maxy2, y4);
            } else {
                yvaluesInPixels4 = toPixel(alto, miny, maxy, y4);
            }
            for (int i = 0; i < LengthPoints4; i++) {   //este if contrala que no se pinte fuera del magren
                if ((xvaluesInPixels4[i] + MarI) < MarI || xvaluesInPixels4[i] > (ancho + 1) || ((alto + MarSup) - yvaluesInPixels4[i]) < (MarSup - 1) || ((alto + MarSup) - yvaluesInPixels4[i]) > (MarSup + alto + 1)) {
                } else {
                    canvas.drawCircle((xvaluesInPixels4[i] + MarI), (alto + MarSup) - yvaluesInPixels4[i], TamPunto4, Puntos4);
                    if (UnirPuntos4) {
                        if (i > 0)// este if asegura dibujar la union de puntos hasta q se tienen dos puntos
                        {    // este if asegura  no se pinte fuera de los margenes
                            if ((-yvaluesInPixels4[i - 1] + MarSup + alto) < MarSup || ((-yvaluesInPixels4[i - 1] + (MarSup + alto))) > (MarSup + alto) ||
                                    (xvaluesInPixels4[i - 1] + MarI) < MarI || (xvaluesInPixels4[i - 1] > (ancho)) ||
                                    (-yvaluesInPixels4[i] + MarSup + alto) < MarSup || ((-yvaluesInPixels4[i] + (MarSup + alto))) > (MarSup + alto) ||
                                    (xvaluesInPixels4[i] + MarI) < MarI || (xvaluesInPixels4[i] > (ancho))
                                    ) {
                            } else {
                                canvas.drawLine((xvaluesInPixels4[i - 1] + MarI), (alto + MarSup) - yvaluesInPixels4[i - 1], (xvaluesInPixels4[i] + MarI), (alto + MarSup) - yvaluesInPixels4[i], Puntos4);
                            }
                        }
                    }

                }
            }

            canvas.drawLine(0.8f * ancho, alto + MarInf, 0.8f * ancho + 80, alto + MarInf, Puntos4);
            canvas.drawText(TituloSerie4, 0.8f * ancho, getHeight() - 20, leyendas);
        }

        ////////////   PUNTOS Y LINEAS QUE UNEN LOS PUNTOS GRAFICA 5      //////////////////////////////////////////
        if(GraficaSerie5) {
            int[] xvaluesInPixels5 = toPixel(ancho, minx, maxx, x5); // SE CONVIERTE A PIXELES LOS x1,y1
            int[] yvaluesInPixels5;
            if (EjeSerie5 == 2) {
                yvaluesInPixels5 = toPixel(alto, miny2, maxy2, y5);
            } else {
                yvaluesInPixels5 = toPixel(alto, miny, maxy, y5);
            }
            for (int i = 0; i < LengthPoints5; i++) {   //este if contrala que no se pinte fuera del magren
                if ((xvaluesInPixels5[i] + MarI) < MarI || xvaluesInPixels5[i] > (ancho + 1) || ((alto + MarSup) - yvaluesInPixels5[i]) < (MarSup - 1) || ((alto + MarSup) - yvaluesInPixels5[i]) > (MarSup + alto + 1)) {
                } else {
                    canvas.drawCircle((xvaluesInPixels5[i] + MarI), (alto + MarSup) - yvaluesInPixels5[i], TamPunto5, Puntos5);
                    if (UnirPuntos5) {
                        if (i > 0)// este if asegura dibujar la union de puntos hasta q se tienen dos puntos
                        {    // este if asegura  no se pinte fuera de los margenes
                            if ((-yvaluesInPixels5[i - 1] + MarSup + alto) < MarSup || ((-yvaluesInPixels5[i - 1] + (MarSup + alto))) > (MarSup + alto) ||
                                    (xvaluesInPixels5[i - 1] + MarI) < MarI || (xvaluesInPixels5[i - 1] > (ancho)) ||
                                    (-yvaluesInPixels5[i] + MarSup + alto) < MarSup || ((-yvaluesInPixels5[i] + (MarSup + alto))) > (MarSup + alto) ||
                                    (xvaluesInPixels5[i] + MarI) < MarI || (xvaluesInPixels5[i] > (ancho))
                                    ) {
                            } else {
                                canvas.drawLine((xvaluesInPixels5[i - 1] + MarI), (alto + MarSup) - yvaluesInPixels5[i - 1], (xvaluesInPixels5[i] + MarI), (alto + MarSup) - yvaluesInPixels5[i], Puntos5);
                            }
                        }
                    }

                }
            }

            canvas.drawLine(ancho, alto + MarInf, ancho + 80, alto + MarInf, Puntos5);
            canvas.drawText(TituloSerie5, ancho, getHeight() - 20, leyendas);


        }

        // -------------------------------------------------

    }

    private void Setup(){
        Cuad.setStrokeWidth(1);
        Cuad.setColor(ColorCuad);

        Texty.setTextAlign(Paint.Align.LEFT);
        Texty.setTextSize(sizeTexY1);
        Texty.setColor(ColorTexty);

        Texty2.setTextAlign(Paint.Align.LEFT);
        Texty2.setTextSize(sizeTexY2);
        Texty2.setColor(ColorTexty2);

        Textx.setTextAlign(Paint.Align.CENTER);
        Textx.setTextSize(sizeTexX);
        Textx.setColor(ColorTextx);

        TituloX.setTextAlign(Paint.Align.CENTER);
        TituloX.setTextSize(sizeTx);
        TituloX.setColor(ColorTituloX);

        //Titulo.setTextAlign(Paint.Align.CENTER);
        Titulo.setTextSize(sizeT);
        Titulo.setColor(ColorTitulo);

        TituloY.setTextAlign(Paint.Align.CENTER);
        TituloY.setTextSize(sizeTy);
        TituloY.setColor(ColorTituloY);

        TituloY2.setTextAlign(Paint.Align.CENTER);
        TituloY2.setTextSize(sizeTy2);
        TituloY2.setColor(ColorTituloY2);

        leyendas.setTextAlign(Paint.Align.LEFT);
        leyendas.setTextSize(sizeLeyend);
        leyendas.setColor(ColorLeyendas);


        EjesSetup.setStrokeWidth(1);//grueso de linea
        EjesSetup.setColor(ColorEjes);

        Puntos1.setStrokeWidth(GruesoLinea);
        Puntos1.setColor(ColorPuntos1);

        Puntos2.setStrokeWidth(GruesoLinea);
        Puntos2.setColor(ColorPuntos2);

        Puntos3.setStrokeWidth(GruesoLinea);
        Puntos3.setColor(ColorPuntos3);

        Puntos4.setStrokeWidth(GruesoLinea);
        Puntos4.setColor(ColorPuntos4);

        Puntos5.setStrokeWidth(GruesoLinea);
        Puntos5.setColor(ColorPuntos5);
    }



    private void ObtMinMax(float[] xvalues, float[] yvalues) {
        float pequex,pequey,grandex,grandey;
        pequex=ObtMin(xvalues);grandex=ObtMax(xvalues);
        pequey=ObtMin(yvalues);grandey=ObtMax(yvalues);

        if(MaxMinFirstTime){minx=pequex;maxx=grandex;miny=pequey;maxy=grandey;MaxMinFirstTime=false;}
        else{if(pequex<minx){minx=pequex;}if(grandex>maxx){maxx=grandex;}
            if(pequey<miny){miny=pequey;}if(grandey>maxy){maxy=grandey;}}

        minxInicial=minx;minyInicial=miny;
        maxxInicial=maxx;maxyInicial=maxy;
    }


    private void ObtMinMaxY2(float[] xvalues,float[] yvalues){
        float grandey2=ObtMax(yvalues),pequey2=ObtMin(yvalues);
        float grandex=ObtMax(xvalues),pequex=ObtMin(xvalues);
        if(pequex<minx){minx=pequex;}if(grandex>maxx){maxx=grandex;}
        if(grandey2>maxy2){maxy2=grandey2;}if(pequey2<miny2){miny2=pequey2;}
    }

    private int[] toPixel(float pixels, float min, float max, float[] value) {
        double[] p = new double[value.length];
        int[] pint = new int[value.length];
        // nunca retorna el ultimo punto
        for (int i = 0; i < value.length; i++) {
            p[i] =((value[i]-min)/(max-min))*pixels;
            pint[i] = (int)p[i];
        }
        return (pint);
    }

    // este metodo solo retorna 1 solo valor en pixeles
    private int toPixelInt(float pixels, float min, float max, float value) {
        double p;
        int pint;
        p =((value-min)/(max-min))*pixels;
        pint = (int)p;
        return (pint);
    }

    private float ObtMax(float[] v) {
        float elMayor = v[0];
        for (int i = 0; i < v.length; i++)
            if (v[i] > elMayor)
                elMayor = v[i];
        return elMayor;
    }

    private float ObtMin(float[] v)
    {
        float elMasPeque = v[0];
        for (int i = 0; i < v.length; i++)
            if (v[i] < elMasPeque)
                elMasPeque = v[i];
        return elMasPeque;
    }

    private static double Redondear(double numero,int digitos)
    {   String N=""+numero; int PosPunto=0;
        for(int i=0;i<N.length();i++)
        {
            if(N.substring(i,i+1)==".") {PosPunto=i;}
        }
        if((N.length()-(PosPunto+1))>digitos)
        {   int cifras=(int) Math.pow(10,digitos);
            return Math.rint(numero*cifras)/cifras;
        }
        else {return numero;}
    }

    // este metodo convierte de float a double
    private static double[] ConFloatAdouble(float[] input)
    {
        if (input == null)
        {
            return null; // Or throw an exception - your choice
        }
        double[] output = new double[input.length];
        for (int i = 0; i < input.length; i++)
        {
            output[i] = input[i];
        }
        return output;
    }

    private float [] Intervalos(float min,float max,int n)
    {
        float [] Intervalo=new float [n];
        float temp;

        if((max-min)>n)//asugura anteros por ser mayor a n2
        {
            for(int i=1;i<=n;i++)
            {
                temp=(float)Math.rint((min+(i)*((max-min)/n)));if(temp==-0.0)temp=0;
                if(temp>=max)temp=max;
                Intervalo[i-1]=temp;

            }
        }
        else// valores con decimal por superar n
        {
            for(int i=1;i<=n;i++)
            {
                temp=min+(i)*((max-min)/n);if(temp==-0.0)temp=0;
                if(temp>=max)temp=max;
                Intervalo[i-1]=temp;

            }
        }
        return Intervalo;
    }

    //todos los efectos del touch estan dentro de este metodo
    // los efectos de zoom y de traslacion se efectuan al variar loas valores minx,maxx,miny,maxy
    //dentro de este metodo
    public boolean onTouch(View view,MotionEvent event)
    {
        if(ActivaTouch)
        {
            int action = MotionEventCompat.getActionMasked(event);
            final int fingersCount = event.getPointerCount();


            switch (action) {
                case (MotionEvent.ACTION_DOWN):
                    xinicial = (int) event.getX(0);
                    yinicial = (int) event.getY(0);
                    //La accion ha sido ABAJO
                    return true;

                case (MotionEvent.ACTION_MOVE):
                    //"La acción ha sido MOVER");
                    dedo1x = (int) event.getX(0);
                    dedo1y = (int) event.getY(0);

                    if (fingersCount == 1) {
                        if (dedo1x < xinicial) {
                            maxx = maxx + (((maxx - minx) / (ancho + 11000)) * Math.abs(dedo1x - xinicial));
                            minx = minx + (((maxx - minx) / (ancho + 11000)) * Math.abs(dedo1x - xinicial));
                        }
                        if (dedo1x > xinicial) {
                            maxx = maxx - ((maxx - minx) / (ancho + 11000)) * Math.abs(dedo1x - xinicial);
                            minx = minx - (((maxx - minx) / (ancho + 11000)) * Math.abs(dedo1x - xinicial));
                        }
                        if (dedo1y > yinicial) {
                            maxy = maxy + ((maxy - miny) / (alto + 11000)) * Math.abs(dedo1y - yinicial);
                            miny = miny + ((maxy - miny) / (alto + 11000)) * Math.abs(dedo1y - yinicial);
                            maxy2 = maxy2 + ((maxy2 - miny2) / (alto + 11000)) * Math.abs(dedo1y - yinicial);//ajuste de escala para y2
                            miny2 = miny2 + ((maxy2 - miny2) / (alto + 11000)) * Math.abs(dedo1y - yinicial);
                        }
                        if (dedo1y < yinicial) {
                            maxy = maxy - ((maxy - miny) / (alto + 11000)) * Math.abs(dedo1y - yinicial);
                            miny = miny - ((maxy - miny) / (alto + 11000)) * Math.abs(dedo1y - yinicial);
                            maxy2 = maxy2 - ((maxy2 - miny2) / (alto + 11000)) * Math.abs(dedo1y - yinicial);//ajuste de escala para y2
                            miny2 = miny2 - ((maxy2 - miny2) / (alto + 11000)) * Math.abs(dedo1y - yinicial);
                        }
                    }

                    if (fingersCount == 2) {
                        double d0x, d0y, d1x, d1y;
                        dedo2x = (int) event.getX(1);
                        dedo2y = (int) event.getY(1);
                        d0x = Math.abs(dedo2x - xinicial);
                        d0y = Math.abs(dedo2y - yinicial);
                        d1x = Math.abs(dedo2x - event.getX(0));
                        d1y = Math.abs(dedo2y - event.getY(0));
                        if (d1x < d0x || d1y < d0y) {
                            minx = minx - ((maxx - minx) / 94);
                            maxx = maxx + ((maxx - minx) / 94);
                            miny = miny - ((maxy - minx) / 94);
                            maxy = maxy + ((maxy - miny) / 94);
                            miny2 = miny2 - ((maxy2 - minx) / 94);
                            maxy2 = maxy2 + ((maxy2 - miny2) / 94);// y2

                        }
                        if (d1x > d0x || d1y > d0y) {
                            minx = minx + ((maxx - minx) / 94);
                            maxx = maxx - ((maxx - minx) / 94);
                            miny = miny + ((maxy - minx) / 94);
                            maxy = maxy - ((maxy - miny) / 94);
                            miny2 = miny2 + ((maxy2 - minx) / 94);
                            maxy2 = maxy2 - ((maxy2 - miny2) / 94); //y2
                        }


                    }
                    invalidate();//esta linea es necesaria para volver a ejecutar el metodo ondraw

                    return true;
                case (MotionEvent.ACTION_UP):

                    return true;

                default:
                    return super.onTouchEvent(event);
            }

        }

        // invalidate();   return true;   //calls onDraw method
        else {return false;}
    }






    //METODOS SET
    public void SetEjey2(String tituloy2){
        Ejey2=true;
        if (tituloy2==""){Stituloy2="Y2";}
        else{Stituloy2=tituloy2;}
    }
    public void SetEscalaAutomatica(boolean auto){
        if(auto) {
            EscalaAutomatica = true;
        }else{EscalaAutomatica=false;}
    }
    public void SetEscalaX(double minX,double maxX){
        minx=(float)minX; maxx=(float) maxX;
    }
    public void SetEscalaY1(double minY1,double maxY1){
        miny=(float)minY1; maxy=(float)maxY1;
    }
    public void SetEscalaY2(double minY2,double maxY2){
        miny2=(float)minY2; maxy2=(float)maxY2;
    }
    public void SetShowEjey2(boolean mostrar){
        if(mostrar)Ejey2=true;
        else {Ejey2=false;}
    }

    public void SetHD(boolean hd){
        if(hd)
        {
            Puntos1.setAntiAlias(true);Puntos2.setAntiAlias(true);Puntos3.setAntiAlias(true);Puntos4.setAntiAlias(true);Puntos5.setAntiAlias(true);
            EjesSetup.setAntiAlias(true);Textx.setAntiAlias(true);Texty.setAntiAlias(true);Texty2.setAntiAlias(true);
            Titulo.setAntiAlias(true);TituloX.setAntiAlias(true);TituloY.setAntiAlias(true);TituloY2.setAntiAlias(true);
            Cuad.setAntiAlias(true);
        }
        else{
            Puntos1.setAntiAlias(false);Puntos2.setAntiAlias(false);Puntos3.setAntiAlias(false);Puntos4.setAntiAlias(false);Puntos5.setAntiAlias(false);
            EjesSetup.setAntiAlias(false);Textx.setAntiAlias(false);Texty.setAntiAlias(false);Texty2.setAntiAlias(false);
            Titulo.setAntiAlias(false);TituloX.setAntiAlias(false);TituloY.setAntiAlias(false);TituloY2.setAntiAlias(false);
            Cuad.setAntiAlias(false);
        }
    }


    public void  SetSizeTextX(int z){
        if(z>=7 && z<=25){sizeTexX=z;}else{ sizeTexX=11;}
    }
    public void  SetSizeTextY1(int z){
        if(z>=7 && z<=25){sizeTexY1=z;} else{sizeTexY1=11;}
    }
    public void  SetSizeTextY2(int z){
        if(z>=7 && z<=25){sizeTexY2=z;} else{sizeTexY2=11;}
    }
    public void  SetSizeTitulo(int z){
        if(z>=7 && z<=50){sizeT=z;} else{sizeT=26;}
    }
    public void  SetSizeTituloX(int z){if(z>=7 && z<=40){sizeTx=z;} else{sizeTx=16;}}
    public void  SetSizeTituloY1(int z){
        if(z>=7 && z<=40){sizeTy=z;} else{sizeTy=16;}
    }
    public void  SetSizeTituloY2(int z){
        if(z>=7 && z<=40){sizeTy2=z;} else{sizeTy2=16;}
    }
    public void SetSizeLeyend(int z){
        if(z>=7 && z<=25){sizeLeyend=z;} else{sizeLeyend=10;}
    }
    public void SetColorFondo(int a,int r,int g,int b)       {ColorFondo=Color.argb(a,r,g,b);}// TODO se cambio esta linea
    public void SetColorEjes(int r,int g,int b)        {ColorEjes=Color.rgb(r,g,b);}
    public void SetColorCuadricula(int r, int g, int b){ColorCuad=Color.rgb(r,g,b);}
    public void SetColorTitulo(int r,int g,int b)      {ColorTitulo=Color.rgb(r,g,b); }
    public void SetColorTituloX(int r,int g,int b)     {ColorTituloX=Color.rgb(r,g,b);}
    public void SetColorTituloY(int r,int g,int b)     {ColorTituloY=Color.rgb(r,g,b);}
    public void SetColorTituloY2(int r,int g,int b)    {ColorTituloY2=Color.rgb(r,g,b);}
    public void SetColorTextX(int r, int g, int b)     {ColorTextx=Color.rgb(r,g,b);}
    public void SetColorTextY1(int r, int g, int b)    {ColorTexty=Color.rgb(r,g,b);}
    public void SetColorTextY2(int r, int g, int b)    {ColorTexty2=Color.rgb(r,g,b);}
    public void  SetColorSerie1(int r, int g, int b){ColorPuntos1=Color.rgb(r,g,b);}
    public void  SetColorSerie2(int r, int g, int b){ColorPuntos2=Color.rgb(r,g,b);}
    public void  SetColorSerie3(int r, int g, int b){ColorPuntos3=Color.rgb(r,g,b);}
    public void  SetColorSerie4(int r, int g, int b){ColorPuntos4=Color.rgb(r,g,b);}
    public void  SetColorSerie5(int r, int g, int b){ColorPuntos5=Color.rgb(r,g,b);}
    public void SetGruesoLinea(int g){GruesoLinea=g;}
    public void SetTouch(boolean t){ this.ActivaTouch=t; }


}
