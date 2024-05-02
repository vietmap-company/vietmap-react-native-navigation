package vn.vietmap.model

enum class VietmapReactNativeEvent(val value: String) {
    BUILD_ROUTE("buildRoute"),
    START_NAVIGATION("startNavigation"),
    STOP_NAVIGATION("stopNavigation"),
    RECENTER("recenter"),
    MUTE("mute"),
    BUILD_AND_START_NAVIGATION("buildAndStartNavigation"),
    FINISH_NAVIGATION("finishNavigation"),
    OVERVIEW("overView"),
    CLEAR_ROUTE("clearRoute"),
}