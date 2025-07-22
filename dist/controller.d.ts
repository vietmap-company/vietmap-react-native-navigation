import Coordinate from './models/coordinates';
export declare class VietMapNavigationController {
    static finishNavigation(): void;
    static overView(): void;
    static startNavigation(): void;
    static buildRoute(coordinates: Coordinate[], profile: 'driving-traffic' | 'cycling' | 'walking' | 'motorcycle' | 'truck'): void;
    static clearRoute(): void;
    static recenter(): void;
}
