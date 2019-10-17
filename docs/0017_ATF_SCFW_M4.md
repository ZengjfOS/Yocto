# ATF SCFW M4

## 参考文档

* [0013_u-boot_generate.md](0013_u-boot_generate.md)

## 编译操作流程

* 创建或者进入编译环境：
  * `DISTRO=fsl-imx-wayland MACHINE=imx8qmmek source ./fsl-setup-release.sh -b imx8-build-wayland`
  * `source setup-environment imx8-build-wayland`
* `bitbake imx-atf -c compile -f`
* `bitbake imx-atf -c deploy -f`
* `bitbake imx-boot -c compile -f`
* `bitbake imx-boot -c deploy -f`

## ATF analysis

* `meta-fsl-bsp-release/imx/meta-bsp/recipes-bsp/imx-mkimage/imx-boot_0.2.bb`
  ```
  do_compile () {
      [...省略]
      # mkimage for i.MX8
      for target in ${IMXBOOT_TARGETS}; do
          echo "building ${SOC_TARGET} - ${target}"
          make SOC=${SOC_TARGET} ${target}
          if [ -e "${S}/${SOC_DIR}/flash.bin" ]; then
              cp ${S}/${SOC_DIR}/flash.bin ${S}/${BOOT_CONFIG_MACHINE}-${target}
          fi
      done
  }
  ```
* check imx-boot type:
  ```
  zengjf@UbuntuServer:imx8qmmek$ ls -al imx-boot-imx8qmmek-sd.bin
  lrwxrwxrwx 2 zengjf zengjf 40 Sep 24 11:45 imx-boot-imx8qmmek-sd.bin -> imx-boot-imx8qmmek-sd.bin-flash_linux_m4
  zengjf@UbuntuServer:imx8qmmek$ pwd
  /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/deploy/images/imx8qmmek
  ```
* `${target}`: `flash_linux_m4`
* `tmp/work/imx8qmmek-poky-linux/imx-boot/0.2-r0/git/iMX8QM/soc.mak`
  ```
  [...省略]
  
  u-boot-hash.bin: u-boot.bin
          ./$(MKIMG) -commit > head.hash
          @cat u-boot.bin head.hash > u-boot-hash.bin
  
  u-boot-atf.bin: u-boot-hash.bin bl31.bin
          @cp bl31.bin u-boot-atf.bin
          @dd if=u-boot-hash.bin of=u-boot-atf.bin bs=1K seek=128
          @if [ -f "hdmitxfw.bin" ] && [ -f "hdmirxfw.bin" ]; then \
          objcopy -I binary -O binary --pad-to 0x20000 --gap-fill=0x0 hdmitxfw.bin hdmitxfw-pad.bin; \
          objcopy -I binary -O binary --pad-to 0x20000 --gap-fill=0x0 hdmirxfw.bin hdmirxfw-pad.bin; \
          cat u-boot-atf.bin hdmitxfw-pad.bin hdmirxfw-pad.bin > u-boot-atf-hdmi.bin; \
          cp u-boot-atf-hdmi.bin u-boot-atf.bin; \
          fi
  
  [...省略]
  
  flash_linux_m4: $(MKIMG) mx8qm-ahab-container.img scfw_tcm.bin u-boot-spl.bin m4_image.bin m4_1_image.bin u-boot-atf-container.img
          ./$(MKIMG) -soc QM -rev B0 -dcd skip -append mx8qm-ahab-container.img -c -flags 0x00200000 -scfw scfw_tcm.bin -ap u-boot-spl.bin a53 0x00100000 -p3 -m4 m4_image.bin 0 0x34FE0000 -p4 -m4 m4_1_image.bin 1 0x38FE0000 -out flash.bin
          cp flash.bin boot-spl-container.img
          @flashbin_size=`wc -c flash.bin | awk '{print $$1}'`; \
                     pad_cnt=$$(((flashbin_size + 0x400 - 1) / 0x400)); \
                     echo "append u-boot-atf-container.img at $$pad_cnt KB"; \
                     dd if=u-boot-atf-container.img of=flash.bin bs=1K seek=$$pad_cnt;
  [...省略]
  ```
