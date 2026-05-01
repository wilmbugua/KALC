#!/bin/bash
# Terminal ID Generator for KALCPOS
# Generates a unique identifier for this installation

ID_FILE=".terminal-id"

if [ -f "$ID_FILE" ]; then
  echo "Terminal ID already exists: $(cat $ID_FILE)"
else
  if command -v uuidgen &> /dev/null; then
    TERMINAL_ID=$(uuidgen)
  else
    # Fallback: generate using /dev/urandom
    TERMINAL_ID=$(cat /dev/urandom | tr -dc 'a-f0-9' | head -c 36)
    TERMINAL_ID="${TERMINAL_ID:0:8}-${TERMINAL_ID:8:4}-${TERMINAL_ID:12:4}-${TERMINAL_ID:16:4}-${TERMINAL_ID:20:12}"
  fi
  echo "$TERMINAL_ID" > "$ID_FILE"
  echo "Terminal ID generated: $TERMINAL_ID"
fi
