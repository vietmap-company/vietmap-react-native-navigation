#ifdef RCT_NEW_ARCH_ENABLED

#import <UIKit/UIKit.h>
#import <React/RCTViewComponentView.h>

NS_ASSUME_NONNULL_BEGIN

/**
 * Fabric (New Architecture) component view for the JS component "VietMapNavigation".
 *
 * It does NOT reimplement the map: it hosts the existing Swift VietMapNavigationView singleton
 * (same instance the Paper RCTViewManager returns), maps codegen props onto its @objc properties,
 * and bridges its RCTDirectEventBlock callbacks into the codegen C++ event emitter as a JSON
 * string payload (`nativeEvent.json`), which the JS wrapper parses back to the legacy shape.
 *
 * Old Architecture apps keep using VietMapNavigationManager (Paper); this file compiles to
 * nothing when RCT_NEW_ARCH_ENABLED is not defined.
 */
@interface RCTVietMapNavigationComponentView : RCTViewComponentView
@end

NS_ASSUME_NONNULL_END

#endif /* RCT_NEW_ARCH_ENABLED */
