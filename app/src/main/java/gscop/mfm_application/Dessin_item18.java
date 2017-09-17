package gscop.mfm_application;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;

public class Dessin_item18 extends View {


    private static final String TAG = "gscop.mfm_application";
    private Bitmap image;
    private Bitmap custom_image;
    private Canvas canvas_cv = new Canvas();
    private float mImageX, mImageY;
    private final Paint paint = new Paint();
    private final Paint mPaintImage = new Paint();
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

    /**
     * Créé une nouvelle instance de Dessin_item18.
     *
     * @param context
     */
    public Dessin_item18(Context context) {

        super(context);
        init(null);
    }

    /**
     * Créé une nouvelle instance de Dessin_item18.
     *
     * @param context
     * @param attrs
     */
    public Dessin_item18(Context context, AttributeSet attrs) {

        super(context, attrs);
        init(attrs);
    }

    /**
     * Créé une nouvelle instance de Dessin_item18.
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public Dessin_item18(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(@Nullable AttributeSet set) {

        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setAntiAlias(true);
        paint.setColor(Color.TRANSPARENT);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(20);

        image = BitmapFactory.decodeResource(getResources(), R.drawable.item18);
        image = getResizeBitmap(image);

        Log.d(TAG," INIT ");
    }

    @Override
    protected void onDraw(Canvas canvas) {

        custom_image = Bitmap.createBitmap(getWidth(),getHeight(),Bitmap.Config.ARGB_8888);
        canvas.drawBitmap(custom_image,0,0,null);
        canvas = new Canvas(custom_image);

        if (mImageX == 0f || mImageY == 0f) {
            mImageX = (getWidth() - image.getWidth()) / 2;
            mImageY = (3*(getHeight() - image.getHeight()) / 4);
        }

        canvas.drawBitmap(image, mImageX, mImageY, mPaintImage);

        for (int i = 0; i < paths.size(); i++) {
            Path fingerPath = paths.get(i);
            if (fingerPath != null) {
                canvas.drawPoint(xDown, yDown, paint);
                if(isPalmTouch.get(i)){
                    paint.setColor(Color.BLUE);
                }
                else{
                    paint.setColor(Color.RED);
                }
                canvas.drawPath(fingerPath, paint);
            }
        }

        for (int i = 0; i < xDownList.size(); i++) {
            if(isPalm.get(i)){
                paint.setColor(Color.BLUE);
            }
            else{
                paint.setColor(Color.RED);
            }
            canvas.drawPoint(xDownList.get(i), yDownList.get(i), paint);
        }

        for (int i = 0; i < completedPaths.size(); i++) {
            Path completedPath = completedPaths.get(i);
            if(isPalm.get(i)){
                paint.setColor(Color.BLUE);
            }
            else{
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

                    // On récupère l'identifiant du contact tactile et ses coordonnées
                    try {
                        p.moveTo(event.getX(id), event.getY(id));
                        paths.put(id, p);

                        // Contiennent les coordonnées du premier point touché
                        xDown = event.getX(id);
                        yDown = event.getY(id);

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
                    float mx = event.getX(id);
                    float my = event.getY(id);
                    if (mx >= mImageX && mx <= (mImageX + image.getWidth())) {
                        if (my >= mImageY && my <= (mImageY + image.getHeight())) {
                            mImageX = mx - (image.getWidth() / 2);
                            mImageY = my - (image.getHeight() / 2);
                            Log.d(TAG," ACTION MOVE CD");
                            invalidate();
                            return false;
                        }
                    }
                }
                else {
                        // Pour chaque identifiant de contact, on récupère ses coordonnés et on créé une ligne entre chacun des points
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
                                }
                                tableauX.add(event.getX(i));
                                tableauY.add(event.getY(i));
                                invalidate();
                            }
                        }
                        Log.d(TAG," ACTION MOVE ");
                        invalidate();
                        break;
                    }
            }

            // Actions à réaliser quand l'utilisateur arrête le contact tactile
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP: {
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
                Log.d(TAG," ACTION UP ");
                invalidate();
                break;
            }

            case MotionEvent.ACTION_CANCEL:{
                Log.d(TAG," PROBLEM WITH PALM TOUCH ");
                break;
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

    public Float getCdX(){return mImageX;}

    public Float getCdY(){return mImageY;}

    public ArrayList getBooleanPalm() {return isPalm;}


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
}
