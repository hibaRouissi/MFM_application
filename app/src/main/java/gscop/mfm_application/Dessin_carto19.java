package gscop.mfm_application;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by Alison.rl on 30/07/2017.
 */

public class Dessin_carto19 extends View implements View.OnClickListener {

    private Paint paint ;
    private final Paint mPaintRect = new Paint();

    private Boolean animStop;
    private int stop = 0;
    private float mImageX,mImageY;
    //insered by Adriana 25_02_18 (pour enregistrer le dessin en pdf)
    private float mImage2X,mImage2Y;
    private int mRectX, mRectY;
    //insered by Adriana 25_02_18 (pour enregistrer le dessin en pdf)
    private int mRect2X, mRect2Y;
    private static final String TAG = "gscop.mfm_application";

    private ArrayList<ArrayList<Float>> completedTabsX = new ArrayList<>();
    private ArrayList<ArrayList<Float>> completedTabsY = new ArrayList<>();
    private HashMap<Integer,Float> animCurrentPos = new HashMap();

    private ArrayList<Long> durationTimes = new ArrayList<>();
    private ArrayList<Long> eventDownTimes = new ArrayList<>();
    private ArrayList<Long> eventUpTimes = new ArrayList<>();
    private ArrayList<Long> animTimesInit = new ArrayList<>();

    private ArrayList<Path> paths = new ArrayList<>();
    private ArrayList<Boolean> isPalm = new ArrayList<>();
    private HashMap<Integer,Path> animPaths = new HashMap<>();

    private HashMap<Integer,Boolean> animRunning = new HashMap<>();
    private HashMap<Integer,PathMeasure> animPathsMeasure = new HashMap<>();
    private HashMap<Integer,Long> animLastUpdated = new HashMap<>();

    // nouvelle animation
    private ArrayList<Long> my_times = new ArrayList<>();
    private ArrayList<Float> my_X = new ArrayList<>();
    private ArrayList<Float> my_Y = new ArrayList<>();
    private int myi=0;
    private long lasttemp;


    public Dessin_carto19(Context context) {
        super(context);
        init();
    }

    public Dessin_carto19(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Dessin_carto19(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setAntiAlias(true);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.STROKE);
        //paint.setStrokeWidth(20); Commented by Adriana
        paint.setStrokeWidth(5);

        mPaintRect.setAntiAlias(true);
        mPaintRect.setColor(Color.BLACK);
        mPaintRect.setStyle(Paint.Style.STROKE);
        //mPaintRect.setStrokeWidth(6); Commented by Adriana
        mPaintRect.setStrokeWidth(19);

        animStop = true;
        this.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // pour la nouvelle animation
        myi=0;

        for(int j = 0; j < completedTabsX.size(); j++ ){
            animRunning.put(j,false);
            animPathsMeasure.put(j,null);
        }
        for(int j = 0; j < eventDownTimes.size(); j++){
            durationTimes.add(eventUpTimes.get(j) - eventDownTimes.get(j));
            Log.d(TAG," DURATION : " + j + durationTimes.get(j));
        }
        startAnimation();
    }

    public void startAnimation(){
        animRunning.put(0, true);
        stop = 0;
        animStop = false;
        invalidate();
    }

    public void setPaths(){
        Log.d(TAG, " NUMBERS OF PATHS : " + completedTabsX.size());
        for(int j = 0; j < completedTabsX.size(); j++) {
            Log.d(TAG, " TABLEAU : " + completedTabsX.get(j).size());
            Path path = new Path();
            for (int i = 0; i < completedTabsX.get(j).size(); i++) {
                Log.d(TAG, " I : " + i);
                if (i == 0) {
                    path.moveTo(completedTabsX.get(j).get(i), completedTabsY.get(j).get(i));
                } else {
                    path.lineTo(completedTabsX.get(j).get(i), completedTabsY.get(j).get(i));
                }
            }
            paths.add(path);
        }
        Log.d(TAG, " PATHS LOADED ! ");
    }


    @Override
    protected void onDraw(Canvas canvas) {

        // inserted in 25/02/2018. See dessin_item_19
        mRectX = (int) mImageX ;
        mRectY = (int) mImageY ;
        mRect2X = (int) mImage2X ;
        mRect2Y = (int) mImage2Y ;

        Rect rect = new Rect ((mRectX - 217), (mRectY + 63),(mRectX + 217),(mRectY - 63));
        canvas.drawRect(rect,mPaintRect);

        paint.setStrokeWidth(8);

        if (my_X.size()!=0){
            if (myi==0){
                lasttemp=System.currentTimeMillis();
                canvas.drawPoint(my_X.get(myi), my_Y.get(myi), paint);
                paint.setColor(Color.MAGENTA);
                myi=1;
            } else if (myi<my_X.size() ){
                long waittime=(my_times.get(myi)-my_times.get(myi-1))-(System.currentTimeMillis()-lasttemp);
                if (waittime>0){
                    try {
                        TimeUnit.MILLISECONDS.sleep(waittime*9/10); // adiantamos com base na observação do tempo do traçado
                    } catch (InterruptedException e) {
                        e.printStackTrace();

                    }
                }
                for (int toto=0; toto<=myi; toto++){
                    canvas.drawPoint(my_X.get(toto), my_Y.get(toto), paint);
                }
                lasttemp=System.currentTimeMillis();
                paint.setColor(Color.RED);
                myi=myi+1;

            } else {
                animStop=true;
                setPaths();
                paint.setStrokeWidth(5);
                for(int j = 0; j < paths.size(); j++){
                    paint.setColor(Color.RED);
                    canvas.drawPath(paths.get(j),paint);
                }
            }
        }


        paint.setStrokeWidth(5);



        if(animStop){
/*
            if(paths.size() == 0 && completedTabsX.size() != 0) {
                setPaths();
                invalidate();
            }
            else{
                for(int j = 0; j < paths.size(); j++){
                    if(isPalm.get(j)){
                        paint.setColor(Color.BLUE);
                    }
                    else{
                        paint.setColor(Color.RED);
                    }
                    canvas.drawPath(paths.get(j),paint);
                }
            }
 */
        } else {
            for (int j = 0; j < paths.size(); j++) {
                Log.d(TAG, " J : " + j);
                isTime(j);
//                if (animRunning.get(j)) {drawAnim(canvas, j);}
//                else {drawStatic(canvas, j);}
            }
            super.onDraw(canvas);
            invalidate();
        }

        super.onDraw(canvas);
    }//



