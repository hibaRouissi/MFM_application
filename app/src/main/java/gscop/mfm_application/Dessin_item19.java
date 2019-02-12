package gscop.mfm_application;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;

public class Dessin_item19 extends View {


    private static final String TAG = "gscop.mfm_application";
    private Bitmap custom_image;
    private Canvas canvas_cv = new Canvas();
    private int mRectX, mRectY;
    private int mRect2X, mRect2Y;
    private float mImageX,mImageY;
    private float mImage2X,mImage2Y;
    //insered by Adriana 20_02_18 (pour serparé les quadre)
    private final Paint paint = new Paint();
    private final Paint mPaintRect = new Paint();
    private Long lastUpTime = 0l;

    private HashMap<Integer, Path> paths = new HashMap<>();
    private HashMap<Integer, ArrayList<Float>> tabsX = new HashMap<>();
    private HashMap<Integer, ArrayList<Float>> tabsY = new HashMap<>();
    private HashMap<Integer,Long> eventTimes = new HashMap<>();
    private HashMap<Integer,Boolean> isPalmTouch = new HashMap<>();

    private ArrayList<Path> completedPaths = new ArrayList<>();
    private ArrayList<ArrayList<Float>> completedTabsX = new ArrayList<>();
    private ArrayList<ArrayList<Float>> completedTabsY = new ArrayList<>();
    private ArrayList<Long> eventDownTimes = new ArrayList<>();
    private ArrayList<Long> eventUpTimes = new ArrayList<>();
    private ArrayList<Boolean> isPalm = new ArrayList<>();

    private final RectF dirtyRect = new RectF();

    private float xDown;
    private float yDown;
    private ArrayList<Float> xDownList = new ArrayList<>();
    private ArrayList<Float> yDownList = new ArrayList<>();

    private boolean on = false;
    private boolean inrange = false;

    private Paint mysquare=new Paint(); // usado apenas para marcar os centros dos toques
    private int centerx     = 767;
    private int centery     = 934;
    private int offsetout   = 13; // 13
    private int offsetin    = -13; // -17
    private int delta       = 10; // filtrar as pequenas mudanças do desenho

    private boolean touched_inside = false;
    private boolean test1 =false ; // se o primeiro toque é no centro da figura
    private boolean test2 =false ; // se o patiente levantou o dedo após o primeiro toque no centro

    private boolean going_up    = false;
    private boolean going_right = false;
    private boolean begining_trace=true; // pour éviter de dire qu´il y a un tournage juste aprés le 1er point
    private boolean cotation_3 = true;  // Esta variável é para testar os laços dentro dos limites
    private boolean first_touch_ok = false; // Verificar se hove um boucle tocando em cima e em baixo
    private boolean last_touch_ok = false;
    private boolean test_velovity=true;

    private int last_x;
    private int last_y;
    private ArrayList<Integer> max_x = new ArrayList<>();
    private ArrayList<Integer> min_x = new ArrayList<>();
    private ArrayList<Integer> max_y = new ArrayList<>();
    private ArrayList<Integer> min_y = new ArrayList<>();

    private int demi_boucles       =0;
    private int max_within_range   =0;
    private int min_within_range   =0;
    private int max_outside_window =0;
    private int min_outside_window =0;

    private boolean first_trace = true;
    private boolean inflection = false;

    // variaveis usadas no calculo da velocidade de desenho
    // velocidade = delta_distance/delta_tempo. Usada para verificar se o usuário
    // parou por um instante durante o desenho.
    private ArrayList<Integer> delta_distance = new ArrayList<>();
    private ArrayList<Long>    delta_tempo    = new ArrayList<>();

    // variáveis para manipulação de pontos. delta_distance=distância entre ponto atual e anterior
    int currentX;
    int currentY;
    int lastX;
    int lastY;
    // variáveis para manipulação de tempo (diferença no tempo = tempo atual - tempo anterior)
    // delta_tempo = currenttime - lastcurrenttime
    long currenttime;
    long lastcurrenttime;
    // variavel usada para informar o primeiro ponto depois de um toque na tela.
    private boolean first_of_move;    // le premier registre (point) sur un chemin

    private ArrayList<Long> my_times = new ArrayList<>();
    private ArrayList<Float> my_X = new ArrayList<>();
    private ArrayList<Float> my_Y = new ArrayList<>();

    private boolean verify_first_touch_up = false;
    private boolean first_touch_up_right  = false;
    private boolean first_touch_up_left   = false;
    private boolean first_touch_down_right  = false;
    private boolean first_touch_down_left   = false;
    private float firsttouch_xdown;
    private float firsttouch_ydown;

