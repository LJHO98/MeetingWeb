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

// 인증 번호 보이기
$(document).ready(function() {
    $(".register-Btn").on("click", function() {
        $("#verificationBox").show();
    });
});

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

let isUsernameChecked = false;

// 아이디, 중복확인 기능 및 유효성 검사
$(document).ready(function() {
    // 중복확인 버튼 클릭 이벤트
    $('#checkUsernameButton').on('click', function() {
        let userName = $('#userName').val();
        let usernameRegex = /^[a-zA-Z0-9]*$/;
        let koreanRegex = /[ㄱ-ㅎ|ㅏ-ㅣ|가-힣]/;

        if (userName.trim() === "") {
            $('#usernameMessage').text("아이디를 입력해주세요.").css("color", "red");
            isUsernameChecked = false;
        } else if (userName.length >= 4 && userName.length <= 10) {
            if (koreanRegex.test(userName)) {
                $('#usernameMessage').text("아이디에 한글을 사용할 수 없습니다.").css("color", "red");
                isUsernameChecked = false;
            } else if (usernameRegex.test(userName)) {
                $.ajax({
                    type: "POST",
                    url: "/start/check-username",
                    data: { userName: userName },
                    success: function(response) {
                        let messageElement = $('#usernameMessage');
                        if (response) {
                            messageElement.text("사용 가능한 아이디입니다.").css("color", "green");
                            isUsernameChecked = true;
                        } else {
                            messageElement.text("중복된 아이디입니다.").css("color", "red");
                            isUsernameChecked = false;
                        }
                    },
                    error: function() {
                        $('#usernameMessage').text("중복 확인 중 오류가 발생했습니다. 다시 시도해주세요.").css("color", "red");
                        isUsernameChecked = false;
                    }
                });
            } else {
                $('#usernameMessage').text("아이디에 특수문자, 띄워쓰기를 사용할 수 없습니다.").css("color", "red");
                isUsernameChecked = false;
            }
        } else {
            $('#usernameMessage').text("아이디는 4~10자여야 합니다.").css("color", "red");
            isUsernameChecked = false;
        }
    });

    // 아이디 입력 필드 변경 시 중복확인 상태 초기화
    $('#userName').on('input', function() {
        isUsernameChecked = false; // 중복확인 상태 초기화
        $('#usernameMessage').text("").css("color", "");
    });

    // 가입 버튼 클릭 시 중복확인 여부 및 유효성 검사
    $('#submitBt').click(function(e) {
        let userName = $('#userName').val();
        let password = $('#password').val();
        let usernameMessage = $('#usernameMessage');

        // 아이디 유효성 검사
        if (userName.trim() === "") {
            e.preventDefault();
            usernameMessage.text("아이디는 필수입니다.").css("color", "red").show();
            return;
        }

        // 중복 확인 여부 검사
        if (!isUsernameChecked) {
            e.preventDefault();
            usernameMessage.text("아이디 중복확인을 해주세요.").css("color", "red");
            return;
        }
    });
});

// 비멀번호 유효성 검사
$(document).ready(function() {
    // 비밀번호 유효성 검사 이벤트 리스너 추가
    $('#password').on('input', function() {
        let password = $('#password').val();
        let passwordRequiredMessage = $('#passwordRequiredMessage');

        if (password.trim() === "") {
            passwordRequiredMessage.text("비밀번호는 필수입니다.").css("display", "block");
        }
    });

    // 가입 버튼 클릭 시 비밀번호 유효성 검사
    $('#submitBt').click(function(e) {
        let password = $('#password').val();
        let passwordRequiredMessage = $('#passwordRequiredMessage');

        // 비밀번호 유효성 검사 (최종 확인)
        if (password.trim() === "") {
            e.preventDefault();
            passwordRequiredMessage.text("비밀번호는 필수입니다.").css("display", "block");
        }else {
            passwordRequiredMessage.css("display", "none");
        }
    });
});

// 비밀번호 확인 기능 및 유효성 검사
$(document).ready(function() {
    // 비밀번호 확인 필드가 변경될 때마다 비교
    $('#passwordConfirm').on('input', function() {
        var password = $('#password').val(); // 비밀번호 필드 값
        var passwordConfirm = $('#passwordConfirm').val(); // 비밀번호 확인 필드 값
        var mismatchMessage = $('#passwordMismatchMessage'); // 비밀번호 불일치 메시지

        // 비밀번호가 일치하지 않으면 메시지 표시
        if (password !== passwordConfirm) {
            mismatchMessage.text("비밀번호가 일치하지 않습니다.").css("color", "red").show();
        } else {
            mismatchMessage.text("비밀번호가 일치합니다.").css("color", "green").show();
        }
    });

    // 가입하기 버튼 클릭 시 비밀번호 확인 유효성 검사 추가
    $('#submitBt').click(function(e) {
        var password = $('#password').val(); // 비밀번호 값
        var passwordConfirm = $('#passwordConfirm').val(); // 비밀번호 확인 값
        var mismatchMessage = $('#passwordMismatchMessage'); // 비밀번호 불일치 메시지

        // 비밀번호가 일치하지 않으면 메시지 표시
        if (password !== passwordConfirm) {
            e.preventDefault();  // 폼 제출 방지
            mismatchMessage.text("비밀번호가 일치하지 않습니다.").css("color", "red").show();
        }
    });
});

