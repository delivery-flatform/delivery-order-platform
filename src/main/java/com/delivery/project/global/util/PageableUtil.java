package com.delivery.project.global.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

public class PageableUtil {

    private static final List<Integer> allowedSize = List.of(10,30,50);

    public static Pageable valiPageable(int page, int size, Sort sort){
        if(!allowedSize.contains(size)){
            size = 10;
        }
        return PageRequest.of(page,size,sort);
    }

    public static Pageable createPageable(int page, int size, String sortBy, boolean isAsc){
        if(sortBy == null){
            sortBy = "createdAt";
        }
        Sort.Direction direction = isAsc ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        return valiPageable(page,size,sort);
    }
}
