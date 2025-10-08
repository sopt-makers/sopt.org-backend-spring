package sopt.org.homepage.soptstory.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * IP 주소 Value Object
 *
 * 책임:
 * - IP 주소 형식 검증 (IPv4, IPv6)
 * - IP 주소 불변성 보장
 * - 좋아요 중복 체크를 위한 동등성 비교
 */
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 전용
public class IpAddress {

    // IPv4 형식: xxx.xxx.xxx.xxx
    private static final String IPV4_REGEX =
            "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}" +
                    "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

    // IPv6 형식 (간단한 검증)
    private static final String IPV6_REGEX =
            "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$";

    private static final int MAX_LENGTH = 45; // IPv6 최대 길이

    @Column(name = "\"ip\"", nullable = false, length = MAX_LENGTH)
    private String value;

    /**
     * IP 주소 생성
     *
     * @param value IP 주소 문자열
     * @throws IllegalArgumentException IP 주소가 null이거나 형식이 잘못된 경우
     */
    public IpAddress(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("IP 주소는 필수입니다.");
        }

        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("IP 주소는 %d자를 초과할 수 없습니다.", MAX_LENGTH)
            );
        }

        if (!isValidIpFormat(value)) {
            throw new IllegalArgumentException(
                    "올바른 IP 주소 형식이 아닙니다: " + value
            );
        }
    }

    private boolean isValidIpFormat(String value) {
        return value.matches(IPV4_REGEX) || value.matches(IPV6_REGEX);
    }

    /**
     * IPv4 형식인지 확인
     */
    public boolean isIpv4() {
        return value.matches(IPV4_REGEX);
    }

    /**
     * IPv6 형식인지 확인
     */
    public boolean isIpv6() {
        return value.matches(IPV6_REGEX);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IpAddress ipAddress = (IpAddress) o;
        return Objects.equals(value, ipAddress.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}