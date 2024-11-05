$(document).ready(function() {
    $('#profileImage').on('change', function(event) {
        const file = event.target.files[0];
        const imagePreview = $('#imagePreview');

        if (file) {
            const reader = new FileReader();
            reader.onload = function(e) {
                imagePreview.attr('src', e.target.result);
                imagePreview.show();
            };
            reader.readAsDataURL(file);
        } else {
            imagePreview.attr('src', '');
            imagePreview.hide();
        }
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

function sample4_execDaumPostcode() {
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
    $('#checkUsernameButton').on('click', function() {
        let userName = $('#userName').val();
        if (userName.length >= 4 && userName.length <= 10) {
            $.ajax({
                type: "POST",
                url: "/start/check-username",
                data: { userName: userName },
                success: function(response) {
                    let messageElement = $('#usernameMessage');
                    if (response) {
                        messageElement.text("사용 가능한 아이디입니다.").css("color", "green");
                    } else {
                        messageElement.text("중복된 아이디입니다.").css("color", "red");
                    }
                }
            });
        } else {
            $('#usernameMessage').text("아이디는 4~10자여야 합니다.").css("color", "red");
        }
    });
});