@echo off
chcp 65001 >nul
setlocal enabledelayedexpansion

rem — 환경에 맞게 수정 —
set PYTHON=C:\Users\Zoe_Lowell\Documents\GitHub\ssafy_home\.venv2\Scripts\python.exe
set SCRIPT=C:\Users\Zoe_Lowell\Documents\GitHub\ssafy_home\크롤링.py
set TOTAL=40448
set DIVS=8

rem CHUNKSIZE 계산 (올림)
set /A CHUNKSIZE=(TOTAL + DIVS - 1) / DIVS
echo [INFO] 총 %TOTAL%건, %DIVS%등분 시 한 구간에 %CHUNKSIZE%건

for /L %%i in (1,1,%DIVS%) do (
    echo.
    echo [INFO] 구간 %%i/%DIVS% 시작…

    rem 전체 커맨드를 큰따옴표로 감싸면 잘립니다.
    start "" cmd /k "%PYTHON% %SCRIPT% --chunk-size %CHUNKSIZE% --chunk-index %%i && echo 완료: %%i & pause"

    timeout /t 5 /nobreak >nul
)

endlocal
