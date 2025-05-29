package sopt.org.homepage.common.util;

import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;

public class IpAddressUtil {

	private static final String[] IP_HEADER_CANDIDATES = {
		"X-Forwarded-For",
		"Proxy-Client-IP",
		"WL-Proxy-Client-IP",
		"HTTP_X_FORWARDED_FOR",
		"HTTP_X_FORWARDED",
		"HTTP_X_CLUSTER_CLIENT_IP",
		"HTTP_CLIENT_IP",
		"HTTP_FORWARDED_FOR",
		"HTTP_FORWARDED",
		"HTTP_VIA",
		"REMOTE_ADDR"
	};

	/**
	 * HttpServletRequest에서 클라이언트의 실제 IP 주소를 추출합니다.
	 * 프록시나 로드밸런서를 통한 요청의 경우 원본 IP를 찾아 반환합니다.
	 *
	 * @param request HttpServletRequest 객체
	 * @return 클라이언트의 IP 주소
	 */
	public static String getClientIpAddress(HttpServletRequest request) {
		for (String header : IP_HEADER_CANDIDATES) {
			String ip = request.getHeader(header);
			if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
				// X-Forwarded-For 헤더의 경우 여러 IP가 콤마로 구분되어 있을 수 있음
				// 첫 번째 IP가 원본 클라이언트 IP
				if (ip.contains(",")) {
					ip = ip.split(",")[0].trim();
				}
				return normalizeIpAddress(ip);
			}
		}

		// 헤더에서 IP를 찾지 못한 경우 기본 remote address 사용
		return normalizeIpAddress(request.getRemoteAddr());
	}

	/**
	 * IP 주소를 정규화합니다. (IPv6 전체 형태를 축약 형태로 변환)
	 *
	 * @param ip 원본 IP 주소
	 * @return 정규화된 IP 주소
	 */
	private static String normalizeIpAddress(String ip) {
		if (ip == null) {
			return "127.0.0.1";
		}

		// IPv6 localhost 전체 형태를 축약 형태로 변환
		if ("0:0:0:0:0:0:0:1".equals(ip)) {
			return "::1";
		}

		return ip;
	}

	/**
	 * IP 주소가 유효한지 검증합니다.
	 *
	 * @param ip 검증할 IP 주소
	 * @return 유효한 IP 주소인지 여부
	 */
	public static boolean isValidIpAddress(String ip) {
		if (!StringUtils.hasText(ip) || "unknown".equalsIgnoreCase(ip)) {
			return false;
		}

		// IPv4 주소 패턴 검증 (간단한 형태)
		String ipv4Pattern = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

		// IPv6 주소 패턴 검증 (간단한 형태)
		String ipv6Pattern = "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$";

		return ip.matches(ipv4Pattern) || ip.matches(ipv6Pattern) ||
			"localhost".equals(ip) || "127.0.0.1".equals(ip) || "::1".equals(ip);
	}
}
