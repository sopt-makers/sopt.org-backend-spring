package sopt.org.homepage.common.util;

import sopt.org.homepage.project.dto.ProjectsResponseDto;

public class ProjectComparator {
    public static int compare(ProjectsResponseDto a, ProjectsResponseDto b) {
        // null 체크 추가
        if (a == null || b == null) {
            return 0;
        }

        Integer genA = a.getGeneration();
        Integer genB = b.getGeneration();

        // generation이 null인 경우 처리
        if (genA == null && genB == null) {
            return 0;
        }
        if (genA == null) {
            return 1;
        }
        if (genB == null) {
            return -1;
        }

        // generation 비교
        if (genA > genB) {
            return -1;
        } else if (genA.equals(genB)) {
            return 0;
        } else {
            return 1;
        }
    }
}