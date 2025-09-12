
import {VietMapNavigationModule} from './native_modules';
import Coordinate from './models/coordinates';

export enum VehicleType {
    car = 1,           // Xe ô tô
    taxi = 2,          // Xe taxi
    bus = 3,           // Xe bus
    coach = 4,         // Xe khách
    truck = 5,         // Xe tải
    trailer = 6,       // Xe remooc
    cycle = 7,         // Xe mô tô
    bike = 8,          // Xe đạp
    pedestrian = 9,    // Người đi bộ
    semiTrailer = 10   // Xe sơ mi rơ moóc
}

export class VietMapNavigationController{

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
    static buildRoute(coordinates: Coordinate[], profile:  'driving-traffic'|'cycling'|'walking'|'motorcycle'|'truck') {
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

    static isSpeedAlertActive(): Promise<boolean> {
        return VietMapNavigationModule.isSpeedAlertActive();
    }

    static configureAlertAPI(apiKey: string, apiID: string) {
        return VietMapNavigationModule.configureAlertAPI(apiKey, apiID);
    }

    /**
     * Configure vehicle for speed alert system
     * @param vehicleId Unique identifier for the vehicle
     * @param vehicleType Type of vehicle (use VehicleType enum)
     * @param seats Number of seats in the vehicle
     * @param weight Vehicle weight in kg
     */
    static configVehicleSpeedAlert(vehicleId: string, vehicleType: VehicleType, seats: number, weight: number) {
        return VietMapNavigationModule.configVehicleSpeedAlert(vehicleId, vehicleType, seats, weight);
    }


    
 

}

// export default VietMapNavigationController