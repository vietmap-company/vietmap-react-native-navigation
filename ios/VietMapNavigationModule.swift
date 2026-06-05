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
        // print("cancelNavigation hereeeeeee")
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
    
    @objc
    func startSpeedAlert() {
        DispatchQueue.main.async {
            let map = VietMapNavigationView.shared
            map.startSpeedAlert()
        }
    }
    
    @objc
    func stopSpeedAlert() {
        DispatchQueue.main.async {
            let map = VietMapNavigationView.shared
            map.stopSpeedAlert()
        }
    }
    
    @objc
    func isSpeedAlertActive(_ resolve: @escaping RCTPromiseResolveBlock, rejecter reject: @escaping RCTPromiseRejectBlock) {
        DispatchQueue.main.async {
            let map = VietMapNavigationView.shared
            let isActive = map.isSpeedAlertActive()
            resolve(isActive)
        }
    }
    
    @objc
    func configureAlertAPI(_ apiKey: String, apiID: String) {
        DispatchQueue.main.async {
            let map = VietMapNavigationView.shared
            map.apiKeyAlert = apiKey
            map.apiIDAlert = apiID
        }
    }
    
    @objc
    func configVehicleSpeedAlert(_ vehicleId: String, vehicleType: Int, seats: Int, weight: Double) {
        DispatchQueue.main.async {
            let map = VietMapNavigationView.shared
            map.configVehicleSpeedAlert(vehicleId: vehicleId, vehicleType: vehicleType, seats: seats, weight: weight)
        }
    }
}
