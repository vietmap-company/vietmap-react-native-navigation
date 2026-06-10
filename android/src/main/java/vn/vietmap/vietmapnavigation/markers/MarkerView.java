package vn.vietmap.vietmapnavigation.markers;

import android.view.View;

import androidx.annotation.NonNull;

import vn.vietmap.vietmapsdk.geometry.LatLng;

/**
 * Thin subclass of the markerview plugin's MarkerView so the manager can reach the hosted
 * child View (for remove/restore when the style reloads). Ported from vietmap-gl-react-native.
 */
public class MarkerView extends com.mapbox.mapboxsdk.plugins.markerview.MarkerView {
    View view;

    public MarkerView(@NonNull LatLng latLng, @NonNull View view) {
        super(latLng, view);
        this.view = view;
    }

    public View getView() {
        return this.view;
    }
}
