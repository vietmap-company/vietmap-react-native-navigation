// RCTCalendarModule.m
#import "VietMapNavigationModule-Header.h"
#import <React/RCTLog.h>
#import <React/RCTBridgeModule.h>
#import <React/RCTViewManager.h>

@interface RCT_EXTERN_MODULE(VietMapNavigationModule, NSObject)
RCT_EXTERN_METHOD(buildRoute:(NSArray *)location profile:(NSString *)profile)
RCT_EXTERN_METHOD(startNavigation)
RCT_EXTERN_METHOD(finishNavigation)
RCT_EXTERN_METHOD(overView)
RCT_EXTERN_METHOD(cancelNavigation)
RCT_EXTERN_METHOD(clearRoute)
RCT_EXTERN_METHOD(recenter)
RCT_EXTERN_METHOD(startSpeedAlert)
RCT_EXTERN_METHOD(stopSpeedAlert)
RCT_EXTERN_METHOD(isSpeedAlertActive:(RCTPromiseResolveBlock)resolve rejecter:(RCTPromiseRejectBlock)reject)
RCT_EXTERN_METHOD(configureAlertAPI:(NSString *)apiKey apiID:(NSString *)apiID)
RCT_EXTERN_METHOD(configVehicleSpeedAlert:(NSString *)vehicleId vehicleType:(NSInteger)vehicleType seats:(NSInteger)seats weight:(double)weight)

@end
