/** @type {[number, number]}
 * Provide an array with longitude and latitude [$longitude, $latitude]
 */
import { NavigationProgressData } from "./models/route_progress_data";
import { RouteData } from "./models/route_data"; 

type OnLocationChangeEvent = {
  nativeEvent?: {
    latitude: number;
    longitude: number;
  };
}; 
type OnErrorEvent = {
  nativeEvent?: {
    message?: string;
  };
};
type LocationData = {
  nativeEvent: {
    data: {
      latitude: number;
      longitude: number;
      x?: string | null;
      y?: string | null;
    };
  };
};
type NavigationPadding = {
  left: number;
  top: number;
  right: number;
  bottom: number;
};
type InitialLatLngZoom = {
  lat: number;
  lng: number;
  zoom: number;
};


interface MilestoneData {
  nativeEvent: {
    data: {
      instruction: string;
    };
    eventType: string;
  };
}

export interface IVietMapNavigationProps {
  baseUrl?: string;
  apiKey: string;
  /**
   * Full VietMap style URL for the tilemap (including the tilemap apikey query param).
   * Use this when your tilemap key differs from the navigation `apiKey`, or to point at a
   * different style endpoint. When omitted, the SDK falls back to a light style built from `apiKey`.
   */
  styleUrl?: string;
  apiKeyAlert?: string;
  apiIDAlert?: string;
  puckImage?: string;
  puckImageWidth?: number;
  puckImageHeight?: number;
  puckImageRotation?: number;
  shouldSimulateRoute?: boolean;
  initialLatLngZoom?: InitialLatLngZoom;
  navigationZoomLevel?: number;
  navigationTiltAnchor?: number;
  navigationPadding?: NavigationPadding;
  /// This is React component style, not a native style
  style?: object; 
  onRouteProgressChange?: (event: NavigationProgressData) => void;
  /**
   * @deprecated Never emitted by either platform. Use {@link IVietMapNavigationProps.onNavigationCancelled}
   * instead, which fires when an in-progress navigation is stopped before reaching the destination.
   */
  onCancelNavigation?: () => void;
  onRouteBuilt?: (event: RouteData) => void;
  onMapLongClick?: (event: LocationData) => void;
  onMapMove?: () => void;
  onMapMoveEnd?: () => void;
  /**
   * Fires whenever the guidance session ends, for any reason:
   * - stopped mid-route by the user/app → preceded by `onNavigationCancelled`
   * - destination reached → preceded by `onArrival`
   * Internal reroutes (off-route or faster-route refresh) do NOT fire this.
   */
  onNavigationFinished?: () => void;
  /**
   * Fires right before `onNavigationFinished` when navigation is stopped BEFORE reaching the
   * destination (user/app called `finishNavigation()` mid-route). Not fired on arrival, nor on
   * internal reroutes.
   */
  onNavigationCancelled?: () => void;
  onNavigationRunning?: () => void;
  onRouteBuildFailed?: () => void;
  onRouteBuilding?: () => void;
  onMapReady?: () => void;
  onMilestoneEvent?: (event: MilestoneData) => void;
  onUserOffRoute?: (event: LocationData) => void;
  onArrival?: (event: LocationData) => void;
  onWaypointArrival?: (event: LocationData) => void;
  onNewRouteSelected?: (event: RouteData) => void;
  onMapClick?: (event: LocationData) => void;
}
 