# Yocto Toolchain

## 参考文档

* [Yocto toolchain installation for out of Yocto builds](http://variwiki.com/index.php?title=Yocto_Toolchain_installation&release=RELEASE_MORTY_BETA_DART-6UL)

## 编译交叉工具链

* `bitbake meta-toolchain`

## Install the toolchain/SDK

* check script
  ```
  zengjf@zengjf:imx8-build-wayland$ ls tmp/deploy/sdk/*
  tmp/deploy/sdk/fsl-imx-wayland-glibc-x86_64-meta-toolchain-aarch64-toolchain-4.14-sumo.host.manifest
  tmp/deploy/sdk/fsl-imx-wayland-glibc-x86_64-meta-toolchain-aarch64-toolchain-4.14-sumo.sh
  tmp/deploy/sdk/fsl-imx-wayland-glibc-x86_64-meta-toolchain-aarch64-toolchain-4.14-sumo.target.manifest
  tmp/deploy/sdk/fsl-imx-wayland-glibc-x86_64-meta-toolchain-aarch64-toolchain-4.14-sumo.testdata.json
  ```
* `tmp/deploy/sdk/fsl-imx-wayland-glibc-x86_64-meta-toolchain-aarch64-toolchain-4.14-sumo.sh`
  * toolchain path: `/opt/fsl-imx-wayland/4.14-sumo`
* `source /opt/fsl-imx-wayland/4.14-sumo/environment-setup-aarch64-poky-linux`