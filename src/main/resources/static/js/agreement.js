// '모두 동의' 체크박스가 변경될 때 개별 체크박스 모두 체크 또는 해제
window.onload = function() {
    document.getElementById("chk_all").addEventListener("change", function () {
        const checkboxes = document.querySelectorAll(".chk");
        checkboxes.forEach((checkbox) => {
            checkbox.checked = this.checked;
        });
    });

// 개별 체크박스 변경 시 '모두 동의' 체크박스 상태 업데이트
    document.querySelectorAll(".chk").forEach((checkbox) => {
        checkbox.addEventListener("change", function () {
            const allChecked = Array.from(document.querySelectorAll(".chk")).every(cb => cb.checked);
            document.getElementById("chk_all").checked = allChecked;
        });
    });

};

function checkAgreement() {
    // 필수 약관 체크 상태 확인
    const requiredCheckboxes = document.querySelectorAll(".chkKey");
    const uncheckedRequired = Array.from(requiredCheckboxes).filter(checkbox => checkbox.checked === false);

    if (uncheckedRequired.length > 0) {
        alert("모든 필수 약관에 동의하셔야 합니다.");
        return;
    }

    // 모든 필수 약관이 체크되면 가입 폼으로 이동
    window.location.href = 'join'; // 가입 폼의 HTML 파일 경로를 지정합니다.
}