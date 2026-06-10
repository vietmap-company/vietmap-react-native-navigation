#import <Foundation/Foundation.h>
@import VietMap;

NS_ASSUME_NONNULL_BEGIN

/**
 * Decouples the ObjC marker views from the Swift navigation view. The Swift `VietMapNavigationView`
 * publishes its map here; `VietMapMarkerView` instances (created independently by React Native) read
 * the current map to add themselves as annotations, and register so they can be re-added after the
 * navigation SDK redraws routes/waypoints (which clears annotations).
 */
@interface VietMapMarkerBridge : NSObject

/// The active navigation map. Set by the Swift navigation view.
@property (class, nonatomic, weak, nullable) MLNMapView *currentMapView;

/// Track a marker so it can be re-added after annotations are cleared.
+ (void)registerMarker:(id<MLNAnnotation>)marker;
+ (void)unregisterMarker:(id<MLNAnnotation>)marker;

/// Re-add every registered marker to the current map (call after showRoutes/showWaypoints).
+ (void)reAddAnnotations;

@end

NS_ASSUME_NONNULL_END
