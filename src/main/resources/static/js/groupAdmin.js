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
    $(document).ready(function(){
        console.log("selectedCategoryNames:", member.selectedCategoryNames); // 디버깅

        // 모달 데이터 업데이트
        $("#modalMemberName").text(member.name);
        $("#modalProfileImg").attr("src", member.profileImgUrl);
        $("#modalGender").text(member.gender.trim());
        $("#modalBirthdate").text(member.birthdate);
        $("#modalActivityArea").text(member.activityArea);

        let selectedCategoryNames = member.selectedCategoryNames;

        // selectedCategoryNames가 배열인 경우, 문자열로 변환
        if ($.isArray(selectedCategoryNames)) {
            selectedCategoryNames = selectedCategoryNames.join(", "); // 배열을 문자열로 변환
        }

        // selectedCategoryNames가 문자열일 경우 split 사용
        if (typeof selectedCategoryNames === "string") {
            const categories = selectedCategoryNames.split(","); // 쉼표로 분리하여 배열로 만들기
            const modalCategories = $("#modalCategories");

            modalCategories.empty(); // 기존 내용을 비움

            // 각 카테고리를 <span>으로 감싸서 추가
            $.each(categories, function (index, category) {
                const span = $("<span>").text(category.trim())  // 공백 제거 후 카테고리 이름 추가
                    .css({
                        "margin-right": "10px",
                        "display": "inline-block",
                        "padding": "5px 10px",
                        "border": "1px solid #ccc",
                        "border-radius": "5px",
                        "background-color": "#f0f0f0",
                        "color": "#333"
                    });

                modalCategories.append(span); // 카테고리 요소를 modalCategories에 추가
            });
        } else {
            console.error("selectedCategoryNames는 문자열 또는 배열이어야 합니다.");
        }

        // gender 값 확인 후 아이콘 업데이트
        const gender = $("#modalGender").text().trim();
        console.log(gender);  // gender 값 확인

        // const genderIcon = $("#genderIcon"); // 아이콘을 표시할 <i> 요소

        // 기존 아이콘 클래스 제거
        $("#genderIcon").removeClass("bi-gender-male bi-gender-female bi-gender-ambiguous");
        console.log($("#genderIcon").attr("class")); // 클래스 변경 전에 확인

        // gender 값에 따라 적절한 아이콘을 설정
        if (gender === "MALE") {
            $("#modalGender").empty(); // 기존 텍스트 제거
            $("#modalGender").addClass("bi-gender-male");  // MALE일 때 남성 아이콘
            const newParagraph = document.createElement('i');
            newParagraph.classList.add('bi-gender-male');
            newParagraph.textContent = "bi-gender-male";
            console.log(11111111111);

        } else if (gender === "FEMALE") {
            $("#modalGender").empty(); // 기존 텍스트 제거
            $("#modalGender").addClass("bi-gender-female");
            console.log(222222222222);// FEMALE일 때 여성 아이콘
        } else if (gender === "OTHER") {
            $("#modalGender").empty(); // 기존 텍스트 제거
            $("#modalGender").addClass("bi-gender-ambiguous");
            console.log(33333333333333);// OTHER일 때 혼합 아이콘
        } else {
            $("#modalGender").empty(); // 기존 텍스트 제거
            $("#modalGender").removeClass();// 값이 없으면 아이콘 제거
            console.log(444444444444);
        }

        console.log($("#genderIcon").attr("class")); // 클래스 변경 후 확인

        // 모달 열기
        $('#memberModal').modal('show');
    });
}