    public void isTime(int j){
        if (j != 0) {
            if(animTimesInit.size() == j) {
                long time = System.currentTimeMillis() - animTimesInit.get(j - 1);
                long timeBetPath = eventDownTimes.get(j) - eventDownTimes.get(j - 1);

                Log.d(TAG, " TIME : " + time);
                Log.d(TAG, " TIME BET : " + timeBetPath);


                if (time > timeBetPath) {
                    animRunning.put(j, true);
                }
            }
        }
    }

    public void drawAnim(Canvas canvas, int j){

        if(animPathsMeasure.get(j) == null){
            // Set the first point of the Path
            PathMeasure animPathMesure = new PathMeasure(paths.get(j),false);
            animPathMesure.nextContour();
            Path animPath = new Path();
            animCurrentPos.put(j,0.0f);
            animLastUpdated.put(j,System.currentTimeMillis());
            animTimesInit.add(System.currentTimeMillis());
            animPathsMeasure.put(j,animPathMesure);
            animPaths.put(j,animPath);
            Log.d(TAG, " INIT DRAW PATH ");
            Log.d(TAG, " PATH LENGTH: " + animPathMesure.getLength());

        } else {
            // Get time since last frame
            long now = System.currentTimeMillis();
            long timeSinceLast = now - animLastUpdated.get(j);
            Log.d(TAG, " TIME SINCE LAST : " + timeSinceLast);

            float animLength = (animPathsMeasure.get(j).getLength()*(timeSinceLast))/durationTimes.get(j);
            float newPos =  animCurrentPos.get(j) + animLength;
            boolean moveTo = (animCurrentPos.get(j) == 0.0f);
            Log.d(TAG, " LENGTH PASS " + animLength);
            Log.d(TAG, " NEW POS " + newPos);

            if(newPos < animPathsMeasure.get(j).getLength()){
                animPathsMeasure.get(j).getSegment(animCurrentPos.get(j), newPos, animPaths.get(j), moveTo);
                animCurrentPos.put(j,newPos);
                animLastUpdated.put(j,now);
            } else {
                animCurrentPos.put(j,(newPos - animLength));
                animPathsMeasure.get(j).getSegment(animCurrentPos.get(j), animPathsMeasure.get(j).getLength(), animPaths.get(j), moveTo);
                animCurrentPos.put(j,0.0f);
                Log.d(TAG, " DONE! ");
                Log.d(TAG, " ERROR TIME : " + ((System.currentTimeMillis() - animTimesInit.get(j)) - durationTimes.get(j)));
                animRunning.put(j, false);
                stop++;
                Log.d(TAG, " STOP NUMBER : " + stop);
                if(stop == paths.size()){
                    Log.d(TAG, " STOP ANIM ");
                    animStop = true;
                    animTimesInit.clear();
                }
            }

            // Draw Path
            if(isPalm.get(j)){
                paint.setColor(Color.BLUE);
            }
            else{
//                paint.setColor(Color.RED);
            }
            canvas.drawPath(animPaths.get(j),paint);
        }
    }

    public void drawStatic(Canvas canvas, int j){
        if(animPathsMeasure.get(j) != null){
            if(isPalm.get(j)){
                paint.setColor(Color.BLUE);
            }
            else{
                paint.setColor(Color.RED);
            }
            canvas.drawPath(paths.get(j),paint);
        }
    }

//    private Bitmap getResizeBitmap(Bitmap bitmap){
//        // L'image serait redimensionné pour le taille du CD (1317 px avec 300ppi de résolution)
//        float aspect_ratio = bitmap.getWidth()/bitmap.getHeight();
//        int mImageWidth = 1317;
//        int mImageHeight = Math.round(mImageWidth*aspect_ratio);
//        bitmap = Bitmap.createScaledBitmap(bitmap,mImageWidth,mImageHeight,false);
//        return bitmap.copy(Bitmap.Config.ARGB_8888,false);
//    }

    public void getTabX(ArrayList<ArrayList<Float>> tabsX) {
        completedTabsX = tabsX;
    }

    public void getTabY(ArrayList<ArrayList<Float>> tabsY) {
        completedTabsY = tabsY;
    }

    public void getEventUpTimes(ArrayList<Long> event_up_times) {eventUpTimes = event_up_times;}

    public void getEventDownTimes(ArrayList<Long> event_times) {
        eventDownTimes = event_times;
    }

    public void getmy_times(ArrayList<Long> my_timesx) { my_times = my_timesx; }
    public void getmy_X(ArrayList<Float> my_Xx) {
        my_X= my_Xx;
    }
    public void getmy_Y(ArrayList<Float> my_Yx) {
        my_Y= my_Yx;
    }

    public void getCdPosition(Float x, Float y, Float x2, Float y2){
        mImageX = x;
        mImageY = y;
        //insered by Adriana 25_02_18 (pour enregistrer le dessin en pdf)
        mImage2X = x2;
        mImage2Y = y2;
    }

    public void getIsPalm(ArrayList<Boolean> palm){
        isPalm = palm;
    }

}
