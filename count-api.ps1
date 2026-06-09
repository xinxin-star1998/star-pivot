$results = Get-ChildItem -Path "E:\star-pivot\project0422\StarPivot\star-pivot-ui\src" -Recurse -File -Include *.ts | Select-String -Pattern "/api/"
$results | Group-Object Path | Select-Object Count, Name | Format-Table
Write-Host "Total: $($results.Count) matches"
