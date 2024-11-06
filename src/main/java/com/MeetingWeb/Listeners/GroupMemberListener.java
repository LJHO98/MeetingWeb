package com.MeetingWeb.Listeners;

import com.MeetingWeb.Entity.GroupMember;
import com.MeetingWeb.Entity.Groups;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.transaction.Transactional;

public class GroupMemberListener {
    @PersistenceContext
    private EntityManager entityManager;

    @PostPersist // 멤버가 추가된 후 호출
    @Transactional
    public void postPersist(GroupMember groupMember) {
        Groups group = groupMember.getGroup();
        if (group.getCurrentHeadCount() < group.getCapacity()) {
            group.setCurrentHeadCount(group.getCurrentHeadCount() + 1);
            entityManager.merge(group); // 변경 사항을 영속성 컨텍스트에 반영
        }
    }

    @PostRemove // 멤버가 삭제된 후 호출
    @Transactional
    public void postRemove(GroupMember groupMember) {
        Groups group = groupMember.getGroup();
        if (group.getCurrentHeadCount() > 0) {
            group.setCurrentHeadCount(group.getCurrentHeadCount() - 1);
            entityManager.merge(group); // 변경 사항을 영속성 컨텍스트에 반영
        }
    }
}
