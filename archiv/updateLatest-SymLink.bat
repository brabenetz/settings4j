rmdir latest
mklink /D latest 2.1-SNAPSHOT
rmdir current
mklink /D current 2.1-SNAPSHOT
cd ..
rmdir currentrelease
mklink /D currentrelease archiv\2.1-SNAPSHOT
cd archiv
