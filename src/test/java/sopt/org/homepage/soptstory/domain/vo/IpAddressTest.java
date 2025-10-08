package sopt.org.homepage.soptstory.domain.vo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.*;

/**
 * IpAddress Value Object 단위 테스트
 */
@DisplayName("IpAddress 단위 테스트")
class IpAddressTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "192.168.0.1",
            "127.0.0.1",
            "0.0.0.0",
            "255.255.255.255",
            "10.0.0.1"
    })
    @DisplayName("유효한 IPv4 주소로 생성 성공")
    void createWithValidIpv4(String validIp) {
        // when
        IpAddress ipAddress = new IpAddress(validIp);

        // then
        assertThat(ipAddress.getValue()).isEqualTo(validIp);
        assertThat(ipAddress.isIpv4()).isTrue();
        assertThat(ipAddress.isIpv6()).isFalse();
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "2001:0db8:85a3:0000:0000:8a2e:0370:7334",
            "2001:db8:85a3:0:0:8a2e:370:7334",
            "fe80:0000:0000:0000:0204:61ff:fe9d:f156"
    })
    @DisplayName("유효한 IPv6 주소로 생성 성공")
    void createWithValidIpv6(String validIp) {
        // when
        IpAddress ipAddress = new IpAddress(validIp);

        // then
        assertThat(ipAddress.getValue()).isEqualTo(validIp);
        assertThat(ipAddress.isIpv6()).isTrue();
        assertThat(ipAddress.isIpv4()).isFalse();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("IP 주소가 null이거나 빈 문자열이면 예외 발생")
    void createWithNullOrEmpty(String invalidIp) {
        // when & then
        assertThatThrownBy(() -> new IpAddress(invalidIp))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("IP 주소는 필수입니다.");
    }

    @Test
    @DisplayName("IP 주소가 공백만 있으면 예외 발생")
    void createWithBlank() {
        // when & then
        assertThatThrownBy(() -> new IpAddress("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("IP 주소는 필수입니다.");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "256.1.1.1",           // 범위 초과
            "192.168.0",           // 불완전한 주소
            "192.168.0.1.1",       // 너무 많은 옥텟
            "abc.def.ghi.jkl",     // 문자
            "192.168.-1.1",        // 음수
            "not-an-ip"            // 완전히 잘못된 형식
    })
    @DisplayName("잘못된 IP 형식이면 예외 발생")
    void createWithInvalidFormat(String invalidIp) {
        // when & then
        assertThatThrownBy(() -> new IpAddress(invalidIp))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("올바른 IP 주소 형식이 아닙니다");
    }

    @Test
    @DisplayName("IP 주소가 45자를 초과하면 예외 발생")
    void createWithTooLongIp() {
        // given
        String tooLongIp = "a".repeat(46);

        // when & then
        assertThatThrownBy(() -> new IpAddress(tooLongIp))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("IP 주소는 45자를 초과할 수 없습니다.");
    }

    @Test
    @DisplayName("동일한 IP 주소의 VO는 동등하다")
    void equalsSameIp() {
        // given
        String ip = "192.168.0.1";
        IpAddress ip1 = new IpAddress(ip);
        IpAddress ip2 = new IpAddress(ip);

        // when & then
        assertThat(ip1).isEqualTo(ip2);
        assertThat(ip1.hashCode()).isEqualTo(ip2.hashCode());
    }

    @Test
    @DisplayName("다른 IP 주소의 VO는 동등하지 않다")
    void notEqualsDifferentIp() {
        // given
        IpAddress ip1 = new IpAddress("192.168.0.1");
        IpAddress ip2 = new IpAddress("192.168.0.2");

        // when & then
        assertThat(ip1).isNotEqualTo(ip2);
    }

    @Test
    @DisplayName("toString()은 IP 주소 값을 반환한다")
    void toStringReturnsValue() {
        // given
        String ip = "192.168.0.1";
        IpAddress ipAddress = new IpAddress(ip);

        // when & then
        assertThat(ipAddress.toString()).isEqualTo(ip);
    }
}