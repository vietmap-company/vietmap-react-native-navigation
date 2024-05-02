
import {VietMapNavigationModule} from './native_modules';
import Coordinate from './models/coordinates';
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
    static buildRoute(coordinates: [Coordinate,Coordinate], profile:  'driving-traffic'|'cycling'|'walking'|'motorcycle') {
        return VietMapNavigationModule.buildRoute(coordinates, profile);
    }
 

    static clearRoute() {
        return VietMapNavigationModule.clearRoute();
    }

    static recenter() {
        return VietMapNavigationModule.recenter();
    }


    
 

}

// export default VietMapNavigationController