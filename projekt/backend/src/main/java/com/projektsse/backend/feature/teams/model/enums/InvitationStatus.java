package com.projektsse.backend.feature.teams.model.enums;

/**
 * It determines the status of a team invitation link
 */
public enum InvitationStatus {
    /**
     * Link was successfully used to join the team
     */
    ACCEPTED,

    /**
     * Link was used to decline the offer
     */
    DECLINED,

    /**
     * Link has expired the established expiry time
     */
    EXPIRED,

    /**
     * Link is active and not expired, but wasn't accepted or declined yet
     */
    PENDING,

    /**
     * Link was invalidated by sender
     */
    REVOKED
}
