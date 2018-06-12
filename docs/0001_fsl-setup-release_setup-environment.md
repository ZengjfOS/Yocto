# fsl-setup-release setup-environment

* `mkdir fsl-release-bsp && cd fsl-release-bsp`
* `repo init -u git://git.freescale.com/imx/fsl-arm-yocto-bsp.git -b imx-4.1-krogoth`
* `repo sync`
* `MACHINE=imx6dlsabresd DISTRO=fsl-imx-x11 source ./fsl-setup-release.sh -b imx6q-x11`
* `bitbake fsl-image-qt5`
* outpu: `imx6q-x11/tmp/deploy/images/imx6dlsabresd`

## fsl-setup-release setup-environment relationship

```Shell
...[省略]
PROGNAME="setup-environment"
...[省略]
# Set up the basic yocto environment
if [ -z "$DISTRO" ]; then
   DISTRO=$FSLDISTRO MACHINE=$MACHINE . ./$PROGNAME $BUILD_DIR
else
   MACHINE=$MACHINE . ./$PROGNAME $BUILD_DIR
fi
...[省略]
```

## fsl-setup-release.sh hacking

```Shell
...[省略]

CWD=`pwd`                           # 获取当前执行脚本的目录
PROGNAME="setup-environment"        # 这里是指定了会调用setup-environment程序
exit_message ()
{
   echo "To return to this build environment later please run:"
   echo "    source setup-environment <build_dir>"

}

usage()
{
    echo -e "\nUsage: source fsl-setup-release.sh
    Optional parameters: [-b build-dir] [-e back-end] [-h]"
echo "
    * [-b build-dir]: Build directory, if unspecified script uses 'build' as output directory
    * [-e back-end]: Options are 'fb', 'dfb', 'x11, 'wayland'
    * [-h]: help
"
}


clean_up()
{

    unset CWD BUILD_DIR BACKEND FSLDISTRO
    unset fsl_setup_help fsl_setup_error fsl_setup_flag
    unset usage clean_up
    unset ARM_DIR META_FSL_BSP_RELEASE
    exit_message clean_up
}

# get command line options
OLD_OPTIND=$OPTIND
unset FSLDISTRO

# 获取命令行参数
while getopts "k:r:t:b:e:gh" fsl_setup_flag
do
    case $fsl_setup_flag in
        b) BUILD_DIR="$OPTARG";
           echo -e "\n Build directory is " $BUILD_DIR
           ;;
        e)
            # Determine what distro needs to be used.
            BACKEND="$OPTARG"
            if [ "$BACKEND" = "fb" ]; then
                if [ -z "$DISTRO" ]; then
                    FSLDISTRO='fsl-imx-fb'
                    echo -e "\n Using FB backend with FB DIST_FEATURES to override poky X11 DIST FEATURES"
                elif [ ! "$DISTRO" = "fsl-imx-fb" ]; then
                    echo -e "\n DISTRO specified conflicts with -e. Please use just one or the other."
                    fsl_setup_error='true'
                fi

            elif [ "$BACKEND" = "dfb" ]; then
                if [ -z "$DISTRO" ]; then
                    FSLDISTRO='fsl-imx-dfb'
                    echo -e "\n Using DirectFB backend with DirectFB DIST_FEATURES to override poky X11 DIST FEATURES"
                elif [ ! "$DISTRO" = "fsl-imx-dfb" ]; then
                    echo -e "\n DISTRO specified conflicts with -e. Please use just one or the other."
                    fsl_setup_error='true'
                fi

            elif [ "$BACKEND" = "wayland" ]; then
                if [ -z "$DISTRO" ]; then
                    FSLDISTRO='fsl-imx-wayland'
                    echo -e "\n Using Wayland backend."
                elif [ ! "$DISTRO" = "fsl-imx-wayland" ]; then
                    echo -e "\n DISTRO specified conflicts with -e. Please use just one or the other."
                    fsl_setup_error='true'
                fi

            elif [ "$BACKEND" = "x11" ]; then
                if [ -z "$DISTRO" ]; then
                    FSLDISTRO='fsl-imx-x11'
                    echo -e  "\n Using X11 backend with poky DIST_FEATURES"
                elif [ ! "$DISTRO" = "fsl-imx-x11" ]; then
                    echo -e "\n DISTRO specified conflicts with -e. Please use just one or the other."
                    fsl_setup_error='true'
                fi

            else
                echo -e "\n Invalid backend specified with -e.  Use fb, dfb, wayland, or x11"
                fsl_setup_error='true'
            fi
           ;;
        h) fsl_setup_help='true';
           ;;
        ?) fsl_setup_error='true';
           ;;
    esac
done


if [ -z "$DISTRO" ]; then
    if [ -z "$FSLDISTRO" ]; then
        FSLDISTRO='fsl-imx-x11'
    fi
else
    FSLDISTRO="$DISTRO"
fi

OPTIND=$OLD_OPTIND

# check the "-h" and other not supported options
if test $fsl_setup_error || test $fsl_setup_help; then
    usage && clean_up && return 1
fi

# 如果一些变量未设置，那就给默认配置
if [ -z "$BUILD_DIR" ]; then
    BUILD_DIR='build'
fi

if [ -z "$MACHINE" ]; then
    echo setting to default machine
    MACHINE='imx6qsabresd'
fi

# New machine definitions may need to be added to the expected location
if [ -d ./sources/meta-freescale ]; then
   cp -r sources/meta-fsl-bsp-release/imx/meta-bsp/conf/machine/* sources/meta-freescale/conf/machine
else
   cp -r sources/meta-fsl-bsp-release/imx/meta-bsp/conf/machine/* sources/meta-fsl-arm/conf/machine
fi

# copy new EULA into community so setup uses latest i.MX EULA
if [ -d ./sources/meta-freescale ]; then
   cp sources/meta-fsl-bsp-release/imx/EULA.txt sources/meta-freescale/EULA
   cp sources/meta-fsl-bsp-release/imx/classes/fsl-eula-unpack.bbclass sources/meta-freescale/classes
else
   cp sources/meta-fsl-bsp-release/imx/EULA.txt sources/meta-fsl-arm/EULA
   cp sources/meta-fsl-bsp-release/imx/classes/fsl-eula-unpack.bbclass sources/meta-fsl-arm/classes
fi

# Set up the basic yocto environment
# 执行setup-environment脚本
if [ -z "$DISTRO" ]; then
   DISTRO=$FSLDISTRO MACHINE=$MACHINE . ./$PROGNAME $BUILD_DIR
else
   MACHINE=$MACHINE . ./$PROGNAME $BUILD_DIR
fi

# Point to the current directory since the last command changed the directory to $BUILD_DIR
BUILD_DIR=.

# 检查文件是否存在
if [ ! -e $BUILD_DIR/conf/local.conf ]; then
    echo -e "\n ERROR - No build directory is set yet. Run the 'setup-environment' script before running this script to create " $BUILD_DIR
    echo -e "\n"
    return 1
fi

# On the first script run, backup the local.conf file
# Consecutive runs, it restores the backup and changes are appended on this one.
if [ ! -e $BUILD_DIR/conf/local.conf.org ]; then
    cp $BUILD_DIR/conf/local.conf $BUILD_DIR/conf/local.conf.org
else
    cp $BUILD_DIR/conf/local.conf.org $BUILD_DIR/conf/local.conf
fi


if [ ! -e $BUILD_DIR/conf/bblayers.conf.org ]; then
    cp $BUILD_DIR/conf/bblayers.conf $BUILD_DIR/conf/bblayers.conf.org
else
    cp $BUILD_DIR/conf/bblayers.conf.org $BUILD_DIR/conf/bblayers.conf
fi


META_FSL_BSP_RELEASE="${CWD}/sources/meta-fsl-bsp-release/imx/meta-bsp"
echo "##Freescale Yocto Project Release layer" >> $BUILD_DIR/conf/bblayers.conf
echo "BBLAYERS += \" \${BSPDIR}/sources/meta-fsl-bsp-release/imx/meta-bsp \"" >> $BUILD_DIR/conf/bblayers.conf
echo "BBLAYERS += \" \${BSPDIR}/sources/meta-fsl-bsp-release/imx/meta-sdk \"" >> $BUILD_DIR/conf/bblayers.conf

echo "BBLAYERS += \" \${BSPDIR}/sources/meta-browser \"" >> $BUILD_DIR/conf/bblayers.conf
echo "BBLAYERS += \" \${BSPDIR}/sources/meta-openembedded/meta-gnome \"" >> $BUILD_DIR/conf/bblayers.conf
echo "BBLAYERS += \" \${BSPDIR}/sources/meta-openembedded/meta-networking \"" >> $BUILD_DIR/conf/bblayers.conf
echo "BBLAYERS += \" \${BSPDIR}/sources/meta-openembedded/meta-python \"" >> $BUILD_DIR/conf/bblayers.conf
echo "BBLAYERS += \" \${BSPDIR}/sources/meta-openembedded/meta-filesystems \"" >> $BUILD_DIR/conf/bblayers.conf

echo "BBLAYERS += \" \${BSPDIR}/sources/meta-qt5 \"" >> $BUILD_DIR/conf/bblayers.conf

echo BSPDIR=$BSPDIR
echo BUILD_DIR=$BUILD_DIR

# Support integrating community meta-freescale instead of meta-fsl-arm
if [ -d ../sources/meta-freescale ]; then
    echo meta-freescale directory found
    # Change settings according to environment
    sed -e "s,meta-fsl-arm\s,meta-freescale ,g" -i conf/bblayers.conf
    sed -e "s,\$.BSPDIR./sources/meta-fsl-arm-extra\s,,g" -i conf/bblayers.conf
fi

cd  $BUILD_DIR
clean_up
unset FSLDISTRO
```