* `bl31.bin`
* `meta-fsl-bsp-release/imx/meta-bsp/recipes-bsp/imx-mkimage/imx-boot_0.2.bb`
  ```
  elif [ "${SOC_TARGET}" = "iMX8QM" ]; then
      echo 8QM boot binary build
      cp ${DEPLOY_DIR_IMAGE}/${BOOT_TOOLS}/${SC_FIRMWARE_NAME} ${S}/${SOC_DIR}/scfw_tcm.bin
      cp ${DEPLOY_DIR_IMAGE}/${BOOT_TOOLS}/${ATF_MACHINE_NAME} ${S}/${SOC_DIR}/bl31.bin
      cp ${DEPLOY_DIR_IMAGE}/${UBOOT_NAME}                     ${S}/${SOC_DIR}/u-boot.bin
      if ${DEPLOY_OPTEE}; then
          cp ${DEPLOY_DIR_IMAGE}/u-boot-spl.bin-${MACHINE}-${UBOOT_CONFIG} ${S}/${SOC_DIR}/u-boot-spl.bin
      fi
  
      cp ${DEPLOY_DIR_IMAGE}/imx8qm_m4_0_TCM_rpmsg_lite_pingpong_rtos_linux_remote_m40.bin ${S}/${SOC_DIR}/m40_tcm.bin
      cp ${DEPLOY_DIR_IMAGE}/imx8qm_m4_1_TCM_rpmsg_lite_pingpong_rtos_linux_remote_m41.bin ${S}/${SOC_DIR}/m41_tcm.bin
      cp ${DEPLOY_DIR_IMAGE}/imx8qm_m4_0_TCM_rpmsg_lite_pingpong_rtos_linux_remote_m40.bin ${S}/${SOC_DIR}/m4_image.bin
      cp ${DEPLOY_DIR_IMAGE}/imx8qm_m4_1_TCM_rpmsg_lite_pingpong_rtos_linux_remote_m41.bin ${S}/${SOC_DIR}/m4_1_image.bin
  
      cp ${DEPLOY_DIR_IMAGE}/mx8qm-ahab-container.img ${S}/${SOC_DIR}/
  ```
* `${BOOT_TOOLS}`: `imx-boot-tools`
* `meta-fsl-bsp-release/imx/meta-bsp/recipes-bsp/imx-atf/imx-atf_2.0.bb`
  ```
  do_deploy () {
      install -d ${DEPLOYDIR}/${BOOT_TOOLS}
      install -m 0644 ${S}/build/${SOC_ATF}/release/bl31.bin ${DEPLOYDIR}/${BOOT_TOOLS}/bl31-${SOC_ATF}.bin
      # Deploy opteee version
      if [ "${BUILD_OPTEE}" = "true" ]; then
          install -m 0644 ${S}/build-optee/${SOC_ATF}/release/bl31.bin ${DEPLOYDIR}/${BOOT_TOOLS}/bl31-${SOC_ATF}.bin-optee
      fi
  }
  ```
* `ls tmp/work/imx8qmmek-poky-linux/imx-atf/2.0+gitAUTOINC+1cb68fa0a0-r0/git`
  ```
  acknowledgements.rst  bl2   bl31  build        common            dco.txt  drivers  include  license.rst      Makefile      plat        services
  bl1                   bl2u  bl32  build-optee  contributing.rst  docs     fdts     lib      maintainers.rst  make_helpers  readme.rst  tools
  ```
* `meta-fsl-bsp-release/imx/meta-bsp/recipes-bsp/imx-atf/imx-atf_2.0.bb`
  ```
  do_compile () {
      export CROSS_COMPILE="${TARGET_PREFIX}"
      cd ${S}
      # Clear LDFLAGS to avoid the option -Wl recognize issue
      unset LDFLAGS
  
      echo "-> Build ${SOC_ATF} bl31.bin"
      # Set BUIL_STRING with the revision info
      BUILD_STRING=""
      if [ -e ${S}/.revision ]; then
          cur_rev=`cat ${S}/.revision`
          echo " Current revision is ${cur_rev} ."
          BUILD_STRING="BUILD_STRING=${cur_rev}"
      else
          echo " No .revision found! "
      fi
      oe_runmake clean PLAT=${SOC_ATF}
      oe_runmake ${BUILD_STRING} PLAT=${SOC_ATF} bl31
  
      # Build opteee version
      if [ "${BUILD_OPTEE}" = "true" ]; then
          oe_runmake clean PLAT=${SOC_ATF} BUILD_BASE=build-optee
          oe_runmake ${BUILD_STRING} PLAT=${SOC_ATF} BUILD_BASE=build-optee SPD=opteed bl31
      fi
      unset CROSS_COMPILE
  }
  ```
