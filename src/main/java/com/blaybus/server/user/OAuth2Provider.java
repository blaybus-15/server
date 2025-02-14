package com.blaybus.server.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OAuth2Provider {
    KAKAO("kakao");

    private final String registrationId;
}