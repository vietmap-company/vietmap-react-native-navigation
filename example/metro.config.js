const {getDefaultConfig, mergeConfig} = require('@react-native/metro-config');
const path = require('path');

/**
 * Metro configuration
 * https://reactnative.dev/docs/metro
 *
 * @type {import('metro-config').MetroConfig}
 */

const defaultConfig = getDefaultConfig(__dirname);

const root = path.resolve(__dirname, '..');

// The library root has its own node_modules (react/react-native as devDependencies for tsc &
// codegen). Library source under ../src must NOT resolve those copies: two react-native copies
// in one bundle means the Fabric spec registers its view config into one
// ReactNativeViewConfigRegistry while the renderer reads another → "View config getter callback
// for component `VietMapNavigation` must be a function (received `undefined`)".
// Block the library's copies and pin resolution to the example's.
const singletonModules = ['react', 'react-native'];
const escapeRegExp = s => s.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
const exclusionList = require('metro-config/src/defaults/exclusionList');

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
    blockList: exclusionList(
      singletonModules.map(
        m =>
          new RegExp(`^${escapeRegExp(path.join(root, 'node_modules', m))}/.*$`),
      ),
    ),
    extraNodeModules: Object.fromEntries(
      singletonModules.map(m => [m, path.join(__dirname, 'node_modules', m)]),
    ),
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
