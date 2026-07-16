package com.star.pivot.system.service.captcha;

import com.star.pivot.framework.cache.RedisCache;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.system.constants.CaptchaTypes;
import com.star.pivot.system.domain.CaptchaState;
import com.star.pivot.system.domain.bo.CaptchaIssueResponse;
import com.star.pivot.system.domain.bo.CaptchaVerifyRequest;
import com.star.pivot.system.service.interfaces.ISysConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 滑块拼图验证码
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SliderCaptchaProvider implements CaptchaProvider {

    static final int BG_WIDTH = 360;
    static final int BG_HEIGHT = 180;
    static final int BLOCK_SIZE = 50;
    private static final int KNOB_R = 10;
    private static final int MAX_ATTEMPTS = 5;
    private static final int TOLERANCE = 6;

    private static final Color[][] PALETTES = {
            {new Color(14, 165, 168), new Color(56, 189, 200), new Color(8, 110, 130)},
            {new Color(37, 99, 235), new Color(96, 165, 250), new Color(30, 64, 175)},
            {new Color(20, 140, 160), new Color(125, 211, 220), new Color(15, 90, 110)},
            {new Color(59, 130, 246), new Color(147, 197, 253), new Color(29, 78, 216)}
    };

    private final RedisCache redisCache;
    private final ISysConfigService sysConfigService;

    @Override
    public String getType() {
        return CaptchaTypes.SLIDER;
    }

    @Override
    public CaptchaIssueResponse generate(String scene) {
        var random = CaptchaSupport.secureRandom();
        int minX = BLOCK_SIZE + 20;
        int maxX = BG_WIDTH - BLOCK_SIZE - 20;
        int targetX = minX + random.nextInt(Math.max(1, maxX - minX + 1));
        int sliderY = 28 + random.nextInt(Math.max(1, BG_HEIGHT - BLOCK_SIZE - 50));

        BufferedImage background = createBackground();
        BufferedImage slider = cutSliderBlock(background, targetX, sliderY);
        drawHole(background, targetX, sliderY);

        String captchaToken = CaptchaSupport.randomToken();
        CaptchaState state = new CaptchaState();
        state.setAttempts(0);
        state.setMaxAttempts(MAX_ATTEMPTS);
        state.setScene(scene);
        state.setCaptchaType(CaptchaTypes.SLIDER);
        state.setTargetX(targetX);

        String key = CaptchaSupport.tokenKey(captchaToken);
        int expireSeconds = sysConfigService.getCaptchaExpireSeconds();
        try {
            redisCache.setCacheObject(key, state, expireSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("存储滑块验证码失败，key={}", key, e);
            throw new BizException(ErrorCode.CAPTCHA_GENERATE_ERROR, "验证码生成失败，请检查Redis连接: " + e.getMessage());
        }

        CaptchaIssueResponse response = new CaptchaIssueResponse();
        response.setCaptchaType(CaptchaTypes.SLIDER);
        response.setCaptchaToken(captchaToken);
        response.setBackgroundImage(CaptchaSupport.toJpegDataUrl(background));
        response.setSliderImage(CaptchaSupport.toPngDataUrl(slider));
        response.setSliderY(sliderY);
        return response;
    }

    @Override
    public boolean matches(CaptchaVerifyRequest request, CaptchaState state) {
        if (request == null || request.getSliderX() == null || state.getTargetX() == null) {
            return false;
        }
        return Math.abs(request.getSliderX() - state.getTargetX()) <= TOLERANCE;
    }

    private BufferedImage createBackground() {
        BufferedImage image = new BufferedImage(BG_WIDTH, BG_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        ThreadLocalRandom random = ThreadLocalRandom.current();
        Color[] palette = PALETTES[random.nextInt(PALETTES.length)];

        // 天空渐变
        g.setPaint(new GradientPaint(0, 0, palette[1], 0, BG_HEIGHT, palette[0]));
        g.fillRect(0, 0, BG_WIDTH, BG_HEIGHT);

        // 远景山峦
        drawHills(g, palette[2], 0.55f, 18);
        drawHills(g, darker(palette[0], 0.85f), 0.68f, 28);
        drawHills(g, darker(palette[2], 0.7f), 0.82f, 36);

        // 柔光圆点 / 光斑
        for (int i = 0; i < 8; i++) {
            int r = 18 + random.nextInt(40);
            g.setColor(new Color(255, 255, 255, 18 + random.nextInt(28)));
            g.fillOval(random.nextInt(BG_WIDTH), random.nextInt(BG_HEIGHT / 2), r, r);
        }

        // 近景波浪线
        g.setStroke(new BasicStroke(1.6f));
        for (int i = 0; i < 3; i++) {
            g.setColor(new Color(255, 255, 255, 35 + i * 15));
            GeneralPath wave = new GeneralPath();
            int baseY = BG_HEIGHT - 28 - i * 14;
            wave.moveTo(0, baseY);
            for (int x = 0; x <= BG_WIDTH; x += 20) {
                double y = baseY + Math.sin((x + i * 40) * 0.04) * (6 + i * 2);
                wave.lineTo(x, y);
            }
            g.draw(wave);
        }

        // 细碎纹理点
        for (int i = 0; i < 60; i++) {
            g.setColor(new Color(255, 255, 255, 20 + random.nextInt(40)));
            g.fillOval(random.nextInt(BG_WIDTH), random.nextInt(BG_HEIGHT), 2, 2);
        }

        g.dispose();
        return image;
    }

    private void drawHills(Graphics2D g, Color color, float heightRatio, int amplitude) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int baseY = (int) (BG_HEIGHT * heightRatio);
        GeneralPath path = new GeneralPath();
        path.moveTo(0, BG_HEIGHT);
        path.lineTo(0, baseY);
        int phase = random.nextInt(100);
        for (int x = 0; x <= BG_WIDTH; x += 12) {
            double y = baseY + Math.sin((x + phase) * 0.03) * amplitude
                    + Math.cos((x + phase) * 0.015) * (amplitude * 0.4);
            path.lineTo(x, y);
        }
        path.lineTo(BG_WIDTH, BG_HEIGHT);
        path.closePath();
        g.setColor(color);
        g.fill(path);
    }

    private Color darker(Color color, float factor) {
        return new Color(
                Math.max(0, Math.min(255, (int) (color.getRed() * factor))),
                Math.max(0, Math.min(255, (int) (color.getGreen() * factor))),
                Math.max(0, Math.min(255, (int) (color.getBlue() * factor)))
        );
    }

    /**
     * 经典拼图块：主体圆角矩形 + 上凸起 + 右凸起 - 左凹陷
     */
    private Shape createBlockShape(int x, int y) {
        int body = BLOCK_SIZE - KNOB_R;
        Area area = new Area(new RoundRectangle2D.Double(x + KNOB_R, y + KNOB_R, body, body, 8, 8));

        // 上凸起
        area.add(new Area(new Ellipse2D.Double(
                x + KNOB_R + (body - KNOB_R * 2) / 2.0,
                y,
                KNOB_R * 2.0,
                KNOB_R * 2.0)));

        // 右凸起
        area.add(new Area(new Ellipse2D.Double(
                x + KNOB_R + body - KNOB_R,
                y + KNOB_R + (body - KNOB_R * 2) / 2.0,
                KNOB_R * 2.0,
                KNOB_R * 2.0)));

        // 左凹陷（从主体挖空）
        area.subtract(new Area(new Ellipse2D.Double(
                x,
                y + KNOB_R + (body - KNOB_R * 2) / 2.0,
                KNOB_R * 2.0,
                KNOB_R * 2.0)));

        return area;
    }

    private BufferedImage cutSliderBlock(BufferedImage background, int x, int y) {
        int width = BLOCK_SIZE + KNOB_R;
        int height = BLOCK_SIZE;
        BufferedImage block = new BufferedImage(width + 4, height + 4, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = block.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Shape shape = createBlockShape(0, 0);
        g.setColor(new Color(0, 0, 0, 45));
        g.translate(2, 3);
        g.fill(shape);
        g.translate(-2, -3);

        g.setClip(shape);
        g.drawImage(background, -x, -y, null);
        g.setClip(null);

        g.setStroke(new BasicStroke(2f));
        g.setColor(new Color(255, 255, 255, 230));
        g.draw(shape);
        g.setStroke(new BasicStroke(1f));
        g.setColor(new Color(0, 0, 0, 55));
        g.draw(shape);
        g.dispose();
        return block;
    }

    private void drawHole(BufferedImage background, int x, int y) {
        Graphics2D g = background.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Shape shape = createBlockShape(x, y);

        // 半透明挖空 + 内阴影感
        g.setColor(new Color(0, 0, 0, 95));
        g.fill(shape);
        g.setColor(new Color(0, 0, 0, 45));
        g.setStroke(new BasicStroke(4f));
        g.draw(shape);
        g.setStroke(new BasicStroke(1.5f));
        g.setColor(new Color(255, 255, 255, 110));
        g.draw(shape);
        g.dispose();
    }
}
