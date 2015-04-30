@echo off

if %PROCESSOR_ARCHITECTURE%==x86 (
  copy /Y opencv_java246.x86.dll opencv_java246.dll
) else (
  copy /Y opencv_java246.x64.dll opencv_java246.dll
)

java -jar dist/JavaOpenCV.jar
::pause