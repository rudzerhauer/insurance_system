#!/bin/bash

echo "Removing all comments from project files..."

# Find all source files (excluding node_modules, .git, .metadata, target, bin directories)
find . -type f \( -name "*.java" -o -name "*.ts" -o -name "*.js" -o -name "*.css" -o -name "*.html" \) \
  ! -path "*/node_modules/*" \
  ! -path "*/.git/*" \
  ! -path "*/.metadata/*" \
  ! -path "*/target/*" \
  ! -path "*/bin/*" \
  ! -path "*/dist/*" \
  ! -path "*/build/*" | while read -r file; do
  
  echo "Processing: $file"
  
  # Remove HTML comments (<!-- ... -->)
  perl -0777 -pe 's/<!--.*?-->//gs' -i "$file"
  
  # Remove multi-line block comments (/* ... */)
  perl -0777 -pe 's:/\*.*?\*/::gs' -i "$file"
  
  # Remove single-line comments (//)
  perl -pe 's://.*$::' -i "$file"
  
  # Remove empty lines (optional, to clean up)
  perl -i -ne 'print if /\S/' "$file"
  
done

echo "Done! All comments removed."
