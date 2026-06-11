
[<img src="https://bizweb.dktcdn.net/100/415/690/themes/804206/assets/logo.png?1689561872933" height="40"/> </p>](https://vietmap.vn/maps-api)
# VietMap React Native Navigation

<img alt="VietMap React Native Navigation" src="./img/ios_nav.jpeg?v=2" width="300" align="right" />

**What is this?** This package adds a complete turn-by-turn navigation map to your React Native app: a live map, route drawing, voice guidance, and real-time progress — powered by [VietMap](https://maps.vietmap.vn/) and optimized for Vietnam.

[Sample demo app shown in the screenshot ➡️](https://github.com/vietmap-company/vietmap-react-native-demo/tree/main/screen/Navigation)

## Features

- A full-fledged turn-by-turn navigation UI for mobile
- Vietnam driving, cycling, motorcycle, and walking directions
- Spoken (voice) turn instructions
- Speed alert support
- Custom markers on the map (`VietMapMarkerView`)

---

## Table of contents

1. [Before you start (checklist)](#1-before-you-start-checklist)
2. [Architecture compatibility — read this first](#2-architecture-compatibility--read-this-first)
3. [Step 1 — Install the package](#3-step-1--install-the-package)
4. [Step 2 — Android setup](#4-step-2--android-setup)
5. [Step 3 — iOS setup](#5-step-3--ios-setup)
6. [Step 4 — Show your first navigation map](#6-step-4--show-your-first-navigation-map)
7. [Step 5 — Run and verify](#7-step-5--run-and-verify)
8. [API reference](#8-api-reference) (props, controller, speed alert, markers)
9. [Troubleshooting](#9-troubleshooting)

---

## 1. Before you start (checklist)

You need these three things. If any item is missing, the SDK will not work:

| ✅ | What | Where to get it |
|---|---|---|
| 1 | **A VietMap API key** | Register at [VIETMAP Maps API](https://maps.vietmap.vn/console/register) or contact [Zalo OA](https://zalo.me/3189066936017422854). Some accounts receive **two keys** — one for the map display (tilemap) and one for routing (navigation). Keep both at hand. |
| 2 | **A React Native app** | An existing app, or create one with `npx @react-native-community/cli init MyApp`. **React Native 0.72.0 or newer** (tested up to 0.85.2) and **React 18 or newer** (tested up to 19.2). |
| 3 | **A real phone for testing** | Navigation uses GPS. Simulators work for map display and simulated routes (`shouldSimulateRoute`), but real navigation needs a real device. |

## 2. Architecture compatibility — read this first

React Native apps run on one of two internal "architectures". You don't need to understand them — you only need to know **which one your app uses**, because it affects how this SDK runs:

| Your app | Supported? | Notes |
|---|---|---|
| **New Architecture** (the default for every app created with React Native **0.76 or newer**) | ✅ **Yes — recommended.** | This SDK is built and tested against the New Architecture. The map renders as a native Fabric component on both Android and iOS. |
| **Old Architecture** (apps that explicitly turned the New Architecture off, or apps on RN 0.72–0.75 that never enabled it) | ✅ Yes | Works through React Native's legacy bridge (the classic view managers are still shipped in this package). If you hit an issue on the Old Architecture, [report it](https://github.com/vietmap-company/vietmap-react-native-navigation/issues) — but we recommend upgrading to the New Architecture, which is where active testing happens. |

**How do I know which architecture my app uses?**

- **Android**: open `android/gradle.properties` and look for the line `newArchEnabled`. `true` = New Architecture, `false` = Old. If your app was created with RN 0.76+, it is `true` by default.
- **iOS**: the New Architecture is on by default from RN 0.76. It is only off if someone in your team deliberately disabled it (e.g. installed pods with `RCT_NEW_ARCH_ENABLED=0`).

> **One iOS-specific note:** on the New Architecture, automatic registration of the map component requires **React Native 0.76 or newer**. On RN 0.72–0.75 with the New Architecture enabled, the SDK falls back to React Native's built-in interop layer — it still works, no action needed from you.

## 3. Setup 

### Step 1 — Install the package

Open a terminal **in your app's root folder** (the folder that contains `package.json`) and run **one** of these:

Using `npm`:
```bash
npm install @vietmap/vietmap-react-native-navigation
```

Using `yarn`:
```bash
yarn add @vietmap/vietmap-react-native-navigation
```

That's it for the JavaScript side. Now configure each platform (next two steps — do **both** if you ship to both platforms).

### Step 2 — Android setup

You will edit **one file**: `android/app/src/main/AndroidManifest.xml`.

**2.1 — Add permissions.** The SDK needs internet (to load maps and routes), location (to navigate), and a foreground service (so navigation keeps running when the screen is off). Paste these lines inside the `<manifest>` tag, **above** the `<application>` tag:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_LOCATION" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
```

**2.2 — Register the navigation service** (required for Android 14 and above; harmless on older versions). Paste this block inside the `<application>` tag:

```xml
<application>
  ...
  <!-- VietMap navigation background service -->
  <service
      android:name="vn.vietmap.services.android.navigation.v5.navigation.NavigationService"
      android:foregroundServiceType="location"
      android:exported="false">
  </service>
</application>
```

**2.3 — Done.** No Gradle changes are needed; the package links automatically.

> The SDK asks the user for location permission by itself the first time the map opens. If the user denies it, navigation cannot start.

### Step 3 — iOS setup

You will edit **two files** (`ios/<YourAppName>/Info.plist` and `ios/Podfile`), then run one command.

**3.1 — Edit `Info.plist`.** Paste the block below inside the top-level `<dict>` tag. Replace **both** occurrences of `YOUR_API_KEY_HERE` with your real VietMap API key, and replace the three "description" texts with a short sentence telling the user why your app needs their location (Apple shows this text in the permission popup and rejects apps with empty/placeholder texts):

```xml
<key>VietMapURL</key>
<string>https://maps.vietmap.vn/api/maps/light/styles.json?apikey=YOUR_API_KEY_HERE</string>
<key>VietMapAPIBaseURL</key>
<string>https://maps.vietmap.vn/api/navigations/route/</string>
<key>VietMapAccessToken</key>
<string>YOUR_API_KEY_HERE</string>
<key>NSLocationAlwaysAndWhenInUseUsageDescription</key>
<string>This app uses your location to guide you to your destination.</string>
<key>NSLocationAlwaysUsageDescription</key>
<string>This app uses your location to guide you to your destination.</string>
<key>NSLocationWhenInUseUsageDescription</key>
<string>This app uses your location to guide you to your destination.</string>
```

**3.2 — (Only if you use the Speed Alert feature)** also add to `Info.plist`:

```xml
<key>UIBackgroundModes</key>
<array>
  <string>location</string>
</array>
```

**3.3 — Edit `ios/Podfile`.** Find the `post_install do |installer|` block near the bottom and add the `system(...)` line inside it:

```ruby
post_install do |installer|
  # ... existing lines, keep them ...

  # Required by VietMap navigation (fixes a header location issue)
  system("cp -R Pods/Headers/Public/VietMapDirections.swift/VietMapDirections.h Pods/Headers/Public/VietMapDirections")
end
```

**3.4 — Install the native dependencies.** From your app's root folder run:

```bash
cd ios && pod install && cd ..
```

If this command finishes without red error text, iOS setup is done.

### Step 4 — Show your first navigation map

Create a new file in your app (for example `NavigationScreen.tsx`), paste this **complete, ready-to-run** code, and put your API key in:

```tsx
import React from 'react';
import { StyleSheet, View } from 'react-native';
import VietMapNavigation, {
  VietMapNavigationController,
} from '@vietmap/vietmap-react-native-navigation';

// 1. PUT YOUR KEY HERE
const VIETMAP_API_KEY = 'YOUR_API_KEY_HERE';

// A fixed starting point for this demo (District 5, Ho Chi Minh City).
const START_POINT = { lat: 10.759156, long: 106.675913 };

export default function NavigationScreen() {
  return (
    <View style={styles.container}>
      <VietMapNavigation
        apiKey={VIETMAP_API_KEY}
        initialLatLngZoom={{ lat: 10.759156, lng: 106.675913, zoom: 15 }}
        // true = the app "drives" along the route by itself. Perfect for testing
        // on a simulator. Set to false for real GPS navigation.
        shouldSimulateRoute={true}
        onMapReady={() => {
          console.log('Map is ready');
        }}
        // 2. LONG-PRESS anywhere on the map to build a route to that point.
        onMapLongClick={(event) => {
          VietMapNavigationController.buildRoute(
            [
              START_POINT,
              {
                lat: event.nativeEvent.data.latitude,
                long: event.nativeEvent.data.longitude,
              },
            ],
            'motorcycle', // or 'driving-traffic' (car) | 'cycling' | 'walking' | 'truck'
          );
        }}
        // 3. When the route is drawn, start navigating right away.
        onRouteBuilt={() => {
          VietMapNavigationController.startNavigation();
        }}
        onArrival={() => {
          console.log('You have arrived!');
        }}
        onNavigationFinished={() => {
          console.log('Navigation finished');
        }}
      />
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1 }, // the map fills whatever space this container has
});
```

Then show this screen from your `App.tsx`. The simplest possible `App.tsx` looks like this:

```tsx
import React from 'react';
import NavigationScreen from './NavigationScreen';

export default function App() {
  return <NavigationScreen />;
}
```

(If your app uses a navigation library like React Navigation, add `NavigationScreen` as a screen instead.)

> **If you have two API keys** (one for the map display, one for routing), pass the map-display one through `styleUrl` and the routing one through `apiKey`:
> ```tsx
> styleUrl={'https://maps.vietmap.vn/api/maps/light/styles.json?apikey=YOUR_TILEMAP_KEY'}
> apiKey={'YOUR_NAVIGATION_KEY'}
> ```

### Step 5 — Run and verify

Run the app:

```bash
# Android
npx react-native run-android

# iOS
npx react-native run-ios
```

Now walk through this checklist — each line confirms one part of the setup:

| ✓ | What you should see | If it fails |
|---|---|---|
| 1 | The app asks for location permission | Re-check Step 2 (Android permissions) / Step 3.1 (iOS plist) |
| 2 | A map of Vietnam appears | Wrong or missing API key → re-check `apiKey` (and `VietMapURL` on iOS) |
| 3 | Long-press the map → a route line is drawn | Routing key invalid → check `apiKey` / contact VietMap |
| 4 | Navigation starts and the camera follows the route (simulated) | Check `shouldSimulateRoute={true}` and the `onRouteBuilt` callback |

All four pass? **Integration is complete.** Everything below is reference material for building your real screens.

---

## 4. API reference

### 4.1 `VietMapNavigation` Props

#### `apiKey` (**Required**)

API key used for **navigation/routing** (the directions service). If you don't provide `styleUrl`, this key is also used to build the default tilemap style URL.

#### `styleUrl`

Full VietMap style URL for the **tilemap** (basemap), including its own apikey query parameter.

Use this when VietMap issued you **two separate keys** — one for the tilemap and one for navigation — or when you need to point at a different style endpoint than the SDK default. When omitted, the SDK falls back to a light style built from `apiKey` (backward compatible).

```tsx
  styleUrl={'https://maps.vietmap.vn/api/maps/light/styles.json?apikey=YOUR_TILEMAP_KEY'}
  apiKey={'YOUR_NAVIGATION_KEY'}
```

#### `baseUrl`

Custom base URL for the navigation API endpoint. Use this if your deployment points at a self-hosted or region-specific VietMap server.

#### `apiKeyAlert`

Speed-alert API key, equivalent to calling `VietMapNavigationController.configureAlertAPI(apiKey, apiKeyId)`. You can pass it directly as a prop instead of calling the controller method.

#### `apiIDAlert`

Speed-alert API key ID, used together with `apiKeyAlert`.

#### `initialLatLngZoom` (**Required**)

Object `InitialLatLngZoom` that contains the longitude and latitude for the initial zoom.<br>
```tsx
  {
    lat: number;  // -90 to 90 degrees
    lng: number;  // -180 to 180 degrees
    zoom: number; // 0 to 21
  }
```

> **Note:** `initialLatLngZoom` uses the key `lng` for longitude, while `buildRoute` coordinates use the key `long`. Make sure you use the correct key for each.

**SDK will auto detect current location of user and move the map to this location, the zoom level will match with provided**

#### `shouldSimulateRoute`

Boolean that controls route simulation. Set this as `true` to auto navigate which is useful for testing or demo purposes. Defaults to `false`.

#### `navigationZoomLevel`

Zoom level while user in navigation.

#### `navigationTiltAnchor`

Vertical anchor (0–1) that controls the camera's tilt focal point during navigation. Useful to shift the perspective so the route ahead takes up more of the screen.

#### `puckImage`

URI of a custom image to use as the user-location puck (the icon that follows the user's position). Use `Image.resolveAssetSource(require('./your-icon.png')).uri` to resolve a local asset.

```tsx
  puckImage={Image.resolveAssetSource(require('./assets/navigation.png')).uri}
```

#### `puckImageWidth`

Width in points/dp of the custom puck image. Defaults to the image's natural size when omitted.

#### `puckImageHeight`

Height in points/dp of the custom puck image. Defaults to the image's natural size when omitted.

#### `puckImageRotation`

Initial rotation (in degrees) applied to the custom puck image. The SDK will override this with the actual bearing during navigation.

#### `style`

React native style for the `VietMapNavigation` react native component

#### `onRouteProgressChange?: (event: NavigationProgressData) => void;`

This callback will response a `NavigationProgressData` model, which contain all data of current navigation progress. More details for `NavigationProgressData` model description [here](./NavigationProgressData.md)

#### `onCancelNavigation` — ⛔ Deprecated

**Deprecated — this event is never emitted on either platform.** Use [`onNavigationCancelled`](#onnavigationcancelled) instead. Kept only so existing code keeps compiling.

#### `onRouteBuilt?: (event: RouteData) => void;`

This callback will response a `RouteData` model, which contain all data of the route.
This will call each time the route was built (first time user build, user off-route and fetch new route)

#### `onMapClick`

This callback will response `LocationData` model, which contain `latitude`, `longitude`. This will called when user click on the map.

#### `onMapLongClick?: (event: LocationData) => void;`

This callback will response `LocationData` model, which contain `latitude`, `longitude`. This will called when user long click on the map.

#### `onMapMove`

This callback will run when user move the map

#### `onMapMoveEnd`

This callback will run when user move the map end

#### `onNavigationFinished`

Fires whenever the guidance session ends, for any reason. The events you receive tell you *how* it ended:

| How navigation ended | Events you receive (in order) |
|---|---|
| User/app stopped it mid-route (`finishNavigation()`) | `onNavigationCancelled` → `onNavigationFinished` |
| User reached the destination | `onArrival` → `onNavigationFinished` |
| Internal reroute (user went off-route, or a faster route was applied) | *(neither — only `onUserOffRoute`; the session keeps running)* |

#### `onNavigationCancelled`

Fires **right before** `onNavigationFinished` when navigation is stopped **before reaching the destination** (the user or your app called `VietMapNavigationController.finishNavigation()` mid-route). It is *not* fired on arrival, and *not* fired during internal reroutes.

#### `onNavigationRunning`

This function will call while the navigation is running

#### `onRouteBuildFailed`

This function will call while the route build failed

#### `onRouteBuilding`

This function will call while the route is building

#### `onMapReady`

This function will call while the map is initial successfully

#### `onMilestoneEvent?: (event: MilestoneData) => void;`

This function will call while have any voice need be play, if the mute receive `true`
value, the SDK will automatically play this voice. The voice data will provide in
`MilestoneData` model

#### `onUserOffRoute?: (event: LocationData) => void;`

This function will call while user is off-route. The location when user off-route will contain in `LocationData` model

#### `onArrival`

This function will call while user is arrival at the destination.

The SDK will automatically stop the navigation when user arrival at the last waypoint.

#### `onWaypointArrival?: (event: LocationData) => void;`

This function is called each time the user arrives at an **intermediate waypoint** (not the final destination). Use this to distinguish waypoint arrivals from the final `onArrival` event when navigating multi-leg routes.

#### `onNewRouteSelected?: (event: RouteData) => void;`

This function will call while user select a new route, cause VietMap SDK will find one or more route between two points. When user select new route, the `RouteData` will response.

### 4.2 `VietMapNavigationController` — control the navigation from your own buttons

```tsx
import { VietMapNavigationController } from '@vietmap/vietmap-react-native-navigation';
```

| Method | What it does |
|---|---|
| `buildRoute(points, profile)` | Draws a route through the given points (`[{lat, long}, ...]`, first = start, last = destination). `profile` is one of `'driving-traffic'` (car), `'motorcycle'`, `'cycling'`, `'walking'`. |
| `startNavigation()` | Starts turn-by-turn guidance on the built route. |
| `finishNavigation()` | Stops the current guidance session. Fires `onNavigationCancelled` (if stopped mid-route) followed by `onNavigationFinished`. |
| `recenter()` | Snaps the camera back to the user after they dragged the map. |
| `overView()` | Zooms out to show the whole route. |
| `clearRoute()` | Removes the drawn route from the map. |

Example — a simple control bar:

```tsx
<View style={{ flex: 1, flexDirection: 'row' }}>
  <View key={'navigation'} style={{ flex: 1 }}>
    <Pressable style={buttonStyle} onPress={() =>
      VietMapNavigationController.buildRoute(
        [
          { lat: 10.759156, long: 106.675913 },
          { lat: 10.762622, long: 106.682220 },
        ],
        'motorcycle'
      )
    }>
      <Text>Build route</Text>
    </Pressable>

    <Pressable style={buttonStyle} onPress={() => VietMapNavigationController.startNavigation()}>
      <Text>Start navigation</Text>
    </Pressable>

    <Pressable style={buttonStyle} onPress={() => VietMapNavigationController.finishNavigation()}>
      <Text>Stop navigation</Text>
    </Pressable>

    <Pressable style={buttonStyle} onPress={() => VietMapNavigationController.recenter()}>
      <Text>Recenter</Text>
    </Pressable>

    <Pressable style={buttonStyle} onPress={() => VietMapNavigationController.overView()}>
      <Text>Overview</Text>
    </Pressable>

    <Pressable onPress={() => VietMapNavigationController.clearRoute()}>
      <Text>Clear route</Text>
    </Pressable>
  </View>
</View>
```

### Important: wait for the map before calling the controller

All functions of `VietMapNavigationController` must be called **after** the `VietMapNavigation` component is mounted and the map is ready. Calling them before the map is ready will crash your app.

The safe places to call them are the `onMapReady` callback or a button the user presses after the map appeared:

```tsx
  <VietMapNavigation
    onMapReady={() => {
      VietMapNavigationController.buildRoute(
        [
          { lat: 10.759156, long: 106.675913 },
          { lat: 10.762622, long: 106.682220 },
        ],
        'motorcycle'
      )
    }}
  />
```

### Reading navigation data (for building your own UI)

The texts below come from the `onRouteProgressChange` callback.

**Current instruction text** (e.g. "Turn left onto Nguyen Trai"):
```tsx
  event?.nativeEvent?.data?.currentStepInstruction
```

**Turn direction guide** (for showing a turn arrow image) — responds in two variables: `currentModifier` and `currentModifierType`:
```tsx
  let modifier = event?.nativeEvent?.data?.currentModifier
  let type = event?.nativeEvent?.data?.currentModifierType
```
Replace all `space` with `_` and join the two variables to get the turn key. Look the key up in the [image direction guide](https://www.figma.com/file/rWyQ5TNtt6E5l8tPEE9Tkl/VietMap-navigation-symbol?type=design&node-id=0-1&t=HeZNcRIAprzQ60ih-0) and [text direction guide](./example/turn_direction_description.json).

**Distance to the next turn** (meters):
```tsx
  event?.nativeEvent?.distanceToNextTurn
```

**Distance and estimated time to the destination:**
```tsx
  /// The distance remaining to the destination, measured in meters
  event?.nativeEvent?.distanceRemaining
  /// The time estimated to traveled the destination, measured in seconds
  event?.nativeEvent?.durationRemaining
  /// The distance user traveled from the origin point, measured in meters
  event?.nativeEvent?.distanceTraveled
```

### 4.3 Speed Alert API

You can control the speed alert feature using the following methods from `VietMapNavigationController`:

#### Step 1: Configure Speed Alert API

Before using speed alert features, you need to configure the API with your credentials. **Important: These configurations should be called in `useEffect` to ensure proper initialization:**

```tsx
import React, { useEffect } from 'react';
import { VietMapNavigationController } from '@vietmap/vietmap-react-native-navigation';

const YourNavigationComponent = () => {
  useEffect(() => {
    // Configure speed alert API with your credentials
    VietMapNavigationController.configureAlertAPI("YOUR_API_KEY_HERE", "YOUR_API_KEY_ID_HERE");
  }, []);

  return (
    // Your navigation component JSX
  );
};
```

**Parameters:**
- `apiKey`: Your VietMap API key for speed alert service
- `apiKeyId`: Your VietMap API key ID for speed alert service

#### Step 2: Configure Vehicle Information

Configure the vehicle information for accurate speed alert calculations. **This should also be called in `useEffect` along with Step 1:**

```tsx
import React, { useEffect } from 'react';
import { VietMapNavigationController, VehicleType } from '@vietmap/vietmap-react-native-navigation';

const YourNavigationComponent = () => {
  useEffect(() => {
    // Step 1: Configure speed alert API
    VietMapNavigationController.configureAlertAPI("YOUR_API_KEY_HERE", "YOUR_API_KEY_ID_HERE");

    // Step 2: Configure vehicle for speed alert
    VietMapNavigationController.configVehicleSpeedAlert("VEHICLE_ID", VehicleType.truck, 5, 1500);
  }, []);

  return (
    // Your navigation component JSX
  );
};
```

**Parameters:**
- `vehicleId`: Unique identifier for the vehicle (string)
- `vehicleType`: Type of vehicle using `VehicleType` enum:
  - `VehicleType.car` - Xe ô tô
  - `VehicleType.taxi` - Xe taxi
  - `VehicleType.bus` - Xe bus
  - `VehicleType.coach` - Xe khách
  - `VehicleType.truck` - Xe tải
  - `VehicleType.trailer` - Xe remooc
  - `VehicleType.cycle` - Xe mô tô
  - `VehicleType.bike` - Xe đạp
  - `VehicleType.pedestrian` - Người đi bộ
  - `VehicleType.semiTrailer` - Xe sơ mi rơ moóc
- `seats`: Number of seats in the vehicle
- `weight`: Vehicle weight in kg

> **Important Notes:**
> - Both `configureAlertAPI` and `configVehicleSpeedAlert` must be called in `useEffect` to ensure proper initialization
> - Call these functions before using any speed alert features
> - These configurations are typically called once when the component mounts

#### Start Speed Alert

```tsx
import { VietMapNavigationController } from '@vietmap/vietmap-react-native-navigation';

// Start speed alert manually
VietMapNavigationController.startSpeedAlert();
```

**Speed Alert Behavior:**

- **Automatic Mode (During Navigation)**: When you start navigation, speed alert will automatically run and will automatically stop when navigation ends. You don't need to manually call `startSpeedAlert()` during navigation.

- **Manual Mode (Without Navigation)**: If you call `startSpeedAlert()` manually, speed alert will run even when not in navigation mode. This is useful for:
  - Speed monitoring during free-drive mode
  - Testing speed alert functionality
  - Custom speed monitoring scenarios outside of navigation

**Important Notes:**
- Manual speed alert will continue running until you explicitly call `stopSpeedAlert()` or start a navigation session
- During navigation, the system automatically manages speed alert lifecycle
- You can check if speed alert is currently active using `isSpeedAlertActive()`

#### Stop Speed Alert

```tsx
import { VietMapNavigationController } from '@vietmap/vietmap-react-native-navigation';

// Stop speed alert
VietMapNavigationController.stopSpeedAlert();
```

#### Check Speed Alert Status

```tsx
import { VietMapNavigationController } from '@vietmap/vietmap-react-native-navigation';

// Check if speed alert is currently active
const isActive = await VietMapNavigationController.isSpeedAlertActive();
console.log('Speed alert is active:', isActive);

// You can also use it in an async function
const checkSpeedAlertStatus = async () => {
  try {
    const isActive = await VietMapNavigationController.isSpeedAlertActive();
    if (isActive) {
      console.log('Speed alert is currently running');
    } else {
      console.log('Speed alert is not active');
    }
  } catch (error) {
    console.error('Error checking speed alert status:', error);
  }
};
```

> **Note:**
> - You should call these methods after the map is ready (e.g. in a button or in the `onMapReady` callback).
> - On Android and iOS, these methods will start/stop the SDK's speed alert system (voice, UI, or notification depending on platform).
> - **iOS Requirement:** For speed alert to work properly on iOS, you must add `UIBackgroundModes` with `location` to your Info.plist file (see [Step 3](#5-step-3--ios-setup), item 5.2).

### 4.4  VietMapMarkerView

`VietMapMarkerView` lets you place any React Native view as a marker directly on the navigation map. The marker stays pinned to its geographic coordinate as the camera moves.

> **Note:** The marker is display-only — it does not receive tap/press events. On Android the native side reparents the marker view into the map's view hierarchy (so the map keeps it positioned across camera moves), which takes it out of React Native's touch system; touchables placed inside the marker content won't fire. To add/remove markers, manage the array you render from JS (e.g. add on `onMapClick`, remove via your own UI/button).

#### Import

```tsx
import { VietMapMarkerView } from '@vietmap/vietmap-react-native-navigation';
```

#### Basic Usage

```tsx
<VietMapNavigation ... >
  {/* VietMapMarkerView must be rendered as children of VietMapNavigation */}
</VietMapNavigation>

{/* Place a marker on the map */}
<VietMapMarkerView
  coordinate={[106.675913, 10.759156]}  // [longitude, latitude]
  anchor={{ x: 0.5, y: 1 }}
>
  <Image source={require('./assets/location.png')} style={{ width: 28, height: 28 }} />
</VietMapMarkerView>
```

#### Multiple Markers Example

```tsx
const [markers, setMarkers] = useState<{ id: string; lat: number; lng: number }[]>([]);
const markerIdRef = useRef(0);

// Drop a marker on every map tap. Give each a stable `id` (not the array index) so React keeps
// each marker's identity when the list changes.
<VietMapNavigation
  ...
  onMapClick={(event) => {
    const { latitude, longitude } = event.nativeEvent.data;
    const id = `marker-${markerIdRef.current++}`;
    setMarkers(prev => [...prev, { id, lat: latitude, lng: longitude }]);
  }}
/>

{markers.map((m, index) => (
  <VietMapMarkerView
    key={m.id}
    id={m.id}
    coordinate={[m.lng, m.lat]}
    anchor={{ x: 0.5, y: 1 }}
  >
    <View style={{ width: 28, height: 28, alignItems: 'center', justifyContent: 'center' }}>
      <Text style={{ position: 'absolute', top: 0, color: 'white', fontWeight: 'bold' }}>
        {index + 1}
      </Text>
      <Image source={require('./assets/location.png')} style={{ width: 28, height: 25 }} />
    </View>
  </VietMapMarkerView>
))}

// Remove all markers
setMarkers([]);
// Remove one marker by id
setMarkers(prev => prev.filter(m => m.id !== someId));
```

#### Props

##### `coordinate` (**Required**)

The geographic position of the marker. Accepts either a `[longitude, latitude]` tuple or an object `{ latitude, longitude }`.

```tsx
coordinate={[106.675913, 10.759156]}        // tuple form — [lng, lat]
coordinate={{ latitude: 10.759156, longitude: 106.675913 }}  // object form
```

> **Note:** The tuple form uses `[longitude, latitude]` order (GeoJSON convention).

##### `anchor`

Controls which point of the marker view sits on the coordinate. Values are in `[0..1]` from the view's top-left corner. Default: `{ x: 0.5, y: 1 }` (bottom-center — a pin tip touches the coordinate).

##### `id`

Optional stable string ID for the marker. Auto-generated when omitted. Pass your own stable id and use it as the React `key` so the right marker is removed when you filter the list.

##### `children` (**Required**)

Exactly one React Native element to render as the marker content. Can be a container `<View>` with multiple children inside.

---

## 5. Troubleshooting

### The map area is blank / white

- Check your **API key** (`apiKey` prop, and `VietMapURL` in `Info.plist` on iOS).
- Check the device has internet access.
- Make sure the map's parent `<View>` has a size (e.g. `style={{ flex: 1 }}`). A container with zero height shows nothing.

### "Invariant Violation: View config getter callback for component `VietMapNavigation` must be a function (received `undefined`)"

This means the JavaScript bundle was built without the SDK's component registration. In a normal app install this should not happen (the package ships its source and registers automatically). If you see it:

1. Stop the Metro bundler and restart it with a clean cache: `npx react-native start --reset-cache`, then rebuild the app.
2. Make sure you have only **one** copy of `react-native` in your project (`npm ls react-native` must show a single version). Monorepos with multiple `node_modules` folders are the usual cause.

### iOS Build Error: Multiple commands produce Assets.car

If you encounter this error during iOS build:
```
❌ error: Multiple commands produce '.../Build/Products/Debug-iphonesimulator/example.app/Assets.car'
```

**Solution:** Add the following code to your `Podfile` in the `post_install` block:

```ruby
post_install do |installer|
  # ...existing code...

  # Fix duplicate Assets.car issue by updating resource references
  resources_script_path = File.join(installer.sandbox.root, 'Target Support Files/Pods-example/Pods-example-resources.sh')
  debug_input_path = File.join(installer.sandbox.root, 'Target Support Files/Pods-example/Pods-example-resources-Debug-input-files.xcfilelist')
  release_input_path = File.join(installer.sandbox.root, 'Target Support Files/Pods-example/Pods-example-resources-Release-input-files.xcfilelist')

  # Remove VietMapNavigation Assets.xcassets references from all files
  [resources_script_path, debug_input_path, release_input_path].each do |file_path|
    if File.exist?(file_path)
      content = File.read(file_path)
      # Remove lines containing VietMapNavigation Assets.xcassets
      updated_content = content.gsub(/.*VietMapNavigation.*Assets\.xcassets.*\n/, '')
      File.write(file_path, updated_content)
    end
  end

  # ...existing code...
end
```

**Note:** Replace `Pods-example` with your actual target name if different.

After adding this code, run:
```bash
cd ios && pod install && cd ..
```

Then clean and rebuild your project:
```bash
npx react-native run-ios --clean
```

### The app crashes when I call a `VietMapNavigationController` method

You called it before the map was ready?. Move the call into the `onMapReady` callback or behind a user button — see ["Important: wait for the map"](#important-wait-for-the-map-before-calling-the-controller).

## License

The source code in this library is [BSD-3-Clause](LICENSE) licensed.

</br>
</br>

[<img src="https://bizweb.dktcdn.net/100/415/690/themes/804206/assets/logo.png?1689561872933" height="40"/> </p>](https://vietmap.vn/maps-api)

Vietmap API and price [here](https://maps.vietmap.vn/web#pricingSection)

Contact for [support](https://maps.vietmap.vn/web)

Vietmap API document [here](https://maps.vietmap.vn/docs/)

Have a bug to report? [Open an issue](https://github.com/vietmap-company/vietmap-react-native-navigation/issues).</br> If possible, include a full log and information that shows the issue.


Have a feature request? [Open an issue](https://github.com/vietmap-company/vietmap-react-native-navigation/issues). </br>Tell us what the feature should do and why you want the feature.