* `tmp/work/imx8qmmek-poky-linux/imx-atf/2.0+gitAUTOINC+1cb68fa0a0-r0/temp/run.do_compile`
  ```
  do_compile() {
      export CROSS_COMPILE="aarch64-poky-linux-"
      cd /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/imx-atf/2.0+gitAUTOINC+1cb68fa0a0-r0/git
      # Clear LDFLAGS to avoid the option -Wl recognize issue
      unset LDFLAGS
  
      echo "-> Build imx8qm bl31.bin"
      # Set BUIL_STRING with the revision info
      BUILD_STRING=""
      if [ -e /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/imx-atf/2.0+gitAUTOINC+1cb68fa0a0-r0/git/.revision ]; then
          cur_rev=`cat /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/imx-atf/2.0+gitAUTOINC+1cb68fa0a0-r0/git/.revision`
          echo " Current revision is ${cur_rev} ."
          BUILD_STRING="BUILD_STRING=${cur_rev}"
      else
          echo " No .revision found! "
      fi
      oe_runmake clean PLAT=imx8qm
      oe_runmake ${BUILD_STRING} PLAT=imx8qm bl31
  
      # Build opteee version
      if [ "true" = "true" ]; then
          oe_runmake clean PLAT=imx8qm BUILD_BASE=build-optee
          oe_runmake ${BUILD_STRING} PLAT=imx8qm BUILD_BASE=build-optee SPD=opteed bl31
      fi
      unset CROSS_COMPILE
  }
  ```

## SC Firmware analysis

* `meta-fsl-bsp-release/imx/meta-bsp/recipes-bsp/imx-sc-firmware/imx-sc-firmware_1.2.bb`
  ```
  [...省略]
  inherit fsl-eula2-unpack2 pkgconfig deploy
  [...省略]
  do_deploy() {
      install -Dm 0644 ${S}/${SC_FIRMWARE_NAME} ${DEPLOYDIR}/${BOOT_TOOLS}/${SC_FIRMWARE_NAME}
      ln -sf ${SC_FIRMWARE_NAME} ${DEPLOYDIR}/${BOOT_TOOLS}/${symlink_name}
  }
  [...省略]
  ```
  * `tmp/work/imx8qmmek-poky-linux/imx-sc-firmware/1.2-r0/deploy-imx-sc-firmware/imx-boot-tools`
    ```
    mx8qm-mek-scfw-tcm.bin  scfw_tcm.bin
    ```
  * `tmp/deploy/images/imx8qmmek/imx-boot-tools`
    ```
    bl31-imx8qm.bin        m40_tcm.bin     m41_tcm.bin   mkimage_imx8              mx8qm-mek-scfw-tcm.bin  soc.mak  u-boot-imx8qmmek.bin-sd
    bl31-imx8qm.bin-optee  m4_1_image.bin  m4_image.bin  mx8qm-ahab-container.img  scfw_tcm.bin            tee.bin  u-boot-spl.bin-imx8qmmek-sd
    ```
* `meta-fsl-bsp-release/imx/meta-bsp/classes/fsl-eula2-unpack2.bbclass`
  ```
  [...省略]
  IMX_PACKAGE_VERSION = "${PV}"
  
  SRC_URI = "${FSL_MIRROR}${IMX_PACKAGE_NAME}.bin;fsl-eula=true"
  
  S = "${WORKDIR}/${IMX_PACKAGE_NAME}"
  [...省略]
  ```
  * `meta-freescale/conf/layer.conf`
    ```
    IMX_MIRROR ?= "https://www.nxp.com/lgfiles/NMG/MAD/YOCTO/"
    [...省略]
    FSL_MIRROR ?= "${IMX_MIRROR}"
    ```
