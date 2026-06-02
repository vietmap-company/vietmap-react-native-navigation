import Foundation
import UIKit
import VietMapDirections
import VietMapNavigation
import VietMapCoreNavigation
import VietmapTrackingSDK

private typealias RouteRequestSuccess = (([Route]) -> Void)
private typealias RouteRequestFailure = ((NSError) -> Void)

extension UIView {
    var parentViewController: UIView? {
        var parentResponder: UIResponder? = self
        while parentResponder != nil {
            parentResponder = parentResponder!.next
            if let viewController = parentResponder as? UIView {
                return viewController
            }
        }
        return nil
    }
    
    func addToWindow() {
        guard let window = UIApplication.shared.connectedScenes
            .compactMap({ $0 as? UIWindowScene })
            .flatMap({ $0.windows })
            .first(where: { $0.isKeyWindow }) else { return }
        self.frame = window.bounds
        window.addSubview(self)
    }
}

@objcMembers class VietMapNavigationView: UIView, NavigationViewControllerDelegate, MLNMapViewDelegate {
    static var shared = VietMapNavigationView()
    var navigationMapView: NavigationMapView? {
        didSet {
            if let mapView = navigationMapView {
                configureMapView(mapView)
            }
        }
    }
    var routes: [Route]? {
        didSet {
            guard let routes = routes,
                  let current = routes.first else { navigationMapView?.removeRoutes(); return }
            navigationMapView?.showRoutes(routes)
            navigationMapView?.showWaypoints(current)
        }
    }
    var embedded: Bool
    var embedding: Bool
    let _url = Bundle.main.object(forInfoDictionaryKey: "VietMapURL") as! String
    var _wayPoints = [Waypoint]()
    var _coordinates: [CLLocationCoordinate2D]?
    var _remainingPointCount: Int = 0
    @objc var routeController: RouteController?

    /// Last location reported by the route controller, used to keep the puck glued to the GPS
    /// position on every rendered frame (see mapViewDidFinishRenderingFrame).
    var currentLocation: CLLocation?
    /// Wall-clock time of the previous course-tracking camera update, used to animate each
    /// camera move over the real GPS tick interval instead of a hardcoded duration.
    private var lastCameraUpdateTime: TimeInterval = 0

    // MARK: - define parameter from RN
    @objc var shouldSimulateRoute: Bool = false
    @objc var initialLatLngZoom: NSDictionary = [:]
    @objc var navigationPadding: NSDictionary = [:]
    @objc var navigationZoomLevel: Double = 10
    @objc var apiKeyAlert: String? {
        didSet {
            configureAlertAPIIfNeeded()
        }
    }
    @objc var apiIDAlert: String? {
        didSet {
            configureAlertAPIIfNeeded()
        }
    }
    
    // MARK: - define Event Block from RN
    @objc var onRouteProgressChange: RCTDirectEventBlock?
    @objc var onError: RCTDirectEventBlock?
    @objc var onNavigationFinished: RCTDirectEventBlock?
    @objc var onArrival: RCTDirectEventBlock?
    @objc var onUserOffRoute: RCTDirectEventBlock?
    @objc var onRouteBuilt: RCTDirectEventBlock?
    @objc var onRouteBuildFailed: RCTDirectEventBlock?
    @objc var onMapLongClick: RCTDirectEventBlock?
    @objc var onMapClick: RCTDirectEventBlock?
    @objc var onMapMove: RCTDirectEventBlock?
    @objc var onMapMoveEnd: RCTDirectEventBlock?
    @objc var onNewRouteSelected: RCTDirectEventBlock?
    @objc var startAlert: RCTDirectEventBlock?
    @objc var stopAlert: RCTDirectEventBlock?

    private var trackingSDK: VietmapTrackingManager?
    var restartAlert: Bool = false
    
