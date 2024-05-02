import Coordinate from './models/coordinates';
export declare class VietMapNavigationController {
    static finishNavigation(): any;
    static overView(): any;
    static startNavigation(): any;
    static cancelNavigation(): any;
    static buildRoute(coordinates: [Coordinate, Coordinate], profile: 'driving-traffic' | 'cycling' | 'walking' | 'motorcycle'): any;
    static clearRoute(): any;
    static recenter(): any;
}
