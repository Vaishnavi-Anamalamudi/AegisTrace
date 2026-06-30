param(
    [switch]$SkipTests
)

$ErrorActionPreference = "Stop"

if ($SkipTests) {
    .\mvnw.cmd --batch-mode --no-transfer-progress clean package -DskipTests
} else {
    .\mvnw.cmd --batch-mode --no-transfer-progress clean verify
}