    // Vehicle configuration properties
    private var vehicleType: Int = 1  // Default vehicle type
    private var seats: Int = 4        // Default seats
    private var weights: Float = 1500 // Default weight
    private let maxProvision: Int = 1000 // Fixed maxProvision value
    
    override init(frame: CGRect) {
        self.embedded = false
        self.embedding = false
        super.init(frame: frame)
        setupTrackingSDK()
    }
    
    required init?(coder aDecoder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        if (navigationMapView == nil) {
            setupMapView()
        }
    }
    
    override func removeFromSuperview() {
        
        VietMapNavigationView.shared = VietMapNavigationView()
        super.removeFromSuperview()
    }
    
    // MARK: - event from RN
    public func sendEvent(event: RCTDirectEventBlock?, data: [String: Any] = ["": ""]) {
        event?(["data": data]);
    }
    
    @objc
    func buildRoute(location: NSArray, profile: NSString) {
        requestRoute(location: encodeBuildRoute(location: location), profile: profile)
    }
    
    @objc
    func startNavigation() {
        guard let route = self.routes?.first, let navigationMapView = self.navigationMapView else {return}
        routeController = RouteController(along: route, locationManager: self.getNavigationLocationManager(simulated: self.shouldSimulateRoute))
        routeController?.delegate = self
        routeController?.reroutesProactively = true
        routeController?.resume()
        // Enable course tracking so the first GPS tick snaps both puck and camera to the route.
        // We drive the camera ourselves per-tick (see progressDidChange) instead of recenterMap(),
        // which lives in the RouteMapViewController we bypass and doesn't move our viewport.
        lastCameraUpdateTime = 0
        navigationMapView.tracksUserCourse = true
        navigationMapView.showsUserLocation = true
        resumeNotifications()
    }
    
    @objc
    func finishNavigation() {
        guard let navigationMapView = self.navigationMapView else {return}
        if (routeController != nil) {
            routeController?.endNavigation()
            navigationMapView.recenterMap()
            navigationMapView.userTrackingMode = .follow
            navigationMapView.tracksUserCourse = false
            currentLocation = nil
            lastCameraUpdateTime = 0
            suspendNotifications()
            sendEvent(event: onNavigationFinished)
            if (restartAlert) {
                // Nếu trước đó có cảnh báo tốc độ đang hoạt động thì restart lại
                startSpeedAlert()
                restartAlert = false
            }
            UIApplication.shared.isIdleTimerDisabled = false
        }
    }
    
    @objc
    func overView() {
        guard let navigationMapView = self.navigationMapView else {return}
        guard let wayPoint = self._wayPoints.first, let coordinate =  self._coordinates else {return}
        navigationMapView.setOverheadCameraView(from: wayPoint.coordinate, along: coordinate, for: self.overheadInsets)
    }
    
    @objc
    func clearRoute() {
        guard let navigationMapView = self.navigationMapView else {return}
        if routes == nil
        {
            return
        }
        navigationMapView.removeRoutes()
        navigationMapView.removeWaypoints()
        _wayPoints.removeAll()
        UIApplication.shared.isIdleTimerDisabled = false
    }
    
    @objc
    func recenter() {
        guard let navigationMapView = self.navigationMapView else {return}
        navigationMapView.recenterMap()
    }
    
    @objc
    func startSpeedAlert() {
        guard let trackingSDK = trackingSDK else { return }
        
        // Use simple turnOnAlert without location mode for now
        trackingSDK.turnOnAlert() { [weak self] (success: Bool) in
            DispatchQueue.main.async {
                if success {
                    self?.sendEvent(event: self?.startAlert)
                } else {
                    
                }
            }
        }
    }
    
    @objc
    func stopSpeedAlert() {
        guard let trackingSDK = trackingSDK else { return }
        
        trackingSDK.turnOffAlert { [weak self] success in
            DispatchQueue.main.async {
                if success {
                    self?.sendEvent(event: self?.stopAlert)
                } else {
                    
                }
            }
        }
    }
    
