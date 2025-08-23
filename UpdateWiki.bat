@echo off
setlocal EnableExtensions

for /f %%b in ('git rev-parse --abbrev-ref HEAD') do set BRANCH=%%b
for /f %%u in ('git remote get-url origin') do set RURL=%%u
for /f "tokens=1-4 delims=/:@" %%a in ("%RURL%") do set OWNER=%%c& set REPO=%%d
if /i "%REPO:~-4%"==".git" set REPO=%REPO:~0,-4%

for /f %%x in ('git ls-remote --heads origin %BRANCH% ^| find /c /v ""') do set HASREMOTE=%%x
if "%HASREMOTE%"=="0" (
  git push -u origin "%BRANCH%" || (echo Push failed.& exit /b 1)
)

gh auth status >NUL 2>&1 || (echo Not authenticated. Run: gh auth login & exit /b 1)

set WF=build-docs.yml
gh workflow run "%WF%" --ref "%BRANCH%" --repo "%OWNER%/%REPO%" >NUL 2>&1 || (
  set WF=build-docs.yaml
  gh workflow run "%WF%" --ref "%BRANCH%" --repo "%OWNER%/%REPO%" >NUL 2>&1 || (
    echo Could not find workflow build-docs.yml or build-docs.yaml in %OWNER%/%REPO%.
    echo Available workflows:
    gh workflow list --repo "%OWNER%/%REPO%"
    exit /b 1
  )
)

echo Triggered %OWNER%/%REPO% on %BRANCH% using %WF%.
gh run watch --repo "%OWNER%/%REPO%" --exit-status

endlocal