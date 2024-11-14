function delegateGroup(groupId, userId){
    if(confirm("모임장 위임은 신중하게 결정해주세요. 정말 위임하시겠습니까?")) {
        $.ajax({
            url: '/group/delegate',
            type: "POST",
            data: {
                groupId: groupId,
                userId: userId
            },
            dataType: "text",
            success: function (data) {
                alert(data);
                location.href="/group/"+groupId;
            },
            error: function () {
                alert("위임 실패");
            }
        });
    }
}




function removeMember(groupId, userId) {
    console.log(groupId);
    console.log(userId);
    if (confirm("정말로 이 회원을 강퇴하시겠습니까?")) {
        $.ajax({
            url: '/group/' + groupId + '/removeMember/' + userId,
            type: 'DELETE',
            success: function(response) {
                alert(response);
                location.reload(); // 페이지 새로고침
            },
            error: function() {
                alert("회원 강퇴에 실패했습니다.");
            }
        });
    }
}



// 회원 정보 모달을 여는 함수
function openMemberModal(member) {
    document.getElementById("modalMemberName").innerText = member.name;
    document.getElementById("modalProfileImg").src = member.profileImgUrl;
    document.getElementById("modalCategories").innerText = member.selectedCategoryNames;
    document.getElementById("modalGender").innerText = member.gender;
    document.getElementById("modalBirthdate").innerText = member.birthdate;
    document.getElementById("modalActivityArea").innerText = member.activityArea;
    $('#memberModal').modal('show');
}