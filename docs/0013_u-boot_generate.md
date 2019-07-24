# u-boot generate

## u-boot代码分析

* `meta-fsl-bsp-release/imx/meta-bsp/classes/image_types_fsl.bbclass`
  ```
  generate_imx_sdcard () {
          [...省略]
  
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
  
          [...省略]
  }
  ```
* 运行时`tmp/work/imx8qmmek-poky-linux/fsl-image-validation-imx/1.0-r0/temp/run.do_image_sdcard`
  ```
  generate_imx_sdcard() {
          [...省略]
  
          # Burn bootloader
          case "imx-boot" in
                  imx-bootlets)
                  bberror "The imx-bootlets is not supported for i.MX based machines"
                  exit 1
                  ;;
                  imx-boot)
                  dd if=/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/deploy/images/imx8qmmek/imx-boot-imx8qmmek-sd.bin of=/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-validation-imx/1.0-r0/deploy-fsl-image-validation-imx-image-complete/fsl-image-validation-imx-imx8qmmek-20190726063617.rootfs.sdcard conv=notrunc seek=32 bs=1K
                  ;;
                  u-boot)
                  if [ -n "${SPL_BINARY}" ]; then
                      if [ -n "${SPL_SEEK}" ]; then
                          dd if=/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/deploy/images/imx8qmmek/${SPL_BINARY} of=/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-validation-imx/1.0-r0/deploy-fsl-image-validation-imx-image-complete/fsl-image-validation-imx-imx8qmmek-20190726063617.rootfs.sdcard conv=notrunc seek=${SPL_SEEK} bs=1K
                          dd if=/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/deploy/images/imx8qmmek/u-boot-imx8qmmek.bin of=/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-validation-imx/1.0-r0/deploy-fsl-image-validation-imx-image-complete/fsl-image-validation-imx-imx8qmmek-20190726063617.rootfs.sdcard conv=notrunc seek=${UBOOT_SEEK} bs=1K
                      else
                          dd if=/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/deploy/images/imx8qmmek/${SPL_BINARY} of=/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-validation-imx/1.0-r0/deploy-fsl-image-validation-imx-image-complete/fsl-image-validation-imx-imx8qmmek-20190726063617.rootfs.sdcard conv=notrunc seek=2 bs=512
                          dd if=/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/deploy/images/imx8qmmek/u-boot-imx8qmmek.bin of=/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-validation-imx/1.0-r0/deploy-fsl-image-validation-imx-image-complete/fsl-image-validation-imx-imx8qmmek-20190726063617.rootfs.sdcard conv=notrunc seek=69 bs=1K
                      fi
                  else
                      dd if=/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/deploy/images/imx8qmmek/u-boot-imx8qmmek.bin of=/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-validation-imx/1.0-r0/deploy-fsl-image-validation-imx-image-complete/fsl-image-validation-imx-imx8qmmek-20190726063617.rootfs.sdcard conv=notrunc seek=2 bs=512
                  fi
                  ;;
                  barebox)
                  dd if=/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/deploy/images/imx8qmmek/barebox-imx8qmmek.bin of=/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-validation-imx/1.0-r0/deploy-fsl-image-validation-imx-image-complete/fsl-image-validation-imx-imx8qmmek-20190726063617.rootfs.sdcard conv=notrunc seek=1 skip=1 bs=512
                  dd if=/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/deploy/images/imx8qmmek/bareboxenv-imx8qmmek.bin of=/home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-validation-imx/1.0-r0/deploy-fsl-image-validation-imx-image-complete/fsl-image-validation-imx-imx8qmmek-20190726063617.rootfs.sdcard conv=notrunc seek=1 bs=512k
                  ;;
                  "")
                  ;;
                  *)
                  bberror "Unknown IMAGE_BOOTLOADER value"
                  exit 1
                  ;;
          esac
  
          [...省略]
  }
  ```
