#!/bin/sh

# 转换平台签名命令
# 这个工具的作用是将系统签名的相关信息导入到已有的签名文件里。
./keytool-importkeypair -k ../signature/demo.jks -p 123456 -pk8 platform.pk8 -cert platform.x509.pem -alias demo
#keytool-importkeypair -k ~/.android/debug.keystore -p android -pk8 platform.pk8 -cert platform.x509.pem -alias platform
# demo.jks : 签名文件

# 123456 : 签名文件密码

# platform.pk8、platform.x509.pem : 系统签名文件

# demo : 签名文件别名

#为脚本文件添加可执行权限：

#$ sudo chmod a+x signature.sh

#执行脚本：

#$ ./signature.sh