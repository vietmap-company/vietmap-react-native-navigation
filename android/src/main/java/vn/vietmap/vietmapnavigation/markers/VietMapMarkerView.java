package vn.vietmap.vietmapnavigation.markers;

import android.content.Context;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.facebook.react.views.view.ReactViewGroup;
import com.mapbox.geojson.Point;

import vn.vietmap.vietmapsdk.geometry.LatLng;
import vn.vietmap.vietmapnavigation.VietMapNavigationView;

/**
 * A React Native marker that hosts a real child view on the navigation map. Modeled on
 * vietmap-gl-react-native's MLRNMarkerView, but standalone: instead of being nested inside a
 * map component, it registers with the singleton {@link VietMapNavigationView} and its
 * MarkerViewManager, which keeps the child view positioned at {@code coordinate} as the camera
 * moves.
 *
 * The hosted child is intentionally NOT parented to this ReactViewGroup — the markerview plugin
 * reparents it into the map view. This ReactViewGroup acts purely as a controller.
 */
public class VietMapMarkerView extends ReactViewGroup
        implements MarkerView.OnPositionUpdateListener, View.OnLayoutChangeListener {
    private View childView;
    private String markerId = "";
    private Point coordinate;
    private Float[] anchor;

    private MarkerView markerView;
    private MarkerViewManager markerViewManager;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private int retries = 0;

    public VietMapMarkerView(@NonNull Context context) {
        super(context);
    }

    public void setMarkerId(String id) {
        markerId = id != null ? id : "";
    }

    public void setMarkerChild(View child) {
        childView = child;
        addToMapIfReady();
    }

    public void removeMarkerChild() {
        removeFromMap();
        childView = null;
    }

    public void setCoordinate(Point point) {
        coordinate = point;
        if (markerView != null) {
            refresh();
        } else {
            addToMapIfReady();
        }
    }

    public void setAnchor(float x, float y) {
        anchor = new Float[]{x, y};
        // Re-trigger a position update so the new anchor offset applies immediately.
        refresh();
    }

    private void addToMapIfReady() {
        if (childView == null || coordinate == null || markerView != null) {
            return;
        }
        VietMapNavigationView nav = VietMapNavigationView.Companion.getInstance();
        MarkerViewManager mvm = nav != null ? nav.getOrCreateMarkerViewManager() : null;
        if (mvm == null) {
            // Map (vietMapGL) not ready yet — retry shortly, bounded to ~5s.
            if (retries++ < 50) {
                handler.postDelayed(this::addToMapIfReady, 100);
            }
            return;
        }
        // Wait until RN has laid the child out (size > 0) BEFORE reparenting it into the MapView.
        // The child is a ReactViewGroup that doesn't self-measure (RN drives layout via Yoga); if it
        // enters the MapView (a FrameLayout) still 0-sized it gets a WRAP_CONTENT measure and the
        // ReactViewGroup returns the AT_MOST spec = the FULL MAP size — covering the screen, breaking
        // the anchor, and making any tap "select" it. Pinning the real size first avoids that.
        if (childView.getWidth() == 0 || childView.getHeight() == 0) {
            if (retries++ < 50) {
                handler.postDelayed(this::addToMapIfReady, 50);
            }
            return;
        }
        markerViewManager = mvm;
        // Pin the child to the size RN just laid it out to (as MarginLayoutParams — the MapView's
        // FrameLayout casts to that). A fixed size means the FrameLayout measures it EXACTLY, so it
        // never inflates to the full map again.
        pinChildSize();
        markerView = new MarkerView(toLatLng(coordinate), childView);
        markerView.setOnPositionUpdateListener(this);
        // Keep the size pinned if the content later re-lays-out (e.g. the image finishes loading).
        childView.addOnLayoutChangeListener(this);
        markerViewManager.addMarker(markerView);
    }

    /**
     * Pins the child's LayoutParams to the size RN computed (via Yoga -> layout()), so the size
     * survives being reparented into the MapView. A ReactViewGroup returns 0 from onMeasure, so we
     * must use its laid-out width/height rather than measure() it. No-op until RN has laid it out
     * (handled later by onLayoutChange).
     */
    private void pinChildSize() {
        if (childView == null) {
            return;
        }
        // ALWAYS install MarginLayoutParams, even before the child has a measured size. RN gives the
        // child a plain ViewGroup.LayoutParams (it positions via layout(), not margins); once we
        // reparent it into the MapView (a FrameLayout) the very next measure pass calls
        // measureChildWithMargins, which casts to MarginLayoutParams and throws ClassCastException on
        // a plain one. Using a fixed (w,h) once known also stops the ReactViewGroup from measuring to
        // the full map size under WRAP_CONTENT; until then WRAP_CONTENT is a safe placeholder.
        int w = childView.getWidth();
        int h = childView.getHeight();
        childView.setLayoutParams(new ViewGroup.MarginLayoutParams(
                w > 0 ? w : ViewGroup.LayoutParams.WRAP_CONTENT,
                h > 0 ? h : ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    /** Re-projects the marker for the current coordinate, re-applying the anchor offset. */
    private void refresh() {
        if (markerView != null && coordinate != null) {
            markerView.setLatLng(toLatLng(coordinate));
        }
    }

    @Override
    public PointF onUpdate(PointF pointF) {
        // Anchor in [0..1]x[0..1] from the view's top-left; default (no anchor) is the
        // plugin's own centering. Prefer measured size when the laid-out width is still 0.
        if (anchor != null && childView != null) {
            return new PointF(
                    pointF.x - childView.getWidth() * anchor[0],
                    pointF.y - childView.getHeight() * anchor[1]
            );
        }
        return pointF;
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom,
                               int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if (left != oldLeft || right != oldRight || top != oldTop || bottom != oldBottom) {
            // RN just (re)laid out the child to its real size — pin that size so reparenting into the
            // MapView keeps it, then re-project.
            pinChildSize();
            refresh();
        }
    }

    public void removeFromMap() {
        handler.removeCallbacksAndMessages(null);
        if (childView != null) {
            childView.removeOnLayoutChangeListener(this);
        }
        if (markerView != null && markerViewManager != null) {
            markerViewManager.removeMarker(markerView);
            markerView.setOnPositionUpdateListener(null);
        }
        markerView = null;
    }

    private static LatLng toLatLng(@NonNull Point point) {
        return new LatLng(point.latitude(), point.longitude());
    }
}
