import Foundation
@objc(VietMapNavigationModule)
class VietMapNavigationModule: NSObject {
    @objc
    func buildRoute(_ location: NSArray, profile: NSString) {
        DispatchQueue.main.async {
            let map = VietMapNavigationView.shared
            map.buildRoute(location: location, profile: profile)
        }
    }
    
    @objc
    func startNavigation() {
        DispatchQueue.main.async {
            let map = VietMapNavigationView.shared
            map.startNavigation()
        }
    }
    
    @objc
    func finishNavigation() {
        DispatchQueue.main.async {
            let map = VietMapNavigationView.shared
            map.finishNavigation()
        }
    }
    
    @objc
    func overView() {
        DispatchQueue.main.async {
            let map = VietMapNavigationView.shared
            map.overView()
        }
    }
    
    @objc
    func cancelNavigation() {
        print("cancelNavigation hereeeeeee")
    }
    
    @objc
    func clearRoute() {
        DispatchQueue.main.async {
            let map = VietMapNavigationView.shared
            map.clearRoute()
        }
    }
    
    @objc
    func recenter() {
        DispatchQueue.main.async {
            let map = VietMapNavigationView.shared
            map.recenter()
        }
    }
}
