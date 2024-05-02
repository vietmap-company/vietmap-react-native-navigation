package vn.vietmap.vietmapnavigation

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.common.MapBuilder
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp
import vn.vietmap.vietmapsdk.Vietmap
//import vn.vietmap.maps.ResourceOptionsManager
//import com.mapbox.maps.TileStoreUsageMode
import javax.annotation.Nonnull

class VietMapNavigationManager(mCallerContext: ReactApplicationContext) :
    SimpleViewManager<VietMapNavigationView>() {
    init {
        mCallerContext.runOnUiQueueThread {
        }
    }

    override fun getName(): String = "VietMapNavigation"

    public override fun createViewInstance(@Nonnull reactContext: ThemedReactContext): VietMapNavigationView {
        Vietmap.getInstance(reactContext)
        VietMapNavigationView.instance = VietMapNavigationView(reactContext)
        return VietMapNavigationView.instance!!
    }

    override fun onDropViewInstance(view: VietMapNavigationView) {
        view.onDropViewInstance()
        super.onDropViewInstance(view)
    }

    override fun getExportedCustomDirectEventTypeConstants(): MutableMap<String, Map<String, String>>? {
        return mutableMapOf<String, Map<String, String>>(
            "onLocationChange" to MapBuilder.of("registrationName", "onLocationChange"),
            "onError" to MapBuilder.of("registrationName", "onError"),
            "onCancelNavigation" to MapBuilder.of("registrationName", "onCancelNavigation"),
            "onArrive" to MapBuilder.of("registrationName", "onArrive"),
            "onMapReady" to MapBuilder.of("registrationName", "onMapReady"),
            "onRouteBuilding" to MapBuilder.of("registrationName", "onRouteBuilding"),
            "onRouteBuilt" to MapBuilder.of("registrationName", "onRouteBuilt"),
            "onRouteBuildFailed" to MapBuilder.of("registrationName", "onRouteBuildFailed"),
            "routeBuildCancelled" to MapBuilder.of("registrationName", "routeBuildCancelled"),
            "routeBuildNoRoutesFound" to MapBuilder.of("registrationName", "routeBuildNoRoutesFound"),
            "onRouteProgressChange" to MapBuilder.of("registrationName", "onRouteProgressChange"),
            "userOffRoute" to MapBuilder.of("registrationName", "userOffRoute"),
            "onMilestoneEvent" to MapBuilder.of("registrationName", "onMilestoneEvent"),
            "onNavigationRunning" to MapBuilder.of("registrationName", "onNavigationRunning"),
            "onNavigationCancelled" to MapBuilder.of("registrationName", "onNavigationCancelled"),
            "onNavigationFinished" to MapBuilder.of("registrationName", "onNavigationFinished"),
            "fasterRouteFound" to MapBuilder.of("registrationName", "fasterRouteFound"),
            "speechAnnouncement" to MapBuilder.of("registrationName", "speechAnnouncement"),
            "bannerInstruction" to MapBuilder.of("registrationName", "bannerInstruction"),
            "onArrival" to MapBuilder.of("registrationName", "onArrival"),
            "failedToReroute" to MapBuilder.of("registrationName", "failedToReroute"),
            "rerouteAlong" to MapBuilder.of("registrationName", "rerouteAlong"),
            "onMapMove" to MapBuilder.of("registrationName", "onMapMove"),
            "onMapMoveEnd" to MapBuilder.of("registrationName", "onMapMoveEnd"),
            "onMapLongClick" to MapBuilder.of("registrationName", "onMapLongClick"),
            "onMapClick" to MapBuilder.of("registrationName", "onMapClick"),
            "onMapRendered" to MapBuilder.of("registrationName", "onMapRendered"),
            "onNewRouteSelected" to MapBuilder.of("registrationName", "onNewRouteSelected"),
            "cameraOnMove" to MapBuilder.of("registrationName", "cameraOnMove"),
            "markerClicked" to MapBuilder.of("registrationName", "markerClicked")
        )
    }

    @ReactProp(name = "apiKey")
    fun setApiKey(view: VietMapNavigationView, sources: String?) {
        if (sources == null) {
            view.setApiKey("")
            return
        }
        print("Set api key "+sources);
        view.setApiKey(sources)
    }


    @ReactProp(name = "navigationZoomLevel")
    fun setNavigationZoomLevel(view: VietMapNavigationView, zoomLevel: Double) {
        view.setNavigationZoomLevel(zoomLevel)
    }

    @ReactProp(name = "initialLatLngZoom")
    fun setInitialLatLngZoom(view: VietMapNavigationView, initialLatLng: ReadableMap?) {
        if(initialLatLng==null) {
            view.setInitialLatLngZoom(initialLatLng!!)
        }
    }
    @ReactProp(name = "navigationTiltLevel")
    fun setNavigationTiltAnchor(view: VietMapNavigationView, tiltAnchor: Double) {
        view.setNavigationTiltAnchor(tiltAnchor)
    }
    @ReactProp(name = "navigationPadding")
    fun setNavigationPadding(view: VietMapNavigationView, padding: ReadableMap) {
        view.setNavigationPadding(padding)
    }
    @ReactProp(name = "shouldSimulateRoute")
    fun setShouldSimulateRoute(view: VietMapNavigationView, shouldSimulateRoute: Boolean) {
        view.setShouldSimulateRoute(shouldSimulateRoute)
    }


}