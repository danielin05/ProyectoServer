# Change to the directory where the script is located
Set-Location $PSScriptRoot

# Set MAVEN_OPTS environment variable
$env:MAVEN_OPTS="--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.nio=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED"

# Check for the first argument and set it as the main class, or use a default if not provided
if ($args.Count -gt 0) {
    $mainClass = $args[0]
} else {
    Write-Host "Error: No main class specified."
    exit 1
}

Write-Host "Setting MAVEN_OPTS to: $env:MAVEN_OPTS"
Write-Host "Main Class: $mainClass"

# Construct Maven argument for the main class
$mavenMainClassArg = "-Dexec.mainClass=$mainClass"

# Check if additional arguments are provided
$javaArgsStr = ""
if ($args.Count -gt 1) {
    # Get the rest of the arguments (excluding the first one for mainClass)
    $javaArgsArray = $args[1..($args.Length - 1)]

    # Escape double quotes in the arguments
    $javaArgsArray = $javaArgsArray -replace '"', '\"'

    # Join the arguments with a space, preserving them as separate arguments
    $javaArgsStr = $javaArgsArray -join ' '
}

# Create the exec.args property, enclosing all arguments in double quotes if they exist
$execArgsForJava = ""
if ($javaArgsStr) {
    $execArgsForJava = "-Dexec.args=`"$javaArgsStr`""
}

Write-Host "Maven Main Class Argument: $mavenMainClassArg"
Write-Host "Java Program Arguments: $javaArgsStr"

# Execute the Maven commands
mvn clean compile test

# Check if additional arguments for Java are present and adjust the command accordingly
if ($execArgsForJava) {
    mvn exec:java -PrunMain $mavenMainClassArg $execArgsForJava
} else {
    mvn exec:java -PrunMain $mavenMainClassArg
}
