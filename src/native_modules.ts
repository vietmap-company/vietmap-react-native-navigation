import {NativeModules} from 'react-native';

interface VietMapNavigationModuleType {
  testModule: () => void;
  buildRoute: (points: any[], vehicle?: string) => void;
  startNavigation: () => void;
  stopNavigation: () => void;
  recenter: () => void;
  mute: () => void;
  buildAndStartNavigation: () => void;
  finishNavigation: () => void;
  overView: () => void;
  clearRoute: () => void;
}

export const VietMapNavigationModule: VietMapNavigationModuleType = NativeModules.VietMapNavigationModule;

// Test if module is available
if (!VietMapNavigationModule) {
  console.error('VietMapNavigationModule is not available. Make sure the native module is properly linked.');
} else {
  console.log('VietMapNavigationModule is available');
}

export default VietMapNavigationModule;