* `meta-fsl-bsp-release/imx/meta-bsp/conf/machine/imx8qmmek.conf`
  ```
  [...省略]
  # BOOT_SPACE = "65536"
  BOOT_SPACE = "131070"
  IMAGE_BOOTLOADER = "imx-boot"
  IMX_BOOT_SEEK = "32"
  [...省略]
  ```
* `bitbake fsl-image-qt5-validation-imx -g`
* `cat recipe-depends.dot | grep u-boot | grep imx`
  ```
  "fsl-image-qt5-validation-imx" -> "u-boot-imx"
  "imx-boot" -> "u-boot-imx"
  "u-boot-imx" [label="u-boot-imx\n:2018.03-r0\n/home/zengjf/imx8-yocto-ga/sources/meta-fsl-bsp-release/imx/meta-bsp/recipes-bsp/u-boot/u-boot-imx_2018.03.bb"]
  [...省略]
  ```
* `meta-fsl-bsp-release/imx/meta-bsp/recipes-bsp/u-boot/u-boot-imx_2018.03.bb`
  ```
  # Copyright (C) 2013-2016 Freescale Semiconductor
  # Copyright 2017-2018 NXP
  
  DESCRIPTION = "i.MX U-Boot suppporting i.MX reference boards."
  require recipes-bsp/u-boot/u-boot.inc
  inherit pythonnative
  
  [...省略]
  ```
