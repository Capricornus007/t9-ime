# SPDX-FileCopyrightText: 2015 - 2024 Rime community
#
# SPDX-License-Identifier: GPL-3.0-or-later

set(BOOST_VERSION 1.92.0)

if(NOT EXISTS "boost-${BOOST_VERSION}.tar.xz")
  message(STATUS "Downloading Boost ${BOOST_VERSION} ......")
  file(
    DOWNLOAD
    "https://github.com/boostorg/boost/releases/download/boost-${BOOST_VERSION}/boost-${BOOST_VERSION}-cmake.tar.xz"
    boost-${BOOST_VERSION}.tar.xz
    EXPECTED_HASH
      SHA256=9bed76128d4e46755dbe818487788c6fceb6f72b378f4daa49b7e1e600d9088d
    SHOW_PROGRESS)

  message(STATUS "Remove older version Boost")
  file(REMOVE_RECURSE "${CMAKE_SOURCE_DIR}/boost")
endif()

if(NOT EXISTS "${CMAKE_SOURCE_DIR}/boost")
  message(STATUS "Extracting Boost ${BOOST_VERSION} ......")
  file(ARCHIVE_EXTRACT INPUT boost-${BOOST_VERSION}.tar.xz DESTINATION
       ${CMAKE_SOURCE_DIR})
  file(RENAME "boost-${BOOST_VERSION}" boost)
endif()

# vmd 是我們自己加的一項：librime 1.17 的 rime_api.cc 有 #include <boost/vmd/is_empty.hpp>，
# 上游 Trime 這份清單沒有它（它的 librime 較舊），少了交叉編會在第一個來源檔就斷。
set(BOOST_INCLUDE_LIBRARIES
    algorithm
    crc
    dll
    interprocess
    range
    regex
    scope_exit
    signals2
    utility
    uuid
    vmd)

add_subdirectory(boost EXCLUDE_FROM_ALL)
