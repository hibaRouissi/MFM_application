package gscop.mfm_application;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Alison.rl on 14/09/2017.
 */

public class Dessin_item22 extends View {
    private static final String TAG = "gscop.mfm_application";
    private final Dessin_item22 context = this;
    private final Paint paint = new Paint();
    private final Paint mPaintImage = new Paint();
    private final RectF dirtyRect = new RectF();
    int down_block;
    int move_block;
    int up_block;
    int fisrt_block;
    int current_block;
    int centerx = 767;
    int centery = 1268;
    int blockside = 155;
    int blockoffset = 172;
    int linewidth = 90; // Réglage du bord tactile
    // variables pour la manipulation de points. delta_distance = distance entre le point actuel et le point précédent
    int lastX;
    int lastY;
    private Bitmap image, image1;
    private Bitmap custom_image;
    private Canvas canvas_cv = new Canvas();
    private float mImageX, mImageY;
    private Long lastUpTime = 0l;
    private HashMap<Integer, Path> paths = new HashMap<>();
    private HashMap<Integer, ArrayList<Float>> tabsX = new HashMap<>();
    private HashMap<Integer, ArrayList<Float>> tabsY = new HashMap<>();
    private HashMap<Integer, Long> eventTimes = new HashMap<>();
    private HashMap<Integer, Boolean> isPalmTouch = new HashMap<>();
    private ArrayList<Path> completedPaths = new ArrayList<>();
    private ArrayList<ArrayList<Float>> completedTabsX = new ArrayList<>();
    private ArrayList<ArrayList<Float>> completedTabsY = new ArrayList<>();

    //         ---------------------------
    //         |        |        |        |
    //         |   1    |   2    |    3   |
    //         |        |        |        |
    //         ---------------------------
    //         |        |        |        |
    //         |   4    |   5    |    6   |
    //         |        |        |        |
    //         ---------------------------
    //         |        |        |        |
    //         |   7    |   8    |    9   |
    //         |        |        |        |
    //         ---------------------------
    private ArrayList<Long> eventDownTimes = new ArrayList<>();
    private ArrayList<Long> eventUpTimes = new ArrayList<>();
    private ArrayList<Boolean> isPalm = new ArrayList<>();
    private float xDown;
    private float yDown;
    private ArrayList<Float> xDownList = new ArrayList<>();
    private ArrayList<Float> yDownList = new ArrayList<>();
    private boolean on = false;
    private Paint mysquare = new Paint(); // utilisé uniquement pour marquer les centres tactiles
    // cotation automatique. variables à réinitialiser si l'utilisateur clique sur effacer
    private boolean firsttouch = true; // la toute premiere touche, jusqu´au point up
    private boolean test1 = false; // si le premier contact est au centre de la figure
    private boolean test2 = false; // si le patient a levé son doigt après le premier contact au centre
    private boolean digitou_1 = false;
    private boolean digitou_2 = false;
    private boolean digitou_3 = false;
    private boolean digitou_4 = false;
    private boolean digitou_5 = false;
    private boolean digitou_6 = false;
    private boolean digitou_7 = false;
    private boolean digitou_8 = false;
    private boolean digitou_9 = false;
    private boolean digitou_10 = false; // out of design
    private boolean deactivate_drawing = false;
    private boolean cruzou_1 = false;
    private boolean cruzou_2 = false;
    private boolean cruzou_3 = false;
    private boolean cruzou_4 = false;
    private boolean cruzou_5 = false;


    // centerx: x à partir du centre du dessin complet
    // centery: y du centre du dessin complet
    // blockside: côté qui doit être considéré pour savoir si le point est à l'intérieur d'un bloc
    // blockoffset: offset entre les blocs.
    private boolean cruzou_6 = false;
    private boolean cruzou_7 = false;
    private boolean cruzou_8 = false;
    private boolean cruzou_9 = false;
    private boolean cruzou_10 = false; // out of design
    private boolean test_line;
    private boolean test_line_ok = true;
    private ArrayList<Long> my_times = new ArrayList<>();
    private ArrayList<Float> my_X = new ArrayList<>();
    private ArrayList<Float> my_Y = new ArrayList<>();

