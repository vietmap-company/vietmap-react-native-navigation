# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.0.0] - 2026-05-06

### ⚠️ BREAKING CHANGES

- **React Native Version Requirement**:
  - Minimum React Native version: `0.72.0` → **`0.72.0`**
  - Tested with React Native: **`0.85.2`**
  - Upgraded from RN 0.80.1 to 0.85.2 (5 minor version jump)
  - **Known Issue**: iOS build may fail with RN 0.85.2 due to `TurboCxxModule.cpp` missing. Recommend using RN 0.84.0 until React Native team resolves this issue.

- **Event Name Changes** (iOS & Android):
  - `userOffRoute` → **`onUserOffRoute`**
  - Update your event handlers:
    ```tsx
    // Old
    <VietMapNavigation userOffRoute={(e) => {}} />
    
    // New
    <VietMapNavigation onUserOffRoute={(e) => {}} />
    ```

- **Android Build Requirements**:
  - Java version: `1.8` → **`17`**
  - Gradle version: `8.5` → **`8.9`**
  - Update `android/build.gradle`:
    ```gradle
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = '17'
    }
    ```

- **Package Manager**:
  - Migrated from npm to yarn
  - Removed `package-lock.json`, added `yarn.lock`

### Added

- **New Architecture Support**:
  - Added Codegen configuration in `package.json`
  - Added `VietMapNavigationNativeComponent.ts` for Fabric/TurboModules
  - Supports both Old and New Architecture
  
- **Android Fabric Support**:
  - New `VietMapEvent.kt` class for Fabric-compatible event dispatch
  - `PluginUtilities.kt` now uses `UIManagerHelper.getEventDispatcher` for Fabric
  - Added `ViewManagerDelegate` support in `VietMapNavigationManager.kt`

### Changed

- **Dependencies**:
  - React: `19.1.0` → `19.2.3`
  - React Native: `0.80.1` → `0.85.2`
  - Android SDK: `maps-sdk-navigation-android` `2.5.1` → `2.6.0`
  - Lifecycle: Migrated from deprecated `lifecycle-extensions` to:
    - `lifecycle-runtime-ktx: 2.8.7`
    - `lifecycle-viewmodel-ktx: 2.8.7`

- **iOS Updates**:
  - Updated `connectedScenes` API for iOS compatibility
  - Minimum iOS version: **13.0+** (required for `UIApplication.shared.connectedScenes`)
  - Requires Xcode 14.0+ for React Native 0.85

- **Gradle Configuration**:
  - Updated AGP (Android Gradle Plugin) compatibility
  - Improved build performance with Gradle 8.9

### Fixed

- **iOS Property Name**: Fixed typo `initialLatLngZoom["long"]` → `initialLatLngZoom["lng"]`
- **Type Safety**: Improved TypeScript type definitions for event handlers

### Migration Guide

#### Step 1: Update Dependencies

```bash
# Update React Native and dependencies
yarn add react-native@0.84.0  # Use 0.84.0 instead of 0.85.2 for stability
yarn add react@19.2.3
```

#### Step 2: Update Android Configuration

```gradle
// android/build.gradle
android {
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = '17'
    }
}
```

#### Step 3: Update Event Handlers

```tsx
// Replace all instances
<VietMapNavigation
  onUserOffRoute={(event) => {
    console.log('User is off route:', event);
  }}
/>
```

#### Step 4: Clean and Rebuild

```bash
# Android
cd android && ./gradlew clean && cd ..
rm -rf android/build android/.gradle

# iOS
cd ios
rm -rf Pods Podfile.lock
pod install
cd ..

# Rebuild
yarn android
yarn ios
```

### Known Issues

- **iOS Build Error with RN 0.85.2**:
  - Error: `TurboCxxModule.cpp` file not found
  - Root Cause: React Native 0.85.2 restructured ReactCommon module
  - Workaround: Use React Native 0.84.0 until official fix is released
  - Tracking: React Native GitHub issues

### Recommendations

