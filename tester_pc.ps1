Add-Type -AssemblyName System.Speech

Clear-Host
Write-Host "======================================================================" -ForegroundColor Yellow
Write-Host " PROBADOR DE VOZ DE PC (CON RECONCILIACION PHONETIC CORE LOGPOSE)" -ForegroundColor Yellow
Write-Host "======================================================================" -ForegroundColor Yellow
Write-Host "El microfono de tu PC esta activo. Habla una frase..." -ForegroundColor Gray
Write-Host "(Ejemplos: 'pone duki', 'llevame a la ypf', 'como esta el clima')" -ForegroundColor Gray
Write-Host ""

try {
    $recognizer = New-Object System.Speech.Recognition.SpeechRecognitionEngine
    $grammar = New-Object System.Speech.Recognition.DictationGrammar
    $recognizer.LoadGrammar($grammar)
    $recognizer.SetInputToDefaultAudioDevice()

    Write-Host "Microfono de PC conectado con exito. Escuchando..." -ForegroundColor Green
    Write-Host ""

    while ($true) {
        $result = $recognizer.Recognize()
        if ($result -and $result.Text) {
            $rawText = $result.Text.ToLower().Trim()
            Write-Host "ESCUCHADO RAW : '$rawText'" -ForegroundColor White
            
            # Reconciliación Fonética ALF-R (Mapeo de alucinaciones conocidas de Windows)
            $text = $rawText
            if ($text -match "lucky|to kill|hornee tonky|educ y|poneuki|droga lo ponen") {
                $text = "pone duki"
                Write-Host "RECONCILIADO : '$text' (Filtro Fonetico Duki)" -ForegroundColor Yellow
            }
            elseif ($text -match "isía|isi a|easy a|ysya") {
                $text = "ysy a"
                Write-Host "RECONCILIADO : '$text' (Filtro Fonetico YSY A)" -ForegroundColor Yellow
            }

            if ($text -match "pone|reproduce|reproduci|play|duki|musica|cancion") {
                $query = $text -replace "pone|reproduce|reproduci|play|musica", ""
                $query = $query.Trim()
                if ([string]::IsNullOrWhiteSpace($query)) { $query = "duki" }
                Write-Host "INTENCION     : MUSICA Y AUDIO (PLAY_MUSIC)" -ForegroundColor Cyan
                Write-Host "ACCION TEORICA: Reproducir '$query' en Spotify/YouTube Music" -ForegroundColor Cyan
            }
            elseif ($text -match "llevame|lleva|guiame|navegar|ir a|ypf|maps") {
                Write-Host "INTENCION     : NAVEGACION GPS (NAVIGATE)" -ForegroundColor Cyan
                Write-Host "ACCION TEORICA: Abrir Google Maps" -ForegroundColor Cyan
            }
            elseif ($text -match "clima|pronostico|llover|tiempo|temperatura") {
                Write-Host "INTENCION     : CLIMA Y RUTA (GET_WEATHER)" -ForegroundColor Cyan
                Write-Host "ACCION TEORICA: Informar pronostico del tiempo" -ForegroundColor Cyan
            }
            elseif ($text -match "silenciar|silencio|ignorar|mute|shh") {
                Write-Host "INTENCION     : TELEFONIA (SILENCE_CALL)" -ForegroundColor Cyan
                Write-Host "ACCION TEORICA: Silenciar timbre de llamada" -ForegroundColor Cyan
            }
            elseif ($text -match "abrir|abre|abri|instagram") {
                Write-Host "INTENCION     : CONTROL DE APPS (OPEN_APP)" -ForegroundColor Cyan
                Write-Host "ACCION TEORICA: Abrir aplicacion" -ForegroundColor Cyan
            }
            else {
                Write-Host "INTENCION     : RECONOCIDO GENERAL" -ForegroundColor Gray
            }
            Write-Host "----------------------------------------------------------------------" -ForegroundColor Gray
        }
    }
} catch {
    Write-Host "Error al acceder al microfono de Windows: $($_.Exception.Message)" -ForegroundColor Red
}
