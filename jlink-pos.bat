
rem jdeps --class-path ./dist/lib/*  -recursive --multi-release 11 ./dist/lib/kalc.jar > files.txt

rmdir /s /q jre

jlink --add-modules javafx.base,javafx.controls,javafx.swing,javafx.graphics,java.management,java.base,java.desktop,java.logging,java.xml,java.sql,java.rmi,java.naming,java.prefs --output jre --compress=2 --no-header-files --no-man-pages --strip-debug