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

@end
