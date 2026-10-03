import subprocess
import os

# Source file
src_path = "/app/applet/app/src/main/res/drawable/ic_app_logo.png"
base_dir = "/app/applet/app/src/main/res"
bg_color = "#0E473B"
sizes = {"mdpi": 48, "hdpi": 72, "xhdpi": 96, "xxhdpi": 144, "xxxhdpi": 192}

# Update background color in XML
bg_xml = os.path.join(base_dir, "drawable/ic_launcher_background.xml")
with open(bg_xml, "w") as f:
    f.write(f'<?xml version="1.0" encoding="utf-8"?>\n<vector xmlns:android="http://schemas.android.com/apk/res/android"\n    android:width="108dp"\n    android:height="108dp"\n    android:viewportWidth="108" \n    android:viewportHeight="108">\n    <path android:fillColor="{bg_color}" android:pathData="M0,0h108v108H0z"/>\n</vector>')

# Generate mipmap icons
for density, size in sizes.items():
    dest_dir = os.path.join(base_dir, f"mipmap-{density}")
    os.makedirs(dest_dir, exist_ok=True)
    
    # Create combined icon (background + foreground)
    # Using ImageMagick to create solid background then overlay foreground
    cmd = [
        "convert", "-size", f"{size}x{size}", f"xc:{bg_color}",
        "(", src_path, "-resize", f"{int(size*0.8)}x{int(size*0.8)}", "-gravity", "center", ")",
        "-composite", f"PNG32:{os.path.join(dest_dir, 'ic_launcher.png')}"
    ]
    subprocess.run(cmd, check=True)
    
    # Create round version
    cmd = [
        "convert", f"{os.path.join(dest_dir, 'ic_launcher.png')}",
        "(", "+clone", "-fill", "white", "-draw", "circle 0,0 0," + str(size//2), "-alpha", "copy", ")",
        "-compose", "copy-opacity", "-composite", f"PNG32:{os.path.join(dest_dir, 'ic_launcher_round.png')}"
    ]
    subprocess.run(cmd, check=True)

print("Icons updated successfully with background color")
