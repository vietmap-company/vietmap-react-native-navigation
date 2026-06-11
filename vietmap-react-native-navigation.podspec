require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

# TargetsToChangeToDynamic = ['MapboxMobileEvents']
TargetsToChangeToDynamic = []

$RNMBNAV = Object.new

def $RNMBNAV.post_install(installer)
  installer.pod_targets.each do |pod|
    if TargetsToChangeToDynamic.include?(pod.name)
      if pod.send(:build_type) != Pod::BuildType.dynamic_framework
        pod.instance_variable_set(:@build_type,Pod::BuildType.dynamic_framework)
        puts "* Changed #{pod.name} to `#{pod.send(:build_type)}`"
        fail "Unable to change build_type" unless mobile_events_target.send(:build_type) == Pod::BuildType.dynamic_framework
      end
    end
  end
end

def $RNMBNAV.pre_install(installer)
  installer.aggregate_targets.each do |target|
    target.pod_targets.select { |p| TargetsToChangeToDynamic.include?(p.name) }.each do |mobile_events_target|
      mobile_events_target.instance_variable_set(:@build_type,Pod::BuildType.dynamic_framework)
      puts "* Changed #{mobile_events_target.name} to #{mobile_events_target.send(:build_type)}"
      fail "Unable to change build_type" unless mobile_events_target.send(:build_type) == Pod::BuildType.dynamic_framework
    end
  end
end

Pod::Spec.new do |s|
  s.name         = "vietmap-react-native-navigation"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.description  = <<-DESC
                  VietMap turn-by-turn routing for React Native.
                   DESC
  s.homepage     = "https://github.com/vietmap-company/vietmap-react-native-navigation"
  s.license    = { :type => "BSD-3-Clause", :file => "LICENSE" }
  s.authors      = { "VietMap" => "maps-api.support@vietmap.vn" }
  s.platforms    = { :ios => "13.4" }
  s.source       = { :git => "https://github.com/vietmap-company/vietmap-react-native-navigation.git", :tag => "#{s.version}" }

  # .mm files host the Fabric (New Architecture) component view; they compile to nothing
  # when RCT_NEW_ARCH_ENABLED is not set, so old-arch apps are unaffected.
  s.source_files = "ios/**/*.{h,m,mm,swift}"
  s.requires_arc = true

  s.dependency 'VietMapNavigation', '3.3.0'
  s.dependency 'VietMapCoreNavigation', '3.1.0'
  s.dependency 'VietmapTrackingSDK', '1.2.2'
  s.swift_version = '5.0'

  # Fix: non-modular-include-in-framework-module with Xcode 16+/iOS 26 SDK
  s.pod_target_xcconfig = {
    'CLANG_ALLOW_NON_MODULAR_INCLUDES_IN_FRAMEWORK_MODULES' => 'YES',
    'ALLOW_NON_MODULAR_INCLUDES_IN_FRAMEWORK_MODULES' => 'YES'
  }

  # New Architecture: pulls React-RCTFabric/ReactCodegen/folly etc. and sets the
  # RCT_NEW_ARCH_ENABLED flag when the app enables it. Falls back to React-Core
  # for RN versions that don't ship the helper.
  if respond_to?(:install_modules_dependencies, true)
    install_modules_dependencies(s)
  else
    s.dependency "React-Core"
  end
end

