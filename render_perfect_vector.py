WIDTH = 1024
HEIGHT = 1024

BG = (19, 77, 55)       # #134D37
GOLD = (200, 157, 93)   # #C89D5D
WHITE = (255, 255, 255) # #FFFFFF

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

# Diamond
diamond = [S(500, 65), S(548, 115), S(500, 165), S(452, 115)]

# Golden Dome
dome_poly = []
# Peak to left outer curve
dome_poly.extend([S(x, y) for x, y in get_bezier_curve((500, 185), (460, 205), (340, 250), (240, 340), 40)])
dome_poly.extend([S(x, y) for x, y in get_bezier_curve((240, 340), (170, 420), (172, 490), (233, 538), 40)])
# Left tip to inner peak
dome_poly.extend([S(x, y) for x, y in get_bezier_curve((233, 538), (280, 450), (370, 330), (450, 265), 40)])
dome_poly.extend([S(x, y) for x, y in get_bezier_curve((450, 265), (480, 250), (495, 248), (500, 248), 20)])
# Inner peak to right tip
dome_poly.extend([S(x, y) for x, y in get_bezier_curve((500, 248), (505, 248), (520, 250), (550, 265), 20)])
dome_poly.extend([S(x, y) for x, y in get_bezier_curve((550, 265), (630, 330), (720, 450), (767, 538), 40)])
# Right tip to peak
dome_poly.extend([S(x, y) for x, y in get_bezier_curve((767, 538), (828, 490), (830, 420), (760, 340), 40)])
dome_poly.extend([S(x, y) for x, y in get_bezier_curve((760, 340), (660, 250), (540, 205), (500, 185), 40)])

# Left White Ribbon:
# Top tip at (386, 380)
# Inner curve going down into the center arch:
# from (386, 380) down-right to (452, 480), then down-left along inner arch to (70, 935)
left_white = []
# Inner contour:
left_white.extend([S(x, y) for x, y in get_bezier_curve((386, 380), (395, 415), (420, 455), (452, 490), 30)])
left_white.extend([S(x, y) for x, y in get_bezier_curve((452, 490), (460, 560), (420, 680), (320, 780), 40)])
left_white.extend([S(x, y) for x, y in get_bezier_curve((320, 780), (220, 870), (120, 920), (70, 935), 40)])
# Outer contour (from bottom tip 70, 935 back up to 386, 380):
left_white.extend([S(x, y) for x, y in get_bezier_curve((70, 935), (50, 830), (75, 710), (150, 600), 40)])
left_white.extend([S(x, y) for x, y in get_bezier_curve((150, 600), (235, 490), (325, 415), (386, 380), 40)])

# Right White Ribbon (Exact Mirror across X=500):
right_white = []
def mirror(p):
    return (1000 - p[0], p[1])

# We can mirror the left_white polygon points
for p in left_white:
    # unscale, mirror, rescale
    orig_x = p[0] * 1000.0 / WIDTH
    orig_y = p[1] * 1000.0 / HEIGHT
    mx, my = mirror((orig_x, orig_y))
    right_white.append(S(mx, my))

# Fill canvas grid
grid = [list(BG) for _ in range(WIDTH * HEIGHT)]

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

out = bytearray()
for p in grid:
    out.extend(p)

with open("perfect_logo.ppm", "wb") as f:
    f.write(f"P6\n{WIDTH} {HEIGHT}\n255\n".encode())
    f.write(out)

print("Rendered perfect_logo.ppm successfully!")
