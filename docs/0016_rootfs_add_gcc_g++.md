# rootfs add gcc g++

## 参考文档

* [Add gcc, g++ to custom minimal image](https://community.nxp.com/thread/441681)

## 处理方法

`IMAGE_INSTALL += " packagegroup-core-buildessential"`

## 注意事项

貌似会导致文件系统打包的时候出现问题，很多指令没有打包到文件系统