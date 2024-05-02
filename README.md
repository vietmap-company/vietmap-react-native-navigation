# React Native VietMap Navigation

<img alt="React Native VietMap Navigation" src="./img/ios-nav.png?v=2" width="300" align="right" />

VietMap turn-by-turn routing based on real-time traffic for React Native. A navigation UI ready to drop into your React Native application. [Sample demo usage shown here for the  app in the screenshot](https://www.maps.vietmap.vn/) ➡️

## Features

- A full-fledged turn-by-turn navigation UI for mobile
- VietNam driving, cycling, motorcycle and walking directions powered by [VietMap data](https://www.maps.vietmap.vn/) 
 - Sounding turn instructions powered by [Google]( )

## Installation Requirements
- You need an api key from VietMap to show map and start navigation.
- VietMap navigation SDK only support React Native version 0.70 and above.
If you're using lower version of React Native, please contact us for more information
## Installation

```
npm install @vietmap/vietmap-react-native-navigation
```
---

### iOS Specific Instructions
 
Add the below codes to the Info.plist file. Replace the **`YOUR_API_KEY_HERE`** with your API key.
```ruby
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

Add below code to `Podfile`

```ruby
    post_install do |installer|
      #...............
      # Add this line
      system("cp -R Pods/Headers/Public/VietMapDirections.swift/VietMapDirections.h Pods/Headers/Public/VietMapDirections")
      #...............
    end
```
Run `pod install` command
```
cd ios && pod install
```

## Usage
### Show navigation view
```tsx
const VMNavigation: React.FC<void> = () => {
  return (
    <View style={styles.container}>
      <View style={styles.mapContainer}>
        <VietMapNavigation
          initialLatLngZoom={{
            lat: 12.895131,
            long: 108.490272,
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
          onRouteProgressChange={(event) => { 
            console.log('onRouteProgressChange', event.nativeEvent?.data); 
          }}
          onError={(event) => {
            // const { message } = event.nativeEvent;
            alert('Error: ' + event);
          }}
          onMapMove={() => {
            console.log('onMapMove');
          }}
          onMilestoneEvent={(event) => {
            console.log('onMilestoneEvent', event.nativeEvent);
          }}
          onArrival={() => {
            alert('You have reached your destination');
          }}
          onRouteBuilt={(event) => {
            console.log('onRouteBuilt', event.nativeEvent.data.duration);
          }}
          onMapClick={(event) => {
            console.log('onMapClick', event.nativeEvent.data.latitude);

            VietMapNavigationController.buildRoute(
              [
                [10.759156, 106.675913],
                [event.nativeEvent.data.latitude, event.nativeEvent.data.longitude]
              ],
              'motorcycle'
            )
          }}
          onMapLongClick={(event) => {
            console.log('onMapLongClick', event.nativeEvent.data.latitude);
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

export default VMNavigation;

```

### `VietMapNavigation` Props

#### `apiKey` (**Required**)

Api key VietMap provided for user/customer

#### `initialLatLngZoom` (**Required**)

Array that contains the longitude and latitude for the initial zoom.<br>
`[$longitude, $latitude, $zoom]`

**SDK will auto detect current location of user and move the map to this location, the zoom level will match with provided**
 
#### `shouldSimulateRoute`

Boolean that controls route simulation. Set this as `true` to auto navigate which is useful for testing or demo purposes. Defaults to `false`.

#### `navigationZoomLevel`

Zoom level while user in navigation

#### `navigationTiltAnchor`

Tilt level while user in navigation

#### `style`

React native style for the `VietMapNavigation` react native component

#### `  onRouteProgressChange?: (event: NavigationProgressData) => void;`

This callback will response a `NavigationProgressData` model, which contain all data of current navigation progress

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

#### `userOffRoute?: (event: LocationData) => void;`

This function will call while user is off-route. The location when user off-route will contain in `LocationData` model

#### `onArrival`

This function will call while user is arrival at the destination

#### `onNewRouteSelected?: (event: RouteData) => void;`

This function will call while user select a new route, cause VietMap SDK will find one or more route between two points. When user select new route, the `RouteData` will response.



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

              onPress={() => VietMapNavigationController.cancelNavigation()}
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

## Contributing

Contributions are very welcome. Please check out the [contributing document](CONTRIBUTING.md).

## License

The source code in this library is [BSD-3-Clause](LICENSE) licensed.