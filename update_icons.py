import subprocess
import os

# Copy the file to all mipmap directories
src_path = "/app/applet/app/src/main/res/drawable/ic_app_logo.png"
base_dir = "/app/applet/app/src/main/res"
sizes = {"mdpi": 48, "hdpi": 72, "xhdpi": 96, "xxhdpi": 144, "xxxhdpi": 192}

for density, size in sizes.items():
    dest_dir = os.path.join(base_dir, f"mipmap-{density}")
    os.makedirs(dest_dir, exist_ok=True)
    
    # Use convert to resize and save
    cmd = [
        "convert", src_path,
        "-filter", "Lanczos",
        "-resize", f"{size}x{size}!",
        f"PNG32:{os.path.join(dest_dir, 'ic_launcher.png')}"
    ]
    subprocess.run(cmd, check=True)
    
    cmd = [
        "convert", src_path,
        "-filter", "Lanczos",
        "-resize", f"{size}x{size}!",
        f"PNG32:{os.path.join(dest_dir, 'ic_launcher_round.png')}"
    ]
    subprocess.run(cmd, check=True)

print("Icons updated successfully")
