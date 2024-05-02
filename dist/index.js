import * as React from 'react';
import { requireNativeComponent, StyleSheet } from 'react-native';
const VietMapNavigation = (props) => {
    return <RNVietMapNavigation style={styles.container} {...props}/>;
};
const RNVietMapNavigation = requireNativeComponent('VietMapNavigation');
const styles = StyleSheet.create({
    container: {
        flex: 1
    },
});
// export * from './models/route_progress_model'
export * from './enums/vietmap_event_type';
export * from './models/route_progress_data';
export { VietMapNavigationModule } from './native_modules';
export default VietMapNavigation;
export * from './models/coordinates';
export * from './typings';
export { VietMapNavigationController } from './controller';