* `tmp/work/imx8qmmek-poky-linux/imx-sc-firmware/1.2-r0/temp/log.fetch`
  ```
  [...省略]
  DEBUG: Executing python function clean_recipe_sysroot
  DEBUG: Python function clean_recipe_sysroot finished
  DEBUG: Executing python function extend_recipe_sysroot
  NOTE: Direct dependencies are []
  NOTE: Installed into sysroot: []
  NOTE: Skipping as already exists in sysroot: []
  DEBUG: Python function extend_recipe_sysroot finished
  DEBUG: Executing python function do_fetch
  DEBUG: Executing python function base_do_fetch
  DEBUG: Trying PREMIRRORS
  DEBUG: For url ['https', 'www.nxp.com', '/lgfiles/NMG/MAD/YOCTO/imx-sc-firmware-1.2.bin', '', '', OrderedDict([('fsl-eula', 'true')])] comparing ['bzr', '.*', '/.*', '', '', OrderedDict()] to ['http', 'downloads.yoctoproject.org', '/mirror/sources/', '', '', OrderedDict()]
  DEBUG: For url ['https', 'www.nxp.com', '/lgfiles/NMG/MAD/YOCTO/imx-sc-firmware-1.2.bin', '', '', OrderedDict([('fsl-eula', 'true')])] comparing ['cvs', '.*', '/.*', '', '', OrderedDict()] to ['http', 'downloads.yoctoproject.org', '/mirror/sources/', '', '', OrderedDict()]
  DEBUG: For url ['https', 'www.nxp.com', '/lgfiles/NMG/MAD/YOCTO/imx-sc-firmware-1.2.bin', '', '', OrderedDict([('fsl-eula', 'true')])] comparing ['git', '.*', '/.*', '', '', OrderedDict()] to ['http', 'downloads.yoctoproject.org', '/mirror/sources/', '', '', OrderedDict()]
  DEBUG: For url ['https', 'www.nxp.com', '/lgfiles/NMG/MAD/YOCTO/imx-sc-firmware-1.2.bin', '', '', OrderedDict([('fsl-eula', 'true')])] comparing ['gitsm', '.*', '/.*', '', '', OrderedDict()] to ['http', 'downloads.yoctoproject.org', '/mirror/sources/', '', '', OrderedDict()]
  DEBUG: For url ['https', 'www.nxp.com', '/lgfiles/NMG/MAD/YOCTO/imx-sc-firmware-1.2.bin', '', '', OrderedDict([('fsl-eula', 'true')])] comparing ['hg', '.*', '/.*', '', '', OrderedDict()] to ['http', 'downloads.yoctoproject.org', '/mirror/sources/', '', '', OrderedDict()]
  DEBUG: For url ['https', 'www.nxp.com', '/lgfiles/NMG/MAD/YOCTO/imx-sc-firmware-1.2.bin', '', '', OrderedDict([('fsl-eula', 'true')])] comparing ['osc', '.*', '/.*', '', '', OrderedDict()] to ['http', 'downloads.yoctoproject.org', '/mirror/sources/', '', '', OrderedDict()]
  DEBUG: For url ['https', 'www.nxp.com', '/lgfiles/NMG/MAD/YOCTO/imx-sc-firmware-1.2.bin', '', '', OrderedDict([('fsl-eula', 'true')])] comparing ['p4', '.*', '/.*', '', '', OrderedDict()] to ['http', 'downloads.yoctoproject.org', '/mirror/sources/', '', '', OrderedDict()]
  DEBUG: For url ['https', 'www.nxp.com', '/lgfiles/NMG/MAD/YOCTO/imx-sc-firmware-1.2.bin', '', '', OrderedDict([('fsl-eula', 'true')])] comparing ['svn', '.*', '/.*', '', '', OrderedDict()] to ['http', 'downloads.yoctoproject.org', '/mirror/sources/', '', '', OrderedDict()]
  DEBUG: Trying Upstream
  DEBUG: Fetching https://www.nxp.com/lgfiles/NMG/MAD/YOCTO/imx-sc-firmware-1.2.bin;fsl-eula=true using command '/usr/bin/env wget -t 2 -T 30 --passive-ftp --no-check-certificate -P /home/zengjf/imx8-yocto-ga/downloads/ 'https://www.nxp.com/lgfiles/NMG/MAD/YOCTO/imx-sc-firmware-1.2.bin''
  DEBUG: Fetcher accessed the network with the command /usr/bin/env wget -t 2 -T 30 --passive-ftp --no-check-certificate -P /home/zengjf/imx8-yocto-ga/downloads/ 'https://www.nxp.com/lgfiles/NMG/MAD/YOCTO/imx-sc-firmware-1.2.bin'
  DEBUG: Running export PSEUDO_DISABLED=1; export PATH="/home/zengjf/imx8-yocto-ga/sources/poky/scripts:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/imx-sc-firmware/1.2-r0/recipe-sysroot-native/usr/bin/aarch64-poky-linux:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/imx-sc-firmware/1.2-r0/recipe-sysroot/usr/bin/crossscripts:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/imx-sc-firmware/1.2-r0/recipe-sysroot-native/usr/sbin:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/imx-sc-firmware/1.2-r0/recipe-sysroot-native/usr/bin:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/imx-sc-firmware/1.2-r0/recipe-sysroot-native/sbin:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/imx-sc-firmware/1.2-r0/recipe-sysroot-native/bin:/home/zengjf/imx8-yocto-ga/sources/poky/bitbake/bin:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/hosttools"; export HOME="/home/zengjf"; /usr/bin/env wget -t 2 -T 30 --passive-ftp --no-check-certificate -P /home/zengjf/imx8-yocto-ga/downloads/ 'https://www.nxp.com/lgfiles/NMG/MAD/YOCTO/imx-sc-firmware-1.2.bin' --progress=dot -v
  --2019-04-20 00:03:54--  https://www.nxp.com/lgfiles/NMG/MAD/YOCTO/imx-sc-firmware-1.2.bin
  Resolving www.nxp.com (www.nxp.com)... 23.209.3.145
  Connecting to www.nxp.com (www.nxp.com)|23.209.3.145|:443... connected.
  HTTP request sent, awaiting response... 200 OK
  Length: 445882 (435K) [application/octet-stream]
  Saving to: ‘/home/zengjf/imx8-yocto-ga/downloads/imx-sc-firmware-1.2.bin’
  
  
  2019-04-20 00:04:21 (22.4 KB/s) - ‘/home/zengjf/imx8-yocto-ga/downloads/imx-sc-firmware-1.2.bin’ saved [445882/445882]
  [...省略]
  ```
