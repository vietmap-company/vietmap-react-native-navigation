import type { HostComponent, ViewProps } from 'react-native';
import type {
  DirectEventHandler,
  Double,
} from 'react-native/Libraries/Types/CodegenTypes';
import codegenNativeComponent from 'react-native/Libraries/Utilities/codegenNativeComponent';

type LatLngZoom = Readonly<{ lat: Double; lng: Double; zoom: Double }>;
type Padding = Readonly<{
  left: Double;
  top: Double;
  right: Double;
  bottom: Double;
}>;

// Event payload contract:
// - iOS Fabric (New Architecture): the native component view serializes the full event payload
//   to a JSON string in `json`; the JS wrapper in index.tsx parses it back, so apps keep
//   receiving the same `event.nativeEvent.data` shape as before.
// - Android and iOS Paper/interop: events bypass the codegen C++ emitters and deliver the rich
//   dictionary payload directly (no `json` field) — hence it is optional.
type GenericEvent = Readonly<{ json?: string }>;

export interface NativeProps extends ViewProps {
  apiKey: string;
  baseUrl?: string;
  styleUrl?: string;
  apiKeyAlert?: string;
  apiIDAlert?: string;
  puckImage?: string;
  puckImageWidth?: Double;
  puckImageHeight?: Double;
  puckImageRotation?: Double;
  shouldSimulateRoute?: boolean;
  initialLatLngZoom?: LatLngZoom;
  navigationZoomLevel?: Double;
  navigationTiltLevel?: Double;
  navigationPadding?: Padding;
  onRouteProgressChange?: DirectEventHandler<GenericEvent>;
  onCancelNavigation?: DirectEventHandler<GenericEvent>;
  onRouteBuilt?: DirectEventHandler<GenericEvent>;
  onMapLongClick?: DirectEventHandler<GenericEvent>;
  onMapMove?: DirectEventHandler<GenericEvent>;
  onMapMoveEnd?: DirectEventHandler<GenericEvent>;
  onNavigationFinished?: DirectEventHandler<GenericEvent>;
  onNavigationCancelled?: DirectEventHandler<GenericEvent>;
  onNavigationRunning?: DirectEventHandler<GenericEvent>;
  onRouteBuildFailed?: DirectEventHandler<GenericEvent>;
  onRouteBuilding?: DirectEventHandler<GenericEvent>;
  onMapReady?: DirectEventHandler<GenericEvent>;
  onMilestoneEvent?: DirectEventHandler<GenericEvent>;
  onUserOffRoute?: DirectEventHandler<GenericEvent>;
  onArrival?: DirectEventHandler<GenericEvent>;
  onWaypointArrival?: DirectEventHandler<GenericEvent>;
  onNewRouteSelected?: DirectEventHandler<GenericEvent>;
  onMapClick?: DirectEventHandler<GenericEvent>;
}

export default codegenNativeComponent<NativeProps>(
  'VietMapNavigation'
) as HostComponent<NativeProps>;
