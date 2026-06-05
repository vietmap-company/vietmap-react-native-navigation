import { IVietMapNavigationProps } from './typings';
declare const VietMapNavigation: (props: IVietMapNavigationProps) => any;
export * from './enums/vietmap_event_type';
export * from './models/route_progress_data';
export { VietMapNavigationModule } from './native_modules';
export * from './models/coordinates';
export * from './typings';
export { VietMapNavigationController, VehicleType } from './controller';
export default VietMapNavigation;
