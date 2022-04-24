package com.harifrizki.crimemapsappsapi.service;

import com.harifrizki.crimemapsappsapi.model.PaginationModel;
import org.springframework.data.domain.Page;

public interface PaginationService {
    PaginationModel getPagination(int pageNo, String controller, Page page, String[] params, String[]paramsValues);
}
