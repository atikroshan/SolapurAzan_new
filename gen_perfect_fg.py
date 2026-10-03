scale = 0.85
offset_x = (1080 - 1000 * scale) / 2
offset_y = (1080 - 1000 * scale) / 2

def T(x, y):
    nx = int(round(x * scale + offset_x))
    ny = int(round(y * scale + offset_y))
    return f"{nx},{ny}"

fg_xml = f'''<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="1080"
    android:viewportHeight="1080">

    <!-- Top Diamond Finial (Gold) -->
    <path
        android:fillColor="#C89D5D"
        android:pathData="M{T(500, 65)} L{T(548, 115)} L{T(500, 165)} L{T(452, 115)} Z" />

    <!-- Golden Dome Arch -->
    <path
        android:fillColor="#C89D5D"
        android:pathData="M {T(500, 185)}
                          C {T(460, 205)} {T(340, 250)} {T(240, 340)}
                          C {T(170, 420)} {T(172, 490)} {T(233, 538)}
                          C {T(280, 450)} {T(370, 330)} {T(450, 265)}
                          C {T(480, 250)} {T(495, 248)} {T(500, 248)}
                          C {T(505, 248)} {T(520, 250)} {T(550, 265)}
                          C {T(630, 330)} {T(720, 450)} {T(767, 538)}
                          C {T(828, 490)} {T(830, 420)} {T(760, 340)}
                          C {T(660, 250)} {T(540, 205)} {T(500, 185)} Z" />

    <!-- Left White Swoosh Ribbon -->
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M {T(386, 380)}
                          C {T(395, 415)} {T(420, 455)} {T(452, 490)}
                          C {T(460, 560)} {T(420, 680)} {T(320, 780)}
                          C {T(220, 870)} {T(120, 920)} {T(70, 935)}
                          C {T(50, 830)} {T(75, 710)} {T(150, 600)}
                          C {T(235, 490)} {T(325, 415)} {T(386, 380)} Z" />

    <!-- Right White Swoosh Ribbon -->
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M {T(614, 380)}
                          C {T(605, 415)} {T(580, 455)} {T(548, 490)}
                          C {T(540, 560)} {T(580, 680)} {T(680, 780)}
                          C {T(780, 870)} {T(880, 920)} {T(930, 935)}
                          C {T(950, 830)} {T(925, 710)} {T(850, 600)}
                          C {T(765, 490)} {T(675, 415)} {T(614, 380)} Z" />
</vector>
'''

with open("app/src/main/res/drawable/ic_launcher_foreground.xml", "w") as f:
    f.write(fg_xml)

print("Generated exact vector ic_launcher_foreground.xml")
