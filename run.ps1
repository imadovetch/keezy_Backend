# Script pour compiler et démarrer l'application Keezy
Write-Host "================================" -ForegroundColor Cyan
Write-Host "Keezy Back - Build & Run Script" -ForegroundColor Cyan
Write-Host "================================" -ForegroundColor Cyan

# Changer vers le répertoire du projet
cd "W:\Outside\Kezzy_New\keezy_back"

# Nettoyer et compiler
Write-Host "`nCleaning previous builds..." -ForegroundColor Yellow
.\mvnw.cmd clean

Write-Host "`nCompiling project..." -ForegroundColor Yellow
.\mvnw.cmd compile

if ($LASTEXITCODE -eq 0) {
    Write-Host "`nCompilation successful! Starting application..." -ForegroundColor Green
    .\mvnw.cmd spring-boot:run
} else {
    Write-Host "`nCompilation failed!" -ForegroundColor Red
    exit 1
}

