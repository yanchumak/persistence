package org.example;

// Keeping AddressEntity leads to fetch all related fields
public record UserProjection(String name, AddressEntity address) {
}
