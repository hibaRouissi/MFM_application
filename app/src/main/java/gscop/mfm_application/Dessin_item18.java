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
import android.media.MediaPlayer;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class Dessin_item18 extends View {


    private static final String TAG = "gscop.mfm_application";
    private Bitmap image;
    private Bitmap custom_image;
    private Canvas canvas_cv = new Canvas();
    private float mImageX, mImageY;
    private final Paint paint = new Paint();
    private final Paint mPaintImage = new Paint();
    private Long lastUpTime = 0l;
    private Paint mycircle = new Paint();


    private HashMap<Integer, Path> paths = new HashMap<>();
    private HashMap<Integer, ArrayList<Float>> tabsX = new HashMap<>();
    private HashMap<Integer, ArrayList<Float>> tabsY = new HashMap<>();
    private HashMap<Integer, Long> eventTimes = new HashMap<>();
    private HashMap<Integer, Boolean> isPalmTouch = new HashMap<>();

    private ArrayList<Path> completedPaths = new ArrayList<>();
    private ArrayList<ArrayList<Float>> completedTabsX = new ArrayList<>();
    private ArrayList<ArrayList<Float>> completedTabsY = new ArrayList<>();
    private ArrayList<Long> eventDownTimes = new ArrayList<>();
    private ArrayList<Long> eventUpTimes = new ArrayList<>();
    private ArrayList<Boolean> isPalm = new ArrayList<>();

    private ArrayList<Long> my_times = new ArrayList<>();
    private ArrayList<Float> my_X = new ArrayList<>();
    private ArrayList<Float> my_Y = new ArrayList<>();

    private final RectF dirtyRect = new RectF();

    private float xDown;
    private float yDown;
    private ArrayList<Float> xDownList = new ArrayList<>();
    private ArrayList<Float> yDownList = new ArrayList<>();
    private boolean on = false;

    // cotation automatique. variaveis a serem reinicializadas se o utilisador clicar em effacer
    private boolean firstpath = true;   // le premier chemin parcouru
    private boolean test1 = false; // se o primeiro toque é dentro do circulo central;
    private boolean test2 = false; // se fez círculo menor em 1 traço sem parada
    private boolean test3 = false; // se fez círculo menor em 1 traço com parada
    private boolean test4 = false; // se fez círculo menor em 2 traços (com/sem parada, não verif)
    private boolean test5 = false; // se fez círculo maior em 1 traço e sem parada
    private boolean test6 = false; // se fez círculo maior em 1 traço com parada
    private boolean test7 = false; // se fez círculo maior em 2 traços (com/sem parada, não verif)

    // variavel usada para informar o primeiro ponto depois de um toque na tela.
    private boolean first_of_move;    // le premier registre (point) sur un chemin

    // variáveis de memória para guardar um traço, usada para compor 2 traços consecutivos,
    // para avaliação de dificuldade de desenho (sucesso apenas com 2 traços)
    private ArrayList<Float> lasttableauX = new ArrayList<>();
    private ArrayList<Float> lasttableauY = new ArrayList<>();
    private ArrayList<Float> last2tableauX = new ArrayList<>();
    private ArrayList<Float> last2tableauY = new ArrayList<>();

    // variaveis usadas no calculo da velocidade de desenho
    // velocidade = delta_distance/delta_tempo. Usada para verificar se o usuário
    // parou por um instante durante o desenho.
    private ArrayList<Integer> delta_distance = new ArrayList<>();
    private ArrayList<Long> delta_tempo = new ArrayList<>();

    // variáveis para manipulação de pontos. delta_distance=distância entre ponto atual e anterior
    int currentX;
    int currentY;
    int lastX;
    int lastY;
    // variáveis para manipulação de tempo (diferença no tempo = tempo atual - tempo anterior)
    // delta_tempo = currenttime - lastcurrenttime
    long currenttime;
    long lastcurrenttime;

    int centerx = 760;
    // Changement de la position de zone 21/11/2018 (avant était 1500)
    int centery = 1020;

    // pour le cas où il ne lève pas le doigt du centre
    private ArrayList<Float> truncated_inner_x = new ArrayList<>();
    private ArrayList<Float> truncated_inner_y = new ArrayList<>();
    private ArrayList<Float> truncated_outer_x = new ArrayList<>();
    private ArrayList<Float> truncated_outer_y = new ArrayList<>();
    private boolean start_truncation_inner = false;
    private boolean start_truncation_outer = false;
    private boolean first_truncation_inner = true;
    private boolean first_truncation_outer = true;
    private boolean enable_composed_truncation_inner = false;
    private boolean enable_composed_truncation_outer = false;
    private int number_of_traces = 0;
    private boolean exited_firsttouch_in_center = false;
    private boolean exited_firsttouch_outside_large_circle = false;

    private boolean test_incomplet = false; // pour faire la differente entre seulement le point au milieu

    public Dessin_item18(Context context) {
        super(context);
        init(null);
    }

    public Dessin_item18(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public Dessin_item18(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(@Nullable AttributeSet set) {

        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20);

        //image = BitmapFactory.decodeResource(getResources(), R.drawable.item18); IMAGE CD CLASSIQUE
        image = BitmapFactory.decodeResource(getResources(), R.drawable.serrure_sable); //nouvelle image serrure sable
        image = getResizeBitmap(image);

        Log.d(TAG, " INIT ");
    }

    @Override
    protected void onDraw(Canvas canvas) {

        custom_image = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        canvas.drawBitmap(custom_image, 0, 0, null);
        canvas = new Canvas(custom_image);

        if (mImageX == 0f || mImageY == 0f) {
            mImageX = (getWidth() - image.getWidth()) / 2;
            mImageY = (3 * (getHeight() - image.getHeight()) / 4);
        }
        // // Changement de la position du dessin 21/11/2018 (avant était mImageY)
        canvas.drawBitmap(image, mImageX, mImageY - 50, mPaintImage);

// remover os caracteres /* para colocar as marcas dos limites
/*
        mycircle.setColor(Color.BLUE);
        mycircle.setStrokeWidth(10);
        mycircle.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(centerx, centery,180 ,mycircle);

        mycircle.setColor(Color.BLUE);
        mycircle.setStrokeWidth(10);
        mycircle.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(centerx, centery,850 ,mycircle);

        mycircle.setColor(Color.BLUE);
        mycircle.setStrokeWidth(10);
        mycircle.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(centerx, centery,590 ,mycircle);

*/
        // remover os caracteres  para colocar as marcas dos limites


        // inserido para a truncagem
        for (int i = 0; i < truncated_inner_x.size(); i++) {
            canvas.drawPoint(truncated_inner_x.get(i), truncated_inner_y.get(i), paint);
            paint.setColor(Color.TRANSPARENT);
        }

        for (int i = 0; i < truncated_outer_x.size(); i++) {
            canvas.drawPoint(truncated_outer_x.get(i), truncated_outer_y.get(i), paint);
            paint.setColor(Color.TRANSPARENT);
        }


        for (int i = 0; i < paths.size(); i++) {
            Path fingerPath = paths.get(i);
            if (fingerPath != null) {
                canvas.drawPoint(xDown, yDown, paint);
                if (isPalmTouch.get(i)) {
                    paint.setColor(Color.BLUE);
                }
                // Couleur qui apparaît lorsque vous écrivez
                else {
                    paint.setColor(Color.TRANSPARENT);
                }
                canvas.drawPath(fingerPath, paint);
            }
        }

        for (int i = 0; i < xDownList.size(); i++) {
            if (isPalm.get(i)) {
                paint.setColor(Color.BLUE);
            } else {
                paint.setColor(Color.TRANSPARENT);
            }
            canvas.drawPoint(xDownList.get(i), yDownList.get(i), paint);
        }

        for (int i = 0; i < completedPaths.size(); i++) {
            Path completedPath = completedPaths.get(i);
            if (isPalm.get(i)) {
                paint.setColor(Color.BLUE);
            } else {
                // Couleur qui apparaît sur l'écran
                paint.setColor(Color.TRANSPARENT);
            }
            canvas.drawPath(completedPath, paint);
        }
        canvas_cv = canvas;
        Log.d(TAG, " ON DRAW ");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int maskedAction = event.getActionMasked();
        int actionIndex = event.getActionIndex();
        int id = event.getPointerId(actionIndex);
        int historySize = event.getHistorySize();
        boolean mytest = false;
        try {
            xDown = event.getX(id);
            yDown = event.getY(id);
            // verify if the touch is inside the authorized window
            //           if (calc_distance((int)xDown, (int)yDown, centerx, centery)<=750)
            mytest = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (mytest) {  // if the event is inside the authorized window
            if (!on) {
                my_times.add(System.currentTimeMillis());
                my_X.add(xDown);
                my_Y.add(yDown);
            }

            switch (maskedAction) {
                // Actions à réaliser quand l'utilisateur touche l'écran
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN: {
                    if (on) {
                        break;
                    } else {
                        Path p = new Path();
                        ArrayList<Float> tableauX = new ArrayList<>();
                        ArrayList<Float> tableauY = new ArrayList<>();
                        // On récupère l'identifiant du contact tactile et ses coordonnées
                        try {
                            p.moveTo(xDown, yDown);
                            paths.put(id, p);

                            number_of_traces = number_of_traces + 1;

                            if (number_of_traces == 1) {
                                if (calc_distance(centerx, centery, (int) xDown, (int) yDown) < 180) {
                                    test1 = true;
                                    Log.d(TAG, "  first touch in center= true");
                                } else {
                                    Log.d(TAG, "  first touch in center= false");
                                }
                            }

                            if (number_of_traces > 1) {
                                if (calc_distance(centerx, centery, (int) xDown, (int) yDown) > 180) {
                                    test_incomplet = true;
                                    Log.d(TAG, "  first touch in center= true");
                                }
                            }


                            // Contiennent les temps de initialisation du movement
                            eventTimes.put(id, System.currentTimeMillis());

                            // Contiennent toutes les coordonnées brutes
                            tableauX.add(event.getX(id));
                            tableauY.add(event.getY(id));
                            tabsX.put(id, tableauX);
                            tabsY.put(id, tableauY);

                            Log.d(TAG, " ACTION DOWN  ID : " + id);
                            Log.d(TAG, " SIZE : " + event.getSize(id) + " TYPE : " + event.getToolType(id));
                            Log.d(TAG, " MAJOR : " + event.getToolMajor(id) + " MINOR : " + event.getToolMinor(id));
                            Log.d(TAG, " POINTS : " + event.getPointerCount());
                            Log.d(TAG, " DOWN X : " + xDown);
                            Log.d(TAG, " DOWN Y : " + yDown);


                            // set in 1 for never change the color
                            if (event.getSize(id) >= 1) {
                                Log.d(TAG, " PALM TOUCH ");
                                isPalmTouch.put(id, true);
                            } else {
                                Log.d(TAG, " FINGER TOUCH ");
                                isPalmTouch.put(id, false);
                            }
                            invalidate();
                            first_of_move = true;

                            if (!enable_composed_truncation_inner) {
                                // pour le truncation
                                if (!first_truncation_inner && start_truncation_inner && (truncated_inner_x.size() != 0)) {
                                    float lastxpoint = truncated_inner_x.get(truncated_inner_x.size() - 1);
                                    float lastypoint = truncated_inner_y.get(truncated_inner_y.size() - 1);
                                    if (calc_distance((int) lastxpoint, (int) lastypoint, (int) xDown, (int) yDown) < 150) {
                                        enable_composed_truncation_inner = true;
                                        Log.d(TAG, "  Enabling composed truncation inner");
                                    } else {
                                        Log.d(TAG, "  NOT Enabling composed truncation inner");
                                    }
                                }
                            } else {
//                                enable_composed_truncation_inner=false;
                            }

                            if (!enable_composed_truncation_outer) {
                                if (!first_truncation_outer && start_truncation_outer && (truncated_outer_x.size() != 0)) {
                                    float lastxpoint = truncated_outer_x.get(truncated_outer_x.size() - 1);
                                    float lastypoint = truncated_outer_y.get(truncated_outer_y.size() - 1);
                                    if (calc_distance((int) lastxpoint, (int) lastypoint, (int) xDown, (int) yDown) < 150) {
                                        enable_composed_truncation_outer = true;
                                        Log.d(TAG, "  Enabling composed truncation outer ");
                                    } else {
                                        Log.d(TAG, "  NOT Enabling composed truncation outer ");
                                    }
                                }
                            } else {
//                                enable_composed_truncation_outer=false;
                            }


                        } catch (IllegalArgumentException ex) {
                            ex.printStackTrace();
                        }

                        break;
                    }
                }

                // Actions à réaliser quand l'utilisateur bouge son doigt
                case MotionEvent.ACTION_MOVE: {
                    if (on) {
                        float mx = event.getX(id);
                        float my = event.getY(id);
                        //**************** NEW - Hiba: bruit quand tour du cd*******************
                        MediaPlayer mp = MediaPlayer.create(getContext(),R.raw.gratter);
                        mp.start();
                        //**************************************************************
                        //Commented by Adriana 11/03/2018 (limiter l'image cd à l'intérieur de l'écran)
                        // if (mx >= mImageX && mx <= (mImageX + image.getWidth())) {
                        //     if (my >= mImageY && my <= (mImageY + image.getHeight())) {
                        if (mx >= ((image.getWidth() / 2)) && mx <= ((image.getWidth() / 2) + 230)) {
                            if (my >= (image.getHeight() / 2) && my <= ((image.getHeight() / 2) + 590)) {
                                mImageX = mx - (image.getWidth() / 2);
                                mImageY = my - (image.getHeight() / 2);
                                Log.d(TAG, " ACTION MOVE CD");
                                centerx = (int) mx;
                                centery = (int) my;
                                invalidate();
                                return false;
                            }
                        }
                    } else {

                        // Pour chaque identifiant de contact, on récupère ses coordonnés et on créé une ligne entre chacun des points
                        long time = event.getEventTime();
                        Log.d(TAG, " ACTION MOVE  ID : " + id);
                        Log.d(TAG, " MOVE EVENT TIME= : " + time);
                        Log.d(TAG, " MOVE HISTORYSIZE=  " + historySize);

                        // inserido para truncagem
                        if (!start_truncation_inner) {
                            int mydist = calc_distance(centerx, centery, (int) xDown, (int) yDown);
                            if ((mydist >= 180 + 20) && (mydist <= 590)) { // +20 pour eviter le bruit
                                start_truncation_inner = true;
                            }
                        }
                        if (start_truncation_inner && (number_of_traces <= 5) && (first_truncation_inner || enable_composed_truncation_inner)) {
                            truncated_inner_x.add(xDown);
                            truncated_inner_y.add(yDown);
                        }

                        if (!start_truncation_outer) {
                            int mydist = calc_distance(centerx, centery, (int) xDown, (int) yDown);
                            if ((mydist >= 590 + 20) && (mydist <= 850)) { // +20 pour eviter le bruit
                                start_truncation_outer = true;
                            }
                        }
                        if (start_truncation_outer && (number_of_traces <= 5) && (first_truncation_outer || enable_composed_truncation_outer)) {
                            truncated_outer_x.add(xDown);
                            truncated_outer_y.add(yDown);
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


                        for (int size = event.getPointerCount(), i = 0; i < size; i++) {
                            Log.d(TAG, " MOVE SIZE=  " + size);
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
                                    Log.d(TAG, " MOVE X : " + historicalX);
                                    Log.d(TAG, " MOVE Y : " + historicalY);
                                }
                                tableauX.add(event.getX(i));
                                tableauY.add(event.getY(i));
                                invalidate();
                            }
                        }
                        Log.d(TAG, " ACTION MOVE FINISHED ");
                        invalidate();
                        break;
                    }
                }

                // Actions à réaliser quand l'utilisateur arrête le contact tactile
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_POINTER_UP: {
                    // On ajoute le trait tracé dans la liste des traits terminés, et on le retire de mX et mY
                    Log.d(TAG, " ACTION UP  ID : " + id);
                    Path p = paths.get(id);
                    ArrayList<Float> tableauX = tabsX.get(id);
                    ArrayList<Float> tableauY = tabsY.get(id);

                    if (p != null) {
                        completedPaths.add(p);
                        completedTabsX.add(tableauX);
                        completedTabsY.add(tableauY);
                        eventDownTimes.add(eventTimes.get(id));
                        eventUpTimes.add(System.currentTimeMillis());
                        double mytime = System.currentTimeMillis();
                        isPalm.add(isPalmTouch.get(id));
                        Log.d(TAG, " DURATION TIME : " + (System.currentTimeMillis() - eventTimes.get(id)));
                        xDownList.add(xDown);
                        yDownList.add(yDown);
                        lastUpTime = System.currentTimeMillis();
                        invalidate();
                        isPalmTouch.remove(id);
                        paths.remove(id);
                        eventTimes.remove(id);


                        // teste si le trait fait un angle supérieur à 360 degrés
                        boolean test_angle = verify_angle_circle(tableauX, tableauY, centerx, centery);
                        Log.d(TAG, "  angle > 360 degrees :" + test_angle);
                        // teste si le traçage a été effectué non-stop
                        // les seuils doivent être ajustés en fonction du comportement attendu
                        boolean test_velovity = verify_min_velocity(100, 50);
                        Log.d(TAG, "  velocity > threshold : " + test_velovity);

                        // teste si le tracé était à l'intérieur du petit cercle
                        boolean test_small_circle = verify_circle(tableauX, tableauY, centerx, centery, 180, 590);
                        Log.d(TAG, "  trace within small circle= " + test_small_circle);
                        // teste si le tracé était à l'intérieur du grand cercle
                        boolean test_large_circle = verify_circle(tableauX, tableauY, centerx, centery, 590, 850);
                        Log.d(TAG, "  trace within large circle= " + test_large_circle);


                        boolean test_truncated_inner_angle = false;
                        boolean test_truncated_inner_small_circle = false;
                        // test with truncated
                        if (truncated_inner_x.size() != 0) {
                            test_truncated_inner_angle = verify_angle_circle(truncated_inner_x, truncated_inner_y, centerx, centery);
                            Log.d(TAG, "  truncated inner angle > 360 degrees :" + test_truncated_inner_angle);
                            // teste si le tracé était à l'intérieur du petit cercle
                            test_truncated_inner_small_circle = verify_circle(truncated_inner_x, truncated_inner_y, centerx, centery, 180, 590);
                            Log.d(TAG, "  truncated inner trace within small circle= " + test_truncated_inner_small_circle);

                            if (verify_min_angle(truncated_inner_x, truncated_inner_y, centerx, centery)) {
                                test_incomplet = true;
                            }
                        }

                        boolean test_truncated_outer_angle = false;
                        boolean test_truncated_outer_large_circle = false;
                        // test with truncated
                        if (truncated_outer_x.size() != 0) {
                            test_truncated_outer_angle = verify_angle_circle(truncated_outer_x, truncated_outer_y, centerx, centery);
                            Log.d(TAG, "  truncated outer angle > 360 degrees :" + test_truncated_outer_angle);
                            // teste si le tracé était à l'intérieur du grand cercle
                            test_truncated_outer_large_circle = verify_circle(truncated_outer_x, truncated_outer_y, centerx, centery, 590, 850);
                            Log.d(TAG, "  truncated outer trace within large circle= " + test_truncated_outer_large_circle);

                            if (verify_min_angle(truncated_outer_x, truncated_outer_y, centerx, centery)) {
                                test_incomplet = true;
                            }
                        }


                        // test2 // se fez círculo menor em 1 traço sem parada
                        // test3 // se fez círculo menor em 1 traço com parada
                        // test4 // se fez círculo menor em 2 traços (com/sem parada, não verif)
                        // test5 // se fez círculo maior em 1 traço e sem parada
                        // test6 // se fez círculo maior em 1 traço com parada
                        // test7 // se fez círculo maior em 2 traços (com/sem parada, não verif)
/*
                        // iniciando verificações em um mesmo traçado.
                        if (test_angle){ // é um círculo completo
                            if (test_small_circle) {
                                if (test_velovity){
                                    test2 = true;
                                } else {
                                    test3=true;
                                }
                            }
                            if (test_large_circle) {
                                if (test_velovity){
                                    test5 = true;
                                } else {
                                    test6=true;
                                }
                            }
                        }
*/


                        // démarrage de la vérification avec tronqué
                        if (test_truncated_inner_angle || test_truncated_outer_angle) { // é um círculo completo
                            if (test_truncated_inner_small_circle && !enable_composed_truncation_inner) {
                                if (test_velovity) {
                                    test2 = true;
                                } else {
                                    test3 = true;
                                }
                            }
                            if (test_truncated_outer_large_circle && !enable_composed_truncation_outer) {
                                if (test_velovity) {
                                    test5 = true;
                                } else {
                                    test6 = true;
                                }
                            }
                        }

                        // verificação do truncated
                        if (test_truncated_inner_angle || test_truncated_outer_angle) { // é um círculo completo
                            if (test_truncated_inner_small_circle && enable_composed_truncation_inner) {
                                test4 = true;
                            }
                            if (test_truncated_outer_large_circle && enable_composed_truncation_outer) {
                                test7 = true;
                            }
                        }

/*
                        // começamos a compor traços
                        if (!firstpath) {
                            int sizeoflasttableauxX = lasttableauX.size();
                            if (sizeoflasttableauxX!=0) {
                                int lastpointX = Math.round(lasttableauX.get(sizeoflasttableauxX - 1));
                                int lastpointY = Math.round(lasttableauY.get(sizeoflasttableauxX - 1));
                                if (calc_distance(lastpointX, lastpointY,
                                        Math.round(tableauX.get(0)), Math.round(tableauY.get(0))) < 150) {
                                    ArrayList<Float> composed_tableauX = lasttableauX;
                                    composed_tableauX.addAll(tableauX);
                                    ArrayList<Float> composed_tableauY = lasttableauY;
                                    composed_tableauY.addAll(tableauY);

                                    // testando se o traçado COMPOSTO faz um angulo maior que 360 graus
                                    test_angle = verify_angle_circle(composed_tableauX, composed_tableauY, centerx, centery);
                                    Log.d(TAG, "COMPOSED:  angle > 360 degrees :" + test_angle);
                                    // testando se o traçado COMPOSTO estava dentro do círculo menor
                                    test_small_circle = verify_circle(composed_tableauX, composed_tableauY, centerx, centery, 90, 590);
                                    Log.d(TAG, "  COMPOSED: trace within small circle= " + test_small_circle);
                                    // testando se o traçado COMPOSTO estava dentro do círculo maior
                                    test_large_circle = verify_circle(composed_tableauX, composed_tableauY, centerx, centery, 590, 850);
                                    Log.d(TAG, "  COMPOSED: trace within large circle= " + test_large_circle);

                                    // test2 // se fez círculo menor em 1 traço sem parada
                                    // test3 // se fez círculo menor em 1 traço com parada
                                    // test4 // se fez círculo menor em 2 traços (com/sem parada, não verif)
                                    // test5 // se fez círculo maior em 1 traço e sem parada
                                    // test6 // se fez círculo maior em 1 traço com parada
                                    // test7 // se fez círculo maior em 2 traços (com/sem parada, não verif)

                                    // iniciando verificações em um mesmo traçado.
                                    if (test_angle){ // é um círculo completo
                                        if (test_small_circle) {
                                            test4 = true;
                                        }
                                        if (test_large_circle) {
                                            test7 = true;
                                        }
                                    }

                                    // verificação do truncated
                                    if (test_truncated_inner_angle || test_truncated_outer_angle){ // é um círculo completo
                                        if (test_truncated_inner_small_circle && enable_composed_truncation_inner) {
                                            test4 = true;
                                        }
                                        if (test_truncated_outer_large_circle && enable_composed_truncation_outer) {
                                            test7 = true;
                                        }
                                    }
                                }
                            }
                            lasttableauX.addAll(tableauX);
                            lasttableauY.addAll(tableauY);
                        } else{
                            lasttableauX.clear();
                            lasttableauY.clear();
                        }
*/

                        Log.d(TAG, " test 1 = " + test1);
                        Log.d(TAG, " test 2 = " + test2);
                        Log.d(TAG, " test 3 = " + test3);
                        Log.d(TAG, " test 4 = " + test4);
                        Log.d(TAG, " test 5 = " + test5);
                        Log.d(TAG, " test 6 = " + test6);
                        Log.d(TAG, " test 7 = " + test7);

                        boolean cotation_3 = false;
                        boolean cotation_2 = false;
                        boolean cotation_1 = false;

                        // pour une cotation = 3;
                        if (test1 && (
                                ((number_of_traces == 2) && test_angle && test_large_circle && test_velovity) || // derniere tracee complete
                                        ((number_of_traces == 1) && test_truncated_outer_angle && test_truncated_outer_large_circle && test_velovity) ||
                                        ((number_of_traces == 2) && exited_firsttouch_outside_large_circle && test_truncated_outer_angle && test_truncated_outer_large_circle && test_velovity)
                        )) {
                            cotation_3 = true;
                            Log.d(TAG, " cotation = 3!!!!!!!!!!!!!!!!");
                        }

                        // pour une cotation = 2;
                        if (test1 && (
                                (!cotation_3) && (
                                        (test_truncated_outer_angle && test_truncated_outer_large_circle))
                        )) {
                            cotation_2 = true;
                            Log.d(TAG, " cotation = 2!!!!!!!!!!!!!!!!");
                        }

                        // pour une cotation = 1;
                        if (test1 && (
                                (!cotation_3 && !cotation_2) && (
                                        (test_truncated_inner_angle && test_truncated_inner_small_circle))
                        )) {
                            cotation_1 = true;
                            Log.d(TAG, " cotation = 1!!!!!!!!!!!!!!!!");
                        }

                        if (!cotation_3 && !cotation_2 && !cotation_1) {
                            Log.d(TAG, " cotation = 0!!!!!!!!!!!!!!!!");
                        }

                        // the 2 lines below somehow corromped tableauX and tableauT memory!!!
                        //lasttableauX = tableauX;
                        //lasttableauY = tableauY;
                        // the 2 assignments above were replaced by the code below:
/*                        lasttableauX.clear();
                        lasttableauY.clear();
                        lasttableauX.addAll(tableauX);
                        lasttableauY.addAll(tableauY);
*/
                        if (first_truncation_inner && start_truncation_inner) {
                            first_truncation_inner = false;
                        }

                        if (first_truncation_outer && start_truncation_outer) {
                            first_truncation_outer = false;
                        }

                    }
                    if (!on) firstpath = false;
                    Log.d(TAG, " ACTION UP ");

                    if (number_of_traces == 1) {
                        if (calc_distance(centerx, centery, (int) xDown, (int) yDown) < 90) {
                            exited_firsttouch_in_center = true;
                            Log.d(TAG, " EXITED FIRST TOUCH INSIDE CENTER !!!!");
                        } else {
                            Log.d(TAG, " EXITED FIRST TOUCH OUTSIDE CENTER !!!!");
                        }

                        if (calc_distance(centerx, centery, (int) xDown, (int) yDown) < 590) {
                            exited_firsttouch_outside_large_circle = true;
                            Log.d(TAG, " EXITED FIRST TOUCH OUTSIDE LARGE CIRCLE !!!!");
                        } else {
                            Log.d(TAG, " EXITED FIRST TOUCH INSIDE LARGE CIRCLE !!!!");
                        }

                    }


                    invalidate();
                    break;
                }

                case MotionEvent.ACTION_CANCEL: {
                    Log.d(TAG, " PROBLEM WITH PALM TOUCH ");
                    break;
                }

            }
        }

        return true;
    }

    public Long getDurationTime() {
        Long durationTime;
        if (eventDownTimes.size() != 0) {
            durationTime = lastUpTime - eventDownTimes.get(0);
            return durationTime;
        } else {
            return 0l;
        }
    }

    public Bitmap getCartographie() {
        // inserted by Adriana (toutes les lignes utilisent canvas_cv pour enregistrer le dessin sur le pdf)
        //custom_image = Bitmap.createBitmap(getWidth(),getHeight(),Bitmap.Config.ARGB_8888);
        canvas_cv.drawBitmap(custom_image, 0, 0, null);
        canvas_cv = new Canvas(custom_image);

        for (int i = 0; i < completedPaths.size(); i++) {
            Path completedPath = completedPaths.get(i);
            if (isPalm.get(i)) {
                paint.setColor(Color.BLUE);
            } else {
                paint.setColor(Color.BLACK); // pdf
            }
            canvas_cv.drawPath(completedPath, paint);
        }

        return custom_image;
    }


    // fonctions pour renvoyer la valeur de chaque test
    public boolean gettest1() {
        return test1;
    }

    public boolean gettest2() {
        return test2;
    }

    public boolean gettest3() {
        return test3;
    }

    public boolean gettest4() {
        return test4;
    }

    public boolean gettest5() {
        return test5;
    }

    public boolean gettest6() {
        return test6;
    }

    public boolean gettest7() {
        return test7;
    }

    public boolean gettest_incomplet() {
        return test_incomplet;
    }

    public Canvas getCanvas() {
        return canvas_cv;
    }

    public Paint getPaint() {
        return paint;
    }

    public ArrayList getTableauX() {
        return completedTabsX;
    }

    public int getnpaths() {
        return completedPaths.size();
    }

    public ArrayList getTableauY() {
        return completedTabsY;
    }

    public void getBooleanClick(boolean click) {
        on = click;
    }

    public ArrayList getEventUpTimes() {
        return eventUpTimes;
    }

    public ArrayList getEventDownTimes() {
        return eventDownTimes;
    }

    public ArrayList getmy_times() {
        return my_times;
    }

    public ArrayList getmy_X() {
        return my_X;
    }

    public ArrayList getmy_Y() {
        return my_Y;
    }

    public Float getCdX() {
        return mImageX;
    }

    public Float getCdY() {
        return mImageY;
    }

    public ArrayList getBooleanPalm() {
        return isPalm;
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

    private Bitmap getResizeBitmap(Bitmap bitmap) {
        // L'image serait redimensionné pour le taille du CD (1317 px avec 300ppi de résolution)
        float aspect_ratio = bitmap.getWidth() / bitmap.getHeight();
        int mImageWidth = 1317;
        int mImageHeight = Math.round(mImageWidth * aspect_ratio);
        bitmap = Bitmap.createScaledBitmap(bitmap, mImageWidth, mImageHeight, false);
        return bitmap.copy(Bitmap.Config.ARGB_8888, false);
    }

    public void orderPaths() {
        HashMap<Integer, Long> upTimes = new HashMap<>();

        for (int i = 0; i < completedTabsX.size(); i++) {
            tabsX.put(i, completedTabsX.get(i));
            tabsY.put(i, completedTabsY.get(i));
            upTimes.put(i, eventUpTimes.get(i));
            eventTimes.put(i, eventDownTimes.get(i));
            isPalmTouch.put(i, isPalm.get(i));
        }

        for (int i = 0; i < (completedTabsX.size() - 1); i++) {
            for (int j = i + 1; j < completedTabsX.size(); j++) {
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
                    isPalmTouch.put(i, isPalmTouch.get(j));
                    isPalmTouch.put(j, b);
                }
            }
        }


        completedTabsX.clear();
        completedTabsY.clear();
        eventUpTimes.clear();
        eventDownTimes.clear();
        isPalm.clear();

        for (int i = 0; i < tabsX.size(); i++) {
            completedTabsX.add(tabsX.get(i));
            completedTabsY.add(tabsY.get(i));
            eventUpTimes.add(upTimes.get(i));
            eventDownTimes.add(eventTimes.get(i));
            isPalm.add(isPalmTouch.get(i));
        }
    }


    // Funções de cotations automáticas
    public int calc_distance(int x1, int y1, int x2, int y2) {
        return (int) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    public int calc_angle(int xc, int yc, int xp, int yp) {
        double cateto_oposto = yp - yc;
        double hipotenusa = calc_distance(xc, yc, xp, yp);
        double angulorad = Math.asin(cateto_oposto / hipotenusa);
        int angulobruto = -(int) (double) (angulorad * 180 / 3.1415);
        int angulo;
        if ((xp <= xc) && (yp <= yc)) { // quadrante II
            angulo = 180 - angulobruto;
        } else if ((xp <= xc) && (yp >= yc)) {// quadrante III
            angulo = 180 - angulobruto;
        } else if ((xp >= xc) && (yp >= yc)) {// quadrante IV
            angulo = 360 + angulobruto;
        } else {
            angulo = angulobruto;
        }
        return angulo;
    }

    public boolean verify_circle(ArrayList<Float> coordenadasX, ArrayList<Float> coordenadasY,
                                 int xc, int yc, int rmin, int rmax) {
        boolean teste = true;
        for (int i = 0; i < coordenadasX.size(); i = i + 1) {
            if (((calc_distance(xc, yc, Math.round(coordenadasX.get(i)), Math.round(coordenadasY.get(i)))) > rmax) ||
                    ((calc_distance(xc, yc, Math.round(coordenadasX.get(i)), Math.round(coordenadasY.get(i)))) < rmin)) {
                teste = false;
            }
        }
        return teste;
    }

    public boolean verify_angle_circle(ArrayList<Float> coordenadasX, ArrayList<Float> coordenadasY,
                                       int xc, int yc) {
        int angulo_total = 0;
        int angulo_atual;
        int angulo_anterior;
        int diferenca;

        //premier point
        angulo_anterior = calc_angle(xc, yc, Math.round(coordenadasX.get(0)), Math.round(coordenadasY.get(0)));
        // pour les autres points
        for (int i = 1; i < coordenadasX.size(); i = i + 1) {
            angulo_atual = calc_angle(xc, yc, Math.round(coordenadasX.get(i)), Math.round(coordenadasY.get(i)));
            diferenca = angulo_atual - angulo_anterior; // calcula a diferança de angulos
            if (diferenca != 0) { // s'il y avait une différence d'angles, fais quelque chose.
                if (Math.abs(diferenca) > 180) { // si très grande différence, sauté en franchissant les 0/360 degrés
                    if (angulo_atual < 90) {  // croisé dans le sens antihoraire
                        diferenca = diferenca + 360;// ajoute un décalage positif
                    } else {// croisé dans le sens des aiguilles d'une montre
                        diferenca = diferenca - 360; // ajoute un décalage négatif
                    }
                }
                angulo_total = angulo_total + diferenca;// agora acumula a diferença, seja positiva ou negativa
            }
            angulo_anterior = angulo_atual; // atualiza o angulo para o próximo loop.
        }
        if (Math.abs(angulo_total) >= 335) { // retorna positivo apenas se o ângulo for maior que 300 graus
            return true;
        } else {
            return false;
        }
    }


    public boolean verify_min_angle(ArrayList<Float> coordenadasX, ArrayList<Float> coordenadasY,
                                    int xc, int yc) {
        int angulo_total = 0;
        int angulo_atual;
        int angulo_anterior;
        int diferenca;

        // primeiro ponto
        angulo_anterior = calc_angle(xc, yc, Math.round(coordenadasX.get(0)), Math.round(coordenadasY.get(0)));
        // para o restante de pontos
        for (int i = 1; i < coordenadasX.size(); i = i + 1) {
            angulo_atual = calc_angle(xc, yc, Math.round(coordenadasX.get(i)), Math.round(coordenadasY.get(i)));
            diferenca = angulo_atual - angulo_anterior; // calcula a diferança de angulos
            if (diferenca != 0) { // se houve diferença de ângulos, faz algo.
                if (Math.abs(diferenca) > 180) { // se diferença muito grande, deu um salto, cruzando o 0/360 graus
                    if (angulo_atual < 90) {  // cruzou no sentido anti-horário
                        diferenca = diferenca + 360; // acrescentar off-set positivo
                    } else { // cruzou no sentido horário
                        diferenca = diferenca - 360; // acrescentar off-set negativo
                    }
                }
                angulo_total = angulo_total + diferenca;// agora acumula a diferença, seja positiva ou negativa
            }
            angulo_anterior = angulo_atual; // atualiza o angulo para o próximo loop.
        }
        if (Math.abs(angulo_total) >= 30) { // retorna positivo apenas se o ângulo for maior que 300 graus
            return true;
        } else {
            return false;
        }
    }


    // verificando a velocidade
    public boolean verify_min_velocity(int limiar_inicio, int limiar_teste) {
        float minimo = 1000000;
        // variável usada para evitar que o dedo parado no início do teste seja computado
        // com velocidade zero, fazendo com que pense-se que houve uma parada.
        boolean user_started = false;
        // o averaging é o número de pontos de ação "Move" que são usados em uma média
        // Geralmente, a cada 30 ms é gerado um novo move. Este número deve ser ajustado
        // de acordo com o comportamento esperado.
        int averaging = 10;

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
                user_started = true;
            }
            if (user_started) {
                if (velocidade < minimo) {
                    minimo = velocidade;
                }
            }
        }
        // apagando a memória
        delta_distance = new ArrayList<>();
        delta_tempo = new ArrayList<>();
        Log.d(TAG, " Velocidade minima =" + minimo);
        // retornando o valor
        if (minimo >= limiar_teste) {
            return true;
        } else {
            return false;
        }
    }


}
