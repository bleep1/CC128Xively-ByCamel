


export PATH=$PATH:/usr/local/maven/bin

stty -F /dev/ttyUSB0 57600
cat /dev/ttyUSB0 >/home/odroid/mypipe.txt  &
(cd /home/odroid/git/CC128Xively-UsingCamel/cc128-to-xively; mvn camel:run) >/var/log/camelRun.log 2>/var/log/camelRun.err &