* `tmp/work/imx8qmmek-poky-linux/imx-sc-firmware/1.2-r0/temp/log.do_unpack`
  ```
  DEBUG: Executing python function do_unpack
  NOTE: Freescale EULA has been accepted for 'imx-sc-firmware'
  DEBUG: Executing python function fsl_bin_do_unpack
  NOTE: Handling file 'imx-sc-firmware-1.2.bin' as a Freescale's EULA binary.
  DEBUG: Running export PSEUDO_DISABLED=1; export PATH="/home/zengjf/imx8-yocto-ga/sources/poky/scripts:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/imx-sc-firmware/1.2-r0/recipe-sysroot-native/usr/bin/aarch64-poky-linux:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/imx-sc-firmware/1.2-r0/recipe-sysroot/usr/bin/crossscripts:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/imx-sc-firmware/1.2-r0/recipe-sysroot-native/usr/sbin:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/imx-sc-firmware/1.2-r0/recipe-sysroot-native/usr/bin:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/imx-sc-firmware/1.2-r0/recipe-sysroot-native/sbin:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/imx-sc-firmware/1.2-r0/recipe-sysroot-native/bin:/home/zengjf/imx8-yocto-ga/sources/poky/bitbake/bin:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/hosttools"; export HOME="/home/zengjf"; sh /home/zengjf/imx8-yocto-ga/downloads/imx-sc-firmware-1.2.bin --auto-accept --force
  DEBUG: Python function fsl_bin_do_unpack finished
  DEBUG: Executing python function base_do_unpack
  NOTE: Unpacking /home/zengjf/imx8-yocto-ga/downloads/imx-sc-firmware-1.2.bin to /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/imx-sc-firmware/1.2-r0/
  DEBUG: Python function base_do_unpack finished
  DEBUG: Python function do_unpack finished
  DEBUG: Executing python function do_qa_unpack
  DEBUG: Python function do_qa_unpack finished
  ```

## M4 Firmware analysis

* `meta-fsl-bsp-release/imx/meta-bsp/recipes-bsp/imx-mkimage/imx-boot_0.2.bb`
  ```
  elif [ "${SOC_TARGET}" = "iMX8QM" ]; then
      echo 8QM boot binary build
      cp ${DEPLOY_DIR_IMAGE}/${BOOT_TOOLS}/${SC_FIRMWARE_NAME} ${S}/${SOC_DIR}/scfw_tcm.bin
      cp ${DEPLOY_DIR_IMAGE}/${BOOT_TOOLS}/${ATF_MACHINE_NAME} ${S}/${SOC_DIR}/bl31.bin
      cp ${DEPLOY_DIR_IMAGE}/${UBOOT_NAME}                     ${S}/${SOC_DIR}/u-boot.bin
      if ${DEPLOY_OPTEE}; then
          cp ${DEPLOY_DIR_IMAGE}/u-boot-spl.bin-${MACHINE}-${UBOOT_CONFIG} ${S}/${SOC_DIR}/u-boot-spl.bin
      fi
  
      cp ${DEPLOY_DIR_IMAGE}/imx8qm_m4_0_TCM_rpmsg_lite_pingpong_rtos_linux_remote_m40.bin ${S}/${SOC_DIR}/m40_tcm.bin
      cp ${DEPLOY_DIR_IMAGE}/imx8qm_m4_1_TCM_rpmsg_lite_pingpong_rtos_linux_remote_m41.bin ${S}/${SOC_DIR}/m41_tcm.bin
      cp ${DEPLOY_DIR_IMAGE}/imx8qm_m4_0_TCM_rpmsg_lite_pingpong_rtos_linux_remote_m40.bin ${S}/${SOC_DIR}/m4_image.bin
      cp ${DEPLOY_DIR_IMAGE}/imx8qm_m4_1_TCM_rpmsg_lite_pingpong_rtos_linux_remote_m41.bin ${S}/${SOC_DIR}/m4_1_image.bin
  
      cp ${DEPLOY_DIR_IMAGE}/mx8qm-ahab-container.img ${S}/${SOC_DIR}/
  ```