    private boolean firsttouch_in_tray = false;
    private boolean lasttouch_in_tray  =false;
    private boolean firsttouch_tray_up = false;
    private int missed_turns=0;
    private int number_of_traces=0;

    private double tata=2.5; // taille

    public Dessin_item19(Context context) {

        super(context);
        init(null);
    }

    public Dessin_item19(Context context, AttributeSet attrs) {

        super(context, attrs);
        init(attrs);
    }

     public Dessin_item19(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(@Nullable AttributeSet set) {

        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setAntiAlias(true);
        paint.setColor(Color.TRANSPARENT);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);// inserted by Adriana 12/02/2018

        mPaintRect.setAntiAlias(true);
        mPaintRect.setColor(Color.BLACK);
        mPaintRect.setStyle(Paint.Style.STROKE);
        mPaintRect.setStrokeWidth(19);// inserted by Adriana 12/02/2018

        Log.d(TAG," INIT ");
    }

    @Override
    protected void onDraw(Canvas canvas) {

        custom_image = Bitmap.createBitmap(getWidth(),getHeight(),Bitmap.Config.ARGB_8888);
        canvas.drawBitmap(custom_image,0,0,null);
        canvas = new Canvas(custom_image);

        if (mImageX == 0f || mImageY == 0f) {
            mImageX = (getWidth()/ 2);
            mImageY = (getHeight()/ 2);
        }
        if (mImage2X == 0f || mImage2Y == 0f) {
            mImage2X = (getWidth()/ 2);
            //Commented by Adriana (Distance entre les carrés - off set) 08/03/2018
            mImage2Y = (getHeight()/ 2 + 232);
        }

        mRectX = (int) mImageX ;
        mRectY = (int) mImageY ;
        // inserted by by Adriana 15/02/2018 (pour séparer les carrés )
        mRect2X = (int) mImage2X ;
        mRect2Y = (int) mImage2Y ;

        //Comented by Adriana (note: taille du dessin - deux) 08/03/2018
        Rect rect = new Rect ((mRectX - 217), (mRectY + 63),(mRectX + 217),(mRectY - 63));
        canvas.drawRect(rect,mPaintRect);

 // remover os caracteres /* para colocar as marcas dos limites
/*
        rect = new Rect (centerx-217 - offsetout, centery + 63 + offsetout,
                centerx+ 217 + offsetout, centery-63 - offsetout);
        mysquare.setColor(Color.BLUE);
        mysquare.setStrokeWidth(1);
        mysquare.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rect,mysquare);

        rect = new Rect (centerx-217 - offsetin, centery + 63 + offsetin,
                centerx+ 217 + offsetin, centery-63 - offsetin);
        canvas.drawRect(rect,mysquare);

        rect = new Rect (centerx-217 - 5*offsetout, centery + 63 + 5*offsetout,
                centerx+ 217 + 5*offsetout, centery-63 - 5*offsetout);
        canvas.drawRect(rect,mysquare);

        // new spots
        mysquare.setColor(Color.RED);
        mysquare.setStrokeWidth(2);
        // cima e direita do paciente
        rect = new Rect (centerx-217-offsetout, centery + 63 + offsetout,
                (int) (centerx-217-(tata-0.5)*offsetin), (int) (centery+63+(tata-0.5)*offsetin));
        canvas.drawRect(rect,mysquare);

        // cima e esquerda do paciente
        rect = new Rect ((int)(centerx+217-(tata-0.5)*offsetout), centery + 63 + offsetout,
                centerx+217-offsetin, (int) (centery+63+(tata-0.5)*offsetin));
        canvas.drawRect(rect,mysquare);

        // baixo e direita do paciente
        rect = new Rect (centerx-217-offsetout, (int)(centery - 63 + (tata-0.5)*offsetout),
                (int) (centerx-217-(tata-0.5)*offsetin), centery-63+offsetin);
        canvas.drawRect(rect,mysquare);

        // baixo e esquerda do paciente
        rect = new Rect ((int)(centerx+217-(tata-0.5)*offsetout), (int) (centery - 63 + (tata-0.5)*offsetout),
                centerx+217-offsetin, centery-63+offsetin);
        canvas.drawRect(rect,mysquare);
*/
        // remover os caracteres */ para colocar as marcas dos limites


        for (int i = 0; i < paths.size(); i++) {
            Path fingerPath = paths.get(i);
            if (fingerPath != null) {
                    canvas.drawPoint(xDown, yDown, paint);
                    if (isPalmTouch.get(i)) {
                        paint.setColor(Color.BLUE);
                    } else {
                        paint.setColor(Color.RED);
                    }
                    canvas.drawPath(fingerPath, paint);
            }
        }

        for (int i = 0; i < xDownList.size(); i++) {
                if (isPalm.get(i)) {
                    paint.setColor(Color.BLUE);
                } else {
                    paint.setColor(Color.BLUE);
                }
                canvas.drawPoint(xDownList.get(i), yDownList.get(i), paint);
        }

        for (int i = 0; i < completedPaths.size(); i++) {
                Path completedPath = completedPaths.get(i);
                if (isPalm.get(i)) {
                    paint.setColor(Color.BLUE);
                } else {
                    paint.setColor(Color.RED);
                }
                canvas.drawPath(completedPath, paint);
        }

        canvas_cv = canvas;
        Log.d(TAG," ON DRAW ");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int maskedAction = event.getActionMasked();
        int actionIndex = event.getActionIndex();
        int id = event.getPointerId(actionIndex);
        int historySize = event.getHistorySize();
        int mysource = 1;
        if (on) {
            mysource=2;
        } else {
            try {
                mysource = event.getToolType(id);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (mysource==2){


            // que avec Stylet

            boolean mytest = false;
            try {
                xDown = event.getX(id);
                yDown = event.getY(id);
                if (xDown >= centerx-217-5*offsetout && xDown <= centerx+217+5*offsetout
                        && yDown >= centery-63-5*offsetout && yDown <= centery+63+5*offsetout)
                    mytest=true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            if (mytest) {
                if (!on){
                    my_times.add(System.currentTimeMillis());
                    my_X.add(xDown);
                    my_Y.add(yDown);
                }

                switch (maskedAction) {
                    // Actions à réaliser quand l'utilisateur touche l'écran
                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_POINTER_DOWN: {
                        if(on){
                            break;
                        }
                        else {
                            Path p = new Path();
                            ArrayList<Float> tableauX = new ArrayList<>();
                            ArrayList<Float> tableauY = new ArrayList<>();
                            number_of_traces=number_of_traces+1;

                            // On récupère l'identifiant du contact tactile et ses coordonnées
                            try {
                                p.moveTo(event.getX(id), event.getY(id));
                                paths.put(id, p);

                                if (verify_first_touch((int) xDown,(int) yDown))  {
                                    first_touch_ok=true;
                                    Log.d(TAG, " first_touch_ok  : TRUE !!!!!!!!!!!!!!");
                                } else {
                                    cotation_3=false;
                                    Log.d(TAG, " first_touch_ok  : FALSE !!!!!!!!!!!!!!");
                                    Log.d(TAG, " xdown=  : "+ xDown);
                                    Log.d(TAG, " ydown=  : "+ yDown);
                                }

                                firsttouch_xdown=xDown;
                                firsttouch_ydown=yDown;

                                firsttouch_in_tray=verify_touch_up((int) xDown,(int) yDown);
                                if (firsttouch_in_tray){
                                    firsttouch_tray_up = true;
                                } else {
                                    firsttouch_in_tray=verify_touch_down((int) xDown,(int) yDown);
                                }



                                // Contiennent les temps de initialisation du movement
                                eventTimes.put(id,System.currentTimeMillis());

                                // Contiennent toutes les coordonnées brutes
                                tableauX.add(event.getX(id));
                                tableauY.add(event.getY(id));
                                tabsX.put(id,tableauX);
                                tabsY.put(id,tableauY);

                                Log.d(TAG, " ACTION DOWN  ID : " + id);
                                Log.d(TAG, " SIZE : " + event.getSize(id) + " TYPE : " + event.getToolType(id));
                                Log.d(TAG, " MAJOR : " + event.getToolMajor(id) + " MINOR : " + event.getToolMinor(id));
                                Log.d(TAG, " POINTS : " + event.getPointerCount());

                                boolean a = verify_inside((int)xDown,(int)yDown);
                                if (a) touched_inside = true;
                                if (a) Log.d(TAG, " TOUCHED INSIDE !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                                boolean b = verify_touch_up((int)xDown,(int)yDown);
                                if (b) Log.d(TAG, " TOUCHED UP !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                                boolean c = verify_touch_down((int)xDown,(int)yDown);
                                if (c) Log.d(TAG, " TOUCHED DOWN !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
                                last_x=(int)xDown;
                                last_y=(int)yDown;
                                Log.d(TAG, "LIGNE 361:  last_y="+last_y);


                                // set in 1 for never change the color
                                if(event.getSize(id) >= 1){
                                    Log.d(TAG," PALM TOUCH ");
                                    isPalmTouch.put(id,true);
                                }
                                else{
                                    Log.d(TAG, " FINGER TOUCH ");
                                    isPalmTouch.put(id,false);
                                }
                                invalidate();

                            } catch (IllegalArgumentException ex) {
                                ex.printStackTrace();
                            }
                            break;
                        }
                    }



                    // Actions à réaliser quand l'utilisateur bouge son doigt
                    case MotionEvent.ACTION_MOVE: {
                        if (on) {
                            inrange=false;
                            float my=0;
                            float mx=0;
                            try {
                                mx = event.getX(id);
                                my = event.getY(id);
                                inrange=true;
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            if (inrange) {
                                //Commented by Adriana 11/03/2018 (limiter l'image cd à l'intérieur de l'écran)
                                if (mx >= 235 && mx <= 1300) {
                                    if (my >= 80 && my <= 1780) {

                                        if ((mx >= mRectX - 210) && (mx <= (mRectX + 210))) {
                                            if (my >= (mRectY - 55) && my <= (mRectY + 55)) {
                                                mImageX = mx;
                                                mImageY = my;
                                                Log.d(TAG, " ACTION MOVE CD");
                                                centerx     = (int) mx;
                                                centery     = (int) my;
                                                invalidate();
                                                return false;
                                            }
                                        }
                                    }
                                }

                                // inserted by by Adriana 11/03/2018
                                if (mx >= 235 && mx <= 1300) {
                                    if (my >= 80 && my <= 1780) {

                                        if ((mx >= mRect2X - 210) && mx <= (mRect2X + 210)) {
                                            if (my >= (mRect2Y - 55) && my <= (mRect2Y + 55)) {
                                                mImage2X = mx;
                                                mImage2Y = my;
                                                Log.d(TAG, " ACTION MOVE CD");
                                                invalidate();
                                                return false;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else {

                            int mydistance = calc_distance((int)xDown, (int)yDown, (int)firsttouch_xdown,(int)firsttouch_ydown);
                            if (mydistance>2*delta){ // pour eviter de affirmer un tournage juste apres le 1er touche
                                begining_trace=false;
                            }

                            // pour calculer la vitesse de la tracée
                            if (first_of_move) {
                                lastX = (int) event.getX();
                                lastY = (int) event.getY();
                                lastcurrenttime = System.currentTimeMillis();
                                first_of_move = false;

                            } else {
                                currentX = (int) event.getX();
                                currentY = (int) event.getY();
                                int distancetemp = calc_distance(currentX, currentY, lastX, lastY);
                                currenttime = System.currentTimeMillis();
                                long dtempo = currenttime - lastcurrenttime;
                                delta_distance.add(distancetemp);
                                delta_tempo.add(dtempo);
                                Log.d(TAG, " DELTA DISTANCE =  " + distancetemp);
                                Log.d(TAG, " DELTA TEMPO    =  " + dtempo);
                                lastX = currentX;
                                lastY = currentY;
                                lastcurrenttime = currenttime;
                            }


                            long time = event.getEventTime();
                            for (int size = event.getPointerCount(), i = 0; i < size; i++) {
                                Path p = paths.get(event.getPointerId(i));
                                ArrayList<Float> tableauX = tabsX.get(event.getPointerId(i));
                                ArrayList<Float> tableauY = tabsY.get(event.getPointerId(i));

                                if (p != null) {
                                    // Permet d'avoir un tracé plus fluide (on prend plus de points en compte)
                                    for (int j = 0; j < historySize; j++) {
                                        float historicalX = event.getHistoricalX(i, j);
                                        float historicalY = event.getHistoricalY(i, j);
                                        expandDirtyRect(historicalX, historicalY);
                                        p.lineTo(historicalX, historicalY);
                                        tableauX.add(historicalX);
                                        tableauY.add(historicalY);
                                        // inserted for automatic test
                                        boolean a = verify_inside((int)historicalX,(int)historicalY);
                                        if (a) touched_inside = true;
                                        if (going_up){
                                            if (((int)historicalY) >= last_y){ // still going up
                                                going_up = true;
                                                last_y = (int)historicalY;
                                                Log.d(TAG, " LIGNE 481: last_y="+last_y);
                                            } else {                    // turned down
                                                if ((last_y - (int)historicalY)>= delta) {
                                                    going_up = false;
                                                    max_y.add(last_y);
                                                    if (!begining_trace){
                                                        Log.d(TAG, " TURNED DOWN!!! , Y MAX  = " + last_y);
                                                        Log.d(TAG, " TURNED DOWN before cotation 3 = " + cotation_3);
                                                        if (!verify_touch_up(last_x, last_y)){
                                                            cotation_3 = false;
                                                            missed_turns=missed_turns+1;
                                                        }
                                                        Log.d(TAG, " TURNED DOWN after cotation 3 = " + cotation_3);
                                                        if (inflection) demi_boucles = demi_boucles + 1;
                                                        if (verify_touch_up(last_x, last_y))
                                                            max_within_range = max_within_range + 1;
                                                        if (!verify_inside(last_x, last_y))
                                                            max_outside_window = max_outside_window + 1;
                                                        last_y = (int) historicalY;
                                                        Log.d(TAG, " LIGNE 497: last_y="+last_y);
                                                        inflection = false;
                                                    }
                                                }
                                            }
                                        } else { // going down
                                            if (((int)historicalY) <= last_y){ // still going down
                                                going_up = false;
                                                last_y = (int)historicalY;
                                                Log.d(TAG, " LIGNE 505: last_y="+last_y);
                                            } else {                           // turned up
                                                if (((int)historicalY) - last_y>= delta) {
                                                    going_up = true;
                                                    min_y.add(last_y);
                                                    if (!begining_trace){
                                                        Log.d(TAG, " TURNED UP!!! , Y MIN  = " + last_y);
                                                        Log.d(TAG, " TURNED UP before cotation 3 = " + cotation_3);
                                                        Log.d(TAG, " xdown= " + xDown + "  , ydown="+yDown);
                                                        Log.d(TAG, " last_x= " + last_x + "  , last_y="+last_y);
                                                        if (!verify_touch_down(last_x, last_y)){
                                                            cotation_3 = false;
                                                            missed_turns=missed_turns+1;
                                                        }
                                                        Log.d(TAG, " TURNED UP after cotation 3 = " + cotation_3);
                                                        if (verify_touch_down(last_x, last_y))
                                                            min_within_range = min_within_range + 1;
                                                        if (!verify_inside(last_x, last_y))
                                                            min_outside_window = min_outside_window + 1;
                                                        if (inflection) demi_boucles = demi_boucles + 1;
                                                        last_y = (int) historicalY;
                                                        Log.d(TAG, " LIGNE 523: last_y="+last_y);
                                                        inflection = false;
                                                    }
                                                }
                                            }
                                        }

                                        if (going_right){
                                            if (((int)historicalX) <= last_x){ // still going right
                                                going_right = true;
                                                last_x = (int)historicalX;
                                            } else {                           // turned left
                                                going_right = false;
                                                max_x.add(last_x);
                                                Log.d(TAG, " TURNED LEFT!!! , X MAX  = "+last_x);
                                                last_x = (int)historicalX;
                                                inflection = true;
                                            }
                                        } else { // going left
                                            if (((int)historicalX) >= last_x){ // still going left
                                                going_right = false;
                                                last_x = (int)historicalX;
                                            } else {                           // turned right
                                                going_right = true;
                                                min_x.add(last_x);
                                                Log.d(TAG, " TURNED RIGHT!!! , X MIN  = "+last_x);
                                                last_x = (int)historicalX;
                                                inflection = true;
                                            }
                                        }


                                    }
                                    tableauX.add(event.getX(i));
                                    tableauY.add(event.getY(i));
                                    invalidate();
                                }
                            }
                            Log.d(TAG, " ACTION MOVE ");
                            invalidate();
                            break;
                        }
                    }

                    // Actions à réaliser quand l'utilisateur arrête le contact tactile
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP: {

                        if (on) {
                            break;
                        }
                        else {
                            // On ajoute le trait tracé dans la liste des traits terminés, et on le retire de mX et mY
                            Path p = paths.get(id);
                            ArrayList<Float> tableauX = tabsX.get(id);
                            ArrayList<Float> tableauY = tabsY.get(id);

                            if (p != null) {
                                completedPaths.add(p);
                                completedTabsX.add(tableauX);
                                completedTabsY.add(tableauY);
                                eventDownTimes.add(eventTimes.get(id));
                                eventUpTimes.add(System.currentTimeMillis());
                                isPalm.add(isPalmTouch.get(id));
                                Log.d(TAG," DURATION TIME : " + (System.currentTimeMillis() - eventTimes.get(id)));
                                xDownList.add(xDown);
                                yDownList.add(yDown);
                                lastUpTime = System.currentTimeMillis();
                                invalidate();
                                isPalmTouch.remove(id);
                                paths.remove(id);
                                eventTimes.remove(id);
                            }

                            if ( verify_last_touch((int) xDown,(int) yDown))  {
                                last_touch_ok=true;
                                Log.d(TAG, " last_touch_ok  : TRUE !!!!!!!!!!!!!!");
                            } else {
                                cotation_3=false;
                                Log.d(TAG, " last_touch_ok  : FALSE !!!!!!!!!!!!!!");
                            }

                            // verify single trace for cotation 3
                            if (first_trace){
                                first_trace=false;
                            } else {
                                cotation_3=false;
                                Log.d(TAG, " First trace. cotation 3 = " + cotation_3);
                            }

                            boolean tatavelocity=verify_min_velocity(100,10);
                            if (!tatavelocity){
                                cotation_3=false;
                                test_velovity=false;
                            }
                            Log.d(TAG, "  velocity > threshold : " + test_velovity);

                            if (firsttouch_in_tray){
                                if (firsttouch_tray_up) {
                                    lasttouch_in_tray = verify_touch_up((int) xDown,(int) yDown);
                                } else {
                                    lasttouch_in_tray = verify_touch_down((int) xDown,(int) yDown);
                                }
                            }

                            Log.d(TAG," ACTION UP ");
                            Log.d(TAG," DEMI BOUCLES = " +demi_boucles);
                            Log.d(TAG," MAX WITHIN RANGE = " +max_within_range);
                            Log.d(TAG," MIN WITHIN RANGE = " +min_within_range);
                            Log.d(TAG," MAX OUTSIDE WINDOW = " +max_outside_window);
                            Log.d(TAG," MIN OUTSIDE WINDOW = " +min_outside_window);
                            Log.d(TAG," cotation 3 = " +cotation_3);

                            for (int i=0; i<max_x.size();i++)
                                Log.d(TAG," MAX("+i+") = " +max_x.get(i));
                            for (int i=0; i<min_x.size();i++)
                                Log.d(TAG," MIN("+i+") = " +min_x.get(i));

                            invalidate();
                            break;
                        }
                    }

                    case MotionEvent.ACTION_CANCEL:{
                        Log.d(TAG," PROBLEM WITH PALM TOUCH ");
                        break;
                    }
                }
            }
        }
        return true;
    }

    public Long getDurationTime(){
        Long durationTime;
        if(eventDownTimes.size() != 0) {
            durationTime = lastUpTime - eventDownTimes.get(0);
            return durationTime;
        }
        else{
            return 0l;
        }
    }

    //Inserted by Adriana 06/03/2018 (To operate the EFFACER button)
    public void cleancompletedPath(){
        completedPaths = new ArrayList<>();
        xDownList = new ArrayList<>();
        yDownList = new ArrayList<>();

        for (int i = 0; i < completedPaths.size(); i++) {
            Path completedPath = completedPaths.get(i);
            if(isPalm.get(i)){
                paint.setColor(Color.BLUE);
            }
            else{
                paint.setColor(Color.TRANSPARENT);
            }
            canvas_cv.drawPath(completedPath, paint);
        }
        // for automatic quotation
        touched_inside = false;
        demi_boucles = 0;
        inflection   = false;
        max_within_range =0;
        min_within_range =0;
        max_outside_window =0;
        min_outside_window =0;
    }

    public Bitmap getCartographie() {return custom_image;}

    public Canvas getCanvas() {return canvas_cv;}

    public Paint getPaint() {return paint;}

    public ArrayList getTableauX() {
        return completedTabsX;
    }

    public ArrayList getTableauY() {return completedTabsY;}

    public void getBooleanClick(boolean click) {on = click;}

    public ArrayList getEventUpTimes(){return eventUpTimes;}

    public ArrayList getEventDownTimes(){return eventDownTimes;}

    public float getImageX(){return mImageX;}

    public float getImageY(){return mImageY;}

    //insered by Adriana 25_02_18 (enregistrer le dessin en pdf)

    public float getImage2X(){return mImage2X;}

    public float getImage2Y(){return mImage2Y;}

    public ArrayList getBooleanPalm() {return isPalm;}


    public boolean get_touched_inside (){return touched_inside;}
    public int get_demi_boucles () {return demi_boucles;}
    public int get_max_within_range(){return max_within_range;}
    public int get_min_within_range(){return min_within_range;}
    public int get_max_outside_window(){return max_outside_window;}
    public int get_min_outside_window(){return min_outside_window;}
    public ArrayList get_max_x(){return max_x;}
    public ArrayList get_min_x(){return min_x;}
    public boolean get_cotation_3(){return cotation_3;}
    public boolean get_last_touch_ok (){return last_touch_ok;}
    public boolean get_first_touch_ok (){return first_touch_ok;}
    public boolean get_firsttouch_in_tray (){return firsttouch_in_tray;}
    public boolean get_lasttouch_in_tray (){return lasttouch_in_tray;}
    public int get_missed_turns() {return missed_turns;}

    public ArrayList getmy_times (){return my_times;}
    public ArrayList getmy_X (){return my_X;}
    public ArrayList getmy_Y (){return my_Y;}
    public int get_number_of_traces(){return number_of_traces;}

    public boolean get_test_velocity () {
        Log.d(TAG," TEST VELOCITY = " +test_velovity);
        return test_velovity;
    }



    public boolean verify_inside(int xp, int yp){
        if ( (xp>=centerx-217-offsetout) && (xp<=centerx+217+offsetout)
             && (yp>=centery-63-offsetout) && (yp<=centery + 63 + offsetout)){
            return true;
        } else {
            return false;
        }
    }

    public boolean verify_touch_up (int xp, int yp){
        if ( (xp>=centerx-217-offsetout) && (xp<=centerx+217+offsetout)
                && (yp>=centery+63+offsetin) && (yp<=centery + 63 + offsetout)){
            return true;
        } else {
            return false;
        }
    }

    public boolean verify_touch_down (int xp, int yp){
        if ( (xp>=centerx-217-offsetout) && (xp<=centerx+217+offsetout)
                && (yp>=centery-63+offsetin) && (yp<=centery - 63 + offsetout)){
            return true;
        } else {
            return false;
        }
    }


    public boolean verify_first_touch (int xp, int yp){
        boolean test=false;
        if ((xp>=centerx-217-offsetout) && (xp<=centerx-217-tata*offsetin)
             &&(yp>=centery+63+tata*offsetin) && (yp<=centery + 63 + offsetout)) {
            test=true;
            first_touch_up_right=true;
            Log.d(TAG," FIRST TOUCH IN UP RIGHT !!!!!!!!!!!! ");
        }
        if ((xp>=centerx+217-tata*offsetout) && (xp<=centerx+217-offsetin)
                &&(yp>=centery+63+tata*offsetin) && (yp<=centery + 63 + offsetout)) {
            test=true;
            first_touch_up_left=true;
            Log.d(TAG," FIRST TOUCH IN UP LEFT !!!!!!!!!!!! ");
        }
        if ((xp>=centerx-217-offsetout) && (xp<=centerx-217-tata*offsetin)
                &&(yp>=centery-63+offsetin) && (yp<=centery - 63 + tata*offsetout)) {
            test=true;
            first_touch_down_right=true;
            Log.d(TAG," FIRST TOUCH IN DOWN RIGHT !!!!!!!!!!!! ");
        }
        if ((xp>=centerx+217-tata*offsetout) && (xp<=centerx+217-offsetin)
                &&(yp>=centery-63+offsetin) && (yp<=centery - 63 + tata*offsetout)) {
            test=true;
            first_touch_down_left=true;
            Log.d(TAG," FIRST TOUCH IN DOWN LEFT !!!!!!!!!!!! ");
        }
        return test;
    }

    public boolean verify_last_touch (int xp, int yp){
        boolean test=false;
        if (first_touch_ok) {
            if (first_touch_up_left && (xp>=centerx-217-offsetout) && (xp<=centerx-217-tata*offsetin)
                    &&(yp>=centery+63+tata*offsetin) && (yp<=centery + 63 + offsetout)) {
                test=true;
                Log.d(TAG," LAST TOUCH IN UP RIGHT !!!!!!!!!!!! ");
            }
            if (first_touch_up_right && (xp>=centerx+217-tata*offsetout) && (xp<=centerx+217-offsetin)
                    &&(yp>=centery+63+tata*offsetin) && (yp<=centery + 63 + offsetout)) {
                test=true;
                Log.d(TAG," LAST TOUCH IN UP LEFT !!!!!!!!!!!! ");
            }
            if (first_touch_down_left && (xp>=centerx-217-offsetout) && (xp<=centerx-217-tata*offsetin)
                    &&(yp>=centery-63+offsetin) && (yp<=centery - 63 + tata*offsetout)) {
                test=true;
                Log.d(TAG," LAST TOUCH IN DOWN RIGHT !!!!!!!!!!!! ");
            }
            if (first_touch_down_right && (xp>=centerx+217-tata*offsetout) && (xp<=centerx+217-offsetin)
                    &&(yp>=centery-63+offsetin) && (yp<=centery - 63 + tata*offsetout)) {
                test=true;
                Log.d(TAG," LAST TOUCH IN DOWN LEFT !!!!!!!!!!!! ");
            }
        }
        return test;
    }

    /**
     * Called when replaying history to ensure the dirty region includes all
     * points.
     *
     * @param historicalX l'historique des coordonnées X entre deux touch events
     * @param historicalY l'historique des coordonnées Y entre deux touch events
     */
    private void expandDirtyRect(float historicalX, float historicalY) {
        if (historicalX < dirtyRect.left) {
            dirtyRect.left = historicalX;
        } else if (historicalX > dirtyRect.right) {
            dirtyRect.right = historicalX;
        }
        if (historicalY < dirtyRect.top) {
            dirtyRect.top = historicalY;
        } else if (historicalY > dirtyRect.bottom) {
            dirtyRect.bottom = historicalY;
        }
    }

    private Bitmap getResizeBitmap(Bitmap bitmap){
        // L'image serait redimensionné pour le taille du CD (1317 px avec 300ppi de résolution)
        float aspect_ratio = bitmap.getWidth()/bitmap.getHeight();
        int mImageWidth = 1317;
        int mImageHeight = Math.round(mImageWidth*aspect_ratio);
        bitmap = Bitmap.createScaledBitmap(bitmap,mImageWidth,mImageHeight,false);
        return bitmap.copy(Bitmap.Config.ARGB_8888,false);
    }

    public void orderPaths(){
        HashMap<Integer,Long> upTimes = new HashMap<>();

        for(int i = 0; i < completedTabsX.size(); i++) {
            tabsX.put(i, completedTabsX.get(i));
            tabsY.put(i, completedTabsY.get(i));
            upTimes.put(i, eventUpTimes.get(i));
            eventTimes.put(i,eventDownTimes.get(i));
            isPalmTouch.put(i,isPalm.get(i));
        }

        for(int i = 0; i < (completedTabsX.size()-1); i++) {
            for (int j = i+1; j < completedTabsX.size(); j++) {
                if (eventTimes.get(j) < eventTimes.get(i)) {
                    ArrayList<Float> x = tabsX.get(i);
                    ArrayList<Float> y = tabsY.get(i);
                    long t1 = eventTimes.get(i);
                    long t2 = upTimes.get(i);
                    boolean b = isPalmTouch.get(i);
                    tabsX.put(i, tabsX.get(j));
                    tabsX.put(j, x);
                    tabsY.put(i, tabsY.get(j));
                    tabsY.put(j, y);
                    upTimes.put(i, upTimes.get(j));
                    upTimes.put(j, t2);
                    eventTimes.put(i, eventTimes.get(j));
                    eventTimes.put(j, t1);
                    isPalmTouch.put(i,isPalmTouch.get(j));
                    isPalmTouch.put(j,b);
                }
            }
        }

        completedTabsX.clear();
        completedTabsY.clear();
        eventUpTimes.clear();
        eventDownTimes.clear();
        isPalm.clear();

        for(int i = 0; i < tabsX.size(); i++){
            completedTabsX.add(tabsX.get(i));
            completedTabsY.add(tabsY.get(i));
            eventUpTimes.add(upTimes.get(i));
            eventDownTimes.add(eventTimes.get(i));
            isPalm.add(isPalmTouch.get(i));
        }
    }



    // Funções de cotations automáticas
    public int calc_distance (int x1, int y1, int x2, int y2){
        return (int) Math.sqrt (Math.pow(x2-x1,2)+ Math.pow(y2-y1,2));
    }


    // verificando a velocidade
    public boolean verify_min_velocity(int limiar_inicio, int limiar_teste) {
        float minimo = 1000000;
        // variável usada para evitar que o dedo parado no início do teste seja computado
        // com velocidade zero, fazendo com que pense-se que houve uma parada.
        boolean user_started =false;
        // o averaging é o número de pontos de ação "Move" que são usados em uma média
        // Geralmente, a cada 30 ms é gerado um novo move. Este número deve ser ajustado
        // de acordo com o comportamento esperado.
        int averaging =10;

        for (int i = 0; i < Math.floor(delta_tempo.size() / averaging); i++) {
            float totaldistancia = 0;
            float totaltime = 0;
            for (int j = 0; j < averaging; j++) {
                totaldistancia = totaldistancia + delta_distance.get(j + i * averaging);
                totaltime = totaltime + delta_tempo.get(j + i * averaging);
            }
            // a multiplicação por mil é porque o tempo é dado em milisegundos.
            // A velocidade é portanto dada em pixels por segundo
            float velocidade = (1000 * totaldistancia / totaltime);
            Log.d(TAG, " Velocidade =" + velocidade);
            if (velocidade >= limiar_inicio) { // este limiar deve ser ajustado
                user_started=true;
            }
            if (user_started) {
                if (velocidade<minimo){
                    minimo = velocidade;
                }
            }
        }
        // apagando a memória
        delta_distance = new ArrayList<>();
        delta_tempo = new ArrayList<>();
        Log.d(TAG, " Velocidade minima =" + minimo);
        // retornando o valor
        if (minimo>=limiar_teste){
            return true;
        } else {
            return false;
        }
    }

}