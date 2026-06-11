#ifdef RCT_NEW_ARCH_ENABLED

#import "RCTVietMapNavigationComponentView.h"

#import <React/RCTComponent.h> // RCTDirectEventBlock (used by the mirror interface below)
#import <React/RCTConversions.h>

#import <react/renderer/components/VietMapNavigationSpec/ComponentDescriptors.h>
#import <react/renderer/components/VietMapNavigationSpec/EventEmitters.h>
#import <react/renderer/components/VietMapNavigationSpec/Props.h>
#import <react/renderer/components/VietMapNavigationSpec/RCTComponentViewHelpers.h>

using namespace facebook::react;

// Mirror declaration of the Swift VietMapNavigationView's RN-facing surface (the class is
// internal Swift, so it never appears in the generated -Swift.h of a library target). The Swift
// class is pinned to this ObjC name via @objc(VietMapNavigationView), so the linker binds this
// interface to the Swift implementation — the same mechanism RCT_EXTERN_MODULE uses for the
// Paper manager/module. @objcMembers on the Swift side guarantees every member below exists.
// Keep in sync with ios/VietMapNavigationView.swift.
@interface VietMapNavigationView : UIView

@property (class, nonatomic, strong) VietMapNavigationView *shared;

@property (nonatomic, copy, nullable) NSString *styleUrl;
@property (nonatomic, assign) BOOL shouldSimulateRoute;
@property (nonatomic, strong) NSDictionary *initialLatLngZoom;
@property (nonatomic, strong) NSDictionary *navigationPadding;
@property (nonatomic, assign) double navigationZoomLevel;
@property (nonatomic, copy, nullable) NSString *apiKeyAlert;
@property (nonatomic, copy, nullable) NSString *apiIDAlert;
@property (nonatomic, copy, nullable) NSString *puckImage;
@property (nonatomic, assign) CGFloat puckImageWidth;
@property (nonatomic, assign) CGFloat puckImageHeight;
@property (nonatomic, assign) CGFloat puckImageRotation;

@property (nonatomic, copy, nullable) RCTDirectEventBlock onRouteProgressChange;
@property (nonatomic, copy, nullable) RCTDirectEventBlock onNavigationFinished;
@property (nonatomic, copy, nullable) RCTDirectEventBlock onNavigationCancelled;
@property (nonatomic, copy, nullable) RCTDirectEventBlock onArrival;
@property (nonatomic, copy, nullable) RCTDirectEventBlock onUserOffRoute;
@property (nonatomic, copy, nullable) RCTDirectEventBlock onRouteBuilt;
@property (nonatomic, copy, nullable) RCTDirectEventBlock onRouteBuildFailed;
@property (nonatomic, copy, nullable) RCTDirectEventBlock onMapLongClick;
@property (nonatomic, copy, nullable) RCTDirectEventBlock onMapClick;
@property (nonatomic, copy, nullable) RCTDirectEventBlock onMapMove;
@property (nonatomic, copy, nullable) RCTDirectEventBlock onMapMoveEnd;
@property (nonatomic, copy, nullable) RCTDirectEventBlock onNewRouteSelected;

@end

namespace {

std::string JSONStringFromDictionary(NSDictionary *_Nullable dictionary)
{
  if (dictionary == nil || ![NSJSONSerialization isValidJSONObject:dictionary]) {
    return "{}";
  }
  NSData *data = [NSJSONSerialization dataWithJSONObject:dictionary options:0 error:nil];
  if (data == nil) {
    return "{}";
  }
  NSString *string = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
  return string == nil ? "{}" : std::string(string.UTF8String);
}

} // namespace

@interface RCTVietMapNavigationComponentView () <RCTVietMapNavigationViewProtocol>
@end

@implementation RCTVietMapNavigationComponentView {
  // Set when the (fresh, default-valued) shared Swift view was just claimed, so the next
  // updateProps pushes every meaningful prop instead of only the ones that differ.
  BOOL _needsFullPropsApply;
}

+ (ComponentDescriptorProvider)componentDescriptorProvider
{
  return concreteComponentDescriptorProvider<VietMapNavigationComponentDescriptor>();
}

- (instancetype)initWithFrame:(CGRect)frame
{
  if (self = [super initWithFrame:frame]) {
    static const auto defaultProps = std::make_shared<const VietMapNavigationProps>();
    _props = defaultProps;
    [self claimSharedViewIfNeeded];
  }
  return self;
}

