/* eslint-disable comma-dangle */
import React from 'react';
import {StyleSheet, View, Pressable, Text} from 'react-native';
import VietMapNavigation from '@vietmap/vietmap-react-native-navigation';
import fromProgressChangeDataJson from '@vietmap/vietmap-react-native-navigation';
import RouteProgressModel from '@vietmap/vietmap-react-native-navigation';
// import {VietMapNavigationController} from '@vietmap/vietmap-react-native-navigation';
import {VietMapNavigationController} from '@vietmap/vietmap-react-native-navigation';

const Navigation = props => {
  const {origin, destination} = props;

  return (
    <View style={styles.container}>
      <View style={styles.mapContainer}>
        <VietMapNavigation
          // showsEndOfRouteFeedback={true}
          shouldSimulateRoute={false}
          apiKey={'YOUR_API_KEY_HERE'}

          onRouteProgressChange={event => {
            // console.log(JSON.stringify(event.nativeEvent.data))
            console.log(event)

            // alert(JSON.stringify(event.nativeEvent))
            // let resData = fromProgressChangeDataJson(event);
            console.log(
              '=====================================================================',
            );
            // console.log('onRouteProgressChange', resData);
            // console.log(
            //   '=====================================================================',
            // );
          }}
          onError={event => {
            const {message} = event.nativeEvent;
            // eslint-disable-next-line no-alert

            alert('Error: ' + message);
          }}
          onArrival={() => {
            // eslint-disable-next-line no-alert
            alert('You have reached your destination');
          }}
          onRouteBuilt={event => {
            console.log('onRouteBuilt', event.nativeEvent);
            // VietMapNavigationController.startNavigation();

            // alert(JSON.stringify(event.nativeEvent))
          }}
          onMapLongClick = {event => {
            console.log('onMapLongClick', event.nativeEvent.data);
            
          }}
          onMapMoveEnd={event => {
            console.log('onMapMoveEnd', event.nativeEvent);
            
          }}
          onNavigationFinished={event => {
            console.log('onNavigationFinished', event.nativeEvent);
          }}
          onNavigationCancelled={event => {
            console.log('onNavigationCancelled', event.nativeEvent);
          }}
          onNavigationRunning={event => {
            console.log('onNavigationRunning', event.nativeEvent);
          }}
          onRouteBuildFailed={event => {
            console.log('onRouteBuildFailed', event.nativeEvent);
          }}
          onRouteBuilding={event => {
            console.log('onRouteBuilt', event.nativeEvent);
          }}
          onMapReady={event => {
            console.log('onMapReady', event.nativeEvent);
          }}
          onMilestoneEvent={event => {
            // console.log('onMilestoneEvent', event.nativeEvent);
          }}
          userOffRoute={event => {
            console.log('userOffRoute', event.nativeEvent);
          }}
          onArrival={event => {
            console.log('onArrival', event.nativeEvent);
          }}
          onNewRouteSelected={event => {
            console.log('onNewRouteSelected', event.nativeEvent);
          }}
          onMapMove={event => {
            console.log('onMapMove', event.nativeEvent);
          }}
          onMapClick={event => {
            console.log('onMapClick', event.nativeEvent.data);
            // {"data": "{\"latitude\":10.752720242423363,\"longitude\":106.6982605007081,\"x\":619.0,\"y\":824.0}", "eventType": "onMapClick"} 
            let dataJson = JSON.parse(event.nativeEvent.data);
            VietMapNavigationController.buildRoute(
              [
                [10.759156, 106.675913],
                [
                  dataJson.latitude,
                  dataJson.longitude,
                ],
              ],
              'motorcycle',
            );
            // alert('Map clicked');
          }}
          onCancelNavigation={event => {
            alert('Cancelled navigation event');
          }}></VietMapNavigation>
        <View style={{flex: 1, flexDirection: 'row'}}>
          <View key={'navigation'} style={{flex: 1}}>
            <Pressable
              onPress={() =>
                VietMapNavigationController.buildRoute(
                  [
                    [10.759156, 106.675913],
                    [10.770612, 106.714432],
                  ],
                  'motorcycle',
                )
              }
              style={{
                justifyContent: 'center',
                alignItems: 'center',
                height: 50,
                backgroundColor: 'white',
              }}>
              <Text
                style={{
                  borderColor: 'white',

                  color: 'black',
                  fontWeight: '700',
                }}>
                Build route
              </Text>
            </Pressable>

            <Pressable
              onPress={() => VietMapNavigationController.startNavigation()}
              style={{
                justifyContent: 'center',
                alignItems: 'center',
                height: 50,
                backgroundColor: 'white',
              }}>
              <Text
                style={{
                  borderColor: 'white',

                  color: 'black',
                  fontWeight: '700',
                }}>
                Start navigation
              </Text>
            </Pressable>
            <Pressable
              onPress={() => VietMapNavigationController.stopNavigation()}
              style={{
                justifyContent: 'center',
                alignItems: 'center',
                height: 50,
                backgroundColor: 'white',
              }}>
              <Text
                style={{
                  borderColor: 'white',

                  color: 'black',
                  fontWeight: '700',
                }}>
                STOP_NAVIGATION
              </Text>
            </Pressable>
            <Pressable
              onPress={() => VietMapNavigationController.recenter()}
              style={{
                justifyContent: 'center',
                alignItems: 'center',
                height: 50,
                backgroundColor: 'white',
              }}>
              <Text
                style={{
                  borderColor: 'white',

                  color: 'black',
                  fontWeight: '700',
                }}>
                RECENTER
              </Text>
            </Pressable>
            <Pressable
              onPress={() => VietMapNavigationController.finishNavigation()}
              style={{
                justifyContent: 'center',
                alignItems: 'center',
                height: 50,
                backgroundColor: 'white',
              }}>
              <Text
                style={{
                  borderColor: 'white',

                  color: 'black',
                  fontWeight: '700',
                }}>
                FINISH_NAVIGATION
              </Text>
            </Pressable>
            <Pressable
              onPress={() => VietMapNavigationController.overView()}
              style={{
                justifyContent: 'center',
                alignItems: 'center',
                height: 50,
                backgroundColor: 'white',
              }}>
              <Text
                style={{
                  borderColor: 'white',

                  color: 'black',
                  fontWeight: '700',
                }}>
                OVERVIEW
              </Text>
            </Pressable>
            <Pressable
              onPress={() => VietMapNavigationController.clearRoute()}
              style={{
                justifyContent: 'center',
                alignItems: 'center',
                height: 50,
                backgroundColor: 'white',
              }}>
              <Text
                style={{
                  borderColor: 'white',

                  color: 'black',
                  fontWeight: '700',
                }}>
                CLEAR ROUTE
              </Text>
            </Pressable>
          </View>
        </View>
      </View>
    </View>
  );
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

export default Navigation;