- **Short Term**: Use React Native `0.84.0` for production stability
- **Long Term**: Wait for React Native `0.85.3` or `0.86.0` to resolve iOS build issues
- Test thoroughly on both iOS and Android before deploying to production
- Review breaking changes in your codebase, especially event handlers

## [1.7.3] - 2026-02-04

### Changed
- **Android Dependencies Update**:
  - Updated `maps-sdk-navigation-android` from v2.5.1 to v2.6.0
  - Improved navigation stability and performance
  - Enhanced compatibility with React Native New Architecture

### Fixed
- **Build Compatibility**: Resolved JitPack dependency resolution issues
  - Fixed build errors when using `newArchEnabled=true`
  - Improved Gradle dependency fetching reliability
  - Better error handling for network-related build failures

### Note
- **New Architecture Support**: This SDK works optimally with `newArchEnabled=false`
- For projects using `newArchEnabled=true` that encounter build errors, set `newArchEnabled=false` in `android/gradle.properties`
- Full New Architecture support is planned for future releases

## [1.7.2] - 2025-11-25

### Added
- **16KB Page Size Support**: Enhanced Android compatibility for Google Play Store requirements
  - Updated `maps-sdk-android` from v2.6.0 to v4.1.0 with full 16KB page size support
  - Updated `vietmap-tracking-sdk-android` from v1.2.2 to v1.2.3 with 16KB alignment

### Changed
- **Android Dependencies Update**:
  - Maps SDK: Upgraded to v4.1.0 for improved performance and 16KB support
  - Tracking SDK: Updated to v1.2.3 with enhanced memory alignment
  - Improved APK compatibility with newer Android devices
  - Better memory management and reduced potential crashes on 16KB page devices

### Note
- For best compatibility with Google Play's 16KB requirement, consider filtering ABIs:
  ```gradle
  android {
      defaultConfig {
          ndk {
              abiFilters 'arm64-v8a', 'x86_64'
          }
      }
  }
  ```

## [1.7.1] - 2025-10-07

### Improved
- **Alert System Performance**: Enhanced alert functionality and user experience
  - Optimized alert detection algorithms for faster response times
  - Improved audio alert timing to prevent overlapping notifications
  - Enhanced alert priority system to handle multiple simultaneous alerts
  - Better memory management for continuous alert processing
  - Reduced CPU usage during active navigation with alerts
  - Improved alert accuracy with enhanced distance calculations
  - Enhanced voice synthesis quality and volume control
  - Better integration with system audio settings and interruptions
  - Optimized battery usage during extended navigation sessions with alerts

### Fixed
- **Alert Stability**: Resolved edge cases in alert system operation
  - Fixed memory leaks during continuous alert monitoring
  - Improved error handling when GPS signal is weak or lost
  - Better handling of rapid alert succession scenarios
  - Fixed audio conflicts with other navigation sounds
  - Resolved timing issues with alert notifications

## [1.7.0] - 2025-10-06

### Added
- **Traffic Alert System**: Enhanced alert support for road signs and traffic infrastructure
  - **Camera Alert**: Voice warnings when approaching speed cameras
  - **Traffic Light Alert**: Audio notifications when approaching traffic lights
  - **Toll Plaza Alert**: Voice alerts when approaching toll plazas and payment stations
  - Cross-platform support for iOS and Android with native voice synthesis
  - Real-time detection of road infrastructure from map data
  - Customizable alert distance and timing for different alert types

### Fixed
- **Xcode 16.1 Compatibility**: Resolved iOS build issues with latest Xcode version
  - Fixed VietMapDirections.swift module loading failures in Xcode 16.1
  - Updated iOS deployment target to 15.1 for React Native 0.80.1 compatibility
  - Disabled explicit module compilation to resolve Swift package conflicts
  - Updated CocoaPods configuration for iOS SDK 26.0 support
  - Enhanced build system compatibility with latest iOS development tools
  - Fixed Node.js path resolution in Xcode build phases

## [1.6.2] - 2025-09-16

