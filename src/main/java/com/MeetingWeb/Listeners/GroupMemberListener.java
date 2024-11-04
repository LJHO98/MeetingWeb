package com.MeetingWeb.Listeners;

import com.MeetingWeb.Entity.GroupMember;
import com.MeetingWeb.Entity.Groups;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;

public class GroupMemberListener {
    @PostPersist // 멤버가 추가된 후 호출
    public void postPersist(GroupMember groupMember) {
        Groups group = groupMember.getGroup();
        if (group.getCurrentHeadCount() < group.getCapacity()) {
            group.setCurrentHeadCount(group.getCurrentHeadCount() + 1);
        }
    }

    @PostRemove // 멤버가 삭제된 후 호출
    public void postRemove(GroupMember groupMember) {
        Groups group = groupMember.getGroup();
        if (group.getCurrentHeadCount() > 0) {
            group.setCurrentHeadCount(group.getCurrentHeadCount() - 1);
        }
    }
}
