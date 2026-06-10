
[<img src="https://bizweb.dktcdn.net/100/415/690/themes/804206/assets/logo.png?1689561872933" height="40"/> </p>](https://vietmap.vn/maps-api)
# VietMap React Native Navigation

<img alt="VietMap React Native Navigation" src="./img/ios_nav.jpeg?v=2" width="300" align="right" />

VietMap turn-by-turn routing based on real-time traffic for React Native. A navigation UI ready to drop into your React Native application. [Sample demo usage shown here for the  app in the screenshot](https://github.com/vietmap-company/vietmap-react-native-demo/tree/main/screen/Navigation) ➡️

## Features

- A full-fledged turn-by-turn navigation UI for mobile
- VietNam driving, cycling, motorcycle, and walking directions powered by [VietMap data](https://maps.vietmap.vn/)
 - Sounding turn instructions powered by [Google]( )

## Installation Requirements
- You need an API key from VietMap to show the map, fetch the route, and start navigation.
- **Compatibility**: 
  - React Native: 0.72.0 and above (tested up to 0.80.1)
  - React: 18.0.0 and above (tested up to 19.1.0)
  - **Architecture**: this is a Fabric (codegen) component and is built for the **New Architecture**. Keep `newArchEnabled=true` (Android `gradle.properties` / iOS `RCT_NEW_ARCH_ENABLED=1`) — the bundled example runs with the New Architecture enabled. Do not disable it: the component is not designed for the legacy Paper renderer.
- If you're using a lower version of React Native, please [contact us](mailto:maps-api.support@vietmap.vn) for more information
## Installation

Using `npm`
```bash
  npm install @vietmap/vietmap-react-native-navigation
```

Using `yarn`

```bash
  yarn add @vietmap/vietmap-react-native-navigation
```
---

### Android configuration
- Add below permission to `AndroidManifest.xml` file

Our SDK use Service to run the navigation in the background, so you need to add the below permission to `AndroidManifest.xml` file

```xml
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_LOCATION" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
```
Add below code to AndroidManifest (for android 14 and above).
```xml
  <application>
  ...
    <!-- Add this code block -->
    <service
        android:name="vn.vietmap.services.android.navigation.v5.navigation.NavigationService"
        android:foregroundServiceType="location"
        android:exported="false">
    </service>
  </application>
```

### iOS Specific Instructions
 
Add the below codes to the Info.plist file. Replace the **`YOUR_API_KEY_HERE`** with your API key.
```xml
  <key>VietMapURL</key>
  <string>https://maps.vietmap.vn/api/maps/light/styles.json?apikey=YOUR_API_KEY_HERE</string>
  <key>VietMapAPIBaseURL</key>
  <string>https://maps.vietmap.vn/api/navigations/route/</string>
  <key>VietMapAccessToken</key>
  <string>YOUR_API_KEY_HERE</string>  
  <key>NSLocationAlwaysAndWhenInUseUsageDescription</key>
  <string>Your request location description</string>
  <key>NSLocationAlwaysUsageDescription</key>
  <string>Your request location description</string>
  <key>NSLocationWhenInUseUsageDescription</key>
  <string>Your request location description</string>
```

**For Speed Alert feature:** Add background location mode to Info.plist if you want to use speed alert functionality:
```xml
  <key>UIBackgroundModes</key>
  <array>
    <string>location</string>
  </array>
```

Add the below code to `Podfile`
```ruby
  system("cp -R Pods/Headers/Public/VietMapDirections.swift/VietMapDirections.h Pods/Headers/Public/VietMapDirections")
```
Like below code:
```ruby
  post_install do |installer|
    #...............
    # Add the line here
    system("cp -R Pods/Headers/Public/VietMapDirections.swift/VietMapDirections.h Pods/Headers/Public/VietMapDirections")
    #...............
  end
```
Run `pod install` command
```bash
  cd ios && pod install && cd ..
```

## Usage
### Show navigation view
```tsx
const VietMapNavigationScreen: React.FC<void> = () => {
  return (
    <View style={styles.container}>
      <View style={styles.mapContainer}>
        <VietMapNavigation
          initialLatLngZoom={{
            lat: 12.895131,
            lng: 108.490272,
            zoom: 17,
          }}
          navigationPadding={{
            left: 0,
            top: 0,
            right: 0,
            bottom: 0
          }}
          navigationZoomLevel={17}
          shouldSimulateRoute={true}
          apiKey={'YOUR_API_KEY_HERE'}
          styleUrl={
            'https://maps.vietmap.vn/maps/styles/dm/style.json?apikey=TILE_MAP_API_KEY'
          } 
          onRouteProgressChange={(event) => { 
            console.log('onRouteProgressChange', event.nativeEvent?.data); 
          }}
          onRouteBuildFailed={() => {
            alert('Route build failed');
          }}
          onMapMove={() => {
            console.log('onMapMove');
          }}
          onMilestoneEvent={(event) => {
            console.log('onMilestoneEvent', event.nativeEvent);
          }}
          onArrival={(event) => {
            alert('You have reached your destination');
          }}
          onRouteBuilt={(event) => {
            console.log('onRouteBuilt', event.nativeEvent.data);
          }}
          onMapClick={(event) => {
            console.log('onMapClick', event.nativeEvent.data);

            VietMapNavigationController.buildRoute(
              [
                { lat: 10.759156, long: 106.675913 },
                {
                  lat: event.nativeEvent.data.latitude,
                  long: event.nativeEvent.data.longitude,
                },
              ],
              'motorcycle'
            )
          }}
          onMapLongClick={(event) => {
            console.log('onMapLongClick', event.nativeEvent.data);
          }}
          onCancelNavigation={() => {
            alert('Cancelled navigation event');
          }}
        />
      </View>
    </View>
  );
};

const buttonStyle = {
  height: 50,
  backgroundColor: 'white',
};

const buttonTextStyle = {
  borderColor: 'white',
  color: 'black',
  fontWeight: '700',
};

const styles = StyleSheet.create({
  container: {
    flex: 3,
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'space-between',
    height: '100%',
  },
  mapContainer: {
    flex: 3,
    flexDirection: 'column',
  },
});

export default VietMapNavigationScreen;

```

### `VietMapNavigation` Props

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

#### `onCancelNavigation`

This function will call while user cancel the navigation

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

This function will call while the navigation progress finished

#### `onNavigationCancelled`

This function will call while the navigation progress cancelled

#### `onNavigationRunning`

This function will call while the navigation is running

#### `onRouteBuildFailed`

This function will call while the route build failed

#### `onRouteBuilding`

This function will call while the route is building

#### `onMapReady`

This function will call while the map is initial successfully

#### `onMilestoneEvent?: (event: MilestoneData) => void;;`

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


### Speed Alert API

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

**Complete Example:**
```tsx
import React, { useEffect } from 'react';
import { VietMapNavigationController, VehicleType } from '@vietmap/vietmap-react-native-navigation';

const YourNavigationComponent = () => {
  useEffect(() => {
    // Step 1: Configure speed alert API with your credentials
    VietMapNavigationController.configureAlertAPI("YOUR_API_KEY_HERE", "YOUR_API_KEY_ID_HERE");
    
    // Step 2: Configure vehicle - Example: truck with 5 seats and 1500kg weight
    VietMapNavigationController.configVehicleSpeedAlert("VEHICLE_ID", VehicleType.truck, 5, 1500);
  }, []);

  return (
    // Your VietMapNavigation component here
  );
};
```

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
> - **iOS Requirement:** For speed alert to work properly on iOS, you must add `UIBackgroundModes` with `location` to your Info.plist file:
> ```xml
> <key>UIBackgroundModes</key>
> <array>
>   <string>location</string>
> </array>
> ```

---

### Add a controller to control the navigation progress

```tsx
        <View style={{ flex: 1, flexDirection: 'row' }}>
          <View key={'navigation'} style={{ flex: 1 }}>
            <Pressable
              style={buttonStyle}
              onPress={() =>
                VietMapNavigationController.buildRoute(
                  [
                    {
                      lat: 10.759156,
                      long: 106.675913,
                    },
                    {
                      lat: event.nativeEvent.data.latitude,
                      long: event.nativeEvent.data.longitude,
                    },
                  ],
                  'motorcycle'
                )
              }
            >
              <Text >Build route</Text>
            </Pressable>

            <Pressable
              style={buttonStyle}

              onPress={() => VietMapNavigationController.startNavigation()}
            >
              <Text >Start navigation</Text>
            </Pressable>

            <Pressable
              style={buttonStyle}

              onPress={() => VietMapNavigationController.finishNavigation()}
            >
              <Text >STOP_NAVIGATION</Text>
            </Pressable>

            <Pressable
              style={buttonStyle}

              onPress={() => VietMapNavigationController.recenter()}
            >
              <Text >RECENTER</Text>
            </Pressable>

            <Pressable
              style={buttonStyle}

              onPress={() => VietMapNavigationController.finishNavigation()}
            >
              <Text >FINISH_NAVIGATION</Text>
            </Pressable>

            <Pressable
              style={buttonStyle}

              onPress={() => VietMapNavigationController.overView()}
            >
              <Text >OVERVIEW</Text>
            </Pressable>

            <Pressable
              onPress={() => VietMapNavigationController.clearRoute()}
            >
              <Text >CLEAR ROUTE</Text>
            </Pressable>
          </View>
        </View>
```
### Importantly
All function of `VietMapNavigationController` must be called after the `VietMapNavigation` component is mounted and the map is ready. If you call it before the map is ready, it will make your app crash.

If you need to call the function of `VietMapNavigationController` when the map is ready, you must use the `onMapReady` callback of `VietMapNavigation` component or use it inside a button.
```tsx
  <VietMapNavigation
  
    onMapReady={() => {
      VietMapNavigationController.buildRoute(
        [
          {
            lat: 10.759156,
            long: 106.675913,
          },
          {
            lat: event.nativeEvent.data.latitude,
            long: event.nativeEvent.data.longitude,
          },
        ],
        'motorcycle'
      )
    }}
  />
```
### Show the instruction guide to navigation screen
- The instruction guide text response in `onRouteProgressChange` callback, you can get it by this code:
```tsx
  event?.nativeEvent?.data?.currentStepInstruction
```
### Turn direction guide
- The instruction guide (turn direction) responds in two variables: `currentModifier` and `currentModifierType`. You can get it by this code:
```tsx
  let modifier = event?.nativeEvent?.data?.currentModifier
  let type = event?.nativeEvent?.data?.currentModifierType
```
Replace all `space` with `_` and join two variables, you will get the turn direction guide (image and text direction) from the below list
- You can get the [image direction guide](https://www.figma.com/file/rWyQ5TNtt6E5l8tPEE9Tkl/VietMap-navigation-symbol?type=design&node-id=0-1&t=HeZNcRIAprzQ60ih-0) and [text direction guide](./example/turn_direction_description.json )

### Distance to the next turn
```tsx
  event?.nativeEvent?.distanceToNextTurn
```
### Distance and estimated time to the destination
```tsx
  /// The distance remaining to the destination, measured in meters 
  event?.nativeEvent?.distanceRemaining
  /// The time estimated to traveled the destination, measured in seconds
  event?.nativeEvent?.durationRemaining
  /// The distance user traveled from the origin point, measured in meters 
  event?.nativeEvent?.distanceTraveled
```

---

## VietMapMarkerView

`VietMapMarkerView` lets you place any React Native view as a marker directly on the navigation map. The marker stays pinned to its geographic coordinate as the camera moves.

> **Note:** The marker is display-only — it does not receive tap/press events. On Android the native side reparents the marker view into the map's view hierarchy (so the map keeps it positioned across camera moves), which takes it out of React Native's touch system; touchables placed inside the marker content won't fire. To add/remove markers, manage the array you render from JS (e.g. add on `onMapClick`, remove via your own UI/button).

### Import

```tsx
import { VietMapMarkerView } from '@vietmap/vietmap-react-native-navigation';
```

### Basic Usage

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

### Multiple Markers Example

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

### Props

#### `coordinate` (**Required**)

The geographic position of the marker. Accepts either a `[longitude, latitude]` tuple or an object `{ latitude, longitude }`.

```tsx
coordinate={[106.675913, 10.759156]}        // tuple form — [lng, lat]
coordinate={{ latitude: 10.759156, longitude: 106.675913 }}  // object form
```

> **Note:** The tuple form uses `[longitude, latitude]` order (GeoJSON convention).

#### `anchor`

Controls which point of the marker view sits on the coordinate. Values are in `[0..1]` from the view's top-left corner. Default: `{ x: 0.5, y: 1 }` (bottom-center — a pin tip touches the coordinate).

#### `id`

Optional stable string ID for the marker. Auto-generated when omitted. Pass your own stable id and use it as the React `key` so the right marker is removed when you filter the list.

#### `children` (**Required**)

Exactly one React Native element to render as the marker content. Can be a container `<View>` with multiple children inside.

---

## Troubleshooting

### iOS Build Error: Multiple commands produce Assets.car

If you encounter this error during iOS build:
```
❌ error: Multiple commands produce '/Library/Developer/Xcode/DerivedData/example-hhsxpyfcdgdhlgcoumkgcndusmvl/Build/Products/Debug-iphonesimulator/example.app/Assets.car'
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

## Contributing

Contributions are very welcome. Please check out the [contributing document](CONTRIBUTING.md).

## License

The source code in this library is [BSD-3-Clause](LICENSE) licensed.

</br>
</br>

[<img src="https://bizweb.dktcdn.net/100/415/690/themes/804206/assets/logo.png?1689561872933" height="40"/> </p>](https://vietmap.vn/maps-api)
Email us: [maps-api.support@vietmap.vn](mailto:maps-api.support@vietmap.vn)

Vietmap API and price [here](https://vietmap.vn/maps-api)

Contact for [support](https://vietmap.vn/lien-he)

Vietmap API document [here](https://maps.vietmap.vn/docs/map-api/overview/)

Have a bug to report? [Open an issue](https://github.com/vietmap-company/vietmap-react-native-navigation/issues).</br> If possible, include a full log and information that shows the issue.


Have a feature request? [Open an issue](https://github.com/vietmap-company/vietmap-react-native-navigation/issues). </br>Tell us what the feature should do and why you want the feature.