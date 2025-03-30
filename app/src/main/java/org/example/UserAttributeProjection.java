package org.example;

import java.util.List;

import lombok.Builder;

@Builder
public record UserAttributeProjection(String id, String name,
                                      List<AttributeProjection> attributes) {
}