### Fixed
- **Android Play Store Compatibility**: Fixed "App does not support 16kb" error for Google Play Store submission
  - Added support for 16KB page size compatibility as required by Google Play Store
  - Updated Android manifest and build configuration to support new Android runtime requirements
  - Enhanced memory allocation handling for devices with 16KB page sizes
  - Improved compatibility with Android 15+ devices that use 16KB memory pages
  - Fixed native library loading issues related to page size alignment
  - Updated build.gradle configurations to ensure Play Store compliance

## [1.6.1] - 2025-09-12

### Improved
- **Speed Alert Performance**: Optimized speed alert reading and processing
  - Enhanced SDK initialization timing to prevent configuration conflicts
  - Improved vehicle configuration with proper type mapping (VehicleType.fromValue for Android, VMVehicleType.fromValue for iOS)
  - Better error handling for uninitialized SDK states
  - Automatic retry mechanism for alert API configuration when SDK becomes available
- **Documentation**: Updated README with step-by-step speed alert configuration guide
  - Clear instructions for configuring alert API credentials
  - Comprehensive vehicle configuration examples
  - Best practices for calling configuration methods in useEffect

## [1.6.0] - 2025-09-09

### Added
- **Speed Alert System**: Complete speed alert functionality with voice warnings and status monitoring
  - New `VietMapNavigationController.startSpeedAlert()` to enable speed limit warnings
  - New `VietMapNavigationController.stopSpeedAlert()` to disable speed limit warnings
  - New `VietMapNavigationController.isSpeedAlertActive()` returns Promise<boolean> for status checking
  - Cross-platform voice alert support for speed limit violations
  - Real-time speed monitoring with audio warnings when exceeding speed limits
  - Automatic speed limit detection from map data
- **Speed Alert Status Check**: Added real-time speed alert status monitoring
  - Cross-platform support for Android and iOS
  - Android implementation uses `speedAlertManager.isSpeedAlertActive()`
  - iOS implementation uses `trackingSDK.isSpeedAlertCurrentlyActive()`
  - Added TypeScript interface with proper Promise<boolean> return type
  - Comprehensive error handling for both platforms

### Improved
- **Speed Alert API**: Enhanced speed alert functionality with comprehensive control
  - Voice warning system for speed limit violations
  - Better state management for speed alert features
  - Real-time status checking capability for UI state synchronization
  - Automatic speed limit reading from navigation data
  - Background location support for continuous monitoring
  - Added usage examples and documentation in README.md
- **iOS Configuration**: Enhanced iOS setup requirements for speed alert functionality
  - Added UIBackgroundModes location requirement for continuous monitoring
  - Improved location permission handling for speed alert features
- **Documentation**: Updated README.md with complete speed alert API documentation
  - Added comprehensive speed alert usage examples
  - Included async/await patterns and error handling examples
  - Enhanced iOS configuration requirements for UIBackgroundModes
  - Added speed limit warning system documentation

## [1.5.1] - 2025-07-22

### Fixed
- **Native Module Export**: Fixed VietMapNavigationModule export issue in React Native 0.80.1
  - Corrected ReactContextBaseJavaModule constructor to properly accept ReactApplicationContext
  - Fixed singleton pattern implementation for native module
  - Improved module registration in VietMapNavigationPackage
- **Auto-linking**: Removed deprecated `react-native.config.js` file
  - React Native 0.60+ handles auto-linking automatically
  - No manual configuration needed for modern React Native versions

### Improved
- **API Call Logging**: Added detailed logging for navigation API requests
  - Better debugging support for custom baseUrl configurations
  - Enhanced error tracking for route building failures
- **Native Module Interface**: Added TypeScript interface for VietMapNavigationModule
  - Better type safety and IDE support
  - Clearer method signatures and return types

## [1.5.0] - 2025-01-22

### Added
- **React 19.1.0 Support**: Full compatibility with React 19.1.0
- **React Native 0.80.1 Support**: Updated to support React Native 0.80.1
- **Configurable Base URL**: Added `baseUrl` prop to allow custom API endpoints
  - Default value: `https://maps.vietmap.vn/api/navigations/route/`
  - Can be overridden from React Native side
