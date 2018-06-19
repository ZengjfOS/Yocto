# Add New Layer And Recipe

## 参考文档

* [How to add a new layer and a new recipe in Yocto](https://community.nxp.com/docs/DOC-331917)

## 操作方法：

* `cd <BSP_DIR>/sources`
* `./poky/scripts/yocto-layer create <NEW_LAYER_NAME>`
  ```Shell
  zengjf@zengjf:~/fsl-release-bsp/sources$ ./poky/scripts/yocto-layer create zengjf
  Please enter the layer priority you'd like to use for the layer: [default: 6]
  Would you like to have an example recipe created? (y/n) [default: n] y
  Please enter the name you'd like to use for your example recipe: [default: example]
  Would you like to have an example bbappend file created? (y/n) [default: n] y
  Please enter the name you'd like to use for your bbappend file: [default: example]
  Please enter the version number you'd like to use for your bbappend file (this should match the recipe you're appending to): [default: 0.1]
  
  New layer created in meta-zengjf.
  
  Don't forget to add it to your BBLAYERS (for details see meta-zengjf/README).
  zengjf@zengjf:~/fsl-release-bsp/sources$ tree -L 4 meta-zengjf/
  meta-zengjf/
  ├── conf
  │   └── layer.conf
  ├── COPYING.MIT
  ├── README
  ├── recipes-example
  │   └── example
  │       ├── example-0.1
  │       │   ├── example.patch
  │       │   └── helloworld.c
  │       └── example_0.1.bb
  └── recipes-example-bbappend
      └── example-bbappend
          ├── example-0.1
          │   └── example.patch
          └── example_0.1.bbappend
  
  7 directories, 8 files
  ```
* `meta-zengjf/conf/layer.conf` config file
  ```Shell
  # We have a conf and classes directory, add to BBPATH
  BBPATH .= ":${LAYERDIR}"
  
  # We have recipes-* directories, add to BBFILES
  BBFILES += "${LAYERDIR}/recipes-*/*/*.bb \
          ${LAYERDIR}/recipes-*/*/*.bbappend"
  
  BBFILE_COLLECTIONS += "zengjf"
  BBFILE_PATTERN_zengjf = "^${LAYERDIR}/"
  BBFILE_PRIORITY_zengjf = "6"
  ```
* Adding the layer to our BSP
  ```Shell
  zengjf@zengjf:~/fsl-release-bsp/sources$ find * -iname bblayers.conf
  base/conf/bblayers.conf
  zengjf@zengjf:~/fsl-release-bsp/sources$ cat base/conf/bblayers.conf
  LCONF_VERSION = "6"
  
  BBPATH = "${TOPDIR}"
  BSPDIR := "${@os.path.abspath(os.path.dirname(d.getVar('FILE', True)) + '/../..')}"
  
  BBFILES ?= ""
  BBLAYERS = " \
    ${BSPDIR}/sources/poky/meta \
    ${BSPDIR}/sources/poky/meta-yocto \
    \
    ${BSPDIR}/sources/meta-openembedded/meta-oe \
    ${BSPDIR}/sources/meta-openembedded/meta-multimedia \
    \
    ${BSPDIR}/sources/meta-fsl-arm \
    ${BSPDIR}/sources/meta-fsl-arm-extra \
    ${BSPDIR}/sources/meta-fsl-demos \
    ${BSPDIR}/sources/meta-zengjf \
  "
  ```
* `rm imx6q-x11/conf/* -rf`
* `MACHINE=imx6dlsabresd DISTRO=fsl-imx-x11 source ./fsl-setup-release.sh -b imx6q-x11`
  ``` 
  zengjf@zengjf:~/fsl-release-bsp/imx6q-x11$ cat conf/bblayers.conf
  POKY_BBLAYERS_CONF_VERSION = "1"
  
  BBPATH = "${TOPDIR}"
  BSPDIR := "${@os.path.abspath(os.path.dirname(d.getVar('FILE', True)) + '/../..')}"
  
  BBFILES ?= ""
  BBLAYERS = " \
    ${BSPDIR}/sources/poky/meta \
    ${BSPDIR}/sources/poky/meta-poky \
    \
    ${BSPDIR}/sources/meta-openembedded/meta-oe \
    ${BSPDIR}/sources/meta-openembedded/meta-multimedia \
    \
    ${BSPDIR}/sources/meta-fsl-arm \
    ${BSPDIR}/sources/meta-fsl-arm-extra \
    ${BSPDIR}/sources/meta-fsl-demos \
    ${BSPDIR}/sources/meta-zengjf \
  "
  ##Freescale Yocto Project Release layer
  BBLAYERS += " ${BSPDIR}/sources/meta-fsl-bsp-release/imx/meta-bsp "
  BBLAYERS += " ${BSPDIR}/sources/meta-fsl-bsp-release/imx/meta-sdk "
  BBLAYERS += " ${BSPDIR}/sources/meta-browser "
  BBLAYERS += " ${BSPDIR}/sources/meta-openembedded/meta-gnome "
  BBLAYERS += " ${BSPDIR}/sources/meta-openembedded/meta-networking "
  BBLAYERS += " ${BSPDIR}/sources/meta-openembedded/meta-python "
  BBLAYERS += " ${BSPDIR}/sources/meta-openembedded/meta-filesystems "
  BBLAYERS += " ${BSPDIR}/sources/meta-qt5 "
  ```
* `bitbake example -g`
* `bitbake example -c cleanall`
* `bitbake example`
* Compile Over Output:
  ```
  zengjf@zengjf:~/fsl-release-bsp/imx6q-x11$ find * -iname helloworld | grep 'example/'
  tmp/work/cortexa9hf-neon-poky-linux-gnueabi/example/0.1-r0/package/usr/bin/helloworld
  tmp/work/cortexa9hf-neon-poky-linux-gnueabi/example/0.1-r0/package/usr/bin/.debug/helloworld
  tmp/work/cortexa9hf-neon-poky-linux-gnueabi/example/0.1-r0/image/usr/bin/helloworld
  tmp/work/cortexa9hf-neon-poky-linux-gnueabi/example/0.1-r0/packages-split/example-dbg/usr/bin/.debug/helloworld
  tmp/work/cortexa9hf-neon-poky-linux-gnueabi/example/0.1-r0/packages-split/example/usr/bin/helloworld
  tmp/work/cortexa9hf-neon-poky-linux-gnueabi/example/0.1-r0/helloworld
  ```

## `fsl-setup-release.sh`配置过程跟踪

```Shell
zengjf@zengjf:~/fsl-release-bsp$ ./auto.sh
++ MACHINE=imx6dlsabresd
++ DISTRO=fsl-imx-x11
++ source ./fsl-setup-release.sh -b imx6q-x11
++++ pwd
+++ CWD=/home/zengjf/fsl-release-bsp
+++ PROGNAME=setup-environment
+++ OLD_OPTIND=1
+++ unset FSLDISTRO
+++ getopts k:r:t:b:e:gh fsl_setup_flag
+++ case $fsl_setup_flag in
+++ BUILD_DIR=imx6q-x11
+++ echo -e '\n Build directory is ' imx6q-x11

 Build directory is  imx6q-x11
+++ getopts k:r:t:b:e:gh fsl_setup_flag
+++ '[' -z fsl-imx-x11 ']'
+++ FSLDISTRO=fsl-imx-x11
+++ OPTIND=1
+++ test
+++ test
+++ '[' -z imx6q-x11 ']'
+++ '[' -z imx6dlsabresd ']'
+++ '[' -d ./sources/meta-freescale ']'
+++ cp -r sources/meta-fsl-bsp-release/imx/meta-bsp/conf/machine/imx6qdlsolo.conf sources/meta-fsl-bsp-release/imx/meta-bsp/conf/machine/imx6qpsabresd.conf sources/meta-fsl-bsp-release/imx/meta-bsp/conf/machine/imx6slevk.conf sources/meta-fsl-bsp-release/imx/meta-bsp/conf/machine/imx6sll_all.conf sources/meta-fsl-bsp-release/imx/meta-bsp/conf/machine/imx6sllevk.conf sources/meta-fsl-bsp-release/imx/meta-bsp/conf/machine/imx6slllpddr2arm2.conf sources/meta-fsl-bsp-release/imx/meta-bsp/conf/machine/imx6slllpddr3arm2.conf sources/meta-fsl-bsp-release/imx/meta-bsp/conf/machine/imx6sx14x14arm2.conf sources/meta-fsl-bsp-release/imx/meta-bsp/conf/machine/imx6sx17x17arm2.conf sources/meta-fsl-bsp-release/imx/meta-bsp/conf/machine/imx6sx19x19ddr3arm2.conf sources/meta-fsl-bsp-release/imx/meta-bsp/conf/machine/imx6sx19x19lpddr2arm2.conf sources/meta-fsl-bsp-release/imx/meta-bsp/conf/machine/imx6sx_all.conf sources/meta-fsl-bsp-release/imx/meta-bsp/conf/machine/imx6ul14x14ddr3arm2.conf sources/meta-fsl-bsp-release/imx/meta-bsp/conf/machine/imx6ul14x14lpddr2arm2.conf sources/meta-fsl-bsp-release/imx/meta-bsp/conf/machine/imx6ul7d.conf sources/meta-fsl-bsp-release/imx/meta-bsp/conf/machine/imx6ul9x9evk.conf sources/meta-fsl-bsp-release/imx/meta-bsp/conf/machine/imx6ulevk.conf sources/meta-fsl-bsp-release/imx/meta-bsp/conf/machine/imx6ull14x14ddr3arm2.conf sources/meta-fsl-bsp-release/imx/meta-bsp/conf/machine/imx6ull14x14evk.conf sources/meta-fsl-bsp-release/imx/meta-bsp/conf/machine/imx6ull9x9evk.conf sources/meta-fsl-bsp-release/imx/meta-bsp/conf/machine/imx7d12x12ddr3arm2.conf sources/meta-fsl-bsp-release/imx/meta-bsp/conf/machine/imx7d12x12lpddr3arm2.conf sources/meta-fsl-bsp-release/imx/meta-bsp/conf/machine/imx7d19x19ddr3arm2.conf sources/meta-fsl-bsp-release/imx/meta-bsp/conf/machine/imx7d19x19lpddr2arm2.conf sources/meta-fsl-bsp-release/imx/meta-bsp/conf/machine/imx7d19x19lpddr3arm2.conf sources/meta-fsl-bsp-release/imx/meta-bsp/conf/machine/imx7dsabresd.conf sources/meta-fsl-bsp-release/imx/meta-bsp/conf/machine/include sources/meta-fsl-arm/conf/machine
+++ '[' -d ./sources/meta-freescale ']'
+++ cp sources/meta-fsl-bsp-release/imx/EULA.txt sources/meta-fsl-arm/EULA
+++ cp sources/meta-fsl-bsp-release/imx/classes/fsl-eula-unpack.bbclass sources/meta-fsl-arm/classes
+++ '[' -z fsl-imx-x11 ']'
+++ MACHINE=imx6dlsabresd
+++ . ./setup-environment imx6q-x11
+++++ pwd
++++ CWD=/home/zengjf/fsl-release-bsp
++++ PROGNAME=setup-environment
++++ SHORTOPTS=h
++++ LONGOPTS=help
+++++ getopt --options h --longoptions help --name setup-environment -- imx6q-x11
++++ ARGS=' -- '\''imx6q-x11'\'''
++++ '[' 0 '!=' 0 -o 1 -lt 1 ']'
++++ eval set -- ' -- '\''imx6q-x11'\'''
+++++ set -- -- imx6q-x11
++++ true
++++ case $1 in
++++ shift
++++ break
+++++ whoami
++++ '[' zengjf = root ']'
++++ '[' -z imx6dlsabresd ']'
+++++ ls -1 /home/zengjf/fsl-release-bsp/sources/meta-fsl-arm/conf/machine /home/zengjf/fsl-release-bsp/sources/meta-fsl-arm-extra/conf/machine
++++ LIST_MACHINES='/home/zengjf/fsl-release-bsp/sources/meta-fsl-arm/conf/machine:
imx23evk.conf
imx28evk.conf
imx51evk.conf
imx53ard.conf
imx53qsb.conf
imx6dlsabreauto.conf
imx6dlsabresd.conf
imx6qdlsolo.conf
imx6qpsabreauto.conf
imx6qpsabresd.conf
imx6qsabreauto.conf
imx6qsabresd.conf
imx6slevk.conf
imx6sll_all.conf
imx6sllevk.conf
imx6slllpddr2arm2.conf
imx6slllpddr3arm2.conf
imx6solosabreauto.conf
imx6solosabresd.conf
imx6sx14x14arm2.conf
imx6sx17x17arm2.conf
imx6sx19x19ddr3arm2.conf
imx6sx19x19lpddr2arm2.conf
imx6sx_all.conf
imx6sxsabreauto.conf
imx6sxsabresd.conf
imx6ul14x14ddr3arm2.conf
imx6ul14x14lpddr2arm2.conf
imx6ul7d.conf
imx6ul9x9evk.conf
imx6ulevk.conf
imx6ull14x14ddr3arm2.conf
imx6ull14x14evk.conf
imx6ull9x9evk.conf
imx7d12x12ddr3arm2.conf
imx7d12x12lpddr3arm2.conf
imx7d19x19ddr3arm2.conf
imx7d19x19lpddr2arm2.conf
imx7d19x19lpddr3arm2.conf
imx7dsabresd.conf
include
ls1021atwr.conf
twr-vf65gs10.conf

/home/zengjf/fsl-release-bsp/sources/meta-fsl-arm-extra/conf/machine:
apalis-imx6.conf
cfa10036.conf
cfa10037.conf
cfa10049.conf
cfa10055.conf
cfa10056.conf
cfa10057.conf
cfa10058.conf
cgtqmx6.conf
cm-fx6.conf
colibri-imx6.conf
colibri-imx7.conf
colibri-vf.conf
cubox-i.conf
imx233-olinuxino-maxi.conf
imx233-olinuxino-micro.conf
imx233-olinuxino-mini.conf
imx233-olinuxino-nano.conf
imx6dl-riotboard.conf
imx6qdl-variscite-som.conf
imx6q-dms-ba16.conf
imx6qsabrelite.conf
imx6sl-warp.conf
imx6ul-pico-hobbit.conf
imx7s-warp.conf
include
m28evk.conf
m53evk.conf
nitrogen6sx.conf
nitrogen6x.conf
nitrogen6x-lite.conf
nitrogen7.conf
pcm052.conf
tx6q-10x0.conf
tx6q-11x0.conf
tx6s-8034.conf
tx6s-8035.conf
tx6u-8033.conf
tx6u-80x0.conf
tx6u-81x0.conf
ventana.conf
wandboard.conf'
+++++ echo -e '/home/zengjf/fsl-release-bsp/sources/meta-fsl-arm/conf/machine:
imx23evk.conf
imx28evk.conf
imx51evk.conf
imx53ard.conf
imx53qsb.conf
imx6dlsabreauto.conf
imx6dlsabresd.conf
imx6qdlsolo.conf
imx6qpsabreauto.conf
imx6qpsabresd.conf
imx6qsabreauto.conf
imx6qsabresd.conf
imx6slevk.conf
imx6sll_all.conf
imx6sllevk.conf
imx6slllpddr2arm2.conf
imx6slllpddr3arm2.conf
imx6solosabreauto.conf
imx6solosabresd.conf
imx6sx14x14arm2.conf
imx6sx17x17arm2.conf
imx6sx19x19ddr3arm2.conf
imx6sx19x19lpddr2arm2.conf
imx6sx_all.conf
imx6sxsabreauto.conf
imx6sxsabresd.conf
imx6ul14x14ddr3arm2.conf
imx6ul14x14lpddr2arm2.conf
imx6ul7d.conf
imx6ul9x9evk.conf
imx6ulevk.conf
imx6ull14x14ddr3arm2.conf
imx6ull14x14evk.conf
imx6ull9x9evk.conf
imx7d12x12ddr3arm2.conf
imx7d12x12lpddr3arm2.conf
imx7d19x19ddr3arm2.conf
imx7d19x19lpddr2arm2.conf
imx7d19x19lpddr3arm2.conf
imx7dsabresd.conf
include
ls1021atwr.conf
twr-vf65gs10.conf

/home/zengjf/fsl-release-bsp/sources/meta-fsl-arm-extra/conf/machine:
apalis-imx6.conf
cfa10036.conf
cfa10037.conf
cfa10049.conf
+++++ grep 'imx6dlsabresd.conf$'
+++++ wc -l
cfa10055.conf
cfa10056.conf
cfa10057.conf
cfa10058.conf
cgtqmx6.conf
cm-fx6.conf
colibri-imx6.conf
colibri-imx7.conf
colibri-vf.conf
cubox-i.conf
imx233-olinuxino-maxi.conf
imx233-olinuxino-micro.conf
imx233-olinuxino-mini.conf
imx233-olinuxino-nano.conf
imx6dl-riotboard.conf
imx6qdl-variscite-som.conf
imx6q-dms-ba16.conf
imx6qsabrelite.conf
imx6sl-warp.conf
imx6ul-pico-hobbit.conf
imx7s-warp.conf
include
m28evk.conf
m53evk.conf
nitrogen6sx.conf
nitrogen6x.conf
nitrogen6x-lite.conf
nitrogen7.conf
pcm052.conf
tx6q-10x0.conf
tx6q-11x0.conf
tx6s-8034.conf
tx6s-8035.conf
tx6u-8033.conf
tx6u-80x0.conf
tx6u-81x0.conf
ventana.conf
wandboard.conf'
++++ VALID_MACHINE=1
++++ '[' ximx6dlsabresd = x ']'
++++ '[' 1 = 0 ']'
++++ '[' '!' -e imx6q-x11/conf/local.conf.sample ']'
++++ echo 'Configuring for imx6dlsabresd'
Configuring for imx6dlsabresd
++++ '[' -z '' ']'
++++ SDKMACHINE=i686
++++ '[' -z fsl-imx-x11 ']'
++++ OEROOT=/home/zengjf/fsl-release-bsp/sources/poky
++++ '[' -e /home/zengjf/fsl-release-bsp/sources/oe-core ']'
++++ updated=
++++ for f in '$CWD/sources/base/*'
+++++ basename /home/zengjf/fsl-release-bsp/sources/base/conf
++++ file=conf
++++ '[' conf = conf ']'
++++ continue
++++ for f in '$CWD/sources/base/*'
+++++ basename /home/zengjf/fsl-release-bsp/sources/base/README
++++ file=README
++++ '[' README = conf ']'
++++ echo README
++++ grep -q '~$'
++++ cmp -s README /home/zengjf/fsl-release-bsp/sources/base/README
++++ for f in '$CWD/sources/base/*'
+++++ basename /home/zengjf/fsl-release-bsp/sources/base/setup-environment
++++ file=setup-environment
++++ '[' setup-environment = conf ']'
++++ echo setup-environment
++++ grep -q '~$'
++++ cmp -s setup-environment /home/zengjf/fsl-release-bsp/sources/base/setup-environment
++++ '[' '' = true ']'
++++ . /home/zengjf/fsl-release-bsp/sources/poky/oe-init-build-env /home/zengjf/fsl-release-bsp/imx6q-x11
+++++ '[' -n /home/zengjf/fsl-release-bsp/sources/poky/oe-init-build-env ']'
+++++ THIS_SCRIPT=/home/zengjf/fsl-release-bsp/sources/poky/oe-init-build-env
+++++ '[' -n '' ']'
+++++ '[' -z '' ']'
+++++ '[' ./auto.sh = /home/zengjf/fsl-release-bsp/sources/poky/oe-init-build-env ']'
+++++ '[' -z /home/zengjf/fsl-release-bsp/sources/poky ']'
+++++ unset THIS_SCRIPT
+++++ export OEROOT
+++++ . /home/zengjf/fsl-release-bsp/sources/poky/scripts/oe-buildenv-internal
++++++ '[' -z /home/zengjf/fsl-release-bsp/sources/poky ']'
++++++ '[' -z '' ']'
++++++ '[' -n '' ']'
+++++++ /usr/bin/env python --version
+++++++ grep 'Python 3'
++++++ py_v3_check=
++++++ '[' -n '' ']'
++++++ unset py_v3_check
+++++++ python -c 'import sys; print sys.version_info >= (2,7,3)'
++++++ py_v26_check=True
++++++ '[' True '!=' True ']'
++++++ unset py_v26_check
++++++ '[' -z '' ']'
++++++ '[' -z /home/zengjf/fsl-release-bsp/imx6q-x11 ']'
++++++ BDIR=/home/zengjf/fsl-release-bsp/imx6q-x11
++++++ '[' /home/zengjf/fsl-release-bsp/imx6q-x11 = / ']'
+++++++ echo /home/zengjf/fsl-release-bsp/imx6q-x11
+++++++ sed -re 's|/+$||'
++++++ BDIR=/home/zengjf/fsl-release-bsp/imx6q-x11
+++++++ readlink -f /home/zengjf/fsl-release-bsp/imx6q-x11
++++++ BDIR=/home/zengjf/fsl-release-bsp/imx6q-x11
++++++ '[' -z /home/zengjf/fsl-release-bsp/imx6q-x11 ']'
++++++ '[' -n '' ']'
++++++ '[' home/zengjf/fsl-release-bsp/imx6q-x11 '!=' /home/zengjf/fsl-release-bsp/imx6q-x11 ']'
++++++ BUILDDIR=/home/zengjf/fsl-release-bsp/imx6q-x11
++++++ unset BDIR
++++++ '[' -z '' ']'
++++++ BITBAKEDIR=/home/zengjf/fsl-release-bsp/sources/poky/bitbake
+++++++ readlink -f /home/zengjf/fsl-release-bsp/sources/poky/bitbake
++++++ BITBAKEDIR=/home/zengjf/fsl-release-bsp/sources/poky/bitbake
+++++++ readlink -f /home/zengjf/fsl-release-bsp/imx6q-x11
++++++ BUILDDIR=/home/zengjf/fsl-release-bsp/imx6q-x11
++++++ '[' '!' -d /home/zengjf/fsl-release-bsp/sources/poky/bitbake ']'
++++++ for newpath in '"$BITBAKEDIR/bin"' '"$OEROOT/scripts"'
+++++++ echo /home/zengjf/fsl-release-bsp/sources/poky/scripts:/home/zengjf/fsl-release-bsp/sources/poky/bitbake/bin:/home/zengjf/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games
+++++++ sed -re 's#(^|:)/home/zengjf/fsl-release-bsp/sources/poky/bitbake/bin(:|$)#\2#g;s#^:##'
++++++ PATH=/home/zengjf/fsl-release-bsp/sources/poky/scripts:/home/zengjf/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games
++++++ PATH=/home/zengjf/fsl-release-bsp/sources/poky/bitbake/bin:/home/zengjf/fsl-release-bsp/sources/poky/scripts:/home/zengjf/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games
++++++ for newpath in '"$BITBAKEDIR/bin"' '"$OEROOT/scripts"'
+++++++ echo /home/zengjf/fsl-release-bsp/sources/poky/bitbake/bin:/home/zengjf/fsl-release-bsp/sources/poky/scripts:/home/zengjf/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games
+++++++ sed -re 's#(^|:)/home/zengjf/fsl-release-bsp/sources/poky/scripts(:|$)#\2#g;s#^:##'
++++++ PATH=/home/zengjf/fsl-release-bsp/sources/poky/bitbake/bin:/home/zengjf/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games
++++++ PATH=/home/zengjf/fsl-release-bsp/sources/poky/scripts:/home/zengjf/fsl-release-bsp/sources/poky/bitbake/bin:/home/zengjf/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games
++++++ unset BITBAKEDIR newpath
++++++ export BUILDDIR
++++++ export PATH
++++++ BB_ENV_EXTRAWHITE_OE='MACHINE DISTRO TCMODE TCLIBC HTTP_PROXY http_proxy HTTPS_PROXY https_proxy FTP_PROXY ftp_proxy FTPS_PROXY ftps_proxy ALL_PROXY all_proxy NO_PROXY no_proxy SSH_AGENT_PID SSH_AUTH_SOCK BB_SRCREV_POLICY SDKMACHINE BB_NUMBER_THREADS BB_NO_NETWORK PARALLEL_MAKE GIT_PROXY_COMMAND SOCKS5_PASSWD SOCKS5_USER SCREENDIR STAMPS_DIR'
+++++++ tr ' ' '\n'
+++++++ LC_ALL=C
+++++++ echo ALL_PROXY BB_NO_NETWORK BB_NUMBER_THREADS BB_SRCREV_POLICY DISTRO FTPS_PROXY FTP_PROXY GIT_PROXY_COMMAND HTTPS_PROXY HTTP_PROXY MACHINE NO_PROXY PARALLEL_MAKE SCREENDIR SDKMACHINE SOCKS5_PASSWD SOCKS5_USER SSH_AGENT_PID SSH_AUTH_SOCK STAMPS_DIR TCLIBC TCMODE all_proxy ftp_proxy ftps_proxy http_proxy https_proxy no_proxy MACHINE DISTRO TCMODE TCLIBC HTTP_PROXY http_proxy HTTPS_PROXY https_proxy FTP_PROXY ftp_proxy FTPS_PROXY ftps_proxy ALL_PROXY all_proxy NO_PROXY no_proxy SSH_AGENT_PID SSH_AUTH_SOCK BB_SRCREV_POLICY SDKMACHINE BB_NUMBER_THREADS BB_NO_NETWORK PARALLEL_MAKE GIT_PROXY_COMMAND SOCKS5_PASSWD SOCKS5_USER SCREENDIR STAMPS_DIR
+++++++ sort --unique
+++++++ tr '\n' ' '
++++++ BB_ENV_EXTRAWHITE='ALL_PROXY BB_NO_NETWORK BB_NUMBER_THREADS BB_SRCREV_POLICY DISTRO FTPS_PROXY FTP_PROXY GIT_PROXY_COMMAND HTTPS_PROXY HTTP_PROXY MACHINE NO_PROXY PARALLEL_MAKE SCREENDIR SDKMACHINE SOCKS5_PASSWD SOCKS5_USER SSH_AGENT_PID SSH_AUTH_SOCK STAMPS_DIR TCLIBC TCMODE all_proxy ftp_proxy ftps_proxy http_proxy https_proxy no_proxy '
++++++ export BB_ENV_EXTRAWHITE
+++++ TEMPLATECONF=
+++++ /home/zengjf/fsl-release-bsp/sources/poky/scripts/oe-setup-builddir
+++++ unset OEROOT
+++++ '[' -z /home/zengjf/fsl-release-bsp/imx6q-x11 ']'
+++++ cd /home/zengjf/fsl-release-bsp/imx6q-x11
+++++ '[' -z '' ']'
+++++ '[' -f bitbake.lock ']'
+++++ grep : bitbake.lock
+++++ '[' 1 = 0 ']'
++++ '[' '!' -e conf/local.conf ']'
+++++ echo /home/zengjf/fsl-release-bsp/sources/poky/scripts:/home/zengjf/fsl-release-bsp/sources/poky/bitbake/bin:/home/zengjf/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games
+++++ sed 's/\(:.\|:\)*:/:/g;s/^.\?://;s/:.\?$//'
++++ export PATH=/home/zengjf/fsl-release-bsp/sources/poky/scripts:/home/zengjf/fsl-release-bsp/sources/poky/bitbake/bin:/home/zengjf/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games
++++ PATH=/home/zengjf/fsl-release-bsp/sources/poky/scripts:/home/zengjf/fsl-release-bsp/sources/poky/bitbake/bin:/home/zengjf/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games
++++ generated_config=
++++ '[' '!' -e conf/local.conf.sample ']'
++++ mv conf/local.conf conf/local.conf.sample
++++ TEMPLATES=/home/zengjf/fsl-release-bsp/sources/base/conf
++++ grep -v '^#\|^$' conf/local.conf.sample
++++ cat
++++ sed -e 's,MACHINE ??=.*,MACHINE ??= '\''imx6dlsabresd'\'',g' -e 's,SDKMACHINE ??=.*,SDKMACHINE ??= '\''i686'\'',g' -e 's,DISTRO ?=.*,DISTRO ?= '\''fsl-imx-x11'\'',g' -i conf/local.conf
++++ cp /home/zengjf/fsl-release-bsp/sources/base/conf/bblayers.conf conf/
++++ for s in '$HOME/.oe' '$HOME/.yocto'
++++ '[' -e /home/zengjf/.oe/site.conf ']'
++++ for s in '$HOME/.oe' '$HOME/.yocto'
++++ '[' -e /home/zengjf/.yocto/site.conf ']'
++++ generated_config=1
++++ EULA_ACCEPTED=
++++ grep -q '^\s*ACCEPT_FSL_EULA\s*=\s*["'\'']..*["'\'']' conf/local.conf
++++ '[' -z '' ']'
++++ '[' -n '' ']'
++++ '[' -n '' ']'
++++ cat

Some BSPs depend on libraries and packages which are covered by Freescale's
End User License Agreement (EULA). To have the right to use these binaries in
your images, you need to read and accept the following...

++++ sleep 4
++++ more -d /home/zengjf/fsl-release-bsp/sources/meta-fsl-arm/EULA
LA_OPT_BASE_LICENSE v16 September 2016

IMPORTANT.  Read the following NXP Semiconductors Software License Agreement
("Agreement") completely.    By selecting the "I Accept" button at the end of
this page, you indicate that you accept the terms of the Agreement and you
acknowledge that you have the authority, for yourself or on behalf of your
company, to bind your company to these terms.  You may then download or install
the file.

NXP SEMICONDUCTORS SOFTWARE LICENSE AGREEMENT

This is a legal agreement between you, as an authorized representative of your
employer, or if you have no employer, as an individual (together "you"), and
Freescale Semiconductor, Inc., a 100% affiliated company of NXP Semiconductors
N.V. ("NXP").  It concerns your rights to use the software identified in the
Software Content Register and provided to you in binary or source code form and
any accompanying written materials (the "Licensed Software"). The Licensed
Software may include any updates or error corrections or documentation relating
to the Licensed Software provided to you by NXP under this License. In
consideration for NXP allowing you to access the Licensed Software, you are
agreeing to be bound by the terms of this Agreement. If you do not agree to all
of the terms of this Agreement, do not download or install the Licensed
Software. If you change your mind later, stop using the Licensed Software and
delete all copies of the Licensed Software in your possession or control. Any
copies of the Licensed Software that you have already distributed, where
permitted, and do not destroy will continue to be governed by this Agreement.
Your prior use will also continue to be governed by this Agreement.

1.                 DEFINITIONS

1.1.                  "Affiliates" means, any corporation, or entity directly
or indirectly controlled by, controlling, or under common control with NXP
Semiconductors N.V.

1.2.                  "Essential Patent" means a patent to the limited extent
that infringement of such patent cannot be avoided in remaining compliant with
the technology standards implicated by the usage of any of the Licensed
Software, including optional implementation of the standards, on technical but
not commercial grounds, taking into account normal technical practice and the
state of the art generally available at the time of standardization.

1.3.                  "Intellectual Property Rights" means any and all rights
under statute, common law or equity in and under copyrights, trade secrets, and
patents (including utility models), and analogous rights throughout the world,
including any applications for and the right to apply for, any of the
foregoing.

1.4.                  "Software Content Register" means the documentation
accompanying the Licensed Software which identifies the contents of the
Licensed Software, including but not limited to identification of any Third
Party Software.

1.5.                  "Third Party Software" means, any software included in
the Licensed Software that is not NXP Proprietary software, and is not open
source software, and to which different license terms may apply.

2.                 LICENSE GRANT.

2.1.                  Separate license grants to Third Party Software, or other
terms applicable to the Licensed Software if different from those granted in
++++ echo

++++ REPLY=
++++ '[' -z '' ']'
++++ echo -n 'Do you accept the EULA you just read? (y/n) '
Do you accept the EULA you just read? (y/n) ++++ read REPLY
y
++++ case "$REPLY" in
++++ echo 'EULA has been accepted.'
EULA has been accepted.
++++ echo 'ACCEPT_FSL_EULA = "1"'
++++ '[' -z y ']'
++++ cat

Welcome to Freescale Community BSP

The Yocto Project has extensive documentation about OE including a
reference manual which can be found at:
    http://yoctoproject.org/documentation

For more information about OpenEmbedded see their website:
    http://www.openembedded.org/

You can now run 'bitbake <target>'

Common targets are:
    core-image-minimal
    meta-toolchain
    meta-toolchain-sdk
    adt-installer
    meta-ide-support

++++ '[' -n 1 ']'
++++ cat
Your build environment has been configured with:

    MACHINE=imx6dlsabresd
    SDKMACHINE=i686
    DISTRO=fsl-imx-x11
    EULA=
++++ clean_up
++++ unset EULA LIST_MACHINES VALID_MACHINE
++++ unset CWD TEMPLATES SHORTOPTS LONGOPTS ARGS PROGNAME
++++ unset generated_config updated
++++ unset MACHINE SDKMACHINE DISTRO OEROOT
+++ BUILD_DIR=.
+++ '[' '!' -e ./conf/local.conf ']'
+++ '[' '!' -e ./conf/local.conf.org ']'
+++ cp ./conf/local.conf ./conf/local.conf.org
+++ '[' '!' -e ./conf/bblayers.conf.org ']'
+++ cp ./conf/bblayers.conf ./conf/bblayers.conf.org
+++ META_FSL_BSP_RELEASE=/sources/meta-fsl-bsp-release/imx/meta-bsp
+++ echo '##Freescale Yocto Project Release layer'
+++ echo 'BBLAYERS += " ${BSPDIR}/sources/meta-fsl-bsp-release/imx/meta-bsp "'
+++ echo 'BBLAYERS += " ${BSPDIR}/sources/meta-fsl-bsp-release/imx/meta-sdk "'
+++ echo 'BBLAYERS += " ${BSPDIR}/sources/meta-browser "'
+++ echo 'BBLAYERS += " ${BSPDIR}/sources/meta-openembedded/meta-gnome "'
+++ echo 'BBLAYERS += " ${BSPDIR}/sources/meta-openembedded/meta-networking "'
+++ echo 'BBLAYERS += " ${BSPDIR}/sources/meta-openembedded/meta-python "'
+++ echo 'BBLAYERS += " ${BSPDIR}/sources/meta-openembedded/meta-filesystems "'
+++ echo 'BBLAYERS += " ${BSPDIR}/sources/meta-qt5 "'
+++ echo BSPDIR=
BSPDIR=
+++ echo BUILD_DIR=.
BUILD_DIR=.
+++ '[' -d ../sources/meta-freescale ']'
+++ cd .
+++ clean_up
+++ unset EULA LIST_MACHINES VALID_MACHINE
+++ unset CWD TEMPLATES SHORTOPTS LONGOPTS ARGS PROGNAME
+++ unset generated_config updated
+++ unset MACHINE SDKMACHINE DISTRO OEROOT
+++ unset FSLDISTRO
++ bitbake fsl-image-qt5 -g
NOTE: Your conf/bblayers.conf has been automatically updated.
Loading cache: 100% |#########################################################################################################################| ETA:  00:00:00
Loaded 2793 entries from dependency cache.
Parsing recipes: 100% |#######################################################################################################################| Time: 00:00:01
Parsing of 2217 .bb files complete (2202 cached, 15 parsed). 2792 targets, 206 skipped, 5 masked, 0 errors.
NOTE: Resolving any missing task queue dependencies
NOTE: Preparing RunQueue
WARNING: /home/zengjf/fsl-release-bsp/sources/meta-fsl-bsp-release/imx/meta-bsp/recipes-bsp/u-boot/u-boot-imx_2016.03.bb.do_compile is tainted from a forced run
NOTE: PN build list saved to 'pn-buildlist'
NOTE: PN dependencies saved to 'pn-depends.dot'
NOTE: Package dependencies saved to 'package-depends.dot'
NOTE: Task dependencies saved to 'task-depends.dot'

Summary: There was 1 WARNING message shown.
```
