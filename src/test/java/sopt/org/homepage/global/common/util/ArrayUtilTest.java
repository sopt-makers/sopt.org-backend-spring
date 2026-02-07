package sopt.org.homepage.global.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ArrayUtil 단위 테스트")
class ArrayUtilTest {

    private final ArrayUtil arrayUtil = new ArrayUtil();

    // ===== paginateArray =====

    @Nested
    @DisplayName("paginateArray - 페이지네이션")
    class PaginateArray {

        private final List<String> items = List.of("A", "B", "C", "D", "E", "F", "G");

        @Test
        @DisplayName("✅ 1페이지 조회 (limit=3)")
        void firstPage() {
            // when
            List<String> result = arrayUtil.paginateArray(items, 1, 3);

            // then
            assertThat(result).containsExactly("A", "B", "C");
        }

        @Test
        @DisplayName("✅ 2페이지 조회 (limit=3)")
        void secondPage() {
            // when
            List<String> result = arrayUtil.paginateArray(items, 2, 3);

            // then
            assertThat(result).containsExactly("D", "E", "F");
        }

        @Test
        @DisplayName("✅ 마지막 페이지 - limit보다 적은 데이터")
        void lastPagePartial() {
            // when - 7개 데이터, 3페이지, limit=3 → 1개만 남음
            List<String> result = arrayUtil.paginateArray(items, 3, 3);

            // then
            assertThat(result).containsExactly("G");
        }

        @Test
        @DisplayName("✅ 범위 초과 페이지 → 빈 목록")
        void pageExceedsRange() {
            // when - 7개 데이터인데 100페이지 요청
            List<String> result = arrayUtil.paginateArray(items, 100, 3);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("✅ 빈 리스트 → 빈 목록")
        void emptyList() {
            // when
            List<String> result = arrayUtil.paginateArray(Collections.emptyList(), 1, 10);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("✅ limit이 전체 크기보다 큰 경우 → 전체 반환")
        void limitLargerThanSize() {
            // when
            List<String> result = arrayUtil.paginateArray(items, 1, 100);

            // then
            assertThat(result).containsExactly("A", "B", "C", "D", "E", "F", "G");
        }
    }

    // ===== dropDuplication =====

    @Nested
    @DisplayName("dropDuplication - 중복 제거")
    class DropDuplication {

        @Test
        @DisplayName("✅ 이름 기준 중복 제거")
        void removeDuplicatesByName() {
            // given
            List<TestItem> items = List.of(
                    new TestItem(1, "Alice"),
                    new TestItem(2, "Bob"),
                    new TestItem(3, "Alice"),
                    new TestItem(4, "Charlie")
            );

            // when
            List<TestItem> result = arrayUtil.dropDuplication(items, TestItem::name);

            // then
            assertThat(result).hasSize(3);
            assertThat(result).extracting(TestItem::name)
                    .containsExactly("Alice", "Bob", "Charlie");
        }

        @Test
        @DisplayName("✅ 첫 번째 등장 항목 유지 (순서 보장)")
        void keepsFirstOccurrence() {
            // given
            List<TestItem> items = List.of(
                    new TestItem(1, "Alice"),
                    new TestItem(2, "Alice"),
                    new TestItem(3, "Alice")
            );

            // when
            List<TestItem> result = arrayUtil.dropDuplication(items, TestItem::name);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).id()).isEqualTo(1);
        }

        @Test
        @DisplayName("✅ 중복 없는 경우 원본 그대로")
        void noDuplicates() {
            // given
            List<TestItem> items = List.of(
                    new TestItem(1, "Alice"),
                    new TestItem(2, "Bob")
            );

            // when
            List<TestItem> result = arrayUtil.dropDuplication(items, TestItem::name);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("✅ 빈 리스트 → 빈 목록")
        void emptyList() {
            // when
            List<TestItem> result = arrayUtil.dropDuplication(
                    Collections.emptyList(), TestItem::name
            );

            // then
            assertThat(result).isEmpty();
        }
    }

    // ===== Test Helper =====

    private record TestItem(int id, String name) {
    }
}