* `poky/meta/recipes-bsp/u-boot/u-boot.inc`
  ```
  [...省略]
  do_deploy () {
      if [ -n "${UBOOT_CONFIG}" ]
      then
          for config in ${UBOOT_MACHINE}; do
              i=$(expr $i + 1);
              for type in ${UBOOT_CONFIG}; do
                  j=$(expr $j + 1);
                  if [ $j -eq $i ]
                  then
                      install -d ${DEPLOYDIR}
                      install -m 644 ${B}/${config}/u-boot-${type}.${UBOOT_SUFFIX} ${DEPLOYDIR}/u-boot-${type}-${PV}-${PR}.${UBOOT_SUFFIX}
                      cd ${DEPLOYDIR}
                      ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_SUFFIX} ${UBOOT_SYMLINK}-${type}
                      ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_SUFFIX} ${UBOOT_SYMLINK}
                      ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_SUFFIX} ${UBOOT_BINARY}-${type}
                      ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_SUFFIX} ${UBOOT_BINARY}
                  fi
              done
              unset  j
          done
          unset  i
      else
          install -d ${DEPLOYDIR}
          install -m 644 ${B}/${UBOOT_BINARY} ${DEPLOYDIR}/${UBOOT_IMAGE}
          cd ${DEPLOYDIR}
          rm -f ${UBOOT_BINARY} ${UBOOT_SYMLINK}
          ln -sf ${UBOOT_IMAGE} ${UBOOT_SYMLINK}
          ln -sf ${UBOOT_IMAGE} ${UBOOT_BINARY}
     fi
  
      if [ -n "${UBOOT_ELF}" ]
      then
          if [ -n "${UBOOT_CONFIG}" ]
          then
              for config in ${UBOOT_MACHINE}; do
                  i=$(expr $i + 1);
                  for type in ${UBOOT_CONFIG}; do
                      j=$(expr $j + 1);
                      if [ $j -eq $i ]
                      then
                          install -m 644 ${B}/${config}/${UBOOT_ELF} ${DEPLOYDIR}/u-boot-${type}-${PV}-${PR}.${UBOOT_ELF_SUFFIX}
                          ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_ELF_SUFFIX} ${DEPLOYDIR}/${UBOOT_ELF_BINARY}-${type}
                          ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_ELF_SUFFIX} ${DEPLOYDIR}/${UBOOT_ELF_BINARY}
                          ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_ELF_SUFFIX} ${DEPLOYDIR}/${UBOOT_ELF_SYMLINK}-${type}
                          ln -sf u-boot-${type}-${PV}-${PR}.${UBOOT_ELF_SUFFIX} ${DEPLOYDIR}/${UBOOT_ELF_SYMLINK}
                      fi
                  done
                  unset j
              done
              unset i
          else
              install -m 644 ${B}/${UBOOT_ELF} ${DEPLOYDIR}/${UBOOT_ELF_IMAGE}
              ln -sf ${UBOOT_ELF_IMAGE} ${DEPLOYDIR}/${UBOOT_ELF_BINARY}
              ln -sf ${UBOOT_ELF_IMAGE} ${DEPLOYDIR}/${UBOOT_ELF_SYMLINK}
          fi
      fi
  
  
       if [ -n "${SPL_BINARY}" ]
       then
           if [ -n "${UBOOT_CONFIG}" ]
           then
               for config in ${UBOOT_MACHINE}; do
                   i=$(expr $i + 1);
                   for type in ${UBOOT_CONFIG}; do
                       j=$(expr $j + 1);
                       if [ $j -eq $i ]
                       then
                           install -m 644 ${B}/${config}/${SPL_BINARY} ${DEPLOYDIR}/${SPL_IMAGE}-${type}-${PV}-${PR}
                           rm -f ${DEPLOYDIR}/${SPL_BINARYNAME} ${DEPLOYDIR}/${SPL_SYMLINK}-${type}
                           ln -sf ${SPL_IMAGE}-${type}-${PV}-${PR} ${DEPLOYDIR}/${SPL_BINARYNAME}-${type}
                           ln -sf ${SPL_IMAGE}-${type}-${PV}-${PR} ${DEPLOYDIR}/${SPL_BINARYNAME}
                           ln -sf ${SPL_IMAGE}-${type}-${PV}-${PR} ${DEPLOYDIR}/${SPL_SYMLINK}-${type}
                           ln -sf ${SPL_IMAGE}-${type}-${PV}-${PR} ${DEPLOYDIR}/${SPL_SYMLINK}
                       fi
                   done
                   unset  j
               done
               unset  i
           else
               install -m 644 ${B}/${SPL_BINARY} ${DEPLOYDIR}/${SPL_IMAGE}
               rm -f ${DEPLOYDIR}/${SPL_BINARYNAME} ${DEPLOYDIR}/${SPL_SYMLINK}
               ln -sf ${SPL_IMAGE} ${DEPLOYDIR}/${SPL_BINARYNAME}
               ln -sf ${SPL_IMAGE} ${DEPLOYDIR}/${SPL_SYMLINK}
           fi
       fi
  
  
      if [ -n "${UBOOT_ENV}" ]
      then
          install -m 644 ${WORKDIR}/${UBOOT_ENV_BINARY} ${DEPLOYDIR}/${UBOOT_ENV_IMAGE}
          rm -f ${DEPLOYDIR}/${UBOOT_ENV_BINARY} ${DEPLOYDIR}/${UBOOT_ENV_SYMLINK}
          ln -sf ${UBOOT_ENV_IMAGE} ${DEPLOYDIR}/${UBOOT_ENV_BINARY}
          ln -sf ${UBOOT_ENV_IMAGE} ${DEPLOYDIR}/${UBOOT_ENV_SYMLINK}
      fi
  
      if [ "${UBOOT_EXTLINUX}" = "1" ]
      then
          install -m 644 ${UBOOT_EXTLINUX_CONFIG} ${DEPLOYDIR}/${UBOOT_EXTLINUX_SYMLINK}
          ln -sf ${UBOOT_EXTLINUX_SYMLINK} ${DEPLOYDIR}/${UBOOT_EXTLINUX_CONF_NAME}-${MACHINE}
          ln -sf ${UBOOT_EXTLINUX_SYMLINK} ${DEPLOYDIR}/${UBOOT_EXTLINUX_CONF_NAME}
      fi
  }
  ```
