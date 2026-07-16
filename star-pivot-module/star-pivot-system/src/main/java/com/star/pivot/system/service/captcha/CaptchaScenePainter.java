package com.star.pivot.system.service.captcha;

import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 验证码背景绘制（滑块 / 点选共用）
 */
public final class CaptchaScenePainter {

    private static final Color[][] PALETTES = {
            {new Color(14, 165, 168), new Color(56, 189, 200), new Color(8, 110, 130)},
            {new Color(37, 99, 235), new Color(96, 165, 250), new Color(30, 64, 175)},
            {new Color(20, 140, 160), new Color(125, 211, 220), new Color(15, 90, 110)},
            {new Color(59, 130, 246), new Color(147, 197, 253), new Color(29, 78, 216)}
    };

    private CaptchaScenePainter() {
    }

    public static BufferedImage paint(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        ThreadLocalRandom random = ThreadLocalRandom.current();
        Color[] palette = PALETTES[random.nextInt(PALETTES.length)];

        g.setPaint(new GradientPaint(0, 0, palette[1], 0, height, palette[0]));
        g.fillRect(0, 0, width, height);

        drawHills(g, width, height, palette[2], 0.55f, 18);
        drawHills(g, width, height, darker(palette[0], 0.85f), 0.68f, 28);
        drawHills(g, width, height, darker(palette[2], 0.7f), 0.82f, 36);

        for (int i = 0; i < 8; i++) {
            int r = 18 + random.nextInt(40);
            g.setColor(new Color(255, 255, 255, 18 + random.nextInt(28)));
            g.fillOval(random.nextInt(width), random.nextInt(Math.max(1, height / 2)), r, r);
        }

        g.setStroke(new BasicStroke(1.6f));
        for (int i = 0; i < 3; i++) {
            g.setColor(new Color(255, 255, 255, 35 + i * 15));
            GeneralPath wave = new GeneralPath();
            int baseY = height - 28 - i * 14;
            wave.moveTo(0, baseY);
            for (int x = 0; x <= width; x += 20) {
                double y = baseY + Math.sin((x + i * 40) * 0.04) * (6 + i * 2);
                wave.lineTo(x, y);
            }
            g.draw(wave);
        }

        for (int i = 0; i < 60; i++) {
            g.setColor(new Color(255, 255, 255, 20 + random.nextInt(40)));
            g.fillOval(random.nextInt(width), random.nextInt(height), 2, 2);
        }

        g.dispose();
        return image;
    }

    private static void drawHills(Graphics2D g, int width, int height, Color color, float heightRatio, int amplitude) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int baseY = (int) (height * heightRatio);
        GeneralPath path = new GeneralPath();
        path.moveTo(0, height);
        path.lineTo(0, baseY);
        int phase = random.nextInt(100);
        for (int x = 0; x <= width; x += 12) {
            double y = baseY + Math.sin((x + phase) * 0.03) * amplitude
                    + Math.cos((x + phase) * 0.015) * (amplitude * 0.4);
            path.lineTo(x, y);
        }
        path.lineTo(width, height);
        path.closePath();
        g.setColor(color);
        g.fill(path);
    }

    private static Color darker(Color color, float factor) {
        return new Color(
                Math.max(0, Math.min(255, (int) (color.getRed() * factor))),
                Math.max(0, Math.min(255, (int) (color.getGreen() * factor))),
                Math.max(0, Math.min(255, (int) (color.getBlue() * factor)))
        );
    }
}
