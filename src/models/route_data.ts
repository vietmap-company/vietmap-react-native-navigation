export interface RouteOptions {
  uuid: string;
  access_token: string;
  annotations: string;
  steps: boolean;
  overview: string;
  banner_instructions: boolean;
  geometries: string;
  roundabout_exits: boolean;
  language: string;
  voice_units: string;
  alternatives: boolean;
  baseUrl: string;
  continue_straight: boolean;
  voice_instructions: boolean;
  user: string;
  coordinates: [number, number][];
  profile: string;
  bearings: string;
}

export interface BannerInstruction {
  primary: {
    modifier: string;
    type: string;
    components: {
      type: string;
      text: string;
    }[];
    text: string;
  };
  distanceAlongGeometry: number;
}

export interface VoiceInstruction {
  ssmlAnnouncement: string;
  announcement: string;
  distanceAlongGeometry: number;
}

export interface Maneuver {
  modifier: string;
  type: string;
  bearing_before: number;
  instruction: string;
  bearing_after: number;
  location: [number, number];
}

export interface Step {
  intersections: {
    out?: number;
    entry: boolean[];
    bearings: number[];
    location: [number, number];
  }[];
  weight: number;
  bannerInstructions?: BannerInstruction[];
  voiceInstructions?: VoiceInstruction[];
  mode: string;
  name: string;
  maneuver: Maneuver;
  geometry: string;
  duration: number;
  distance: number;
}

export interface Leg {
  summary: string;
  steps: Step[];
}

export interface Data {
  routeOptions: RouteOptions;
  weight_name: string;
  duration: number;
  geometry: string;
  legs: Leg[];
}

export interface RouteData {
  nativeEvent: {
    data: Data;
  };
}
