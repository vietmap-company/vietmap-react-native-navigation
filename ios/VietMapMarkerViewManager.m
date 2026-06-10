#import "VietMapMarkerViewManager.h"
#import "VietMapMarkerView.h"

// Exports the native component as "VietMapMarkerView" (manager name minus the "Manager" suffix).
@implementation VietMapMarkerViewManager

RCT_EXPORT_MODULE()

RCT_EXPORT_VIEW_PROPERTY(markerId, NSString)
// JS `coordinate` ([lng, lat]) maps to reactCoordinate; the bare `coordinate` is the
// MLNAnnotation CLLocationCoordinate2D and must not be set directly by RN.
RCT_REMAP_VIEW_PROPERTY(coordinate, reactCoordinate, NSArray)
RCT_EXPORT_VIEW_PROPERTY(anchor, NSDictionary)

- (UIView *)view {
    return [[VietMapMarkerView alloc] init];
}

@end
