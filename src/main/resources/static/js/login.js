$(function(){
    $("#register-Btn").click(function(){
        $("#certification-Btn").prop("disabled", false);
        sendNumber();

    });
});
//인증 번호 전송
function sendNumber(){
    // var token = $("meta[name=_csrf]").attr("content");
    // var header = $("meta[name=_csrf_header]").attr("content");
    $.ajax({
        url:"/mail",
        type:"post",
        dataType:"text",
        data:{"email" : $("#email").val()},
        // beforeSend : function(xhr){
        //     xhr.setRequestHeader(header, token);
        // },
        success:function(data){
            showAlert(data);

            //인증번호 확인버튼 활성화
            $("#certification-Btn").click(function(){
                confirmCode();
                console.log("wswwdwdwdwdwdwdwdwd");
            });
            // "인증번호 발급" 버튼 클릭 이벤트 핸들러
            clearInterval(countdown);
            seconds = 180; // 3분

            updateCountdown();
            // 1초마다 카운트다운 업데이트
            countdown = setInterval(updateCountdown, 1000);
            $("#number").focus();
        },
        error: function(xhr, status, error) {
            console.log("Error Status:", status); // 상태 코드
            console.log("Error:", error); // 에러 메시지
            console.log("Response:", xhr.responseText); // 서버 응답 메시지
            showAlert(xhr.responseText + "\n상태: " + status + "\n에러: " + error);
            $("#register-Btn").prop('disabled', false);
        }


    });

}
$(function(){
    $("#next-Btn").click(function(){
        console.log("ddddddddddddddd");
        $.ajax({
            url:"/login/searchId",
            type:"post",
            dataType:"text",
            data:{"email" : $("#pwEmail").val()},
            success: function(data) {
                // 성공적으로 받은 ID 값 사용
                console.log("사용자 ID: " + data);
                $("#idDisplay").text(data); // 예시로 HTML 요소에 ID 표시
            },
            error: function(xhr) {
                alert("User not found");
            }
        });

    });
});
//인증 번호 확인
function confirmCode(){
    var token = $("meta[name=_csrf]").attr("content");
    var header = $("meta[name=_csrf_header]").attr("content");
    $.ajax({
        url:"/verifyCode",
        type:"post",
        dataType:"text",
        data:{"number" : $("#number").val(),
            "email" : $("#email").val()},
        // beforeSend : function(xhr){
        //     xhr.setRequestHeader(header, token);
        // },
        success:function(data){
            $("#next-Btn").prop("disabled", false);
            showAlert(data);

            // $(".next-Btn").attr("type", "submit");
        },
        error: function(xhr, status, error) {
            // 오류 발생 시 상세 정보 출력
            showAlert(xhr.responseText + "\n상태: " + status + "\n에러: " + error);
            $("#register-Btn").prop('disabled',false);
        }


    });
}

//인증코드 유효시간 표시
// 시간을 업데이트하고 화면에 표시하는 함수
let seconds; // 남은 시간 변수
let countdown; // 카운트다운을 관리하는 변수
const updateCountdown = function() {


    if (seconds >= 0) {
        const minutes = Math.floor(seconds / 60);
        const remainingSeconds = seconds % 60;
        $(".time").text(`${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`);
        seconds--;
    } else {
        clearInterval(countdown);
        alert('인증번호 유효시간이 만료되었습니다.');
    }
};

const showAlert = (message) => {
    const alertBox = $("#alert-box");
    alertBox.text(message);
    alertBox.show();

    setTimeout(function(){
        alertBox.fadeOut();
    }, 3000); // 3초 후에 메시지 사라짐
}



$(function(){
    $("#pwRegister-Btn").click(function(){
        $("#pwCertification-Btn").prop("disabled", false);
        pwSendNumber();

    });
});
//인증 번호 전송
function pwSendNumber(){
    // var token = $("meta[name=_csrf]").attr("content");
    // var header = $("meta[name=_csrf_header]").attr("content");
    $.ajax({
        url:"/join-Mail",
        type:"post",
        dataType:"text",
        data:{"email" : $("#pwEmail").val()},
        // beforeSend : function(xhr){
        //     xhr.setRequestHeader(header, token);
        // },
        success:function(data){
            showAlert(data);

            //인증번호 확인버튼 활성화
            $("#pwCertification-Btn").click(function(){
                pwConfirmCode();
                console.log("wswwdwdwdwdwdwdwdwd");
            });
            // "인증번호 발급" 버튼 클릭 이벤트 핸들러
            clearInterval(countdown);
            seconds = 180; // 3분

            updateCountdown();
            // 1초마다 카운트다운 업데이트
            countdown = setInterval(updateCountdown, 1000);
            $("#pwNumber").focus();
        },
        error: function(xhr, status, error) {
            console.log("Error Status:", status); // 상태 코드
            console.log("Error:", error); // 에러 메시지
            console.log("Response:", xhr.responseText); // 서버 응답 메시지
            showAlert(xhr.responseText + "\n상태: " + status + "\n에러: " + error);
            $("#pwRegister-Btn").prop('disabled', false);
        }


    });

}
$(function(){
    $("#change-Btn").click(function(){
        $.ajax({
            url: "/login/searchPw",
            type: "post",
            contentType: "application/json",  // JSON 형식으로 전송
            dataType: "text",
            data: JSON.stringify({ // JSON.stringify를 사용하여 JSON 형식으로 변환
                "pwEmail": $("#pwEmail").val(),
                "pw": $("#pw").val(),
                "pwCheck": $("#pwCheck").val()
            }),
            success: function(data) {
                console.log(data);
            },
            error: function(xhr) {
                alert("User not found");
            }
        });
    });
});

//인증 번호 확인
function pwConfirmCode(){
    // var token = $("meta[name=_csrf]").attr("content");
    // var header = $("meta[name=_csrf_header]").attr("content");
    $.ajax({
        url:"/join-VerifyCode",
        type:"post",
        dataType:"text",
        data:{"number" : $("#pwNumber").val(),
            "email" : $("#pwEmail").val()},
        // beforeSend : function(xhr){
        //     xhr.setRequestHeader(header, token);
        // },
        success:function(data){
            $("#pwNext-Btn").prop("disabled", false);
            showAlert(data);

            // $(".next-Btn").attr("type", "submit");
        },
        error: function(xhr, status, error) {
            // 오류 발생 시 상세 정보 출력
            showAlert(xhr.responseText + "\n상태: " + status + "\n에러: " + error);
            $("#pwRegister-Btn").prop('disabled',false);
        }


    });
}