    public Dessin_item22(Context context) {

        super(context);
        init(null);
    }

    public Dessin_item22(Context context, AttributeSet attrs) {

        super(context, attrs);
        init(attrs);
    }

    public Dessin_item22(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(@Nullable AttributeSet set) {

        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(linewidth - 5);
        image = BitmapFactory.decodeResource(getResources(), R.drawable.item22); // importer l'image de grille
        image = getResizeBitmap(image);

        Log.d(TAG, " INIT ");

    }

    @Override
    protected void onDraw(Canvas canvas) {

        custom_image = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888); // pour créer une image du type bitmap cad la grille de symboles
        canvas.drawBitmap(custom_image, 0, 0, null);
        canvas = new Canvas(custom_image);

        if (mImageX == 0f || mImageY == 0f) {
            mImageX = (getWidth() - image.getWidth()) / 2;
            mImageY = (3 * (getHeight() - image.getHeight()) / 4);
        }

        canvas.drawBitmap(image, mImageX, mImageY, mPaintImage);


        // supprime les caractères / * pour placer des repères
/*
//    a=-1; b=-1; c=-1; d=-1 : bloco 1 case 1 en haut à gauche
//    a=0; b=-1; c=0; d=-1 : bloco 2
//    a=1; b=-1; c=1; d=-1 : bloco 3
//    a=-1; b=0; c=-1; d=0 : bloco 4
//    a=0; b=0; c=0; d=0 : bloco 5
//    a=1; b=0; c=1; d=0 : bloco 6
//    a=-1; b=1; c=-1; d=1 : bloco 7
//    a=0; b=1; c=0; d=1 : bloco 8
//    a=1; b=1; c=1; d=1 : bloco 9

        int a,b,c,d;
        a=-1; b=-1; c=-1; d=-1;
        Rect rect = new Rect (centerx-blockside/2+a*blockoffset, centery+blockside/2+b*blockoffset,
                centerx+blockside/2+c*blockoffset,centery-blockside/2+d*blockoffset);
        mysquare.setColor(Color.BLUE);
        mysquare.setStrokeWidth(10);
        mysquare.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rect,mysquare);
        a=0; b=-1; c=0; d=-1;
        rect = new Rect (centerx-blockside/2+a*blockoffset, centery+blockside/2+b*blockoffset,
                centerx+blockside/2+c*blockoffset,centery-blockside/2+d*blockoffset);
        mysquare.setColor(Color.BLUE);
        mysquare.setStrokeWidth(10);
        mysquare.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rect,mysquare);
      a=1; b=-1; c=1; d=-1 ;
        rect = new Rect (centerx-blockside/2+a*blockoffset, centery+blockside/2+b*blockoffset,
                centerx+blockside/2+c*blockoffset,centery-blockside/2+d*blockoffset);
        mysquare.setColor(Color.BLUE);
        mysquare.setStrokeWidth(10);
        mysquare.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rect,mysquare);
      a=-1; b=0; c=-1; d=0 ;
        rect = new Rect (centerx-blockside/2+a*blockoffset, centery+blockside/2+b*blockoffset,
                centerx+blockside/2+c*blockoffset,centery-blockside/2+d*blockoffset);
        mysquare.setColor(Color.BLUE);
        mysquare.setStrokeWidth(10);
        mysquare.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rect,mysquare);
      a=0; b=0; c=0; d=0 ;
        rect = new Rect (centerx-blockside/2+a*blockoffset, centery+blockside/2+b*blockoffset,
                centerx+blockside/2+c*blockoffset,centery-blockside/2+d*blockoffset);
        mysquare.setColor(Color.BLUE);
        mysquare.setStrokeWidth(10);
        mysquare.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rect,mysquare);
      a=1; b=0; c=1; d=0 ;
        rect = new Rect (centerx-blockside/2+a*blockoffset, centery+blockside/2+b*blockoffset,
                centerx+blockside/2+c*blockoffset,centery-blockside/2+d*blockoffset);
        mysquare.setColor(Color.BLUE);
        mysquare.setStrokeWidth(10);
        mysquare.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rect,mysquare);
      a=-1; b=1; c=-1; d=1 ;
        rect = new Rect (centerx-blockside/2+a*blockoffset, centery+blockside/2+b*blockoffset,
                centerx+blockside/2+c*blockoffset,centery-blockside/2+d*blockoffset);
        mysquare.setColor(Color.BLUE);
        mysquare.setStrokeWidth(10);
        mysquare.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rect,mysquare);
      a=0; b=1; c=0; d=1 ;
        rect = new Rect (centerx-blockside/2+a*blockoffset, centery+blockside/2+b*blockoffset,
                centerx+blockside/2+c*blockoffset,centery-blockside/2+d*blockoffset);
        mysquare.setColor(Color.BLUE);
        mysquare.setStrokeWidth(10);
        mysquare.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rect,mysquare);
      a=1; b=1; c=1; d=1 ;
        rect = new Rect (centerx-blockside/2+a*blockoffset, centery+blockside/2+b*blockoffset,
                centerx+blockside/2+c*blockoffset,centery-blockside/2+d*blockoffset);
        mysquare.setColor(Color.BLUE);
        mysquare.setStrokeWidth(10);
        mysquare.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rect,mysquare);


        rect = new Rect (centerx-blockside*2, centery+blockside*2,
                centerx+blockside*2,centery-blockside*2);
        mysquare.setColor(Color.BLUE);
        mysquare.setStrokeWidth(2);
        mysquare.setStyle(Paint.Style.STROKE);
        canvas.drawRect(rect,mysquare);

*/
// supprime les caractères * / pour mettre des repères

        for (int i = 0; i < paths.size(); i++) {
            Path fingerPath = paths.get(i);
            if (fingerPath != null) {
                canvas.drawPoint(xDown, yDown, paint);
                if (isPalmTouch.get(i)) {
                    paint.setColor(Color.BLUE);
                } else {
                    paint.setColor(Color.TRANSPARENT);
                }
                canvas.drawPath(fingerPath, paint);
            }
        }

        for (int i = 0; i < xDownList.size(); i++) {
            if (isPalm.get(i)) {
                paint.setColor(Color.BLACK);
            } else {
                paint.setColor(Color.TRANSPARENT);
            }
            canvas.drawPoint(xDownList.get(i), yDownList.get(i), paint);
        }

        for (int i = 0; i < completedPaths.size(); i++) {
            Path completedPath = completedPaths.get(i);
            if (isPalm.get(i)) {
                paint.setColor(Color.GREEN);
            } else {
                paint.setColor(Color.TRANSPARENT);
            }
            canvas.drawPath(completedPath, paint);
        }

        canvas_cv = canvas;
        Log.d(TAG, " ON DRAW ");
    }


