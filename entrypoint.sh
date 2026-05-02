#!/bin/sh
# KALCPOS Docker Entrypoint
# Builds classpath and starts the application

# Change to app directory
cd /app

# Generate Terminal ID if not present (for cross-machine identification)
ID_FILE=".terminal-id"
if [ ! -f "$ID_FILE" ]; then
    if command -v uuidgen >/dev/null 2>&1; then
        TERMINAL_ID=$(uuidgen)
    else
        # Fallback: generate using /dev/urandom
        TERMINAL_ID=$(cat /dev/urandom | tr -dc 'a-f0-9' | head -c 36)
        TERMINAL_ID="${TERMINAL_ID:0:8}-${TERMINAL_ID:8:4}-${TERMINAL_ID:12:4}-${TERMINAL_ID:16:4}-${TERMINAL_ID:20:12}"
    fi
    echo "$TERMINAL_ID" > "$ID_FILE"
    echo "Generated Terminal ID: $TERMINAL_ID"
else
    echo "Terminal ID: $(cat $ID_FILE)"
fi

# Build classpath: kalc.jar + all JARs in lib/
CLASSPATH="kalc.jar"
for jar in lib/*.jar; do
    CLASSPATH="$CLASSPATH:$jar"
done

# Start the application
exec java -cp "$CLASSPATH" ke.kalc.pos.forms.StartPOS
