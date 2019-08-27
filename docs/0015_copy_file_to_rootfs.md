# copy file to rootfs

## 处理流程

* `mkdir tmp/deploy/images/imx8qmmek/copy_files`
* `touch tmp/deploy/images/imx8qmmek/copy_files/zengjf`
* `cat poky/meta/classes/image_types.bbclass`
  ```
  [...省略]
  oe_mkext234fs () {
      fstype=$1
      extra_imagecmd=""
      if [ $# -gt 1 ]; then
          shift
          extra_imagecmd=$@
      fi

      if [ -d ${DEPLOY_DIR_IMAGE}/copy_files ]; then
          cp ${DEPLOY_DIR_IMAGE}/copy_files/* ${IMAGE_ROOTFS}/ -rf                      # copy file to rootfs
      fi

      # If generating an empty image the size of the sparse block should be large
      # enough to allocate an ext4 filesystem using 4096 bytes per inode, this is
      # about 60K, so dd needs a minimum count of 60, with bs=1024 (bytes per IO)
      eval local COUNT=\"0\"
      eval local MIN_COUNT=\"60\"
      if [ $ROOTFS_SIZE -lt $MIN_COUNT ]; then
          eval COUNT=\"$MIN_COUNT\"
      fi
      # Create a sparse image block
      bbdebug 1 Executing "dd if=/dev/zero of=${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.$fstype seek=$ROOTFS_SIZE count=$COUNT bs=1024"
      dd if=/dev/zero of=${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.$fstype seek=$ROOTFS_SIZE count=$COUNT bs=1024
      bbdebug 1 "Actual Rootfs size:  `du -s ${IMAGE_ROOTFS}`"
      bbdebug 1 "Actual Partion size: `stat -c '%s' ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.$fstype`"
      bbdebug 1 Executing "mkfs.$fstype -F $extra_imagecmd ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.$fstype -d ${IMAGE_ROOTFS}"
      mkfs.$fstype -F $extra_imagecmd ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.$fstype -d ${IMAGE_ROOTFS}
      # Error codes 0-3 indicate successfull operation of fsck (no errors or errors corrected)
      fsck.$fstype -pvfD ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.$fstype || [ $? -le 3 ]
  }
  [...省略]
  ```
* `bitbake core-image-minimal`
* `ls tmp/work/imx8qmmek-poky-linux/core-image-minimal/1.0-r0/rootfs`
  ```
  bin  boot  dev  etc  home  lib  media  mnt  opt  proc  run  sbin  sys  tmp  usr  var  zengjf
  ```