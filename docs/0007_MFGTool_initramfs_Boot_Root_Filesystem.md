# MFGTool initramfs Boot Root Filesystem

## Common Command

* `MACHINE=imx6dlsabresd DISTRO=fsl-imx-x11 source ./fsl-setup-release.sh -b imx6q-x11`
* `bitbake fsl-image-mfgtool-initramfs`
* `source setup-environment imx6q-x11/`

## 参考文档：

* [IMX6 - booting to a roofts on ramdisk (Boundary Sabre-lite)?](https://community.nxp.com/thread/301459)
* [U-Boot Scripts](http://www.compulab.co.il/utilite-computer/wiki/index.php/U-Boot_Scripts)
* [initramfs切入真实linux文件系统](http://www.iteedu.com/os/linux/mklinuxdiary/ch3initrd/23.php)
* [Low on space in /run](https://serverfault.com/questions/472683/low-on-space-in-run)
* [mount: mounting proc on /proc failed: Device or resource busy](https://www.cnblogs.com/zengjfgit/p/9323709.html)

## U-Boot Boot Analysis

* MfgTool Boot from RAM file system info
  ```
  [...省略]
  Boot from USB for mfgtools
  Use default environment for                              mfgtools
  Run bootcmd_mfg: run mfgtool_args;bootz ${loadaddr} ${initrd_addr} ${fdt_addr};
  Hit any key to stop autoboot:  0
  Kernel image @ 0x12000000 [ 0x000000 - 0x557d98 ]
  ## Loading init Ramdisk from Legacy Image at 12c00000 ...
     Image Name:   fsl-image-mfgtool-initramfs-imx6
     Image Type:   ARM Linux RAMDisk Image (gzip compressed)
     Data Size:    7565864 Bytes = 7.2 MiB
     Load Address: 00000000
     Entry Point:  00000000
     Verifying Checksum ... OK
  ## Flattened Device Tree blob at 18000000
     Booting using the fdt blob at 0x18000000
  [...省略]
  ```
* bootz help
  ```
  => help bootz
  bootz - boot Linux zImage image from memory
  
  Usage:
  bootz [addr [initrd[:size]] [fdt]]
      - boot Linux zImage stored in memory
          The argument 'initrd' is optional and specifies the address
          of the initrd in memory. The optional argument ':size' allows
          specifying the size of RAW initrd.
          When booting a Linux kernel which requires a flat device-tree
          a third argument is required which is the address of the
          device-tree blob. To boot that kernel without an initrd image,
          use a '-' for the second argument. If you do not pass a third
          a bd_info struct will be passed instead
  
  =>
  ```
* Print all environments
  ```
  => print
  baudrate=115200
  board_name=SABRESD
  board_rev=MX6DL
  boot_fdt=try
  bootcmd=run findfdt;mmc dev ${mmcdev};if mmc rescan; then if run loadbootscript; then run bootscript; else if run loadimage; then run mmcboot; else run netboot; fi; fi; else run netboot; fi
  bootcmd_mfg=run mfgtool_args;bootz ${loadaddr} ${initrd_addr} ${fdt_addr};
  bootdelay=3
  bootscript=echo Running bootscript from mmc ...; source
  console=ttymxc0
  dfu_alt_info=spl raw 0x400
  dfu_alt_info_img=u-boot raw 0x10000
  dfu_alt_info_spl=spl raw 0x400
  dfuspi=dfu 0 sf 0:0:10000000:0
  emmcdev=2
  epdc_waveform=epdc_splash.bin
  ethact=FEC
  ethprime=FEC
  fdt_addr=0x18000000
  fdt_file=undefined
  fdt_high=0xffffffff
  findfdt=if test $fdt_file = undefined; then if test $board_name = SABREAUTO && test $board_rev = MX6QP; then setenv fdt_file imx6qp-sabreauto.dtb; fi; if test $board_name = SABREAUTO && test $board_rev = MX6Q; then setenv fdt_file imx6q-sabreauto.dtb; fi; if test $board_name = SABREAUTO && test $board_rev = MX6DL; then setenv fdt_file imx6dl-sabreauto.dtb; fi; if test $board_name = SABRESD && test $board_rev = MX6QP; then setenv fdt_file imx6qp-sabresd.dtb; fi; if test $board_name = SABRESD && test $board_rev = MX6Q; then setenv fdt_file imx6q-sabresd.dtb; fi; if test $board_name = SABRESD && test $board_rev = MX6DL; then setenv fdt_file imx6dl-sabresd.dtb; fi; if test $fdt_file = undefined; then echo WARNING: Could not determine dtb to use; fi; fi;
  image=zImage
  initrd_addr=0x12C00000
  initrd_high=0xffffffff
  ip_dyn=yes
  loadaddr=0x12000000
  loadbootscript=fatload mmc ${mmcdev}:${mmcpart} ${loadaddr} ${script};
  loadfdt=fatload mmc ${mmcdev}:${mmcpart} ${fdt_addr} ${fdt_file}
  loadimage=fatload mmc ${mmcdev}:${mmcpart} ${loadaddr} ${image}
  mfgtool_args=setenv bootargs console=ttymxc0,115200 rdinit=/linuxrc g_mass_storage.stall=0 g_mass_storage.removable=1 g_mass_storage.file=/fat g_mass_storage.ro=1 g_mass_storage.idVendor=0x066F g_mass_storage.idProduct=0x37FF g_mass_storage.iSerialNumber="" enable_wait_mode=off
  mmcargs=setenv bootargs console=${console},${baudrate} ${smp} root=${mmcroot}
  mmcautodetect=yes
  mmcboot=echo Booting from mmc ...; run mmcargs; if test ${boot_fdt} = yes || test ${boot_fdt} = try; then if run loadfdt; then bootz ${loadaddr} - ${fdt_addr}; else if test ${boot_fdt} = try; then bootz; else echo WARN: Cannot load the DT; fi; fi; else bootz; fi;
  mmcdev=2
  mmcpart=1
  mmcroot=/dev/mmcblk3p2 rootwait rw
  netargs=setenv bootargs console=${console},${baudrate} ${smp} root=/dev/nfs ip=dhcp nfsroot=${serverip}:${nfsroot},v3,tcp
  netboot=echo Booting from net ...; run netargs; if test ${ip_dyn} = yes; then setenv get_cmd dhcp; else setenv get_cmd tftp; fi; ${get_cmd} ${image}; if test ${boot_fdt} = yes || test ${boot_fdt} = try; then if ${get_cmd} ${fdt_addr} ${fdt_file}; then bootz ${loadaddr} - ${fdt_addr}; else if test ${boot_fdt} = try; then bootz; else echo WARN: Cannot load the DT; fi; fi; else bootz; fi;
  script=boot.scr
  update_emmc_firmware=if test ${ip_dyn} = yes; then setenv get_cmd dhcp; else setenv get_cmd tftp; fi; if ${get_cmd} ${update_sd_firmware_filename}; then if mmc dev ${emmcdev} 1; then setexpr fw_sz ${filesize} / 0x200; setexpr fw_sz ${fw_sz} + 1; mmc write ${loadaddr} 0x2 ${fw_sz}; fi; fi
  update_sd_firmware=if test ${ip_dyn} = yes; then setenv get_cmd dhcp; else setenv get_cmd tftp; fi; if mmc dev ${mmcdev}; then if ${get_cmd} ${update_sd_firmware_filename}; then setexpr fw_sz ${filesize} / 0x200; setexpr fw_sz ${fw_sz} + 1; mmc write ${loadaddr} 0x2 ${fw_sz}; fi; fi
  
  Environment size: 3588/8188 bytes
  ```
* Boot with initramfs with manual operation in U-Boot
  * default variable:
    * `fdt_addr=0x18000000`
    * `initrd_addr=0x12C00000`
    * `loadaddr=0x12000000`
  * `load mmc 2 $loadaddr zImage`
  * `load mmc 2 $initrd_addr initramfs`
  * `load mmc 2 $fdt_addr imx6dl-sabresd.dtb`
  * `bootz $loadaddr $initrd_addr $fdt_addr`
  * 在运行bootz之前，可选项运行条件：`setenv bootargs console=ttymxc0,115200 rdinit=/linuxrc`

## initramfs linuxrc switch_root to root file system

* `~/fsl-release-bsp/imx6q-x11/tmp/work/imx6dlsabresd-poky-linux-gnueabi/fsl-image-mfgtool-initramfs/1.0-r0/rootfs/linuxrc`
  ```
  #!/bin/sh
  export PATH=/sbin:/bin:/usr/sbin:/usr/bin
  
  mount -t devtmpfs none /dev
  mount -t proc none /proc
  mount -t sysfs none /sys
  mount -t tmpfs tmpfs /run
  
  sleep 1
  
  mount /dev/mmcblk3p2 /mnt
  
  # mount --move /dev     /mnt/dev
  # mount --move /proc    /mnt/proc
  # mount --move /sys     /mnt/sys
  # mount --move /run     /mnt/run
  
  echo "switch_root /mnt /linuxrc"
  exec switch_root /mnt /sbin/init
  ```
* recompile
  * `target=fsl-image-mfgtool-initramfs`
  * `source setup-environment imx6q-x11`
  * `bitbake -f -c image_cpio $target`

## Support dm-crypt

* Work Path: `~/fsl-release-bsp/imx6q-x11/tmp/work/imx6dlsabresd-poky-linux-gnueabi/fsl-image-mfgtool-initramfs/1.0-r0`
* `sources/meta-fsl-arm/recipes-fsl/images/fsl-image-mfgtool-initramfs.bb`
* `sources/meta-fsl-arm/classes/mfgtool-initramfs-image.bbclass`
  ```
  # Generates a Manufacturing Tool Initramfs image
  #
  # This generates the initramfs used for the installation process. The
  # image provides the utilities which are used, in the target, during
  # the process and receive the commands from the MfgTool application.
  #
  # Copyright 2014, 2016 (C) O.S. Systems Software LTDA.
  
  DEPENDS += "u-boot-mfgtool linux-mfgtool"
  
  FEATURE_PACKAGES_mtd = "packagegroup-fsl-mfgtool-mtd"
  FEATURE_PACKAGES_extfs = "packagegroup-fsl-mfgtool-extfs"
  FEATURE_PACKAGES_f2fs = "packagegroup-fsl-mfgtool-f2fs"
  
  IMAGE_FSTYPES = "cpio.gz.u-boot"
  IMAGE_FSTYPES_mxs = "cpio.gz.u-boot"
  IMAGE_ROOTFS_SIZE ?= "8192"
  IMAGE_CLASSES = "image_types_uboot"
  
  # Filesystems enabled by default
  DEFAULT_FS_SUPPORT = " \
      mtd \
      extfs \
  "
  
  IMAGE_INSTALL += "cryptsetup"         # add cryptsetup support
  
  IMAGE_FEATURES = " \
      ${DEFAULT_FS_SUPPORT} \
      \
      read-only-rootfs \
  "
  
  # Avoid installation of syslog
  BAD_RECOMMENDATIONS += "busybox-syslog"
  
  # Avoid static /dev
  USE_DEVFS = "1"
  
  inherit core-image
  
  CORE_IMAGE_BASE_INSTALL = " \
      ${CORE_IMAGE_EXTRA_INSTALL} \
  "
  ```
* recompile
  * `target=fsl-image-mfgtool-initramfs`
  * `source setup-environment imx6q-x11`
  * `bitbake $target`
  * `bitbake -f -c image_cpio $target`

