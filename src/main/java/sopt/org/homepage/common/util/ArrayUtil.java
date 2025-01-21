package sopt.org.homepage.common.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
@Component
public class ArrayUtil {
    public <T, O> List<T> dropDuplication(List<T> array, Function<T, O> propertyAccessor) {
        Set<O> seen = new HashSet<>();
        List<T> uniqueArray = new ArrayList<>();

        for (T element : array) {
            O property = propertyAccessor.apply(element);
            if (!seen.contains(property)) {
                seen.add(property);
                uniqueArray.add(element);
            }
        }

        return uniqueArray;
    }

    public <T> List<T> paginateArray(List<T> items, Integer page, Integer limit) {
        int offset = (page - 1) * limit;
        int toIndex = Math.min(offset + limit, items.size());

        if (offset > items.size()) {
            return new ArrayList<>();
        }

        return new ArrayList<>(items.subList(offset, toIndex));
    }
}
