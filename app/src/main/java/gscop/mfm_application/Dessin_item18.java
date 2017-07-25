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
    private int resize = 1317;

    private HashMap<Integer, Path> paths = new HashMap<>();
    private ArrayList<Path> completedPaths = new ArrayList<>();

    private ArrayList<Float> tableauX = new ArrayList<>();
    private ArrayList<Float> tableauY = new ArrayList<>();

    private final RectF dirtyRect = new RectF();

    private float xDown;
    private float yDown;
    private ArrayList<Float> xDownList = new ArrayList<>();
    private ArrayList<Float> yDownList = new ArrayList<>();
    private ArrayList<Long> time_path = new ArrayList<>();

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

        image = BitmapFactory.decodeResource(getResources(), R.drawable.item18_test);
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

        for (Path fingerPath : paths.values()) {
            if (fingerPath != null) {
                canvas.drawPoint(xDown, yDown, paint);
                canvas.drawPath(fingerPath, paint);
            }
        }

        for (int i = 0; i < xDownList.size(); i++) {
            canvas.drawPoint(xDownList.get(i), yDownList.get(i), paint);
        }

        for (Path completedPath : completedPaths) {
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
                if(on == true){
                    break;
                }
                else {
                    Path p = new Path();
                    // On récupère l'identifiant du contact tactile et ses coordonnées
                    try {
                        p.moveTo(event.getX(id), event.getY(id));
                        paths.put(id, p);

                        // Contiennent les coordonnées du premier point touché
                        xDown = event.getX(id);
                        yDown = event.getY(id);


                        // Contiennent toutes les coordonnées brutes
                        tableauX.add(event.getX(id));
                        tableauY.add(event.getY(id));

                        Log.d(TAG, " ACTION DOWN  ID : " + id);
                        Log.d(TAG, " SIZE : " + event.getSize(id) + " TYPE : " + event.getToolType(id));
                        Log.d(TAG, " MAJOR : " + event.getToolMajor(id) + " MINOR : " + event.getToolMinor(id));
                        Log.d(TAG, " POINTS : " + event.getPointerCount());

                        invalidate();

                    } catch (IllegalArgumentException ex) {
                        ex.printStackTrace();
                    }
                }
                break;
            }

            // Actions à réaliser quand l'utilisateur bouge son doigt
            case MotionEvent.ACTION_MOVE: {
                if (on == true) {
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
                        for (int size = event.getPointerCount(), i = 0; i < size; i++) {
                            Path p = paths.get(event.getPointerId(i));
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
                if (p != null) {
                    completedPaths.add(p);
                    xDownList.add(xDown);
                    yDownList.add(yDown);
                    invalidate();
                    paths.remove(id);
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



    public Bitmap getCartographie() {return custom_image;}

    public Canvas getCanvas() {return canvas_cv;}

    public Paint getPaint() {return paint;}

    public ArrayList getTableauX() {
        return tableauX;
    }

    public ArrayList getTableauY() {
        return tableauY;
    }

    public void getBooleanClick(boolean click) {
        on = click;
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
        int mImageWidth = resize;
        int mImageHeight = Math.round(mImageWidth*aspect_ratio);
        bitmap = Bitmap.createScaledBitmap(bitmap,mImageWidth,mImageHeight,false);
        return bitmap.copy(Bitmap.Config.ARGB_8888,false);
    }
}
