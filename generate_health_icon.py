#!/usr/bin/env python3
"""Generate health app icon for '测了么'."""
import struct
import zlib
import math

def create_png(width, height, color_fn):
    raw = b''
    for y in range(height):
        raw += b'\x00'
        for x in range(width):
            r, g, b, a = color_fn(x, y, width, height)
            raw += bytes([r, g, b, a])
    def chunk(ctype, data):
        c = ctype + data
        return struct.pack('>I', len(data)) + c + struct.pack('>I', zlib.crc32(c) & 0xffffffff)
    png = b'\x89PNG\r\n\x1a\n'
    png += chunk(b'IHDR', struct.pack('>IIBBBBB', width, height, 8, 6, 0, 0, 0))
    png += chunk(b'IDAT', zlib.compress(raw))
    png += chunk(b'IEND', b'')
    return png

def health_icon(x, y, w, h):
    cx, cy = w // 2, h // 2
    border = 4
    corner_r = border + 2
    corners = [(border, border), (w-border, border), (border, h-border), (w-border, h-border)]
    for corner in corners:
        if math.sqrt((x - corner[0])**2 + (y - corner[1])**2) > corner_r:
            if (x < border and y < border) or (x >= w-border and y < border) or (x < border and y >= h-border) or (x >= w-border and y >= h-border):
                return (0, 0, 0, 0)
    dist = math.sqrt((x - cx)**2 + (y - cy)**2)
    max_dist = math.sqrt(cx**2 + cy**2)
    t = dist / max_dist
    bg_r = int(20 - t * 8)
    bg_g = int(35 - t * 12)
    bg_b = int(50 - t * 18)

    # Heart shape
    heart_cx = cx
    heart_cy = int(h * 0.42)
    heart_scale = w * 0.25
    # Normalize to heart equation
    nx = (x - heart_cx) / heart_scale
    ny = (heart_cy - y) / heart_scale + 0.3
    # Heart formula: (x^2 + y^2 - 1)^3 - x^2*y^3 < 0
    heart_val = (nx*nx + ny*ny - 1)**3 - nx*nx * ny*ny*ny
    if heart_val < 0:
        # Gradient inside heart
        r = min(255, int(220 + (1 - dist / max_dist) * 35))
        g = int(80 + (1 - dist / max_dist) * 40)
        b = int(80 + (1 - dist / max_dist) * 40)
        # Heart outline
        if heart_val > -0.1:
            r, g, b = int(r * 0.5), int(g * 0.4), int(b * 0.4)
        return (r, g, b, 255)

    # ECG line (heartbeat wave)
    ecg_y = int(h * 0.72)
    ecg_width = int(w * 0.7)
    ecg_left = int(w * 0.15)
    if abs(y - ecg_y) < 3:
        progress = (x - ecg_left) / max(1, ecg_width)
        if 0 <= progress <= 1:
            # PQRST wave
            wave = 0
            if 0.1 < progress < 0.2: wave = math.sin((progress - 0.1) * 10 * math.pi) * 8
            elif 0.3 < progress < 0.35: wave = -10
            elif 0.35 < progress < 0.45: wave = 25
            elif 0.45 < progress < 0.5: wave = -15
            elif 0.6 < progress < 0.7: wave = math.sin((progress - 0.6) * 10 * math.pi) * 6
            if abs(y - (ecg_y - int(wave))) < 2:
                return (100, 200, 180, 220)

    # Small cross/plus sign (medical)
    cross_cx = int(w * 0.82)
    cross_cy = int(h * 0.22)
    cross_r = int(w * 0.08)
    if abs(x - cross_cx) < cross_r and abs(y - cross_cy) < cross_r:
        if abs(x - cross_cx) < 3 or abs(y - cross_cy) < 3:
            return (255, 255, 255, 200)

    # Stars
    stars = [(0.12, 0.15), (0.88, 0.12), (0.15, 0.85), (0.85, 0.88)]
    for sx, sy in stars:
        star_x = int(w * sx); star_y = int(h * sy)
        if math.sqrt((x - star_x)**2 + (y - star_y)**2) < int(w * 0.02):
            return (200, 220, 255, 180)

    return (bg_r, bg_g, bg_b, 255)

sizes = {'mdpi': 48, 'hdpi': 72, 'xhdpi': 96, 'xxhdpi': 144, 'xxxhdpi': 192}
output_dir = '/root/health-tracker/android/app/src/main/res'
import os

for density, size in sizes.items():
    png_data = create_png(size, size, health_icon)
    path = os.path.join(output_dir, f'mipmap-{density}', 'ic_launcher.png')
    with open(path, 'wb') as f:
        f.write(png_data)
    print(f'Created {path} ({size}x{size}, {len(png_data)} bytes)')
    path_round = os.path.join(output_dir, f'mipmap-{density}', 'ic_launcher_round.png')
    with open(path_round, 'wb') as f:
        f.write(png_data)

print('\nAll health icons generated!')
