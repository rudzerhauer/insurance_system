#!/usr/bin/env bash
set -euo pipefail

# Usage: ./scripts/strip-comments.sh [output-zip]
OUT=${1:-../project-no-comments.zip}

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
TMPDIR=$(mktemp -d)
COPYDIR="$TMPDIR/project"

echo "Copying project to temporary dir: $COPYDIR"
rsync -a --exclude 'node_modules' --exclude '.git' --exclude 'dist' --exclude 'target' "$ROOT/" "$COPYDIR/"

echo "Stripping comments from source files..."
# Remove block comments (/* ... */) and HTML comments (<!-- ... -->) and lines that start with //
find "$COPYDIR" -type f \( -name "*.java" -o -name "*.ts" -o -name "*.js" -o -name "*.css" -o -name "*.scss" -o -name "*.html" -o -name "*.xml" -o -name "*.properties" -o -name "*.kt" -o -name "*.groovy" \) -print0 |
while IFS= read -r -d '' file; do
  # remove block comments /* ... */ safely
  perl -0777 -pe 's:/\*.*?\*/::gs' -i "$file"
  # remove HTML comments <!-- ... -->
  perl -0777 -pe 's:<!--.*?-->::gs' -i "$file"
  # remove lines that start with optional whitespace then // (keep inline // after code to avoid breaking URLs)
  sed -i.bak -E '/^[[:space:]]*\/\//d' "$file" && rm -f "$file.bak"
done

echo "Creating zip $OUT"
cd "$TMPDIR"
zip -r "$OUT" project > /dev/null

echo "Zip created: $OUT"
echo "Temporary files are in $TMPDIR (removed on reboot)."
