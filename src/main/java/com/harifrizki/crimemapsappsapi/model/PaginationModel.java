package com.harifrizki.crimemapsappsapi.model;

import lombok.Getter;
import lombok.Setter;

public class PaginationModel {

    @Getter @Setter
    private int pageNo = 0;

    @Getter @Setter
    private int totalPage = 0;

    @Getter @Setter
    private int contentSizePerPage = 0;

    @Getter @Setter
    private String firstPage = "";

    @Getter @Setter
    private String lastPage = "";

    @Getter @Setter
    private String currentPage = "";

    @Getter @Setter
    private String nextPage = "";

    @Getter @Setter
    private String prevPage = "";

    @Getter @Setter
    private Long totalContentSize = 0L;

    public PaginationModel() {
    }
}
