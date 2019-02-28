package com.repository.converter;

import com.model.Address;

import javax.persistence.AttributeConverter;

public class UserAdressConverter implements AttributeConverter<Address, String> {
    private final String SEPARATOR = ":";

    @Override
    public String convertToDatabaseColumn(Address address) {
        return address.getCountry() + SEPARATOR + address.getCity();
    }

    @Override
    public Address convertToEntityAttribute(String s) {
        return new Address(s.split(SEPARATOR)[0], s.split(SEPARATOR)[1]);
    }
}