    @objc
    func isSpeedAlertActive() -> Bool {
        guard let trackingSDK = trackingSDK else { return false }
        return trackingSDK.isSpeedAlertCurrentlyActive()
    }
    
    private func configureAlertAPIIfNeeded() {
        guard let trackingSDK = trackingSDK,
              let apiKeyAlert = apiKeyAlert,
              let apiIDAlert = apiIDAlert else { return }
        
        trackingSDK.configureAlertAPI(
            apiKey: apiKeyAlert,
            apiID: apiIDAlert,
            url: "https://drive-api.vietmap.vn/fleetwork/api/Alert/v2/mpp"
        )
    }
    
    @objc
    func configVehicleSpeedAlert(vehicleId: String, vehicleType: Int, seats: Int, weight: Double) {
        // Configure vehicle for speed alert with maxProvision defaulted to 1000
        guard let trackingSDK = trackingSDK else { return }
        
        // Use VMVehicleType.fromValue() to convert Int to VMVehicleType enum
        let vehicleTypeEnum = VMVehicleType.fromValue(vehicleType)
        
        print("setup configVehicleSpeedAlert \(vehicleId) \(vehicleTypeEnum) \(seats) \(weight)")
        
        // Call the SDK method with maxProvision set to default 1000
        trackingSDK.configureVehicleWithType(
           vehicleId: vehicleId,
           vehicleType: vehicleTypeEnum,
           seats: seats,
           weight: Double(weight)
        )
        
        // Store vehicle configuration for later use if needed
        self.vehicleType = vehicleType
        self.seats = seats
        self.weights = Float(weight)
    }
    
    private func setupTrackingSDK() {
        trackingSDK = VietmapTrackingManager.shared
        
        // Try to configure with custom values if available
        configureAlertAPIIfNeeded()
    }

    // MARK: - sub function Event RN
    func getNavigationLocationManager(simulated: Bool) -> NavigationLocationManager {
        guard let route = self.routes?.first else { return NavigationLocationManager() }
        let simulatedLocationManager = SimulatedLocationManager(route: route)
        simulatedLocationManager.speedMultiplier = 2
        return simulated ? simulatedLocationManager : NavigationLocationManager()
    }
    
