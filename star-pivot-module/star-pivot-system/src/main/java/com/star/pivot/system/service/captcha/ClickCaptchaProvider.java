package com.star.pivot.system.service.captcha;

import com.star.pivot.framework.cache.RedisCache;
import com.star.pivot.framework.exception.BizException;
import com.star.pivot.framework.exception.ErrorCode;
import com.star.pivot.system.constants.CaptchaTypes;
import com.star.pivot.system.domain.CaptchaState;
import com.star.pivot.system.domain.bo.CaptchaClickPoint;
import com.star.pivot.system.domain.bo.CaptchaIssueResponse;
import com.star.pivot.system.domain.bo.CaptchaVerifyRequest;
import com.star.pivot.system.service.interfaces.ISysConfigService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 文字点选验证码：按提示顺序点击图中文字
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ClickCaptchaProvider implements CaptchaProvider {

    private static final int BG_WIDTH = 360;
    private static final int BG_HEIGHT = 180;
    private static final int MAX_ATTEMPTS = 5;
    private static final int TOLERANCE = 28;
    private static final int TARGET_COUNT = 3;
    private static final int DECOY_COUNT = 3;

    private static final String[] WORD_POOL = {
            "星", "枢", "管", "理", "安", "全", "验", "证", "码", "登",
            "录", "系", "统", "平", "台", "服", "务", "智", "能", "云"
    };

    private static final Font WORD_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 28);

    private final RedisCache redisCache;
    private final ISysConfigService sysConfigService;

    @Override
    public String getType() {
        return CaptchaTypes.CLICK;
    }

    @Override
    public CaptchaIssueResponse generate(String scene) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        List<String> pool = new ArrayList<>(List.of(WORD_POOL));
        Collections.shuffle(pool, random);

        List<String> targets = new ArrayList<>(pool.subList(0, TARGET_COUNT));
        List<String> decoys = new ArrayList<>(pool.subList(TARGET_COUNT, TARGET_COUNT + DECOY_COUNT));

        List<WordPoint> allWords = new ArrayList<>();
        for (String word : targets) {
            allWords.add(new WordPoint(word, true));
        }
        for (String word : decoys) {
            allWords.add(new WordPoint(word, false));
        }
        Collections.shuffle(allWords, random);
        placeWords(allWords, random);

        BufferedImage background = CaptchaScenePainter.paint(BG_WIDTH, BG_HEIGHT);
        Graphics2D g = background.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setFont(WORD_FONT);

        for (WordPoint word : allWords) {
            AffineHelper.drawRotatedWord(g, word.text, word.x, word.y, word.angle, word.color);
        }
        g.dispose();

        String clickTargets = targets.stream()
                .map(t -> allWords.stream().filter(w -> w.text.equals(t) && w.target).findFirst().orElseThrow())
                .map(w -> w.x + "," + w.y)
                .collect(Collectors.joining(";"));

        String captchaToken = CaptchaSupport.randomToken();
        CaptchaState state = new CaptchaState();
        state.setAttempts(0);
        state.setMaxAttempts(MAX_ATTEMPTS);
        state.setScene(scene);
        state.setCaptchaType(CaptchaTypes.CLICK);
        state.setClickTargets(clickTargets);

        String key = CaptchaSupport.tokenKey(captchaToken);
        try {
            redisCache.setCacheObject(key, state, sysConfigService.getCaptchaExpireSeconds(), TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("存储点选验证码失败，key={}", key, e);
            throw new BizException(ErrorCode.CAPTCHA_GENERATE_ERROR, "验证码生成失败，请检查Redis连接: " + e.getMessage());
        }

        CaptchaIssueResponse response = new CaptchaIssueResponse();
        response.setCaptchaType(CaptchaTypes.CLICK);
        response.setCaptchaToken(captchaToken);
        response.setBackgroundImage(CaptchaSupport.toJpegDataUrl(background));
        response.setClickWords(targets);
        response.setClickTip("请依次点击【" + String.join("】【", targets) + "】");
        return response;
    }

    @Override
    public boolean matches(CaptchaVerifyRequest request, CaptchaState state) {
        if (request == null || CollectionUtils.isEmpty(request.getClickPoints())
                || state.getClickTargets() == null || state.getClickTargets().isBlank()) {
            return false;
        }
        String[] parts = state.getClickTargets().split(";");
        if (request.getClickPoints().size() != parts.length) {
            return false;
        }
        for (int i = 0; i < parts.length; i++) {
            String[] xy = parts[i].split(",");
            if (xy.length != 2) {
                return false;
            }
            int expectX = Integer.parseInt(xy[0]);
            int expectY = Integer.parseInt(xy[1]);
            CaptchaClickPoint point = request.getClickPoints().get(i);
            if (point == null || point.getX() == null || point.getY() == null) {
                return false;
            }
            double distance = Math.hypot(point.getX() - expectX, point.getY() - expectY);
            if (distance > TOLERANCE) {
                return false;
            }
        }
        return true;
    }

    private void placeWords(List<WordPoint> words, ThreadLocalRandom random) {
        for (WordPoint word : words) {
            boolean placed = false;
            for (int attempt = 0; attempt < 40 && !placed; attempt++) {
                int x = 40 + random.nextInt(BG_WIDTH - 80);
                int y = 40 + random.nextInt(BG_HEIGHT - 60);
                boolean overlap = false;
                for (WordPoint other : words) {
                    if (other.x > 0 && Math.hypot(other.x - x, other.y - y) < 48) {
                        overlap = true;
                        break;
                    }
                }
                if (!overlap) {
                    word.x = x;
                    word.y = y;
                    word.angle = -30 + random.nextInt(61);
                    word.color = new Color(
                            20 + random.nextInt(40),
                            20 + random.nextInt(40),
                            20 + random.nextInt(50));
                    placed = true;
                }
            }
            if (!placed) {
                word.x = 50 + random.nextInt(BG_WIDTH - 100);
                word.y = 50 + random.nextInt(BG_HEIGHT - 80);
                word.angle = 0;
                word.color = Color.DARK_GRAY;
            }
        }
    }

    private static final class WordPoint {
        private final String text;
        private final boolean target;
        private int x;
        private int y;
        private int angle;
        private Color color;

        private WordPoint(String text, boolean target) {
            this.text = text;
            this.target = target;
        }
    }

    private static final class AffineHelper {
        private static void drawRotatedWord(Graphics2D g, String text, int x, int y, int angle, Color color) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.rotate(Math.toRadians(angle), x, y);
            g2.setColor(new Color(255, 255, 255, 160));
            g2.drawString(text, x + 1, y + 1);
            g2.setColor(color);
            g2.drawString(text, x, y);
            g2.dispose();
        }
    }
}
