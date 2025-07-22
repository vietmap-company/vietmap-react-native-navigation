module.exports = {
  dependency: {
    platforms: {
      android: {
        sourceDir: '../android',
        packageImportPath: 'import vn.vietmap.vietmapnavigation.VietMapNavigationPackage;',
        packageName: 'vn.vietmap.vietmapnavigation.VietMapNavigationPackage'
      },
      ios: {
        podspecPath: '../vietmap-react-native-navigation.podspec'
      }
    }
  }
};
