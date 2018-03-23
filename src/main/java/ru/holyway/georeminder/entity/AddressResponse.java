package ru.holyway.georeminder.entity;

import org.springframework.util.CollectionUtils;

import java.util.List;

public class AddressResponse {
    private List<AddressResult> results;

    public AddressResponse(List<AddressResult> results) {
        this.results = results;
    }

    public AddressResponse() {
    }

    public void setResults(List<AddressResult> results) {
        this.results = results;
    }

    public AddressResult getAddressResult() {
        if (!CollectionUtils.isEmpty(results)) {
            return results.get(0);
        }
        return null;
    }


}
