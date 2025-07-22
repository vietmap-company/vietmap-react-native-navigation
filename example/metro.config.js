const {getDefaultConfig, mergeConfig} = require('@react-native/metro-config');
const path = require('path');

/**
 * Metro configuration
 * https://reactnative.dev/docs/metro
 *
 * @type {import('metro-config').MetroConfig}
 */

const defaultConfig = getDefaultConfig(__dirname);

const config = {
  resolver: {
    ...defaultConfig.resolver,
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
  transformer: {
    ...defaultConfig.transformer,
    getTransformOptions: async () => ({
      transform: {
        experimentalImportSupport: false,
        inlineRequires: true,
      },
    }),
  },
};

module.exports = mergeConfig(defaultConfig, config);
