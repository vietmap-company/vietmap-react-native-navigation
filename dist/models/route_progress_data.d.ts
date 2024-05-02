export interface CurrentStep {
    instructions: string;
    distance: number;
    expectedTravelTime: number;
}
export interface CurrentLeg {
    distance: number;
    expectedTravelTime: number;
    steps: CurrentStep[];
}
export interface Location {
    latitude: number;
    longitude: number;
    provider: string;
    speed: number;
    bearing: number;
    altitude: number;
    accuracy: number;
    speedAccuracyMetersPerSecond: number;
}
export interface SnappedLocation extends Location {
}
export interface NavigationProgressData {
    nativeEvent?: {
        data: {
            distanceRemaining: number;
            durationRemaining: number;
            distanceTraveled: number;
            legIndex: number;
            currentLegDistanceRemaining: number;
            currentLegDistanceTraveled: number;
            currentStepInstruction: string;
            distanceToNextTurn: number;
            currentModifier: string;
            currentModifierType: string;
            currentLeg: CurrentLeg;
            location: Location;
            snappedLocation: SnappedLocation;
        };
    };
}
export declare function fromNavigationProgressDataJson(json: string): NavigationProgressData;