#pragma mark - Shared Swift view lifecycle

// The Swift view is a singleton whose removeFromSuperview() swaps in a brand-new instance —
// that is how the library tears a map down between mounts (the Paper manager relies on the
// same behavior). So: claim the CURRENT shared instance on mount, and detach on recycle, which
// triggers that swap exactly like a Paper unmount. Only one mounted map is supported at a time
// (same constraint as the Paper path).
- (void)claimSharedViewIfNeeded
{
  if (self.contentView != nil && self.contentView == VietMapNavigationView.shared) {
    return;
  }
  // Detach any stale instance FIRST. The Swift removeFromSuperview() swaps the singleton
  // unconditionally, so reading `shared` is only valid AFTER the detach — otherwise we would
  // host an instance the module no longer talks to.
  if (self.contentView != nil) {
    [self unbindEventHandlersFrom:(VietMapNavigationView *)self.contentView];
    self.contentView = nil;
  }
  VietMapNavigationView *sharedView = VietMapNavigationView.shared;
  self.contentView = sharedView;
  [self bindEventHandlersTo:sharedView];
  _needsFullPropsApply = YES;
}

- (void)prepareForRecycle
{
  VietMapNavigationView *navView = (VietMapNavigationView *)self.contentView;
  if (navView != nil) {
    // Unbind first so the discarded map instance cannot emit into this recycled view, then
    // detach — which runs the Swift singleton swap (fresh map for the next mount).
    [self unbindEventHandlersFrom:navView];
    self.contentView = nil;
  }
  [super prepareForRecycle];
  _needsFullPropsApply = YES;
}

#pragma mark - Events

// Bridges every RCTDirectEventBlock the Swift view exposes AND the codegen spec declares.
// (Spec-only events — onCancelNavigation, onNavigationRunning, onRouteBuilding, onMapReady,
// onMilestoneEvent, onWaypointArrival — have no iOS source yet, exactly as on the Paper path.
// Swift-only blocks — onError, startAlert, stopAlert — are not in the codegen spec, so they
// cannot be emitted through Fabric.)
#define VMNAV_BIND_EVENT(eventName)                                                                \
  navView.eventName = ^(NSDictionary *body) {                                                      \
    RCTVietMapNavigationComponentView *strongSelf = weakSelf;                                      \
    if (strongSelf == nil) {                                                                       \
      return;                                                                                      \
    }                                                                                              \
    const auto emitter =                                                                           \
        std::static_pointer_cast<const VietMapNavigationEventEmitter>(strongSelf->_eventEmitter);  \
    if (emitter == nullptr) {                                                                      \
      return;                                                                                      \
    }                                                                                              \
    emitter->eventName({.json = JSONStringFromDictionary(body)});                                  \
  }

- (void)bindEventHandlersTo:(VietMapNavigationView *)navView
{
  __weak RCTVietMapNavigationComponentView *weakSelf = self;
  VMNAV_BIND_EVENT(onRouteProgressChange);
  VMNAV_BIND_EVENT(onNavigationFinished);
  VMNAV_BIND_EVENT(onNavigationCancelled);
  VMNAV_BIND_EVENT(onArrival);
  VMNAV_BIND_EVENT(onUserOffRoute);
  VMNAV_BIND_EVENT(onRouteBuilt);
  VMNAV_BIND_EVENT(onRouteBuildFailed);
  VMNAV_BIND_EVENT(onMapLongClick);
  VMNAV_BIND_EVENT(onMapClick);
  VMNAV_BIND_EVENT(onMapMove);
  VMNAV_BIND_EVENT(onMapMoveEnd);
  VMNAV_BIND_EVENT(onNewRouteSelected);
}

- (void)unbindEventHandlersFrom:(VietMapNavigationView *)navView
{
  navView.onRouteProgressChange = nil;
  navView.onNavigationFinished = nil;
  navView.onNavigationCancelled = nil;
  navView.onArrival = nil;
  navView.onUserOffRoute = nil;
  navView.onRouteBuilt = nil;
  navView.onRouteBuildFailed = nil;
  navView.onMapLongClick = nil;
  navView.onMapClick = nil;
  navView.onMapMove = nil;
  navView.onMapMoveEnd = nil;
  navView.onNewRouteSelected = nil;
}

#pragma mark - Props

