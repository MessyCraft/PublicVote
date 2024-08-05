package com.github.messycraft.publicvote.entity;

import lombok.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@AllArgsConstructor
@Getter
public class Vote {

    @NonNull
    private String title;

    @NonNull
    private UUID creator;

    @NonNull
    private String creatorName;

    @NonNull
    private String createTime;

    @NonNull
    private boolean isPublic;

    @NonNull
    private int voteTime;

    @Setter
    private int realVoteTime = -1;

    @Setter
    private String finishTime = "";

    @Setter
    private boolean isEnded = false;

    private Map<String, Boolean> voters = new HashMap<>();

    private UUID uniqueId = UUID.randomUUID();

}
