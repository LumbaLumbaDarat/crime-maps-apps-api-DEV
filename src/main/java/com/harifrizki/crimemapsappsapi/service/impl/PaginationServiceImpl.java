package com.harifrizki.crimemapsappsapi.service.impl;

import com.harifrizki.crimemapsappsapi.model.PaginationModel;
import com.harifrizki.crimemapsappsapi.service.PaginationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import static com.harifrizki.crimemapsappsapi.utils.AppsConstants.*;
import static com.harifrizki.crimemapsappsapi.utils.UtilizationClass.combineUrl;

@Component
public class PaginationServiceImpl implements PaginationService {

    @Autowired
    private Environment environment;

    public PaginationServiceImpl() {
    }

    @Override
    public PaginationModel getPagination(int pageNo, String controller, Page page, String[] params, String[] paramsValues) {
        PaginationModel paginationModel = new PaginationModel();
        paginationModel.setPageNo(pageNo);
        paginationModel.setContentSizePerPage(Integer.parseInt(environment.
                getProperty(PAGINATION_CONTENT_SIZE_PER_PAGE)));
        paginationModel.setTotalPage(page.getTotalPages());
        paginationModel.setFirstPage(environment.getProperty(SERVER_DEVELOPMENT_URL).
                concat(combineUrl(controller, params,
                        addString(paramsValues.length, paramsValues, "1"))));
        paginationModel.setCurrentPage(environment.getProperty(SERVER_DEVELOPMENT_URL).
                concat(combineUrl(controller, params,
                        addString(paramsValues.length, paramsValues, String.valueOf(pageNo)))));

        if (page.getTotalPages() != 0)
            paginationModel.setLastPage(environment.getProperty(SERVER_DEVELOPMENT_URL).
                concat(combineUrl(controller, params,
                        addString(paramsValues.length, paramsValues, String.valueOf(page.getTotalPages())))));
        else paginationModel.setLastPage(environment.getProperty(SERVER_DEVELOPMENT_URL).
                concat(combineUrl(controller, params,
                        addString(paramsValues.length, paramsValues, "1"))));

        if (pageNo < page.getTotalPages()) {
            int nextPage = pageNo + 1;
            paginationModel.setNextPage(environment.getProperty(SERVER_DEVELOPMENT_URL).
                    concat(combineUrl(controller, params,
                            addString(paramsValues.length, paramsValues, String.valueOf(nextPage)))));
        } else paginationModel.setNextPage(EMPTY_STRING);

        if (pageNo > 1)
        {
            int prevPage = pageNo - 1;
            paginationModel.setPrevPage(environment.getProperty(SERVER_DEVELOPMENT_URL).
                    concat(combineUrl(controller, params,
                            addString(paramsValues.length, paramsValues, String.valueOf(prevPage)))));
        } else paginationModel.setPrevPage(EMPTY_STRING);

        paginationModel.setTotalContentSize(page.getTotalElements());
        return paginationModel;
    }

    public static String[] addString(int arrayLength, String[] strings, String values)
    {
        String[] newStrings = new String[arrayLength + 1];
        if (arrayLength >= 0) System.arraycopy(strings, 0, newStrings, 0, arrayLength);
        newStrings[arrayLength] = values;
        return newStrings;
    }
}
