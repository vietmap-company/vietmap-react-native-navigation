if(NOT TARGET fbjni::fbjni)
add_library(fbjni::fbjni SHARED IMPORTED)
set_target_properties(fbjni::fbjni PROPERTIES
    IMPORTED_LOCATION "/Users/dev/.gradle/caches/8.10.2/transforms/ebeb1423ad5262e535284e118f13fa9e/transformed/jetified-fbjni-0.6.0/prefab/modules/fbjni/libs/android.x86/libfbjni.so"
    INTERFACE_INCLUDE_DIRECTORIES "/Users/dev/.gradle/caches/8.10.2/transforms/ebeb1423ad5262e535284e118f13fa9e/transformed/jetified-fbjni-0.6.0/prefab/modules/fbjni/include"
    INTERFACE_LINK_LIBRARIES ""
)
endif()

