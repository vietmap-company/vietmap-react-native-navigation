import Coordinate from './models/coordinates';
export declare enum VehicleType {
    car = 1,// Xe ô tô
    taxi = 2,// Xe taxi
    bus = 3,// Xe bus
    coach = 4,// Xe khách
    truck = 5,// Xe tải
    trailer = 6,// Xe remooc
    cycle = 7,// Xe mô tô
    bike = 8,// Xe đạp
    pedestrian = 9,// Người đi bộ
    semiTrailer = 10
}
export declare class VietMapNavigationController {
    static finishNavigation(): void;
    static overView(): void;
    static startNavigation(): void;
    static buildRoute(coordinates: Coordinate[], profile: 'driving-traffic' | 'cycling' | 'walking' | 'motorcycle' | 'truck'): void;
    static clearRoute(): void;
    static recenter(): void;
    static startSpeedAlert(): void;
    static stopSpeedAlert(): void;
    static isSpeedAlertActive(): Promise<boolean>;
    static configureAlertAPI(apiKey: string, apiID: string): void;
    /**
     * Configure vehicle for speed alert system
     * @param vehicleId Unique identifier for the vehicle
     * @param vehicleType Type of vehicle (use VehicleType enum)
     * @param seats Number of seats in the vehicle
     * @param weight Vehicle weight in kg
     */
    static configVehicleSpeedAlert(vehicleId: string, vehicleType: VehicleType, seats: number, weight: number): void;
}
