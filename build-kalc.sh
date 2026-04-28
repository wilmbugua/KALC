#!/bin/bash
# KALC POS Build Script
# Usage: ./build-kalc.sh (Linux/macOS) or run in Git Bash on Windows

set -e

echo "=== KALC POS Build Script ==="
echo ""

# Check Java is available
if ! command -v javac &> /dev/null; then
    echo "ERROR: javac not found. Please install JDK 11+"
    echo "Download from: https://adoptium.net/"
    exit 1
fi

# Create output directory
mkdir -p bin
echo "✓ Created bin/ directory"

# Build classpath from all JARs in lib/
CP="bin"
for jar in lib/*.jar; do
    CP="$CP:$jar"
done
echo "✓ Classpath built with ${#lib} JARs"

# Find all Java source files
echo "Scanning source files..."
find src-pos src-beans src-data -name "*.java" > sources.txt
SOURCE_COUNT=$(wc -l < sources.txt)
echo "✓ Found $SOURCE_COUNT Java source files"

# Compile
echo ""
echo "Compiling..."
javac -encoding UTF-8 -cp "$CP" -d bin @sources.txt 2>&1 | tee compile.log

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

# Summary
echo ""
echo "=== Build Summary ==="
echo "Output: kalc.jar"
echo "Classes: $(find bin -name "*.class" | wc -l) .class files"
echo ""
echo "To run:"
echo "  java -cp \"kalc.jar:lib/*\" ke.kalc.pos.forms.StartPOS"
echo ""
echo "Build Complete!"