    public boolean onTouchEvent(MotionEvent event) {
        int maskedAction = event.getActionMasked();
        int actionIndex = event.getActionIndex();
        int id = event.getPointerId(actionIndex);
        int historySize = event.getHistorySize();
        boolean mytest = false;
        SoundPool sp = new SoundPool(2, AudioManager.STREAM_MUSIC,0);
        int sound= sp.load(getContext(),R.raw.beep,1);
        int sound2= sp.load(getContext(),R.raw.beep2,1);
        int sound3= sp.load(getContext(),R.raw.beep3,1);
        try {
            xDown = event.getX(id);
            yDown = event.getY(id);
            if (xDown >= centerx - blockside * 2 && xDown <= centerx + blockside * 2
                    && yDown >= centery - blockside * 2 && yDown <= centery + blockside * 2)
                mytest = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (mytest) {
            if (!on && !deactivate_drawing) {
                my_times.add(System.currentTimeMillis());
                my_X.add(xDown);
                my_Y.add(yDown);
            }

            switch (maskedAction) {
                // Actions à réaliser quand l'utilisateur touche l'écran
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_POINTER_DOWN: {
                    if (on || deactivate_drawing) {
                        break;
                    } else {

                        Path p = new Path();
                        ArrayList<Float> tableauX = new ArrayList<>();
                        ArrayList<Float> tableauY = new ArrayList<>();

                        // On récupère l'identifiant du contact tactile et ses coordonnées
                        try {
                            p.moveTo(event.getX(id), event.getY(id));
                            paths.put(id, p);

                            // Contiennent les coordonnées du premier point touché
                            xDown = event.getX(id);
                            yDown = event.getY(id);

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

                            down_block = get_current_block((int) xDown, (int) yDown);
                            Log.d(TAG, " CURRENT BLOCK = " + down_block);
                            if (firsttouch) {
                                fisrt_block = down_block;
                                if (fisrt_block == 5) {
                                    test1 = true;
                                    digitou_5 = true;
                                    Log.d(TAG, "  first touch in center= true");
                                    MediaPlayer mp = MediaPlayer.create(getContext(),R.raw.beep3);
                                    mp.start();
                                } else {
                                    Log.d(TAG, "  first touch in center= false");
                                    MediaPlayer mp = MediaPlayer.create(getContext(),R.raw.beep3);
                                    mp.start();
                                }
                            } else if (down_block != current_block) { // nouvelle touche!!
                                if (down_block == 1) {
                                    MediaPlayer mp = MediaPlayer.create(getContext(),R.raw.beep3);
                                    mp.start();
                                    digitou_1 = true;
                                    Log.d(TAG, " TYPED BLOCK 1 ");
                                 //   sp.play(sound,1,1,0,0,1);
                                } else if (down_block == 2) {
                                    digitou_2 = true;
                                    Log.d(TAG, " TYPED BLOCK 2 ");
                                 //   sp.play(sound2,1,1,0,0,1);
                                } else if (down_block == 3) {
                                    digitou_3 = true;
                                    Log.d(TAG, " TYPED BLOCK 3 ");
                                //    sp.play(sound3,1,1,0,0,1);
                                } else if (down_block == 4) {
                                    digitou_4 = true;
                                    Log.d(TAG, " TYPED BLOCK 4 ");
                                 //   sp.play(sound,1,1,0,0,1);
                                } else if (down_block == 5) {
                                    digitou_5 = true;
                                    Log.d(TAG, " TYPED BLOCK 5 ");
                                  //  sp.play(sound2,1,1,0,0,1);
                                } else if (down_block == 6) {
                                    digitou_6 = true;
                                    Log.d(TAG, " TYPED BLOCK 6 ");
                               //     sp.play(sound3,1,1,0,0,1);
                                } else if (down_block == 7) {
                                    digitou_7 = true;
                                    Log.d(TAG, " TYPED BLOCK 7 ");
                                 //   sp.play(sound,1,1,0,0,1);
                                } else if (down_block == 8) {
                                    digitou_8 = true;
                                    Log.d(TAG, " TYPED BLOCK 8 ");
                                 //   sp.play(sound2,1,1,0,0,1);
                                } else if (down_block == 9) {
                                    digitou_9 = true;
                                    Log.d(TAG, " TYPED BLOCK 9 ");
                                  //  sp.play(sound3,1,1,0,0,1);
                                } else if (down_block == 10) {
                                    digitou_10 = true;
                                    Log.d(TAG, " TYPED OUTSIDE THE FIGURE BOUNDARY !!! ");
                                 //   sp.play(sound,1.0f,1.0f,0,0,1.0f);
                                }

                                MediaPlayer mp = MediaPlayer.create(getContext(),R.raw.beep3);
                                mp.start();

                            }

                            test_line = verify_borders_untouched((int) xDown, (int) yDown);
                            current_block = down_block;

                            // set in 1 for never change the color
                            if (event.getSize(id) >= 1) {
                                Log.d(TAG, " PALM TOUCH ");
                                isPalmTouch.put(id, true);
                            } else {
                                Log.d(TAG, " FINGER TOUCH ");
                                isPalmTouch.put(id, false);
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
                        float mx = event.getX(id);
                        float my = event.getY(id);

                        ///Commented by Adriana 11/03/2018 (limiter l'image cd à l'intérieur de l'écran)
                        if (mx >= 270 && mx <= 1270) {
                            if (my >= 270 && my <= 1600) {

                                if (mx >= mImageX && mx <= (mImageX + image.getWidth())) {
                                    if (my >= mImageY && my <= (mImageY + image.getHeight())) {
                                        mImageX = mx - (image.getWidth() / 2);
                                        mImageY = my - (image.getHeight() / 2);
                                        Log.d(TAG, " ACTION MOVE CD");
                                        centerx = (int) mx; // inserted to update the margins
                                        centery = (int) my;// inserted to update the margins
                                        invalidate();
                                        return false;
                                    }
                                }
                            }
                        }
                    } else if (deactivate_drawing) {
                        break;
                    } else {
                        // Pour chaque identifiant de contact, on récupère ses coordonnés et on créé une ligne entre chacun des points
                        long time = event.getEventTime();
                        int xmove = (int) event.getX();
                        int ymove = (int) event.getY();
                        Log.d(TAG, " MOVE X : " + xmove);
                        Log.d(TAG, " MOVE Y : " + ymove);
                        move_block = get_current_block(xmove, ymove);
                        Log.d(TAG, " MOVE BLOCK = " + move_block);
                        if (move_block == current_block) {
                            Log.d(TAG, " STILL IN THE SAME BLOCK ");
                        } else {// there was a crossing!!
                            if (move_block == 1) {
                                cruzou_1 = true;
                                Log.d(TAG, " CROSSED BLOCK 1 ");
                            } else if (move_block == 2) {
                                cruzou_2 = true;
                                Log.d(TAG, " CROSSED BLOCK 2 ");
                            } else if (move_block == 3) {
                                cruzou_3 = true;
                                Log.d(TAG, " CROSSED BLOCK 3 ");
                            } else if (move_block == 4) {
                                cruzou_4 = true;
                                Log.d(TAG, " CROSSED BLOCK 4 ");
                            } else if (move_block == 5) {
                                cruzou_5 = true;
                                Log.d(TAG, " CROSSED BLOCK 5 ");
                            } else if (move_block == 6) {
                                cruzou_6 = true;
                                Log.d(TAG, " CROSSED BLOCK 6 ");
                            } else if (move_block == 7) {
                                cruzou_7 = true;
                                Log.d(TAG, " CROSSED BLOCK 7 ");
                            } else if (move_block == 8) {
                                cruzou_8 = true;
                                Log.d(TAG, " CROSSED BLOCK 8 ");
                            } else if (move_block == 9) {
                                cruzou_9 = true;
                                Log.d(TAG, " CROSSED BLOCK 9 ");
                            } else if (move_block == 10) {
                                cruzou_10 = true;
                                Log.d(TAG, " CROSSED THE FIGURE BOUNDARY !!! ");
                            }
                            current_block = move_block;

                        }

                        // inclus pour ramasser ou traîner depuis un centre
                        // qui s'étend (passe et passe un peu) à une autre place
                        test_line = verify_borders_untouched((int) xDown, (int) yDown);

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
                    } else if (deactivate_drawing) {
                        break;
                    } else {
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
                            Log.d(TAG, " DURATION TIME : " + (System.currentTimeMillis() - eventTimes.get(id)));
                            xDownList.add(xDown);
                            yDownList.add(yDown);
                            lastUpTime = System.currentTimeMillis();
                            invalidate();
                            isPalmTouch.remove(id);
                            paths.remove(id);
                            eventTimes.remove(id);
                            lastX = Math.round(tableauX.get(tableauX.size() - 1));
                            lastY = Math.round(tableauY.get(tableauY.size() - 1));
                            up_block = get_current_block(lastX, lastY);
                            if (!on) {
                                if (firsttouch) { //vérifier si vous avez retiré votre doigt du centre
                                    if ((fisrt_block == 5) && (up_block == 5) && (cruzou_1 == false) && (cruzou_2 == false) &&
                                            (cruzou_3 == false) && (cruzou_4 == false) && (cruzou_5 == false) &&
                                            (cruzou_6 == false) && (cruzou_7 == false) && (cruzou_8 == false) &&
                                            (cruzou_9 == false) && (cruzou_10 == false)) {
                                        test2 = true; // o patiente levantou o dedo
                                        Log.d(TAG, " TEST2 = TRUE !!! ");
                                    }
                                }
                                current_block = up_block;
                            }


                        }
                        Log.d(TAG, " ACTION UP ");

                        Log.d(TAG, " digitou 1 = " + digitou_1);
                        Log.d(TAG, " digitou 2 = " + digitou_2);
                        Log.d(TAG, " digitou 3 = " + digitou_3);
                        Log.d(TAG, " digitou 4 = " + digitou_4);
                        Log.d(TAG, " digitou 5 = " + digitou_5);
                        Log.d(TAG, " digitou 6 = " + digitou_6);
                        Log.d(TAG, " digitou 7 = " + digitou_7);
                        Log.d(TAG, " digitou 8 = " + digitou_8);
                        Log.d(TAG, " digitou 9 = " + digitou_9);
                        Log.d(TAG, " digitou 10 = " + digitou_10);

                        Log.d(TAG, " cruzou 1 = " + cruzou_1);
                        Log.d(TAG, " cruzou 2 = " + cruzou_2);
                        Log.d(TAG, " cruzou 3 = " + cruzou_3);
                        Log.d(TAG, " cruzou 4 = " + cruzou_4);
                        Log.d(TAG, " cruzou 5 = " + cruzou_5);
                        Log.d(TAG, " cruzou 6 = " + cruzou_6);
                        Log.d(TAG, " cruzou 7 = " + cruzou_7);
                        Log.d(TAG, " cruzou 8 = " + cruzou_8);
                        Log.d(TAG, " cruzou 9 = " + cruzou_9);
                        Log.d(TAG, " cruzou 10 = " + cruzou_10);

                        if (digitou_1 && digitou_2 && digitou_3 && digitou_4 && digitou_5 && digitou_6
                                && digitou_7 && digitou_8 && digitou_9) {
                            deactivate_drawing = true;
                        }


                        Log.d(TAG, " test2 = " + test2);
                        Log.d(TAG, " test_line_ok = " + test_line_ok);

                        if (!test_line) {
                            Log.d(TAG, " BORDERS TOUCHED !!!!!!!!!!!!!!!!!!!! ");
                            test_line_ok = false;
                        } else
                            Log.d(TAG, " BORDERS NOT TOUCHED !!!!!!!!!!!!!!!!!!!! ");

                        firsttouch = false;
// juste au moment où le firsttouch est refusé
                        invalidate();
                        break;
                    }
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
            // to avoid crashes
            if (lastUpTime != 0)
                durationTime = lastUpTime - eventDownTimes.get(0);
            else
                durationTime = new Long(1);
            return durationTime;
        } else {
            return 0l;
        }
    }

    public Bitmap getCartographie() {
        // inserted by Adriana (toutes les lignes utilisent canvas_cv pour enregistrer le dessin sur le pdf)
        canvas_cv.drawBitmap(custom_image, 0, 0, null);
        canvas_cv = new Canvas(custom_image);

        for (int i = 0; i < completedPaths.size(); i++) {
            Path completedPath = completedPaths.get(i);
            if (isPalm.get(i)) {
                paint.setColor(Color.BLUE);
            } else {
                paint.setColor(Color.RED);
            }
            canvas_cv.drawPath(completedPath, paint);
        }

        for (int i = 0; i < xDownList.size(); i++) {
            if (isPalm.get(i)) {
                paint.setColor(Color.BLACK);
            } else {
                // paint.setColor(Color.RED)// commented by adriana in 14/02/2018
                paint.setColor(Color.RED); //PDF
            }
            canvas_cv.drawPoint(xDownList.get(i), yDownList.get(i), paint);
        }


        return custom_image;
    }

    //Inserted by Adriana 06/03/2018 (To operate the EFFACER button)
    public void cleancompletedPath() {
        completedPaths = new ArrayList<>();
        completedTabsX = new ArrayList<>();
        completedTabsY = new ArrayList<>();
        xDownList = new ArrayList<>();
        yDownList = new ArrayList<>();


// cotation automatique. variables à réinitialiser si l'utilisateur clique sur effacer
        firsttouch = true; // la toute premiere touche, jusqu´au point up
        test1 = false; // si la première touche est au centre du dessin (bloc 5);
        test2 = false;// a levé son doigt après avoir touché les cinq

        digitou_1 = false;
        digitou_2 = false;
        digitou_3 = false;
        digitou_4 = false;
        digitou_5 = false;
        digitou_6 = false;
        digitou_7 = false;
        digitou_8 = false;
        digitou_9 = false;
        digitou_10 = false; // out of design

        cruzou_1 = false;
        cruzou_2 = false;
        cruzou_3 = false;
        cruzou_4 = false;
        cruzou_5 = false;
        cruzou_6 = false;
        cruzou_7 = false;
        cruzou_8 = false;
        cruzou_9 = false;
        cruzou_10 = false; // out of design
    }


    // fonctions pour renvoyer la valeur de chaque test
    public boolean gettest1() {
        return test1;
    }

    public boolean gettest2() {
        return test2;
    }

    public boolean getdigitou_1() {
        return digitou_1;
    }

    public boolean getdigitou_2() {
        return digitou_2;
    }

    public boolean getdigitou_3() {
        return digitou_3;
    }

    public boolean getdigitou_4() {
        return digitou_4;
    }

    public boolean getdigitou_5() {
        return digitou_5;
    }

    public boolean getdigitou_6() {
        return digitou_6;
    }

    public boolean getdigitou_7() {
        return digitou_7;
    }

    public boolean getdigitou_8() {
        return digitou_8;
    }

    public boolean getdigitou_9() {
        return digitou_9;
    }

    public boolean getdigitou_10() {
        return digitou_10;
    }

    public boolean getcruzou_1() {
        return cruzou_1;
    }

    public boolean getcruzou_2() {
        return cruzou_2;
    }

    public boolean getcruzou_3() {
        return cruzou_3;
    }

    public boolean getcruzou_4() {
        return cruzou_4;
    }

    public boolean getcruzou_5() {
        return cruzou_5;
    }

    public boolean getcruzou_6() {
        return cruzou_6;
    }

    public boolean getcruzou_7() {
        return cruzou_7;
    }

    public boolean getcruzou_8() {
        return cruzou_8;
    }

    public boolean getcruzou_9() {
        return cruzou_9;
    }

    public boolean getcruzou_10() {
        return cruzou_10;
    }

    public boolean get_test_line_ok() {
        return test_line_ok;
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

    // inserted on 28/02
    public ArrayList getxDownList() {
        return xDownList;
    }

    public ArrayList getyDownList() {
        return yDownList;
    }

    public Float getQX() {
        return mImageX;
    }

    public Float getQY() {
        return mImageY;
    }

    public ArrayList getBooleanPalm() {
        return isPalm;
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
        // L'image serait redimensionné pour le taille du quadrillage (200 px avec 300ppi de résolution)
        float aspect_ratio = bitmap.getWidth() / bitmap.getHeight();
        // int mImageWidth = 600;// commented by adriana in 12/01/2018
        int mImageWidth = 540; // inserted by adriana in 12/01/2018 to reduce the size
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


    // fonctions de cotation automatique
    public boolean verify_square(int xp, int yp, int xc, int yc, int lado) {
        if ((xp >= xc - (lado / 2)) && (xp <= xc + (lado / 2)) && (yp >= yc - (lado / 2)) && (yp < yc + (lado / 2))) {
            return true;
        } else {
            return false;
        }
    }


    // cette fonction dépend des variables globales suivantes:
    // centerx: x à partir du centre du dessin complet
    // centery: y du centre du dessin complet
    // blockside: côté qui doit être considéré pour savoir si le point est à l'intérieur d'un bloc
    // blockoffset: offset entre les blocs.
    // Ces éléments ci-dessus peuvent être ajustés. Les deux premiers
    // doit être mis à jour si le dessin est changé de position.

    public int get_current_block(int xp, int yp) {

        boolean quadrado1 = verify_square(xp, yp, centerx - blockoffset, centery - blockoffset, blockside);
        boolean quadrado2 = verify_square(xp, yp, centerx, centery - blockoffset, blockside);
        boolean quadrado3 = verify_square(xp, yp, centerx + blockoffset, centery - blockoffset, blockside);
        boolean quadrado4 = verify_square(xp, yp, centerx - blockoffset, centery, blockside);
        boolean quadrado5 = verify_square(xp, yp, centerx, centery, blockside);
        boolean quadrado6 = verify_square(xp, yp, centerx + blockoffset, centery, blockside);
        boolean quadrado7 = verify_square(xp, yp, centerx - blockoffset, centery + blockoffset, blockside);
        boolean quadrado8 = verify_square(xp, yp, centerx, centery + blockoffset, blockside);
        boolean quadrado9 = verify_square(xp, yp, centerx + blockoffset, centery + blockoffset, blockside);
        boolean quadrado10 = verify_square(xp, yp, centerx, centery, (int) (blockoffset * 1.6));

        if (quadrado1) {
            return 1;
        } else if (quadrado2) {
            return 2;
        } else if (quadrado3) {
            return 3;
        } else if (quadrado4) {
            return 4;
        } else if (quadrado5) {
            return 5;
        } else if (quadrado6) {
            return 6;
        } else if (quadrado7) {
            return 7;
        } else if (quadrado8) {
            return 8;
        } else if (quadrado9) {
            return 9;
        } else if (quadrado10) {
            return 10; // n'est pas dans un carré !!
        } else {
            return 0;
        }
    }


    public boolean verify_borders_untouched(int xp, int yp) {
        float delta = (float) 8.5;
        boolean test = true;

        if (((xp >= centerx - blockoffset - blockside / 2 - delta - linewidth / 2) && (xp <= centerx + blockoffset + blockside / 2 + delta + linewidth / 2))
                && (
                ((yp >= centery + blockoffset + blockside / 2 + delta - linewidth / 2) && (yp <= centery + blockoffset + blockside / 2 + delta + linewidth / 2))
                        || ((yp >= centery + blockside / 2 + delta - linewidth / 2) && (yp <= centery + blockside / 2 + delta + linewidth / 2))
                        || ((yp >= centery - blockside / 2 - delta - linewidth / 2) && (yp <= centery - blockside / 2 - delta + linewidth / 2))
                        || ((yp >= centery - blockoffset - blockside / 2 - delta - linewidth / 2) && (yp <= centery - blockoffset - blockside / 2 - delta + linewidth / 2)))) {
            test = false;
        }

        if (((yp >= centery - blockoffset - blockside / 2 - delta - linewidth / 2) && (yp <= centery + blockoffset + blockside / 2 + delta + linewidth / 2))
                && (
                ((xp >= centerx + blockoffset + blockside / 2 + delta - linewidth / 2) && (xp <= centerx + blockoffset + blockside / 2 + delta + linewidth / 2))
                        || ((xp >= centerx + blockside / 2 + delta - linewidth / 2) && (xp <= centerx + blockside / 2 + delta + linewidth / 2))
                        || ((xp >= centerx - blockside / 2 - delta - linewidth / 2) && (xp <= centerx - blockside / 2 - delta + linewidth / 2))
                        || ((xp >= centerx - blockoffset - blockside / 2 - delta - linewidth / 2) && (xp <= centerx - blockoffset - blockside / 2 - delta + linewidth / 2)))) {
            test = false;
        }

        return test;

    }


}

