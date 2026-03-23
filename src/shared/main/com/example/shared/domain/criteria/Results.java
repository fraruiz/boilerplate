package com.example.shared.domain.criteria;

import java.util.List;

public record Results<T>(Integer page,
                         Integer total,
                         Integer size,
                         List<T> results) {
}
