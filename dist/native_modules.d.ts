interface VietMapNavigationModuleType {
    testModule: () => void;
    buildRoute: (points: any[], vehicle?: string) => void;
    startNavigation: () => void;
    stopNavigation: () => void;
    recenter: () => void;
    mute: () => void;
    buildAndStartNavigation: () => void;
    finishNavigation: () => void;
    overView: () => void;
    clearRoute: () => void;
}
export declare const VietMapNavigationModule: VietMapNavigationModuleType;
export default VietMapNavigationModule;
