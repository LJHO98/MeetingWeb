// 아이디 찾기 모달 열기
function idOpenModal(obj) {
    const modal = document.getElementById("search_Id");
    modal.style.display = "block";
    modal.querySelector(".idModal_content1").style.display = "block";
    modal.querySelector(".idModal_content2").style.display = "none";
}

// 아이디 찾기 모달 닫기
function idCloseModal() {
    const modal = document.getElementById("search_Id");
    modal.style.display = "none";
}

// 아이디 찾기 모달에서 다음으로 전환
function idNextModal() {
    const modal = document.getElementById("search_Id");
    modal.querySelector(".idModal_content1").style.display = "none";
    modal.querySelector(".idModal_content2").style.display = "block";
}

// 비밀번호 찾기 모달 열기
function pwOpenModal(obj) {
    const modal = document.getElementById("search_Pw");
    modal.style.display = "block";
    modal.querySelector(".pwModal_content1").style.display = "block";
    modal.querySelector(".pwModal_content2").style.display = "none";
}

// 비밀번호 찾기 모달 닫기
function pwCloseModal() {
    const modal = document.getElementById("search_Pw");
    modal.style.display = "none";
}

// 비밀번호 찾기 모달에서 다음으로 전환
function pwNextModal() {
    const modal = document.getElementById("search_Pw");
    modal.querySelector(".pwModal_content1").style.display = "none";
    modal.querySelector(".pwModal_content2").style.display = "block";
}

// 모달 외부 클릭 시 닫기
window.onclick = function(event) {
    const idModal = document.getElementById("search_Id");
    const pwModal = document.getElementById("search_Pw");
    if (event.target === idModal) {
        idModal.style.display = "none";
    }
    if (event.target === pwModal) {
        pwModal.style.display = "none";
    }
};
