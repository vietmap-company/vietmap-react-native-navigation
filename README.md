
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
- VietMap navigation SDK only supported React Native version 0.70 and above.
If you're using a lower version of React Native, please [contact us](mailto:maps-api.support@vietmap.vn) for more information
## Installation

Using `npm`
```bash
  npm install @vietmap/vietmap-react-native-navigation-070
```

Using `yarn`

```bash
  yarn add @vietmap/vietmap-react-native-navigation-070
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
            console.log('onRouteBuilt', event.nativeEvent.data);
          }}
          onMapClick={(event) => {
            console.log('onMapClick', event.nativeEvent.data);

            VietMapNavigationController.buildRoute(
              [
                [10.759156, 106.675913],
                [event.nativeEvent.data.latitude, event.nativeEvent.data]
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

Api key VietMap provided for user/customer

#### `initialLatLngZoom` (**Required**)

Object `InitialLatLngZoom` that contains the longitude and latitude for the initial zoom.<br>
```tsx
  {
    lat: number;  // -90 to 90 degrees
    long: number; // -180 to 180 degrees
    zoom: number; // 0 to 21
  }
```

**SDK will auto detect current location of user and move the map to this location, the zoom level will match with provided**
 
#### `shouldSimulateRoute`

Boolean that controls route simulation. Set this as `true` to auto navigate which is useful for testing or demo purposes. Defaults to `false`.

#### `navigationZoomLevel`

Zoom level while user in navigation
 
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