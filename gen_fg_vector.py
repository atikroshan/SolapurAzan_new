# Scale from 1000x1000 to 1080x1080 with 0.76 scale and (160, 160) offset
scale = 0.76
offset_x = 160
offset_y = 160

def T(x, y):
    nx = int(round(x * scale + offset_x))
    ny = int(round(y * scale + offset_y))
    return f"{nx},{ny}"

# Diamond
d_top = T(500, 65)
d_right = T(548, 115)
d_bot = T(500, 165)
d_left = T(452, 115)

fg_xml = f'''<?xml version="1.0" encoding="utf-8"?>
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="1080"
    android:viewportHeight="1080">

    <!-- Top Diamond Finial (Gold) -->
    <path
        android:fillColor="#C89D5D"
        android:pathData="M{d_top} L{d_right} L{d_bot} L{d_left} Z" />

    <!-- Golden Dome Arch -->
    <path
        android:fillColor="#C89D5D"
        android:pathData="M {T(500, 185)}
                          C {T(490, 205)} {T(385, 240)} {T(290, 320)}
                          C {T(200, 395)} {T(170, 480)} {T(172, 538)}
                          C {T(185, 530)} {T(200, 480)} {T(260, 410)}
                          C {T(330, 330)} {T(420, 280)} {T(500, 252)}
                          C {T(580, 280)} {T(670, 330)} {T(740, 410)}
                          C {T(800, 480)} {T(815, 530)} {T(828, 538)}
                          C {T(830, 480)} {T(800, 395)} {T(710, 320)}
                          C {T(615, 240)} {T(510, 205)} {T(500, 185)} Z" />

    <!-- Left White Swoosh Ribbon -->
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M {T(388, 380)}
                          C {T(388, 430)} {T(350, 490)} {T(300, 535)}
                          C {T(230, 600)} {T(120, 675)} {T(60, 770)}
                          C {T(45, 800)} {T(48, 880)} {T(72, 935)}
                          C {T(80, 870)} {T(125, 800)} {T(195, 730)}
                          C {T(285, 640)} {T(375, 520)} {T(405, 450)}
                          C {T(420, 415)} {T(410, 390)} {T(388, 380)} Z" />

    <!-- Right White Swoosh Ribbon -->
    <path
        android:fillColor="#FFFFFF"
        android:pathData="M {T(612, 380)}
                          C {T(612, 430)} {T(650, 490)} {T(700, 535)}
                          C {T(770, 600)} {T(880, 675)} {T(940, 770)}
                          C {T(955, 800)} {T(952, 880)} {T(928, 935)}
                          C {T(920, 870)} {T(875, 800)} {T(805, 730)}
                          C {T(715, 640)} {T(625, 520)} {T(595, 450)}
                          C {T(580, 415)} {T(590, 390)} {T(612, 380)} Z" />
</vector>
'''

with open("app/src/main/res/drawable/ic_launcher_foreground.xml", "w") as f:
    f.write(fg_xml)

print("Generated vector ic_launcher_foreground.xml")
