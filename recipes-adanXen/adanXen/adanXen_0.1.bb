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

SRC_URI = "git://github.com/ZengjfOS/Yocto;protocol=https;branch=xen"

# Modify these as desired
PV = "0.1+git${SRCPV}"
SRCREV = "dc4244124d46a37887c1ec196bcf2bbd47ff44a5"
DEPENDS += "imx-xen"

S = "${WORKDIR}/git"

# NOTE: no Makefile found, unable to determine what needs to be done

do_configure () {
	# Specify any needed configure commands here
	:
}

do_compile () {
	bbdebug 2 "DEPLOYDIR: ${DEPLOYDIR}"
	bbdebug 2 "DESTDIR: ${D}"
	# Specify compilation commands here
	oe_runmake
}

do_install () {
	# Specify install commands here
	bbdebug 2 "DEPLOYDIR: ${DEPLOYDIR}"
	bbdebug 2 "DESTDIR: ${D}"
	oe_runmake DESTDIR="${D}" install
}

