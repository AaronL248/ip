#!/usr/bin/env python3
"""Compile and run console UI test cases described in a Markdown test plan."""

from __future__ import annotations

import re
import shutil
import subprocess
import sys
import tempfile
from pathlib import Path


ROOT = Path.cwd()
PLAN_PATH = ROOT / "test" / "ui-test-plan.md"
SOURCE_DIR = ROOT / "src" / "main" / "java"
MAIN_CLASS = "Marcus"
CASE_PATTERN = re.compile(
    r"^## Test Case: (?P<name>.+?)\n.*?^### Input\s*\n```text\n(?P<input>.*?)\n```"
    r"\s*\n.*?^### Expected Output\s*\n```text\n(?P<expected>.*?)\n```",
    re.MULTILINE | re.DOTALL,
)


def normalise(text: str) -> str:
    """Make line-ending differences irrelevant while retaining visible spacing."""
    return text.replace("\r\n", "\n").rstrip("\n")


def print_block(label: str, content: str) -> None:
    """Print a labelled console transcript block."""
    print(f"{label}:")
    print(content if content else "<empty>")


def load_cases() -> list[tuple[str, str, str]]:
    """Read named test cases from the project's Markdown test plan."""
    if not PLAN_PATH.is_file():
        raise FileNotFoundError(f"Test plan not found: {PLAN_PATH}")
    cases = [
        (match.group("name").strip(), match.group("input"), match.group("expected"))
        for match in CASE_PATTERN.finditer(PLAN_PATH.read_text())
    ]
    if not cases:
        raise ValueError("No test cases found. Follow the format in the test-ui skill.")
    return cases


def compile_program(output_dir: Path) -> None:
    """Compile all project Java source files into an isolated directory."""
    sources = sorted(SOURCE_DIR.rglob("*.java"))
    if not sources:
        raise FileNotFoundError(f"No Java files found under {SOURCE_DIR}")
    result = subprocess.run(
        ["javac", "-d", str(output_dir), *map(str, sources)],
        capture_output=True,
        text=True,
    )
    if result.returncode != 0:
        print_block("Compilation error", result.stderr)
        raise RuntimeError("Compilation failed")


def main() -> int:
    """Run all planned cases, stopping immediately after a failed comparison."""
    try:
        cases = load_cases()
    except (FileNotFoundError, ValueError) as error:
        print(error, file=sys.stderr)
        return 2

    build_dir = Path(tempfile.mkdtemp(prefix="ui-test-"))
    try:
        compile_program(build_dir)
        for number, (name, commands, expected) in enumerate(cases, start=1):
            result = subprocess.run(
                ["java", "-cp", str(build_dir), MAIN_CLASS],
                input=commands + "\n",
                capture_output=True,
                text=True,
            )
            print(f"\nTest {number}: {name}")
            print_block("Console input", commands)
            print_block("Console output", result.stdout)

            actual = normalise(result.stdout)
            wanted = normalise(expected)
            if result.returncode != 0 or actual != wanted:
                print("RESULT: FAIL")
                print_block("Expected output", expected)
                print_block("Actual output", result.stdout)
                if result.stderr:
                    print_block("Program error", result.stderr)
                return 1
            print("RESULT: PASS")
    except RuntimeError:
        return 1
    finally:
        shutil.rmtree(build_dir, ignore_errors=True)

    print(f"\nAll {len(cases)} UI test case(s) passed.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