## setup-environment hacking

```Shell
...[省略]

CWD=`pwd`
PROGNAME="setup-environment"

usage()
{
    echo -e "\nUsage: source $PROGNAME <build-dir>
    <build-dir>: specifies the build directory location (required)

If undefined, this script will set \$MACHINE to 'imx6qsabresd'.
"

    ls sources/*/conf/machine/*.conf > /dev/null 2>&1
    if [ $? -eq 0 ]; then
        echo -e "
Supported machines: `echo; ls sources/*/conf/machine/*.conf \
| sed s/\.conf//g | sed -r 's/^.+\///' | xargs -I% echo -e "\t%"`

To build for a machine listed above, run this script as:
MACHINE=<machine> source $PROGNAME <build-dir>
"
    fi
}

clean_up()
{
   unset EULA LIST_MACHINES VALID_MACHINE
   unset CWD TEMPLATES SHORTOPTS LONGOPTS ARGS PROGNAME
   unset generated_config updated
   unset MACHINE SDKMACHINE DISTRO OEROOT
}

# get command line options
SHORTOPTS="h"
LONGOPTS="help"

ARGS=$(getopt --options $SHORTOPTS  \
  --longoptions $LONGOPTS --name $PROGNAME -- "$@" )
# Print the usage menu if invalid options are specified
if [ $? != 0 -o $# -lt 1 ]; then
   usage && clean_up
   return 1
fi

eval set -- "$ARGS"
while true;
do
    case $1 in
        -h|--help)
           usage
           clean_up
           return 0
           ;;
        --)
           shift
           break
           ;;
    esac
done

# 不能使用root权限
if [ "$(whoami)" = "root" ]; then
    echo "ERROR: do not use the BSP as root. Exiting..."
fi

# 设置默认机型
if [ -z "$MACHINE" ]; then
    MACHINE='imx6qsabresd'
fi

# Check the machine type specified
LIST_MACHINES=`ls -1 $CWD/sources/*/conf/machine`
VALID_MACHINE=`echo -e "$LIST_MACHINES" | grep ${MACHINE}.conf$ | wc -l`
if [ "x$MACHINE" = "x" ] || [ "$VALID_MACHINE" = "0" ]; then
    echo -e "\nThe \$MACHINE you have specified ($MACHINE) is not supported by this build setup"
    usage && clean_up
    return 1
else
    if [ ! -e $1/conf/local.conf.sample ]; then
        echo "Configuring for ${MACHINE}"
    fi
fi

if [ -z "$SDKMACHINE" ]; then
    SDKMACHINE='i686'
fi

if [ -z "$DISTRO" ]; then
    DISTRO='poky'
fi

OEROOT=$PWD/sources/poky
if [ -e $PWD/sources/oe-core ]; then
    OEROOT=$PWD/sources/oe-core
fi

# Ensure all files in sources/base are kept in sync with project root
updated=
for f in $CWD/sources/base/*; do
    file="$(basename $f)"
    if [ "$file" = "conf" ] || echo $file | grep -q '~$'; then
        continue
    fi

    if ! cmp -s "$file" "$f"; then
        updated="true"
        [ -e $file ] && chmod u+w $file
        cp $f $file
    fi
done
if [ "$updated" = "true" ]; then
    echo "The project root content has been updated. Please run '$PROGNAME' again."
    return
fi

. $OEROOT/oe-init-build-env $CWD/$1 > /dev/null

# if conf/local.conf not generated, no need to go further
if [ ! -e conf/local.conf ]; then
    clean_up && return 1
fi

# Clean up PATH, because if it includes tokens to current directories somehow,
# wrong binaries can be used instead of the expected ones during task execution
export PATH="`echo $PATH | sed 's/\(:.\|:\)*:/:/g;s/^.\?://;s/:.\?$//'`"

generated_config=
if [ ! -e conf/local.conf.sample ]; then
    mv conf/local.conf conf/local.conf.sample

    # Generate the local.conf based on the Yocto defaults
    TEMPLATES=$CWD/sources/base/conf 
    grep -v '^#\|^$' conf/local.conf.sample > conf/local.conf
    cat >> conf/local.conf <<EOF

DL_DIR ?= "\${BSPDIR}/downloads/"
EOF
    # Change settings according environment
    sed -e "s,MACHINE ??=.*,MACHINE ??= '$MACHINE',g" \
        -e "s,SDKMACHINE ??=.*,SDKMACHINE ??= '$SDKMACHINE',g" \
        -e "s,DISTRO ?=.*,DISTRO ?= '$DISTRO',g" \
        -i conf/local.conf

    cp $TEMPLATES/* conf/

    for s in $HOME/.oe $HOME/.yocto; do
        if [ -e $s/site.conf ]; then
            echo "Linking $s/site.conf to conf/site.conf"
            ln -s $s/site.conf conf
        fi
    done

    generated_config=1
fi

# Handle EULA setting
EULA_ACCEPTED=

# EULA has been accepted already (ACCEPT_FSL_EULA is set in local.conf)
if grep -q '^\s*ACCEPT_FSL_EULA\s*=\s*["'\'']..*["'\'']' conf/local.conf; then
    EULA_ACCEPTED=1
fi

if [ -z "$EULA_ACCEPTED" ] && [ -n "$EULA" ]; then
    # The FSL EULA is not set as accepted in local.conf, but the EULA
    # variable is set in the environment, so we just configure
    # ACCEPT_FSL_EULA in local.conf according to $EULA.
    echo "ACCEPT_FSL_EULA = \"$EULA\"" >> conf/local.conf
elif [ -n "$EULA_ACCEPTED" ]; then
    # The FSL EULA has been accepted once, so ACCEPT_FSL_EULA is set
    # in local.conf.  No need to do anything.
    :
else
    # THE FSL EULA is not set as accepted in local.conf, and EULA is
    # not set in the environment, so we need to ask user if he/she
    # accepts the FSL EULA:
    cat <<EOF

Some BSPs depend on libraries and packages which are covered by Freescale's
End User License Agreement (EULA). To have the right to use these binaries in
your images, you need to read and accept the following...

EOF

    sleep 4

    more -d $CWD/sources/meta-fsl-arm/EULA
    echo
    REPLY=
    while [ -z "$REPLY" ]; do
        echo -n "Do you accept the EULA you just read? (y/n) "
        read REPLY
        case "$REPLY" in
            y|Y)
            echo "EULA has been accepted."
            echo "ACCEPT_FSL_EULA = \"1\"" >> conf/local.conf
            ;;
            n|N)
            echo "EULA has not been accepted."
            ;;
            *)
            REPLY=
            ;;
        esac
    done
fi

cat <<EOF

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

EOF

if [ -n "$generated_config" ]; then
    cat <<EOF
Your build environment has been configured with:

    MACHINE=$MACHINE
    SDKMACHINE=$SDKMACHINE
    DISTRO=$DISTRO
    EULA=$EULA
EOF
else
    echo "Your configuration files at $1 have not been touched."
fi

clean_up
```
