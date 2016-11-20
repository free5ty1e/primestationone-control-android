#!/bin/bash
adb devices
j=0
k=0
for i in $(adb devices | awk '{print $1}'); do
if [ "$j" -ne "$k" ]
then
echo item: $i
adb shell pm clear com.chrisprime.primestationonecontrol.debug
adb shell pm clear com.chrisprime.primestationonecontrol.debug.test
adb -s "$i" uninstall com.chrisprime.primestationonecontrol.debug ;
adb -s "$i" uninstall com.chrisprime.primestationonecontrol.debug.test
fi
j=$((j+1))
done
