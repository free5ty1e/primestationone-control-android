#!/bin/bash

wait-for-device-ready

function wait-for-device-ready {
    local bootanim=""
    local failcounter=0
    until [[ "$bootanim" =~ "stopped" ]]; do
       bootanim=`adb hell getprop init.svc.bootanim 2>&1`
       echo "$bootanim"
       if [[ "$bootanim" =~ "not found" ]]; then
          let "failcounter += 1"
          if [[ ${failcounter} -gt 1200 ]]; then
            echo "Failed to start emulator"
            exit 1
          fi
       fi
       sleep 1
    done
    sleep 30
    adb shell input keyevent 82 &
    sleep 150
    echo "Done"
}