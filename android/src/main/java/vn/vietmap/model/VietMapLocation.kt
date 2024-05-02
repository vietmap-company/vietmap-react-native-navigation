package vn.vietmap.model

class VietMapLocation(val name: String = "", private val latitude: Double?, private val longitude: Double?) {
    override fun toString(): String {
        return "{" +
                "  \"latitude\": $latitude," +
                "  \"longitude\": $longitude" +
                "}"
    }

}