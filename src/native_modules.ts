import {NativeModules} from 'react-native';

interface VietMapNavigationModuleType {
  buildRoute: (points: any[], vehicle?: string) => void;
  startNavigation: () => void;
  stopNavigation: () => void;
  recenter: () => void;
  mute: () => void;
  buildAndStartNavigation: () => void;
  finishNavigation: () => void;
  overView: () => void;
  clearRoute: () => void;
  startSpeedAlert: () => void;
  stopSpeedAlert: () => void;
  isSpeedAlertActive: () => Promise<boolean>;
  configureAlertAPI: (apiKey: string, apiID: string) => void;
  configVehicleSpeedAlert: (vehicleId: string, vehicleType: number, seats: number, weight: number) => void;
}

export const VietMapNavigationModule: VietMapNavigationModuleType = NativeModules.VietMapNavigationModule;

// Test if module is available
if (!VietMapNavigationModule) {
  console.error('VietMapNavigationModule is not available. Make sure the native module is properly linked.');
} else {
  console.log('VietMapNavigationModule is available');
}

export default VietMapNavigationModule;