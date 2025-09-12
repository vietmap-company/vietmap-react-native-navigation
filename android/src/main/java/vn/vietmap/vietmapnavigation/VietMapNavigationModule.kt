package vn.vietmap.vietmapnavigation

import android.util.Log
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableNativeArray
import com.facebook.react.bridge.Promise
import vn.vietmap.model.VietmapReactNativeEvent


class VietMapNavigationModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {
    
    companion object {
        var currentInstance: VietMapNavigationModule? = null
    }
    
    init {
        currentInstance = this
    }
    
    override fun getName(): String {
        return "VietMapNavigationModule"
    }

    private fun sendEvent(eventName: VietmapReactNativeEvent, eventData: Any?) {
        Log.d(eventName.name, "Sent event")
        when (eventName) {
            VietmapReactNativeEvent.BUILD_ROUTE -> VietMapNavigationView.instance!!.buildRoute(
                eventData
            )

            VietmapReactNativeEvent.START_NAVIGATION -> VietMapNavigationView.instance!!.startNavigation()
            VietmapReactNativeEvent.STOP_NAVIGATION -> VietMapNavigationView.instance!!.finishNavigation()
            VietmapReactNativeEvent.RECENTER -> VietMapNavigationView.instance!!.recenter()
            VietmapReactNativeEvent.MUTE -> VietMapNavigationView.instance!!.setMute(mute = true)
            VietmapReactNativeEvent.BUILD_AND_START_NAVIGATION -> VietMapNavigationView.instance!!.demoVMFunc()
            VietmapReactNativeEvent.FINISH_NAVIGATION -> VietMapNavigationView.instance!!.finishNavigation()
            VietmapReactNativeEvent.OVERVIEW -> VietMapNavigationView.instance!!.overViewRoute()
            VietmapReactNativeEvent.CLEAR_ROUTE -> VietMapNavigationView.instance!!.clearRoute()
        }
    }

    @ReactMethod
    fun clearRoute() {
        sendEvent(
            VietmapReactNativeEvent.CLEAR_ROUTE, null
        )
    }

    @ReactMethod
    fun buildRoute(points: ReadableArray, vehicle: String?) {
        val coordinatesList: WritableArray = WritableNativeArray()
        val originCoordinates: WritableArray = WritableNativeArray()
        val destinationCoordinates: WritableArray = WritableNativeArray()
        originCoordinates.pushDouble(
            points.getMap(0)?.getDouble("long") ?: 0.0
        )
        originCoordinates.pushDouble(
            points.getMap(0)?.getDouble("lat") ?: 0.0
        )
        destinationCoordinates.pushDouble(
            points.getMap(1)?.getDouble("long") ?: 0.0
        )
        destinationCoordinates.pushDouble(
            points.getMap(1)?.getDouble("lat") ?: 0.0
        )
        coordinatesList.pushArray(
            originCoordinates
        )


        sendEvent(
            VietmapReactNativeEvent.BUILD_ROUTE,
            mapOf("points" to points, "vehicle" to vehicle)
        )
    }

    @ReactMethod
    fun startNavigation() {
        Log.d("startNavigation", "startNavigation")
        sendEvent(VietmapReactNativeEvent.START_NAVIGATION, null)
    }

    @ReactMethod
    fun stopNavigation() {
        sendEvent(VietmapReactNativeEvent.STOP_NAVIGATION, null)
    }

    @ReactMethod
    fun recenter() {
        sendEvent(VietmapReactNativeEvent.RECENTER, null)
    }

    @ReactMethod
    fun mute() {
        sendEvent(VietmapReactNativeEvent.MUTE, null)
    }

    @ReactMethod
    fun buildAndStartNavigation() {
        sendEvent(VietmapReactNativeEvent.BUILD_AND_START_NAVIGATION, null)
    }

    @ReactMethod
    fun finishNavigation() {
        sendEvent(VietmapReactNativeEvent.FINISH_NAVIGATION, null)
    }

    @ReactMethod
    fun overView() {
        sendEvent(VietmapReactNativeEvent.OVERVIEW, null)
    }

    @ReactMethod
    fun startSpeedAlert() {
        VietMapNavigationView.instance?.startSpeedAlert()
    }

    @ReactMethod
    fun stopSpeedAlert() {
        VietMapNavigationView.instance?.stopSpeedAlert()
    }

    @ReactMethod
    fun isSpeedAlertActive(promise: Promise) {
        try {
            val isActive = VietMapNavigationView.instance?.isSpeedAlertActive() ?: false
            promise.resolve(isActive)
        } catch (e: Exception) {
            promise.reject("ERROR", "Failed to check speed alert status", e)
        }
    }

    @ReactMethod
    fun configureAlertAPI(apiKey: String, apiID: String) {
        VietMapNavigationView.instance?.let { view ->
            view.setApiKeyAlert(apiKey)
            view.setApiIDAlert(apiID)
        }
    }

    @ReactMethod
    fun configVehicleSpeedAlert(vehicleId: String, vehicleType: Int, seats: Int, weight: Double) {
        VietMapNavigationView.instance?.configVehicleSpeedAlert(vehicleId, vehicleType, seats, weight)
    }
}