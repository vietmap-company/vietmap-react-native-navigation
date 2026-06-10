package vn.vietmap.vietmapnavigation.markers;

import androidx.annotation.NonNull;

import vn.vietmap.vietmapsdk.maps.MapView;
import vn.vietmap.vietmapsdk.maps.VietMapGL;

import java.util.ArrayList;
import java.util.List;

import java.lang.reflect.InvocationTargetException;

/**
 * Subclass of the markerview plugin's MarkerViewManager that also tracks the markers it owns so
 * their child views can be removed/restored when the map style reloads. Ported from
 * vietmap-gl-react-native.
 */
public class MarkerViewManager extends com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager {
    private final List<MarkerView> markers = new ArrayList<>();
    private final MapView mapView;
    private java.lang.reflect.Method markerUpdate;

    public MarkerViewManager(MapView mapView, VietMapGL map) {
        super(mapView, map);
        this.mapView = mapView;

        try {
            markerUpdate = MarkerView.class.getSuperclass().getDeclaredMethod("update");
            markerUpdate.setAccessible(true);
        } catch (NoSuchMethodException e) {
            System.out.println(e.toString());
        }
    }

    public void addMarker(@NonNull MarkerView markerView) {
        super.addMarker(markerView);
        markers.add(markerView);
    }

    public void removeMarker(@NonNull MarkerView markerView) {
        super.removeMarker(markerView);
        markers.remove(markerView);
    }

    public void removeViews() {
        for (MarkerView marker : markers) {
            mapView.removeView(marker.getView());
        }
    }

    public void restoreViews() {
        for (MarkerView marker : markers) {
            mapView.addView(marker.getView());
        }
    }

    public void updateMarkers() {
        try {
            if (markerUpdate != null) {
                for (int i = 0; i < markers.size(); i++) {
                    markerUpdate.invoke(markers.get(i));
                }
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.toString());
        } catch (IllegalAccessException e) {
            System.out.println(e.toString());
        } catch (InvocationTargetException e) {
            System.out.println(e.toString());
        }
    }
}