* `tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/temp/run.do_deploy`
  ```
  do_deploy() {
      if [ -n "sd" ]
      then
          for config in  imx8qm_mek_defconfig; do
              i=$(expr $i + 1);
              for type in sd; do
                  j=$(expr $j + 1);
                  if [ $j -eq $i ]
                  then
                      install -d /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx
                      install -m 644 /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/build/${config}/u-boot-${type}.bin /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx/u-boot-${type}-2018.03-r0.bin
                      cd /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx
                      ln -sf u-boot-${type}-2018.03-r0.bin u-boot-imx8qmmek.bin-${type}
                      ln -sf u-boot-${type}-2018.03-r0.bin u-boot-imx8qmmek.bin
                      ln -sf u-boot-${type}-2018.03-r0.bin u-boot.bin-${type}
                      ln -sf u-boot-${type}-2018.03-r0.bin u-boot.bin
                  fi
              done
              unset  j
          done
          unset  i
      else
          install -d /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx
          install -m 644 /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/build/u-boot.bin /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx/u-boot-imx8qmmek-2018.03-r0.bin
          cd /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx
          rm -f u-boot.bin u-boot-imx8qmmek.bin
          ln -sf u-boot-imx8qmmek-2018.03-r0.bin u-boot-imx8qmmek.bin
          ln -sf u-boot-imx8qmmek-2018.03-r0.bin u-boot.bin
     fi
  
      if [ -n "" ]
      then
          if [ -n "sd" ]
          then
              for config in  imx8qm_mek_defconfig; do
                  i=$(expr $i + 1);
                  for type in sd; do
                      j=$(expr $j + 1);
                      if [ $j -eq $i ]
                      then
                          install -m 644 /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/build/${config}/ /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx/u-boot-${type}-2018.03-r0.elf
                          ln -sf u-boot-${type}-2018.03-r0.elf /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx/u-boot.elf-${type}
                          ln -sf u-boot-${type}-2018.03-r0.elf /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx/u-boot.elf
                          ln -sf u-boot-${type}-2018.03-r0.elf /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx/u-boot-imx8qmmek.elf-${type}
                          ln -sf u-boot-${type}-2018.03-r0.elf /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx/u-boot-imx8qmmek.elf
                      fi
                  done
                  unset j
              done
              unset i
          else
              install -m 644 /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/build/ /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx/u-boot-imx8qmmek-2018.03-r0.elf
              ln -sf u-boot-imx8qmmek-2018.03-r0.elf /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx/u-boot.elf
              ln -sf u-boot-imx8qmmek-2018.03-r0.elf /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx/u-boot-imx8qmmek.elf
          fi
      fi
  
  
       if [ -n "" ]
       then
           if [ -n "sd" ]
           then
               for config in  imx8qm_mek_defconfig; do
                   i=$(expr $i + 1);
                   for type in sd; do
                       j=$(expr $j + 1);
                       if [ $j -eq $i ]
                       then
                           install -m 644 /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/build/${config}/ /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx/-imx8qmmek-2018.03-r0-${type}-2018.03-r0
                           rm -f /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx/ /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx/-imx8qmmek-${type}
                           ln -sf -imx8qmmek-2018.03-r0-${type}-2018.03-r0 /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx/-${type}
                           ln -sf -imx8qmmek-2018.03-r0-${type}-2018.03-r0 /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx/
                           ln -sf -imx8qmmek-2018.03-r0-${type}-2018.03-r0 /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx/-imx8qmmek-${type}
                           ln -sf -imx8qmmek-2018.03-r0-${type}-2018.03-r0 /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx/-imx8qmmek
                       fi
                   done
                   unset  j
               done
               unset  i
           else
               install -m 644 /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/build/ /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx/-imx8qmmek-2018.03-r0
               rm -f /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx/ /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx/-imx8qmmek
               ln -sf -imx8qmmek-2018.03-r0 /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx/
               ln -sf -imx8qmmek-2018.03-r0 /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx/-imx8qmmek
           fi
       fi
  
  
      if [ -n "" ]
      then
          install -m 644 /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/.txt /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx/-imx8qmmek-2018.03-r0.txt
          rm -f /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx/.txt /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx/-imx8qmmek.txt
          ln -sf -imx8qmmek-2018.03-r0.txt /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx/.txt
          ln -sf -imx8qmmek-2018.03-r0.txt /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx/-imx8qmmek.txt
      fi
  
      if [ "${UBOOT_EXTLINUX}" = "1" ]
      then
          install -m 644 /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/build/extlinux.conf /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx/extlinux.conf-imx8qmmek-r0
          ln -sf extlinux.conf-imx8qmmek-r0 /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx/extlinux.conf-imx8qmmek
          ln -sf extlinux.conf-imx8qmmek-r0 /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx/extlinux.conf
      fi
  }
  ```
