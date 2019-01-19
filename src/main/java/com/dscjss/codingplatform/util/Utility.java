package com.dscjss.codingplatform.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class Utility {

    public static Pageable createPageable(int page, String sortBy, String order, int pageSize) {
        if(page < 1)
            page = 1;
        page -= 1;
        return PageRequest.of(page, pageSize,
                Sort.by( order != null && order.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC,
                        sortBy != null ? sortBy : "id"));
    }

}
