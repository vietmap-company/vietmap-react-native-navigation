#import "React/RCTViewManager.h"

@interface RCT_EXTERN_MODULE(VietMapNavigationManager, RCTViewManager)

RCT_EXPORT_VIEW_PROPERTY(onRouteProgressChange, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onError, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onNavigationFinished, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onArrival, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(userOffRoute, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onRouteBuilt, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onRouteBuildFailed, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onMapLongClick, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onMapClick, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onMapMove, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onMapMoveEnd, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onNewRouteSelected, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(shouldSimulateRoute, BOOL)
RCT_EXPORT_VIEW_PROPERTY(initialLatLngZoom, NSDictionary)
RCT_EXPORT_VIEW_PROPERTY(navigationPadding, NSDictionary)
RCT_EXPORT_VIEW_PROPERTY(navigationZoomLevel, NSNumber)
@end
