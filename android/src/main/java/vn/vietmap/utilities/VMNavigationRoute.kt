package vn.vietmap.utilities

import android.content.Context
import android.text.TextUtils
import androidx.annotation.FloatRange
import com.mapbox.api.directions.v5.MapboxDirections
import com.mapbox.api.directions.v5.models.DirectionsResponse
import com.mapbox.api.directions.v5.models.RouteOptions
import com.mapbox.geojson.Point
import okhttp3.Interceptor
import retrofit2.Call
import retrofit2.Callback
import vn.vietmap.services.android.navigation.v5.utils.LocaleUtils
import java.util.Locale

class VMNavigationRoute private constructor(private val mapboxDirections: MapboxDirections) {
    fun getRoute(callback: Callback<DirectionsResponse?>?) {
        mapboxDirections.enqueueCall(callback)
    }

    val call: Call<DirectionsResponse>
        get() = mapboxDirections.cloneCall()

    fun cancelCall() {
        call.cancel()
    }

    class Builder internal constructor() {
        private val directionsBuilder: MapboxDirections.Builder = MapboxDirections.builder()

        fun user(user: String): Builder {
            directionsBuilder.user(user)
            return this
        }

        fun profile(profile: String): Builder {
            directionsBuilder.profile(profile)
            return this
        }

        fun origin(origin: Point): Builder {
            this.origin(origin, null as Double?, null as Double?)
            return this
        }

        fun origin(origin: Point, angle: Double?, tolerance: Double?): Builder {
            directionsBuilder.origin(origin)
            directionsBuilder.addBearing(angle, tolerance)
            return this
        }

        fun destination(destination: Point): Builder {
            this.destination(destination, null as Double?, null as Double?)
            return this
        }

        fun destination(destination: Point, angle: Double?, tolerance: Double?): Builder {
            directionsBuilder.destination(destination)
            directionsBuilder.addBearing(angle, tolerance)
            return this
        }

        fun addWaypoint(waypoint: Point): Builder {
            directionsBuilder.addWaypoint(waypoint)
            directionsBuilder.addBearing(null as Double?, null as Double?)
            return this
        }

        fun addWaypoint(waypoint: Point, angle: Double?, tolerance: Double?): Builder {
            directionsBuilder.addWaypoint(waypoint)
            directionsBuilder.addBearing(angle, tolerance)
            return this
        }

        fun alternatives(alternatives: Boolean?): Builder {
            directionsBuilder.alternatives(alternatives)
            return this
        }

        fun language(language: Locale?): Builder {
            directionsBuilder.language(language)
            return this
        }

        fun language(context: Context?, localeUtils: LocaleUtils): Builder {
            directionsBuilder.language(localeUtils.inferDeviceLocale(context))
            return this
        }

        fun annotations(vararg annotations: String?): Builder {
            directionsBuilder.annotations(*annotations)
            return this
        }

        fun addBearing(
            @FloatRange(from = 0.0, to = 360.0) angle: Double?,
            @FloatRange(from = 0.0, to = 360.0) tolerance: Double?,
        ): Builder {
            directionsBuilder.addBearing(angle, tolerance)
            return this
        }

        fun radiuses(@FloatRange(from = 0.0) radiuses: List<Double?>?): Builder {
            directionsBuilder.radiuses(radiuses!!)
            return this
        }

        fun voiceUnits(voiceUnits: String?): Builder {
            directionsBuilder.voiceUnits(voiceUnits)
            return this
        }

        fun voiceUnits(context: Context?, localeUtils: LocaleUtils): Builder {
            directionsBuilder.voiceUnits(localeUtils.getUnitTypeForDeviceLocale(context))
            return this
        }

        fun exclude(exclude: String?): Builder {
            directionsBuilder.exclude(exclude)
            return this
        }

        fun clientAppName(clientAppName: String): Builder {
            directionsBuilder.clientAppName(clientAppName)
            return this
        }

        fun interceptor(interceptor: Interceptor?): Builder {
            directionsBuilder.interceptor(interceptor)
            return this
        }

        fun apikey(apikey: String): Builder {
            directionsBuilder.apikey(apikey)
            return this
        }

        fun baseUrl(baseUrl: String?): Builder {
            directionsBuilder.baseUrl(baseUrl)
            return this
        }

        fun addApproaches(vararg approaches: String?): Builder {
            directionsBuilder.addApproaches(*approaches)
            return this
        }

        fun addWaypointNames(vararg waypointNames: String?): Builder {
            directionsBuilder.addWaypointNames(*waypointNames)
            return this
        }

        fun routeOptions(options: RouteOptions): Builder {
            if (!TextUtils.isEmpty(options.baseUrl())) {
                directionsBuilder.baseUrl(options.baseUrl())
            }

            if (!TextUtils.isEmpty(options.language())) {
                directionsBuilder.language(Locale(options.language()))
            }

            if (options.alternatives() != null) {
                directionsBuilder.alternatives(options.alternatives())
            }

            if (!TextUtils.isEmpty(options.profile())) {
                directionsBuilder.profile(options.profile())
            }

            if (options.alternatives() != null) {
                directionsBuilder.alternatives(options.alternatives())
            }

            if (!TextUtils.isEmpty(options.voiceUnits())) {
                directionsBuilder.voiceUnits(options.voiceUnits())
            }

            if (!TextUtils.isEmpty(options.user())) {
                directionsBuilder.user(options.user())
            }

            if (!TextUtils.isEmpty(options.accessToken())) {
                directionsBuilder.apikey(options.accessToken())
            }

            if (!TextUtils.isEmpty(options.annotations())) {
                directionsBuilder.annotations(*arrayOf(options.annotations()))
            }

            var waypointNames: Array<String?>
            if (!TextUtils.isEmpty(options.approaches())) {
                waypointNames =
                    options.approaches()!!.split(";".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                directionsBuilder.addApproaches(*waypointNames)
            }

            if (!TextUtils.isEmpty(options.waypointNames())) {
                waypointNames =
                    options.waypointNames()!!.split(";".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                directionsBuilder.addWaypointNames(*waypointNames)
            }

            return this
        }

        fun build(): VMNavigationRoute {
            directionsBuilder.steps(true).continueStraight(true).geometries("polyline6")
                .overview("full").voiceInstructions(true).bannerInstructions(true)
                .roundaboutExits(true)
            return VMNavigationRoute(directionsBuilder.build())
        }
    }

    companion object {
        fun builder(context: Context?): Builder {
            return builder(context, LocaleUtils())
        }

        fun builder(context: Context?, localeUtils: LocaleUtils): Builder {
            return Builder()
                .annotations("congestion", "distance").language(context, localeUtils)
                .voiceUnits(context, localeUtils).profile("driving-traffic")
        }
    }
}
