package vn.vietmap.utilities

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.annotations.SerializedName
import com.mapbox.api.directions.v5.DirectionsAdapterFactory
import com.mapbox.api.directions.v5.WalkingOptions
import com.mapbox.api.directions.v5.WalkingOptionsAdapterFactory
import com.mapbox.api.directions.v5.models.DirectionsJsonObject
import com.mapbox.api.directions.v5.utils.FormatUtils
import com.mapbox.api.directions.v5.utils.ParseUtils
import com.mapbox.geojson.Point
import com.mapbox.geojson.PointAsCoordinatesTypeAdapter


abstract class VMRouteOptions : DirectionsJsonObject() {
    abstract fun baseUrl(): String

    abstract fun user(): String

    abstract fun profile(): String

    abstract fun coordinates(): List<Point?>

    abstract fun alternatives(): Boolean?

    abstract fun language(): String?

    abstract fun radiuses(): String?

    fun radiusesList(): List<Double>? {
        return ParseUtils.parseToDoubles(this.radiuses())
    }

    abstract fun bearings(): String?

    fun bearingsList(): List<List<Double>>? {
        return ParseUtils.parseToListOfListOfDoubles(this.bearings())
    }

    @SerializedName("continue_straight")
    abstract fun continueStraight(): Boolean?

    @SerializedName("roundabout_exits")
    abstract fun roundaboutExits(): Boolean?

    abstract fun geometries(): String?

    abstract fun overview(): String?

    abstract fun steps(): Boolean?

    abstract fun annotations(): String?

    fun annotationsList(): List<String>? {
        return ParseUtils.parseToStrings(this.annotations(), ",")
    }

    abstract fun exclude(): String?

    @SerializedName("voice_instructions")
    abstract fun voiceInstructions(): Boolean?

    @SerializedName("banner_instructions")
    abstract fun bannerInstructions(): Boolean?

    @SerializedName("voice_units")
    abstract fun voiceUnits(): String?

    @SerializedName("access_token")
    abstract fun accessToken(): String

    @SerializedName("uuid")
    abstract fun requestUuid(): String

    abstract fun approaches(): String?

    fun approachesList(): List<String>? {
        return ParseUtils.parseToStrings(this.approaches())
    }

    @SerializedName("waypoints")
    abstract fun waypointIndices(): String?

    fun waypointIndicesList(): List<Int>? {
        return ParseUtils.parseToIntegers(this.waypointIndices())
    }

    @SerializedName("waypoint_names")
    abstract fun waypointNames(): String?

    fun waypointNamesList(): List<String>? {
        return ParseUtils.parseToStrings(this.waypointNames())
    }

    @SerializedName("waypoint_targets")
    abstract fun waypointTargets(): String?

    fun waypointTargetsList(): List<Point>? {
        return ParseUtils.parseToPoints(this.waypointTargets())
    }

    abstract fun walkingOptions(): WalkingOptions?

    @SerializedName("snapping_closures")
    abstract fun snappingClosures(): String?

    fun snappingClosuresList(): List<Boolean>? {
        return ParseUtils.parseToBooleans(this.snappingClosures())
    }

    abstract fun toBuilder(): Builder

    abstract class Builder {
        abstract fun baseUrl(var1: String): Builder?

        abstract fun user(var1: String): Builder?

        abstract fun profile(var1: String): Builder?

        abstract fun coordinates(var1: List<Point?>): Builder?

        abstract fun alternatives(var1: Boolean): Builder?

        abstract fun language(var1: String): Builder?

        abstract fun radiuses(var1: String): Builder?

        fun radiusesList(radiuses: List<Double?>): Builder {
            val result = FormatUtils.formatRadiuses(radiuses)
            if (result != null) {
                this.radiuses(result)
            }

            return this
        }

        abstract fun bearings(var1: String): Builder?

        fun bearingsList(bearings: List<List<Double?>?>): Builder {
            val result = FormatUtils.formatBearings(bearings)
            if (result != null) {
                this.bearings(result)
            }

            return this
        }

        abstract fun continueStraight(var1: Boolean): Builder?

        abstract fun roundaboutExits(var1: Boolean): Builder?

        abstract fun geometries(var1: String): Builder?

        abstract fun overview(var1: String): Builder?

        abstract fun steps(var1: Boolean): Builder?

        abstract fun annotations(var1: String): Builder?

        fun annotationsList(annotations: List<String?>): Builder {
            val result = FormatUtils.join(",", annotations)
            if (result != null) {
                this.annotations(result)
            }

            return this
        }

        abstract fun voiceInstructions(var1: Boolean): Builder?

        abstract fun bannerInstructions(var1: Boolean): Builder?

        abstract fun voiceUnits(var1: String): Builder?

        abstract fun accessToken(var1: String): Builder?

        abstract fun requestUuid(var1: String): Builder?

        abstract fun exclude(var1: String): Builder?

        abstract fun approaches(var1: String): Builder?

        fun approachesList(approaches: List<String?>): Builder {
            val result = FormatUtils.formatApproaches(approaches)
            if (result != null) {
                this.approaches(result)
            }

            return this
        }

        abstract fun waypointIndices(var1: String): Builder?

        fun waypointIndicesList(indices: List<Int?>): Builder {
            val result = FormatUtils.join(";", indices)
            if (result != null) {
                this.waypointIndices(result)
            }

            return this
        }

        abstract fun waypointNames(var1: String): Builder?

        fun waypointNamesList(waypointNames: List<String?>): Builder {
            val result = FormatUtils.formatWaypointNames(waypointNames)
            if (result != null) {
                this.waypointNames(result)
            }

            return this
        }

        abstract fun waypointTargets(var1: String): Builder?

        fun waypointTargetsList(waypointTargets: List<Point?>): Builder {
            this.waypointTargets(FormatUtils.formatPointsList(waypointTargets)!!)
            return this
        }

        abstract fun walkingOptions(var1: WalkingOptions): Builder?

        abstract fun snappingClosures(var1: String): Builder?

        fun snappingClosures(snappingClosures: List<Boolean?>): Builder {
            val result = FormatUtils.join(";", snappingClosures)
            if (result != null) {
                this.snappingClosures(result)
            } else {
                this.snappingClosures("")
            }

            return this
        }

        abstract fun build(): VMRouteOptions?
    }

    companion object {

        fun fromJson(json: String?): VMRouteOptions {
            val gson = GsonBuilder()
            gson.registerTypeAdapterFactory(DirectionsAdapterFactory.create())
            gson.registerTypeAdapter(Point::class.java, PointAsCoordinatesTypeAdapter())
            gson.registerTypeAdapterFactory(WalkingOptionsAdapterFactory.create())
            return gson.create().fromJson(json, VMRouteOptions::class.java) as VMRouteOptions
        }
    }
}
