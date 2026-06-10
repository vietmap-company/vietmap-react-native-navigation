#import "VietMapMarkerBridge.h"

@implementation VietMapMarkerBridge

static __weak MLNMapView *sCurrentMapView = nil;
// Markers are held weakly so unmounted RN views can deallocate; the NSHashTable does the bookkeeping.
static NSHashTable *sMarkers = nil;

+ (void)initialize {
    if (self == [VietMapMarkerBridge class]) {
        sMarkers = [NSHashTable weakObjectsHashTable];
    }
}

+ (MLNMapView *)currentMapView {
    return sCurrentMapView;
}

+ (void)setCurrentMapView:(MLNMapView *)currentMapView {
    sCurrentMapView = currentMapView;
}

+ (void)registerMarker:(id<MLNAnnotation>)marker {
    if (marker != nil) {
        [sMarkers addObject:marker];
    }
}

+ (void)unregisterMarker:(id<MLNAnnotation>)marker {
    if (marker != nil) {
        [sMarkers removeObject:marker];
    }
}

+ (void)reAddAnnotations {
    MLNMapView *map = sCurrentMapView;
    if (map == nil) {
        return;
    }
    for (id<MLNAnnotation> marker in [sMarkers allObjects]) {
        if (![map.annotations containsObject:marker]) {
            [map addAnnotation:marker];
        }
    }
}

@end
