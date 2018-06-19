# Operation bb File

## Pre Command

* `mkdir fsl-release-bsp && cd fsl-release-bsp`
* `repo init -u git://git.freescale.com/imx/fsl-arm-yocto-bsp.git -b imx-4.1-krogoth`
* `repo sync`
* `MACHINE=imx6dlsabresd DISTRO=fsl-imx-x11 source ./fsl-setup-release.sh -b imx6q-x11`
* `bitbake fsl-image-qt5`
* output: `imx6q-x11/tmp/deploy/images/imx6dlsabresd`

## Get Release Dependent .bb Files

* `MACHINE=imx6dlsabresd DISTRO=fsl-imx-x11 source ./fsl-setup-release.sh -b imx6q-x11`
* `bitbake fsl-image-qt5 -g`
* Generate file: [refers/dotFiles](refers/dotFiles)

当你不确定依赖的bb文件的时候，就可以通过这个方法来查看。

## Get Specified Dependent .bb File

* `MACHINE=imx6dlsabresd DISTRO=fsl-imx-x11 source ./fsl-setup-release.sh -b imx6q-x11`
* `bitbake u-boot-imx -g`
