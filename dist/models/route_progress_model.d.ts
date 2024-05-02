export interface ProgressChangeData {
    routeData: RouteData;
    eventType: string;
}
export interface RouteData {
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
}
export interface CurrentLeg {
    distance: number;
    expectedTravelTime: number;
    steps: Step[];
}
export interface Step {
    instructions: string;
    distance: number;
    expectedTravelTime: number;
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
export interface SnappedLocation {
    latitude: number;
    longitude: number;
    provider: string;
    speed: number;
    bearing: number;
    altitude: number;
    accuracy: number;
    speedAccuracyMetersPerSecond: number;
}
export declare function fromProgressChangeDataJson(json: string): ProgressChangeData | null;
