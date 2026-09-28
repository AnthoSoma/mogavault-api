package com.mogavault.api.common.exception;

import java.util.Map;

public record ValidationErrorItem(
        String key,
        Map<String, Object> params
) {
}