import { NativeModules } from 'react-native';
export const VietMapNavigationModule = NativeModules.VietMapNavigationModule;
// Test if module is available
if (!VietMapNavigationModule) {
    console.error('VietMapNavigationModule is not available. Make sure the native module is properly linked.');
}
else {
    console.log('VietMapNavigationModule is available');
}
export default VietMapNavigationModule;
