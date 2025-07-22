const {getDefaultConfig, mergeConfig} = require('@react-native/metro-config');
const path = require('path');

/**
 * Metro configuration
 * https://reactnative.dev/docs/metro
 *
 * @type {import('metro-config').MetroConfig}
 */

const config = {
  resolver: {
    alias: {
      '@vietmap/vietmap-react-native-navigation': path.resolve(__dirname, '../src'),
      '@assets': path.resolve(__dirname, './assets'),
    },
    nodeModulesPaths: [
      path.resolve(__dirname, 'node_modules'),
      path.resolve(__dirname, '../node_modules'),
      path.resolve(__dirname, '../../node_modules'),
    ],
  },
  watchFolders: [
    path.resolve(__dirname, '../src'),
    path.resolve(__dirname, './assets'),
    path.resolve(__dirname, '../'),
  ],
};

module.exports = mergeConfig(getDefaultConfig(__dirname), config);
