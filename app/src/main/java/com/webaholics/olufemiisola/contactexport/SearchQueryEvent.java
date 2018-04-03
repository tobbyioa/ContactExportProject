package com.webaholics.olufemiisola.contactexport;

/**
 * Created by Olufemi Isola on 19/11/17.
 */

class SearchQueryEvent {
    String query;

    public SearchQueryEvent(String query) {
        this.query=query;
    }

    public String getQuery() {
        return query;
    }
}