$(function(){
    $(".register-Btn").click(function(){
        $(".certification-Btn").prop("disabled", false);
        sendNumber();

    });
});

// 이메일 유효성 검사
$(document).ready(function() {
    // 가입하기 버튼 클릭 시 이메일 필수 검사
    $('#submitBt').click(function(e) {
        var email = $('.email').val();  // 이메일 필드 값
        var emailMessage = $('#emailVerificationMessage');  // 이메일 필수 메시지 요소

        // 이메일이 비어 있으면
        if (email.trim() === "") {
            e.preventDefault();  // 폼 제출 방지
            emailMessage.text("이메일은 필수입니다.").css("color", "red").show();
        } else {
            emailMessage.hide();  // 이메일이 유효하면 메시지 숨기기
        }
    });

    // 이메일 인증 버튼 클릭 시 이메일 필수 여부 체크
    $(".register-Btn").click(function(){
        var email = $(".email").val(); // 이메일 값 가져오기
        var emailMessage = $("#emailVerificationMessage");

        // 이메일이 비어 있으면
        if (email.trim() === "") {
            emailMessage.text("이메일을 입력해주세요.").css("color", "red").show();
        } else {
            // 이메일 인증 절차 시작
            $(".certification-Btn").prop("disabled", false);
        }
    });
});


// 이름 유효성 검사
$(document).ready(function() {
    $('#submitBt').click(function(e) {
        var name = $('#nameInput').val(); // 이름 입력 필드 값 가져오기
        var nameMessage = $('#nameMessage'); // 경고 메시지 요소

        // 이름이 비어 있으면
        if (name.trim() === "") {
            e.preventDefault(); // 폼 제출 방지
            nameMessage.text('이름을 입력해주세요'); // 메시지 설정
            nameMessage.show(); // 경고 메시지 표시
        } else {
            nameMessage.hide(); // 이름이 입력되었으면 경고 메시지 숨기기
        }
    });
});


// 생년월일 유효성 검사 및 기능
document.addEventListener("DOMContentLoaded", function() {
    const birthDateInput = document.getElementById("birthdate");
    const birthDateMessage = document.getElementById("birthDateMessage");

    // 가입하기 버튼 클릭 시 생년월일 필수 검사
    document.getElementById('submitBt').addEventListener('click', function(e) {
        // 생년월일이 비어 있으면
        if (!birthDateInput.value) {
            e.preventDefault();  // 폼 제출 방지
            birthDateMessage.textContent = "생년월일은 필수입니다.";
            birthDateMessage.style.display = "block";
        } else {
            birthDateMessage.style.display = "none";
        }
    });

    // 생년월일 필드 값 변경 시 14세 이하 검사
    birthDateInput.addEventListener("change", function() {
        const inputDate = new Date(birthDateInput.value);
        const today = new Date();
        const age = today.getFullYear() - inputDate.getFullYear();
        const monthDifference = today.getMonth() - inputDate.getMonth();
        const dayDifference = today.getDate() - inputDate.getDate();

        const isUnder14 = age < 14 || (age === 14 && (monthDifference < 0 || (monthDifference === 0 && dayDifference < 0)));

        if (isUnder14) {
            birthDateMessage.textContent = "14세 이하의 사용자는 가입이 불가능합니다.";
            birthDateMessage.style.display = "block";
        } else {
            birthDateMessage.style.display = "none";
        }
    });
});


// 성별 유효성 검사
$(document).ready(function() {
    $('#submitBt').click(function(e) {
        let gender = $("input[name='gender']:checked").val();
        let genderMessage = $('#genderMessage');
        if (!gender) {
            e.preventDefault();
            genderMessage.remove();
            let errorMessage = $('<div id="genderMessage" style="color: red;">성별을 선택해주세요.</div>');
            $('.gender-options').after(errorMessage);
        } else {
            genderMessage.remove();
        }
    });
});


// 카테고리 유효성 검사
$(document).ready(function() {
    $('#submitBt').click(function(e) {
        let checkedCategories = $('.categoryCheck:checked').length;
        let categoryMessage = $('#categoryMessage');
        if (checkedCategories === 0) {
            e.preventDefault();
            categoryMessage.remove();
            let errorMessage = $('<div id="categoryMessage" style="color: red;">최소 1개 이상의 카테고리를 선택해주세요.</div>');
            $('.categoryList').after(errorMessage);
        } else {
            categoryMessage.remove();
        }
    });
});


// 주소 유효성 검사
$(document).ready(function() {
    $('#submitBt').click(function(e) {
        let address = $('#activityArea').val();
        let addressMessage = $('#addressMessage');
        if (address.trim() === "") {
            e.preventDefault();
            addressMessage.remove();  // 기존 메시지를 제거하고
            let errorMessage = $('<div id="addressMessage" style="color: red;">주소를 입력해주세요.</div>');
            $('.joinCheck').last().after(errorMessage);
        } else {
            addressMessage.remove();
        }
    });
});