#!/usr/bin/env bash
set -euo pipefail

example_dir="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"
jdk_home="${1:?Usage: run.sh JDK_HOME [true|false] [visual|pixels] [snapshot.png]}"
vulkan="${2:-true}"
test_kind="${3:-visual}"
case "$vulkan" in true|false) ;; *) echo 'Vulkan must be true or false' >&2; exit 2 ;; esac
case "$test_kind" in
  visual) test_class=EmojiRenderTest ;;
  pixels) test_class=EmojiPixelTest ;;
  *) echo 'Test must be visual or pixels' >&2; exit 2 ;;
esac

class_dir="$(mktemp -d)"
trap 'rm -rf -- "$class_dir"' EXIT
"$jdk_home/bin/javac" -d "$class_dir" "$example_dir/$test_class.java"
test_args=()
if [[ "$test_kind" == pixels && $# -ge 4 ]]; then test_args+=("$4"); fi
"$jdk_home/bin/java" -Dawt.toolkit.name=WLToolkit "-Dsun.java2d.vulkan=$vulkan" \
  -cp "$class_dir" "$test_class" "${test_args[@]}"
