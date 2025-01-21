package sopt.org.homepage.internal.crew.dto;

public record PaginationMeta(

        Integer page,

        Integer take,

        Integer itemCount,

        Integer pageCount,

        Boolean hasPreviousPage,

        Boolean hasNextPage
) {
}