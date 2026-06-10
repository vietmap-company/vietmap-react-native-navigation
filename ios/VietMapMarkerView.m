#import "VietMapMarkerView.h"
#import "VietMapMarkerBridge.h"
#import <React/UIView+React.h>

@implementation VietMapMarkerView {
    // Guards against infinite recursion: this view is both the MLNAnnotation and its annotation view,
    // so [map removeAnnotation:self] makes MapLibre call removeFromSuperview on self, which would call
    // removeAnnotation again — looping until the stack overflows (EXC_BAD_ACCESS).
    BOOL _removingFromMap;
    // The map this marker was actually added to. Removing must target the same map: the nav view can
    // recreate its map, so currentMapView may differ from where we registered — and on add MapLibre
    // begins KVO-observing our `coordinate`; removing against the wrong map throws an NSRangeException
    // ("not registered as an observer").
    __weak MLNMapView *_attachedMap;
}

#pragma mark - React subviews (host the marker content)

- (void)insertReactSubview:(UIView *)subview atIndex:(NSInteger)atIndex {
    [super insertReactSubview:subview atIndex:0];
}

- (void)removeReactSubview:(UIView *)subview {
    [super removeReactSubview:subview];
    // RN removes the hosted content only when this marker component unmounts, so an empty reactSubviews
    // is our reliable teardown signal. (removeFromSuperview is NOT — MapLibre fires that during its own
    // annotation removal on every route build.)
    if (self.reactSubviews.count == 0) {
        [self _teardown];
    }
}

// NOTE: we deliberately do NOT remove + re-add the annotation here. The annotation is added once and
// stays put; MapLibre KVO-observes `coordinate` to move it, and updating the view's bounds +
// centerOffset repositions it in place. Churning addAnnotation/removeAnnotation on every layout pass
// races MapLibre's observer registration and crashes ("not registered as an observer").
- (void)reactSetFrame:(CGRect)frame {
    [super reactSetFrame:frame];
    [self _setCenterOffset:frame];
    [self _addAnnotation];
}

// Under the New Architecture, the marker is hosted inside the legacy-interop wrapper and
// reactSetFrame timing isn't guaranteed. didMoveToWindow fires reliably, so we use it only to
// (re)attempt attachment. We must NOT recompute centerOffset here: outside reactSetFrame `self.frame`
// is the map/interop-positioned frame, not the content size, and using it produces a huge
// centerOffset that throws the marker far off its coordinate when zoomed out.
- (void)didMoveToWindow {
    [super didMoveToWindow];
    if (self.window != nil) {
        [self _addAnnotation];
    }
}

// Under the New Architecture reactSetFrame isn't guaranteed to fire, so centerOffset could stay
// (0,0) — which makes MapLibre center the whole view on the coordinate (the marker's center, not
// its tip, lands on the tap point). layoutSubviews fires reliably once the view is laid out, and
// since the view is size-to-content (position:absolute on the JS side) self.bounds is the real
// marker size here, so the anchor math is correct.
- (void)layoutSubviews {
    [super layoutSubviews];
    [self _setCenterOffset:self.bounds];
}

#pragma mark - Props

- (void)setAnchor:(NSDictionary<NSString *, NSNumber *> *)anchor {
    _anchor = anchor;
    [self _setCenterOffset:self.frame];
}

- (void)setReactCoordinate:(NSArray<NSNumber *> *)reactCoordinate {
    _reactCoordinate = reactCoordinate;
    if (reactCoordinate.count < 2) {
        return;
    }
    CLLocationCoordinate2D coord =
        CLLocationCoordinate2DMake(reactCoordinate[1].doubleValue, reactCoordinate[0].doubleValue);
    dispatch_async(dispatch_get_main_queue(), ^{
        self.coordinate = coord;
    });
}

- (NSString *)reuseIdentifier {
    return _markerId ?: @"VietMapMarkerView";
}

#pragma mark - Annotation view

