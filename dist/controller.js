import { VietMapNavigationModule } from './native_modules';
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
}
// export default VietMapNavigationController
