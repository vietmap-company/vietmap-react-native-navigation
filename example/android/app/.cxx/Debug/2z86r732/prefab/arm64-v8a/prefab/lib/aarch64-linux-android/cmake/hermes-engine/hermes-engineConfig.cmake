if(NOT TARGET hermes-engine::libhermes)
add_library(hermes-engine::libhermes SHARED IMPORTED)
set_target_properties(hermes-engine::libhermes PROPERTIES
    IMPORTED_LOCATION "/Users/dev/.gradle/caches/8.10.2/transforms/691d43b1ec560e813632d527702289a6/transformed/jetified-hermes-android-0.76.5-debug/prefab/modules/libhermes/libs/android.arm64-v8a/libhermes.so"
    INTERFACE_INCLUDE_DIRECTORIES "/Users/dev/.gradle/caches/8.10.2/transforms/691d43b1ec560e813632d527702289a6/transformed/jetified-hermes-android-0.76.5-debug/prefab/modules/libhermes/include"
    INTERFACE_LINK_LIBRARIES ""
)
endif()

