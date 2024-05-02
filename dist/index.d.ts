import * as React from 'react';
import { IVietMapNavigationProps } from './typings';
declare const VietMapNavigation: (props: IVietMapNavigationProps) => React.JSX.Element;
export * from './enums/vietmap_event_type';
export * from './models/route_progress_data';
export { VietMapNavigationModule } from './native_modules';
export default VietMapNavigation;
export * from './models/coordinates';
export * from './typings';
export { VietMapNavigationController } from './controller';