    func resumeNotifications() {
        NotificationCenter.default.addObserver(self, selector: #selector(progressDidChange(_ :)), name: .routeControllerProgressDidChange, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(rerouted(_:)), name: .routeControllerDidReroute, object: nil)
//        NotificationCenter.default.addObserver(self, selector: #selector(didPassInstructionPoint(notification:)), name: .routeControllerDidPassSpokenInstructionPoint, object: routeController)
//        NotificationCenter.default.addObserver(self, selector: #selector(updateInstructionsBanner(notification:)), name: .routeControllerDidPassVisualInstructionPoint, object: routeController)
    }
    
    func suspendNotifications() {
        NotificationCenter.default.removeObserver(self, name: .routeControllerProgressDidChange, object: nil)
        NotificationCenter.default.removeObserver(self, name: .routeControllerWillReroute, object: nil)
        NotificationCenter.default.removeObserver(self, name: .routeControllerDidReroute, object: nil)
    }
    
    @objc var overheadInsets: UIEdgeInsets {
        return UIEdgeInsets(
            top: navigationPadding["top"] as? Double ?? 0,
            left: navigationPadding["left"] as? Double ?? 0,
            bottom: navigationPadding["bottom"] as? Double ?? 0,
            right: navigationPadding["right"] as? Double ?? 0)
    }

    // MARK: - onProgressChange
    @objc func progressDidChange(_ notification: NSNotification) {
        guard let navigationMapView = self.navigationMapView else {return}
        guard let routeProgress = notification.userInfo?[RouteControllerNotificationUserInfoKey.routeProgressKey] as? RouteProgress else { return }
        guard let location = notification.userInfo?[RouteControllerNotificationUserInfoKey.locationKey] as? CLLocation else { return }
        guard let rawLocation = notification.userInfo?[RouteControllerNotificationUserInfoKey.rawLocationKey] as? CLLocation else { return }
        // Store the latest GPS so mapViewDidFinishRenderingFrame can keep the puck glued to it.
        currentLocation = location
        // Update the user puck
        let camera = MLNMapCamera(lookingAtCenter: location.coordinate, altitude: altitudeForZoomLevel(zoomLevel: navigationZoomLevel), pitch: Constants.courseTrackingCameraPitch, heading: location.course)
        // Add maneuver arrow
        if routeProgress.currentLegProgress.followOnStep != nil {
            navigationMapView.addArrow(route: routeProgress.route, legIndex: routeProgress.legIndex, stepIndex: routeProgress.currentLegProgress.stepIndex + 1)
        } else {
            navigationMapView.removeArrow()
        }
         if (trackingSDK?.isSpeedAlertCurrentlyActive() == true) {
            // Stop alert trước khi xử lý
            stopSpeedAlert()
            restartAlert = true
        }
        // Move camera explicitly to GPS on every tick.
        // The Pods SDK's setCamera() lives in RouteMapViewController which we bypass.
        // updateCourseTracking() ignores its camera param; only setCamera() moves the viewport.
        // Guard on tracksUserCourse so the camera stops following when the user explores the map.
        if navigationMapView.tracksUserCourse {
            // Place GPS at `courseTrackingUserAnchorRatio` down the visible content — matching
            // NavigationMapView's internal userAnchorPoint (contentFrame.height * 0.8). Without
            // this, setCamera puts GPS at center (50%), leaving the puck visually below the GPS
            // screen position.
            //
            // setCamera centers the target in the region BELOW the top inset, so to land the
            // puck at ratio `r` of the content height the top inset must be (2r - 1) * height.
            // Use the safe-area-inset content rect, not raw bounds: navigation banners and the
            // notch shrink the visible map, and bounds.height would push the anchor off-target.
            let content = navigationMapView.bounds.inset(by: navigationMapView.safeAreaInsets)
            let edgePaddingTop = max(0, (2 * Constants.courseTrackingUserAnchorRatio - 1)) * content.height
            let edgePadding = UIEdgeInsets(top: edgePaddingTop, left: 0, bottom: 0, right: 0)

            // Animate over the real interval between GPS ticks so the camera glides continuously
            // instead of finishing early and stalling. Clamp to avoid a multi-second crawl after
            // a signal gap, or a stutter when ticks arrive in a burst. First tick has no prior
            // timestamp, so fall back to the default duration.
            let now = Date().timeIntervalSince1970
            let duration: TimeInterval
            if lastCameraUpdateTime > 0 {
                let delta = now - lastCameraUpdateTime
                duration = min(Constants.courseTrackingMaxCameraDuration, max(Constants.courseTrackingMinCameraDuration, delta))
            } else {
                duration = Constants.courseTrackingDefaultCameraDuration
            }
            lastCameraUpdateTime = now

            navigationMapView.setCamera(camera, withDuration: duration, animationTimingFunction: CAMediaTimingFunction(name: .linear), edgePadding: edgePadding, completionHandler: nil)

            // Keep rendering at full frame rate while course-tracking.
            //
            // NavigationMapView registers its OWN progressDidChange observer in commonInit() that
            // listens to the same .routeControllerProgressDidChange notification. When tracking, its
            // 3s enableFrameByFrameCourseViewTracking window expires and it drops
            // preferredFramesPerSecond to FrameIntervalOptions.decreasedFramesPerSecond (5 fps) on
            // straight stretches to save power — fine for the SDK's own course tracking, but at 5 fps
            // our per-tick camera animation and per-frame puck reprojection turn choppy (the jitter
            // that kicks in ~3s after movement starts). Our observer runs after the SDK's, so pinning
            // the frame rate back to maximum here wins.
            navigationMapView.preferredFramesPerSecond = MLNMapViewPreferredFramesPerSecond.maximum
        }
        // Sets userLocationForCourseTracking (required by frame-by-frame) and updates puck bearing.
        navigationMapView.updateCourseTracking(location: location, camera: camera, animated: true)
        // Handle alert
        trackingSDK?.processExternalLocation(latitude: location.coordinate.latitude, longitude: location.coordinate.longitude, speed: location.speed, heading: location.course)
        sendEvent(event: onRouteProgressChange, data: encodeRouteProgressChange(routeProgress: routeProgress,location:location,rawLocation:rawLocation))
    }
    
    func altitudeForZoomLevel(zoomLevel: Double) -> CLLocationDistance {
        return pow(2, 18 - zoomLevel)
    }
    
    @objc func didPassInstructionPoint(notification: NSNotification) {
        guard let routeProgress = notification.userInfo?[RouteControllerNotificationUserInfoKey.routeProgressKey] as? RouteProgress else { return }
        
        if routeProgress.currentLegProgress.currentStepProgress.durationRemaining <= RouteControllerHighAlertInterval {
//            print("milestone sssssssssss")
        }
    }
    
    @objc func updateInstructionsBanner(notification: NSNotification) {
        guard let routeProgress = notification.userInfo?[RouteControllerNotificationUserInfoKey.routeProgressKey] as? RouteProgress else { return }
//        print("milestone herree")
    }

    
    // MARK: - reroute
    @objc func rerouted(_ notification: NSNotification) {
        guard let navigationMapView = self.navigationMapView else {return}
        navigationMapView.showRoutes([(routeController?.routeProgress.route)!])
        navigationMapView.tracksUserCourse = true
        navigationMapView.recenterMap()
        if let userInfo = notification.object as? RouteController {
            let locationData = userInfo.locationManager.location?.coordinate
            sendEvent(event: onUserOffRoute, data: encodeLocation(location: locationData!, position: nil))
        }
    }
    
    // MARK: - init map view
    func setupMapView() {
        let parentView = self.parentViewController
        navigationMapView = NavigationMapView(frame: frame,styleURL: URL(string: _url))
        // Gesture for map
        let longClick = UILongPressGestureRecognizer(target: self, action: #selector(handleLongPress(_:)))
        longClick.delegate = self
        navigationMapView!.addGestureRecognizer(longClick)
        parentView?.addSubview(navigationMapView!)
        
        let onClick = UITapGestureRecognizer(target: self, action: #selector(handlePress(_:)))
        onClick.delegate = self
        navigationMapView?.addGestureRecognizer(onClick)
        
        let gestureRecognizers = navigationMapView!.gestureRecognizers
        for gestureRecognizer in gestureRecognizers ?? [] where !(gestureRecognizer is UILongPressGestureRecognizer) && !(gestureRecognizer is UITapGestureRecognizer) {
            gestureRecognizer.addTarget(self, action: #selector(handlePanGesture(_:)))
        }
    }
    
    // MARK: - configureMapView
    func configureMapView(_ mapView: NavigationMapView) {
        mapView.autoresizingMask = [.flexibleWidth, .flexibleHeight]
        mapView.delegate = self
        mapView.navigationMapDelegate = self
        mapView.userTrackingMode = .follow
        mapView.logoView.isHidden = false
        if let initLat = initialLatLngZoom["lat"] as? Double, let initLong = initialLatLngZoom["lng"] as? Double {
            let initialCoordinate = CLLocationCoordinate2D(latitude: initLat, longitude: initLong)
            let zoomLevel: Double = initialLatLngZoom["zoom"] as? Double ?? 6
            
            mapView.setCenter(initialCoordinate, zoomLevel: zoomLevel, animated: false)
        }
    }

    // MARK: - MLNMapViewDelegate
    // Mirror what the Pods SDK's updateCourseTrackingAfterDidFinishRenderingFrame() does internally.
    // On every rendered frame, convert the GPS coordinate to the current screen point and place
    // the puck there. During camera animation the screen point changes each frame, so the puck
    // glides smoothly with the GPS instead of lagging behind.
    //
    // Intentionally NOT guarded by tracksUserCourse: when the user pans the map mid-navigation the
    // SDK flips tracksUserCourse to false, and because we bypass RouteMapViewController its internal
    // per-frame puck reprojection doesn't run. Without reprojecting here the puck would freeze at
    // its last screen point while the map slides underneath, so it appears dragged by the finger
    // and detached from the route. Reprojecting every frame keeps the puck glued to its map
    // coordinate whether the camera is following or the user is exploring.
    func mapViewDidFinishRenderingFrame(_ mapView: MLNMapView, fullyRendered: Bool) {
        guard let navigationMapView = self.navigationMapView,
              let location = currentLocation,
              let puck = navigationMapView.userCourseView else { return }
        puck.center = navigationMapView.convert(location.coordinate, toPointTo: navigationMapView)
    }

    // MARK: - define response
    fileprivate lazy var defaultSuccess: RouteRequestSuccess = { [weak self] (routes) in
        guard let strongSelf = self else { return }
        strongSelf.routes = routes
        guard let current = routes.first else { return }
         
        strongSelf._remainingPointCount = current.routeOptions.waypoints.count - 1
        
        strongSelf._wayPoints = current.routeOptions.waypoints
        strongSelf._coordinates = current.coordinates
        strongSelf.sendEvent(event: strongSelf.onRouteBuilt, data: encodeRoute(route: current))
    }
    
    fileprivate lazy var defaultFailure: RouteRequestFailure = { [weak self] (error) in
        guard let strongSelf = self else { return }
        strongSelf.sendEvent(event: strongSelf.onRouteBuildFailed)
    }
}

// MARK: - UIGestureRecognizerDelegate
extension VietMapNavigationView : UIGestureRecognizerDelegate {
    
    public func gestureRecognizer(_ gestureRecognizer: UIGestureRecognizer, shouldRecognizeSimultaneouslyWith otherGestureRecognizer: UIGestureRecognizer) -> Bool {
        return true
    }
    
    @objc func handleLongPress(_ gesture: UILongPressGestureRecognizer) {
        guard let navigationMapView = self.navigationMapView else {return}
        guard gesture.state == .ended else { return }
        let location = navigationMapView.convert(gesture.location(in: navigationMapView), toCoordinateFrom: navigationMapView)
        
        let screenPosition = gesture.location(in: navigationMapView)
        sendEvent(event: onMapLongClick, data: encodeLocation(location: location, position: screenPosition))
    }
    
    @objc func handlePress(_ gesture: UITapGestureRecognizer) {
        guard let navigationMapView = self.navigationMapView else {return}
        guard gesture.state == .ended else { return }
        let location = navigationMapView.convert(gesture.location(in: navigationMapView), toCoordinateFrom: navigationMapView)
        let screenPosition = gesture.location(in: navigationMapView)
        sendEvent(event: onMapClick, data: encodeLocation(location: location, position: screenPosition))
    }
    
    @objc func handlePanGesture(_ gestureRecognizer: UIPanGestureRecognizer) {
        if gestureRecognizer.state == .began {
            
        } else if gestureRecognizer.state == .changed {
            sendEvent(event: onMapMove)
        } else if gestureRecognizer.state == .ended {
            sendEvent(event: onMapMoveEnd)
        }
    }
    
    // MARK: - requestRoute
    func requestRoute(location: NSArray, profile: NSString) {
        guard location.count >= 2 else { return }
        // let originWaypoint = Waypoint(coordinate: CLLocationCoordinate2D(latitude: (location[0] as! NSArray)[0] as! CLLocationDegrees, longitude: (location[0] as! NSArray)[1] as! CLLocationDegrees))
        // let destinationWaypoint = Waypoint(coordinate: CLLocationCoordinate2D(latitude: (location[1] as! NSArray)[0] as! CLLocationDegrees, longitude: (location[1] as! NSArray)[1] as! CLLocationDegrees))
        /// Convert location to list of waypoints
        
        let waypoints = location.map { (item) -> Waypoint in
            let lat = (item as! NSArray)[0] as! Double
            let long = (item as! NSArray)[1] as! Double
            return Waypoint(coordinate: CLLocationCoordinate2D(latitude: lat, longitude: long))
        } 
        embedding = true
        var mode: MBDirectionsProfileIdentifier = .automobileAvoidingTraffic
        
        if (profile == "cycling")
        {
            mode = .cycling
        }
        else if(profile == "driving-traffic")
        {
            mode = .automobileAvoidingTraffic
        }
        else if(profile == "walking")
        {
            mode = .walking
        }
        else if(profile == "motorcycle")
        {
            mode = .init("mapbox/motorcycle")
        }
        let routeOptions = NavigationRouteOptions(waypoints: waypoints , profileIdentifier: mode)
        routeOptions.shapeFormat = .polyline6
        routeOptions.locale = Locale(identifier: "vi")
        requestRoute(with: routeOptions, success: defaultSuccess, failure: defaultFailure)
    }
    
    fileprivate func requestRoute(with options: RouteOptions, success: @escaping RouteRequestSuccess, failure: RouteRequestFailure?) {
        let handler: Directions.RouteCompletionHandler = {(waypoints, potentialRoutes, potentialError) in
            if let error = potentialError, let fail = failure { return fail(error) }
            guard let routes = potentialRoutes else { return }
            return success(routes)
        }
        let apiUrl = Directions.shared.url(forCalculating: options)
        Directions.shared.calculate(options, completionHandler: handler)
    }
}

// MARK: - NavigationMapViewDelegate
extension VietMapNavigationView: NavigationMapViewDelegate {
    func navigationMapView(_ mapView: NavigationMapView, didSelect waypoint: Waypoint) {
        guard let routeOptions = routes?.first?.routeOptions else { return }
//        let modifiedOptions = routeOptions.without(waypoint: waypoint)
    }

    func navigationMapView(_ mapView: NavigationMapView, didSelect route: Route) {
        guard let routes = routes else { return }
        guard let index = routes.firstIndex(where: { $0 == route }) else { return }
        self.routes!.remove(at: index)
        self.routes!.insert(route, at: 0)
        self.sendEvent(event: self.onNewRouteSelected, data: encodeRoute(route: route))
    }
}

// MARK: - RouteControllerDelegate
extension VietMapNavigationView: RouteControllerDelegate {
    public func routeController(_ routeController: RouteController, didArriveAt waypoint: Waypoint) -> Bool {
        
        let arrivalData: [String: Any] = [
            "latitude": waypoint.coordinate.latitude,
            "longitude": waypoint.coordinate.longitude
        ]
        sendEvent(event: onArrival, data:arrivalData)
        if (restartAlert) {
            // Nếu trước đó có cảnh báo tốc độ đang hoạt động thì restart lại
            startSpeedAlert()
            restartAlert = false
        }
        if(_remainingPointCount == 1)
        {
            suspendNotifications()
        }
        else
        {
            _remainingPointCount -= 1
        }

        return true;
    }
    
    func navigationViewControllerDidDismiss(_ navigationViewController: NavigationViewController, byCanceling canceled: Bool) {
        if (canceled)
        {
            UIApplication.shared.isIdleTimerDisabled = false
            sendEvent(event: onNavigationFinished)
            if (restartAlert) {
                // Nếu trước đó có cảnh báo tốc độ đang hoạt động thì restart lại
                startSpeedAlert()
                restartAlert = false
            }
        }
    }
}
