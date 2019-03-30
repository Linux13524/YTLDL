cmake_minimum_required(VERSION 3.6)

include(cmake/conan.cmake)

if(NOT DEFINED ANDROID)
    message(FATAL_ERROR "This is an android project!" )
endif()

set(CMAKE_CXX_COMPILER_VERSION "8.0")


### CONAN
# Set conan settings
if(${CMAKE_ANDROID_ARCH_ABI} STREQUAL armeabi-v7a)
    set(CONAN_SETTINGS arch=armv7)
elseif(${CMAKE_ANDROID_ARCH_ABI} STREQUAL arm64-v8a)
    set(CONAN_SETTINGS arch=armv8)
elseif(${CMAKE_ANDROID_ARCH_ABI} STREQUAL x86)
    set(CONAN_SETTINGS arch=x86)
elseif(${CMAKE_ANDROID_ARCH_ABI} STREQUAL x86_64)
    set(CONAN_SETTINGS arch=x86_64)
endif()

set(CONAN_SETTINGS ${CONAN_SETTINGS} compiler.version=8 compiler.libcxx=libc++ os.api_level=21)

# Runs conan install to get dependencies
conan_cmake_run(CONANFILE conanfile.txt
        BASIC_SETUP
        SETTINGS ${CONAN_SETTINGS})

### native.so
add_library(native SHARED
        src/main/cpp/filesystem.cpp
        src/main/cpp/handle.h
        src/main/cpp/channel.cpp
        src/main/cpp/playlist.cpp
        src/main/cpp/video.cpp)

# Workaround for "error: cannot find -lpthread" TODO: fix
list(REMOVE_ITEM CONAN_LIBS "pthread")

target_link_libraries(native
        PRIVATE ${CONAN_LIBS}
        PRIVATE log)

target_include_directories(native PRIVATE
        $<BUILD_INTERFACE:${CONAN_INCLUDE_DIRS}>)

#if(${CONAN_SETTINGS_BUILD_TYPE} STREQUAL "Debug")
#    message("Paths ${CONAN_SRC_DIRS_YOUTUBE_LIST_DOWNLOADER}")
#    file(GLOB_RECURSE debug_sources "${CONAN_SRC_DIRS_YOUTUBE_LIST_DOWNLOADER}/*.cpp")
#    message("Paths2 ${debug_sources}")
#endif()