* `ls -al tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx`
  ```
  total 644
  drwxr-xr-x  2 zengjf zengjf   4096 Jul 10 14:57 .
  drwxrwxr-x 16 zengjf zengjf   4096 Jul 24 15:49 ..
  lrwxrwxrwx  2 zengjf zengjf     24 Jul 10 14:57 u-boot.bin -> u-boot-sd-2018.03-r0.bin
  lrwxrwxrwx  2 zengjf zengjf     24 Jul 10 14:57 u-boot.bin-sd -> u-boot-sd-2018.03-r0.bin
  lrwxrwxrwx  2 zengjf zengjf     24 Jul 10 14:57 u-boot-imx8qmmek.bin -> u-boot-sd-2018.03-r0.bin
  lrwxrwxrwx  2 zengjf zengjf     24 Jul 10 14:57 u-boot-imx8qmmek.bin-sd -> u-boot-sd-2018.03-r0.bin
  -rw-r--r--  2 zengjf zengjf 649522 Jul 10 14:57 u-boot-sd-2018.03-r0.bin
  ```
* SPL version output: `ls -al tmp/work/imx8qmmek-poky-linux/u-boot-imx/2018.03-r0/deploy-u-boot-imx`
  ```
  total 768
  drwxr-xr-x  2 zengjf zengjf   4096 Jul 16 12:25 .
  drwxrwxr-x 21 zengjf zengjf   4096 Jul 22 17:06 ..
  lrwxrwxrwx  2 zengjf zengjf     24 Jul 16 12:25 u-boot.bin -> u-boot-sd-2018.03-r0.bin
  lrwxrwxrwx  2 zengjf zengjf     24 Jul 16 12:25 u-boot.bin-sd -> u-boot-sd-2018.03-r0.bin
  lrwxrwxrwx  2 zengjf zengjf     24 Jul 16 12:25 u-boot-imx8qmmek.bin -> u-boot-sd-2018.03-r0.bin
  lrwxrwxrwx  2 zengjf zengjf     24 Jul 16 12:25 u-boot-imx8qmmek.bin-sd -> u-boot-sd-2018.03-r0.bin
  -rw-r--r--  2 zengjf zengjf 692594 Jul 16 12:25 u-boot-sd-2018.03-r0.bin
  lrwxrwxrwx  2 zengjf zengjf     49 Jul 16 12:25 u-boot-spl.bin -> u-boot-spl.bin-imx8qmmek-2018.03-r0-sd-2018.03-r0
  lrwxrwxrwx  2 zengjf zengjf     49 Jul 16 12:25 u-boot-spl.bin-imx8qmmek -> u-boot-spl.bin-imx8qmmek-2018.03-r0-sd-2018.03-r0
  -rw-r--r--  2 zengjf zengjf  78773 Jul 16 12:25 u-boot-spl.bin-imx8qmmek-2018.03-r0-sd-2018.03-r0
  lrwxrwxrwx  2 zengjf zengjf     49 Jul 16 12:25 u-boot-spl.bin-imx8qmmek-sd -> u-boot-spl.bin-imx8qmmek-2018.03-r0-sd-2018.03-r0
  lrwxrwxrwx  2 zengjf zengjf     49 Jul 16 12:25 u-boot-spl.bin-sd -> u-boot-spl.bin-imx8qmmek-2018.03-r0-sd-2018.03-r0
  ```
