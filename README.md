# README

* [The Yocto Project Development Manual](https://www.yoctoproject.org/docs/1.5.1/dev-manual/dev-manual.html#dev-manual-intro)
* [L4.1.15_2.0.0_LINUX_DOCS](https://www.nxp.com/webapp/Download?colCode=L4.1.15_2.0.0-LINUX-DOCS&Parent_nodeId=1337695836026701499367&Parent_pageType=product&Parent_nodeId=1337695836026701499367&Parent_pageType=product)

## Install libs

* `sudo apt-get install gawk wget git-core diffstat unzip texinfo gcc-multilib build-essential chrpath socat libsdl1.2-dev`
* `sudo apt-get install libsdl1.2-dev xterm sed cvs subversion coreutils texi2html docbook-utils python-pysqlite2 help2man make gcc g++ desktop-file-utils libgl1-mesa-dev libglu1-mesa-dev mercurial autoconf automake groff curl lzop asciidoc`
* i.MX layers host packages for a Ubuntu 12.04 host setup only are:  
  `sudo apt-get install uboot-mkimage`
* i.MX layers host packages for a Ubuntu 14.04 host setup only are:  
  `sudo apt-get install u-boot-tools`
* Repo: https://github.com/ZoroZeng/manifest

## How to select additional packages

* [YOCTO Customizing Images Doc](https://www.yoctoproject.org/docs/1.5.1/dev-manual/dev-manual.html#usingpoky-extend-customimage)
* Search YOCTO Recipes: https://layers.openembedded.org/layerindex/branch/master/recipes/
* 《i.MX Yocto Project User's Guide.pdf》 -- A.4 How to select additional packages

## Docs

* [0004_Add_New_layer_And_Recipe.md](docs/0004_Add_New_layer_And_Recipe.md)
* [0003_Operation_bb_File.md](docs/0003_Operation_bb_File.md)
* [0002_Read_Manual.md](docs/0002_Read_Manual.md)
* [0001_fsl-setup-release_setup-environment.md](docs/0001_fsl-setup-release_setup-environment.md)