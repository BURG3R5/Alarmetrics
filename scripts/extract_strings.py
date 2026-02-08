import glob
import os
import re

# TODO: adb pull ((adb shell pm path com.example.app).Split(":")[1])
# TODO: java -jar apktool.jar d *.apk -f -o temp

OUTPUT_FILE = "strings.txt"

PATTERN = re.compile(
    r'<string\s+name="example_key">\s*(.*?)\s*</string>',
    re.DOTALL
)

with open(OUTPUT_FILE, "w", encoding="utf-8") as out:
    for path in glob.glob("temp/res/values-*/strings.xml", recursive=True):
        dirname = os.path.basename(os.path.dirname(path))

        if not dirname.startswith("values-"):
            continue

        language = dirname[len("values-"):]

        with open(path, "r", encoding="utf-8") as f:
            content = f.read()

        for match in PATTERN.finditer(content):
            value = match.group(1)
            value = value.replace("(", "\\\\(")
            value = value.replace(")", "\\\\)")
            # TODO: Turn resource string into regex pattern. Alarm title should be captured, but time shouldn't.
            out.write(f"{language}:: {value}\n")
