import math

# We can render an anti-aliased 1000x1000 image of the exact logo
WIDTH = 1000
HEIGHT = 1000

# Colors
BG = (19, 77, 55)       # #134D37
GOLD = (200, 157, 93)   # #C89D5D
WHITE = (255, 255, 255) # #FFFFFF

# Let's test basic PPM generation
pixels = bytearray()
for y in range(HEIGHT):
    for x in range(WIDTH):
        pixels.extend(BG)

with open("test.ppm", "wb") as f:
    f.write(f"P6\n{WIDTH} {HEIGHT}\n255\n".encode())
    f.write(pixels)

print("PPM written")
