// '모두 동의' 체크박스가 변경될 때 개별 체크박스 모두 체크 또는 해제
document.getElementById("chk_all").addEventListener("change", function() {
    const checkboxes = document.querySelectorAll(".chk");
    checkboxes.forEach((checkbox) => {
        checkbox.checked = this.checked;
    });
});

// 개별 체크박스 변경 시 '모두 동의' 체크박스 상태 업데이트
document.querySelectorAll(".chk").forEach((checkbox) => {
    checkbox.addEventListener("change", function() {
        const allChecked = document.querySelectorAll(".chk:checked").length === document.querySelectorAll(".chk").length;
        document.getElementById("chk_all").checked = allChecked;
    });
});

function checkAgreement() {
    const allChecked = document.querySelectorAll(".chk:checked").length === document.querySelectorAll(".chk").length;
    if (!allChecked) {
        alert("모든 필수 약관에 동의하셔야 합니다.");
        return;
    }

    // 모든 필수 약관이 체크되면 가입 폼을 서서히 나타나게 함
    const joinForm = document.getElementById("joinForm");
    const agreementContainer = document.querySelector(".agreement-container");

    // 약관 동의 섹션 숨기기
    agreementContainer.classList.add("hidden");

    // 가입 폼을 부드럽게 나타나게 함
    joinForm.style.display = "block";
    joinForm.style.opacity = "0";
    joinForm.style.transform = "scale(0.95)";

    let opacity = 0;
    const fadeIn = setInterval(() => {
        opacity += 0.05;
        joinForm.style.opacity = opacity.toString();
        joinForm.style.transform = `scale(${0.95 + 0.05 * (opacity / 1)})`;

        if (opacity >= 1) {
            clearInterval(fadeIn);
            joinForm.style.opacity = "1";
            joinForm.style.transform = "scale(1)";
        }
    }, 30); // 30ms 간격으로 서서히 나타남
}
