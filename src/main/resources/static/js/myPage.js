$(document).ready(function() {
    $('#profileImg').on('change', function(event) {
        var file = event.target.files[0];
        if (!file) {
            console.log("파일 선택 취소"); // 파일 선택이 취소되면 기존 이미지 유지
            return;
        }

        var reader = new FileReader();
        reader.onload = function(e) {
            $('#previewImage').attr('src', e.target.result); // 새로운 이미지 URL로 src를 업데이트
        };
        reader.readAsDataURL(file);
    });

    const maxChecked = 3; // 최대 체크 개수
    const checkboxes = document.querySelectorAll('.categoryCheck');

    checkboxes.forEach(checkbox => {
        checkbox.addEventListener('change', () => {
            const checkedCount = document.querySelectorAll('.categoryCheck:checked').length;

            if (checkedCount >= maxChecked) {
                checkboxes.forEach(cb => {
                    if (!cb.checked) {
                        cb.disabled = true; // 체크 개수를 초과하면 나머지 체크박스를 비활성화
                    }
                });
            } else {
                checkboxes.forEach(cb => cb.disabled = false); // 조건 미충족 시 모든 체크박스를 활성화
            }
        });
    });
});

function addAddr() {
    new daum.Postcode({
        oncomplete: function(data) {
            // 시와 구 정보 추출
            var city = data.sido || ''; // 시/도를 나타내는 필드
            var district = data.sigungu || ''; // 구/군 정보를 나타내는 필드
            var fullAddress = city;

            // 시와 구가 모두 있는 경우 "시 구" 형태로 결합
            if (city && district) {
                fullAddress += ' ' + district;
            } else if (city && !district) {
                fullAddress = city; // 시만 있는 경우 시만 표시
            } else if (!city && district) {
                fullAddress = district; // 구만 있는 경우 구만 표시
            }
            // 출력할 도로명 주소와 시, 구 정보를 설정
            document.getElementById("activityArea").value = fullAddress; // 수정된 구/군, 시 정보를 표시
        }
    }).open();
}

$(document).ready(function() {
    $('#deleteAccountButton').on('click', function() {
        if (confirm("정말로 회원 탈퇴를 하시겠습니까?")) {
            $.ajax({
                url: '/start/deleteAccount',
                type: 'DELETE',
                success: function(response) {
                    alert("회원 탈퇴가 완료되었습니다. 이용해주셔서 감사합니다.");
                    window.location.href = '/'; // 메인 페이지로 이동
                },
                error: function(error) {
                    alert("회원 탈퇴에 실패했습니다. 다시 시도해주세요.");
                }
            });
        }
    });
});