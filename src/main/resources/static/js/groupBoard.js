const groupId = [[${groupDetail.groupId}]];
console.log(groupId);
function loadNotices() {
    $.ajax({
        url: '/notices/all',
        method: 'GET',
        data: { groupId: groupId },
        success: function(response) {
            const noticeList = document.getElementById("noticeList");
            noticeList.innerHTML = ''; // 기존 목록 초기화

            const isGroupOwner = response.isGroupOwner; // 서버에서 전달된 isGroupOwner 값
            const notices = response.notices;

            notices.forEach(notice => {
                console.log(notice.id);
                const noticeItem = document.createElement("li");
                noticeItem.innerHTML = `
          <p>${notice.content}</p>
          ${isGroupOwner ? `<button class="btn btn-danger btn-sm" onclick="deleteNotice(${notice.id})">삭제</button>` : ''}
        `;
                noticeList.appendChild(noticeItem);
            });
        },
        error: function() {
            alert("공지사항을 불러오는데 실패했습니다.");
        }
    });
}

// 공지사항 입력 필드 추가
function addNoticeField() {
    const noticeContainer = document.getElementById("noticeContainer");
    const newNotice = document.createElement("div");
    newNotice.className = "notice-input";
    newNotice.innerHTML = `
        <input type="text" class="form-control mb-2" placeholder="공지사항 내용을 입력하세요" id="newNoticeContent">
        <button class="btn btn-success btn-sm" onclick="saveNotice()">입력</button>
        <button class="btn btn-danger btn-sm" onclick="removeNoticeField(this)">닫기</button>
      `;
    noticeContainer.appendChild(newNotice);
}

// 공지사항 필드 삭제
function removeNoticeField(button) {
    const noticeContainer = document.getElementById("noticeContainer");
    noticeContainer.removeChild(button.parentNode);
}

// 공지사항 저장 AJAX
function saveNotice() {
    const content = document.getElementById("newNoticeContent").value;
    if (content.trim() === "") {
        alert("내용을 입력하세요.");
        return;
    }

    // 공지사항 저장 AJAX
    $.ajax({
        url: '/notices/save',
        method: 'POST',
        data: { content: content,
            groupId: groupId},
        success: function(response) {
            alert("공지사항이 저장되었습니다.");
            location.reload(); // 페이지 새로고침하여 목록 업데이트
        },
        error: function() {
            alert("저장에 실패했습니다.");
        }
    });
}

function openPostModal(post, groupId) {
    document.getElementById("modalTitle").innerText = post.title;
    document.getElementById("modalContent").innerHTML = post.content;
    document.getElementById("modalUserName").innerText = post.userName;
    document.getElementById("editButton").setAttribute("onclick", `editPost(${post.boardId}, ${groupId})`);
    $('#postModal').modal('show'); // 모달 열기
}

function editPost(boardId, groupId) {
    if (!boardId || !groupId) {
        alert("게시글 ID 또는 그룹 ID를 가져올 수 없습니다.");
        return;
    }
    const url = `/group/${groupId}/updateWrite/${boardId}`;
    window.location.href = url;
}

function deleteNotice(id) {
    if (!confirm("정말 삭제하시겠습니까?")) return;

    $.ajax({
        url: '/notices/delete/' + id,
        method: 'DELETE',
        success: function(response) {
            alert("공지사항이 삭제되었습니다.");
            location.reload(); // 페이지 새로고침하여 목록 업데이트
        },
        error: function() {
            console.log(id);
            alert("삭제에 실패했습니다.");
        }
    });
}

function moveToWritePage() {
    const groupId = [[${groupDetail.groupId}]];
    if (!groupId) {
        alert("그룹 ID를 가져올 수 없습니다.");
        return;
    }
    window.location.href = `/group/${groupId}/write`;
}

// 페이지 로드 시 공지사항 목록을 불러옴
$(document).ready(function() {
    loadNotices();
});