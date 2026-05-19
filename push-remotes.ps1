# StarPivot: push to Gitee and GitHub separately
# Usage:
#   .\push-remotes.ps1
#   .\push-remotes.ps1 gitee
#   .\push-remotes.ps1 github
#   .\push-remotes.ps1 setup
#   .\push-remotes.ps1 all -Branch develop

param(
    [Parameter(Position = 0)]
    [ValidateSet('all', 'gitee', 'github', 'setup')]
    [string]$Target = 'all',

    [string]$Branch = ''
)

$ErrorActionPreference = 'Stop'

$GiteeUrl = 'https://gitee.com/xin1998/StarPivot.git'
$GithubUrl = 'https://github.com/xinxin-star1998/star-pivot.git'

$RepoRoot = $PSScriptRoot
if (-not (Test-Path (Join-Path $RepoRoot '.git'))) {
    Write-Error 'Not a git repository. Run this script from StarPivot root.'
}

Set-Location $RepoRoot

function Write-Step([string]$Message) {
    Write-Host ''
    Write-Host ">> $Message" -ForegroundColor Cyan
}

function Ensure-Remotes {
    $hasGithub = git remote | Select-String -Pattern '^github$' -Quiet
    if (-not $hasGithub) {
        git remote add github $GithubUrl
        Write-Host "Added remote github -> $GithubUrl"
    }

    $pushUrls = @(git config --get-all remote.origin.pushurl 2>$null)
    if ($pushUrls.Count -gt 1) {
        Write-Host 'Multiple origin pushurl detected. Keeping Gitee on origin; use this script for GitHub.'
        git config --unset-all remote.origin.pushurl 2>$null
        git remote set-url origin $GiteeUrl
        git remote set-url --push origin $GiteeUrl
    }

    $originUrl = git config --get remote.origin.url
    if ($originUrl -ne $GiteeUrl) {
        git remote set-url origin $GiteeUrl
        Write-Host "Set origin to Gitee: $GiteeUrl"
    }

    $githubUrl = git config --get remote.github.url
    if ($githubUrl -ne $GithubUrl) {
        git remote set-url github $GithubUrl
        Write-Host "Set github remote: $GithubUrl"
    }
}

function Get-CurrentBranch {
    $b = git branch --show-current 2>$null
    if ([string]::IsNullOrWhiteSpace($b)) {
        throw 'Cannot detect current branch. Checkout a branch first.'
    }
    return $b
}

function Push-Remote([string]$Name, [string]$Remote) {
    Write-Step "Pushing to $Name ($Remote) ..."
    git push $Remote $script:Branch
    if ($LASTEXITCODE -ne 0) {
        throw "Push to $Name failed (branch: $($script:Branch))."
    }
    Write-Host "[$Name] OK" -ForegroundColor Green
}

if ($Target -eq 'setup') {
    Ensure-Remotes
    Write-Host ''
    Write-Host 'Remotes configured:' -ForegroundColor Green
    git remote -v
    exit 0
}

Ensure-Remotes

if ([string]::IsNullOrWhiteSpace($Branch)) {
    $Branch = Get-CurrentBranch
} else {
    $Branch = $Branch.Trim()
}

Write-Host '========================================'
Write-Host '  StarPivot push (Gitee + GitHub)'
Write-Host "  Branch: $Branch"
Write-Host '========================================'

$failed = @()

if (($Target -eq 'all') -or ($Target -eq 'gitee')) {
    try {
        Push-Remote 'Gitee' 'origin'
    } catch {
        Write-Host $_.Exception.Message -ForegroundColor Red
        $failed += 'Gitee'
    }
}

if (($Target -eq 'all') -or ($Target -eq 'github')) {
    try {
        Push-Remote 'GitHub' 'github'
    } catch {
        Write-Host $_.Exception.Message -ForegroundColor Red
        $failed += 'GitHub'
    }
}

Write-Host ''
if ($failed.Count -eq 0) {
    Write-Host 'All remotes pushed successfully.' -ForegroundColor Green
    exit 0
}

Write-Host "Failed: $($failed -join ', ')" -ForegroundColor Red
Write-Host 'If GitHub says fetch first, run:'
Write-Host "  git fetch github $Branch"
Write-Host "  git merge github/$Branch"
Write-Host '  .\push-remotes.ps1 github'
exit 1
