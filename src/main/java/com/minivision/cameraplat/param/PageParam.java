package com.minivision.cameraplat.param;

import javax.validation.constraints.Max;

public class PageParam {
    private int offset = 0;

    @Max(100)
    private int limit = 10;

    private String search;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
