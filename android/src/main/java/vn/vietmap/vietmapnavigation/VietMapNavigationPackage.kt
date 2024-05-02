package vn.vietmap.vietmapnavigation

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager


class VietMapNavigationPackage : ReactPackage {
  override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {

    val modules: MutableList<NativeModule> = ArrayList()

    modules.add(VietMapNavigationModule.instance)

    return modules
  }

  override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
    return listOf(VietMapNavigationManager(reactContext))
  }
}