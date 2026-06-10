package vn.vietmap.vietmapnavigation.markers;

import android.view.View;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.mapbox.geojson.Point;

/**
 * ViewManager for {@link VietMapMarkerView}. The hosted child is captured and handed to the
 * markerview plugin instead of being parented to the ReactViewGroup, so all child-management
 * overrides report an empty group to React Native.
 */
public class VietMapMarkerViewManager extends ViewGroupManager<VietMapMarkerView> {
    public static final String REACT_CLASS = "VietMapMarkerView";

    @NonNull
    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @NonNull
    @Override
    protected VietMapMarkerView createViewInstance(@NonNull ThemedReactContext reactContext) {
        return new VietMapMarkerView(reactContext);
    }

    @ReactProp(name = "markerId")
    public void setMarkerId(VietMapMarkerView view, String id) {
        view.setMarkerId(id);
    }

    @ReactProp(name = "coordinate")
    public void setCoordinate(VietMapMarkerView view, ReadableArray coordinate) {
        // coordinate is [longitude, latitude]
        if (coordinate != null && coordinate.size() >= 2) {
            view.setCoordinate(Point.fromLngLat(coordinate.getDouble(0), coordinate.getDouble(1)));
        }
    }

    @ReactProp(name = "anchor")
    public void setAnchor(VietMapMarkerView view, ReadableMap anchor) {
        if (anchor != null && anchor.hasKey("x") && anchor.hasKey("y")) {
            view.setAnchor((float) anchor.getDouble("x"), (float) anchor.getDouble("y"));
        }
    }

    @Override
    public void addView(VietMapMarkerView parent, View child, int index) {
        parent.setMarkerChild(child);
    }

    @Override
    public int getChildCount(VietMapMarkerView parent) {
        return 0;
    }

    @Override
    public View getChildAt(VietMapMarkerView parent, int index) {
        return null;
    }

    @Override
    public void removeViewAt(VietMapMarkerView parent, int index) {
        parent.removeMarkerChild();
    }

    @Override
    public void onDropViewInstance(@NonNull VietMapMarkerView view) {
        view.removeFromMap();
        super.onDropViewInstance(view);
    }
}
