import React, { useMemo, type ReactElement } from 'react';
import {
  Platform,
  requireNativeComponent,
  StyleSheet,
  View,
  type ViewProps,
} from 'react-native';

/** Native component name — must match REACT_CLASS (Android) and RCT_EXPORT_MODULE (iOS). */
export const NATIVE_NAME = 'VietMapMarkerView';

/** Accepts a [lng, lat] tuple or a {latitude, longitude} object. */
export type MarkerCoordinate =
  | [number, number]
  | { latitude: number; longitude: number };

interface NativeMarkerProps extends ViewProps {
  markerId: string;
  /** [longitude, latitude] */
  coordinate: number[];
  anchor?: { x: number; y: number };
  // Children reach the native side as subviews, not as a prop; keep this optional so tsc does
  // not demand a `children` attribute on the host component JSX (public API still requires it
  // via VietMapMarkerViewProps).
  children?: React.ReactNode;
}

const NativeMarkerView =
  requireNativeComponent<NativeMarkerProps>(NATIVE_NAME);

export interface VietMapMarkerViewProps {
  /** Optional stable id; auto-generated when omitted. */
  id?: string;
  coordinate: MarkerCoordinate;
  /**
   * Anchor point in [0..1] x [0..1] from the marker's top-left. Default {x:0.5, y:1}
   * (bottom-center) so a pin's tip sits on the coordinate.
   */
  anchor?: { x: number; y: number };
  /** Exactly one child — the marker content (can be a container with many elements). */
  children: ReactElement;
}

function toLngLat(c: MarkerCoordinate): [number, number] {
  return Array.isArray(c) ? c : [c.longitude, c.latitude];
}

let lastId = 0;

/**
 * Places a real React Native view as a marker on the VietMap navigation map. The native side
 * hosts the child view directly (no bitmap conversion) and keeps it pinned to `coordinate` as the
 * camera moves.
 *
 * Note: rendering arbitrary heavy content is the app's responsibility — if you need a rasterized
 * image, convert it yourself and pass an <Image/> child.
 */
export const VietMapMarkerView = ({
  id,
  coordinate,
  anchor = { x: 0.5, y: 1 },
  children,
}: VietMapMarkerViewProps) => {
  const markerId = useMemo(() => id ?? `vmnav-marker-${++lastId}`, [id]);

  return (
    <NativeMarkerView
      markerId={markerId}
      coordinate={toLngLat(coordinate)}
      anchor={anchor}
      // Size the native view to its content. On iOS this view IS the annotation view, so if it
      // stretched to full width the map would center the (left-aligned) content half a screen off
      // its coordinate — and that offset grows huge when zoomed out. `position: absolute` makes it
      // wrap its content and stay out of the surrounding RN layout flow.
      style={styles.wrapper}
    >
      {/*
        Android-only: wrap the content in a non-collapsable View. RN's view flattening collapses a
        layout-only container (e.g. a pin <View> with no background) and promotes its children
        (Image, Text) straight onto the marker — the native side then only captures the LAST child
        (the Text), so the image vanishes and an absolutely-positioned label can stretch full-screen.
        `collapsable={false}` guarantees the native marker hosts exactly one real subtree.
        iOS doesn't flatten, so we leave its tree untouched.
      */}
      {Platform.OS === 'android' ? (
        <View collapsable={false}>{children}</View>
      ) : (
        children
      )}
    </NativeMarkerView>
  );
};

const styles = StyleSheet.create({
  wrapper: { position: 'absolute' },
});

export default VietMapMarkerView;
