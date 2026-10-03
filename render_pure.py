WIDTH = 1024
HEIGHT = 1024

BG = (19, 77, 55)       # #134D37
GOLD = (200, 157, 93)   # #C89D5D
WHITE = (255, 255, 255) # #FFFFFF

# Create canvas grid using standard python lists / bytearray
grid = [list(BG) for _ in range(WIDTH * HEIGHT)]

def eval_bezier(p0, p1, p2, p3, t):
    t2 = t * t
    t3 = t2 * t
    mt = 1.0 - t
    mt2 = mt * mt
    mt3 = mt2 * mt
    return (
        mt3 * p0[0] + 3 * mt2 * t * p1[0] + 3 * mt * t2 * p2[0] + t3 * p3[0],
        mt3 * p0[1] + 3 * mt2 * t * p1[1] + 3 * mt * t2 * p2[1] + t3 * p3[1]
    )

def get_bezier_curve(p0, p1, p2, p3, n_steps=60):
    pts = []
    for i in range(n_steps + 1):
        t = i / float(n_steps)
        pts.append(eval_bezier(p0, p1, p2, p3, t))
    return pts

def S(x, y):
    return (x * WIDTH / 1000.0, y * HEIGHT / 1000.0)

# Build Gold Polygon
diamond = [S(500, 65), S(548, 115), S(500, 165), S(452, 115)]

dome_poly = []
dome_poly.extend([S(x, y) for x, y in get_bezier_curve((500, 185), (490, 205), (385, 240), (290, 320), 40)])
dome_poly.extend([S(x, y) for x, y in get_bezier_curve((290, 320), (200, 395), (170, 480), (172, 538), 40)])
dome_poly.extend([S(x, y) for x, y in get_bezier_curve((172, 538), (185, 530), (200, 480), (260, 410), 40)])
dome_poly.extend([S(x, y) for x, y in get_bezier_curve((260, 410), (330, 330), (420, 280), (500, 252), 40)])
dome_poly.extend([S(x, y) for x, y in get_bezier_curve((500, 252), (580, 280), (670, 330), (740, 410), 40)])
dome_poly.extend([S(x, y) for x, y in get_bezier_curve((740, 410), (800, 480), (815, 530), (828, 538), 40)])
dome_poly.extend([S(x, y) for x, y in get_bezier_curve((828, 538), (830, 480), (800, 395), (710, 320), 40)])
dome_poly.extend([S(x, y) for x, y in get_bezier_curve((710, 320), (615, 240), (510, 205), (500, 185), 40)])

left_white = []
left_white.extend([S(x, y) for x, y in get_bezier_curve((388, 380), (388, 430), (350, 490), (300, 535), 40)])
left_white.extend([S(x, y) for x, y in get_bezier_curve((300, 535), (230, 600), (120, 675), (60, 770), 40)])
left_white.extend([S(x, y) for x, y in get_bezier_curve((60, 770), (45, 800), (48, 880), (72, 935), 40)])
left_white.extend([S(x, y) for x, y in get_bezier_curve((72, 935), (80, 870), (125, 800), (195, 730), 40)])
left_white.extend([S(x, y) for x, y in get_bezier_curve((195, 730), (285, 640), (375, 520), (405, 450), 40)])
left_white.extend([S(x, y) for x, y in get_bezier_curve((405, 450), (420, 415), (410, 390), (388, 380), 20)])

right_white = []
right_white.extend([S(x, y) for x, y in get_bezier_curve((612, 380), (612, 430), (650, 490), (700, 535), 40)])
right_white.extend([S(x, y) for x, y in get_bezier_curve((700, 535), (770, 600), (880, 675), (940, 770), 40)])
right_white.extend([S(x, y) for x, y in get_bezier_curve((940, 770), (955, 800), (952, 880), (928, 935), 40)])
right_white.extend([S(x, y) for x, y in get_bezier_curve((928, 935), (920, 870), (875, 800), (805, 730), 40)])
right_white.extend([S(x, y) for x, y in get_bezier_curve((805, 730), (715, 640), (625, 520), (595, 450), 40)])
right_white.extend([S(x, y) for x, y in get_bezier_curve((595, 450), (580, 415), (590, 390), (612, 380), 20)])

def fill_polygon(poly, color):
    min_y = max(0, int(min(p[1] for p in poly)))
    max_y = min(HEIGHT - 1, int(max(p[1] for p in poly)))
    n = len(poly)
    
    for y in range(min_y, max_y + 1):
        intersections = []
        for i in range(n):
            p1 = poly[i]
            p2 = poly[(i + 1) % n]
            if (p1[1] <= y < p2[1]) or (p2[1] <= y < p1[1]):
                if p1[1] != p2[1]:
                    x = p1[0] + (y - p1[1]) * (p2[0] - p1[0]) / (p2[1] - p1[1])
                    intersections.append(x)
        intersections.sort()
        for i in range(0, len(intersections) - 1, 2):
            x_start = max(0, int(intersections[i]))
            x_end = min(WIDTH - 1, int(intersections[i + 1]))
            for x in range(x_start, x_end + 1):
                grid[y * WIDTH + x] = list(color)

fill_polygon(diamond, GOLD)
fill_polygon(dome_poly, GOLD)
fill_polygon(left_white, WHITE)
fill_polygon(right_white, WHITE)

# Serialize to PPM
out = bytearray()
for p in grid:
    out.extend(p)

with open("exact_logo.ppm", "wb") as f:
    f.write(f"P6\n{WIDTH} {HEIGHT}\n255\n".encode())
    f.write(out)

print("Rendered exact_logo.ppm successfully!")