* `ls tmp/deploy/images/imx8qmmek/imx8qm_m4*`
  ```
  tmp/deploy/images/imx8qmmek/imx8qm_m4_0_TCM_hello_world_m40.bin                            tmp/deploy/images/imx8qmmek/imx8qm_m4_1_TCM_power_mode_switch_m41.bin
  tmp/deploy/images/imx8qmmek/imx8qm_m4_0_TCM_rpmsg_lite_pingpong_rtos_linux_remote_m40.bin  tmp/deploy/images/imx8qmmek/imx8qm_m4_1_TCM_rpmsg_lite_pingpong_rtos_linux_remote_m41.bin
  tmp/deploy/images/imx8qmmek/imx8qm_m4_0_TCM_rpmsg_lite_str_echo_rtos_m40.bin               tmp/deploy/images/imx8qmmek/imx8qm_m4_1_TCM_rpmsg_lite_str_echo_rtos_m41.bin
  tmp/deploy/images/imx8qmmek/imx8qm_m4_1_TCM_hello_world_m41.bin
  ```
* `find * -iname imx8qm_m4_0_TCM_hello_world_m40.bin`
  ```
  aarch64-mx8-poky-linux/imx-m4-demos/2.5.2-r0/package/imx8qm_m4_0_TCM_hello_world_m40.bin
  aarch64-mx8-poky-linux/imx-m4-demos/2.5.2-r0/packages-split/imx-m4-demos/imx8qm_m4_0_TCM_hello_world_m40.bin
  aarch64-mx8-poky-linux/imx-m4-demos/2.5.2-r0/imx8qm-m4-demo-2.5.2/imx8qm_m4_0_TCM_hello_world_m40.bin
  aarch64-mx8-poky-linux/imx-m4-demos/2.5.2-r0/deploy-imx-m4-demos/imx8qm_m4_0_TCM_hello_world_m40.bin
  aarch64-mx8-poky-linux/imx-m4-demos/2.5.2-r0/image/imx8qm_m4_0_TCM_hello_world_m40.bin
  ```
* `find * -iname imx-m4-demos*`
  ```
  meta-fsl-bsp-release/imx/meta-sdk/recipes-fsl/m4-demos/imx-m4-demos_2.5.2.bb
  meta-fsl-bsp-release/imx/meta-sdk/recipes-fsl/m4-demos/imx-m4-demos_2.3.0.bb
  meta-fsl-bsp-release/imx/meta-sdk/recipes-fsl/m4-demos/imx-m4-demos_2.5.0.bb
  meta-fsl-bsp-release/imx/meta-sdk/recipes-fsl/m4-demos/imx-m4-demos-2.inc
  meta-fsl-bsp-release/imx/meta-sdk/recipes-fsl/m4-demos/imx-m4-demos_2.5.1.bb
  meta-fsl-bsp-release/imx/meta-sdk/recipes-fsl/m4-demos/imx-m4-demos_1.0.1.bb
  ```
* `meta-fsl-bsp-release/imx/meta-sdk/recipes-fsl/m4-demos/imx-m4-demos_2.5.2.bb`
  * `meta-fsl-bsp-release/imx/meta-sdk/recipes-fsl/m4-demos/imx-m4-demos-2.inc`
    * `IMX_PACKAGE_NAME = "${SOC}-m4-demo-${PV}"`
    * `meta-fsl-bsp-release/imx/meta-bsp/classes/fsl-eula2-unpack2.bbclass`
      ```
      [...省略]
      IMX_PACKAGE_VERSION = "${PV}"
      
      SRC_URI = "${FSL_MIRROR}${IMX_PACKAGE_NAME}.bin;fsl-eula=true"
      
      S = "${WORKDIR}/${IMX_PACKAGE_NAME}"
      [...省略]
      ```
      * `meta-freescale/conf/layer.conf`
        ```
        IMX_MIRROR ?= "https://www.nxp.com/lgfiles/NMG/MAD/YOCTO/"
        [...省略]
        FSL_MIRROR ?= "${IMX_MIRROR}"
        ```
