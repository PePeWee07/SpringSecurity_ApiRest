package com.ucacue.UcaApp.exception.crud;

public class UserNotFoundException extends RuntimeException {
    private Object userIdentifier;
    private SearchType searchType;

    public enum SearchType {
        ID, EMAIL
    }

    public UserNotFoundException(Object userIdentifier, SearchType searchType) {
        super("User not found with " + searchType.name().toLowerCase() + ": " + userIdentifier);
        this.userIdentifier = userIdentifier;
        this.searchType = searchType;
    }

    public Object getUserIdentifier() {
        return userIdentifier;
    }

    public SearchType getSearchType() {
        return searchType;
    }
}
