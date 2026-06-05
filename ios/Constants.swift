//
//  Constants.swift
//  vietmap-react-native-navigation
//
//  Course-tracking camera constants. These mirror the VietMapNavigation (Pods) SDK's internal
//  RouteMapViewController behavior, which this view bypasses. Keep them in sync with the SDK if
//  it ever changes its course-tracking layout.
//

import Foundation
import CoreGraphics
import CoreLocation

enum Constants {
    // MARK: - Course Tracking Camera Constants

    // Fraction of the visible content height (top → bottom) where the GPS puck should sit.
    // Must match NavigationMapView's internal userAnchorPoint (contentFrame.height * 0.8).
    static let courseTrackingUserAnchorRatio: CGFloat = 0.8
    // Camera pitch (degrees) used while tracking the user's course.
    static let courseTrackingCameraPitch: CGFloat = 60
    // Fallback camera animation duration (seconds) when the real GPS tick interval is unknown.
    static let courseTrackingDefaultCameraDuration: TimeInterval = 1.0
    // Clamp the dynamic per-tick camera duration so a long GPS gap (tunnel, signal loss)
    // doesn't produce a single multi-second crawl, and a burst of ticks doesn't stutter.
    static let courseTrackingMinCameraDuration: TimeInterval = 0.5
    static let courseTrackingMaxCameraDuration: TimeInterval = 2.0
}
