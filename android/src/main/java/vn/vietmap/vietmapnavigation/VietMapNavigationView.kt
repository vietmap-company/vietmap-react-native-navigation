package vn.vietmap.vietmapnavigation

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.location.Location
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.UIManagerHelper
import vn.vietmap.utilities.VietMapEvent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.mapbox.api.directions.v5.models.BannerInstructions
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.DirectionsRoute
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.Point
import com.mapbox.turf.TurfMisc
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import vn.vietmap.android.gestures.MoveGestureDetector
import vn.vietmap.model.CurrentCenterPoint
import vn.vietmap.model.VietMapEvents
import vn.vietmap.model.VietMapLocation
import vn.vietmap.model.VietMapRouteProgressEvent
import vn.vietmap.services.android.navigation.ui.v5.ThemeSwitcher
import vn.vietmap.services.android.navigation.ui.v5.camera.CameraOverviewCancelableCallback
import vn.vietmap.services.android.navigation.ui.v5.listeners.BannerInstructionsListener
import vn.vietmap.services.android.navigation.ui.v5.listeners.NavigationListener
import vn.vietmap.services.android.navigation.ui.v5.listeners.SpeechAnnouncementListener
import vn.vietmap.services.android.navigation.ui.v5.route.NavigationMapRoute
import vn.vietmap.services.android.navigation.ui.v5.voice.NavigationSpeechPlayer
import vn.vietmap.services.android.navigation.ui.v5.voice.SpeechAnnouncement
import vn.vietmap.services.android.navigation.ui.v5.voice.SpeechPlayer
import vn.vietmap.services.android.navigation.ui.v5.voice.SpeechPlayerProvider
import vn.vietmap.services.android.navigation.v5.location.engine.LocationEngineProvider
import vn.vietmap.services.android.navigation.v5.location.replay.ReplayRouteLocationEngine
import vn.vietmap.services.android.navigation.v5.milestone.Milestone
import vn.vietmap.services.android.navigation.v5.milestone.MilestoneEventListener
import vn.vietmap.services.android.navigation.v5.milestone.VoiceInstructionMilestone
import vn.vietmap.services.android.navigation.v5.navigation.NavigationConstants
import vn.vietmap.services.android.navigation.v5.navigation.NavigationEventListener
import vn.vietmap.services.android.navigation.v5.navigation.NavigationRoute
import vn.vietmap.services.android.navigation.v5.navigation.NavigationTimeFormat
import vn.vietmap.services.android.navigation.v5.navigation.VietmapNavigation
import vn.vietmap.services.android.navigation.v5.navigation.VietmapNavigationOptions
import vn.vietmap.services.android.navigation.v5.navigation.camera.RouteInformation
import vn.vietmap.services.android.navigation.v5.offroute.OffRouteListener
import vn.vietmap.services.android.navigation.v5.route.FasterRouteListener
import vn.vietmap.services.android.navigation.v5.route.RouteListener
import vn.vietmap.services.android.navigation.v5.routeprogress.ProgressChangeListener
import vn.vietmap.services.android.navigation.v5.routeprogress.RouteProgress
import vn.vietmap.services.android.navigation.v5.snap.SnapToRoute
import vn.vietmap.services.android.navigation.v5.utils.RouteUtils
import vn.vietmap.utilities.JSONConverter
import vn.vietmap.vietmapnavigation.databinding.NavigationViewBinding
import vn.vietmap.vietmapsdk.Vietmap
import vn.vietmap.vietmapsdk.camera.CameraPosition
import vn.vietmap.vietmapsdk.camera.CameraUpdate
import vn.vietmap.vietmapsdk.camera.CameraUpdateFactory
import vn.vietmap.vietmapsdk.camera.CameraUpdateFactory.newLatLngBounds
import vn.vietmap.vietmapsdk.geometry.LatLng
import vn.vietmap.vietmapsdk.geometry.LatLngBounds
import vn.vietmap.vietmapsdk.location.LocationComponentActivationOptions
import vn.vietmap.vietmapsdk.location.LocationComponentOptions
import vn.vietmap.vietmapsdk.location.engine.LocationEngine
import vn.vietmap.vietmapsdk.location.modes.CameraMode
import vn.vietmap.vietmapsdk.location.modes.RenderMode
import vn.vietmap.vietmapsdk.maps.MapView
import vn.vietmap.vietmapsdk.maps.Style
import vn.vietmap.vietmapsdk.maps.VietMapGL
import vn.vietmap.vietmapsdk.style.layers.LineLayer
import vn.vietmap.vietmapsdk.style.layers.Property.LINE_CAP_ROUND
import vn.vietmap.vietmapsdk.style.layers.Property.LINE_JOIN_ROUND
import vn.vietmap.vietmapsdk.style.layers.PropertyFactory.iconAllowOverlap
import vn.vietmap.vietmapsdk.style.layers.PropertyFactory.iconIgnorePlacement
import vn.vietmap.vietmapsdk.style.layers.PropertyFactory.iconImage
import vn.vietmap.vietmapsdk.style.layers.PropertyFactory.lineCap
import vn.vietmap.vietmapsdk.style.layers.PropertyFactory.lineColor
import vn.vietmap.vietmapsdk.style.layers.PropertyFactory.lineJoin
import vn.vietmap.vietmapsdk.style.layers.PropertyFactory.lineWidth
import vn.vietmap.vietmapsdk.style.layers.SymbolLayer
import vn.vietmap.vietmapsdk.style.sources.GeoJsonSource
import com.vietmap.trackingsdk.VietmapTrackingSDK
import com.vietmap.trackingsdk.VehicleType
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt
import androidx.core.graphics.scale
import androidx.core.graphics.createBitmap


