/* eslint-disable comma-dangle */
// import React from 'react';
import { StyleSheet, View, Pressable, Text, Dimensions, Image } from 'react-native';
import VietMapNavigation, { NavigationProgressData } from '@vietmap/vietmap-react-native-navigation';
import { VietMapNavigationController } from '@vietmap/vietmap-react-native-navigation';
import React, { useEffect, useState } from 'react';
 

const VMNavigation: React.FC<void> = () => {
  const [instructionText, setInstructionText] = useState<string>("");

  const [guideText, setGuideText] = useState<string>("");
  const [distanceToNextTurn, setDistanceToNextTurn] = useState<string>("");

  const translationGuide: Map<string, string> = new Map([
    ["arrive_left", "Đích đến bên trái"],
    ["arrive_right", "Đích đến bên phải"],
    ["arrive_straight", "Đích đến phía trước"],
    ["arrive", "Đến"],
    ["close", "Đóng"],
    ["continue_left", "Tiếp tục đi và rẽ trái"],
    ["continue_right", "Tiếp tục đi và rẽ phải"],
    ["continue_slight_left", "Tiếp tục đi và rẽ nhẹ về bên trái"],
    ["continue_slight_right", "Tiếp tục đi và rẽ nhẹ về bên phải"],
    ["continue_straight", "Tiếp tục đi thẳng"],
    ["continue_uturn", "Tiếp tục đi và quay đầu"],
    ["continue", "Tiếp tục đi"],
    ["depart_left", "Rẽ trái khi xuất phát"],
    ["depart_right", "Rẽ phải khi xuất phát"],
    ["depart_straight", "Tiếp tục đi thẳng khi xuất phát"],
    ["depart", "Xuất phát"],
    ["end_of_road_left", "Rẽ trái ở cuối đường"],
    ["end_of_road_right", "Rẽ phải ở cuối đường"],
    ["flag", "Cờ"],
    ["fork_left", "Rẽ trái ở ngã ba"],
    ["fork_right", "Rẽ phải ở ngã ba"],
    ["fork_slight_left", "Rẽ nhẹ về bên trái ở ngã ba"],
    ["fork_slight_right", "Rẽ nhẹ về bên phải ở ngã ba"],
    ["fork_straight", "Tiếp tục đi thẳng ở nơi giao nhánh"],
    ["fork", "Giao nhánh"],
    ["invalid_left", "Hướng trái không hợp lệ"],
    ["invalid_right", "Hướng phải không hợp lệ"],
    ["invalid_slight_left", "Hướng nhẹ về phía trái không hợp lệ"],
    ["invalid_slight_right", "Hướng nhẹ về phía phải không hợp lệ"],
    ["invalid_straight", "Hướng đi thẳng không hợp lệ"],
    ["invalid_uturn", "Hướng quay đầu không hợp lệ"],
    ["invalid", "Hướng không hợp lệ"],
    ["merge_left", "Hợp nhất vào phía trái"],
    ["merge_right", "Hợp nhất vào phía phải"],
    ["merge_slight_left", "Hợp nhất và rẽ nhẹ về phía trái"],
    ["merge_slight_right", "Hợp nhất và rẽ nhẹ về phía phải"],
    ["merge_straight", "Hợp nhất và đi thẳng"],
    ["new_name_left", "Rẽ trái"],
    ["new_name_right", "Rẽ phải"],
    ["new_name_sharp_left", "Rẽ ngoặt về trái"],
    ["new_name_sharp_right", "Rẽ ngoặt về phải"],
    ["new_name_slight_left", "Rẽ nhẹ về trái"],
    ["new_name_slight_right", "Rẽ nhẹ về phải"],
    ["new_name_straight", "Tiếp tục đi thẳng"],
    ["notification_sharp_right", "Rẽ ngoặt về phải"],
    ["notification_left", "Rẽ trái"],
    ["notification_right", "Rẽ phải"],
    ["notification_sharp_left", "Rẽ ngoặt về trái"],
    ["notification_slight_left", "Rẽ nhẹ về trái"],
    ["notification_slight_right", "Rẽ nhẹ về phải"],
    ["notification_straight", "Tiếp tục đi thẳng"],
    ["off_ramp_left", "Rẽ trái ở lối ra"],
    ["off_ramp_right", "Rẽ phải ở lối ra"],
    ["off_ramp_slight_left", "Rẽ nhẹ về trái ở lối ra"],
    ["off_ramp_slight_right", "Rẽ nhẹ về phải ở lối ra"],
    ["on_ramp_left", "Rẽ trái ở lối vào"],
    ["on_ramp_right", "Rẽ phải ở lối vào"],
    ["on_ramp_sharp_left", "Rẽ ngoặt trái ở lối vào"],
    ["on_ramp_sharp_right", "Rẽ ngoặt phải ở lối vào"],
    ["on_ramp_slight_left", "Rẽ nhẹ về trái ở lối vào"],
    ["on_ramp_slight_right", "Rẽ nhẹ về phải ở lối vào"],
    ["on_ramp_straight", "Tiếp tục đi thẳng ở lối vào"],
    ["rotary_left", "Rẽ trái ở vòng xuyến"],
    ["rotary_right", "Rẽ phải ở vòng xuyến"],
    ["rotary_sharp_left", "Rẽ trái ở vòng xuyến"],
    ["rotary_sharp_right", "Rẽ phải ở vòng xuyến"],
    ["rotary_slight_left", "Rẽ nhẹ về trái ở vòng xuyến"],
    ["rotary_slight_right", "Rẽ nhẹ về phải ở vòng xuyến"],
    ["rotary_straight", "Tiếp tục đi thẳng ở vòng xuyến"],
    ["rotary", "Vòng xuyến"],
    ["roundabout_left", "Rẽ trái ở vòng xoay"],
    ["roundabout_right", "Rẽ phải ở vòng xoay"],
    ["roundabout_sharp_left", "Rẽ ngoặt về trái ở vòng xoay"],
    ["roundabout_sharp_right", "Rẽ ngoặt về phải ở vòng xoay"],
    ["roundabout_slight_left", "Rẽ nhẹ về trái ở vòng xoay"],
    ["roundabout_slight_right", "Rẽ nhẹ về phải ở vòng xoay"],
    ["roundabout_straight", "Tiếp tục đi thẳng ở vòng xoay"],
    ["roundabout", "Vòng xoay"],
    ["turn_left", "Rẽ trái"],
    ["turn_right", "Rẽ phải"],
    ["turn_sharp_left", "Rẽ ngoặt về trái"],
    ["turn_sharp_right", "Rẽ ngoặt về phải"],
    ["turn_slight_left", "Rẽ nhẹ về trái"],
    ["turn_slight_right", "Rẽ nhẹ về phải"],
    ["turn_straight", "Tiếp tục đi thẳng"],
    ["updown", ""],
    ["uturn", "Quay đầu"]
  ]);

  const getGuideText = (modifier: string, type: string) => {
    console.log(modifier, type)
    if (modifier != null && type != null) {
      console.log(modifier, type);
      let data = [
        type.split(" ").join("_")
        ,
        modifier.split(" ").join("_")
      ];

      setGuideText(translationGuide.get(data.join('_'))?.toLowerCase() ?? '');
      console.log(data.join('_'))
    }
 
  }
  const getDistanceToNextTurn = (distance: number) => {
    var data = distance ?? 0;
    console.log(data)
    if (data < 1000) { setDistanceToNextTurn(`Còn ${Math.round(data)} mét,`) } else {

      setDistanceToNextTurn(`Còn ${(data / 1000).toFixed(2)} Km,`);
    }
  }
  return (
    <View style={styles.container}>
      <View style={styles.mapContainer}>
        <VietMapNavigation
          //[12.895131, 108.490272, 6]
          // initialLatLngZoom={{
          //   lat: 12.895131,
          //   long: 108.490272,
          //   zoom: 10,
          // }}
          navigationPadding={{
            left: 50,
            top: 50,
            right: 50,
            bottom: 50
          }}
          navigationZoomLevel={18}
          shouldSimulateRoute={true}
          apiKey={'YOUR_API_KEY_HERE'}
          onRouteProgressChange={(event) => {
            setInstructionText(event?.nativeEvent?.data?.currentStepInstruction ?? '');
            let modifier = event?.nativeEvent?.data?.currentModifier
            let type = event?.nativeEvent?.data?.currentModifierType

            if (type != null && modifier != null) {
              var data = [
                type.replace(' ', ' _'),
                modifier.replace(' ', ' _')
              ];
              var path = `./svg/png_navigation_symbol/${data.join('_')}.png`;
              getGuideText(modifier, type);
              // setInstructionImage(path);

              if (event?.nativeEvent?.data?.distanceToNextTurn != null) {
                getDistanceToNextTurn(event?.nativeEvent?.data?.distanceToNextTurn);
              } console.log('=====================================================================');
              console.log('onRouteProgressChange', event?.nativeEvent?.data?.currentStepInstruction);
              console.log('=====================================================================');
            }
          }}
          onNavigationRunning={
            () => {
              console.log('onNavigationRunning');
            }
          }
          onMapMove={() => {
            console.log('onMapMove');
          }}
          onMapMoveEnd={() => {
            console.log('onMapMoveEnd')
          }}
          onMilestoneEvent={(event) => {

            console.log('=====================================================================');
            console.log('onMilestoneEvent', event.nativeEvent);
            console.log('=====================================================================');
          }}
          onArrival={() => {
            alert('You have reached your destination');


          }}
          onRouteBuilt={(event) => {
            console.log('onRouteBuilt',JSON.stringify( event.nativeEvent.data));
          }}
          onMapClick={(event) => {
            console.log('onMapClick', event.nativeEvent.data.latitude);
          }}
          onMapReady={() => {

          }}
          onMapLongClick={(event) => {
            console.log('onMapLongClick', event.nativeEvent.data.latitude);
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
          onCancelNavigation={() => {
            alert('Cancelled navigation event');


          }}
          onNavigationFinished={() => {
            alert('Finished navigation event');


          }}
        />
        <View style={{ flex: 1, flexDirection: 'row' }}>
          <View key={'navigation'} style={{ flex: 1 }}>
            <Pressable

              style={buttonStyle}
              onPress={() =>
                VietMapNavigationController.buildRoute(
                  [
                    {
                      lat: 10.816964,
                      long: 106.613452,
                    },
                    {
                      lat: 10.762429,
                      long: 106.678900,
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
      </View>
      <View style={{
        borderRadius: 10,
        width: Dimensions.get('window').width - 20, height: 100, backgroundColor: 'blue', position: 'absolute', left: 10, top: 10, opacity: 0.5
      }}>
        <View style={{ flex: 1, flexDirection: 'row', justifyContent: 'center', alignItems: 'center' }}>

          <View style={{ flex: 1, flexDirection: 'column', justifyContent: 'center', alignItems: 'center' }}>

            <Text
              style={{
                color: 'white',

                fontSize: 20,
                fontWeight: '700',
              }}>
              {instructionText ?? ""}
            </Text>
            <Text style={{
              color: 'white',
              fontSize: 20,
              fontWeight: '700',
            }}>
              {distanceToNextTurn} {guideText}
            </Text>

          </View>
        </View>
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