- (void)updateProps:(const Props::Shared &)props oldProps:(const Props::Shared &)oldProps
{
  [self claimSharedViewIfNeeded];

  // _props still holds the previous props here — [super updateProps:] swaps it at the end.
  const auto &newProps = *std::static_pointer_cast<const VietMapNavigationProps>(props);
  const auto &prevProps = *std::static_pointer_cast<const VietMapNavigationProps>(_props);
  const BOOL force = _needsFullPropsApply;
  _needsFullPropsApply = NO;

  VietMapNavigationView *navView = (VietMapNavigationView *)self.contentView;

  // A freshly claimed Swift view holds default values, so on `force` every prop the app actually
  // set must be pushed; "actually set" is approximated as "differs from the codegen default"
  // (empty string / 0) so absent optional props don't stomp the Swift-side defaults.
  // NOTE: apiKey, baseUrl and navigationTiltLevel exist in the codegen spec (Android uses them)
  // but the iOS implementation reads its key/URLs from Info.plist — nothing to map here.

  if ((force || newProps.styleUrl != prevProps.styleUrl) && !newProps.styleUrl.empty()) {
    navView.styleUrl = RCTNSStringFromString(newProps.styleUrl);
  }

  if (force || newProps.shouldSimulateRoute != prevProps.shouldSimulateRoute) {
    navView.shouldSimulateRoute = newProps.shouldSimulateRoute;
  }

  const auto &llz = newProps.initialLatLngZoom;
  const auto &prevLlz = prevProps.initialLatLngZoom;
  const bool llzChanged = llz.lat != prevLlz.lat || llz.lng != prevLlz.lng || llz.zoom != prevLlz.zoom;
  const bool llzIsSet = llz.lat != 0 || llz.lng != 0 || llz.zoom != 0;
  if ((force || llzChanged) && llzIsSet) {
    navView.initialLatLngZoom = @{@"lat" : @(llz.lat), @"lng" : @(llz.lng), @"zoom" : @(llz.zoom)};
  }

  const auto &pad = newProps.navigationPadding;
  const auto &prevPad = prevProps.navigationPadding;
  const bool padChanged = pad.left != prevPad.left || pad.top != prevPad.top ||
      pad.right != prevPad.right || pad.bottom != prevPad.bottom;
  const bool padIsSet = pad.left != 0 || pad.top != 0 || pad.right != 0 || pad.bottom != 0;
  if ((force || padChanged) && padIsSet) {
    navView.navigationPadding =
        @{@"left" : @(pad.left), @"top" : @(pad.top), @"right" : @(pad.right), @"bottom" : @(pad.bottom)};
  }

  if ((force || newProps.navigationZoomLevel != prevProps.navigationZoomLevel) &&
      newProps.navigationZoomLevel != 0) {
    navView.navigationZoomLevel = newProps.navigationZoomLevel;
  }

  if ((force || newProps.apiKeyAlert != prevProps.apiKeyAlert) && !newProps.apiKeyAlert.empty()) {
    navView.apiKeyAlert = RCTNSStringFromString(newProps.apiKeyAlert);
  }

  if ((force || newProps.apiIDAlert != prevProps.apiIDAlert) && !newProps.apiIDAlert.empty()) {
    navView.apiIDAlert = RCTNSStringFromString(newProps.apiIDAlert);
  }

  if ((force || newProps.puckImage != prevProps.puckImage) && !newProps.puckImage.empty()) {
    navView.puckImage = RCTNSStringFromString(newProps.puckImage);
  }

  if ((force || newProps.puckImageWidth != prevProps.puckImageWidth) && newProps.puckImageWidth != 0) {
    navView.puckImageWidth = (CGFloat)newProps.puckImageWidth;
  }

  if ((force || newProps.puckImageHeight != prevProps.puckImageHeight) && newProps.puckImageHeight != 0) {
    navView.puckImageHeight = (CGFloat)newProps.puckImageHeight;
  }

  if (force || newProps.puckImageRotation != prevProps.puckImageRotation) {
    navView.puckImageRotation = (CGFloat)newProps.puckImageRotation;
  }

  [super updateProps:props oldProps:oldProps];
}

@end

Class<RCTComponentViewProtocol> VietMapNavigationCls(void)
{
  return RCTVietMapNavigationComponentView.class;
}

#endif /* RCT_NEW_ARCH_ENABLED */