class VietMapNavigationView(
    private val context: ThemedReactContext,
) :
    FrameLayout(context), ProgressChangeListener,
    OffRouteListener, MilestoneEventListener, NavigationEventListener, NavigationListener,
    FasterRouteListener, SpeechAnnouncementListener, BannerInstructionsListener, RouteListener,
    VietMapGL.OnMapClickListener,
    MapView.OnDidFinishRenderingMapListener, VietMapGL.OnMapLongClickListener {
    private var currentRoute: DirectionsRoute? = null
    private var routeClicked: Boolean = false
    private var locationEngine: LocationEngine? = null
    private var navigationMapRoute: NavigationMapRoute? = null
    private var belowLayerId: String? = null
    private var markerViewManager: vn.vietmap.vietmapnavigation.markers.MarkerViewManager? = null
    private var directionsRoutes: List<DirectionsRoute>? = null
    private var distanceToOffRoute =
        50 //distance in meter (matches Flutter; 30 was too trigger-happy)
    private val navigationOptions =
        VietmapNavigationOptions.builder().maxTurnCompletionOffset(30.0).maneuverZoneRadius(40.0)
            .maximumDistanceOffRoute(50.0).deadReckoningTimeInterval(5.0)
            .maxManipulatedCourseAngle(25.0).userLocationSnapDistance(20.0).secondsBeforeReroute(3)
            .enableOffRouteDetection(true).enableFasterRouteDetection(false).snapToRoute(false)
            .manuallyEndNavigationUponCompletion(false).defaultMilestonesEnabled(true)
            .minimumDistanceBeforeRerouting(10.0).metersRemainingTillArrival(20.0)
            .isFromNavigationUi(false).isDebugLoggingEnabled(false)
            .roundingIncrement(NavigationConstants.ROUNDING_INCREMENT_FIFTY)
            .timeFormatType(NavigationTimeFormat.NONE_SPECIFIED)
            .locationAcceptableAccuracyInMetersThreshold(100).build()
    private var navigation: VietmapNavigation? = null
    private var onMapReady = false
    private var isDisposed = false
    private var isRefreshing = false
    private var isBuildingRoute = false
    private var isNavigationInProgress = false
    private var isNavigationCanceled = false
    private var isOverviewing = false
    private var isNextTurnHandling = false
    private var routeUtils = RouteUtils()
    private val snapEngine = SnapToRoute()
    private var apikey: String? = null

    // True once a custom styleUrl prop has been supplied, so setApiKey stops overwriting mapStyleURL.
    private var hasCustomStyleUrl: Boolean = false
    private var apiKeyAlert: String? = null
    private var apiIDAlert: String? = null
    private var customPuckImageUri: String? = null
    private var currentStyle: Style? = null
    private var puckImageWidth: Int = 0
    private var puckImageHeight: Int = 0
    private var puckImageRotation: Float = 0f

    // Vehicle configuration variables
    private var pendingVehicleId: String? = null
    private var pendingVehicleType: Int? = null
    private var pendingVehicleSeats: Int? = null
    private var pendingVehicleWeight: Double? = null

    private var baseUrl: String = "https://maps.vietmap.vn/api/navigations/route/"
    private var speechPlayer: SpeechPlayer? = null
    private var routeProgress: RouteProgress? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var primaryRouteIndex = 0
    private var padding: IntArray = intArrayOf(50, 50, 50, 50)
    private var profile: String = "driving-traffic"

    private var navigationZoomLevel = 18.0
    private var bearing = 0.0
    private var tilt = 0.0
    private var currentCenterPoint: CurrentCenterPoint? = null

    // --- Off-route tracking (ported from Flutter FlutterMapViewFactory) ---
    private var firstOffRoutePoint: Location? = null
    private var isUserCurrentlyOffRoute = false
    private var consecutiveOnRouteCount = 0

    // --- Multi-waypoint arrival, distance-based & single-fire (ported from Flutter) ---
    private var completedWaypointIndex = 0
    private var pendingArrivalPoint: Point? = null
    private var lastArrivedLegIndex = -1

    // --- GPS smoothing + snapped/raw blend (ported from Flutter GpsTracker, minus stuck/wrong-way) ---
    private val kalmanLat = GpsKalmanFilter()
    private val kalmanLng = GpsKalmanFilter()
    private val kalmanBearing = GpsKalmanFilter(0.001, 0.01)
    private var bearingDivergenceCount = 0
    private var isBearingDiverging = false

    private var shouldSimulateRoute = false
    private var arrivalIndex = 0

    /**
     * Bindings to the example layout.
     */
    private var binding: NavigationViewBinding =
        NavigationViewBinding.inflate(LayoutInflater.from(context), this, true)

    private lateinit var vietMapGL: VietMapGL
    private lateinit var vietmapSDK: VietmapTrackingSDK

    private var restartAlert = false


    override fun onAttachedToWindow() {
        Vietmap.getInstance(context)
        super.onAttachedToWindow()
        onCreate()
    }

    override fun requestLayout() {
        super.requestLayout()

        post(measureAndLayout)
    }

    private val measureAndLayout = Runnable {
        measure(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        )
        layout(left, top, right, bottom)
    }

    companion object {
        private const val TAG = "VietMapNavigationView"

        // --- Navigation tuning constants (ported from Flutter models/Constant.kt) ---
        // Within this distance the puck stays 100% snapped; beyond it we blend toward raw GPS.
        private const val OFF_ROUTE_SNAP_START_DISTANCE = 10.0
        // Bearing divergence (raw vs snapped) above this for N ticks → treat as diverging (blend toward raw).
        private const val BEARING_DIVERGENCE_THRESHOLD = 60.0
        private const val BEARING_DIVERGENCE_CONSECUTIVE_COUNT = 4
        // Skip divergence detection when GPS accuracy is worse than this (metres) — too noisy to trust.
        private const val WRONG_WAY_MAX_ACCURACY = 15.0f
        // Consecutive on-route ticks needed to clear an active off-route state.
        private const val REQUIRED_ON_ROUTE_COUNT = 5
        // Fire arrival when within this many metres of the arrive maneuver.
        private const val ARRIVAL_RADIUS = 15.0

        var instance: VietMapNavigationView? = null

        //Config
        var initialLatitude: Double? = null
        var initialLongitude: Double? = null
        var mapZoomLevel: Double = 15.0
        var mapStyleURL: String? =
            "https://maps.vietmap.vn/api/maps/light/styles.json?apikey=YOUR_API_KEY_HERE"

        var distanceRemaining: Double? = null
        var durationRemaining: Double? = null
        var voiceInstructionsEnabled = true
        var bannerInstructionsEnabled = true
        var longPressDestinationEnabled = true
        var animateBuildRoute = true

        //        var originPoint: Point? = null
//        var destinationPoint: Point? = null
        var listNavigationRemainingPoints: MutableList<Point> = mutableListOf()
        var isRunning: Boolean = false
    }

    @SuppressLint("MissingPermission")
    fun onCreate() {

        fusedLocationClient = context.currentActivity?.let {
            LocationServices.getFusedLocationProviderClient(
                it
            )
        }
        locationEngine = if (shouldSimulateRoute) {
            ReplayRouteLocationEngine()

        } else {
            LocationEngineProvider.getBestLocationEngine(context)
        }

        context.currentActivity?.let {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                val permissionState =
                    ContextCompat.checkSelfPermission(it, Manifest.permission.POST_NOTIFICATIONS)

                if (permissionState == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(
                        it,
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        1
                    )
                }
            }
        }

        binding.mapView.getMapAsync {
            this.onMapReady = true
            this.vietMapGL = it
//            context.currentActivity?.runOnUiThread {
//                this.vietMapGL.locationComponent.cameraMode = CameraMode.TRACKING_GPS
//
//            }
            if (shouldSimulateRoute) {
                locationEngine = ReplayRouteLocationEngine()
            }

            vietMapGL.setStyle(mapStyleURL) { style ->
                addDestinationIconSymbolLayer(style)
                val routeLineLayer = LineLayer("line-layer-id", "source-id")
                routeLineLayer.setProperties(
                    lineWidth(9f),
                    lineColor(Color.RED),
                    lineCap(LINE_CAP_ROUND),
                    lineJoin(LINE_JOIN_ROUND)
                )
                style.addLayer(routeLineLayer)
                vietMapGL.addOnMoveListener(object : VietMapGL.OnMoveListener {
                    override fun onMoveBegin(moveGestureDetector: MoveGestureDetector) {
                        isOverviewing = true
                        sendEvent(VietMapEvents.ON_MAP_MOVE)
                    }

                    override fun onMove(moveGestureDetector: MoveGestureDetector) {}
                    override fun onMoveEnd(moveGestureDetector: MoveGestureDetector) {
                        sendEvent(VietMapEvents.ON_MAP_MOVE_END)
                    }
                })
                enableLocationComponent(style)
                initMapRoute()
                configSpeechPlayer()
            }

            if (longPressDestinationEnabled) vietMapGL.addOnMapLongClickListener(this)



            if (initialLatitude != null && initialLongitude != null) {
                moveCamera(
                    LatLng(initialLatitude!!, initialLongitude!!),
                    null,
                    zoomLevel = mapZoomLevel
                )
            }
            sendEvent(VietMapEvents.MAP_READY)
        }
        // Initialize SDK sau khi map ready
        initializeSDK()
    }

    private fun initializeSDK() {
        // Initialize SDK
        vietmapSDK = VietmapTrackingSDK.getInstance(context)
        vietmapSDK.initialize("VIETMAP_ALERT")

        // Gọi configureAlertAPI nếu apiKeyAlert và apiIDAlert đã được set trước đó
        configureAlertAPI()

        // Gọi configureVehicle nếu vehicle info đã được set trước đó
        configureVehicle()
    }

    private fun startRoute(points: ReadableArray, profile: String) {
        // Create a list of coordinates that includes origin, destination, and waypoints
        val coordinatesList = mutableListOf<Point>()
//        coordinatesList.add(
//            Point.fromLngLat(
//                points.getMap(0).getDouble("long"),
//                points.getMap(0).getDouble("lat")
//            )
//        )
//        coordinatesList.add(
//            Point.fromLngLat(
//                points.getMap(1).getDouble("long"),
//                points.getMap(1).getDouble("lat")
//            )
//        )
        for (i in 0 until points.size()) {
            coordinatesList.add(
                Point.fromLngLat(
                    points.getMap(i)?.getDouble("long") ?: 0.0,
                    points.getMap(i)?.getDouble("lat") ?: 0.0
                )
            )
        }

        listNavigationRemainingPoints = coordinatesList
        findRoute(coordinatesList, profile)
    }

    private fun onDestroy() {
//        VietMapNavigationProvider.destroy()
//        mapboxReplayer.finish()
//        maneuverApi.cancel()
//        routeLineApi.cancel()
//        routeLineView.cancel()
//        speechApi.cancel()
//        voiceInstructionsPlayer.shutdown()
    }

    private fun findRoute(coordinates: List<Point>, profile: String?) {
        try {
            val br = bearing
            Log.d("VietMapNavigation", "getroute")
            sendEvent(VietMapEvents.ROUTE_BUILDING)
            val build = NavigationRoute.builder(context)
                .baseUrl(baseUrl)
                .apikey(apikey ?: "")

                .origin(coordinates.first(), 60.0, br)

                .destination(coordinates.last())
                .alternatives(true)
                ///driving-traffic
                ///cycling
                ///walking
                ///motorcycle
                .profile(profile ?: "driving-traffic")

            for (i in 1 until coordinates.size - 1) {
                build.addWaypoint(coordinates[i])
            }

            val builder = build.build()

            listNavigationRemainingPoints.removeAt(0)
            builder.getRoute(object : Callback<DirectionsResponse> {
                override fun onResponse(
                    call: Call<DirectionsResponse?>, response: Response<DirectionsResponse?>,
                ) {
                    if (response.body() == null || response.body()!!.routes().size < 1) {
                        sendEvent(VietMapEvents.ROUTE_BUILD_FAILED)
                        return
                    }
                    directionsRoutes = response.body()!!.routes()
                    currentRoute = if (directionsRoutes!!.size <= primaryRouteIndex) {
                        directionsRoutes!![0]
                    } else {
                        directionsRoutes!![primaryRouteIndex]
                    }

                    sendEvent(
                        VietMapEvents.ROUTE_BUILT,
                        currentRoute?.toJson()?.let { JSONObject(it) })

                    // Draw the route on the map
                    if (navigationMapRoute != null) {
                        navigationMapRoute?.removeRoute()
                    } else {
                        val routeStyleRes = ThemeSwitcher.retrieveNavigationViewStyle(
                            binding.mapView!!.context,
                            vn.vietmap.services.android.navigation.R.style.NavigationMapRoute
                        )

                        belowLayerId = vietMapGL?.style?.let { findFirstSymbolLayerId(it) }
                        navigationMapRoute =
                            NavigationMapRoute(
                                navigation,
                                binding.mapView!!,
                                vietMapGL!!,
                                routeStyleRes,
                                belowLayerId
                            )
                    }

                    //show multiple route to map
                    if (response.body()!!.routes().size > 1) {
                        navigationMapRoute?.addRoutes(directionsRoutes!!)
                    } else {
                        navigationMapRoute?.addRoute(currentRoute)
                    }


                    isBuildingRoute = false
                    // get route point from current route
                    val routePoints: List<Point> =
                        currentRoute?.routeOptions()?.coordinates() as List<Point>
                    animateVietmapGLForRouteOverview(padding, routePoints)
                    //Start Navigation again from new Point, if it was already in Progress
//                    if (isNavigationInProgress || isStartNavigation) {
//                        startNavigation()
//                    }
                }

                override fun onFailure(call: Call<DirectionsResponse?>, throwable: Throwable) {
                    isBuildingRoute = false
                    sendEvent(
                        VietMapEvents.ROUTE_BUILD_FAILED
                    )

                    //TODO: Handle send event here
                }
            })
        } catch (ex: Exception) {
            sendErrorToReact(ex.toString())
        }

    }

    private fun sendErrorToReact(error: String?) {
        val event = Arguments.createMap()
        event.putString("error", error)
        val surfaceId = UIManagerHelper.getSurfaceId(this)
        UIManagerHelper.getEventDispatcherForReactTag(context, id)
            ?.dispatchEvent(VietMapEvent(surfaceId, id, "onError", event))
    }

    fun onDropViewInstance() {
        this.onDestroy()
    }

    fun setApiKey(apiKey: String) {
        this.apikey = apiKey
        // apiKey is the navigation/routing token. Only derive the tilemap style URL from it as a
        // fallback when the app hasn't supplied an explicit styleUrl (supports separate tilemap key).
        if (!hasCustomStyleUrl) {
            mapStyleURL = "https://maps.vietmap.vn/api/maps/light/styles.json?apikey=$apiKey"
        }
    }

    fun setStyleUrl(styleUrl: String) {
        hasCustomStyleUrl = true
        mapStyleURL = styleUrl
    }

    fun setApiKeyAlert(apiKeyAlert: String) {
        this.apiKeyAlert = apiKeyAlert
        configureAlertAPI()
    }

    fun setApiIDAlert(apiIDAlert: String) {
        this.apiIDAlert = apiIDAlert
        configureAlertAPI()
    }

    private fun configureAlertAPI() {
        if (::vietmapSDK.isInitialized && apiKeyAlert != null && apiIDAlert != null) {
            try {
                vietmapSDK.configureAlertAPI(apiKeyAlert!!, apiIDAlert!!)
            } catch (e: Exception) {
                Log.e("VietmapSpeedAlert", "Error configuring Alert API", e)
            }
        } else {
            Log.d(
                "VietmapSpeedAlert",
                "Cannot configure Alert API - SDK not initialized or missing credentials"
            )
        }
    }

    private fun configureVehicle() {
        if (::vietmapSDK.isInitialized &&
            pendingVehicleId != null &&
            pendingVehicleType != null &&
            pendingVehicleSeats != null &&
            pendingVehicleWeight != null
        ) {
            try {
                val vehicleTypeEnum = VehicleType.fromValue(pendingVehicleType!!)
                vietmapSDK.configureVehicle(
                    pendingVehicleId!!,
                    vehicleTypeEnum,
                    pendingVehicleSeats!!,
                    pendingVehicleWeight!!
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error configuring vehicle", e)
            }
        } else {
            Log.d(TAG, "Cannot configure vehicle - SDK not initialized or missing vehicle data")
        }
    }

    fun setMute(mute: Boolean) {
//        this.padding = padding
    }

    fun setInitialLatLngZoom(initialLatLng: ReadableMap) {
        initialLatitude = initialLatLng.getDouble("lat")
        initialLongitude = initialLatLng.getDouble("lng")
        mapZoomLevel = initialLatLng.getDouble("zoom")

    }

    fun setNavigationZoomLevel(zoomLevel: Double) {
        this.navigationZoomLevel = zoomLevel
    }

    fun setNavigationTiltAnchor(tiltAnchor: Double) {
        this.tilt = tiltAnchor
    }

    fun setNavigationPadding(padding: ReadableMap) {
        this.padding =
            intArrayOf(
                padding.getInt("left"),
                padding.getInt("top"),
                padding.getInt("right"),
                padding.getInt("bottom")
            )
    }

    fun setShouldSimulateRoute(shouldSimulateRoute: Boolean) {
        this.shouldSimulateRoute = shouldSimulateRoute
    }


//    override fun onMapReady(p0: VietMapGL) {
//        this.onMapReady = true
//        this.vietMapGL = p0
//        if (shouldSimulateRoute) {
//            locationEngine = ReplayRouteLocationEngine()
//        }
//
//        vietMapGL.setStyle(mapStyleURL) { style ->
//            addDestinationIconSymbolLayer(style)
//            val routeLineLayer = LineLayer("line-layer-id", "source-id")
//            routeLineLayer.setProperties(
//                lineWidth(9f),
//                lineColor(Color.RED),
//                lineCap(LINE_CAP_ROUND),
//                lineJoin(LINE_JOIN_ROUND)
//            )
//            style.addLayer(routeLineLayer)
//            vietMapGL.addOnMoveListener(object : VietMapGL.OnMoveListener {
//                override fun onMoveBegin(moveGestureDetector: MoveGestureDetector) {
//                    isOverviewing = true
//                    sendEvent(VietMapEvents.ON_MAP_MOVE)
//                }
//
//                override fun onMove(moveGestureDetector: MoveGestureDetector) {}
//                override fun onMoveEnd(moveGestureDetector: MoveGestureDetector) {
//                    sendEvent(VietMapEvents.ON_MAP_MOVE_END)
//                }
//            })
//            enableLocationComponent(style)
//            initMapRoute()
//        }
//        if (longPressDestinationEnabled) vietMapGL.addOnMapLongClickListener(this)
//        if (initialLatitude != null && initialLongitude != null) {
//            moveCamera(
//
//                LatLng(
//                    initialLatitude!!,
//                    initialLongitude!!
//                ), null, mapZoomLevel
//            )
//        }
//        sendEvent(VietMapEvents.MAP_READY)
//    }

    private fun initMapRoute() {
        val routeStyleRes = ThemeSwitcher.retrieveNavigationViewStyle(
            binding.mapView!!.context,
            vn.vietmap.services.android.navigation.R.style.NavigationMapRoute
        )

        belowLayerId = vietMapGL?.style?.let { findFirstSymbolLayerId(it) }
        navigationMapRoute =
            NavigationMapRoute(
                navigation,
                binding.mapView!!,
                vietMapGL!!,
                routeStyleRes,
                belowLayerId
            )

        navigationMapRoute?.setOnRouteSelectionChangeListener {
            routeClicked = true

            currentRoute = it

            val routePoints: List<Point> =
                currentRoute?.routeOptions()?.coordinates() as List<Point>
            animateVietmapGLForRouteOverview(padding, routePoints)
            primaryRouteIndex = try {
                it.routeIndex()?.toInt() ?: 0
            } catch (e: Exception) {
                0
            }
            if (isRunning) {
                finishNavigation(isOffRouted = true)
                startNavigation()
            }
            sendEvent(
                VietMapEvents.ON_NEW_ROUTE_SELECTED, JSONObject(it.toJson())
            )
            //TODO: Handle send event here
        }

        vietMapGL.addOnMapClickListener(this)

    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
        currentStyle = loadedMapStyle
        val customLocationComponentOptions =
            LocationComponentOptions.builder(context).pulseEnabled(true)
                .build()
        vietMapGL.locationComponent.let { locationComponent ->
            locationComponent.activateLocationComponent(
                LocationComponentActivationOptions.builder(context, loadedMapStyle)
                    .locationComponentOptions(customLocationComponentOptions)
                    .locationEngine(locationEngine).build()
            )

            locationComponent.locationEngine = locationEngine
            if (locationEngine is ReplayRouteLocationEngine) {

                if (initialLatitude != null && initialLongitude != null) {
                    (locationEngine as ReplayRouteLocationEngine).assignLastLocation(
                        Point.fromLngLat(
                            initialLongitude!!, initialLatitude!!
                        )
                    )
                }
                locationComponent.setCameraMode(
                    CameraMode.TRACKING_GPS_NORTH,
                    750L,
                    navigationZoomLevel,
                    0.0,
                    tilt,
                    null
                )
            } else {
                locationComponent.setCameraMode(
                    CameraMode.TRACKING_GPS_NORTH,
                    750L,
                    mapZoomLevel,
                    locationComponent.lastKnownLocation?.bearing?.toDouble() ?: 0.0,
                    tilt,
                    null
                )
            }
            locationComponent.zoomWhileTracking(mapZoomLevel)
            locationComponent.renderMode = RenderMode.GPS

            locationComponent.isLocationComponentEnabled = true
        }
        applyCustomPuckImage(loadedMapStyle)
    }

    fun setPuckImage(uri: String) {
        customPuckImageUri = uri
        currentStyle?.let { applyCustomPuckImage(it) }
    }

    fun setPuckImageWidth(width: Double) {
        puckImageWidth = width.toInt()
        currentStyle?.let { applyCustomPuckImage(it) }
    }

    fun setPuckImageHeight(height: Double) {
        puckImageHeight = height.toInt()
        currentStyle?.let { applyCustomPuckImage(it) }
    }

    fun setPuckImageRotation(rotation: Double) {
        puckImageRotation = rotation.toFloat()
        currentStyle?.let { applyCustomPuckImage(it) }
    }

    private fun applyCustomPuckImage(style: Style) {
        val uri = customPuckImageUri ?: return
        Thread {
            try {
                val bitmap: Bitmap? = when {
                    // RN base64 data URI
                    uri.startsWith("data:image") -> {
                        val base64 = uri.substringAfter(",")
                        val bytes = android.util.Base64.decode(base64, android.util.Base64.DEFAULT)
                        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    }
                    // iOS-style/local file path (rare on Android, kept for safety)
                    uri.startsWith("file://") -> {
                        val path = java.net.URI(uri).path
                        BitmapFactory.decodeFile(path)
                    }
                    // Dev (Metro packager serves require'd assets over http) & remote URLs
                    uri.startsWith("http://") || uri.startsWith("https://") -> {
                        val url = java.net.URL(uri)
                        val connection = url.openConnection()
                        connection.connectTimeout = 5000
                        connection.readTimeout = 5000
                        connection.connect()
                        BitmapFactory.decodeStream(connection.getInputStream())
                    }
                    // Release: resolveAssetSource() returns a bare drawable resource name
                    // (the require'd image is compiled into res/drawable by the bundler).
                    else -> loadDrawableBitmap(uri)
                }

                if (bitmap == null) {
                    Log.e(
                        TAG,
                        "Puck image could not be decoded from URI: \"$uri\". Falling back to default navigation icon."
                    )
                    return@Thread
                }

                val scaledBitmap = if (puckImageWidth > 0 && puckImageHeight > 0) {
                    bitmap.scale(puckImageWidth, puckImageHeight)
                } else bitmap
                val finalBitmap = if (puckImageRotation != 0f) {
                    val result = createBitmap(scaledBitmap.width, scaledBitmap.height)
                    val matrix = android.graphics.Matrix()
                    matrix.postRotate(
                        puckImageRotation,
                        scaledBitmap.width / 2f,
                        scaledBitmap.height / 2f
                    )
                    Canvas(result).drawBitmap(scaledBitmap, matrix, null)
                    result
                } else scaledBitmap

                post {
                    if (::vietMapGL.isInitialized) {
                        style.addImage("custom-puck-icon", finalBitmap)
                        vietMapGL.locationComponent.applyStyle(
                            LocationComponentOptions.builder(context)
                                .pulseEnabled(true)
                                .gpsName("custom-puck-icon")
                                .build()
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Failed to load puck image from URI: \"$uri\". Falling back to default navigation icon.",
                    e
                )
            }
        }.start()
    }

    private fun loadDrawableBitmap(resourceName: String): Bitmap? {
        val resId = context.resources.getIdentifier(resourceName, "drawable", context.packageName)
        if (resId == 0) {
            Log.e(TAG, "No drawable resource found for puck image name: \"$resourceName\".")
            return null
        }
        val drawable = ContextCompat.getDrawable(context, resId) ?: return null
        val w =
            if (puckImageWidth > 0) puckImageWidth else (if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 96)
        val h =
            if (puckImageHeight > 0) puckImageHeight else (if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 96)
        val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bmp
    }

    /**
     * Find the first SymbolLayer ID in the style.
     * Route layers are inserted below this layer so they render above
     * road/fill layers but below labels and POI icons. Returns null when the
     * style has no symbol layer, letting the SDK fall back to its own ordering.
     */
    private fun findFirstSymbolLayerId(style: Style): String? {
        for (layer in style.layers) {
            if (layer is SymbolLayer) {
                return layer.id
            }
        }
        return null
    }

    /**
     * Lazily create the MarkerViewManager bound to this navigation map. Returns null until the
     * map (vietMapGL) is ready. VietMapMarkerView components register their hosted child views
     * here; the plugin keeps each view positioned at its coordinate across camera moves.
     */
    fun getOrCreateMarkerViewManager(): vn.vietmap.vietmapnavigation.markers.MarkerViewManager? {
        if (markerViewManager == null && ::vietMapGL.isInitialized) {
            markerViewManager =
                vn.vietmap.vietmapnavigation.markers.MarkerViewManager(binding.mapView, vietMapGL)
        }
        return markerViewManager
    }

    private fun addDestinationIconSymbolLayer(loadedMapStyle: Style) {
        val geoJsonSource = GeoJsonSource("destination-source-id")
        loadedMapStyle.addSource(geoJsonSource)

        val destinationSymbolLayer =
            SymbolLayer("destination-symbol-layer-id", "destination-source-id")
        destinationSymbolLayer.withProperties(
            iconImage("destination-icon-id"), iconAllowOverlap(true), iconIgnorePlacement(true)
        )
        loadedMapStyle.addLayer(destinationSymbolLayer)
    }

    private fun moveCamera(
        location: LatLng,
        bearing: Float?,
        zoomLevel: Double = navigationZoomLevel,
    ) {

        val cameraPosition = CameraPosition.Builder().target(location).zoom(zoomLevel).tilt(tilt)

        if (bearing != null) {
            cameraPosition.bearing(bearing.toDouble())
        }

        var duration = 1000
        if (!animateBuildRoute) duration = 1
        vietMapGL.animateCamera(
            CameraUpdateFactory.newCameraPosition(cameraPosition.build()), duration
        )
    }


    private fun getRoute(
        context: Context, isStartNavigation: Boolean, bearing: Float?, profile: String,
    ) {
//        if (!PluginUtilities.isNetworkAvailable(context)) {
//            sendEvent(
//                VietMapEvents.ROUTE_BUILD_FAILED,
//            )
//            return
//        } 
        sendEvent(VietMapEvents.ROUTE_BUILDING)
        val br = bearing ?: 0.0
        val routeOptions = RouteOptions.builder()
            .coordinates(listNavigationRemainingPoints)
            .bearings(br.toString())
            .alternatives(true)
            .baseUrl("")
            .accessToken(apikey ?: "")
            .requestUuid("")
            .profile(profile)
            .user("")
            .build()
        val build = NavigationRoute.builder(context)
            .baseUrl(baseUrl)
            .apikey(apikey ?: "")
            .routeOptions(routeOptions)
            .origin(listNavigationRemainingPoints.first(), 60.0, br.toDouble())
            .destination(listNavigationRemainingPoints.last())
            .alternatives(true)
        ///driving-traffic
        ///cycling
        ///walking
        ///motorcycle
//            .profile(profile)

        for (i in 1 until listNavigationRemainingPoints.size - 1) {
            build.addWaypoint(listNavigationRemainingPoints[i])
        }
        val builder = build.build()
        listNavigationRemainingPoints.removeAt(0)
        builder.getRoute(object : Callback<DirectionsResponse?> {
            override fun onResponse(
                call: Call<DirectionsResponse?>, response: Response<DirectionsResponse?>,
            ) {
                if (response.body() == null || response.body()!!.routes().size < 1) {
                    sendEvent(VietMapEvents.ROUTE_BUILD_FAILED)
                    return
                }
                directionsRoutes = response.body()!!.routes()
                currentRoute = if (directionsRoutes!!.size <= primaryRouteIndex) {
                    directionsRoutes!![0]
                } else {
                    directionsRoutes!![primaryRouteIndex]
                }

                sendEvent(VietMapEvents.ROUTE_BUILT, currentRoute?.toJson()?.let { JSONObject(it) })

                // Draw the route on the map
                if (navigationMapRoute != null) {
                    navigationMapRoute?.removeRoute()
                } else {
                    val routeStyleRes = ThemeSwitcher.retrieveNavigationViewStyle(
                        binding.mapView.context,
                        vn.vietmap.services.android.navigation.R.style.NavigationMapRoute
                    )

                    belowLayerId = vietMapGL.style?.let { findFirstSymbolLayerId(it) }
                    navigationMapRoute =
                        NavigationMapRoute(
                            navigation,
                            binding.mapView,
                            vietMapGL,
                            routeStyleRes,
                            belowLayerId
                        )
                }

                //show multiple route to map
                if (response.body()!!.routes().size > 1) {
                    navigationMapRoute?.addRoutes(directionsRoutes!!)
                } else {
                    navigationMapRoute?.addRoute(currentRoute)
                }


                isBuildingRoute = false
                // get route point from current route
                val routePoints: List<Point> =
                    currentRoute?.routeOptions()?.coordinates() as List<Point>
                animateVietmapGLForRouteOverview(padding, routePoints)
                //Start Navigation again from new Point, if it was already in Progress 
                if (isNavigationInProgress || isStartNavigation) {
                    startNavigation()
                }
            }

            override fun onFailure(call: Call<DirectionsResponse?>, throwable: Throwable) {
                isBuildingRoute = false
                sendEvent(
                    VietMapEvents.ROUTE_BUILD_FAILED
                )
            }
        })
    }

    fun buildRoute(data: Any?) {
        if (data != null) {
            val points = (data as Map<*,*>)["points"] as ReadableArray
            profile = data["vehicle"].toString()

            startRoute(
                points,
                profile
            )
        }

    }

    fun demoVMFunc() {
        startNavigation()
    }

    fun startNavigation() {
        context.currentActivity?.runOnUiThread {

            tilt = 10000.0
            isOverviewing = false
            isNavigationCanceled = false

            // Fresh navigation session: reset GPS smoothing, off-route and arrival bookkeeping.
            resetGpsTracker()
            resetTrackingOffRouteState()
            pendingArrivalPoint = null
            lastArrivedLegIndex = -1
            completedWaypointIndex = 0

            navigation = VietmapNavigation(
                context, navigationOptions, locationEngine!!
            )
            if (currentRoute != null) {
                if (shouldSimulateRoute) {
                    val mockLocationEngine = ReplayRouteLocationEngine()

                    mockLocationEngine.assign(currentRoute)
                    navigation?.locationEngine = mockLocationEngine
                } else {
                    locationEngine?.let {
                        navigation?.locationEngine = it
                    }
                }
                isRunning = true
                // Mirror the Flutter SDK's navigation camera model: detach the LocationComponent's own
                // engine and feed the puck manually via forceLocationUpdate (onProgressChange), while
                // the built-in TRACKING_GPS camera (set by recenter() below) follows the puck smoothly.
                // We do NOT drive the camera per-tick anymore — that manual moveCamera fought the
                // LocationComponent and caused the jitter / unresponsive recenter.
                vietMapGL.locationComponent.locationEngine = null
                navigation?.addNavigationEventListener(this)
                navigation?.addFasterRouteListener(this)
                navigation?.addMilestoneEventListener(this)
                navigation?.addOffRouteListener(this)
                navigation?.addProgressChangeListener(this)


                navigation?.snapEngine = snapEngine

                navigationMapRoute!!.updateRouteArrowVisibilityTo(true)
                navigationMapRoute!!.showAlternativeRoutes(true)
                navigationMapRoute!!.updateRouteVisibilityTo(true)
                navigationMapRoute!!.showAlternativeRoutes(true)
                currentRoute?.let {
                    isNavigationInProgress = true
                    navigation?.startNavigation(currentRoute!!)
                    sendEvent(VietMapEvents.NAVIGATION_RUNNING)
                    recenter()

                }
            }
        }
    }

    fun finishNavigation(
        isOffRouted: Boolean = false,
        emitFinishedEvent: Boolean = true,
        isArrival: Boolean = false,
    ) {
        context.currentActivity?.runOnUiThread {
            // Captured before the teardown below clears it: a session that was still guiding when
            // a non-arrival finish comes in counts as a user/app cancellation.
            val wasNavigating = isNavigationInProgress

//            navigationZoomLevel = mapZoomLevel
            bearing = 0.0
            tilt = 0.0
            isNavigationCanceled = true

            // Off-route + arrival state belong to the route being left; a reroute restarts leg
            // indexing from 0, so always clear the pending arrival anchor.
            resetGpsTracker()
            resetTrackingOffRouteState()
            pendingArrivalPoint = null
            lastArrivedLegIndex = -1

            if (!isOffRouted) {
                isNavigationInProgress = false
//                moveCameraToOriginOfRoute()
                overViewRoute()
                // Real finish (not a reroute): the next session starts waypoint counting fresh.
                completedWaypointIndex = 0
                // Parity with iOS, which emits onNavigationFinished from its own stop path. The
                // NavigationListener.onNavigationFinished/onCancelNavigation overrides below never
                // run: that ui.v5 drop-in listener is never registered (we only
                // addNavigationEventListener), so this is the only place Android can report a real
                // session end. Suppressed for internal restarts (refreshNavigation) via
                // emitFinishedEvent=false.
                if (emitFinishedEvent) {
                    if (wasNavigating && !isArrival) {
                        // Guidance was still in progress and we did not reach the destination —
                        // a user/app stop is a cancellation. Always followed by FINISHED.
                        sendEvent(VietMapEvents.NAVIGATION_CANCELLED)
                    }
                    sendEvent(VietMapEvents.NAVIGATION_FINISHED)
                }
            }

            if (currentRoute != null) {
                isRunning = false
                navigation?.stopNavigation()
                navigation?.removeFasterRouteListener(this)
                navigation?.removeMilestoneEventListener(this)
                navigation?.removeNavigationEventListener(this)
                navigation?.removeOffRouteListener(this)
                navigation?.removeProgressChangeListener(this)

                if (restartAlert) {
                    // Nếu có cảnh báo tốc độ đang tạm dừng thì khởi động lại
                    val speedAlertManager = vietmapSDK.speedAlertManager
                    speedAlertManager.startSpeedAlerts()
                    restartAlert = false
                }
            }

        }
    }

    private fun moveCameraToOriginOfRoute() {
        currentRoute?.let {
            try {
                val originCoordinate = it.routeOptions()?.coordinates()?.get(0)
                originCoordinate?.let {
                    val location = LatLng(originCoordinate.latitude(), originCoordinate.longitude())
                    // println("MoveCamera1")
                    moveCamera(location, null)
                }
            } catch (e: java.lang.Exception) {
                Timber.i(String.format("moveCameraToOriginOfRoute, %s", "Error: ${e.message}"))
            }
        }
    }

    override fun onProgressChange(location: Location, routeProgress: RouteProgress) {
        if (!isNavigationCanceled) {
            try {
                val noRoutes: Boolean = directionsRoutes?.isEmpty() ?: true

                val newCurrentRoute: Boolean = !routeProgress.directionsRoute()
                    .equals(directionsRoutes?.get(primaryRouteIndex))
                val isANewRoute: Boolean = noRoutes || newCurrentRoute
                if (isANewRoute) {
                } else {

                    distanceRemaining = routeProgress.distanceRemaining()
                    durationRemaining = routeProgress.durationRemaining()


                    if (!isDisposed && !isBuildingRoute) {
                        val snappedLocation: Location =
                            snapEngine.getSnappedLocation(location, routeProgress)
                        // Xử lý cảnh báo tốc độ
                        val speedAlertManager = vietmapSDK.speedAlertManager
                        if (speedAlertManager.isSpeedAlertActive()) {
                            // Stop alert trước khi xử lý
                            speedAlertManager.stopSpeedAlerts()
                            restartAlert = true
                        }
                        // Log.d("VietmapSpeedAlert", "Current location: ${snappedLocation.latitude}, ${snappedLocation.longitude}, ${snappedLocation.speed}, ${snappedLocation.bearing.toDouble()}")
                        // Xử lý location
                        speedAlertManager.processExternalLocation(
                            snappedLocation.latitude,
                            snappedLocation.longitude,
                            snappedLocation.speed.toDouble(),
                            snappedLocation.bearing.toDouble()
                        )

                        // Route progress is reported with the snapped location (accurate for the SDK).
                        val progressEvent =
                            VietMapRouteProgressEvent(routeProgress, location, snappedLocation)
                        sendRouteProgressEvent(progressEvent)

                        // Distance from raw GPS to the snapped point — drives both the off-route
                        // recovery counter and the snapped/raw blend below.
                        val distanceFromRoute = calculateDistanceBetween2Point(location, snappedLocation)

                        // If we were flagged off-route but are snapping close again, count consecutive
                        // on-route ticks and clear the off-route state once stable (ported from Flutter).
                        if (isUserCurrentlyOffRoute && firstOffRoutePoint != null && distanceFromRoute < distanceToOffRoute) {
                            consecutiveOnRouteCount++
                            if (consecutiveOnRouteCount >= REQUIRED_ON_ROUTE_COUNT) {
                                resetTrackingOffRouteState()
                            }
                        } else {
                            consecutiveOnRouteCount = 0
                        }

                        // Track bearing divergence (blend decision only — no wrong-way event), then blend
                        // between snapped and Kalman-smoothed raw GPS for a smooth off-route transition.
                        detectBearingDivergence(location, snappedLocation, location.accuracy)
                        val displayLocation = blendLocation(location, snappedLocation, distanceFromRoute)

                        currentCenterPoint =
                            CurrentCenterPoint(
                                displayLocation.latitude,
                                displayLocation.longitude,
                                displayLocation.bearing
                            )
                        if (!isOverviewing) {
                            this.routeProgress = routeProgress
                        }

                        // Feed the puck on the UI thread. With TRACKING_GPS active, forceLocationUpdate
                        // now drives the tracking camera, which calls map APIs (getMetersPerPixelAtLatitude)
                        // that MUST run on the UI thread — onProgressChange can fire off the main thread.
                        context.currentActivity?.runOnUiThread {
                            vietMapGL.locationComponent.forceLocationUpdate(displayLocation)
                        }

                        // Distance-based, single-fire arrival (replaces the old milestone isArrivalEvent path).
                        checkPendingArrival(location, routeProgress)
                    }

//                    if (shouldSimulateRoute && !isDisposed && !isBuildingRoute) {
//                        vietmapGL?.locationComponent?.forceLocationUpdate(location)
//                    }

                    if (!isRefreshing) {
                        isRefreshing = true
                    }
                }
                handleProgressChange(routeProgress, location)
            } catch (_: java.lang.Exception) {
            }
        }
    }

    override fun userOffRoute(location: Location) {
        // Record the first off-route point (unless on a roundabout, where the SDK's snap is noisy).
        // checkIfUserOffRoute measures from this anchor so a single GPS blip doesn't trigger a reroute.
        if (firstOffRoutePoint == null && !isRoundabout()) {
            firstOffRoutePoint = location
            isUserCurrentlyOffRoute = true
        }
        if (checkIfUserOffRoute(location)) {
            speechPlayer!!.onOffRoute()
            sendEvent(
                VietMapEvents.USER_OFF_ROUTE,
                JSONObject("{\"latitude\":${location.latitude},\"longitude\":${location.longitude}}")
            )
            doOnNewRoute(Point.fromLngLat(location.longitude, location.latitude))
        }
    }

    private fun doOnNewRoute(offRoutePoint: Point?) {
        if (!isBuildingRoute) {
            isBuildingRoute = true

            offRoutePoint?.let {

                finishNavigation(isOffRouted = true)
                // println("MoveCamera3")

                moveCamera(LatLng(it.latitude(), it.longitude()), null)

                sendEvent(
                    VietMapEvents.USER_OFF_ROUTE, JSONObject(
                        VietMapLocation(
                            latitude = it.latitude(), longitude = it.longitude()
                        ).toString()
                    )
                )

            }

//            originPoint = offRoutePoint

            if (listNavigationRemainingPoints.isNotEmpty()) {
                listNavigationRemainingPoints.add(0, offRoutePoint!!)
                isNavigationInProgress = true

                fetchRouteWithBearing(false, profile)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun fetchRouteWithBearing(isStartNavigation: Boolean, profile: String) {
        context.currentActivity?.let {
            fusedLocationClient?.lastLocation?.addOnSuccessListener(
                it
            ) { location: Location? ->
                if (location != null) {
                    getRoute(context, isStartNavigation, location.bearing, profile)
                } else {

                    getRoute(context, isStartNavigation, null, profile)
                }
            }
        }

    }

    private fun checkIfUserOffRoute(location: Location): Boolean {
        if (routeProgress?.currentStepPoints() != null) {
            val snapLocation: Location = snapEngine.getSnappedLocation(location, routeProgress)
            // Measure from the first off-route anchor (ported from Flutter) so a momentary blip back
            // toward the route doesn't keep resetting the distance and suppress a genuine reroute.
            val distance: Double = calculateDistanceBetween2Point(location, firstOffRoutePoint ?: snapLocation)
            // The bearing-divergence guard (ported from Flutter) is what stops spurious reroutes:
            // during simulation a transient snap error near turns can push `distance` over the
            // threshold for one tick, but the heading still matches the route — so it is NOT off-route.
            // Without this guard those blips rerouted and restarted the replay engine from the origin.
            return distance > this.distanceToOffRoute
                    && checkIfUserIsDrivingToOtherRoute(location)
                    && areBearingsDiverging(
                location.bearing.toDouble(),
                snapLocation.bearing.toDouble()
            )
        }
        return false
    }

    private fun areBearingsDiverging(
        bearing1: Double,
        bearing2: Double,
        threshold: Double = 30.0
    ): Boolean {
        val diff = abs(bearing1 - bearing2) % 360
        val shortestAngle = if (diff > 180) 360 - diff else diff
        return shortestAngle >= threshold
    }

    private fun checkIfUserIsDrivingToOtherRoute(location: Location): Boolean {
        directionsRoutes?.forEach {
            //get list point
            snapLocationLatLng(
                location,
                it.routeOptions()?.coordinates() as List<Point>
            ).let { snapLocation ->
                val distance: Double = calculateDistanceBetween2Point(location, snapLocation)
                if (distance < 30) {
                    if (it != currentRoute) {
                        currentRoute = it
                        currentRoute?.toJson()?.let { it1 ->
                            sendEvent(
                                VietMapEvents.ON_NEW_ROUTE_SELECTED,
                                JSONObject(it1)
                            )
                            return false
                        }
                    }

                }
            }
        }
        return true
    }

    private fun snapLocationLatLng(location: Location, stepCoordinates: List<Point>): Location {
        val snappedLocation = Location(location)
        val locationToPoint = Point.fromLngLat(location.longitude, location.latitude)
        if (stepCoordinates.size > 1) {
            val feature = TurfMisc.nearestPointOnLine(locationToPoint, stepCoordinates)
            val point = feature.geometry() as Point?
            snappedLocation.longitude = point!!.longitude()
            snappedLocation.latitude = point.latitude()
        }
        return snappedLocation
    }

    private fun calculateDistanceBetween2Point(location1: Location, location2: Location): Double {
        val radius = 6371000.0 // meters

        val dLat = (location2.latitude - location1.latitude) * PI / 180.0
        val dLon = (location2.longitude - location1.longitude) * PI / 180.0

        val a =
            sin(dLat / 2.0) * sin(dLat / 2.0) + cos(location1.latitude * PI / 180.0) * cos(location2.latitude * PI / 180.0) * sin(
                dLon / 2.0
            ) * sin(dLon / 2.0)
        val c = 2.0 * kotlin.math.atan2(sqrt(a), sqrt(1.0 - a))

        return radius * c
    }

    // ====== GPS smoothing & snapped/raw blend (ported from Flutter GpsTracker) ======

    /**
     * Tracks whether the raw GPS bearing is diverging from the snapped route bearing for several
     * consecutive ticks. Used only to decide the blend below — NOT to raise a wrong-way event.
     */
    private fun detectBearingDivergence(rawLocation: Location, snappedLocation: Location, gpsAccuracy: Float): Boolean {
        if (gpsAccuracy > WRONG_WAY_MAX_ACCURACY) {
            bearingDivergenceCount = 0
            isBearingDiverging = false
            return false
        }
        val diff = abs(rawLocation.bearing.toDouble() - snappedLocation.bearing.toDouble()) % 360.0
        val shortestAngle = if (diff > 180.0) 360.0 - diff else diff
        if (shortestAngle > BEARING_DIVERGENCE_THRESHOLD) {
            bearingDivergenceCount++
        } else {
            bearingDivergenceCount = 0
        }
        isBearingDiverging = bearingDivergenceCount >= BEARING_DIVERGENCE_CONSECUTIVE_COUNT
        return isBearingDiverging
    }

    /**
     * Returns the snapped location while on-route, or a Kalman-smoothed raw GPS location once the
     * user is far enough off-route / diverging — giving a smooth transition instead of the puck
     * snapping back and forth.
     */
    private fun blendLocation(rawLocation: Location, snappedLocation: Location, distanceFromRoute: Double): Location {
        if (!isBearingDiverging && distanceFromRoute <= OFF_ROUTE_SNAP_START_DISTANCE) {
            return snappedLocation
        }
        return getSmoothedLocation(rawLocation)
    }

    private fun getSmoothedLocation(rawLocation: Location): Location {
        val smoothed = Location(rawLocation)
        smoothed.latitude = kalmanLat.update(rawLocation.latitude)
        smoothed.longitude = kalmanLng.update(rawLocation.longitude)
        smoothed.bearing = kalmanBearing.update(rawLocation.bearing.toDouble()).toFloat()
        smoothed.speed = rawLocation.speed
        smoothed.time = rawLocation.time
        smoothed.accuracy = rawLocation.accuracy
        return smoothed
    }

    /** Reset the Kalman filters and divergence state — call on start / reroute. */
    private fun resetGpsTracker() {
        kalmanLat.reset()
        kalmanLng.reset()
        kalmanBearing.reset()
        bearingDivergenceCount = 0
        isBearingDiverging = false
    }

    // ====== Distance-based, single-fire arrival (ported from Flutter) ======

    /**
     * Fires arrival exactly once per leg when the user gets within [ARRIVAL_RADIUS] of the armed
     * arrive maneuver (see onMilestoneEvent). On the final leg sends ON_ARRIVAL and finishes; on an
     * intermediate waypoint sends ON_WAYPOINT_ARRIVAL and advances. Replaces the old isArrivalEvent
     * path that could double-fire.
     */
    private fun checkPendingArrival(location: Location, routeProgress: RouteProgress) {
        val arrivalPoint = pendingArrivalPoint ?: return
        if (!isNavigationInProgress || isNavigationCanceled) return

        val arrivalLocation = Location("").apply {
            latitude = arrivalPoint.latitude()
            longitude = arrivalPoint.longitude()
        }
        val distance = calculateDistanceBetween2Point(location, arrivalLocation)
        if (distance <= ARRIVAL_RADIUS) {
            pendingArrivalPoint = null
            lastArrivedLegIndex = routeProgress.legIndex()
            val isFinalLeg = routeUtils.isLastLeg(routeProgress)
            if (isFinalLeg) {
                try {
                    vietMapGL.locationComponent.locationEngine = locationEngine
                } catch (_: Exception) {
                }
                val data = JSONObject()
                data.put("latitude", arrivalPoint.latitude())
                data.put("longitude", arrivalPoint.longitude())
                sendEvent(VietMapEvents.ON_ARRIVAL, data)
                if (listNavigationRemainingPoints.isNotEmpty()) {
                    listNavigationRemainingPoints.removeAt(0)
                }
                // Destination reached: this auto-stop is a completion, not a cancellation.
                finishNavigation(isArrival = true)
            } else {
                completedWaypointIndex++
                if (listNavigationRemainingPoints.isNotEmpty()) {
                    listNavigationRemainingPoints.removeAt(0)
                }
                sendEvent(
                    VietMapEvents.ON_WAYPOINT_ARRIVAL,
                    JSONObject("{\"latitude\":${arrivalPoint.latitude()},\"longitude\":${arrivalPoint.longitude()}}")
                )
            }
        }
    }

    // ====== Off-route helpers (ported from Flutter) ======

    private fun isRoundabout(): Boolean {
        val legProgress = routeProgress?.currentLegProgress() ?: return false
        val currentType = legProgress.currentStep()?.maneuver()?.type()
        return isRoundaboutManeuver(currentType)
    }

    private fun isRoundaboutManeuver(type: String?): Boolean {
        if (type == null) return false
        return type.contains("roundabout", ignoreCase = true) ||
                type.contains("rotary", ignoreCase = true)
    }

    private fun resetTrackingOffRouteState() {
        firstOffRoutePoint = null
        isUserCurrentlyOffRoute = false
        consecutiveOnRouteCount = 0
    }

    override fun onMilestoneEvent(
        routeProgress: RouteProgress,
        instruction: String,
        milestone: Milestone,
    ) {
        if (voiceInstructionsEnabled) {
            playVoiceAnnouncement(milestone)
        }
        // Arm arrival detection (ported from Flutter): capture the "arrive" maneuver location so
        // onProgressChange (checkPendingArrival) fires arrival exactly once when within ARRIVAL_RADIUS.
        // Guarded by lastArrivedLegIndex so we don't re-arm a leg whose arrival already fired.
        val currentLegIdx = routeProgress.legIndex()
        if (pendingArrivalPoint == null && isNavigationInProgress && currentLegIdx > lastArrivedLegIndex) {
            val legProgress = routeProgress.currentLegProgress()
            val upComingStep = legProgress?.upComingStep()
            val currentStep = legProgress?.currentStep()
            if (upComingStep?.maneuver()?.type()?.contains("arrive") == true) {
                pendingArrivalPoint = upComingStep.maneuver().location()
            } else if (currentStep?.maneuver()?.type()?.contains("arrive") == true) {
                pendingArrivalPoint = currentStep.maneuver().location()
            }
        }
        if (!isNavigationCanceled) {
            sendEvent(VietMapEvents.MILESTONE_EVENT, JSONObject().put("instruction", instruction))
        }
    }

    private fun playVoiceAnnouncement(milestone: Milestone?) {
        if (milestone is VoiceInstructionMilestone) {
            val announcement = SpeechAnnouncement.builder()
                .voiceInstructionMilestone(milestone as VoiceInstructionMilestone?).build()
            speechPlayer!!.play(announcement)
        }
    }

    private fun configSpeechPlayer() {
        val speechPlayerProvider = SpeechPlayerProvider(context, "vi", true)
        this.speechPlayer = NavigationSpeechPlayer(speechPlayerProvider)
    }

    override fun onRunning(p0: Boolean) {
        if (!isNavigationCanceled) {
            sendEvent(VietMapEvents.NAVIGATION_RUNNING)

        }
    }

    override fun onCancelNavigation() {
        sendEvent(VietMapEvents.NAVIGATION_CANCELLED)
        navigation?.stopNavigation()
        isRunning = false

        if (restartAlert) {
            // Nếu trước đó có cảnh báo tốc độ đang hoạt động thì restart lại
            val speedAlertManager = vietmapSDK.speedAlertManager
            speedAlertManager.startSpeedAlerts()
            restartAlert = false
        }
    }

    override fun onNavigationFinished() {
        vietMapGL.locationComponent.locationEngine = locationEngine
        // Navigation owned the camera (CameraMode.NONE); hand it back to the LocationComponent
        // so the puck is followed again now that the raw location engine is re-attached.
        vietMapGL.locationComponent.cameraMode = CameraMode.TRACKING_GPS_NORTH
        sendEvent(VietMapEvents.NAVIGATION_FINISHED)

        if (restartAlert) {
            // Nếu trước đó có cảnh báo tốc độ đang hoạt động thì restart lại
            val speedAlertManager = vietmapSDK.speedAlertManager
            speedAlertManager.startSpeedAlerts()
            restartAlert = false
        }
    }

    override fun onNavigationRunning() {
        if (!isNavigationCanceled) {
            sendEvent(VietMapEvents.NAVIGATION_RUNNING)

        }
    }

    override fun fasterRouteFound(directionsRoute: DirectionsRoute) {

        refreshNavigation(directionsRoute)
    }

    private fun refreshNavigation(directionsRoute: DirectionsRoute?, shouldCancel: Boolean = true) {
        directionsRoute?.let {

            if (shouldCancel) {

                currentRoute = directionsRoute
                // Internal stop+restart for a route refresh — not a real session end, so don't
                // emit onNavigationFinished to JS.
                finishNavigation(emitFinishedEvent = false)
                startNavigation()
            }
        }
    }

    override fun willVoice(announcement: SpeechAnnouncement?): SpeechAnnouncement? {
        return if (voiceInstructionsEnabled) {

            announcement
        } else {
            null
        }
    }

    override fun willDisplay(instructions: BannerInstructions?): BannerInstructions? {
        return if (bannerInstructionsEnabled) {

            return instructions
        } else {
            null
        }
    }

    override fun onResponseReceived(p0: DirectionsResponse?, p1: RouteProgress?) {
    }

    override fun onErrorReceived(p0: Throwable?) {
    }

    override fun onDidFinishRenderingMap(p0: Boolean) {
    }

    private fun animateVietmapGLForRouteOverview(padding: IntArray, routePoints: List<Point>) {
        if (routePoints.size <= 1) {
            return
        }
        val resetUpdate: CameraUpdate = buildResetCameraUpdate()
        val overviewUpdate: CameraUpdate = buildOverviewCameraUpdate(padding, routePoints)
        context.currentActivity?.runOnUiThread {
            vietMapGL.animateCamera(
                resetUpdate, 150, CameraOverviewCancelableCallback(overviewUpdate, vietMapGL)
            )
        }
    }

    private fun buildResetCameraUpdate(): CameraUpdate {
        val resetPosition: CameraPosition = CameraPosition.Builder().tilt(0.0).bearing(0.0).build()
        return CameraUpdateFactory.newCameraPosition(resetPosition)
    }

    private fun buildOverviewCameraUpdate(
        padding: IntArray, routePoints: List<Point>,
    ): CameraUpdate {
        val routeBounds = convertRoutePointsToLatLngBounds(routePoints)
        return newLatLngBounds(
            routeBounds, padding[0], padding[1], padding[2], padding[3]
        )
    }

    private fun convertRoutePointsToLatLngBounds(routePoints: List<Point>): LatLngBounds {
        val latLngs: MutableList<LatLng> = ArrayList()
        for (routePoint in routePoints) {
            latLngs.add(LatLng(routePoint.latitude(), routePoint.longitude()))
        }
        return LatLngBounds.Builder().includes(latLngs).build()
    }


    private fun handleProgressChange(routeProgress: RouteProgress, location: Location) {
        // println("handleProgressChange")
        if (location.speed < 1) return
        // println("start handleProgressChange")

        val distanceRemainingToNextTurn =
            routeProgress.currentLegProgress()?.currentStepProgress()?.distanceRemaining()
        if (distanceRemainingToNextTurn != null && distanceRemainingToNextTurn < 30) {
            isNextTurnHandling = true
            val resetPosition: CameraPosition =
                CameraPosition.Builder().tilt(0.0).zoom(17.0).build()
            val cameraUpdate = CameraUpdateFactory.newCameraPosition(resetPosition)
            vietMapGL.animateCamera(
                cameraUpdate, 1000
            )
        } else {
            if (routeProgress.currentLegProgress().currentStepProgress()
                    .distanceTraveled() > 30 && !isOverviewing
            ) {
                isNextTurnHandling = false
                recenter()
            }
        }
    }


    fun recenter() {
        isOverviewing = false
        // Match the Flutter SDK exactly: ONLY hand the camera back to the LocationComponent in
        // TRACKING_GPS, which smoothly animates to the puck (fed by forceLocationUpdate) and follows it.
        // Do NOT also moveCamera()/animateCamera() here — that extra programmatic animation overlaps the
        // setCameraMode transition and, during simulation, makes the location component reset its
        // animation/engine state, restarting the ReplayRouteLocationEngine from the route origin.
        context.currentActivity?.runOnUiThread {
            vietMapGL.locationComponent.setCameraMode(
                CameraMode.TRACKING_GPS,
                1000L,
                navigationZoomLevel,
                null,
                tilt,
                null
            )
        }
    }

    fun overViewRoute() {
        isOverviewing = true
        // Must run on the UI thread: setting cameraMode triggers a LocationComponent camera transition
        // whose reset calls getMetersPerPixelAtLatitude (a map API that asserts the UI thread), and the
        // overview animateCamera touches the map too. Commands can arrive off the main thread.
        context.currentActivity?.runOnUiThread {
            // Release the tracking camera first; otherwise the per-tick forceLocationUpdate would snap
            // the camera back to the puck and fight the overview animation. recenter() restores TRACKING_GPS.
            vietMapGL.locationComponent.cameraMode = CameraMode.NONE
            routeProgress?.let { showRouteOverview(padding, it) }
        }
    }

    fun clearRoute() {
        context.currentActivity?.runOnUiThread {
            if (navigationMapRoute != null) {
                navigationMapRoute?.removeRoute()
            }
            currentRoute = null
        }
    }


    private fun showRouteOverview(padding: IntArray?, currentRouteProgress: RouteProgress) {

        val routeInformation: RouteInformation =
            buildRouteInformationFromProgress(currentRouteProgress)
        animateCameraForRouteOverview(routeInformation, padding!!)
    }

    private fun buildRouteInformationFromProgress(routeProgress: RouteProgress?): RouteInformation {
        return if (routeProgress == null) {
            RouteInformation.create(null, null, null)
        } else RouteInformation.create(routeProgress.directionsRoute(), null, null)
    }

    private fun animateCameraForRouteOverview(
        routeInformation: RouteInformation, padding: IntArray,
    ) {
        val cameraEngine = navigation?.cameraEngine
        val routePoints = cameraEngine?.overview(routeInformation)
        if (routePoints?.isNotEmpty() == true) {
            animateVietmapGLForRouteOverview(padding, routePoints)
        }
    }

    override fun onMapClick(point: LatLng): Boolean {
        if (routeClicked) {
            routeClicked = false
            return true
        }

        val pointF = vietMapGL.projection.toScreenLocation(point)
        val map = JSONObject()
        map.put("latitude", point.latitude)
        map.put("longitude", point.longitude)
        map.put("x", pointF.x)
        map.put("y", pointF.y)
        sendEvent(
            VietMapEvents.ON_MAP_CLICK,
            map
        )
        return true
    }


    override fun onMapLongClick(point: LatLng): Boolean {
        val pointf = vietMapGL.projection.toScreenLocation(point)

        val map = JSONObject()
        map.put("latitude", point.latitude)
        map.put("longitude", point.longitude)
        map.put("x", pointf.x)
        map.put("y", pointf.y)
        sendEvent(
            VietMapEvents.ON_MAP_LONG_CLICK,
            map
        )
        return false

    }


    private fun sendRouteProgressEvent(event: VietMapRouteProgressEvent) {
        val dataString = JSONObject(event.toJson())
        val writableMap = Arguments.createMap()
        writableMap.putString("eventType", VietMapEvents.PROGRESS_CHANGE.value)

        val convertedData = JSONConverter().convertJsonToReadableMap(dataString)
        writableMap.putMap("data", convertedData)
        val surfaceId = UIManagerHelper.getSurfaceId(this)
        UIManagerHelper.getEventDispatcherForReactTag(context, id)
            ?.dispatchEvent(VietMapEvent(surfaceId, id, "onRouteProgressChange", writableMap))
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun startSpeedAlert() {
        // Check permissions for GPS mode (always starts with GPS)
        if (!vietmapSDK.hasLocationPermissions()) {
            checkPermissions()
            return
        }

        // Get speed alert manager and start with GPS mode (simplified)
        val speedAlertManager = vietmapSDK.speedAlertManager
        speedAlertManager.startSpeedAlerts() // Always starts with GPS
        sendEvent(VietMapEvents.START_ALERT)
    }

    fun stopSpeedAlert() {
        val speedAlertManager = vietmapSDK.speedAlertManager
        speedAlertManager.stopSpeedAlerts()
        restartAlert = false
        sendEvent(VietMapEvents.STOP_ALERT)
    }

    fun isSpeedAlertActive(): Boolean {
        return try {
            val speedAlertManager = vietmapSDK.speedAlertManager
            speedAlertManager.isSpeedAlertActive()
        } catch (e: Exception) {
            false
        }
    }

    fun configVehicleSpeedAlert(vehicleId: String, vehicleType: Int, seats: Int, weight: Double) {
        // Store vehicle configuration parameters
        pendingVehicleId = vehicleId
        pendingVehicleType = vehicleType
        pendingVehicleSeats = seats
        pendingVehicleWeight = weight

        Log.d(
            TAG,
            "Storing vehicle config: ID=$vehicleId, Type=$vehicleType, Seats=$seats, Weight=$weight"
        )

        // Try to configure immediately if SDK is initialized
        configureVehicle()
    }

    private fun sendEvent(eventName: VietMapEvents, data: JSONObject? = null) {
        val writableMap = Arguments.createMap()
        writableMap.putString("eventType", eventName.value)
        if (data != null) {
            val convertedData = JSONConverter().convertJsonToReadableMap(data)
            writableMap.putMap("data", convertedData)
        }
        val surfaceId = UIManagerHelper.getSurfaceId(this)
        UIManagerHelper.getEventDispatcherForReactTag(context, id)
            ?.dispatchEvent(VietMapEvent(surfaceId, id, eventName.value, writableMap))
    }

    fun setBaseUrl(baseUrl: String) {
        this.baseUrl = baseUrl
    }

    private fun checkPermissions() {
        context.currentActivity?.let { activity ->
            if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    1001
                )
            }
        }
    }
}

/**
 * Simple 1D Kalman filter for GPS coordinate/bearing smoothing (ported from Flutter
 * utilities/GpsKalmanFilter.kt). Reduces jitter in raw GPS so the off-route blend is smooth.
 */
private class GpsKalmanFilter(
    private val q: Double = 0.00001, // process noise
    private val r: Double = 0.0001   // measurement noise
) {
    private var p: Double = 1.0      // estimation error covariance
    private var x: Double = 0.0      // estimated value
    private var initialized: Boolean = false

    fun update(measurement: Double): Double {
        if (!initialized) {
            x = measurement
            initialized = true
            return x
        }
        p += q
        val k = p / (p + r)
        x += k * (measurement - x)
        p *= (1 - k)
        return x
    }

    fun reset() {
        initialized = false
        p = 1.0
    }
}