- (MLNAnnotationView *)getAnnotationView {
    if (self.reactSubviews.count == 0) {
        // No custom content: let the SDK draw a default pin.
        return nil;
    }
    self.enabled = YES;
    const CGFloat defaultZPosition = 0.0;
    if (self.layer.zPosition == defaultZPosition) {
        self.layer.zPosition = [self _getZPosition];
    }
    // Make sure the anchor offset is applied before the map positions the view, so the marker's
    // anchor point (bottom-center by default) — not its center — sits on the coordinate.
    [self _setCenterOffset:self.bounds];
    return self;
}

- (CGFloat)_getZPosition {
    double latitudeMax = 90.0;
    return latitudeMax - self.coordinate.latitude;
}

#pragma mark - Anchor → centerOffset

- (void)_setCenterOffset:(CGRect)frame {
    if (frame.size.width == 0 || frame.size.height == 0 || _anchor == nil) {
        return;
    }

    float x = [_anchor[@"x"] floatValue];
    float y = [_anchor[@"y"] floatValue];

    float dx = -(x * frame.size.width - (frame.size.width / 2));
    float dy = -(y * frame.size.height - (frame.size.height / 2));

    if (x == 0) {
        dx = frame.size.width / 2;
    } else if (x == 1) {
        dx = -frame.size.width / 2;
    }

    if (y == 0) {
        dy = frame.size.height / 2;
    } else if (y == 1) {
        dy = -frame.size.height / 2;
    }

    self.centerOffset = CGVectorMake(dx, dy);
}

#pragma mark - Add / remove

- (void)_addAnnotation {
    if (![self _isFrameSet]) {
        return;
    }
    // Register regardless of map readiness, so VietMapMarkerBridge.reAddAnnotations can attach this
    // marker once the navigation view publishes its map.
    [VietMapMarkerBridge registerMarker:self];
    MLNMapView *map = VietMapMarkerBridge.currentMapView;
    if (map == nil) {
        return;
    }
    if (![map.annotations containsObject:self]) {
        [map addAnnotation:self];
    }
    _attachedMap = map;
}

- (BOOL)_isFrameSet {
    return self.frame.size.width > 0 && self.frame.size.height > 0;
}

// Removes self from the map, guarded so the removeAnnotation → removeFromSuperview → removeAnnotation
// cycle can't recurse. MapLibre's removeAnnotation reentrantly calls removeFromSuperview on the
// annotation view (which is self); the flag makes that nested call a no-op.
- (void)_removeFromMapIfNeeded {
    if (_removingFromMap) {
        return;
    }
    _removingFromMap = YES;
    MLNMapView *map = _attachedMap;
    if (map != nil && [map.annotations containsObject:self]) {
        // @try guards the KVO teardown: if MapLibre's observer registration was already torn down
        // (e.g. the map was recreated mid-flight) removeAnnotation can throw "not registered as an
        // observer"; swallowing it keeps an unmount from crashing the app.
        @try {
            [map removeAnnotation:self];
        } @catch (NSException *exception) {
            NSLog(@"[VMMarker] removeAnnotation threw for id=%@: %@", _markerId, exception);
        }
    }
    _attachedMap = nil;
    _removingFromMap = NO;
}

// Teardown for real RN unmount: pull the annotation off the map and stop tracking it. Distinct from
// removeFromSuperview, which MapLibre also calls while removing the annotation itself.
- (void)_teardown {
    [self _removeFromMapIfNeeded];
    [VietMapMarkerBridge unregisterMarker:self];
}

// IMPORTANT: do NOT remove the annotation here. MapLibre calls removeFromSuperview on the annotation
// view as part of its OWN annotation removal — e.g. the nav SDK runs removeAnnotations(annotations) on
// every route build (NavigationMapView.showWaypoints/removeWaypoints). Calling removeAnnotation back
// from here re-enters that removal and double-removes the `coordinate` KVO observer, which throws
// NSRangeException ("not registered as an observer") deep inside the SDK. Real unmount is handled in
// removeReactSubview; transient SDK sweeps are re-added by VietMapMarkerBridge.reAddAnnotations.
- (void)removeFromSuperview {
    [super removeFromSuperview];
}

@end
