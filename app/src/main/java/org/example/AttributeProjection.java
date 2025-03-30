package org.example;

import lombok.Builder;

@Builder
public record AttributeProjection(String name, String value) {
}
