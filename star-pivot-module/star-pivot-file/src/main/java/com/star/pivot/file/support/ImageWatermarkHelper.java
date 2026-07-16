package com.star.pivot.file.support;

import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Locale;

/**
 * 对图片字节流叠加平铺文字水印。
 */
public final class ImageWatermarkHelper {

    private ImageWatermarkHelper() {
    }

    public static boolean isSupportedFormat(String fileExt, String contentType) {
        String format = resolveFormat(fileExt, contentType);
        return format != null;
    }

    public static byte[] apply(
            byte[] source,
            String fileExt,
            String contentType,
            String text,
            int fontSize,
            Color color,
            int rotate,
            int gapX,
            int gapY) throws Exception {
        if (source == null || source.length == 0 || !StringUtils.hasText(text)) {
            return source;
        }
        String format = resolveFormat(fileExt, contentType);
        if (format == null) {
            return source;
        }

        BufferedImage src = ImageIO.read(new ByteArrayInputStream(source));
        if (src == null) {
            return source;
        }

        int width = src.getWidth();
        int height = src.getHeight();
        int imageType = src.getColorModel().hasAlpha()
                ? BufferedImage.TYPE_INT_ARGB
                : BufferedImage.TYPE_INT_RGB;
        BufferedImage canvas = new BufferedImage(width, height, imageType);
        Graphics2D g = canvas.createGraphics();
        try {
            g.drawImage(src, 0, 0, null);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, Math.max(10, fontSize)));
            g.setColor(color);

            FontMetrics metrics = g.getFontMetrics();
            int textW = Math.max(metrics.stringWidth(text), 1);
            int textH = Math.max(metrics.getHeight(), 1);
            int stepX = Math.max(gapX, textW + 40);
            int stepY = Math.max(gapY, textH + 40);

            AffineTransform origin = g.getTransform();
            double radians = Math.toRadians(rotate);
            for (int y = -height; y < height * 2; y += stepY) {
                for (int x = -width; x < width * 2; x += stepX) {
                    g.setTransform(origin);
                    g.rotate(radians, x, y);
                    g.drawString(text, x, y);
                }
            }
            g.setTransform(origin);
        } finally {
            g.dispose();
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        String writeFormat = "jpg".equals(format) ? "jpeg" : format;
        if (!ImageIO.write(canvas, writeFormat, out)) {
            // 回退 PNG
            out.reset();
            ImageIO.write(canvas, "png", out);
        }
        return out.toByteArray();
    }

    public static Color parseColor(String color, float defaultAlpha) {
        if (!StringUtils.hasText(color)) {
            return new Color(0f, 0f, 0f, defaultAlpha);
        }
        String c = color.trim().toLowerCase(Locale.ROOT);
        try {
            if (c.startsWith("rgba(") && c.endsWith(")")) {
                String[] parts = c.substring(5, c.length() - 1).split(",");
                if (parts.length == 4) {
                    int r = clamp(Integer.parseInt(parts[0].trim()));
                    int g = clamp(Integer.parseInt(parts[1].trim()));
                    int b = clamp(Integer.parseInt(parts[2].trim()));
                    float a = Float.parseFloat(parts[3].trim());
                    return new Color(r / 255f, g / 255f, b / 255f, Math.min(1f, Math.max(0f, a)));
                }
            }
            if (c.startsWith("rgb(") && c.endsWith(")")) {
                String[] parts = c.substring(4, c.length() - 1).split(",");
                if (parts.length == 3) {
                    int r = clamp(Integer.parseInt(parts[0].trim()));
                    int g = clamp(Integer.parseInt(parts[1].trim()));
                    int b = clamp(Integer.parseInt(parts[2].trim()));
                    return new Color(r, g, b, Math.round(defaultAlpha * 255));
                }
            }
            if (c.startsWith("#")) {
                String hex = c.substring(1);
                if (hex.length() == 6) {
                    int rgb = Integer.parseInt(hex, 16);
                    return new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF,
                            Math.round(defaultAlpha * 255));
                }
            }
        } catch (Exception ignored) {
            // fall through
        }
        return new Color(0f, 0f, 0f, defaultAlpha);
    }

    private static int clamp(int v) {
        return Math.min(255, Math.max(0, v));
    }

    private static String resolveFormat(String fileExt, String contentType) {
        if (StringUtils.hasText(fileExt)) {
            String ext = fileExt.toLowerCase(Locale.ROOT);
            if (ext.startsWith(".")) {
                ext = ext.substring(1);
            }
            return switch (ext) {
                case "jpg", "jpeg" -> "jpg";
                case "png" -> "png";
                case "gif" -> "gif";
                case "bmp" -> "bmp";
                default -> null;
            };
        }
        if (StringUtils.hasText(contentType)) {
            String ct = contentType.toLowerCase(Locale.ROOT);
            if (ct.contains("jpeg") || ct.contains("jpg")) {
                return "jpg";
            }
            if (ct.contains("png")) {
                return "png";
            }
            if (ct.contains("gif")) {
                return "gif";
            }
            if (ct.contains("bmp")) {
                return "bmp";
            }
        }
        return null;
    }
}
