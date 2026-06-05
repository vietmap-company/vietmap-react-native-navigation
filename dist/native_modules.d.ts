interface VietMapNavigationModuleType {
    buildRoute: (points: any[], vehicle?: string) => void;
    startNavigation: () => void;
    stopNavigation: () => void;
    recenter: () => void;
    mute: () => void;
    buildAndStartNavigation: () => void;
    finishNavigation: () => void;
    overView: () => void;
    clearRoute: () => void;
    startSpeedAlert: () => void;
    stopSpeedAlert: () => void;
    isSpeedAlertActive: () => Promise<boolean>;
    configureAlertAPI: (apiKey: string, apiID: string) => void;
    configVehicleSpeedAlert: (vehicleId: string, vehicleType: number, seats: number, weight: number) => void;
}
export declare const VietMapNavigationModule: VietMapNavigationModuleType;
export default VietMapNavigationModule;
