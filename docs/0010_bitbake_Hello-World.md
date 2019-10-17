# bitbake Hello-World

## 参考文档

* [A.4. The Hello World Example](https://www.yoctoproject.org/docs/2.7.1/bitbake-user-manual/bitbake-user-manual.html#hello-world-example)
* [1.4. Obtaining BitBake](https://www.yoctoproject.org/docs/2.7.1/bitbake-user-manual/bitbake-user-manual.html#obtaining-bitbake)
* [yocto recipe : (1) 撰寫 recipe](http://yi-jyun.blogspot.com/2018/02/yocto-2-recipe.html)
* [3.3.2. Locate or Automatically Create a Base Recipe](https://www.yoctoproject.org/docs/current/dev-manual/dev-manual.html#new-recipe-writing-a-new-recipe)
* [DynamicDevices meta-example](https://github.com/DynamicDevices/meta-example)

## 源代码

* [helloworld源代码](https://github.com/ZengjfOS/Yocto/tree/helloworld)
* [meta-layer源代码](https://github.com/ZengjfOS/Yocto/tree/_Setup_Env)

## 创建layer

* `bitbake-layers create-layer meta-example`
* `imx8-build-wayland/conf/bblayers.conf`
  ```
  zengjf@UbuntuServer:imx8-build-wayland$ cat conf/bblayers.conf
  LCONF_VERSION = "7"
  
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
    ${BSPDIR}/sources/meta-freescale \
    ${BSPDIR}/sources/meta-freescale-3rdparty \
    ${BSPDIR}/sources/meta-freescale-distro \
  "
  
  # i.MX Yocto Project Release layers
  BBLAYERS += " ${BSPDIR}/sources/meta-fsl-bsp-release/imx/meta-bsp "
  BBLAYERS += " ${BSPDIR}/sources/meta-fsl-bsp-release/imx/meta-sdk "
  
  BBLAYERS += " ${BSPDIR}/sources/meta-browser "
  BBLAYERS += " ${BSPDIR}/sources/meta-openembedded/meta-gnome "
  BBLAYERS += " ${BSPDIR}/sources/meta-openembedded/meta-networking "
  BBLAYERS += " ${BSPDIR}/sources/meta-openembedded/meta-python "
  BBLAYERS += " ${BSPDIR}/sources/meta-openembedded/meta-filesystems "
  BBLAYERS += " ${BSPDIR}/sources/meta-qt5 "
  BBLAYERS += " ${BSPDIR}/sources/meta-example "                                # add your layer
  ```
* `sources/meta-example/recipes-example/example/example_0.1.bb`
  ```
  SUMMARY = "bitbake-layers recipe"
  DESCRIPTION = "Recipe created by bitbake-layers"
  LICENSE = "MIT"
  
  python do_build() {                                                   #  尝试过很多次，这个build不会给执行，暂时不知道为什么
      bb.plain("***********************************************");
      bb.plain("*                                             *");
      bb.plain("*  Example recipe created by bitbake-layers   *");
      bb.plain("*                                             *");
      bb.plain("***********************************************");
  }
  
  python do_compile() {
      bb.plain("*****************do_compile********************");
      bb.plain("*                                             *");
      bb.plain("*  Example recipe created by bitbake-layers   *");
      bb.plain("*                                             *");
      bb.plain("***********************************************");
  }
  ```
* bitbake example -c compile -DD -f
  ```
  DEBUG: Inheriting /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/logging.bbclass (from /home/zengjf/imx8-yocto-ga/sources/poky/meta/classes/base.bbclass:11)
  DEBUG: Stampfile /home/zengjf/imx8-yocto-ga/imx8-build-wayland/tmp/stamps/aarch64-poky-linux/example/0.1-r0.do_compile.ba1917feed47dfea38fb990ca5447d00 not available
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
  DEBUG: Parsing /home/zengjf/imx8-yocto-ga/sources/meta-example/recipes-example/example/example_0.1.bb (full)
  DEBUG: Executing task do_compile
  DEBUG: example-0.1-r0 do_compile: Executing python function do_compile
  *****************do_compile********************
  *                                             *
  *  Example recipe created by bitbake-layers   *
  *                                             *
  ***********************************************
  DEBUG: example-0.1-r0 do_compile: Python function do_compile finished
  DEBUG: Teardown for bitbake-worker
  NOTE: Tasks Summary: Attempted 258 tasks of which 257 didn't need to be rerun and all succeeded.
  
  Summary: There were 4 WARNING messages shown.
  ```

## recipetool创建recipe

* `recipetool create --help`
* `cd sources/meta-example`
* `mkdir recipes-helloworld/helloworld`
* `recipetool create -N helloworld -B helloworld -o recipes-helloworld/helloworld/helloworld_0.1.bb https://github.com/ZengjfOS/Yocto`
* `cat recipes-helloworld/helloworld/helloworld_0.1.bb`
  ```
  # Recipe created by recipetool
  # This is the basis of a recipe and may need further editing in order to be fully functional.
  # (Feel free to remove these comments when editing.)
  # Unable to find any files that looked like license statements. Check the accompanying
  # documentation and source headers and set LICENSE and LIC_FILES_CHKSUM accordingly.
  #
  # NOTE: LICENSE is being set to "CLOSED" to allow you to at least start building - if
  # this is not accurate with respect to the licensing of the software being built (it
  # will not be in most cases) you must specify the correct value before using this
  # recipe for anything other than initial testing/development!
  LICENSE = "CLOSED"
  LIC_FILES_CHKSUM = ""
  SRC_URI = "git://github.com/ZengjfOS/Yocto;protocol=https;branch=helloworld"
  # Modify these as desired
  PV = "0.1+git${SRCPV}"
  SRCREV = "1da23ae3b40b9081cc2e6d9621c8d6cf32c50494"
  S = "${WORKDIR}/git"
  # NOTE: no Makefile found, unable to determine what needs to be done
  do_configure () {
          # Specify any needed configure commands here
          :
  }
  do_compile () {
          # Specify compilation commands here
          :
  }
  do_install () {
          # Specify install commands here
          :
  }

  ```
* 修改`recipes-helloworld/helloworld/helloworld_0.1.bb`
  ```
  # will not be in most cases) you must specify the correct value before using this
  # recipe for anything other than initial testing/development!
  LICENSE = "CLOSED"
  LIC_FILES_CHKSUM = ""
  
  SRC_URI = "git://github.com/ZengjfOS/Yocto;protocol=https;branch=helloworld"
  
  inherit deploy
  
  # Modify these as desired
  PV = "0.1+git${SRCPV}"
  SRCREV = "8892d685573f8b7d75e0d9d2755fef697b35df58"
  
  S = "${WORKDIR}/git"
  
  # NOTE: no Makefile found, unable to determine what needs to be done
  
  do_configure () {
          # Specify any needed configure commands here
          :
  }
  
  do_compile () {
          # Specify compilation commands here
          oe_runmake
  }
  
  do_install () {
          # Specify install commands here
          bbdebug 2 ${D}
          oe_runmake install 'DESTDIR=${D}'
  }
  
  do_deploy () {
          bbdebug 2 "DEPLOYDIR: ${DEPLOYDIR}"
          install -d "${DEPLOYDIR}"
  }
  addtask deploy after do_populate_sysroot
  ```
* 编译、安装：
  * `bitbake helloworld -DD -c compile`
  * `bitbake helloworld -DD -c install`
  * `bitbake helloworld -DD -c deploy`
* `tmp/work/aarch64-poky-linux/helloworld/0.1+gitAUTOINC+8892d68557-r0`
* `imx8-build-wayland/conf/local.conf`中添加`helloworld`
* `bitbake fsl-image-qt5-validation-imx`
* `imx8-build-wayland/tmp/work/imx8qmmek-poky-linux/fsl-image-qt5-validation-imx/1.0-r0/rootfs/usr/bin/hello`