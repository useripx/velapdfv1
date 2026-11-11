<#
.SYNOPSIS
    Requirements Specification Integrity Checker
.DESCRIPTION
    Memvalidasi apakah berkas spesifikasi kebutuhan memiliki format REQ-F-xxx / REQ-NF-xxx
    dan kriteria penerimaan terukur.
#>
param(
    [Parameter(Mandatory=$true)]
    [string]$FilePath
)

if (!(Test-Path $FilePath)) {
    Write-Error "File not found: $FilePath"
    exit 1
}

$Content = Get-Content $FilePath -Raw
$HasReqId = ($Content -match 'REQ-(F|NF)-[0-9]{3}')
$HasAcceptanceCriteria = ($Content -match '(?i)kriteria penerimaan|acceptance criteria')

if ($HasReqId -and $HasAcceptanceCriteria) {
    Write-Host "[PASS] Dokumen kebutuhan valid dan terstruktur." -ForegroundColor Green
    exit 0
} else {
    Write-Warning "Dokumen kebutuhan belum memiliki ID unik atau Kriteria Penerimaan."
    exit 1
}
