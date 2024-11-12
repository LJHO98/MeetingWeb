let seconds; // 남은 시간 변수
let countdown; // 카운트다운을 관리하는 변수
let emailVerified = false; // 이메일 인증 상태 변수

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

$(function(){
    $(".register-Btn").click(function(){
        $(".certification-Btn").prop("disabled", false);
        sendNumber();

    });
});
//인증 번호 전송
function sendNumber(){
    // var token = $("meta[name=_csrf]").attr("content");
    // var header = $("meta[name=_csrf_header]").attr("content");
    $.ajax({
        url:"/join-Mail",
        type:"post",
        dataType:"text",
        data:{"email" : $(".email").val()},
        // beforeSend : function(xhr){
        //     xhr.setRequestHeader(header, token);
        // },
        success:function(data){
            showAlert(data);

            //인증번호 확인버튼 활성화
            $(".certification-Btn").click(function(){
                confirmCode();
                $(".time").show()
                console.log("wswwdwdwdwdwdwdwdwd");
            });
            // "인증번호 발급" 버튼 클릭 이벤트 핸들러
            clearInterval(countdown);
            seconds = 180; // 3분

            updateCountdown();
            // 1초마다 카운트다운 업데이트
            countdown = setInterval(updateCountdown, 1000);
            $(".number").focus();
        },
        error: function(xhr, status, error) {
            console.log("Error Status:", status); // 상태 코드
            console.log("Error:", error); // 에러 메시지
            console.log("Response:", xhr.responseText); // 서버 응답 메시지
            showAlert(xhr.responseText + "\n상태: " + status + "\n에러: " + error);
            $(".register-Btn").prop('disabled', false);
        }


    });

}
//인증 번호 확인
function confirmCode(){
    var token = $("meta[name=_csrf]").attr("content");
    var header = $("meta[name=_csrf_header]").attr("content");
    $.ajax({
        url:"/join-VerifyCode",
        type:"post",
        dataType:"text",
        data:{"number" : $(".number").val(),
            "email" : $(".email").val()},
        // beforeSend : function(xhr){
        //     xhr.setRequestHeader(header, token);
        // },
        success:function(data){
            showAlert(data);
            clearInterval(countdown);
            countdown = undefined;
            $(".time").hide();
            $(".register-Btn").attr('disabled',true);
            $(".certification-Btn").attr('disabled',true);
            emailVerified = true; // 이메일 인증 완료
            $("#emailVerificationMessage").text(""); // 경고 메시지 제거
            // $(".next-Btn").attr("type", "submit");
            $("#submitBt").attr("type", "submit");
        },

        error: function(xhr, status, error) {
            // 오류 발생 시 상세 정보 출력
            showAlert(xhr.responseText + "\n상태: " + status + "\n에러: " + error);
            emailVerified = false; // 인증 실패 시 상태 업데이트
            $("#emailVerificationMessage").text("인증 번호가 잘못되었습니다. 다시 시도하세요."); // 실패 시 경고 메시지
        }


    });
}

//인증코드 유효시간 표시
// 시간을 업데이트하고 화면에 표시하는 함수

const updateCountdown = function() {


    if (seconds >= 0) {
        const minutes = Math.floor(seconds / 60);
        const remainingSeconds = seconds % 60;
        $(".time").text(`${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`);
        seconds--;
    } else {
        clearInterval(countdown);
        alert('인증번호 유효시간이 만료되었습니다.');
        emailVerified = false; // 유효시간 만료 시 이메일 인증 실패
    }
};
$(".joinForm").submit(function(e) {
    if (emailVerified == false) {
        e.preventDefault(); // 폼 제출 방지
        $("#emailVerificationMessage").css("display", "block"); // 경고 메시지 표시
            } else if(emailVerified == true) {
        $("#emailVerificationMessage").css("display", "none"); // 경고 메시지 표시X
    }
});

const showAlert = (message) => {
    const alertBox = $("#alert-box");
    alertBox.text(message);
    alertBox.show();

    setTimeout(function(){
        alertBox.fadeOut();
    }, 3000); // 3초 후에 메시지 사라짐
}
// $("#submitBt").click(function (){
//     let emailVerificationMessage = $('#emailVerificationMessage').val();
//     if (emailVerificationMessage == false){
//         $("#emailVerificationMessage").text("이메일 인증을 완료해야 합니다.");
//     }else if (emailVerificationMessage == true){
//         $("#emailVerificationMessage").text("");
//     }
// });