@echo off
	
rem comfile
javac -encoding UTF-8 Subway.java

rem make folder
MKDIR my_output

echo "-execute your program-"

echo Start Measure %Time%

for /l %%i in (1, 1, 62) do (
	SETLOCAL EnableDelayedExpansion
	for /f "delims=" %%t in (testset\argument\%%i.txt) do (
		set a=%%t)
						
	java -Dfile.encoding=UTF-8 Subway !a! < testset\input\%%i.txt > my_output\%%i.txt
)

REM for /l %%i in (31, 1, 61) do (
REM 	SETLOCAL EnableDelayedExpansion
REM 	for /f "delims=" %%t in (testset\argument\%%i.txt) do (
REM 		set b=%%t)
						
REM 	java -Dfile.encoding=UTF-8 Subway !b! < testset\input\%%i.txt > my_output\%%i.txt
REM )


echo Stop Measure %Time% 

echo "-print wrong answer-"

rem compare output

for /l %%i in (1, 1, 62) do (
	fc my_output\%%i.txt testset\output\%%i.txt	
)


pause