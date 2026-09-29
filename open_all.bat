@echo off
echo Opening Mobile Money System...
start http://localhost:8080/dashboard.html
timeout /t 2 /nobreak >nul
start http://localhost:8080/admin.html