* 修改`meta-fsl-bsp-release/imx/meta-bsp/classes/image_types_fsl.bbclass`
  ```
  generate_imx_sdcard () {
          [...省略]
          # Burn bootloader
          bbdebug 2 "generate_imx_sdcard IMAGE_BOOTLOADER: ${IMAGE_BOOTLOADER}"
          case "${IMAGE_BOOTLOADER}" in
                  imx-bootlets)
                  [...省略]
                  imx-boot)
                  dd if=${DEPLOY_DIR_IMAGE}/imx-boot-${MACHINE}-${UBOOT_CONFIG}.bin of=${SDCARD} conv=notrunc seek=${IMX_BOOT_SEEK} bs=1K
                  [...省略]
          esac
          [...省略]
  }
  ```
* `bitbake fsl-image-qt5-validation-imx -DD -c do_image_sdcard -f`
  ```
  [...省略]
  DEBUG: Python function extend_recipe_sysroot finished
  DEBUG: Executing shell function do_image_sdcard
  0+0 records in
  0+0 records out
  0 bytes copied, 0.000196454 s, 0.0 kB/s
  Model:  (file)
  Disk /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/deploy-fsl-image-qt5-validation-imx-image-complete/fsl-image-qt5-validation-imx-imx8qmmek-20190726082314.rootfs.sdcard: 1250MB
  Sector size (logical/physical): 512B/512B
  Partition Table: msdos
  Disk Flags:
  
  Number  Start   End     Size    Type     File system  Flags
   1      8389kB  143MB   134MB   primary               lba
   2      143MB   1242MB  1099MB  primary
  
  DEBUG: generate_imx_sdcard IMAGE_BOOTLOADER: imx-boot
  950+0 records in
  950+0 records out
  972800 bytes (973 kB, 950 KiB) copied, 0.576254 s, 1.7 MB/s
  mkfs.fat: warning - lowercase labels might not work properly with DOS or Windows
  mkfs.fat 4.1 (2017-01-24)
  16+0 records in
  16+0 records out
  134217728 bytes (134 MB, 128 MiB) copied, 0.551393 s, 243 MB/s
  7+1 records in
  7+1 records out
  1098907648 bytes (1.1 GB, 1.0 GiB) copied, 2.61798 s, 420 MB/s
  DEBUG: Shell function do_image_sdcard finished
  DEBUG: Executing python function create_symlinks
  NOTE: Creating symlink: /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/deploy-fsl-image-qt5-validation-imx-image-complete/fsl-image-qt5-validation-imx-imx8qmmek.sdcard.bz2 -> fsl-image-qt5-validation-imx-imx8qmmek-20190726082314.rootfs.sdcard.bz2
  DEBUG: Python function create_symlinks finished
  ```
* `DEBUG: generate_imx_sdcard IMAGE_BOOTLOADER: imx-boot`
* `u-boot-sd-2018.03-r0.bin`
* `ls -al tmp/deploy/images/imx8qmmek/u-boot*`
  ```
  lrwxrwxrwx 2 zengjf zengjf     24 Jul 10 14:57 u-boot.bin -> u-boot-sd-2018.03-r0.bin
  lrwxrwxrwx 2 zengjf zengjf     24 Jul 10 14:57 u-boot.bin-sd -> u-boot-sd-2018.03-r0.bin
  lrwxrwxrwx 2 zengjf zengjf     24 Jul 10 14:57 u-boot-imx8qmmek.bin -> u-boot-sd-2018.03-r0.bin
  lrwxrwxrwx 2 zengjf zengjf     24 Jul 10 14:57 u-boot-imx8qmmek.bin-sd -> u-boot-sd-2018.03-r0.bin
  -rw-r--r-- 2 zengjf zengjf 649522 Jul 10 14:57 u-boot-sd-2018.03-r0.bin
  ```

