import os

INPUT_FILE = "strings.txt"
OUTPUT_BASE = "./app/src/main/res"

STRING_TEMPLATE = '    <string name="example_app">{text}</string>\n'

XML_HEADER = '<?xml version="1.0" encoding="utf-8"?>\n'
RESOURCES_OPEN = '<resources>\n'
RESOURCES_CLOSE = '</resources>\n'

with open(INPUT_FILE, "r", encoding="utf-8") as f:
    lines = f.readlines()

for line in lines:
    line = line.strip()
    if not line or "::" not in line:
        continue

    lang, text = map(str.strip, line.split("::", 1))

    out_dir = os.path.join(OUTPUT_BASE, f"values-{lang}")
    os.makedirs(out_dir, exist_ok=True)

    out_file = os.path.join(out_dir, "patterns.xml")
    string_line = STRING_TEMPLATE.format(text=text)

    if not os.path.exists(out_file):
        with open(out_file, "w", encoding="utf-8") as f:
            f.write(XML_HEADER)
            f.write(RESOURCES_OPEN)
            f.write(string_line)
            f.write(RESOURCES_CLOSE)
    else:
        with open(out_file, "r", encoding="utf-8") as f:
            content = f.readlines()

        new_content = []
        inserted = False

        for line in content:
            new_content.append(line)
            if not inserted and "<resources>" in line:
                new_content.append(string_line)
                inserted = True

        with open(out_file, "w", encoding="utf-8") as f:
            f.writelines(new_content)
