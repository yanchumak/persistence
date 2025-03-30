package org.example;

import lombok.Builder;

@Builder
public record UserAttributeFlatProjection(String name,
                                          // It is better to use a separate projection for the attribute
                                          AttributeEntity attribute) {
}
