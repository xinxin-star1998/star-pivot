package com.star.pivot.workflow.engine;

import com.star.pivot.workflow.domain.model.RuntimeDefinition;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
public class ConditionEvaluator {

    public String evaluate(List<RuntimeDefinition.RuntimeBranch> branches, Map<String, Object> variables) {
        if (branches == null || branches.isEmpty()) {
            return null;
        }
        String defaultNext = null;
        for (RuntimeDefinition.RuntimeBranch branch : branches) {
            Map<String, Object> condition = branch.getCondition();
            if (condition != null && "default".equals(String.valueOf(condition.get("type")))) {
                defaultNext = branch.getNext();
                continue;
            }
            if (matches(condition, variables)) {
                return branch.getNext();
            }
        }
        return defaultNext;
    }

    private boolean matches(Map<String, Object> condition, Map<String, Object> variables) {
        if (condition == null || variables == null) {
            return false;
        }
        if ("default".equals(String.valueOf(condition.get("type")))) {
            return true;
        }
        String field = String.valueOf(condition.get("field"));
        String op = String.valueOf(condition.get("op"));
        Object expect = condition.get("value");
        if (!StringUtils.hasText(field) || !variables.containsKey(field)) {
            return false;
        }
        Object actual = variables.get(field);
        return compare(actual, op, expect);
    }

    private boolean compare(Object actual, String op, Object expect) {
        if (actual == null || expect == null) {
            return false;
        }
        try {
            BigDecimal left = new BigDecimal(String.valueOf(actual));
            BigDecimal right = new BigDecimal(String.valueOf(expect));
            return switch (op) {
                case ">" -> left.compareTo(right) > 0;
                case ">=" -> left.compareTo(right) >= 0;
                case "<" -> left.compareTo(right) < 0;
                case "<=" -> left.compareTo(right) <= 0;
                case "==" -> left.compareTo(right) == 0;
                case "!=" -> left.compareTo(right) != 0;
                default -> false;
            };
        } catch (NumberFormatException ex) {
            String left = String.valueOf(actual);
            String right = String.valueOf(expect);
            return switch (op) {
                case "==" -> left.equals(right);
                case "!=" -> !left.equals(right);
                default -> false;
            };
        }
    }
}