- **Troubleshooting Section**: Added comprehensive troubleshooting guide in README
  - Fix for iOS "Multiple commands produce Assets.car" error
  - Clear step-by-step solutions with code examples

### Changed
- **Upgraded Dependencies**:
  - React: 18.3.1 → 19.1.0
  - React Native: 0.76.5 → 0.80.1
  - TypeScript: Updated to 5.0.0
- **Android Build System**:
  - Android Gradle Plugin: 8.1.2 → 8.7.3
  - Gradle Wrapper: 8.0/8.10.2 → 8.11.1
  - Compile/Target SDK: 34 → 35
- **TypeScript Configuration**:
  - Target: ES6 → ES2020
  - Added modern ES modules support
  - Enhanced type safety options
- **Compatibility Requirements**:
  - React: >=18.0.0 (previously *)
  - React Native: >=0.70.0 (previously *)

### Fixed
- **Kotlin Null Safety**: Fixed compilation errors with nullable ReadableMap objects
- **Build Compatibility**: Resolved build issues with newer Android toolchain
- **Package Namespace**: Fixed deprecated package attribute warnings

### Technical Details
- Updated peerDependencies to specify minimum supported versions
- Enhanced error handling for null values in native modules
- Improved documentation with clearer installation requirements
- Added support for modern JavaScript features (ES2020)

### Breaking Changes
- Minimum React version is now 18.0.0
- Minimum React Native version is now 0.70.0
- Removed support for older Android build tools

---

## [1.4.0] - Previous Release

### Features
- Initial React Native navigation implementation
- Turn-by-turn navigation for Vietnam
- Support for multiple vehicle types (driving, cycling, walking, motorcycle)
- Real-time traffic integration
- Voice instructions
- Route optimization

---

## Migration Guide from 1.4.0 to 1.5.0

### For React < 18.0.0 users:
```bash
npm install react@^19.1.0 react-native@^0.80.1
```

### For Android developers:
1. Update your `android/build.gradle`:
   ```groovy
   classpath "com.android.tools.build:gradle:8.7.3"
   ```

2. Update `android/gradle/wrapper/gradle-wrapper.properties`:
   ```properties
   distributionUrl=https\://services.gradle.org/distributions/gradle-8.11.1-all.zip
   ```

3. Update `android/app/build.gradle` if needed:
   ```groovy
   compileSdkVersion 35
   targetSdkVersion 35
   ```

### For iOS developers:
If you encounter "Multiple commands produce Assets.car" error, add the fix from the README troubleshooting section to your Podfile.

### New Features Usage:
```typescript
// Using custom baseUrl
<VietMapNavigation
  baseUrl="https://your-custom-api.com/route/"
  // ... other props
/>
```

All notable changes to this project will be documented in this file.

## [1.5.0] - 2025-07-22

### Added
- Support for React 19.1.0
- Support for React Native 0.80.1
- Troubleshooting section in README for common iOS build issues
- TypeScript configuration improvements for modern ES versions

### Changed
- Updated minimum peerDependency versions:
  - React: >=18.0.0 (tested up to 19.1.0)
  - React Native: >=0.70.0 (tested up to 0.80.1)
- Updated Android build tools to AGP 8.7.3
- Updated compileSdkVersion and targetSdkVersion to 35
- Enhanced TypeScript configuration with ES2020 target

### Fixed
- Added fix for iOS "Multiple commands produce Assets.car" build error
- Improved compatibility with latest React Native versions

### Documentation
- Added troubleshooting guide for common iOS build issues
- Updated installation requirements with clear compatibility information
- Added step-by-step solution for Assets.car duplicate issue

## [1.4.0] - Previous Release

### Features
- Turn-by-turn navigation for React Native
- Support for multiple vehicle types (driving, cycling, motorcycle, walking)
- Real-time navigation progress tracking
- Voice instructions and banner instructions
- Route overview and recenter functionality
