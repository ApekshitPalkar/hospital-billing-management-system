#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BUILD_DIR="$ROOT_DIR/target/validation-tests"
MAIN_CLASSES="$BUILD_DIR/main"
TEST_CLASSES="$BUILD_DIR/test"

if ! command -v javac >/dev/null 2>&1 || ! command -v java >/dev/null 2>&1; then
  echo "A JDK (Java 17 or later) is required to run validation checks." >&2
  exit 1
fi

rm -rf "$BUILD_DIR"
mkdir -p "$MAIN_CLASSES" "$TEST_CLASSES"

mapfile -d '' MAIN_SOURCES < <(find "$ROOT_DIR/src/main/java" -type f -name '*.java' -print0)
javac --release 17 -encoding UTF-8 -d "$MAIN_CLASSES" "${MAIN_SOURCES[@]}"

javac --release 17 -encoding UTF-8 -cp "$MAIN_CLASSES" -d "$TEST_CLASSES" \
  "$ROOT_DIR/src/test/java/com/apekshit/hospital/service/BillingServiceValidationTest.java"

java -cp "$MAIN_CLASSES:$TEST_CLASSES" com.apekshit.hospital.service.BillingServiceValidationTest
