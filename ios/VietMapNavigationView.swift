import Foundation
import UIKit
import VietMapDirections
import VietMapNavigation
import VietMapCoreNavigation

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
    
    func addToWindow()  {
        let window = UIApplication.shared.keyWindow!
        self.frame = window.bounds
        window.addSubview(self)
    }
}

@objcMembers class VietMapNavigationView: UIView, NavigationViewControllerDelegate, MGLMapViewDelegate {
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
    
    @objc var routeController: RouteController?
    
    // MARK: - define parameter from RN
    @objc var shouldSimulateRoute: Bool = false
    @objc var initialLatLngZoom: NSDictionary = [:]
    @objc var navigationPadding: NSDictionary = [:]
    @objc var navigationZoomLevel: Double = 10
    
    // MARK: - define Event Block from RN
    @objc var onRouteProgressChange: RCTDirectEventBlock?
    @objc var onError: RCTDirectEventBlock?
    @objc var onNavigationFinished: RCTDirectEventBlock?
    @objc var onArrival: RCTDirectEventBlock?
    @objc var userOffRoute: RCTDirectEventBlock?
    @objc var onRouteBuilt: RCTDirectEventBlock?
    @objc var onRouteBuildFailed: RCTDirectEventBlock?
    @objc var onMapLongClick: RCTDirectEventBlock?
    @objc var onMapClick: RCTDirectEventBlock?
    @objc var onMapMove: RCTDirectEventBlock?
    @objc var onMapMoveEnd: RCTDirectEventBlock?
    @objc var onNewRouteSelected: RCTDirectEventBlock?
    
    override init(frame: CGRect) {
        self.embedded = false
        self.embedding = false
        super.init(frame: frame)
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
        navigationMapView.recenterMap()
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
            suspendNotifications()
            sendEvent(event: onNavigationFinished)
            
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
        // Update the user puck
        let camera = MGLMapCamera(lookingAtCenter: location.coordinate, altitude: altitudeForZoomLevel(zoomLevel: navigationZoomLevel), pitch: 60, heading: location.course)
        navigationMapView.updateCourseTracking(location: location, camera: camera, animated: true)
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
            sendEvent(event: userOffRoute, data: encodeLocation(location: locationData!, position: nil))
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
        if let initLat = initialLatLngZoom["lat"] as? Double, let initLong = initialLatLngZoom["long"] as? Double {
            let initialCoordinate = CLLocationCoordinate2D(latitude: initLat, longitude: initLong)
            let zoomLevel: Double = initialLatLngZoom["zoom"] as? Double ?? 6
            
            mapView.setCenter(initialCoordinate, zoomLevel: zoomLevel, animated: true)
        }
    }
    
    // MARK: - define response
    fileprivate lazy var defaultSuccess: RouteRequestSuccess = { [weak self] (routes) in
        guard let strongSelf = self else { return }
        strongSelf.routes = routes
        guard let current = routes.first else { return }
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
        guard location.count == 2 else { return }
        let originWaypoint = Waypoint(coordinate: CLLocationCoordinate2D(latitude: (location[0] as! NSArray)[0] as! CLLocationDegrees, longitude: (location[0] as! NSArray)[1] as! CLLocationDegrees))
        let destinationWaypoint = Waypoint(coordinate: CLLocationCoordinate2D(latitude: (location[1] as! NSArray)[0] as! CLLocationDegrees, longitude: (location[1] as! NSArray)[1] as! CLLocationDegrees))
        
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
        let routeOptions = NavigationRouteOptions(waypoints: [originWaypoint, destinationWaypoint], profileIdentifier: mode)
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
        sendEvent(event: onArrival)
        suspendNotifications()
        return true;
    }
    
    func navigationViewControllerDidDismiss(_ navigationViewController: NavigationViewController, byCanceling canceled: Bool) {
        if (canceled)
        {
            UIApplication.shared.isIdleTimerDisabled = false
            sendEvent(event: onNavigationFinished)
        }
    }
}
