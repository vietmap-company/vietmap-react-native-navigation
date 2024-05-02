@objc(VietMapNavigationManager)
class VietMapNavigationManager: RCTViewManager {
    override func view() -> UIView! {
        return VietMapNavigationView.shared;
    }
  override init() {
      
  }

  override static func requiresMainQueueSetup() -> Bool {
    return true
  }
}

