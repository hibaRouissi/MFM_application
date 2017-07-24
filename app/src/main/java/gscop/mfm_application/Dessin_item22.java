package gscop.mfm_application;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;

public class Dessin_item22 extends View {

    private Bitmap cartographie;
    private final Paint paint = new Paint();

    private HashMap<Integer, Float> mX = new HashMap<>();
    private HashMap<Integer, Float> mY = new HashMap<>();
    private HashMap<Integer, Path> paths = new HashMap<>();
    private ArrayList<Path> completedPaths = new ArrayList<>();

    private ArrayList<Float> tableauX = new ArrayList<>();
    private ArrayList<Float> tableauY = new ArrayList<>();

    private final RectF dirtyRect = new RectF();

    private float xDown;
    private float yDown;
    private ArrayList<Float> xDownList = new ArrayList<>();
    private ArrayList<Float> yDownList = new ArrayList<>();

    /**
     * Créé une nouvelle instance de Dessin_item22.
     *
     * @param context
     */
    public Dessin_item22(Context context) {
        super(context);
    }

    /**
     * Créé une nouvelle instance de Dessin_item222.
     *
     * @param context
     * @param attrs
     */
    public Dessin_item22(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Créé une nouvelle instance de Dessin_item22.
     *
     * @param context
     * @param attrs
     * @param defStyle
     */
    public Dessin_item22(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        completedPaths = new ArrayList<>();

        // Initialise les caractéristiques du trait (forme, couleur...)
        paint.setAntiAlias(true);
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(26);
        paint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // On transforme le drawable du CD en bitmap
        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.item22);

        // On fait en sorte que l'image du CD soit toujours de la même taille quelle que soit la tablette utilisée : l'image est redimensionnée pour être à la bonne taille en fonction de la densité de l'écran
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        float totalDIP_X = metrics.xdpi;
        float totalDIP_Y = metrics.ydpi;
        image = Bitmap.createScaledBitmap (image, (int)(6.6*totalDIP_X), (int)(7.2*totalDIP_Y), false);

        image = image.copy(Bitmap.Config.ARGB_8888, true);

        // On ajoute ce bitmap au canvas pour pouvoir dessiner dessus : les deux nombres en paramètres servent à positionner l'image dans le canvas
        canvas.drawBitmap(image, 0, 0, null);
        canvas = new Canvas(image);

        // On dessine le trait que l'utilisateur est en train de faire (les points de départ et le mouvement)
        for (Path fingerPath : paths.values()) {
            if (fingerPath != null) {
                canvas.drawPoint(xDown, yDown, paint);
                canvas.drawPath(fingerPath, paint);
            }
        }

        // Permet de garder le dessin des points de départ à l'écran (sinon il arrive qu'il n'y ait aucun dessin lorsqu'on touche rapidement l'écran avec un doigt sans le bouger)
        for (int i = 0; i < xDownList.size(); i++) {
            canvas.drawPoint(xDownList.get(i), yDownList.get(i), paint);
        }

        // On affiche les traits terminés (sinon seul le trait en train d'être dessiné est affiché)
        for (Path completedPath : completedPaths) {
            canvas.drawPath(completedPath, paint);
        }

        this.cartographie = image;
    }

    //
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
                Path p = new Path();
                // On récupère l'identifiant du contact tactile et ses coordonnées
                try {
                    p.moveTo(event.getX(id), event.getY(id));
                    paths.put(id, p);

                    // Contiennent les coordonnées rangées par identifiant
                    mX.put(id, event.getX(id));
                    mY.put(id, event.getY(id));

                    // Contiennent les coordonnées du premier point touché
                    xDown = event.getX(id);
                    yDown = event.getY(id);

                    // Contiennent toutes les coordonnées brutes
                    tableauX.add(event.getX(id));
                    tableauY.add(event.getY(id));

                    invalidate();

                } catch (IllegalArgumentException ex) {
                    ex.printStackTrace();
                }
                break;
            }

            // Actions à réaliser quand l'utilisateur bouge son doigt
            case MotionEvent.ACTION_MOVE: {
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
                        mX.put(event.getPointerId(i), event.getX(i));
                        mY.put(event.getPointerId(i), event.getY(i));
                        tableauX.add(event.getX(i));
                        tableauY.add(event.getY(i));
                        invalidate();
                    }
                }
                invalidate();
                break;
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
                    mX.remove(id);
                    mY.remove(id);
                }
                invalidate();

                break;
            }
        }

        return true;
    }


    public Bitmap getCartographie() {
        return cartographie;
    }

    public ArrayList getTableauX() {
        return tableauX;
    }

    public ArrayList getTableauY() {
        return tableauY;
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

}
