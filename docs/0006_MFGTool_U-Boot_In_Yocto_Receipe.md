# MFGTool U-Boot Compile In Yocto Receipe

## 参考文档

* [WORKDIR in yocto receipe](https://stackoverflow.com/questions/28827764/workdir-in-yocto-receipe)
* https://www.yoctoproject.org/docs/2.1/ref-manual/ref-manual.html#var-WORKDIR
* [Clean/Rebuild target using bitbake](https://community.nxp.com/thread/366592)

## 原理

* The WORKDIR directory is defined as follows: `${TMPDIR}/work/${MULTIMACH_TARGET_SYS}/${PN}/${EXTENDPE}${PV}-${PR}`
* `${TMPDIR}` will be the folder named "tmp" within your Yocto build directory.
* 以i.MX6编译U-Boot的mfgtool为例：
  * 查找所有和mfgtool相关的receipe：
    ```
    meta-fsl-arm/recipes-kernel/linux/linux-mfgtool.inc
    meta-fsl-arm/recipes-kernel/linux/linux-imx-mfgtool_4.1.15.bb
    meta-fsl-arm/classes/mfgtool-initramfs-image.bbclass
    meta-fsl-arm/recipes-fsl/images/fsl-image-mfgtool-initramfs.bb
    meta-fsl-arm/recipes-fsl/packagegroups/packagegroup-fsl-mfgtool.bb
    meta-fsl-arm/filesystem-layer/recipes-fsl/packagegroups/packagegroup-fsl-mfgtool.bbappend
    meta-fsl-arm/recipes-bsp/u-boot/u-boot-imx-mfgtool_2015.04.bb
    meta-fsl-arm/recipes-bsp/u-boot/u-boot-mfgtool.inc
    meta-fsl-bsp-release/imx/meta-sdk/recipes-fsl/packagegroup/packagegroup-fsl-mfgtool.bbappend
    meta-fsl-bsp-release/imx/meta-bsp/recipes-kernel/linux/linux-mfgtool.inc
    meta-fsl-bsp-release/imx/meta-bsp/recipes-kernel/linux/linux-imx-mfgtool_4.1.15.bb
    meta-fsl-bsp-release/imx/meta-bsp/recipes-bsp/u-boot/u-boot-imx-mfgtool_2016.03.bb
    ```
  * `MACHINE=imx6dlsabresd DISTRO=fsl-imx-x11 source ./fsl-setup-release.sh -b imx6q-x11`
  * 查看依赖：`bitbake fsl-image-mfgtool-initramfs -g`
    * `cat task-depends.dot`
      ```
      [...省略]
      "u-boot-imx-mfgtool.do_package" [label="u-boot-imx-mfgtool do_package\n:2016.03-r0\n/home/zengjf/fsl-release-bsp/sources/meta-fsl-bsp-release/imx/meta-bsp/recipes-bsp/u-boot/u-boot-imx-mfgtool_2016.03.bb"]
      [...省略]
      ```
    * `cat sources/meta-fsl-bsp-release/imx/meta-bsp/recipes-bsp/u-boot/u-boot-imx-mfgtool_2016.03.bb`
      ```
      # Copyright (C) 2014 O.S. Systems Software LTDA.
      # Copyright (C) 2014-2016 Freescale Semiconductor
      
      require u-boot-imx_${PV}.bb                   # PV=2016.03
      require recipes-bsp/u-boot/u-boot-mfgtool.inc
      ```
    * `cat sources/meta-fsl-bsp-release/imx/meta-bsp/recipes-bsp/u-boot/u-boot-imx_2016.03.bb`
      ```Shell
      # Copyright (C) 2013-2016 Freescale Semiconductor
      
      DESCRIPTION = "U-Boot provided by Freescale with focus on  i.MX reference boards."
      require recipes-bsp/u-boot/u-boot.inc
      
      PROVIDES += "u-boot"
      
      LICENSE = "GPLv2+"
      LIC_FILES_CHKSUM = "file://Licenses/gpl-2.0.txt;md5=b234ee4d69f5fce4486a80fdaf4a4263"
      
      SRCBRANCH = "imx_v2016.03_4.1.15_2.0.0_ga"
      UBOOT_SRC ?= "git://git.freescale.com/imx/uboot-imx.git;protocol=git"
      SRC_URI = "${UBOOT_SRC};branch=${SRCBRANCH}"
      SRCREV = "a57b13b942d59719e3621179e98bd8a0ab235088"
      
      S = "${WORKDIR}/git"
      
      inherit fsl-u-boot-localversion
      
      LOCALVERSION ?= "-${SRCBRANCH}"
      
      PACKAGE_ARCH = "${MACHINE_ARCH}"
      COMPATIBLE_MACHINE = "(mx6|mx6ul|mx6sll|mx7)"
      ```
    * `cat sources/poky/meta/recipes-bsp/u-boot/u-boot.inc`
      ```Shell
      SUMMARY = "Universal Boot Loader for embedded devices"
      HOMEPAGE = "http://www.denx.de/wiki/U-Boot/WebHome"
      SECTION = "bootloaders"
      PROVIDES = "virtual/bootloader"
      
      LICENSE = "GPLv2+"
      LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"
      
      SRC_URI = "git://git.denx.de/u-boot.git;branch=master"
      
      S = "$WORKDIR/git"
      
      PACKAGE_ARCH = "$MACHINE_ARCH"
      
      inherit uboot-config deploy
      
      EXTRA_OEMAKE = 'CROSS_COMPILE=$TARGET_PREFIX CC="${TARGET_PREFIX}gcc $TOOLCHAIN_OPTIONS" V=1'
      EXTRA_OEMAKE += 'HOSTCC="$BUILD_CC $BUILD_CFLAGS $BUILD_LDFLAGS"'
      
      PACKAGECONFIG ??= "openssl"
      # u-boot will compile its own tools during the build, with specific
      # configurations (aka when CONFIG_FIT_SIGNATURE is enabled) openssl is needed as
      # a host build dependency.
      PACKAGECONFIG[openssl] = ",,openssl-native"
      
      # Allow setting an additional version string that will be picked up by the
      # u-boot build system and appended to the u-boot version.  If the .scmversion
      # file already exists it will not be overwritten.
      UBOOT_LOCALVERSION ?= ""
      
      # Some versions of u-boot use .bin and others use .img.  By default use .bin
      # but enable individual recipes to change this value.
      UBOOT_SUFFIX ??= "bin"
      UBOOT_IMAGE ?= "u-boot-$MACHINE-$PV-$PR.$UBOOT_SUFFIX"
      UBOOT_BINARY ?= "u-boot.$UBOOT_SUFFIX"
      UBOOT_SYMLINK ?= "u-boot-$MACHINE.$UBOOT_SUFFIX"
      UBOOT_MAKE_TARGET ?= "all"
      
      # Output the ELF generated. Some platforms can use the ELF file and directly
      # load it (JTAG booting, QEMU) additionally the ELF can be used for debugging
      # purposes.
      UBOOT_ELF ?= ""
      UBOOT_ELF_SUFFIX ?= "elf"
      UBOOT_ELF_IMAGE ?= "u-boot-$MACHINE-$PV-$PR.$UBOOT_ELF_SUFFIX"
      UBOOT_ELF_BINARY ?= "u-boot.$UBOOT_ELF_SUFFIX"
      UBOOT_ELF_SYMLINK ?= "u-boot-$MACHINE.$UBOOT_ELF_SUFFIX"
      
      # Some versions of u-boot build an SPL (Second Program Loader) image that
      # should be packaged along with the u-boot binary as well as placed in the
      # deploy directory.  For those versions they can set the following variables
      # to allow packaging the SPL.
      SPL_BINARY ?= ""
      SPL_BINARYNAME ?= "${@os.path.basename(d.getVar("SPL_BINARY", True))}"
      SPL_IMAGE ?= "$SPL_BINARYNAME-$MACHINE-$PV-$PR"
      SPL_SYMLINK ?= "$SPL_BINARYNAME-$MACHINE"
      
      # Additional environment variables or a script can be installed alongside
      # u-boot to be used automatically on boot.  This file, typically 'uEnv.txt'
      # or 'boot.scr', should be packaged along with u-boot as well as placed in the
      # deploy directory.  Machine configurations needing one of these files should
      # include it in the SRC_URI and set the UBOOT_ENV parameter.
      UBOOT_ENV_SUFFIX ?= "txt"
      UBOOT_ENV ?= ""
      UBOOT_ENV_BINARY ?= "$UBOOT_ENV.$UBOOT_ENV_SUFFIX"
      UBOOT_ENV_IMAGE ?= "$UBOOT_ENV-$MACHINE-$PV-$PR.$UBOOT_ENV_SUFFIX"
      UBOOT_ENV_SYMLINK ?= "$UBOOT_ENV-$MACHINE.$UBOOT_ENV_SUFFIX"
      
      do_compile () {
          if test "${@bb.utils.contains('DISTRO_FEATURES', 'ld-is-gold', 'ld-is-gold', '', d)}" = "ld-is-gold"  ; then
              sed -i 's/$(CROSS_COMPILE)ld$/$(CROSS_COMPILE)ld.bfd/g' config.mk
          fi
      
          unset LDFLAGS
          unset CFLAGS
          unset CPPFLAGS
      
          if test ! -e $B/.scmversion -a ! -e $S/.scmversion 
          then
              echo $UBOOT_LOCALVERSION > $B/.scmversion
              echo $UBOOT_LOCALVERSION > $S/.scmversion
          fi
      
          if test "x$UBOOT_CONFIG" != "x" 
          then
              for config in $UBOOT_MACHINE; do
                  i=`expr $i + 1`;
                  for type  in $UBOOT_CONFIG; do
                      j=`expr $j + 1`;
                      if test $j -eq $i 
                      then
                          # 1. cat imx6q-x11/tmp/work/imx6dlsabresd-poky-linux-gnueabi/u-boot-imx-mfgtool/2016.03-r0/temp/run.do_compile.16791
                          #     [...省略]
                          #     oe_runmake O=${config} ${config}
                          #     oe_runmake O=${config} u-boot.imx
                          #     [...省略]
                          # 2. cat imx6q-x11/tmp/work/imx6dlsabresd-poky-linux-gnueabi/u-boot-imx-mfgtool/2016.03-r0/temp/log.do_compile.16791
                          #     [...省略]
                          #     NOTE: make -j 8 CROSS_COMPILE=arm-poky-linux-gnueabi- CC=arm-poky-linux-gnueabi-gcc  --sysroot=/home/aplex/sbc7819/fsl-release-bsp/imx6q-x11/tmp/sysroots/imx6dlsabresd V=1 HOSTCC=gcc  -isystem/home/aplex/sbc7819/fsl-release-bsp/imx6q-x11/tmp/sysroots/x86_64-linux/usr/include -O2 -pipe -L/home/aplex/sbc7819/fsl-release-bsp/imx6q-x11/tmp/sysroots/x86_64-linux/usr/lib -L/home/aplex/sbc7819/fsl-release-bsp/imx6q-x11/tmp/sysroots/x86_64-linux/lib -Wl,-rpath-link,/home/aplex/sbc7819/fsl-release-bsp/imx6q-x11/tmp/sysroots/x86_64-linux/usr/lib -Wl,-rpath-link,/home/aplex/sbc7819/fsl-release-bsp/imx6q-x11/tmp/sysroots/x86_64-linux/lib -Wl,-rpath,/home/aplex/sbc7819/fsl-release-bsp/imx6q-x11/tmp/sysroots/x86_64-linux/usr/lib -Wl,-rpath,/home/aplex/sbc7819/fsl-release-bsp/imx6q-x11/tmp/sysroots/x86_64-linux/lib -Wl,-O1 O=mx6dlsabresd_config mx6dlsabresd_config
                          #     make -C /home/aplex/sbc7819/fsl-release-bsp/imx6q-x11/tmp/work/imx6dlsabresd-poky-linux-gnueabi/u-boot-imx-mfgtool/2016.03-r0/git/mx6dlsabresd_config KBUILD_SRC=/home/aplex/sbc7819/fsl-release-bsp/imx6q-x11/tmp/work/imx6dlsabresd-poky-linux-gnueabi/u-boot-imx-mfgtool/2016.03-r0/git \
                          #             -f /home/aplex/sbc7819/fsl-release-bsp/imx6q-x11/tmp/work/imx6dlsabresd-poky-linux-gnueabi/u-boot-imx-mfgtool/2016.03-r0/git/Makefile mx6dlsabresd_config
                          #     make[1]: Entering directory `/home/aplex/sbc7819/fsl-release-bsp/imx6q-x11/tmp/work/imx6dlsabresd-poky-linux-gnueabi/u-boot-imx-mfgtool/2016.03-r0/git/mx6dlsabresd_config'
                          #     make -f ../scripts/Makefile.build obj=scripts/basic
                          #     ln -fsn .. source
                          #     /bin/bash ../scripts/mkmakefile \
                          #                 .. . 2016 03
                          #     rm -f .tmp_quiet_recordmcount
                          #       GEN     ./Makefile
                          #     make -f ../scripts/Makefile.build obj=scripts/kconfig mx6dlsabresd_config
                          #     make[2]: `../mx6dlsabresd_config' is up to date.
                          #     make[1]: Leaving directory `/home/aplex/sbc7819/fsl-release-bsp/imx6q-x11/tmp/work/imx6dlsabresd-poky-linux-gnueabi/u-boot-imx-mfgtool/2016.03-r0/git/mx6dlsabresd_config'
                          #     NOTE: make -j 8 CROSS_COMPILE=arm-poky-linux-gnueabi- CC=arm-poky-linux-gnueabi-gcc  --sysroot=/home/aplex/sbc7819/fsl-release-bsp/imx6q-x11/tmp/sysroots/imx6dlsabresd V=1 HOSTCC=gcc  -isystem/home/aplex/sbc7819/fsl-release-bsp/imx6q-x11/tmp/sysroots/x86_64-linux/usr/include -O2 -pipe -L/home/aplex/sbc7819/fsl-release-bsp/imx6q-x11/tmp/sysroots/x86_64-linux/usr/lib -L/home/aplex/sbc7819/fsl-release-bsp/imx6q-x11/tmp/sysroots/x86_64-linux/lib -Wl,-rpath-link,/home/aplex/sbc7819/fsl-release-bsp/imx6q-x11/tmp/sysroots/x86_64-linux/usr/lib -Wl,-rpath-link,/home/aplex/sbc7819/fsl-release-bsp/imx6q-x11/tmp/sysroots/x86_64-linux/lib -Wl,-rpath,/home/aplex/sbc7819/fsl-release-bsp/imx6q-x11/tmp/sysroots/x86_64-linux/usr/lib -Wl,-rpath,/home/aplex/sbc7819/fsl-release-bsp/imx6q-x11/tmp/sysroots/x86_64-linux/lib -Wl,-O1 O=mx6dlsabresd_config u-boot.imx
                          #     make -C /home/aplex/sbc7819/fsl-release-bsp/imx6q-x11/tmp/work/imx6dlsabresd-poky-linux-gnueabi/u-boot-imx-mfgtool/2016.03-r0/git/mx6dlsabresd_config KBUILD_SRC=/home/aplex/sbc7819/fsl-release-bsp/imx6q-x11/tmp/work/imx6dlsabresd-poky-linux-gnueabi/u-boot-imx-mfgtool/2016.03-r0/git \
                          #             -f /home/aplex/sbc7819/fsl-release-bsp/imx6q-x11/tmp/work/imx6dlsabresd-poky-linux-gnueabi/u-boot-imx-mfgtool/2016.03-r0/git/Makefile u-boot.imx
                          #     make[1]: Entering directory `/home/aplex/sbc7819/fsl-release-bsp/imx6q-x11/tmp/work/imx6dlsabresd-poky-linux-gnueabi/u-boot-imx-mfgtool/2016.03-r0/git/mx6dlsabresd_config'
                          #     set -e; : '  CHK     include/config/uboot.release'; mkdir -p include/config/;   echo "2016.03$(/bin/bash ../scripts/setlocalversion ..)" < include/config/auto.conf > include/config/uboot.release.tmp; if [ -r include/config/uboot.release ] && cmp -s include/config/uboot.release include/config/uboot.release.tmp; then rm -f include/config/uboot.release.tmp; else : '  UPD     include/config/uboot.release'; mv -f include/config/uboot.release.tmp include/config/uboot.release; fi
                          #     [...省略]
                          #     make -f ../scripts/Makefile.build obj=arch/arm/imx-common u-boot.imx
                          #     mkdir -p board/freescale/mx6sabresd/
                          #       ./tools/mkimage -n board/freescale/mx6sabresd/mx6dlsabresd.cfg.cfgtmp -T imximage -e 0x17800000 -d u-boot.bin u-boot.imx
                          #     Image Type:   Freescale IMX Boot Image
                          #     Image Ver:    2 (i.MX53/6/7 compatible)
                          #     Mode:         DCD
                          #     Data Size:    479232 Bytes = 468.00 kB = 0.46 MB
                          #     Load Address: 177ff420
                          #     Entry Point:  17800000
                          oe_runmake O=$config $config
                          oe_runmake O=$config $UBOOT_MAKE_TARGET
                          cp  $S/$config/$UBOOT_BINARY  $S/$config/u-boot-$type.$UBOOT_SUFFIX
                      fi
                  done
                  unset  j
              done
              unset  i
          else
              oe_runmake $UBOOT_MACHINE
              oe_runmake $UBOOT_MAKE_TARGET
          fi
      }
      
      do_install () {
          if test "x$UBOOT_CONFIG" != "x" 
          then
              for config in $UBOOT_MACHINE; do
                  i=`expr $i + 1`;
                  for type in $UBOOT_CONFIG; do
                      j=`expr $j + 1`;
                      if test $j -eq $i 
                      then
                          install -d $D/boot
                          install $S/$config/u-boot-$type.$UBOOT_SUFFIX $D/boot/u-boot-$type-$PV-$PR.$UBOOT_SUFFIX
                          ln -sf u-boot-$type-$PV-$PR.$UBOOT_SUFFIX $D/boot/$UBOOT_BINARY-$type
                          ln -sf u-boot-$type-$PV-$PR.$UBOOT_SUFFIX $D/boot/$UBOOT_BINARY
                      fi
                  done
                  unset  j
              done
              unset  i
          else
              install -d $D/boot
              install $S/$UBOOT_BINARY $D/boot/$UBOOT_IMAGE
              ln -sf $UBOOT_IMAGE $D/boot/$UBOOT_BINARY
          fi
      
          if test "x$UBOOT_ELF" != "x" 
          then
              if test "x$UBOOT_CONFIG" != "x" 
              then
                  for config in $UBOOT_MACHINE; do
                      i=`expr $i + 1`;
                      for type in $UBOOT_CONFIG; do
                          j=`expr $j + 1`;
                          if test $j -eq $i 
                          then
                              install $S/$config/$UBOOT_ELF $D/boot/u-boot-$type-$PV-$PR.$UBOOT_ELF_SUFFIX
                              ln -sf u-boot-$type-$PV-$PR.$UBOOT_ELF_SUFFIX $D/boot/$UBOOT_BINARY-$type
                              ln -sf u-boot-$type-$PV-$PR.$UBOOT_ELF_SUFFIX $D/boot/$UBOOT_BINARY
                          fi
                      done
                      unset j
                  done
                  unset i
              else
                  install $S/$UBOOT_ELF $D/boot/$UBOOT_ELF_IMAGE
                  ln -sf $UBOOT_ELF_IMAGE $D/boot/$UBOOT_ELF_BINARY
              fi
          fi
      
          if test -e $WORKDIR/fw_env.config  ; then
              install -d $D${sysconfdir}
              install -m 644 $WORKDIR/fw_env.config $D${sysconfdir}/fw_env.config
          fi
      
          if test "x$SPL_BINARY" != "x" 
          then
              if test "x$UBOOT_CONFIG" != "x" 
              then
                  for config in $UBOOT_MACHINE; do
                      i=`expr $i + 1`;
                      for type in $UBOOT_CONFIG; do
                          j=`expr $j + 1`;
                          if test $j -eq $i 
                          then
                              install $S/$config/$SPL_BINARY $D/boot/$SPL_IMAGE-$type-$PV-$PR
                              ln -sf $SPL_IMAGE-$type-$PV-$PR $D/boot/$SPL_BINARYNAME-$type
                              ln -sf $SPL_IMAGE-$type-$PV-$PR $D/boot/$SPL_BINARYNAME
                          fi
                      done
                      unset  j
                  done
                  unset  i
              else
                  install $S/$SPL_BINARY $D/boot/$SPL_IMAGE
                  ln -sf $SPL_IMAGE $D/boot/$SPL_BINARYNAME
              fi
          fi
      
          if test "x$UBOOT_ENV" != "x" 
          then
              install $WORKDIR/$UBOOT_ENV_BINARY $D/boot/$UBOOT_ENV_IMAGE
              ln -sf $UBOOT_ENV_IMAGE $D/boot/$UBOOT_ENV_BINARY
          fi
      }
      
      FILES_$PN = "/boot $sysconfdir"
      
      do_deploy () {
          if test "x$UBOOT_CONFIG" != "x" 
          then
              for config in $UBOOT_MACHINE; do
                  i=`expr $i + 1`;
                  for type in $UBOOT_CONFIG; do
                      j=`expr $j + 1`;
                      if test $j -eq $i 
                      then
                          install -d $DEPLOYDIR
                          install $S/$config/u-boot-$type.$UBOOT_SUFFIX $DEPLOYDIR/u-boot-$type-$PV-$PR.$UBOOT_SUFFIX
                          cd $DEPLOYDIR
                          ln -sf u-boot-$type-$PV-$PR.$UBOOT_SUFFIX $UBOOT_SYMLINK-$type
                          ln -sf u-boot-$type-$PV-$PR.$UBOOT_SUFFIX $UBOOT_SYMLINK
                          ln -sf u-boot-$type-$PV-$PR.$UBOOT_SUFFIX $UBOOT_BINARY-$type
                          ln -sf u-boot-$type-$PV-$PR.$UBOOT_SUFFIX $UBOOT_BINARY
                      fi
                  done
                  unset  j
              done
              unset  i
          else
              install -d $DEPLOYDIR
              install $S/$UBOOT_BINARY $DEPLOYDIR/$UBOOT_IMAGE
              cd $DEPLOYDIR
              rm -f $UBOOT_BINARY $UBOOT_SYMLINK
              ln -sf $UBOOT_IMAGE $UBOOT_SYMLINK
              ln -sf $UBOOT_IMAGE $UBOOT_BINARY
          fi
      
          if test "x$UBOOT_ELF" != "x" 
          then
              if test "x$UBOOT_CONFIG" != "x" 
              then
                  for config in $UBOOT_MACHINE; do
                      i=`expr $i + 1`;
                      for type in $UBOOT_CONFIG; do
                          j=`expr $j + 1`;
                          if test $j -eq $i 
                          then
                              install $S/$config/$UBOOT_ELF $DEPLOYDIR/u-boot-$type-$PV-$PR.$UBOOT_ELF_SUFFIX
                              ln -sf u-boot-$type-$PV-$PR.$UBOOT_ELF_SUFFIX $DEPLOYDIR/$UBOOT_ELF_BINARY-$type
                              ln -sf u-boot-$type-$PV-$PR.$UBOOT_ELF_SUFFIX $DEPLOYDIR/$UBOOT_ELF_BINARY
                              ln -sf u-boot-$type-$PV-$PR.$UBOOT_ELF_SUFFIX $DEPLOYDIR/$UBOOT_ELF_SYMLINK-$type
                              ln -sf u-boot-$type-$PV-$PR.$UBOOT_ELF_SUFFIX $DEPLOYDIR/$UBOOT_ELF_SYMLINK
                          fi
                      done
                      unset j
                  done
                  unset i
              else
                  install $S/$UBOOT_ELF $DEPLOYDIR/$UBOOT_ELF_IMAGE
                  ln -sf $UBOOT_ELF_IMAGE $DEPLOYDIR/$UBOOT_ELF_BINARY
                  ln -sf $UBOOT_ELF_IMAGE $DEPLOYDIR/$UBOOT_ELF_SYMLINK
              fi
          fi
      
          if test "x$SPL_BINARY" != "x" 
          then
              if test "x$UBOOT_CONFIG" != "x" 
              then
                  for config in $UBOOT_MACHINE; do
                      i=`expr $i + 1`;
                      for type in $UBOOT_CONFIG; do
                          j=`expr $j + 1`;
                          if test $j -eq $i 
                          then
                              install $S/$config/$SPL_BINARY $DEPLOYDIR/$SPL_IMAGE-$type-$PV-$PR
                              rm -f $DEPLOYDIR/$SPL_BINARYNAME $DEPLOYDIR/$SPL_SYMLINK-$type
                              ln -sf $SPL_IMAGE-$type-$PV-$PR $DEPLOYDIR/$SPL_BINARYNAME-$type
                              ln -sf $SPL_IMAGE-$type-$PV-$PR $DEPLOYDIR/$SPL_BINARYNAME
                              ln -sf $SPL_IMAGE-$type-$PV-$PR $DEPLOYDIR/$SPL_SYMLINK-$type
                              ln -sf $SPL_IMAGE-$type-$PV-$PR $DEPLOYDIR/$SPL_SYMLINK
                          fi
                      done
                      unset  j
                  done
                  unset  i
              else
                  install $S/$SPL_BINARY $DEPLOYDIR/$SPL_IMAGE
                  rm -f $DEPLOYDIR/$SPL_BINARYNAME $DEPLOYDIR/$SPL_SYMLINK
                  ln -sf $SPL_IMAGE $DEPLOYDIR/$SPL_BINARYNAME
                  ln -sf $SPL_IMAGE $DEPLOYDIR/$SPL_SYMLINK
              fi
          fi
      
          if test "x$UBOOT_ENV" != "x" 
          then
              install $WORKDIR/$UBOOT_ENV_BINARY $DEPLOYDIR/$UBOOT_ENV_IMAGE
              rm -f $DEPLOYDIR/$UBOOT_ENV_BINARY $DEPLOYDIR/$UBOOT_ENV_SYMLINK
              ln -sf $UBOOT_ENV_IMAGE $DEPLOYDIR/$UBOOT_ENV_BINARY
              ln -sf $UBOOT_ENV_IMAGE $DEPLOYDIR/$UBOOT_ENV_SYMLINK
          fi
      }
      ```
    * `cat sources/meta-fsl-arm/classes/fsl-u-boot-localversion.bbclass`
      ```Shell
      # Freescale U-Boot LOCALVERSION extension
      #
      # This allow to easy reuse of code between different U-Boot recipes
      #
      # The following options are supported:
      #
      #  SCMVERSION        Puts the Git hash in U-Boot local version
      #  LOCALVERSION      Value used in LOCALVERSION (default to '+fslc')
      #
      # Copyright 2014 (C) O.S. Systems Software LTDA.
      
      SCMVERSION ??= "y"
      LOCALVERSION ??= "+fslc"
      
      UBOOT_LOCALVERSION = "${LOCALVERSION}"
      
      do_compile_prepend() {
              if [ "${SCMVERSION}" = "y" ]; then
                      # Add GIT revision to the local version
                      head=`git rev-parse --verify --short HEAD 2> /dev/null`
                      printf "%s%s%s" "${UBOOT_LOCALVERSION}" +g $head > ${S}/.scmversion
                      printf "%s%s%s" "${UBOOT_LOCALVERSION}" +g $head > ${B}/.scmversion
          else
                      printf "%s" "${UBOOT_LOCALVERSION}" > ${S}/.scmversion
                      printf "%s" "${UBOOT_LOCALVERSION}" > ${B}/.scmversion
              fi
      }
      ```
    * `cat sources/meta-fsl-arm/recipes-bsp/u-boot/u-boot-mfgtool.inc`
      ```Shell
      # Produces a Manufacturing Tool compatible U-Boot
      #
      # This makes a separated binary set for Manufacturing Tool use
      # without clobbering the U-Boot used for normal use.
      #
      # This file must to be included after the original u-boot.inc file,
      # as it overrides the need values.
      #
      # Copyright (C) 2014 O.S. Systems Software LTDA.
      
      # Adjust provides
      PROVIDES = "u-boot-mfgtool"
      
      # Use 'mfgtool' config
      UBOOT_CONFIG = "mfgtool"
      
      # Add 'mfgtool' suffix
      UBOOT_IMAGE = "u-boot-${MACHINE}-mfgtool-${PV}-${PR}.${UBOOT_SUFFIX}"
      UBOOT_SYMLINK = "u-boot-${MACHINE}-mfgtool.${UBOOT_SUFFIX}"
      SPL_IMAGE = "${SPL_BINARY}-${MACHINE}-mfgtool-${PV}-${PR}"
      SPL_SYMLINK = "${SPL_BINARY}-mfgtool-${MACHINE}"
      ```
    * WORKDIR: `imx6q-x11/tmp/work/imx6dlsabresd-poky-linux-gnueabi/u-boot-imx-mfgtool/2016.03-r0`
    * 单独编译u-boot: `bitbake u-boot-imx-mfgtool`
    * Compile Log: `ls imx6q-x11/tmp/work/imx6dlsabresd-poky-linux-gnueabi/u-boot-imx-mfgtool/2016.03-r0/temp/log*`
      ```
      log.do_compile         log.do_deploy.16348   log.do_package            log.do_package_qa.16324         log.do_populate_lic            log.do_unpack.5868
      log.do_compile.6802    log.do_fetch          log.do_package.14236      log.do_package_write_rpm        log.do_populate_lic.15027      log.task_order
      log.do_configure       log.do_fetch.5838     log.do_packagedata        log.do_package_write_rpm.15578  log.do_populate_sysroot
      log.do_configure.6681  log.do_install        log.do_packagedata.14598  log.do_patch                    log.do_populate_sysroot.13944
      log.do_deploy          log.do_install.12192  log.do_package_qa         log.do_patch.6645               log.do_unpack
      ```
    * 重新编译：
      * `bitbake -f -c compile u-boot-imx-mfgtool`
      * `bitbake -f -c install u-boot-imx-mfgtool`
      * `bitbake -f -c deploy u-boot-imx-mfgtool`
      * 在make之前，clean一下的做法：`cat sources/poky/meta/recipes-bsp/u-boot/u-boot.inc`
        ```Makefile
        [...省略]
        oe_runmake O=${config} clean
        oe_runmake O=${config} ${config}
        oe_runmake O=${config} ${UBOOT_MAKE_TARGET}
        cp  ${S}/${config}/${UBOOT_BINARY}  ${S}/${config}/u-boot-${type}.${UBOOT_SUFFIX}
        [...省略]
        ```
      * 查看新生成的u-boot：`cat imx6q-x11/tmp/deploy/images/imx6dlsabresd$ ls -al u-boot*`
        ```
        lrwxrwxrwx 1 aplex aplex     29  7月  3 13:35 u-boot.imx -> u-boot-mfgtool-2016.03-r0.imx
        lrwxrwxrwx 1 aplex aplex     24  6月 21 09:02 u-boot-imx6dlsabresd.imx -> u-boot-sd-2016.03-r0.imx
        lrwxrwxrwx 1 aplex aplex     24  6月 21 09:02 u-boot-imx6dlsabresd.imx-sd -> u-boot-sd-2016.03-r0.imx
        lrwxrwxrwx 1 aplex aplex     29  7月  3 13:35 u-boot-imx6dlsabresd-mfgtool.imx -> u-boot-mfgtool-2016.03-r0.imx
        lrwxrwxrwx 1 aplex aplex     29  7月  3 13:35 u-boot-imx6dlsabresd-mfgtool.imx-mfgtool -> u-boot-mfgtool-2016.03-r0.imx
        lrwxrwxrwx 1 aplex aplex     29  7月  3 13:35 u-boot.imx-mfgtool -> u-boot-mfgtool-2016.03-r0.imx
        lrwxrwxrwx 1 aplex aplex     24  6月 21 09:02 u-boot.imx-sd -> u-boot-sd-2016.03-r0.imx
        -rwxr-xr-x 2 aplex aplex 486400  7月  3 13:35 u-boot-mfgtool-2016.03-r0.imx
        -rwxr-xr-x 2 aplex aplex 478208  6月 13 17:55 u-boot-sd-2016.03-r0.imx
        ```
    * 修改mfgtools U-Boot BOOTDELAY: `cat imx6q-x11/tmp/work/imx6dlsabresd-poky-linux-gnueabi/u-boot-imx-mfgtool/2016.03-r0/git/mx6dlsabresd_config/source/common/autoboot.c`
      ```C
      [...省略]
      const char *bootdelay_process(void)
      {
      [...省略]
      #if !defined(CONFIG_FSL_FASTBOOT) && defined(is_boot_from_usb)
              if (is_boot_from_usb()) {
                      disconnect_from_pc();
                      printf("Boot from USB for mfgtools\n");
                      bootdelay = 0;
                      set_default_env("Use default environment for \
                                       mfgtools\n");
              } else {
                      printf("Normal Boot\n");
              }
      #endif
      [...省略]
      }
      [...省略]
      ```
    * DCD Table地址信息： `od -x -j 0x2c -N 4 u-boot.imx`
      ```
      0000054 02d2 40f0     # bigend: d202 f040
      0000060
      ```
