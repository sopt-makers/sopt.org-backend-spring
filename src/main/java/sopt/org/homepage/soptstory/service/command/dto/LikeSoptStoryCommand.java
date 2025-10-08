package sopt.org.homepage.soptstory.service.command.dto;

/**
 * 좋아요 Command
 *
 * @param soptStoryId SoptStory ID
 * @param ip 사용자 IP 주소
 */
public record LikeSoptStoryCommand(
        Long soptStoryId,
        String ip
) {
    public LikeSoptStoryCommand {
        if (soptStoryId == null) {
            throw new IllegalArgumentException("SoptStory ID는 필수입니다.");
        }
        if (ip == null || ip.isBlank()) {
            throw new IllegalArgumentException("IP 주소는 필수입니다.");
        }
    }
}