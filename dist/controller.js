import { VietMapNavigationModule } from './native_modules';
export var VehicleType;
(function (VehicleType) {
    VehicleType[VehicleType["car"] = 1] = "car";
    VehicleType[VehicleType["taxi"] = 2] = "taxi";
    VehicleType[VehicleType["bus"] = 3] = "bus";
    VehicleType[VehicleType["coach"] = 4] = "coach";
    VehicleType[VehicleType["truck"] = 5] = "truck";
    VehicleType[VehicleType["trailer"] = 6] = "trailer";
    VehicleType[VehicleType["cycle"] = 7] = "cycle";
    VehicleType[VehicleType["bike"] = 8] = "bike";
    VehicleType[VehicleType["pedestrian"] = 9] = "pedestrian";
    VehicleType[VehicleType["semiTrailer"] = 10] = "semiTrailer"; // Xe sơ mi rơ moóc
})(VehicleType || (VehicleType = {}));
export class VietMapNavigationController {
    static finishNavigation() {
        return VietMapNavigationModule.finishNavigation();
    }
    static overView() {
        return VietMapNavigationModule.overView();
    }
    static startNavigation() {
        return VietMapNavigationModule.startNavigation();
    }
    // static cancelNavigation() {
    //     return VietMapNavigationModule.cancelNavigation();
    // }
    ///driving-traffic
    ///cycling
    ///walking
    ///motorcycle
    static buildRoute(coordinates, profile) {
        return VietMapNavigationModule.buildRoute(coordinates, profile);
    }
    static clearRoute() {
        return VietMapNavigationModule.clearRoute();
    }
    static recenter() {
        return VietMapNavigationModule.recenter();
    }
    static startSpeedAlert() {
        return VietMapNavigationModule.startSpeedAlert();
    }
    static stopSpeedAlert() {
        return VietMapNavigationModule.stopSpeedAlert();
    }
    static isSpeedAlertActive() {
        return VietMapNavigationModule.isSpeedAlertActive();
    }
    static configureAlertAPI(apiKey, apiID) {
        return VietMapNavigationModule.configureAlertAPI(apiKey, apiID);
    }
    /**
     * Configure vehicle for speed alert system
     * @param vehicleId Unique identifier for the vehicle
     * @param vehicleType Type of vehicle (use VehicleType enum)
     * @param seats Number of seats in the vehicle
     * @param weight Vehicle weight in kg
     */
    static configVehicleSpeedAlert(vehicleId, vehicleType, seats, weight) {
        return VietMapNavigationModule.configVehicleSpeedAlert(vehicleId, vehicleType, seats, weight);
    }
}
// export default VietMapNavigationController
