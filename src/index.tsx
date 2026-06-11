import * as React from 'react';
import { StyleSheet } from 'react-native';

import { IVietMapNavigationProps } from './typings';
import RNVietMapNavigation from './VietMapNavigationNativeComponent';

// Every event prop of the native component. Handlers for these are wrapped so the payload shape
// is identical on every architecture (see normalizeEventHandler).
const EVENT_PROPS = [
  'onRouteProgressChange',
  'onCancelNavigation',
  'onRouteBuilt',
  'onMapLongClick',
  'onMapMove',
  'onMapMoveEnd',
  'onNavigationFinished',
  'onNavigationCancelled',
  'onNavigationRunning',
  'onRouteBuildFailed',
  'onRouteBuilding',
  'onMapReady',
  'onMilestoneEvent',
  'onUserOffRoute',
  'onArrival',
  'onWaypointArrival',
  'onNewRouteSelected',
  'onMapClick',
] as const;

// On iOS Fabric (New Architecture) the native side delivers the event payload as a JSON string in
// `nativeEvent.json` (codegen C++ emitters only forward declared fields, and the rich payloads are
// impractical to type field-by-field). Android and iOS Paper/interop deliver the dictionary
// payload directly. Parse the JSON form back so apps always see the same
// `event.nativeEvent.data...` shape regardless of platform/architecture.
const normalizeEventHandler = (handler: (e: any) => void) => (e: any) => {
  const nativeEvent = e?.nativeEvent;
  if (nativeEvent && typeof nativeEvent.json === 'string') {
    let parsed: any = {};
    try {
      parsed = JSON.parse(nativeEvent.json);
    } catch {
      // Malformed payload — deliver an empty event rather than crashing the handler.
    }
    handler({ nativeEvent: parsed });
  } else {
    handler(e);
  }
};

const VietMapNavigation = (props: IVietMapNavigationProps) => {
  const wrappedProps: any = { ...props };
  for (const eventName of EVENT_PROPS) {
    const handler = (props as any)[eventName];
    if (typeof handler === 'function') {
      wrappedProps[eventName] = normalizeEventHandler(handler);
    }
  }
  return <RNVietMapNavigation style={styles.container} {...wrappedProps} />;
};

const styles = StyleSheet.create({
  container: {
    flex: 1
  },
});
// export * from './models/route_progress_model'
export * from './enums/vietmap_event_type';
export * from './models/route_progress_data';
export {VietMapNavigationModule} from './native_modules';
export * from './models/coordinates';
export * from './typings';
export {VietMapNavigationController, VehicleType} from './controller';
export {VietMapMarkerView} from './components/VietMapMarkerView';
export type {
  VietMapMarkerViewProps,
  MarkerCoordinate,
} from './components/VietMapMarkerView';
export default VietMapNavigation;