## imx-boot

* `meta-fsl-bsp-release/imx/meta-bsp/recipes-bsp/imx-mkimage/imx-boot_0.2.bb`
  ```
  [...省略]
  UBOOT_NAME = "u-boot-${MACHINE}.bin-${UBOOT_CONFIG}"                              # u-boot-imx8qmmek.bin-sd
  BOOT_CONFIG_MACHINE = "${BOOT_NAME}-${MACHINE}-${UBOOT_CONFIG}.bin"               # imx-boot-imx8qmmek-sd.bin
  [...省略]
  do_compile () {
      if [ "${SOC_TARGET}" = "iMX8M" -o "${SOC_TARGET}" = "iMX8MM" ]; then
          [...省略]
      elif [ "${SOC_TARGET}" = "iMX8QM" ]; then
          echo 8QM boot binary build
          cp ${DEPLOY_DIR_IMAGE}/${BOOT_TOOLS}/${SC_FIRMWARE_NAME} ${S}/${SOC_DIR}/scfw_tcm.bin
          cp ${DEPLOY_DIR_IMAGE}/${BOOT_TOOLS}/${ATF_MACHINE_NAME} ${S}/${SOC_DIR}/bl31.bin
          cp ${DEPLOY_DIR_IMAGE}/${UBOOT_NAME}                     ${S}/${SOC_DIR}/u-boot.bin
  
          cp ${DEPLOY_DIR_IMAGE}/imx8qm_m4_0_TCM_rpmsg_lite_pingpong_rtos_linux_remote_m40.bin ${S}/${SOC_DIR}/m40_tcm.bin
          cp ${DEPLOY_DIR_IMAGE}/imx8qm_m4_1_TCM_rpmsg_lite_pingpong_rtos_linux_remote_m41.bin ${S}/${SOC_DIR}/m41_tcm.bin
          cp ${DEPLOY_DIR_IMAGE}/mx8qm-ahab-container.img ${S}/${SOC_DIR}/
  
      else
          [...省略]
      fi
  
      # Copy TEE binary to SoC target folder to mkimage
      if ${DEPLOY_OPTEE}; then
          cp ${DEPLOY_DIR_IMAGE}/tee.bin             ${S}/${SOC_DIR}/
      fi
  
      # mkimage for i.MX8
      for target in ${IMXBOOT_TARGETS}; do
          echo "building ${SOC_TARGET} - ${target}"
          make SOC=${SOC_TARGET} ${target}                                          # make SOC=iMX8QM flash_b0
          if [ -e "${S}/${SOC_DIR}/flash.bin" ]; then
              cp ${S}/${SOC_DIR}/flash.bin ${S}/${BOOT_CONFIG_MACHINE}-${target}    # imx-boot-imx8qmmek-sd.bin-flash_b0
          fi
      done
  }
  ```
* `make SOC=iMX8QM flash_b0`
* `tmp/work/imx8qmmek-poky-linux/imx-boot/0.2-r0/git/Makefile`
  ```
  
  MKIMG = $(PWD)/mkimage_imx8
  CC = gcc
  CFLAGS ?= -g -O2 -Wall -std=c99 -static
  INCLUDE += $(CURR_DIR)/src
  
  SRCS = src/imx8qm.c  src/imx8qx.c src/imx8qxb0.c src/mkimage_imx8.c
  
  ifneq ($(findstring iMX8M,$(SOC)),)
  SOC_DIR = iMX8M
  endif
  SOC_DIR ?= $(SOC)
  
  vpath $(INCLUDE)
  
  .PHONY:  clean all bin
  
  .DEFAULT:
          @$(MAKE) -s --no-print-directory bin
          @$(MAKE) --no-print-directory -C $(SOC_DIR) -f soc.mak $@
  
  #print out usage as the default target
  all: $(MKIMG) help
  
  clean:
          @rm -f $(MKIMG)
          @rm -f src/build_info.h
          @$(MAKE) --no-print-directory -C iMX8QM -f soc.mak clean
          @$(MAKE) --no-print-directory -C iMX8QX -f soc.mak  clean
          @$(MAKE) --no-print-directory -C iMX8M -f soc.mak  clean
          @$(MAKE) --no-print-directory -C iMX8dv -f soc.mak  clean
  
  $(MKIMG): src/build_info.h $(SRCS)
          @echo "Compiling mkimage_imx8"
          $(CC) $(CFLAGS) $(SRCS) -o $(MKIMG) -I src
  
  bin: $(MKIMG)
  
  src/build_info.h:
          @echo -n '#define MKIMAGE_COMMIT 0x' > src/build_info.h
          @git rev-parse --short=8 HEAD >> src/build_info.h
          @echo '' >> src/build_info.h
  [...省略]
  ```
