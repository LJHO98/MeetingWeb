// 모달 열기
function openModal(obj) {
    const userId = obj.dataset.id;
    const modal = document.getElementById("search_Id");
    document.getElementById("email").value = userId;  // 이메일 필드에 설정
    modal.style.display = "block";
    document.querySelector(".modal_content1").style.display = "block";
    document.querySelector(".modal_content2").style.display = "none";
}

// 모달 닫기
function closeModal() {
    const modal = document.getElementById("search_Id");
    modal.style.display = "none";
}

// 모달 외부 클릭 시 닫기
window.onclick = function(event) {
    const modal = document.getElementById("search_Id");
    if (event.target === modal) {
        modal.style.display = "none";
    }
}

// 다음 모달로 전환
function nextModal() {
    document.querySelector(".modal_content1").style.display = "none";
    document.querySelector(".modal_content2").style.display = "block";
}
