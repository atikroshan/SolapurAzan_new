import os

svg_content = """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 680 200" width="100%" height="100%" style="overflow: visible;">
  <defs>
    <!-- Metallic Gold 0-100% Vertical Gradient as specified in HTML -->
    <linearGradient id="goldGradient" x1="0%" y1="0%" x2="0%" y2="100%">
      <stop offset="0%" stop-color="#F3DE8E" />
      <stop offset="40%" stop-color="#F3D66A" />
      <stop offset="100%" stop-color="#B37C3C" />
    </linearGradient>

    <!-- Golden Atmospheric Glow Filter -->
    <filter id="goldGlow" x="-20%" y="-20%" width="140%" height="140%">
      <feGaussianBlur stdDeviation="3.5" result="blur" />
      <feColorMatrix type="matrix" values="
        1 0 0 0 0.99
        0 0.85 0 0 0.84
        0 0 0.4 0 0.38
        0 0 0 0.6 0" in="blur" result="glow" />
      <feMerge>
        <feMergeNode in="glow" />
        <feMergeNode in="SourceGraphic" />
      </feMerge>
    </filter>

    <!-- White Punchline Drop Shadow -->
    <filter id="whiteGlow" x="-20%" y="-20%" width="140%" height="140%">
      <feDropShadow dx="0" dy="1.5" stdDeviation="2.5" flood-color="#ffffff" flood-opacity="0.35" />
    </filter>
  </defs>

  <g filter="url(#goldGlow)">
    <!-- ==================== AZAN ==================== -->
    <!-- A1 -->
    <path d="M 18,140 L 14,146 L 22,148 L 26,140 L 40,32 L 48,16 L 56,32 L 70,140 L 74,148 L 82,146 L 78,140 L 62,140 L 59,114 L 37,114 L 34,140 Z M 48,46 L 41,96 L 55,96 Z" fill="url(#goldGradient)" />
    <!-- Z -->
    <path d="M 88,38 L 94,22 L 152,22 L 156,38 L 114,124 L 156,124 L 160,116 L 162,140 L 156,146 L 90,146 L 86,130 L 128,44 L 88,44 Z" fill="url(#goldGradient)" />
    <!-- A2 -->
    <path d="M 168,140 L 164,146 L 172,148 L 176,140 L 190,32 L 198,16 L 206,32 L 220,140 L 224,148 L 232,146 L 228,140 L 212,140 L 209,114 L 187,114 L 184,140 Z M 198,46 L 191,96 L 205,96 Z" fill="url(#goldGradient)" />
    <!-- N -->
    <path d="M 238,140 L 234,146 L 244,148 L 248,140 L 248,38 L 238,24 L 254,22 L 290,118 L 290,38 L 282,24 L 302,22 L 306,38 L 306,140 L 310,148 L 300,148 L 262,48 L 262,140 Z" fill="url(#goldGradient)" />

    <!-- ==================== TIME ==================== -->
    <!-- T -->
    <path d="M 346,38 L 350,22 L 424,22 L 428,38 L 396,44 L 396,140 L 392,148 L 400,148 L 404,140 L 404,44 Z M 382,44 L 382,140 L 378,146 L 388,148 L 392,140 L 392,44 Z" fill="url(#goldGradient)" />
    <!-- I -->
    <path d="M 436,38 L 442,22 L 458,22 L 464,38 L 464,140 L 468,148 L 458,148 L 454,140 L 454,38 L 446,38 Z" fill="url(#goldGradient)" />
    <!-- M -->
    <path d="M 474,140 L 470,146 L 480,148 L 484,140 L 484,38 L 476,24 L 492,22 L 522,106 L 552,22 L 568,24 L 560,38 L 560,140 L 564,148 L 554,148 L 550,140 L 550,56 L 526,128 L 518,128 L 494,56 L 494,140 Z" fill="url(#goldGradient)" />
    <!-- E -->
    <path d="M 576,38 L 582,22 L 642,22 L 646,38 L 598,42 L 598,72 L 634,72 L 638,88 L 598,88 L 598,126 L 646,126 L 650,118 L 652,142 L 646,146 L 580,146 L 576,132 Z" fill="url(#goldGradient)" />

    <!-- Serrated Base Teeth Under AZAN TIME -->
    <path d="M 22,148 L 30,154 L 38,148 L 46,154 L 54,148 L 62,154 L 70,148 L 78,154 L 86,148 L 94,154 L 102,148 L 110,154 L 118,148 L 126,154 L 134,148 L 142,154 L 150,148 L 158,154 L 166,148 L 174,154 L 182,148 L 190,154 L 198,148 L 206,154 L 214,148 L 222,154 L 230,148 L 238,154 L 246,148 L 254,154 L 262,148 L 270,154 L 278,148 L 286,154 L 294,148 L 302,154 L 310,148
             M 346,148 L 354,154 L 362,148 L 370,154 L 378,148 L 386,154 L 394,148 L 402,154 L 410,148 L 418,154 L 426,148 L 434,154 L 442,148 L 450,154 L 458,148 L 466,154 L 474,148 L 482,154 L 490,148 L 498,154 L 506,148 L 514,154 L 522,148 L 530,154 L 538,148 L 546,154 L 554,148 L 562,154 L 570,148 L 578,154 L 586,148 L 594,154 L 602,148 L 610,154 L 618,148 L 626,154 L 634,148 L 642,154 L 650,148"
          stroke="url(#goldGradient)" stroke-width="2.2" stroke-linecap="round" fill="none" />
  </g>

  <!-- ==================== PUNCHLINE ==================== -->
  <text x="334" y="184" text-anchor="middle"
        font-family="'Arial Narrow', Arial, 'DejaVu Sans', sans-serif"
        font-size="19" font-weight="700" letter-spacing="2.2"
        fill="#FFFFFF" filter="url(#whiteGlow)"
        opacity="0.96">"AAO ALLAH KI RAAH MEIN CHALE"</text>
</svg>"""

os.makedirs('app/src/main/assets', exist_ok=True)
with open('app/src/main/assets/azan_app_logo.svg', 'w') as f:
    f.write(svg_content)

print("Saved app/src/main/assets/azan_app_logo.svg, size:", len(svg_content))