* `@$(MAKE) --no-print-directory -C $(SOC_DIR) -f soc.mak $@`
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
  flash_b0: $(MKIMG) mx8qm-ahab-container.img scfw_tcm.bin u-boot-atf.bin
          ./$(MKIMG) -soc QM -rev B0 -append mx8qm-ahab-container.img -c -scfw scfw_tcm.bin -ap u-boot-atf.bin a53 0x80000000 -out flash.bin
  [...省略]
  ```
* `tmp/work/imx8qmmek-poky-linux/imx-boot/0.2-r0/git/imx-boot-imx8qmmek-sd.bin-flash_b0`
* `meta-fsl-bsp-release/imx/meta-bsp/recipes-bsp/imx-mkimage/imx-boot_0.2.bb`
  ```
  do_deploy () {
      [...省略]
  
      # copy the generated boot image to deploy path
      for target in ${IMXBOOT_TARGETS}; do                                                # flash_b0
          # Use first "target" as IMAGE_IMXBOOT_TARGET
          if [ "$IMAGE_IMXBOOT_TARGET" = "" ]; then
              IMAGE_IMXBOOT_TARGET="$target"
              echo "Set boot target as $IMAGE_IMXBOOT_TARGET"
          fi
          install -m 0644 ${S}/${BOOT_CONFIG_MACHINE}-${target} ${DEPLOYDIR}              # imx-boot-imx8qmmek-sd.bin-flash_b0
      done
      cd ${DEPLOYDIR}
      ln -sf ${BOOT_CONFIG_MACHINE}-${IMAGE_IMXBOOT_TARGET} ${BOOT_CONFIG_MACHINE}        # ln -sf imx-boot-imx8qmmek-sd.bin-${IMAGE_IMXBOOT_TARGET} imx-boot-imx8qmmek-sd.bin
      cd -
  }
  ```
* `ls -al tmp/work/imx8qmmek-poky-linux/imx-boot/0.2-r0/deploy-imx-boot/*`
  ```
  total 964
  drwxrwxr-x  3 wugn wugn   4096 Jul 24 15:49 .
  drwxrwxr-x 15 wugn wugn   4096 Jul 24 15:49 ..
  lrwxrwxrwx  2 wugn wugn     34 Jul 24 15:49 imx-boot-imx8qmmek-sd.bin -> imx-boot-imx8qmmek-sd.bin-flash_b0
  -rw-r--r--  2 wugn wugn 972800 Jul 24 15:49 imx-boot-imx8qmmek-sd.bin-flash_b0
  drwxr-xr-x  2 wugn wugn   4096 Jul 24 15:49 imx-boot-tools
  
  ```
* `ls -al tmp/deploy/images/imx8qmmek/imx-boot-imx8qmmek-sd.bin`
  ```
  lrwxrwxrwx 2 wugn wugn     34 Jul 24 15:49 imx-boot-imx8qmmek-sd.bin -> imx-boot-imx8qmmek-sd.bin-flash_b0
  -rw-r--r-- 2 wugn wugn 972800 Jul 24 15:49 imx-boot-imx8qmmek-sd.bin-flash_b0
  ```