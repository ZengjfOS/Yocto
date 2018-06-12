# Read Manual

https://www.yoctoproject.org/docs/1.5.1/dev-manual/dev-manual.html

## Layer Configuration File

Through the use of the BBPATH variable, BitBake locates .bbclass files, configuration files, and files that are included with include and require statements. For these cases, BitBake uses the first file that matches the name found in BBPATH. This is similar to the way the PATH variable is used for binaries. We recommend, therefore, that you use unique .bbclass and configuration filenames in your custom layer.

## Layer Add Content

Depending on the type of layer, add the content. If the layer adds support for a machine, add the machine configuration in a conf/machine/ file within the layer. If the layer adds distro policy, add the distro configuration in a conf/distro/ file within the layer. If the layer introduces new recipes, put the recipes you need in recipes-* subdirectories within the layer.
