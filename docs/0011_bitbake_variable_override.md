# bitbake variable override

## 参考文档

* [3.1. Basic Syntax](https://www.yoctoproject.org/docs/latest/bitbake-user-manual/bitbake-user-manual.html#basic-syntax)
* [MACHINEOVERRIDES](https://www.yoctoproject.org/docs/latest/mega-manual/mega-manual.html#var-MACHINEOVERRIDES)

## 代码分析

* meta-fsl-bsp-release/imx/meta-bsp/conf/machine/imx8qmmek.conf
  ```
  #@TYPE: Machine
  #@NAME: i.MX 8QM B0 MEK
  #@DESCRIPTION: i.MX 8QuadMax Multisensory Enablement Kit (MEK) board
  #@MAINTAINER: Jun Zhu <junzhu@nxp.com>
  
  MACHINEOVERRIDES =. "mx8:mx8qm:"
  
  require conf/machine/include/imx-base.inc
  require conf/machine/include/arm/arch-arm64.inc
  
  MACHINE_FEATURES_append = " qca6174"
  [...省略]
  ```
* meta-fsl-bsp-release/imx/meta-bsp/conf/machine/include/imx-base.inc
  ```
  [...省略]
  SERIAL_CONSOLE = "115200 ttymxc0"
  SERIAL_CONSOLE_mxs = "115200 ttyAMA0"
  
  KERNEL_IMAGETYPE = "zImage"
  KERNEL_IMAGETYPE_mx8 = "Image"
  
  MACHINE_FEATURES = "usbgadget usbhost vfat alsa touchscreen"
  
  MACHINE_FEATURES_append_mx8qm  = " xen"
  MACHINE_FEATURES_append_mx8qxp = " xen"
  [...省略]
  ```
* 检查确认`KERNEL_IMAGETYPE`
  ```
  zengjf@UbuntuServer:imx8-build-wayland$ bitbake example -e  | grep KERNEL_IMAGETYPE
  # $KERNEL_IMAGETYPE [4 operations]
  KERNEL_IMAGETYPE="Image"
  # $KERNEL_IMAGETYPES [2 operations]
  #     [_defaultval] "${KERNEL_IMAGETYPE}"
  #     [doc] "The list of types of kernel to build for a device, usually set by the machine configuration files and defaults to KERNEL_IMAGETYPE."
  #   "${KERNEL_IMAGETYPE}"
  KERNEL_IMAGETYPES="Image"
  # $KERNEL_IMAGETYPE_mx8
  KERNEL_IMAGETYPE_mx8="Image"
  ```