* `tmp/work/aarch64-mx8-poky-linux/imx-m4-demos/2.5.2-r0/temp/log.do_fetch`
  ```
  DEBUG: Executing python function clean_recipe_sysroot
  DEBUG: Python function clean_recipe_sysroot finished
  DEBUG: Executing python function extend_recipe_sysroot
  NOTE: Direct dependencies are []
  NOTE: Installed into sysroot: []
  NOTE: Skipping as already exists in sysroot: []
  DEBUG: Python function extend_recipe_sysroot finished
  DEBUG: Executing python function do_fetch
  DEBUG: Executing python function base_do_fetch
  DEBUG: Trying PREMIRRORS
  DEBUG: For url ['https', 'www.nxp.com', '/lgfiles/NMG/MAD/YOCTO/imx8qm-m4-demo-2.5.2.bin', '', '', OrderedDict([('fsl-eula', 'true'), ('name', 'imx8qm')])] comparing ['bzr', '.*', '/.*', '', '', OrderedDict()] to ['http', 'downloads.yoctoproject.org', '/mirror/sources/', '', '', OrderedDict()]
  DEBUG: For url ['https', 'www.nxp.com', '/lgfiles/NMG/MAD/YOCTO/imx8qm-m4-demo-2.5.2.bin', '', '', OrderedDict([('fsl-eula', 'true'), ('name', 'imx8qm')])] comparing ['cvs', '.*', '/.*', '', '', OrderedDict()] to ['http', 'downloads.yoctoproject.org', '/mirror/sources/', '', '', OrderedDict()]
  DEBUG: For url ['https', 'www.nxp.com', '/lgfiles/NMG/MAD/YOCTO/imx8qm-m4-demo-2.5.2.bin', '', '', OrderedDict([('fsl-eula', 'true'), ('name', 'imx8qm')])] comparing ['git', '.*', '/.*', '', '', OrderedDict()] to ['http', 'downloads.yoctoproject.org', '/mirror/sources/', '', '', OrderedDict()]
  DEBUG: For url ['https', 'www.nxp.com', '/lgfiles/NMG/MAD/YOCTO/imx8qm-m4-demo-2.5.2.bin', '', '', OrderedDict([('fsl-eula', 'true'), ('name', 'imx8qm')])] comparing ['gitsm', '.*', '/.*', '', '', OrderedDict()] to ['http', 'downloads.yoctoproject.org', '/mirror/sources/', '', '', OrderedDict()]
  DEBUG: For url ['https', 'www.nxp.com', '/lgfiles/NMG/MAD/YOCTO/imx8qm-m4-demo-2.5.2.bin', '', '', OrderedDict([('fsl-eula', 'true'), ('name', 'imx8qm')])] comparing ['hg', '.*', '/.*', '', '', OrderedDict()] to ['http', 'downloads.yoctoproject.org', '/mirror/sources/', '', '', OrderedDict()]
  DEBUG: For url ['https', 'www.nxp.com', '/lgfiles/NMG/MAD/YOCTO/imx8qm-m4-demo-2.5.2.bin', '', '', OrderedDict([('fsl-eula', 'true'), ('name', 'imx8qm')])] comparing ['osc', '.*', '/.*', '', '', OrderedDict()] to ['http', 'downloads.yoctoproject.org', '/mirror/sources/', '', '', OrderedDict()]
  DEBUG: For url ['https', 'www.nxp.com', '/lgfiles/NMG/MAD/YOCTO/imx8qm-m4-demo-2.5.2.bin', '', '', OrderedDict([('fsl-eula', 'true'), ('name', 'imx8qm')])] comparing ['p4', '.*', '/.*', '', '', OrderedDict()] to ['http', 'downloads.yoctoproject.org', '/mirror/sources/', '', '', OrderedDict()]
  DEBUG: For url ['https', 'www.nxp.com', '/lgfiles/NMG/MAD/YOCTO/imx8qm-m4-demo-2.5.2.bin', '', '', OrderedDict([('fsl-eula', 'true'), ('name', 'imx8qm')])] comparing ['svn', '.*', '/.*', '', '', OrderedDict()] to ['http', 'downloads.yoctoproject.org', '/mirror/sources/', '', '', OrderedDict()]
  DEBUG: Trying Upstream
  DEBUG: Fetching https://www.nxp.com/lgfiles/NMG/MAD/YOCTO/imx8qm-m4-demo-2.5.2.bin;fsl-eula=true;name=imx8qm using command '/usr/bin/env wget -t 2 -T 30 --passive-ftp --no-check-certificate -P /home/zengjf/imx8-yocto-ga/downloads/ 'https://www.nxp.com/lgfiles/NMG/MAD/YOCTO/imx8qm-m4-demo-2.5.2.bin''
  DEBUG: Fetcher accessed the network with the command /usr/bin/env wget -t 2 -T 30 --passive-ftp --no-check-certificate -P /home/zengjf/imx8-yocto-ga/downloads/ 'https://www.nxp.com/lgfiles/NMG/MAD/YOCTO/imx8qm-m4-demo-2.5.2.bin'
  DEBUG: Running export PSEUDO_DISABLED=1; export PATH="/home/zengjf/imx8-yocto-ga/sources/poky/scripts:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/aarch64-mx8-poky-linux/imx-m4-demos/2.5.2-r0/recipe-sysroot-native/usr/bin/aarch64-poky-linux:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/aarch64-mx8-poky-linux/imx-m4-demos/2.5.2-r0/recipe-sysroot/usr/bin/crossscripts:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/aarch64-mx8-poky-linux/imx-m4-demos/2.5.2-r0/recipe-sysroot-native/usr/sbin:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/aarch64-mx8-poky-linux/imx-m4-demos/2.5.2-r0/recipe-sysroot-native/usr/bin:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/aarch64-mx8-poky-linux/imx-m4-demos/2.5.2-r0/recipe-sysroot-native/sbin:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/aarch64-mx8-poky-linux/imx-m4-demos/2.5.2-r0/recipe-sysroot-native/bin:/home/zengjf/imx8-yocto-ga/sources/poky/bitbake/bin:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/hosttools"; export HOME="/home/zengjf"; /usr/bin/env wget -t 2 -T 30 --passive-ftp --no-check-certificate -P /home/zengjf/imx8-yocto-ga/downloads/ 'https://www.nxp.com/lgfiles/NMG/MAD/YOCTO/imx8qm-m4-demo-2.5.2.bin' --progress=dot -v
  --2019-04-20 00:49:08--  https://www.nxp.com/lgfiles/NMG/MAD/YOCTO/imx8qm-m4-demo-2.5.2.bin
  Resolving www.nxp.com (www.nxp.com)... 2.21.26.24
  Connecting to www.nxp.com (www.nxp.com)|2.21.26.24|:443... connected.
  HTTP request sent, awaiting response... 200 OK
  Length: 96711 (94K) [application/octet-stream]
  Saving to: ‘/home/zengjf/imx8-yocto-ga/downloads/imx8qm-m4-demo-2.5.2.bin’
  
  
  2019-04-20 00:49:14 (92.7 KB/s) - ‘/home/zengjf/imx8-yocto-ga/downloads/imx8qm-m4-demo-2.5.2.bin’ saved [96711/96711]
  
  DEBUG: Python function base_do_fetch finished
  DEBUG: Python function do_fetch finished
  ```
