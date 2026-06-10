#import <React/RCTComponent.h>
#import <React/RCTView.h>
#import <UIKit/UIKit.h>
@import VietMap;

NS_ASSUME_NONNULL_BEGIN

/**
 * A map marker that hosts a real React Native child view. Ported and slimmed from
 * vietmap-gl-react-native's MLRNPointAnnotation (callout/drag/title removed). It is both the
 * MLNAnnotation and its MLNAnnotationView; MLNMapView keeps it positioned at `coordinate` as the
 * camera moves. It attaches itself to the map published via VietMapMarkerBridge.
 */
@interface VietMapMarkerView : MLNAnnotationView <MLNAnnotation>

@property (nonatomic, copy, nullable) NSString *markerId;
/// [longitude, latitude] from JS.
@property (nonatomic, copy, nullable) NSArray<NSNumber *> *reactCoordinate;
@property (nonatomic, assign) CLLocationCoordinate2D coordinate;
@property (nonatomic, copy, nullable) NSDictionary<NSString *, NSNumber *> *anchor;

- (nullable MLNAnnotationView *)getAnnotationView;

@end

NS_ASSUME_NONNULL_END
