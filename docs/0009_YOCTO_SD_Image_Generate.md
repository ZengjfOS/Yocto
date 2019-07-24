# YOCTO SD Image

## 参考文档

* [bitbake Anonymous Python Functions](https://www.yoctoproject.org/docs/1.6/bitbake-user-manual/bitbake-user-manual.html#anonymous-python-functions)
* [bitbake Tasks](https://www.yoctoproject.org/docs/1.6/bitbake-user-manual/bitbake-user-manual.html#tasks)
* [bitbake Variable Flags](https://www.yoctoproject.org/docs/1.6/bitbake-user-manual/bitbake-user-manual.html#variable-flags)
* [bitbake Variable Flag Syntax](https://www.yoctoproject.org/docs/1.6/bitbake-user-manual/bitbake-user-manual.html#variable-flag-syntax)
* [bitbake Usage and syntax -- -DD](https://www.yoctoproject.org/docs/2.4.2/bitbake-user-manual/bitbake-user-manual.html#usage-and-syntax)

## YOCTO command

* 创建或者进入编译环境：
  * `DISTRO=fsl-imx-wayland MACHINE=imx8qmmek source ./fsl-setup-release.sh -b imx8-build-wayland`
  * `source setup-environment imx8-build-wayland`
* `cd imx8-build-wayland`
* 编译：
  * `bitbake fsl-image-qt5-validation-imx -c cleanall -DD`
  * `bitbake fsl-image-qt5-validation-imx`
  * `bitbake fsl-image-qt5-validation-imx -DD`
  * `bitbake fsl-image-qt5-validation-imx -DD -c image_ext4`
  * `bitbake fsl-image-qt5-validation-imx -DD -c image_ext4 -f`
  * `bitbake fsl-image-qt5-validation-imx -DD -c do_image_sdcard -f`

## local.conf

```
zengjf@UbuntuServer:imx8-build-wayland$ cat conf/local.conf
MACHINE ??= 'imx8qmmek'
DISTRO ?= 'fsl-imx-wayland'
PACKAGE_CLASSES ?= "package_rpm"
EXTRA_IMAGE_FEATURES ?= "debug-tweaks"
USER_CLASSES ?= "buildstats image-mklibs image-prelink"
PATCHRESOLVE = "noop"
BB_DISKMON_DIRS ??= "\
    STOPTASKS,${TMPDIR},1G,100K \
    STOPTASKS,${DL_DIR},1G,100K \
    STOPTASKS,${SSTATE_DIR},1G,100K \
    STOPTASKS,/tmp,100M,100K \
    ABORT,${TMPDIR},100M,1K \
    ABORT,${DL_DIR},100M,1K \
    ABORT,${SSTATE_DIR},100M,1K \
    ABORT,/tmp,10M,1K"
PACKAGECONFIG_append_pn-qemu-native = " sdl"
PACKAGECONFIG_append_pn-nativesdk-qemu = " sdl"
PACKAGECONFIG_append_pn-qtmultimedia=" gstreamer"
CONF_VERSION = "1"
DISTRO_FEATURES_append = " xen"

IMAGE_INSTALL_append = " gstreamer1.0-libav"
IMAGE_INSTALL_append = " gstreamer1.0 \
 gstreamer1.0-plugins-bad \
 gstreamer1.0-plugins-base \
 gstreamer1.0-plugins-ugly \
 gstreamer1.0-plugins-good \
 qtmultimedia \
 qtquickcontrols \
 qtquickcontrols2 "

IMAGE_INSTALL_append += " opencv wget"
PACKAGECONFIG_append_pn-opencv_mx8 = " dnn opencl python2 qt5"
PACKAGECONFIG_remove_pn-opencv_mx8 = "python3"
IMAGE_INSTALL_append += " sshd roslaunch rostopic roscpp-dev std-msgs-dev"
IMAGE_INSTALL_append += " libuv sophus i2c-tools"

LICENSE_FLAGS_WHITELIST = "commercial"
DL_DIR ?= "${BSPDIR}/downloads/"
ACCEPT_FSL_EULA = "1"
```

## 查询images目录

```
zengjf@UbuntuServer:sources$ pwd
/home/zengjf/imx8-yocto-ga/sources
zengjf@UbuntuServer:sources$ find * -iname images
meta-freescale/recipes-fsl/images
meta-freescale/recipes-graphics/images
meta-freescale-distro/recipes-fsl/images
meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images
meta-fsl-bsp-release/imx/meta-sdk/recipes-fsl/images
meta-openembedded/meta-initramfs/recipes-bsp/images
meta-openembedded/meta-xfce/recipes-core/images
poky/meta-selftest/recipes-test/images
poky/meta-skeleton/recipes-multilib/images
poky/bitbake/lib/bb/ui/icons/images
poky/bitbake/lib/toaster/toastergui/static/css/images
poky/meta/recipes-sato/images
poky/meta/recipes-rt/images
poky/meta/recipes-extended/images
poky/meta/recipes-connectivity/connman/connman-gnome/images
poky/meta/recipes-graphics/images
poky/meta/recipes-core/images
zengjf@UbuntuServer:sources$ ls meta-freescale/recipes-fsl/images
fsl-image-mfgtool-initramfs.bb
zengjf@UbuntuServer:sources$ ls meta-freescale/recipes-graphics/images
core-image-weston.bbappend
zengjf@UbuntuServer:sources$ ls meta-freescale-distro/recipes-fsl/images
fsl-image-machine-test.bb  fsl-image-multimedia.bb  fsl-image-multimedia-full.bb
zengjf@UbuntuServer:sources$ ls meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images
fsl-image-qt5.bb  fsl-image-qt5-validation-imx.bb
zengjf@UbuntuServer:sources$ ls meta-fsl-bsp-release/imx/meta-sdk/recipes-fsl/images
fsl-image-gui.bb  fsl-image-machine-test.bbappend  fsl-image-mfgtool-initramfs.bbappend  fsl-image-multimedia.bbappend  fsl-image-validation-imx.bb
zengjf@UbuntuServer:sources$ ls meta-openembedded/meta-initramfs/recipes-bsp/images
initramfs-debug-image.bb  initramfs-kexecboot-image.bb  initramfs-kexecboot-klibc-image.bb
zengjf@UbuntuServer:sources$ ls meta-openembedded/meta-xfce/recipes-core/images
core-image-minimal-xfce.bb
```

## 指定编译打包后的文件系统类型

* meta-fsl-bsp-release/imx/meta-sdk/conf/distro/fsl-imx-wayland.conf
  ```
  # i.MX DISTRO for Wayland without X11
  
  include conf/distro/include/fsl-imx-base.inc
  include conf/distro/include/fsl-imx-preferred-env.inc
  
  DISTRO = "fsl-imx-wayland"
  
  # Remove conflicting backends
  DISTRO_FEATURES_remove = "directfb x11 "
  DISTRO_FEATURES_append = " wayland pam systemd"
  ```
* meta-fsl-bsp-release/imx/meta-sdk/conf/distro/include/fsl-imx-base.inc
  ```
  [...省略]
  IMX_DEFAULT_DISTRO_FEATURES = "opengl ptest multiarch bluez"
  # Enable vulkan distro feature only for mx8
  IMX_DEFAULT_DISTRO_FEATURES_append_mx8 = " vulkan"
  IMX_DEFAULT_EXTRA_RDEPENDS = "packagegroup-core-boot"
  IMX_DEFAULT_EXTRA_RRECOMMENDS = "kernel-module-af-packet"
  IMAGE_FSTYPES = "tar.bz2 ext4 sdcard.bz2"
  
  BBMASK += "meta-freescale/recipes-graphics/wayland/wayland_1.15.%.bbappend"
  BBMASK += "meta-freescale/recipes-graphics/drm/libdrm_%.bbappend"
  BBMASK += "poky/meta/recipes-graphics/vulkan/vulkan_1.0.65.2.bb"
  [...省略]
  ```
* `IMAGE_FSTYPES = "tar.bz2 ext4 sdcard.bz2"`

## 查询fsl-image-qt5-validation-imx目录

```
zengjf@UbuntuServer:sources$ find * -iname fsl-image-qt5-validation-imx*
meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb
```

## 文件系统合成分析

* meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb
  ```
  DESCRIPTION = "Freescale Image - Adds Qt5"
  LICENSE = "MIT"
  
  require recipes-fsl/images/fsl-image-validation-imx.bb
  
  [...省略]
  ```
* meta-fsl-bsp-release/imx/meta-sdk/recipes-fsl/images/fsl-image-validation-imx.bb
  ```
  # Copyright (C) 2015 Freescale Semiconductor
  # Copyright 2017-2018 NXP
  # Released under the MIT license (see COPYING.MIT for the terms)
  
  DESCRIPTION = "Freescale Image to validate i.MX machines. \
  This image contains everything used to test i.MX machines including GUI, \
  demos and lots of applications. This creates a very large image, not \
  suitable for production."
  LICENSE = "MIT"
  
  inherit core-image
  
  [...省略]
  ```
* poky/meta/classes/core-image.bbclass
  ```
  # Common code for generating core reference images
  #
  # Copyright (C) 2007-2011 Linux Foundation
  
  [...省略]
  
  inherit image
  ```
* poky/meta/classes/image.bbclass
  ```
  [...省略]
  
  python () {
      vardeps = set()
      # We allow CONVERSIONTYPES to have duplicates. That avoids breaking
      # derived distros when OE-core or some other layer independently adds
      # the same type. There is still only one command for each type, but
      # presumably the commands will do the same when the type is the same,
      # even when added in different places.
      #
      # Without de-duplication, gen_conversion_cmds() below
      # would create the same compression command multiple times.
      ctypes = set(d.getVar('CONVERSIONTYPES').split())
      old_overrides = d.getVar('OVERRIDES', False)
  
      def _image_base_type(type):
          basetype = type
          for ctype in ctypes:
              if type.endswith("." + ctype):
                  basetype = type[:-len("." + ctype)]
                  break
  
          if basetype != type:
              # New base type itself might be generated by a conversion command.
              basetype = _image_base_type(basetype)
  
          return basetype
  
      basetypes = {}
      alltypes = d.getVar('IMAGE_FSTYPES').split()
      typedeps = {}
  
      if d.getVar('IMAGE_GEN_DEBUGFS') == "1":
          debugfs_fstypes = d.getVar('IMAGE_FSTYPES_DEBUGFS').split()
          for t in debugfs_fstypes:
              alltypes.append("debugfs_" + t)
  
      def _add_type(t):
          baset = _image_base_type(t)
          input_t = t
          if baset not in basetypes:
              basetypes[baset]= []
          if t not in basetypes[baset]:
              basetypes[baset].append(t)
          debug = ""
          if t.startswith("debugfs_"):
              t = t[8:]
              debug = "debugfs_"
          deps = (d.getVar('IMAGE_TYPEDEP_' + t) or "").split()
          vardeps.add('IMAGE_TYPEDEP_' + t)
          if baset not in typedeps:
              typedeps[baset] = set()
          deps = [debug + dep for dep in deps]
          for dep in deps:
              if dep not in alltypes:
                  alltypes.append(dep)
              _add_type(dep)
              basedep = _image_base_type(dep)
              typedeps[baset].add(basedep)
  
          if baset != input_t:
              _add_type(baset)
  
      for t in alltypes[:]:
          _add_type(t)
  
      d.appendVarFlag('do_image', 'vardeps', ' '.join(vardeps))
  
      maskedtypes = (d.getVar('IMAGE_TYPES_MASKED') or "").split()
      maskedtypes = [dbg + t for t in maskedtypes for dbg in ("", "debugfs_")]
  
      for t in basetypes:
          vardeps = set()
          cmds = []
          subimages = []
          realt = t
  
          if t in maskedtypes:
              continue
  
          localdata = bb.data.createCopy(d)
          debug = ""
          if t.startswith("debugfs_"):
              setup_debugfs_variables(localdata)
              debug = "setup_debugfs "
              realt = t[8:]
          localdata.setVar('OVERRIDES', '%s:%s' % (realt, old_overrides))
          localdata.setVar('type', realt)
          # Delete DATETIME so we don't expand any references to it now
          # This means the task's hash can be stable rather than having hardcoded
          # date/time values. It will get expanded at execution time.
          # Similarly TMPDIR since otherwise we see QA stamp comparision problems
          # Expand PV else it can trigger get_srcrev which can fail due to these variables being unset
          localdata.setVar('PV', d.getVar('PV'))
          localdata.delVar('DATETIME')
          localdata.delVar('DATE')
          localdata.delVar('TMPDIR')
          vardepsexclude = (d.getVarFlag('IMAGE_CMD_' + realt, 'vardepsexclude', True) or '').split()
          for dep in vardepsexclude:
              localdata.delVar(dep)
  
          image_cmd = localdata.getVar("IMAGE_CMD")
          vardeps.add('IMAGE_CMD_' + realt)
          if image_cmd:
              cmds.append("\t" + image_cmd)
          else:
              bb.fatal("No IMAGE_CMD defined for IMAGE_FSTYPES entry '%s' - possibly invalid type name or missing support class" % t)
          cmds.append(localdata.expand("\tcd ${IMGDEPLOYDIR}"))
  
          # Since a copy of IMAGE_CMD_xxx will be inlined within do_image_xxx,
          # prevent a redundant copy of IMAGE_CMD_xxx being emitted as a function.
          d.delVarFlag('IMAGE_CMD_' + realt, 'func')
  
          rm_tmp_images = set()
          def gen_conversion_cmds(bt):
              for ctype in sorted(ctypes):
                  if bt.endswith("." + ctype):
                      type = bt[0:-len(ctype) - 1]
                      if type.startswith("debugfs_"):
                          type = type[8:]
                      # Create input image first.
                      gen_conversion_cmds(type)
                      localdata.setVar('type', type)
                      cmd = "\t" + (localdata.getVar("CONVERSION_CMD_" + ctype) or localdata.getVar("COMPRESS_CMD_" + ctype))
                      if cmd not in cmds:
                          cmds.append(cmd)
                      vardeps.add('CONVERSION_CMD_' + ctype)
                      vardeps.add('COMPRESS_CMD_' + ctype)
                      subimage = type + "." + ctype
                      if subimage not in subimages:
                          subimages.append(subimage)
                      if type not in alltypes:
                          rm_tmp_images.add(localdata.expand("${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}"))
  
          for bt in basetypes[t]:
              gen_conversion_cmds(bt)
  
          localdata.setVar('type', realt)
          if t not in alltypes:
              rm_tmp_images.add(localdata.expand("${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}"))
          else:
              subimages.append(realt)
  
          # Clean up after applying all conversion commands. Some of them might
          # use the same input, therefore we cannot delete sooner without applying
          # some complex dependency analysis.
          for image in sorted(rm_tmp_images):
              cmds.append("\trm " + image)
  
          after = 'do_image'
          for dep in typedeps[t]:
              after += ' do_image_%s' % dep.replace("-", "_").replace(".", "_")
  
          task = "do_image_%s" % t.replace("-", "_").replace(".", "_")
  
          d.setVar(task, '\n'.join(cmds))
          d.setVarFlag(task, 'func', '1')
          d.setVarFlag(task, 'fakeroot', '1')
  
          d.appendVarFlag(task, 'prefuncs', ' ' + debug + ' set_image_size')
          d.prependVarFlag(task, 'postfuncs', ' create_symlinks')
          d.appendVarFlag(task, 'subimages', ' ' + ' '.join(subimages))
          d.appendVarFlag(task, 'vardeps', ' ' + ' '.join(vardeps))
          d.appendVarFlag(task, 'vardepsexclude', 'DATETIME DATE ' + ' '.join(vardepsexclude))
  
          # DEBUG: Adding task do_image_sdcard before do_image_complete, after do_image do_image_ext4
          # DEBUG: Adding task do_image_ext4 before do_image_complete, after do_image
          # DEBUG: Adding task do_image_tar before do_image_complete, after do_image
          bb.debug(2, "Adding task %s before %s, after %s" % (task, 'do_image_complete', after))
          bb.build.addtask(task, 'do_image_complete', after, d)
  }
  
  [...省略]
  ```
* poky/meta/classes/image_types.bbclass
  ```
  [...省略]
  
  oe_mkext234fs () {
          fstype=$1
          extra_imagecmd=""
  
          if [ $# -gt 1 ]; then
                  shift
                  extra_imagecmd=$@
          fi
  
          # If generating an empty image the size of the sparse block should be large
          # enough to allocate an ext4 filesystem using 4096 bytes per inode, this is
          # about 60K, so dd needs a minimum count of 60, with bs=1024 (bytes per IO)
          eval local COUNT=\"0\"
          eval local MIN_COUNT=\"60\"
          if [ $ROOTFS_SIZE -lt $MIN_COUNT ]; then
                  eval COUNT=\"$MIN_COUNT\"
          fi
          # Create a sparse image block
          bbdebug 1 Executing "dd if=/dev/zero of=${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.$fstype seek=$ROOTFS_SIZE count=$COUNT bs=1024"
          dd if=/dev/zero of=${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.$fstype seek=$ROOTFS_SIZE count=$COUNT bs=1024
          bbdebug 1 "Actual Rootfs size:  `du -s ${IMAGE_ROOTFS}`"
          bbdebug 1 "Actual Partion size: `stat -c '%s' ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.$fstype`"
          bbdebug 1 Executing "mkfs.$fstype -F $extra_imagecmd ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.$fstype -d ${IMAGE_ROOTFS}"
          mkfs.$fstype -F $extra_imagecmd ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.$fstype -d ${IMAGE_ROOTFS}
          # Error codes 0-3 indicate successfull operation of fsck (no errors or errors corrected)
          fsck.$fstype -pvfD ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.$fstype || [ $? -le 3 ]
  }
  
  IMAGE_CMD_ext2 = "oe_mkext234fs ext2 ${EXTRA_IMAGECMD}"
  IMAGE_CMD_ext3 = "oe_mkext234fs ext3 ${EXTRA_IMAGECMD}"
  IMAGE_CMD_ext4 = "oe_mkext234fs ext4 ${EXTRA_IMAGECMD}"
  
  [...省略]
  ```
* meta-fsl-bsp-release/imx/meta-bsp/classes/image_types_fsl.bbclass
  ```
  inherit image_types
  
  IMAGE_BOOTLOADER ?= "u-boot"
  
  IMAGE_BOOTFILES ?= ""
  IMAGE_BOOTFILES_DEPENDS ?= ""
  
  IMAGE_BOOTFILES += \
      "${@bb.utils.contains('COMBINED_FEATURES', 'xen', 'xen', '', d)}"
  IMAGE_BOOTFILES_DEPENDS += \
      "${@bb.utils.contains('COMBINED_FEATURES', 'xen', 'imx-xen:do_deploy', '', d)}"
  
  IMX_BOOT_SEEK ?= "33"
  
  # Handle u-boot suffixes
  UBOOT_SUFFIX ?= "bin"
  UBOOT_SUFFIX_SDCARD ?= "${UBOOT_SUFFIX}"
  
  #
  # Handles i.MX mxs bootstream generation
  #
  MXSBOOT_NAND_ARGS ?= ""
  
  # Include required software for optee
  IMAGE_INSTALL_append = " ${@bb.utils.contains('COMBINED_FEATURES', 'optee', 'packagegroup-fsl-optee-imx', '', d)} "
  
  # Include userspace xen tools
  IMAGE_INSTALL_append = " ${@bb.utils.contains('COMBINED_FEATURES', 'xen', 'imx-xen-base imx-xen-hypervisor', '', d)} "
  
  # Include jailhouse kernel module and tools
  IMAGE_INSTALL_append = " ${@bb.utils.contains('COMBINED_FEATURES', 'jailhouse', 'jailhouse', '', d)} "
  
  # IMX Bootlets Linux bootstream
  do_image_linux.sb[depends] = "elftosb-native:do_populate_sysroot \
                                imx-bootlets:do_deploy \
                                virtual/kernel:do_deploy"
  IMAGE_LINK_NAME_linux.sb = ""
  IMAGE_CMD_linux.sb () {
          kernel_bin="`readlink ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${MACHINE}.bin`"
          kernel_dtb="`readlink ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${MACHINE}.dtb || true`"
          linux_bd_file=imx-bootlets-linux.bd-${MACHINE}
          if [ `basename $kernel_bin .bin` = `basename $kernel_dtb .dtb` ]; then
                  # When using device tree we build a zImage with the dtb
                  # appended on the end of the image
                  linux_bd_file=imx-bootlets-linux.bd-dtb-${MACHINE}
                  cat $kernel_bin $kernel_dtb \
                      > $kernel_bin-dtb
                  rm -f ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${MACHINE}.bin-dtb
                  ln -s $kernel_bin-dtb ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${MACHINE}.bin-dtb
          fi
  
          # Ensure the file is generated
          rm -f ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.linux.sb
          (cd ${DEPLOY_DIR_IMAGE}; elftosb -z -c $linux_bd_file -o ${IMAGE_NAME}.linux.sb)
  
          # Remove the appended file as it is only used here
          rm -f ${DEPLOY_DIR_IMAGE}/$kernel_bin-dtb
  }
  
  # IMX Bootlets barebox bootstream
  do_image_barebox-mxsboot-sdcard[depends] = "elftosb-native:do_populate_sysroot \
                                              u-boot-mxsboot-native:do_populate_sysroot \
                                              imx-bootlets:do_deploy \
                                              barebox:do_deploy"
  IMAGE_CMD_barebox.mxsboot-sdcard () {
          barebox_bd_file=imx-bootlets-barebox_ivt.bd-${MACHINE}
  
          # Ensure the files are generated
          (cd ${DEPLOY_DIR_IMAGE}; rm -f ${IMAGE_NAME}.barebox.sb ${IMAGE_NAME}.barebox.mxsboot-sdcard; \
           elftosb -f mx28 -z -c $barebox_bd_file -o ${IMAGE_NAME}.barebox.sb; \
           mxsboot sd ${IMAGE_NAME}.barebox.sb ${IMAGE_NAME}.barebox.mxsboot-sdcard)
  }
  
  # U-Boot mxsboot generation to SD-Card
  UBOOT_SUFFIX_SDCARD_mxs ?= "mxsboot-sdcard"
  do_image_uboot-mxsboot-sdcard[depends] = "u-boot-mxsboot-native:do_populate_sysroot \
                                            u-boot:do_deploy"
  IMAGE_CMD_uboot.mxsboot-sdcard = "mxsboot sd ${DEPLOY_DIR_IMAGE}/u-boot-${MACHINE}.${UBOOT_SUFFIX} \
                                               ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.uboot.mxsboot-sdcard"
  
  do_image_uboot-mxsboot-nand[depends] = "u-boot-mxsboot-native:do_populate_sysroot \
                                          u-boot:do_deploy"
  IMAGE_CMD_uboot.mxsboot-nand = "mxsboot ${MXSBOOT_NAND_ARGS} nand \
                                               ${DEPLOY_DIR_IMAGE}/u-boot-${MACHINE}.${UBOOT_SUFFIX} \
                                               ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.uboot.mxsboot-nand"
  
  # Boot partition volume id
  BOOTDD_VOLUME_ID ?= "Boot ${MACHINE}"
  
  # Boot partition size [in KiB]
  BOOT_SPACE ?= "8192"
  
  # Barebox environment size [in KiB]
  BAREBOX_ENV_SPACE ?= "512"
  
  # Set alignment in KiB
  IMAGE_ROOTFS_ALIGNMENT_mx6 ?= "4096"
  IMAGE_ROOTFS_ALIGNMENT_mx7 ?= "4096"
  IMAGE_ROOTFS_ALIGNMENT_mx8 ?= "8192"
  
  do_image_sdcard[depends] = "parted-native:do_populate_sysroot \
                              dosfstools-native:do_populate_sysroot \
                              mtools-native:do_populate_sysroot \
                              virtual/kernel:do_deploy \
                              ${IMAGE_BOOTLOADER}:do_deploy \
                              ${IMAGE_BOOTFILES_DEPENDS} \
                             "
  
  SDCARD = "${IMGDEPLOYDIR}/${IMAGE_NAME}.rootfs.sdcard"
  
  SDCARD_GENERATION_COMMAND_mxs = "generate_mxs_sdcard"
  SDCARD_GENERATION_COMMAND_mx25 = "generate_imx_sdcard"
  SDCARD_GENERATION_COMMAND_mx5 = "generate_imx_sdcard"
  SDCARD_GENERATION_COMMAND_mx6 = "generate_imx_sdcard"
  SDCARD_GENERATION_COMMAND_mx7 = "generate_imx_sdcard"
  SDCARD_GENERATION_COMMAND_mx8 = "generate_imx_sdcard"
  SDCARD_GENERATION_COMMAND_vf = "generate_imx_sdcard"
  
  
  #
  # Generate the boot image with the boot scripts and required Device Tree
  # files
  _generate_boot_image() {
          local boot_part=$1
  
          # Create boot partition image
          BOOT_BLOCKS=$(LC_ALL=C parted -s ${SDCARD} unit b print \
                            | awk "/ $boot_part / { print substr(\$4, 1, length(\$4 -1)) / 1024 }")
  
          # mkdosfs will sometimes use FAT16 when it is not appropriate,
          # resulting in a boot failure from SYSLINUX. Use FAT32 for
          # images larger than 512MB, otherwise let mkdosfs decide.
          if [ $(expr $BOOT_BLOCKS / 1024) -gt 512 ]; then
                  FATSIZE="-F 32"
          fi
  
          rm -f ${WORKDIR}/boot.img
          mkfs.vfat -n "${BOOTDD_VOLUME_ID}" -S 512 ${FATSIZE} -C ${WORKDIR}/boot.img $BOOT_BLOCKS
  
          mcopy -i ${WORKDIR}/boot.img -s ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${MACHINE}.bin ::/${KERNEL_IMAGETYPE}
  
          # Copy boot scripts
          for item in ${BOOT_SCRIPTS}; do
                  src=`echo $item | awk -F':' '{ print $1 }'`
                  dst=`echo $item | awk -F':' '{ print $2 }'`
  
                  mcopy -i ${WORKDIR}/boot.img -s ${DEPLOY_DIR_IMAGE}/$src ::/$dst
          done
  
          # Copy device tree file
          if test -n "${KERNEL_DEVICETREE}"; then
                  for DTS_FILE in ${KERNEL_DEVICETREE}; do
                          DTS_BASE_NAME=`basename ${DTS_FILE} | awk -F "." '{print $1}'`
                          if [ -e "${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${DTS_BASE_NAME}.dtb" ]; then
                                  kernel_bin="`readlink ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${MACHINE}.bin`"
                                  kernel_bin_for_dtb="`readlink ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${DTS_BASE_NAME}.dtb | sed "s,$DTS_BASE_NAME,${MACHINE},g;s,\.dtb$,.bin,g"`"
                                  if [ $kernel_bin = $kernel_bin_for_dtb ]; then
                                          mcopy -i ${WORKDIR}/boot.img -s ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${DTS_BASE_NAME}.dtb ::/${DTS_BASE_NAME}.dtb
                                  fi
                          else
                                  bbfatal "${DTS_FILE} does not exist."
                          fi
                  done
          fi
  
          # Copy extlinux.conf to images that have U-Boot Extlinux support.
          if [ "${UBOOT_EXTLINUX}" = "1" ]; then
                  mmd -i ${WORKDIR}/boot.img ::/extlinux
                  mcopy -i ${WORKDIR}/boot.img -s ${DEPLOY_DIR_IMAGE}/extlinux.conf ::/extlinux/extlinux.conf
          fi
  
          # Copy additional files to boot partition: such as m4 images and firmwares
          if [ -n "${IMAGE_BOOTFILES}" ]; then
              for IMAGE_FILE in ${IMAGE_BOOTFILES}; do
                  if [ -e "${DEPLOY_DIR_IMAGE}/${IMAGE_FILE}" ]; then
                      mcopy -i ${WORKDIR}/boot.img -s ${DEPLOY_DIR_IMAGE}/${IMAGE_FILE} ::/${IMAGE_FILE}
                  else
                      bbfatal "${IMAGE_FILE} does not exist."
                  fi
              done
          fi
  
      # add tee to boot image
      if ${@bb.utils.contains('COMBINED_FEATURES', 'optee', 'true', 'false', d)}; then
          for UTEE_FILE_PATH in `find ${DEPLOY_DIR_IMAGE} -maxdepth 1 -type f -name 'uTee-*' -print -quit`; do
              UTEE_FILE=`basename ${UTEE_FILE_PATH}`
              mcopy -i ${WORKDIR}/boot.img -s ${DEPLOY_DIR_IMAGE}/${UTEE_FILE} ::/${UTEE_FILE}
          done
      fi
  
  }
  
  #
  # Create an image that can by written onto a SD card using dd for use
  # with i.MX SoC family
  #
  # External variables needed:
  #   ${SDCARD_ROOTFS}    - the rootfs image to incorporate
  #   ${IMAGE_BOOTLOADER} - bootloader to use {u-boot, barebox}
  #
  # The disk layout used is:
  #
  #    0                      -> IMAGE_ROOTFS_ALIGNMENT         - reserved to bootloader (not partitioned)
  #    IMAGE_ROOTFS_ALIGNMENT -> BOOT_SPACE                     - kernel and other data
  #    BOOT_SPACE             -> SDIMG_SIZE                     - rootfs
  #
  #                                                     Default Free space = 1.3x
  #                                                     Use IMAGE_OVERHEAD_FACTOR to add more space
  #                                                     <--------->
  #            4MiB               8MiB           SDIMG_ROOTFS                    4MiB
  # <-----------------------> <----------> <----------------------> <------------------------------>
  #  ------------------------ ------------ ------------------------ -------------------------------
  # | IMAGE_ROOTFS_ALIGNMENT | BOOT_SPACE | ROOTFS_SIZE            |     IMAGE_ROOTFS_ALIGNMENT    |
  #  ------------------------ ------------ ------------------------ -------------------------------
  # ^                        ^            ^                        ^                               ^
  # |                        |            |                        |                               |
  # 0                      4096     4MiB +  8MiB       4MiB +  8Mib + SDIMG_ROOTFS   4MiB +  8MiB + SDIMG_ROOTFS + 4MiB
  generate_imx_sdcard () {
          # Create partition table
          parted -s ${SDCARD} mklabel msdos
          parted -s ${SDCARD} unit KiB mkpart primary fat32 ${IMAGE_ROOTFS_ALIGNMENT} $(expr ${IMAGE_ROOTFS_ALIGNMENT} \+ ${BOOT_SPACE_ALIGNED})
          parted -s ${SDCARD} unit KiB mkpart primary $(expr  ${IMAGE_ROOTFS_ALIGNMENT} \+ ${BOOT_SPACE_ALIGNED}) $(expr ${IMAGE_ROOTFS_ALIGNMENT} \+ ${BOOT_SPACE_ALIGNED} \+ $ROOTFS_SIZE)
          parted ${SDCARD} print
  
          # Burn bootloader
          case "${IMAGE_BOOTLOADER}" in
                  imx-bootlets)
                  bberror "The imx-bootlets is not supported for i.MX based machines"
                  exit 1
                  ;;
                  imx-boot)
                  dd if=${DEPLOY_DIR_IMAGE}/imx-boot-${MACHINE}-${UBOOT_CONFIG}.bin of=${SDCARD} conv=notrunc seek=${IMX_BOOT_SEEK} bs=1K
                  ;;
                  u-boot)
                  if [ -n "${SPL_BINARY}" ]; then
                      if [ -n "${SPL_SEEK}" ]; then
                          dd if=${DEPLOY_DIR_IMAGE}/${SPL_BINARY} of=${SDCARD} conv=notrunc seek=${SPL_SEEK} bs=1K
                          dd if=${DEPLOY_DIR_IMAGE}/u-boot-${MACHINE}.${UBOOT_SUFFIX_SDCARD} of=${SDCARD} conv=notrunc seek=${UBOOT_SEEK} bs=1K
                      else
                          dd if=${DEPLOY_DIR_IMAGE}/${SPL_BINARY} of=${SDCARD} conv=notrunc seek=2 bs=512
                          dd if=${DEPLOY_DIR_IMAGE}/u-boot-${MACHINE}.${UBOOT_SUFFIX_SDCARD} of=${SDCARD} conv=notrunc seek=69 bs=1K
                      fi
                  else
                      dd if=${DEPLOY_DIR_IMAGE}/u-boot-${MACHINE}.${UBOOT_SUFFIX_SDCARD} of=${SDCARD} conv=notrunc seek=2 bs=512
                  fi
                  ;;
                  barebox)
                  dd if=${DEPLOY_DIR_IMAGE}/barebox-${MACHINE}.bin of=${SDCARD} conv=notrunc seek=1 skip=1 bs=512
                  dd if=${DEPLOY_DIR_IMAGE}/bareboxenv-${MACHINE}.bin of=${SDCARD} conv=notrunc seek=1 bs=512k
                  ;;
                  "")
                  ;;
                  *)
                  bberror "Unknown IMAGE_BOOTLOADER value"
                  exit 1
                  ;;
          esac
  
          _generate_boot_image 1
  
          # Burn Partition
          dd if=${WORKDIR}/boot.img of=${SDCARD} conv=notrunc,fsync seek=1 bs=$(expr ${IMAGE_ROOTFS_ALIGNMENT} \* 1024)
          dd if=${SDCARD_ROOTFS} of=${SDCARD} conv=notrunc,fsync seek=1 bs=$(expr ${BOOT_SPACE_ALIGNED} \* 1024 + ${IMAGE_ROOTFS_ALIGNMENT} \* 1024)
  }
  
  #
  # Create an image that can by written onto a SD card using dd for use
  # with i.MXS SoC family
  #
  # External variables needed:
  #   ${SDCARD_ROOTFS}    - the rootfs image to incorporate
  #   ${IMAGE_BOOTLOADER} - bootloader to use {imx-bootlets, u-boot}
  #
  generate_mxs_sdcard () {
          # Create partition table
          parted -s ${SDCARD} mklabel msdos
  
          case "${IMAGE_BOOTLOADER}" in
                  imx-bootlets)
                  # The disk layout used is:
                  #
                  #    0                      -> 1024                           - Unused (not partitioned)
                  #    1024                   -> BOOT_SPACE                     - kernel and other data (bootstream)
                  #    BOOT_SPACE             -> SDIMG_SIZE                     - rootfs
                  #
                  #                                     Default Free space = 1.3x
                  #                                     Use IMAGE_OVERHEAD_FACTOR to add more space
                  #                                     <--------->
                  #    1024        8MiB          SDIMG_ROOTFS                    4MiB
                  # <-------> <----------> <----------------------> <------------------------------>
                  #  --------------------- ------------------------ -------------------------------
                  # | Unused | BOOT_SPACE | ROOTFS_SIZE            |     IMAGE_ROOTFS_ALIGNMENT    |
                  #  --------------------- ------------------------ -------------------------------
                  # ^        ^            ^                        ^                               ^
                  # |        |            |                        |                               |
                  # 0      1024      1024 + 8MiB       1024 + 8Mib + SDIMG_ROOTFS      1024 + 8MiB + SDIMG_ROOTFS + 4MiB
                  parted -s ${SDCARD} unit KiB mkpart primary 1024 $(expr ${IMAGE_ROOTFS_ALIGNMENT} \+ ${BOOT_SPACE_ALIGNED})
                  parted -s ${SDCARD} unit KiB mkpart primary $(expr ${IMAGE_ROOTFS_ALIGNMENT} \+ ${BOOT_SPACE_ALIGNED}) $(expr ${IMAGE_ROOTFS_ALIGNMENT} \+ ${BOOT_SPACE_ALIGNED} \+ $ROOTFS_SIZE)
  
                  # Empty 4 blocks from boot partition
                  dd if=/dev/zero of=${SDCARD} conv=notrunc seek=2048 count=4
  
                  # Write the bootstream in (2048 + 4) blocks
                  dd if=${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.linux.sb of=${SDCARD} conv=notrunc seek=2052
                  ;;
                  u-boot)
                  # The disk layout used is:
                  #
                  #    1M - 2M                  - reserved to bootloader and other data
                  #    2M - BOOT_SPACE          - kernel
                  #    BOOT_SPACE - SDCARD_SIZE - rootfs
                  #
                  # The disk layout used is:
                  #
                  #    1M                     -> 2M                             - reserved to bootloader and other data
                  #    2M                     -> BOOT_SPACE                     - kernel and other data
                  #    BOOT_SPACE             -> SDIMG_SIZE                     - rootfs
                  #
                  #                                                        Default Free space = 1.3x
                  #                                                        Use IMAGE_OVERHEAD_FACTOR to add more space
                  #                                                        <--------->
                  #            4MiB                8MiB             SDIMG_ROOTFS                    4MiB
                  # <-----------------------> <-------------> <----------------------> <------------------------------>
                  #  ---------------------------------------- ------------------------ -------------------------------
                  # |      |      |                          |ROOTFS_SIZE             |     IMAGE_ROOTFS_ALIGNMENT    |
                  #  ---------------------------------------- ------------------------ -------------------------------
                  # ^      ^      ^          ^               ^                        ^                               ^
                  # |      |      |          |               |                        |                               |
                  # 0     1M     2M         4M        4MiB + BOOTSPACE   4MiB + BOOTSPACE + SDIMG_ROOTFS   4MiB + BOOTSPACE + SDIMG_ROOTFS + 4MiB
                  #
                  parted -s ${SDCARD} unit KiB mkpart primary 1024 2048
                  parted -s ${SDCARD} unit KiB mkpart primary 2048 $(expr ${IMAGE_ROOTFS_ALIGNMENT} \+ ${BOOT_SPACE_ALIGNED})
                  parted -s ${SDCARD} unit KiB mkpart primary $(expr  ${IMAGE_ROOTFS_ALIGNMENT} \+ ${BOOT_SPACE_ALIGNED}) $(expr ${IMAGE_ROOTFS_ALIGNMENT} \+ ${BOOT_SPACE_ALIGNED} \+ $ROOTFS_SIZE)
  
                  dd if=${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.rootfs.uboot.mxsboot-sdcard of=${SDCARD} conv=notrunc seek=1 bs=$(expr 1024 \* 1024)
  
                  _generate_boot_image 2
  
                  dd if=${WORKDIR}/boot.img of=${SDCARD} conv=notrunc seek=2 bs=$(expr 1024 \* 1024)
                  ;;
                  barebox)
                  # BAREBOX_ENV_SPACE is taken on BOOT_SPACE_ALIGNED but it doesn't really matter as long as the rootfs is aligned
                  parted -s ${SDCARD} unit KiB mkpart primary 1024 $(expr ${IMAGE_ROOTFS_ALIGNMENT} \+ ${BOOT_SPACE_ALIGNED} - ${BAREBOX_ENV_SPACE})
                  parted -s ${SDCARD} unit KiB mkpart primary $(expr ${IMAGE_ROOTFS_ALIGNMENT} \+ ${BOOT_SPACE_ALIGNED} - ${BAREBOX_ENV_SPACE}) $(expr ${IMAGE_ROOTFS_ALIGNMENT} \+ ${BOOT_SPACE_ALIGNED})
                  parted -s ${SDCARD} unit KiB mkpart primary $(expr ${IMAGE_ROOTFS_ALIGNMENT} \+ ${BOOT_SPACE_ALIGNED}) $(expr ${IMAGE_ROOTFS_ALIGNMENT} \+ ${BOOT_SPACE_ALIGNED} \+ $ROOTFS_SIZE)
  
                  dd if=${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.barebox.mxsboot-sdcard of=${SDCARD} conv=notrunc seek=1 bs=$(expr 1024 \* 1024)
                  dd if=${DEPLOY_DIR_IMAGE}/bareboxenv-${MACHINE}.bin of=${SDCARD} conv=notrunc seek=$(expr ${IMAGE_ROOTFS_ALIGNMENT} \+ ${BOOT_SPACE_ALIGNED} - ${BAREBOX_ENV_SPACE}) bs=1024
                  ;;
                  *)
                  bberror "Unknown IMAGE_BOOTLOADER value"
                  exit 1
                  ;;
          esac
  
          # Change partition type for mxs processor family
          bbnote "Setting partition type to 0x53 as required for mxs' SoC family."
          echo -n S | dd of=${SDCARD} bs=1 count=1 seek=450 conv=notrunc
  
          parted ${SDCARD} print
  
          dd if=${SDCARD_ROOTFS} of=${SDCARD} conv=notrunc,fsync seek=1 bs=$(expr ${BOOT_SPACE_ALIGNED} \* 1024 + ${IMAGE_ROOTFS_ALIGNMENT} \* 1024)
  }
  
  IMAGE_CMD_sdcard () {
          if [ -z "${SDCARD_ROOTFS}" ]; then
                  bberror "SDCARD_ROOTFS is undefined. To use sdcard image from Freescale's BSP it needs to be defined."
                  exit 1
          fi
  
          # Align boot partition and calculate total SD card image size
          BOOT_SPACE_ALIGNED=$(expr ${BOOT_SPACE} + ${IMAGE_ROOTFS_ALIGNMENT} - 1)
          BOOT_SPACE_ALIGNED=$(expr ${BOOT_SPACE_ALIGNED} - ${BOOT_SPACE_ALIGNED} % ${IMAGE_ROOTFS_ALIGNMENT})
          SDCARD_SIZE=$(expr ${IMAGE_ROOTFS_ALIGNMENT} + ${BOOT_SPACE_ALIGNED} + $ROOTFS_SIZE + ${IMAGE_ROOTFS_ALIGNMENT})
  
          # Initialize a sparse file
          dd if=/dev/zero of=${SDCARD} bs=1 count=0 seek=$(expr 1024 \* ${SDCARD_SIZE})
  
          # SDCARD_GENERATION_COMMAND_mx8 = "generate_imx_sdcard"
          ${SDCARD_GENERATION_COMMAND}
  }
  
  # The sdcard requires the rootfs filesystem to be built before using
  # it so we must make this dependency explicit.
  IMAGE_TYPEDEP_sdcard += "${@d.getVar('SDCARD_ROOTFS', 1).split('.')[-1]}"
  
  # In case we are building for i.MX23 or i.MX28 we need to have the
  # image stream built before the sdcard generation
  IMAGE_TYPEDEP_sdcard += " \
      ${@bb.utils.contains('IMAGE_FSTYPES', 'uboot.mxsboot-sdcard', 'uboot.mxsboot-sdcard', '', d)} \
      ${@bb.utils.contains('IMAGE_FSTYPES', 'barebox.mxsboot-sdcard', 'barebox.mxsboot-sdcard', '', d)} \
  "
  ```
* 如果需要修改FAT分区，请修改`BOOT_SPACE`值: `meta-fsl-bsp-release/imx/meta-bsp/conf/machine/imx8qmmek.conf`
  ```
  [...省略]
  BOOT_SPACE = "65536"                          # 修改为131070
  IMAGE_BOOTLOADER = "imx-boot"
  IMX_BOOT_SEEK = "32"
  [...省略]
  ```

## fsl-image-qt5-validation-imx编译目录查看

* check dir
  ```
  zengjf@UbuntuServer:work$ find * -iname fsl-image-qt5-validation-imx
  imx8qmmek-poky-linux/fsl-image-qt5-validation-imx
  imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/license-destdir/fsl-image-qt5-validation-imx
  zengjf@UbuntuServer:work$ pwd
  /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work
  zengjf@UbuntuServer:work$ cd imx8qmmek-poky-linux/fsl-image-qt5-validation-imx
  zengjf@UbuntuServer:fsl-image-qt5-validation-imx$ ls
  1.0-r0
  zengjf@UbuntuServer:fsl-image-qt5-validation-imx$ cd 1.0-r0/
  zengjf@UbuntuServer:1.0-r0$ ls
  boot.img                                            image_initial_manifest                                                              oe-rootfs-repo  recipe-sysroot-native
  deploy-fsl-image-qt5-validation-imx-image-complete  intercept_scripts-4e483ccbb8eebedac79a97921e7e51359effab8948df422687b89ca0ae30316d  pseudo          rootfs
  fsl-image-qt5-validation-imx-1.0                    license-destdir                                                                     recipe-sysroot  temp
  zengjf@UbuntuServer:1.0-r0$ cd temp/
  zengjf@UbuntuServer:temp$ ls
  dnf.librepo.log                      log.do_rootfs.30183          run.do_populate_lic.22137            run.prelink_setup.16730                  run.sstate_hardcode_path_unpack.28039
  dnf.log                              log.task_order               run.do_prepare_recipe_sysroot        run.prelink_setup.28044                  run.sstate_hardcode_path_unpack.28317
  dnf.rpm.log                          run.create_symlinks.11724    run.do_prepare_recipe_sysroot.18020  run.reproducible_final_image_task.11705  run.sstate_sign_package.22137
  hawkey.log                           run.create_symlinks.11725    run.do_rootfs                        run.reproducible_final_image_task.16730  run.sstate_task_postfunc.11700
  log.do_image                         run.create_symlinks.11771    run.do_rootfs.10177                  run.reproducible_final_image_task.28044  run.sstate_task_postfunc.12386
  log.do_image.11705                   run.create_symlinks.16749    run.do_rootfs.25152                  run.rootfs_reproducible.10177            run.sstate_task_postfunc.16725
  log.do_image.16730                   run.create_symlinks.16752    run.do_rootfs.30129                  run.rootfs_reproducible.25152            run.sstate_task_postfunc.17003
  log.do_image.28044                   run.create_symlinks.16794    run.do_rootfs.30183                  run.rootfs_reproducible.30183            run.sstate_task_postfunc.22137
  log.do_image_complete                run.create_symlinks.28063    run.empty_var_volatile.10177         run.rootfs_update_timestamp.10177        run.sstate_task_postfunc.28039
  log.do_image_complete.12386          run.create_symlinks.28064    run.empty_var_volatile.25152         run.rootfs_update_timestamp.25152        run.sstate_task_postfunc.28317
  log.do_image_complete.17003          run.create_symlinks.28107    run.empty_var_volatile.30183         run.rootfs_update_timestamp.30183        run.sstate_task_prefunc.11700
  log.do_image_complete.28317          run.do_image                 run.extend_recipe_sysroot.10177      run.set_image_size.11724                 run.sstate_task_prefunc.12386
  log.do_image_ext4                    run.do_image.11705           run.extend_recipe_sysroot.11724      run.set_image_size.11725                 run.sstate_task_prefunc.16725
  log.do_image_ext4.11724              run.do_image.16730           run.extend_recipe_sysroot.11771      run.set_image_size.11771                 run.sstate_task_prefunc.17003
  log.do_image_ext4.16749              run.do_image.28044           run.extend_recipe_sysroot.16749      run.set_image_size.16749                 run.sstate_task_prefunc.22137
  log.do_image_ext4.28063              run.do_image_complete        run.extend_recipe_sysroot.16794      run.set_image_size.16752                 run.sstate_task_prefunc.28039
  log.do_image_qa                      run.do_image_complete.12386  run.extend_recipe_sysroot.18020      run.set_image_size.16794                 run.sstate_task_prefunc.28317
  log.do_image_qa.11700                run.do_image_complete.17003  run.extend_recipe_sysroot.25152      run.set_image_size.28063                 run.systemd_create_users.10177
  log.do_image_qa.16725                run.do_image_complete.28317  run.extend_recipe_sysroot.28063      run.set_image_size.28064                 run.systemd_create_users.25152
  log.do_image_qa.28039                run.do_image_ext4            run.extend_recipe_sysroot.28107      run.set_image_size.28107                 run.systemd_create_users.30183
  log.do_image_sdcard                  run.do_image_ext4.11724      run.extend_recipe_sysroot.30129      run.set_systemd_default_target.10177     run.write_deploy_manifest.12386
  log.do_image_sdcard.11771            run.do_image_ext4.16749      run.extend_recipe_sysroot.30183      run.set_systemd_default_target.25152     run.write_deploy_manifest.17003
  log.do_image_sdcard.16794            run.do_image_ext4.28063      run.license_create_manifest.10177    run.set_systemd_default_target.30183     run.write_deploy_manifest.28317
  log.do_image_sdcard.28107            run.do_image_qa              run.license_create_manifest.25152    run.sort_passwd.10177                    run.write_image_manifest.10177
  log.do_image_tar                     run.do_image_qa.11700        run.license_create_manifest.30183    run.sort_passwd.25152                    run.write_image_manifest.25152
  log.do_image_tar.11725               run.do_image_qa.16725        run.mklibs_optimize_image.11705      run.sort_passwd.30183                    run.write_image_manifest.30183
  log.do_image_tar.16752               run.do_image_qa.28039        run.mklibs_optimize_image.16730      run.ssh_allow_empty_password.10177       run.write_image_test_data.10177
  log.do_image_tar.28064               run.do_image_sdcard          run.mklibs_optimize_image.28044      run.ssh_allow_empty_password.25152       run.write_image_test_data.25152
  log.do_populate_lic                  run.do_image_sdcard.11771    run.populate_lic_qa_checksum.22137   run.ssh_allow_empty_password.30183       run.write_image_test_data.30183
  log.do_populate_lic.22137            run.do_image_sdcard.16794    run.postinst_enable_logging.10177    run.sstate_create_package.22137          run.write_package_manifest.10177
  log.do_prepare_recipe_sysroot        run.do_image_sdcard.28107    run.postinst_enable_logging.25152    run.sstate_hardcode_path.22137           run.write_package_manifest.25152
  log.do_prepare_recipe_sysroot.18020  run.do_image_tar             run.postinst_enable_logging.30183    run.sstate_hardcode_path_unpack.11700    run.write_package_manifest.30183
  log.do_rootfs                        run.do_image_tar.11725       run.prelink_image.11705              run.sstate_hardcode_path_unpack.12386    saved
  log.do_rootfs.10177                  run.do_image_tar.16752       run.prelink_image.16730              run.sstate_hardcode_path_unpack.16725    saved_packaging_data
  log.do_rootfs.25152                  run.do_image_tar.28064       run.prelink_image.28044              run.sstate_hardcode_path_unpack.17003
  log.do_rootfs.30129                  run.do_populate_lic          run.prelink_setup.11705              run.sstate_hardcode_path_unpack.22137
  zengjf@UbuntuServer:temp$ pwd
  /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/temp
  ```
* run.do_xxx
  ```
  zengjf@UbuntuServer:temp$ find ! -name . -prune -type l | grep run
  ./run.do_image_tar
  ./run.do_rootfs
  ./run.do_image
  ./run.do_image_complete
  ./run.do_image_ext4
  ./run.do_image_sdcard
  ./run.do_populate_lic
  ./run.do_prepare_recipe_sysroot
  ./run.do_image_qa
  ```
* log.do_image_sdcard
  ```
  zengjf@UbuntuServer:temp$ cat log.do_image_sdcard
  DEBUG: Executing python function set_image_size
  DEBUG: 1069952.000000 = 823040 * 1.300000
  DEBUG: 1069952.000000 = max(1069952.000000, 65536)[1069952.000000] + 1
  DEBUG: 1069952.000000 = int(1069952.000000)
  DEBUG: 1073152 = aligned(1069952)
  DEBUG: returning 1073152
  DEBUG: Python function set_image_size finished
  DEBUG: Executing python function extend_recipe_sysroot
  [...省略]
  DEBUG: Python function extend_recipe_sysroot finished
  DEBUG: Executing shell function do_image_sdcard
  0+0 records in
  0+0 records out
  0 bytes copied, 0.000165533 s, 0.0 kB/s
  Model:  (file)
  Disk /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/deploy-fsl-image-qt5-validation-imx-image-complete/fsl-image-qt5-validation-imx-imx8qmmek-20190724080153.rootfs.sdcard: 1183MB
  Sector size (logical/physical): 512B/512B
  Partition Table: msdos
  Disk Flags:
  
  Number  Start   End     Size    Type     File system  Flags
   1      8389kB  75.5MB  67.1MB  primary               lba
   2      75.5MB  1174MB  1099MB  primary
  
  950+0 records in
  950+0 records out
  972800 bytes (973 kB, 950 KiB) copied, 0.00281714 s, 345 MB/s
  mkfs.fat: warning - lowercase labels might not work properly with DOS or Windows
  mkfs.fat 4.1 (2017-01-24)
  8+0 records in
  8+0 records out
  67108864 bytes (67 MB, 64 MiB) copied, 0.109199 s, 615 MB/s
  14+1 records in
  14+1 records out
  1098907648 bytes (1.1 GB, 1.0 GiB) copied, 1.91728 s, 573 MB/s
  DEBUG: Shell function do_image_sdcard finished
  DEBUG: Executing python function create_symlinks
  NOTE: Creating symlink: /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/deploy-fsl-image-qt5-validation-imx-image-complete/fsl-image-qt5-validation-imx-imx8qmmek.sdcard.bz2 -> fsl-image-qt5-validation-imx-imx8qmmek-20190724080153.rootfs.sdcard.bz2
  DEBUG: Python function create_symlinks finished
  ```
* meta-fsl-bsp-release/imx/meta-bsp/conf/machine/imx8qmmek.conf
  ```
  [...省略]
  BOOT_SPACE = "65536"                          # 修改为131070
  IMAGE_BOOTLOADER = "imx-boot"
  IMX_BOOT_SEEK = "32"
  [...省略]
  ```
  * log.do_image_sdcard
    ```
    DEBUG: Executing python function set_image_size
    DEBUG: 1069962.400000 = 823048 * 1.300000
    DEBUG: 1069962.400000 = max(1069962.400000, 65536)[1069962.400000] + 1
    DEBUG: 1069963.000000 = int(1069962.400000)
    DEBUG: 1073152 = aligned(1069963)
    DEBUG: returning 1073152
    DEBUG: Python function set_image_size finished
    DEBUG: Executing python function extend_recipe_sysroot
    [...省略]
    DEBUG: Python function extend_recipe_sysroot finished
    DEBUG: Executing shell function do_image_sdcard
    0+0 records in
    0+0 records out
    0 bytes copied, 0.000253039 s, 0.0 kB/s
    Model:  (file)
    Disk /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/deploy-fsl-image-qt5-validation-imx-image-complete/fsl-image-qt5-validation-imx-imx8qmmek-20190724093849.rootfs.sdcard: 1250MB
    Sector size (logical/physical): 512B/512B
    Partition Table: msdos
    Disk Flags:
  
    Number  Start   End     Size    Type     File system  Flags
     1      8389kB  143MB   134MB   primary               lba
     2      143MB   1242MB  1099MB  primary
  
    950+0 records in
    950+0 records out
    972800 bytes (973 kB, 950 KiB) copied, 0.0218725 s, 44.5 MB/s
    mkfs.fat: warning - lowercase labels might not work properly with DOS or Windows
    mkfs.fat 4.1 (2017-01-24)
    16+0 records in
    16+0 records out
    134217728 bytes (134 MB, 128 MiB) copied, 0.215037 s, 624 MB/s
    7+1 records in
    7+1 records out
    1098907648 bytes (1.1 GB, 1.0 GiB) copied, 1.82605 s, 602 MB/s
    DEBUG: Shell function do_image_sdcard finished
    DEBUG: Executing python function create_symlinks
    NOTE: Creating symlink: /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/deploy-fsl-image-qt5-validation-imx-image-complete/fsl-image-qt5-validation-imx-imx8qmmek.sdcard.bz2 -> fsl-image-qt5-validation-imx-imx8qmmek-20190724093849.rootfs.sdcard.bz2
    DEBUG: Python function create_symlinks finished
    ```
* rootfs: `tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/rootfs`

## bitbake with -DD for debug

```
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-support/libcheck/libcheck_0.12.0.bb:do_build
DEBUG: Stamp current task virtual:native:/home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-support/libffi/libffi_3.2.1.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-bsp/recipes-multimedia/alsa/imx-alsa-plugins_1.0.26.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/meta-qt5/recipes-qt/examples/quitindicators_1.0.1.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/meta-openembedded/meta-oe/recipes-multimedia/v4l2apps/v4l-utils_1.12.3.bb:do_build
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_populate_lic: Executing python function sstate_hardcode_path
DEBUG: Stamp current task virtual:native:/home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-devtools/libdnf/libdnf_0.11.1.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-support/libevent/libevent_2.1.8.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-support/libcap/libcap_2.25.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-support/libexif/libexif_0.6.21.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/meta-openembedded/meta-oe/recipes-devtools/protobuf/protobuf_3.5.1.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-extended/tcp-wrappers/tcp-wrappers_7.6.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/meta-qt5/recipes-qt/demo-extrafiles/qt5-demo-extrafiles.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-devtools/python/python3_3.5.5.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-devtools/quilt/quilt-native_0.65.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-core/netbase/netbase_5.4.bb:do_build
DEBUG: Stamp current task virtual:native:/home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-graphics/xorg-lib/libxau_1.0.8.bb:do_build
DEBUG: Stamp current task virtual:native:/home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-kernel/dtc/dtc_1.4.5.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-extended/libidn/libidn_1.33.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-core/systemd/systemd-compat-units.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-bsp/recipes-multimedia/imx-parser/imx-parser_4.4.4.bb:do_build
DEBUG: Stamp current task virtual:native:/home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-core/dbus/dbus_1.12.2.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/recipes-fsl/m4-demos/imx-m4-demos_2.5.1.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-support/popt/popt_1.16.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-devtools/perl/perl_5.24.1.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-core/packagegroups/packagegroup-base.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-multimedia/libtheora/libtheora_1.1.1.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-extended/man-db/man-db_2.8.1.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-support/libevdev/libevdev_1.5.8.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-extended/logrotate/logrotate_3.13.0.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-support/attr/acl_2.2.52.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-support/liburcu/liburcu_0.10.1.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-multimedia/alsa/alsa-plugins_1.1.5.bb:do_build
DEBUG: Stamp current task virtual:native:/home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-graphics/xorg-proto/kbproto_1.0.7.bb:do_build
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_populate_lic: Python function sstate_hardcode_path finished
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-support/gnome-desktop-testing/gnome-desktop-testing_2014.1.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-extended/bzip2/bzip2_1.0.6.bb:do_build
DEBUG: Stamp current task virtual:native:/home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-support/libcheck/libcheck_0.12.0.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-gnome/gdk-pixbuf/gdk-pixbuf_2.36.11.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-core/base-files/base-files_3.0.14.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-extended/xz/xz_5.2.3.bb:do_build
DEBUG: Stamp current task virtual:native:/home/zengjf/imx8-yocto-ga/sources/meta-qt5/recipes-qt/qt5/qtxmlpatterns_git.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-bsp/recipes-multimedia/gstreamer/gstreamer1.0-rtsp-server_1.14.0.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-multimedia/libvorbis/libvorbis_1.3.5.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-devtools/libtool/libtool_2.4.6.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-support/gnutls/gnutls_3.6.1.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-extended/at/at_3.1.20.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-support/iso-codes/iso-codes_3.77.bb:do_build
DEBUG: Stamp current task virtual:native:/home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-devtools/rpm/rpm_4.14.1.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-devtools/strace/strace_4.20.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-devtools/perl/perl-native_5.24.1.bb:do_build
DEBUG: Stamp current task virtual:native:/home/zengjf/imx8-yocto-ga/sources/meta-qt5/recipes-qt/qt5/qt3d_git.bb:do_build
DEBUG: Stamp current task virtual:native:/home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-core/update-rc.d/update-rc.d_0.7.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-devtools/opkg-utils/opkg-utils_0.3.6.bb:do_build
DEBUG: Stamp current task virtual:native:/home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-devtools/prelink/prelink_git.bb:do_build
DEBUG: Stamp current task virtual:native:/home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-devtools/createrepo-c/createrepo-c_git.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-kernel/kmod/kmod-native_git.bb:do_build
DEBUG: Stamp current task virtual:native:/home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-extended/groff/groff_1.22.3.bb:do_build
DEBUG: Stamp current task virtual:native:/home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-support/nspr/nspr_4.19.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-devtools/m4/m4-native_1.4.18.bb:do_build
DEBUG: Stamp current task virtual:native:/home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-core/libxml/libxml2_2.9.7.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-graphics/vulkan/vulkan_1.0.65.2.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-kernel/kmod/depmodwrapper-cross_1.0.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-gnome/gnome/gconf_3.2.6.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-kernel/lttng/lttng-modules_2.10.6.bb:do_build
DEBUG: Stamp current task virtual:native:/home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-extended/acpica/acpica_20170303.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-multimedia/speex/speexdsp_1.2rc3.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-bsp/recipes-multimedia/gstreamer/gstreamer1.0-libav_1.14.0.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-graphics/xorg-lib/libxkbcommon_0.8.0.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/meta-qt5/recipes-qt/examples/qt5everywheredemo_1.0.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-multimedia/mpg123/mpg123_1.25.10.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-core/packagegroups/packagegroup-core-boot.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-core/packagegroups/packagegroup-core-tools-debug.bb:do_build
DEBUG: Stamp current task virtual:native:/home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-support/mpfr/mpfr_3.1.5.bb:do_build
DEBUG: Stamp current task virtual:native:/home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-support/gdbm/gdbm_1.14.1.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-bsp/gnu-efi/gnu-efi_3.0.6.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-devtools/gcc/gcc-source_7.3.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-extended/iptables/iptables_1.6.2.bb:do_build
DEBUG: Stamp current task virtual:native:/home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-devtools/python/python3-iniparse_0.4.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-extended/rpcbind/rpcbind_0.2.4.bb:do_build
DEBUG: Stamp current task virtual:native:/home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-graphics/xorg-util/util-macros_1.19.1.bb:do_build
DEBUG: Stamp current task virtual:native:/home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-devtools/unifdef/unifdef_2.11.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-extended/wget/wget_1.19.5.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-bsp/recipes-connectivity/bluez5/bluez5_5.49.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-extended/texinfo-dummy-native/texinfo-dummy-native.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-devtools/file/file_5.32.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-connectivity/wpa-supplicant/wpa-supplicant_2.6.bb:do_build
DEBUG: Stamp current task virtual:native:/home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-devtools/pkgconfig/pkgconfig_git.bb:do_build
DEBUG: Stamp current task virtual:native:/home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-support/libpcre/libpcre_8.41.bb:do_build
DEBUG: Stamp current task virtual:native:/home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-core/readline/readline_7.0.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-core/dbus/dbus_1.12.2.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-multimedia/webp/libwebp_0.6.1.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-kernel/kern-tools/kern-tools-native_git.bb:do_build
DEBUG: Stamp current task virtual:native:/home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-devtools/python/python3-setuptools_39.0.0.bb:do_build
DEBUG: Stamp current task virtual:native:/home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-devtools/xmlto/xmlto_0.0.28.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-graphics/pango/pango_1.40.14.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-support/libffi/libffi_3.2.1.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-devtools/cmake/cmake-native_3.10.3.bb:do_build
DEBUG: Stamp current task virtual:native:/home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-devtools/pseudo/pseudo_git.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-support/curl/curl_7.61.0.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-core/libcgroup/libcgroup_0.41.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-bsp/recipes-graphics/imx-gpu-viv/imx-gpu-viv_6.2.4.p2.3-aarch64.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-bsp/recipes-multimedia/gstreamer/gstreamer1.0_1.14.imx.bb:do_build
DEBUG: Stamp current task virtual:native:/home/zengjf/imx8-yocto-ga/sources/meta-qt5/recipes-qt/qt5/qtdeclarative_git.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-devtools/gcc/gcc-runtime_7.3.bb:do_build
DEBUG: Stamp current task virtual:native:/home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-graphics/xorg-lib/libxext_1.3.3.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/meta-qt5/recipes-qt/qt5/qtquickcontrols2_git.bb:do_build
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_populate_lic: Executing shell function sstate_create_package
DEBUG: Stamp current task virtual:native:/home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-support/gmp/gmp_6.1.2.bb:do_build
DEBUG: Stamp current task virtual:native:/home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-core/dbus/dbus-glib_0.108.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-devtools/valgrind/valgrind_3.13.0.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-devtools/python/python3-pygobject_3.28.1.bb:do_build
DEBUG: Stamp current task virtual:native:/home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-extended/bc/bc_1.06.bb:do_build
DEBUG: Stamp current task /home/zengjf/imx8-yocto-ga/sources/poky/meta/recipes-support/boost/bjam-native_1.66.0.bb:do_build
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_prepare_recipe_sysroot: sed -e 's:^[^/]*/:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/recipe-sysroot/:g' /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/imx8qmmek/depmodwrapper-cross/fixmepath | xargs sed -i -e 's:FIXMESTAGINGDIRTARGET:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/recipe-sysroot:g; s:FIXMESTAGINGDIRHOST:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/recipe-sysroot-native:g' -e 's:FIXME_COMPONENTS_DIR:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components:g' -e 's:FIXME_HOSTTOOLS_DIR:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/hosttools:g' -e 's:FIXME_PKGDATA_DIR:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/pkgdata/imx8qmmek:g' -e 's:FIXME_PSEUDO_LOCALSTATEDIR:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/pseudo/:g' -e 's:FIXME_LOGFIFO:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/temp/fifo.8156:g'
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_populate_lic: Shell function sstate_create_package finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_populate_lic: Executing python function sstate_sign_package
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_populate_lic: Python function sstate_sign_package finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_prepare_recipe_sysroot: sed -e 's:^[^/]*/:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/recipe-sysroot-native/:g' /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/quilt-native/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/pkgconfig-native/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/libtool-native/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/automake-native/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/glib-2.0-native/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/libsdl-native/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/gnu-config-native/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/autoconf-native/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/python3-native/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/gtk-doc-native/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/libpcre-native/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/libpng-native/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/libx11-native/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/bison-native/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/openssl-native/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/ncurses-native/fixmepath | xargs sed -i -e 's:FIXMESTAGINGDIRTARGET:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/recipe-sysroot:g; s:FIXMESTAGINGDIRHOST:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/recipe-sysroot-native:g' -e 's:FIXME_COMPONENTS_DIR:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components:g' -e 's:FIXME_HOSTTOOLS_DIR:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/hosttools:g' -e 's:FIXME_PKGDATA_DIR:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/pkgdata/imx8qmmek:g' -e 's:FIXME_PSEUDO_LOCALSTATEDIR:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/pseudo/:g' -e 's:FIXME_LOGFIFO:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/temp/fifo.8156:g'
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_populate_lic: Executing python function sstate_hardcode_path_unpack
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_populate_lic: Python function sstate_hardcode_path_unpack finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_populate_lic: Staging files from /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/license-destdir to /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/deploy/licenses
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_populate_lic: Python function sstate_task_postfunc finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_prepare_recipe_sysroot: Python function extend_recipe_sysroot finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_prepare_recipe_sysroot: Python function do_prepare_recipe_sysroot finished
DEBUG: Marking task /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:do_configure as buildable
DEBUG: Setscene covered task /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:do_configure
DEBUG: Marking task /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:do_compile as buildable
DEBUG: Setscene covered task /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:do_compile
DEBUG: Marking task /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:do_install as buildable
DEBUG: Setscene covered task /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:do_install
DEBUG: Marking task /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:do_package as buildable
DEBUG: Setscene covered task /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:do_package
DEBUG: Marking task /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:do_packagedata as buildable
DEBUG: Setscene covered task /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:do_packagedata
DEBUG: Marking task /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:do_rootfs as buildable
DEBUG: Stampfile /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/stamps/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0.do_rootfs.46f64f18b01d9060e26bf91423696a10 not available
DEBUG: Starting bitbake-worker
DEBUG: Found bblayers.conf (/home/zengjf/imx8-yocto-ga/imx8-build-wayland/conf/bblayers.conf)
DEBUG: Adding layer /home/zengjf/imx8-yocto-ga/sources/poky/meta
DEBUG: Adding layer /home/zengjf/imx8-yocto-ga/sources/poky/meta-poky
DEBUG: Adding layer /home/zengjf/imx8-yocto-ga/sources/meta-openembedded/meta-oe
DEBUG: Adding layer /home/zengjf/imx8-yocto-ga/sources/meta-openembedded/meta-multimedia
DEBUG: Adding layer /home/zengjf/imx8-yocto-ga/sources/meta-freescale
DEBUG: Adding layer /home/zengjf/imx8-yocto-ga/sources/meta-freescale-3rdparty
DEBUG: Adding layer /home/zengjf/imx8-yocto-ga/sources/meta-freescale-distro
DEBUG: Adding layer /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-bsp
DEBUG: Adding layer /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk
DEBUG: Adding layer /home/zengjf/imx8-yocto-ga/sources/meta-browser
DEBUG: Adding layer /home/zengjf/imx8-yocto-ga/sources/meta-openembedded/meta-gnome
DEBUG: Adding layer /home/zengjf/imx8-yocto-ga/sources/meta-openembedded/meta-networking
DEBUG: Adding layer /home/zengjf/imx8-yocto-ga/sources/meta-openembedded/meta-python
DEBUG: Adding layer /home/zengjf/imx8-yocto-ga/sources/meta-openembedded/meta-filesystems
DEBUG: Adding layer /home/zengjf/imx8-yocto-ga/sources/meta-qt5
DEBUG: Adding layer /home/zengjf/imx8-yocto-ga/sources/meta--bsp-release
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/poky/meta/conf/bitbake.conf:746: including conf/abi_version.conf
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/poky/meta/conf/bitbake.conf:747: including conf/site.conf
DEBUG: CONF file 'conf/site.conf' not found
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/poky/meta/conf/bitbake.conf:748: including conf/auto.conf
DEBUG: CONF file 'conf/auto.conf' not found
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/poky/meta/conf/bitbake.conf:749: including conf/local.conf
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/poky/meta/conf/bitbake.conf:750: including conf/multiconfig/default.conf
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/poky/meta/conf/bitbake.conf:751: including conf/machine/imx8qmmek.conf
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-bsp/conf/machine/imx8qmmek.conf:8: including conf/machine/include/imx-base.inc
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-bsp/conf/machine/include/imx-base.inc:3: including conf/machine/include/fsl-default-settings.inc
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-bsp/conf/machine/include/imx-base.inc:4: including conf/machine/include/fsl-default-versions.inc
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-bsp/conf/machine/imx8qmmek.conf:9: including conf/machine/include/arm/arch-arm64.inc
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/poky/meta/conf/machine/include/arm/arch-arm64.inc:3: including conf/machine/include/arm/arch-armv7ve.inc
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/poky/meta/conf/machine/include/arm/arch-armv7ve.inc:8: including conf/machine/include/arm/arch-armv7a.inc
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/poky/meta/conf/machine/include/arm/arch-armv7a.inc:8: including conf/machine/include/arm/arch-armv6.inc
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/poky/meta/conf/machine/include/arm/arch-armv6.inc:8: including conf/machine/include/arm/arch-armv5-dsp.inc
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/poky/meta/conf/machine/include/arm/arch-armv5-dsp.inc:4: including conf/machine/include/arm/arch-armv5.inc
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/poky/meta/conf/machine/include/arm/arch-armv5.inc:8: including conf/machine/include/arm/arch-armv4.inc
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/poky/meta/conf/machine/include/arm/arch-armv4.inc:15: including conf/machine/include/arm/arch-arm.inc
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/poky/meta/conf/machine/include/arm/arch-armv4.inc:16: including conf/machine/include/arm/feature-arm-thumb.inc
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/poky/meta/conf/machine/include/arm/arch-armv5.inc:9: including conf/machine/include/arm/feature-arm-vfp.inc
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/poky/meta/conf/machine/include/arm/arch-armv7a.inc:9: including conf/machine/include/arm/feature-arm-neon.inc
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/poky/meta/conf/bitbake.conf:752: including conf/machine-sdk/x86_64.conf
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/poky/meta/conf/bitbake.conf:753: including conf/distro/fsl-imx-wayland.conf
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/conf/distro/fsl-imx-wayland.conf:3: including conf/distro/include/fsl-imx-base.inc
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/conf/distro/fsl-imx-wayland.conf:4: including conf/distro/include/fsl-imx-preferred-env.inc
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/poky/meta/conf/bitbake.conf:754: including conf/distro/defaultsetup.conf
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/poky/meta/conf/distro/defaultsetup.conf:1: including conf/distro/include/default-providers.inc
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/poky/meta/conf/distro/defaultsetup.conf:2: including conf/distro/include/default-versions.inc
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/poky/meta/conf/distro/defaultsetup.conf:3: including conf/distro/include/default-distrovars.inc
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/poky/meta/conf/distro/defaultsetup.conf:4: including conf/distro/include/world-broken.inc
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/poky/meta/conf/distro/defaultsetup.conf:7: including conf/distro/include/tcmode-default.inc
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/poky/meta/conf/distro/include/tcmode-default.inc:73: including conf/distro/include/as-needed.inc
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/poky/meta/conf/distro/defaultsetup.conf:10: including conf/distro/include/tclibc-glibc.inc
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/poky/meta/conf/distro/defaultsetup.conf:12: including conf/distro/include/uninative-flags.inc
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/poky/meta/conf/bitbake.conf:755: including conf/documentation.conf
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/poky/meta/conf/bitbake.conf:756: including conf/licenses.conf
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/poky/meta/conf/bitbake.conf:757: including conf/sanity.conf
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/base.bbclass (from configuration INHERITs:0)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/patch.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/base.bbclass:4)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/terminal.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/patch.bbclass:11)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/staging.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/base.bbclass:5)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/mirrors.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/base.bbclass:7)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/utils.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/base.bbclass:8)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/utility-tasks.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/base.bbclass:9)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/metadata_scm.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/base.bbclass:10)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/logging.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/base.bbclass:11)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/meta-openembedded/meta-gnome/classes/sanity-meta-gnome.bbclass (from configuration INHERITs:0)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/meta-freescale/classes/machine-overrides-extender.bbclass (from configuration INHERITs:0)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/meta-freescale/classes/fsl-dynamic-packagearch.bbclass (from configuration INHERITs:0)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta-poky/classes/poky-sanity.bbclass (from configuration INHERITs:0)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/package_rpm.bbclass (from configuration INHERITs:0)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/package.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/package_rpm.bbclass:1)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/packagedata.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/package.bbclass:41)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/chrpath.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/package.bbclass:42)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/insane.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/package.bbclass:45)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/buildstats.bbclass (from configuration INHERITs:0)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image-mklibs.bbclass (from configuration INHERITs:0)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/linuxloader.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image-mklibs.bbclass:5)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image-prelink.bbclass (from configuration INHERITs:0)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/debian.bbclass (from configuration INHERITs:0)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/devshell.bbclass (from configuration INHERITs:0)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/sstate.bbclass (from configuration INHERITs:0)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/license.bbclass (from configuration INHERITs:0)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/remove-libtool.bbclass (from configuration INHERITs:0)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/blacklist.bbclass (from configuration INHERITs:0)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/sanity.bbclass (from configuration INHERITs:0)
DEBUG: Using cache in '/home/zengjf/imx8-yocto-ga/imx8-build-wayland/cache/bb_codeparser.dat'
DEBUG: Running /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:do_rootfs under fakeroot, fakedirs: /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/pseudo/
DEBUG: Parsing /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb (full)
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:4: including recipes-fsl/images/fsl-image-validation-imx.bb
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/core-image.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/recipes-fsl/images/fsl-image-validation-imx.bb:11)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/core-image.bbclass:72)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/rootfs_rpm.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:1)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/populate_sdk_ext.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:6)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/populate_sdk_base.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/populate_sdk_ext.bbclass:3)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/meta.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/populate_sdk_base.bbclass:1)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image_types_wic.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:145)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-bsp/classes/image_types_fsl.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:181)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image_types.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-bsp/classes/image_types_fsl.bbclass:1)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/siteinfo.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image_types.bbclass:230)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/kernel-arch.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image_types.bbclass:230)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/rootfs-postcommands.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:194)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/distro_features_check.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:6)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/populate_sdk_qt5.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:6)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/populate_sdk_qt5_base.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/populate_sdk_qt5.bbclass:3)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/qmake5_paths.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/populate_sdk_qt5_base.bbclass:2)
DEBUG: SITE files ['endian-little', 'bit-64', 'arm-common', 'arm-64', 'common-linux', 'common-glibc', 'aarch64-linux', 'common']
DEBUG: Adding task do_image_sdcard before do_image_complete, after do_image do_image_ext4
DEBUG: Adding task do_image_ext4 before do_image_complete, after do_image
DEBUG: Adding task do_image_tar before do_image_complete, after do_image
DEBUG: Executing task do_rootfs
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_rootfs: Executing python function extend_recipe_sysroot
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_rootfs: sed -e 's:^[^/]*/:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/recipe-sysroot-native/:g' /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/rpm-native/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/curl-native/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/libxml2-native/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/gdk-pixbuf-native/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/elfutils-native/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/dbus-native/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/e2fsprogs-native/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/gobject-introspection-native/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/libcheck-native/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/gpgme-native/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/nspr-native/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/libgpg-error-native/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/libassuan-native/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/intltool-native/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/gettext-native/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/perl-native/fixmepath | xargs sed -i -e 's:FIXMESTAGINGDIRTARGET:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/recipe-sysroot:g; s:FIXMESTAGINGDIRHOST:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/recipe-sysroot-native:g' -e 's:FIXME_COMPONENTS_DIR:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components:g' -e 's:FIXME_HOSTTOOLS_DIR:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/hosttools:g' -e 's:FIXME_PKGDATA_DIR:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/pkgdata/imx8qmmek:g' -e 's:FIXME_PSEUDO_LOCALSTATEDIR:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/pseudo/:g' -e 's:FIXME_LOGFIFO:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/temp/fifo.8197:g'
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_rootfs: Python function extend_recipe_sysroot finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_rootfs: Executing python function do_rootfs
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_rootfs: Executing python function write_package_manifest
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_rootfs: Python function write_package_manifest finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_rootfs: Executing python function license_create_manifest
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_rootfs: Python function license_create_manifest finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_rootfs: Executing shell function ssh_allow_empty_password
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_rootfs: Shell function ssh_allow_empty_password finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_rootfs: Executing shell function postinst_enable_logging
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_rootfs: Shell function postinst_enable_logging finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_rootfs: Executing shell function rootfs_update_timestamp
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_rootfs: Shell function rootfs_update_timestamp finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_rootfs: Executing python function write_image_test_data
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_rootfs: Python function write_image_test_data finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_rootfs: Executing shell function set_systemd_default_target
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_rootfs: Shell function set_systemd_default_target finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_rootfs: Executing shell function systemd_create_users
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_rootfs: Shell function systemd_create_users finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_rootfs: Executing shell function empty_var_volatile
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_rootfs: Shell function empty_var_volatile finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_rootfs: Executing python function sort_passwd
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_rootfs: Python function sort_passwd finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_rootfs: Executing shell function rootfs_reproducible
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_rootfs: Shell function rootfs_reproducible finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_rootfs: Executing python function write_image_manifest
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_rootfs: Python function write_image_manifest finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_rootfs: Python function do_rootfs finished
DEBUG: Marking task /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:do_image_qa as buildable
DEBUG: Stampfile /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/stamps/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0.do_image_qa.9fd957e006e459e08ecaa16db430665c not available
DEBUG: Running /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:do_image_qa under fakeroot, fakedirs: /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/pseudo/
DEBUG: Parsing /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb (full)
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:4: including recipes-fsl/images/fsl-image-validation-imx.bb
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/core-image.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/recipes-fsl/images/fsl-image-validation-imx.bb:11)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/core-image.bbclass:72)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/rootfs_rpm.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:1)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/populate_sdk_ext.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:6)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/populate_sdk_base.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/populate_sdk_ext.bbclass:3)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/meta.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/populate_sdk_base.bbclass:1)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image_types_wic.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:145)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-bsp/classes/image_types_fsl.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:181)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image_types.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-bsp/classes/image_types_fsl.bbclass:1)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/siteinfo.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image_types.bbclass:230)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/kernel-arch.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image_types.bbclass:230)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/rootfs-postcommands.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:194)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/distro_features_check.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:6)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/populate_sdk_qt5.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:6)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/populate_sdk_qt5_base.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/populate_sdk_qt5.bbclass:3)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/qmake5_paths.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/populate_sdk_qt5_base.bbclass:2)
DEBUG: SITE files ['endian-little', 'bit-64', 'arm-common', 'arm-64', 'common-linux', 'common-glibc', 'aarch64-linux', 'common']
DEBUG: Adding task do_image_sdcard before do_image_complete, after do_image do_image_ext4
DEBUG: Adding task do_image_ext4 before do_image_complete, after do_image
DEBUG: Adding task do_image_tar before do_image_complete, after do_image
DEBUG: Executing task do_image_qa
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_qa: Executing python function sstate_task_prefunc
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_qa: Python function sstate_task_prefunc finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_qa: Executing python function do_image_qa
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_qa: Python function do_image_qa finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_qa: Executing python function sstate_task_postfunc
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_qa: Executing python function sstate_hardcode_path_unpack
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_qa: Python function sstate_hardcode_path_unpack finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_qa: Python function sstate_task_postfunc finished
DEBUG: Marking task /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:do_image as buildable
DEBUG: Stampfile /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/stamps/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0.do_image.2f7c32961c8fbe6a83e17bd481e052b5 not available
DEBUG: Running /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:do_image under fakeroot, fakedirs: /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/pseudo/
DEBUG: Parsing /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb (full)
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:4: including recipes-fsl/images/fsl-image-validation-imx.bb
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/core-image.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/recipes-fsl/images/fsl-image-validation-imx.bb:11)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/core-image.bbclass:72)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/rootfs_rpm.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:1)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/populate_sdk_ext.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:6)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/populate_sdk_base.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/populate_sdk_ext.bbclass:3)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/meta.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/populate_sdk_base.bbclass:1)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image_types_wic.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:145)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-bsp/classes/image_types_fsl.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:181)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image_types.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-bsp/classes/image_types_fsl.bbclass:1)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/siteinfo.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image_types.bbclass:230)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/kernel-arch.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image_types.bbclass:230)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/rootfs-postcommands.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:194)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/distro_features_check.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:6)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/populate_sdk_qt5.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:6)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/populate_sdk_qt5_base.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/populate_sdk_qt5.bbclass:3)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/qmake5_paths.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/populate_sdk_qt5_base.bbclass:2)
DEBUG: SITE files ['endian-little', 'bit-64', 'arm-common', 'arm-64', 'common-linux', 'common-glibc', 'aarch64-linux', 'common']
DEBUG: Adding task do_image_sdcard before do_image_complete, after do_image do_image_ext4
DEBUG: Adding task do_image_ext4 before do_image_complete, after do_image
DEBUG: Adding task do_image_tar before do_image_complete, after do_image
DEBUG: Executing task do_image
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image: Executing python function do_image
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image: Executing shell function mklibs_optimize_image
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image: Shell function mklibs_optimize_image finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image: Executing python function prelink_setup
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image: Python function prelink_setup finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image: Executing shell function prelink_image
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image: Shell function prelink_image finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image: Executing shell function reproducible_final_image_task
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image: Shell function reproducible_final_image_task finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image: Python function do_image finished
DEBUG: Marking task /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:do_image_tar as buildable
DEBUG: Marking task /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:do_image_ext4 as buildable
DEBUG: Stampfile /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/stamps/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0.do_image_ext4.977ea96bb72a3b8265ca61aa9aeb0a4a not available
DEBUG: Stampfile /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/stamps/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0.do_image_tar.60df1a52ae1c375a0e1e71fb0ea53a36 not available
DEBUG: Running /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:do_image_ext4 under fakeroot, fakedirs: /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/pseudo/
DEBUG: Parsing /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb (full)
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:4: including recipes-fsl/images/fsl-image-validation-imx.bb
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/core-image.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/recipes-fsl/images/fsl-image-validation-imx.bb:11)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/core-image.bbclass:72)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/rootfs_rpm.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:1)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/populate_sdk_ext.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:6)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/populate_sdk_base.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/populate_sdk_ext.bbclass:3)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/meta.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/populate_sdk_base.bbclass:1)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image_types_wic.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:145)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-bsp/classes/image_types_fsl.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:181)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image_types.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-bsp/classes/image_types_fsl.bbclass:1)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/siteinfo.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image_types.bbclass:230)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/kernel-arch.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image_types.bbclass:230)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/rootfs-postcommands.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:194)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/distro_features_check.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:6)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/populate_sdk_qt5.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:6)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/populate_sdk_qt5_base.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/populate_sdk_qt5.bbclass:3)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/qmake5_paths.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/populate_sdk_qt5_base.bbclass:2)
DEBUG: Running /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:do_image_tar under fakeroot, fakedirs: /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/pseudo/
DEBUG: SITE files ['endian-little', 'bit-64', 'arm-common', 'arm-64', 'common-linux', 'common-glibc', 'aarch64-linux', 'common']
DEBUG: Adding task do_image_sdcard before do_image_complete, after do_image do_image_ext4
DEBUG: Adding task do_image_ext4 before do_image_complete, after do_image
DEBUG: Parsing /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb (full)
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:4: including recipes-fsl/images/fsl-image-validation-imx.bb
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/core-image.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/recipes-fsl/images/fsl-image-validation-imx.bb:11)
DEBUG: Adding task do_image_tar before do_image_complete, after do_image
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/core-image.bbclass:72)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/rootfs_rpm.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:1)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/populate_sdk_ext.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:6)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/populate_sdk_base.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/populate_sdk_ext.bbclass:3)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/meta.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/populate_sdk_base.bbclass:1)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image_types_wic.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:145)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-bsp/classes/image_types_fsl.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:181)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image_types.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-bsp/classes/image_types_fsl.bbclass:1)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/siteinfo.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image_types.bbclass:230)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/kernel-arch.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image_types.bbclass:230)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/rootfs-postcommands.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:194)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/distro_features_check.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:6)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/populate_sdk_qt5.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:6)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/populate_sdk_qt5_base.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/populate_sdk_qt5.bbclass:3)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/qmake5_paths.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/populate_sdk_qt5_base.bbclass:2)
DEBUG: SITE files ['endian-little', 'bit-64', 'arm-common', 'arm-64', 'common-linux', 'common-glibc', 'aarch64-linux', 'common']
DEBUG: Adding task do_image_sdcard before do_image_complete, after do_image do_image_ext4
DEBUG: Adding task do_image_ext4 before do_image_complete, after do_image
DEBUG: Adding task do_image_tar before do_image_complete, after do_image
DEBUG: Executing task do_image_ext4
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_ext4: Executing python function set_image_size
DEBUG: Executing task do_image_tar
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_tar: Executing python function set_image_size
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_ext4: 1069952.000000 = 823040 * 1.300000
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_ext4: 1069952.000000 = max(1069952.000000, 65536)[1069952.000000] + 1
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_ext4: 1069952.000000 = int(1069952.000000)
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_ext4: 1073152 = aligned(1069952)
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_ext4: returning 1073152
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_ext4: Python function set_image_size finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_ext4: Executing python function extend_recipe_sysroot
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_tar: 1069952.000000 = 823040 * 1.300000
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_tar: 1069952.000000 = max(1069952.000000, 65536)[1069952.000000] + 1
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_tar: 1069952.000000 = int(1069952.000000)
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_tar: 1073152 = aligned(1069952)
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_tar: returning 1073152
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_tar: Python function set_image_size finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_tar: Executing shell function do_image_tar
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_ext4: Python function extend_recipe_sysroot finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_ext4: Executing shell function do_image_ext4
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_ext4: Executing dd if=/dev/zero of=/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/deploy-fsl-image-qt5-validation-imx-image-complete/fsl-image-qt5-validation-imx-imx8qmmek-20190724080153.rootfs.ext4 seek=1073152 count=0 bs=1024
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_ext4: Actual Rootfs size:  823040   /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/rootfs
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_ext4: Actual Partion size: 1098907648
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_ext4: Executing mkfs.ext4 -F -i 4096 /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/deploy-fsl-image-qt5-validation-imx-image-complete/fsl-image-qt5-validation-imx-imx8qmmek-20190724080153.rootfs.ext4 -d /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/rootfs
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_ext4: Shell function do_image_ext4 finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_ext4: Executing python function create_symlinks
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_ext4: Python function create_symlinks finished
DEBUG: Marking task /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:do_image_sdcard as buildable
DEBUG: Stampfile /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/stamps/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0.do_image_sdcard.85fc5ba9c4ac362ecc258d7b613ed1a4 not available
DEBUG: Running /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:do_image_sdcard under fakeroot, fakedirs: /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/pseudo/
DEBUG: Parsing /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb (full)
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:4: including recipes-fsl/images/fsl-image-validation-imx.bb
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/core-image.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/recipes-fsl/images/fsl-image-validation-imx.bb:11)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/core-image.bbclass:72)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/rootfs_rpm.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:1)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/populate_sdk_ext.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:6)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/populate_sdk_base.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/populate_sdk_ext.bbclass:3)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/meta.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/populate_sdk_base.bbclass:1)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image_types_wic.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:145)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-bsp/classes/image_types_fsl.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:181)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image_types.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-bsp/classes/image_types_fsl.bbclass:1)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/siteinfo.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image_types.bbclass:230)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/kernel-arch.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image_types.bbclass:230)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/rootfs-postcommands.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:194)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/distro_features_check.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:6)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/populate_sdk_qt5.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:6)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/populate_sdk_qt5_base.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/populate_sdk_qt5.bbclass:3)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/qmake5_paths.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/populate_sdk_qt5_base.bbclass:2)
DEBUG: SITE files ['endian-little', 'bit-64', 'arm-common', 'arm-64', 'common-linux', 'common-glibc', 'aarch64-linux', 'common']
DEBUG: Adding task do_image_sdcard before do_image_complete, after do_image do_image_ext4
DEBUG: Adding task do_image_ext4 before do_image_complete, after do_image
DEBUG: Adding task do_image_tar before do_image_complete, after do_image
DEBUG: Executing task do_image_sdcard
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_sdcard: Executing python function set_image_size
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_sdcard: 1069952.000000 = 823040 * 1.300000
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_sdcard: 1069952.000000 = max(1069952.000000, 65536)[1069952.000000] + 1
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_sdcard: 1069952.000000 = int(1069952.000000)
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_sdcard: 1073152 = aligned(1069952)
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_sdcard: returning 1073152
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_sdcard: Python function set_image_size finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_sdcard: Executing python function extend_recipe_sysroot
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_tar: Shell function do_image_tar finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_tar: Executing python function create_symlinks
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_tar: Python function create_symlinks finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_sdcard: sed -e 's:^[^/]*/:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/recipe-sysroot/:g' /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/aarch64/python/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/aarch64/systemd/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/aarch64/curl/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/aarch64/base-passwd/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/aarch64/dbus/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/aarch64-mx8/pulseaudio/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/aarch64/avahi/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/aarch64/python3/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/aarch64/gobject-introspection/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/aarch64/icu/fixmepath | xargs sed -i -e 's:FIXMESTAGINGDIRTARGET:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/recipe-sysroot:g; s:FIXMESTAGINGDIRHOST:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/recipe-sysroot-native:g' -e 's:FIXME_COMPONENTS_DIR:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components:g' -e 's:FIXME_HOSTTOOLS_DIR:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/hosttools:g' -e 's:FIXME_PKGDATA_DIR:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/pkgdata/imx8qmmek:g' -e 's:FIXME_PSEUDO_LOCALSTATEDIR:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/pseudo/:g' -e 's:FIXME_LOGFIFO:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/temp/fifo.26109:g'
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_sdcard: sed -e 's:^[^/]*/:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/recipe-sysroot-native/:g' /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/gcc-cross-aarch64/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/python-native/fixmepath /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components/x86_64/gmp-native/fixmepath | xargs sed -i -e 's:FIXMESTAGINGDIRTARGET:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/recipe-sysroot:g; s:FIXMESTAGINGDIRHOST:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/recipe-sysroot-native:g' -e 's:FIXME_COMPONENTS_DIR:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/sysroots-components:g' -e 's:FIXME_HOSTTOOLS_DIR:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/hosttools:g' -e 's:FIXME_PKGDATA_DIR:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/pkgdata/imx8qmmek:g' -e 's:FIXME_PSEUDO_LOCALSTATEDIR:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/pseudo/:g' -e 's:FIXME_LOGFIFO:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/temp/fifo.26109:g'
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_sdcard: Python function extend_recipe_sysroot finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_sdcard: Executing shell function do_image_sdcard
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_sdcard: Shell function do_image_sdcard finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_sdcard: Executing python function create_symlinks
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_sdcard: Python function create_symlinks finished
DEBUG: Marking task /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:do_image_complete as buildable
DEBUG: Stampfile /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/stamps/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0.do_image_complete.a108ab65ecb25ad4cf03a86fb482884e.imx8qmmek not available
DEBUG: Running /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:do_image_complete under fakeroot, fakedirs: /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/pseudo/
DEBUG: Parsing /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb (full)
DEBUG: CONF /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:4: including recipes-fsl/images/fsl-image-validation-imx.bb
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/core-image.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/recipes-fsl/images/fsl-image-validation-imx.bb:11)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/core-image.bbclass:72)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/rootfs_rpm.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:1)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/populate_sdk_ext.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:6)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/populate_sdk_base.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/populate_sdk_ext.bbclass:3)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/meta.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/populate_sdk_base.bbclass:1)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image_types_wic.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:145)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-bsp/classes/image_types_fsl.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:181)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image_types.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-bsp/classes/image_types_fsl.bbclass:1)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/siteinfo.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image_types.bbclass:230)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/kernel-arch.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image_types.bbclass:230)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/rootfs-postcommands.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/image.bbclass:194)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/distro_features_check.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:6)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/populate_sdk_qt5.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:6)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/populate_sdk_qt5_base.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/populate_sdk_qt5.bbclass:3)
DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/qmake5_paths.bbclass (from /home/zengjf/imx8-yocto-ga/sources/meta-qt5/classes/populate_sdk_qt5_base.bbclass:2)
DEBUG: SITE files ['endian-little', 'bit-64', 'arm-common', 'arm-64', 'common-linux', 'common-glibc', 'aarch64-linux', 'common']
DEBUG: Adding task do_image_sdcard before do_image_complete, after do_image do_image_ext4
DEBUG: Adding task do_image_ext4 before do_image_complete, after do_image
DEBUG: Adding task do_image_tar before do_image_complete, after do_image
DEBUG: Executing task do_image_complete
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_complete: Executing python function sstate_task_prefunc
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_complete: Python function sstate_task_prefunc finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_complete: Executing python function do_image_complete
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_complete: Executing python function write_deploy_manifest
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_complete: Python function write_deploy_manifest finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_complete: Python function do_image_complete finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_complete: Executing python function sstate_task_postfunc
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_complete: Preparing tree /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/deploy-fsl-image-qt5-validation-imx-image-complete for packaging at /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/sstate-build-image_complete/deploy-fsl-image-qt5-validation-imx-image-complete
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_complete: Executing python function sstate_hardcode_path_unpack
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_complete: Python function sstate_hardcode_path_unpack finished
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_complete: Staging files from /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/deploy-fsl-image-qt5-validation-imx-image-complete to /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/deploy/images/imx8qmmek
DEBUG: fsl-image-qt5-validation-imx-1.0-r0 do_image_complete: Python function sstate_task_postfunc finished
DEBUG: Marking task /home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-sdk/dynamic-layers/qt5-layer/recipes-fsl/images/fsl-image-qt5-validation-imx.bb:do_build as buildable
DEBUG: Stampfile /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/stamps/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0.do_build.e3758fd90944b8b698bed4ad100ed1c4 not available
DEBUG: Teardown for bitbake-worker
DEBUG: Teardown for bitbake-worker
```