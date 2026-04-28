#!/bin/bash
# KALC POS Build Script
# Works on Linux, macOS, and Windows (Git Bash)

set -e

echo "=== KALC POS Build Script ==="
echo ""

# Check Java is available
if ! command -v javac &> /dev/null; then
    echo "ERROR: javac not found. Please install JDK 11+"
    echo "Download from: https://adoptium.net/"
    exit 1
fi

# Clean previous build
rm -rf bin
mkdir -p bin
echo "✓ Created bin/ directory"

# Determine classpath separator based on OS
case "$(uname -s)" in
    MINGW*|CYGWIN*|MSYS*) SEP=';' ;;
    *) SEP=':' ;;
esac

# Build classpath using Java's wildcard expansion
CP="bin${SEP}lib/*"
echo "✓ Using classpath separator: $SEP"

# Find all Java source files
echo "Scanning source files..."
find src-pos src-beans src-data -name "*.java" > sources.txt
SOURCE_COUNT=$(wc -l < sources.txt)
echo "✓ Found $SOURCE_COUNT Java source files"

# Compile
echo ""
echo "Compiling..."
javac -encoding UTF-8 -d bin -cp "$CP" @sources.txt 2>&1 | tee compile.log

if [ ${PIPESTATUS[0]} -eq 0 ]; then
    echo ""
    echo "✓ Compilation successful!"
else
    echo ""
    echo "✗ Compilation failed. See compile.log for details"
    exit 1
fi

# Create JAR
echo ""
echo "Creating kalc.jar..."
jar cvf kalc.jar -C bin . > /dev/null
echo "✓ kalc.jar created successfully"

echo ""
echo "=== Build Complete ==="
echo "Run:"
echo "  java -cp \"kalc.jar${SEP}lib/*\" ke.kalc.pos.forms.StartPOS"
echo ""
echo "Or double-click: kalc.jar (if associated with Java)"
echo ""