* `tmp/work/aarch64-mx8-poky-linux/imx-m4-demos/2.5.2-r0/temp/log.do_unpack`
  ```
  DEBUG: Executing python function do_unpack
  NOTE: Freescale EULA has been accepted for 'imx-m4-demos'
  DEBUG: Executing python function fsl_bin_do_unpack
  NOTE: Handling file 'imx8qm-m4-demo-2.5.2.bin' as a Freescale's EULA binary.
  DEBUG: Running export PSEUDO_DISABLED=1; export PATH="/home/zengjf/imx8-yocto-ga/sources/poky/scripts:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/aarch64-mx8-poky-linux/imx-m4-demos/2.5.2-r0/recipe-sysroot-native/usr/bin/aarch64-poky-linux:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/aarch64-mx8-poky-linux/imx-m4-demos/2.5.2-r0/recipe-sysroot/usr/bin/crossscripts:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/aarch64-mx8-poky-linux/imx-m4-demos/2.5.2-r0/recipe-sysroot-native/usr/sbin:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/aarch64-mx8-poky-linux/imx-m4-demos/2.5.2-r0/recipe-sysroot-native/usr/bin:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/aarch64-mx8-poky-linux/imx-m4-demos/2.5.2-r0/recipe-sysroot-native/sbin:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/aarch64-mx8-poky-linux/imx-m4-demos/2.5.2-r0/recipe-sysroot-native/bin:/home/zengjf/imx8-yocto-ga/sources/poky/bitbake/bin:/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/hosttools"; export HOME="/home/zengjf"; sh /home/zengjf/imx8-yocto-ga/downloads/imx8qm-m4-demo-2.5.2.bin --auto-accept --force
  DEBUG: Python function fsl_bin_do_unpack finished
  DEBUG: Executing python function base_do_unpack
  NOTE: Unpacking /home/zengjf/imx8-yocto-ga/downloads/imx8qm-m4-demo-2.5.2.bin to /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/aarch64-mx8-poky-linux/imx-m4-demos/2.5.2-r0/
  DEBUG: Python function base_do_unpack finished
  DEBUG: Python function do_unpack finished
  DEBUG: Executing python function do_qa_unpack
  DEBUG: Python function do_qa_unpack finished
  ```