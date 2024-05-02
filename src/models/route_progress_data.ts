// Step interface represents a step in a leg with instructions, distance, and expected travel time
export interface CurrentStep {
  instructions: string; // Instructions for the step
  distance: number; // Distance for the step
  expectedTravelTime: number; // Expected travel time for the step
}

// Leg interface represents a leg of the journey with distance, expected travel time, and steps
export interface CurrentLeg {
  distance: number; // Total distance of the leg
  expectedTravelTime: number; // Expected travel time for the leg
  steps: CurrentStep[]; // Array of steps in the leg
}

// Location interface represents a location with latitude, longitude, and other details
export interface Location {
  latitude: number; // Latitude coordinate
  longitude: number; // Longitude coordinate
  provider: string; // Provider of the location data
  speed: number; // Speed in meters per second
  bearing: number; // Bearing in degrees
  altitude: number; // Altitude in meters
  accuracy: number; // Accuracy of the location in meters
  speedAccuracyMetersPerSecond: number; // Speed accuracy in meters per second
}

// SnappedLocation extends Location to represent a snapped location
export interface SnappedLocation extends Location {}

// NavigationData interface represents the main navigation data structure
export interface NavigationProgressData {
  nativeEvent?: {
    data: {
      distanceRemaining: number; // Remaining distance to destination
      durationRemaining: number; // Remaining duration to destination
      distanceTraveled: number; // Distance traveled so far
      legIndex: number; // Current leg index
      currentLegDistanceRemaining: number; // Remaining distance of current leg
      currentLegDistanceTraveled: number; // Distance traveled in current leg
      currentStepInstruction: string; // Instruction for the current step
      distanceToNextTurn: number; // Distance to the next turn
      currentModifier: string; // Current modifier (e.g., 'right')
      currentModifierType: string; // Type of the current modifier (e.g., 'rotary')
      currentLeg: CurrentLeg; // Current leg details
      location: Location; // Current location
      snappedLocation: SnappedLocation; // Snapped location}
    };
  };
}
function fromJson(json: string): NavigationProgressData {
  const parsedJson = JSON.parse(json);
  const dataString = parsedJson.data;
  const data = JSON.parse(dataString) as NavigationProgressData;
  return data;
}
// fromJson function to parse JSON object and return NavigationData object
export function fromNavigationProgressDataJson(json: string): NavigationProgressData {
  return JSON.parse(json) as NavigationProgressData;
}
