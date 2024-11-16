// 페이지에서 전달된 Thymeleaf 변수 사용
var groupList = window.groupList || [];
var gang = window.trnFormat || [];
var isLeader = window.isLeader || false;
var format = gang;

groupList = groupList.map(group => {
    return {
        ...group,
        name: decodeURI((group.name))
    };
});


var depth=new Array();
do{
    depth.push({라운드:gang,풀:0});
    gang/=2;
}while(gang>=1);



// 아래 세가지 만 변경 가능!!!!!
var team_width=160; // 모임 너비
var team_height=120;// 모임 높이
var wadd=0; // 라운드별 간격 조절용 변수 ! 이것만 변경!!!!


var line_depth = [team_height/4, team_height, team_height*2+team_height/2,team_height*5+team_height/2];
console.log(line_depth);
console.log(groupList); // groupList가 제대로 값이 전달되는지 확인
for(var i in groupList){
    var bracket = Math.floor(groupList[i].matchNumber / 2) + 1; // 브라켓 번호 계산
    var $d=$(`<div class="team quarterfinals" data-num="${i}" >
            <img src="${groupList[i].profileImgUrl}">
            <span data-id="${groupList[i].groupId}" data-match="${groupList[i].matchNumber}" data-bracket="${groupList[i].bracketNumber}"
                    data-status="${groupList[i].completed ? 'completed' : 'pending'}">${groupList[i].name}</span>
            <span class='score'>${groupList[i].score}</span>
        </div>`);
    $d.css({"width":team_width,"height":team_height});
    if (groupList[i].isCompleted) {
        $team.addClass("completed"); // 완료 상태 시각적 표시
    }
    $(".tournament").append($d);
}

var hg=h=100; // top 값
var hinc=team_height+team_height/2; // top 증가 값  .team의  높이에 따라 변해야 되는값
var depth_hinc=team_height/2+team_height/4; // 라운드마다 top 시작 위치 증가값
var w=20; //라운드 간격
var tp=0; //모임영역 인덱스
var line_width=4;


for(var i in depth){
    var k=1;
    for(; k<=depth[i].라운드; k++){
        if( tp == $(".team").length) break;
        $(".team").eq(tp).css("position","absolute");
        $(".team").eq(tp).css("top",hg);
        $(".team").eq(tp++).css("left",w);
        depth[i].풀++;
        console.log(tp+" "+depth[i].라운드);
        hg+=hinc;
    }
    // if( tp == $(".team").length && k-1!=depth[i].라운드) break;
    // depth[i].풀=1;
    if(i!=0) depth_hinc=depth_hinc*2;
    hg=h=h+depth_hinc;
    hinc *=2;
    w+=(team_width*2)+wadd; // 라운드별 간격 조절시 현재 계산식에  늘리고 싶은만큼 + 하기
}
console.log(depth);
if(gang < $(".team").length){
    var end=0;
    var i=0;
    for(var di=1; di<depth.length &&depth[di].풀!=0; di++){
        end+=depth[di].풀*2;
        console.log(di);
        for(; i< end; i++){
            var left = $(".team").eq(i-1);
            var left_offset=left.offset();
            var right=$(".team").eq(i);
            var right_offset=right.offset();
            // for(var k=1; k<=2; k++){
            line_make(i,di-1);
            //}
            if(i%2==0){
                var $line4 = $(`<div class='line_dp'></div>`);
                $line4.css("width",(team_width/2)+(wadd/2)+line_width+"px");  //  위에  라운드별 간격에 추가 한값의
                $line4.css("border",line_width/2+"px solid red");
                $line4.css("position","absolute");
                $line4.css("top",$(".team").eq(i).offset().top+($(".team").outerHeight())+line_depth[di-1]-2);
                $line4.css("left",$(".team").eq(i).offset().left+$(".team").outerWidth()+team_width/2-line_width+(wadd/2));
                $(".tournament").append($line4);
            }
        }
    }
    moveAllLines(-130, -606);
}

function line_make(idx,depth_idx){
    var line_color="black";

    var $line = $(`<div class='line_dp'></div>`);
    $line.css("width",(team_width/2)+(wadd/2)+"px");
    $line.css("height",((team_height/2+team_height/4)*(2**depth_idx))+"px");
    $line.css("position","absolute");
    if(idx%2==0){
        if($(".team").eq(idx).find(".score").text() > $(".team").eq(idx+1).find(".score").text()) line_color="red";
        $line.css({"border-top":line_width+"px solid "+line_color,"border-right":line_width+"px solid "+line_color });
        $line.css("top",$(".team").eq(idx).offset().top+($(".team").outerHeight()/2)-2);
    }else{
        if($(".team").eq(idx).find(".score").text() > $(".team").eq(idx-1).find(".score").text()) line_color="red";
        $line.css({"border-bottom":"4px solid "+line_color,"border-right":"4px solid "+line_color});
        $line.css("top",$(".team").eq(idx).offset().top-(line_depth[depth_idx]));
        console.log("bbb  "+line_depth)
    }

    $line.css("left",$(".team").eq(idx).offset().left+$(".team").width()+22);
    $(".tournament").append($line);
}


//선 위치 조정
function moveAllLines(topOffset, leftOffset) {
    $(".line_dp").each(function() {
        var currentTop = parseFloat($(this).css("top"));   // 현재 top 값 가져오기
        var currentLeft = parseFloat($(this).css("left")); // 현재 left 값 가져오기

        // top과 left를 offset만큼 이동
        $(this).css({
            "top": (currentTop + topOffset) + "px",   // 세로 이동
            "left": (currentLeft + leftOffset) + "px" // 가로 이동
        });
    });
}

// var pt=``;
// var ft=gang;
// for(var i in groupList) {
//     if(i==gang) {
//         var $ptbox = $('<div class="participatingBox"></div>');
//         $ptbox.append(pt);
//         ft = ft/2;
//         gang = gang+ft;
//         $(".tournament").append($ptbox);
//         pt=``;
//     }
//     pt +=`<div class="team quarterfinals" data-num="${i}">
//         <img src="${groupList[i].profileImg}">
//         <span data-id="${groupList[i].groupId}" data-match="${groupList[i].matchNumber}">${groupList[i].name}</span>
//         <span>${groupList[i].score}</span>
//     </div>`;
// }
// var $ptbox = $('<div class="participatingBox"></div>');
// $ptbox.append(pt);
// $(".tournament").append($ptbox);

$(".team").on("click", function() {
    var aNum = $(this).data("num") + 1;
    var bNum = aNum % 2 == 0 ? aNum - 1 : aNum + 1;
    var agid = $(this).find("span").data("id");
    var aMatchNum = $(this).find("span").data("match");
    var bgid = $(".team").eq(bNum - 1).find("span").data("id");
    var bMatchNum = $(".team").eq(bNum - 1).find("span").data("match");
    var aName = $(this).find("span[data-id]").text();
    var bName = $(".team").eq(bNum - 1).find("span[data-id]").text();

    $(".team-selection .a1").data("id", agid).text(aName);
    $(".team-selection .b1").data("id", bgid).text(bName);
    $(".team-selection .a1").data("match", aMatchNum);
    $(".team-selection .b1").data("match", bMatchNum);

    var currentBracket = $(this).find("span").data("bracket");
    var prevBracket = currentBracket - 1;

    console.log("Current Bracket:", currentBracket, "Previous Bracket:", prevBracket);

    if (!isLeader) {
        alert("권한이 없습니다.");
        return;
    }

// 이전 브라켓 상태 확인
    if (prevBracket > 0) {
        var prevBracketTeams = $(".team span").filter(function () {
            return $(this).data("bracket") === prevBracket;
        });

        if(currentBracket === (format*2) - 1){
            alert("우승자 결과는 수정할 수 없습니다.");
            return;
        }
        if (prevBracketTeams.length === 0) {
            alert("이전 브라켓이 존재하지 않습니다.");
            return;
        }

        var prevCompleted = true;
        prevBracketTeams.each(function () {
            if ($(this).data("status") !== "completed") {
                prevCompleted = false;
                console.log("Pending Match Found:", $(this).data("id"));
            }
        });

        if (!prevCompleted) {
            alert("이전 브라켓이 완료되지 않았습니다.");
            return;
        }
    }


    // 권한이 있는 경우 모달 열기
    $("#resultModal").modal("show");
});

$(".resSelect").on("click", function() {
    // $(".resSelect").removeAttr("style");
    // $(this).css("color", "red");
    $(".resSelect").css("border", "none"); // 모든 선택 요소의 스타일 초기화
    $(this).css("border", "3px solid red"); // 선택된 팀에 빨간 테두리
    $("#win").val($(this).data("id"));
    $("#matchNum").val($(this).data("match"));
});

$("#resultSave").on("click", function() {
    var wingid = $("#win").val();
    var scoreA = $("#scoreA").val();
    var scoreB = $("#scoreB").val();
    var tid = $("input[name='tournamentId']").val();
    var matchNum = $("#matchNum").val();

    if (!wingid || !scoreA || !tid || !matchNum || !scoreB) {
        alert("모든 필드를 입력해주세요.");
        return;
    }

    // 무승부 검사: scoreA와 scoreB가 같을 때 경고 메시지 표시 및 초기화
    if (!isNaN(scoreA) && !isNaN(scoreB) && scoreA === scoreB) {
        alert("무승부는 기록할 수 없습니다. 다른 점수를 입력해주세요.");
        $("#scoreA").val(0);
        $("#scoreB").val(0);
        return;
    }

    // location.href = "/tournament/matchResult?wgid=" + wingid + "&tid=" + tid + "&scoreA=" + scoreA + "&scoreB="+ scoreB + "&matchNum=" + matchNum;
    $.post("/tournament/matchResult", {
        wgid: wingid,
        tid: tid,
        scoreA: scoreA,
        scoreB: scoreB,
        matchNum: matchNum
    }).done(function () {
        // 경기 결과 저장 성공 시 작업 수행
        console.log("경기 결과가 성공적으로 저장되었습니다.");

        // 특정 브라켓의 상태를 업데이트하는 함수 호출
        var currentBracket = $(".team span").filter(function () {
            return $(this).data("match") === parseInt(matchNum);
        }).data("bracket");

        if (currentBracket !== undefined) {
            completeBracketSpans(currentBracket); // 브라켓 상태를 업데이트하는 함수 호출
        } else {
            console.error("Bracket number not found for Match Number:", matchNum);
        }


        alert("경기 결과가 저장되었습니다.");
        location.reload(); // 새로고침으로 UI 갱신
    }).fail(function (xhr) {
        if(xhr.responseText){
            alert(xhr.responseText);
            $("#resultModal").modal("hide");
        }else{
            alert("경기 결과 저장 중 오류가 발생했습니다.");
            $("#resultModal").modal("hide");
        }

    });
});

function completeBracketSpans(bracketNumber) {
    // 특정 브라켓에 해당하는 모든 span 태그 필터링
    var bracketSpans = $(".team span").filter(function () {
        return $(this).data("bracket") === bracketNumber;
    });

    if (bracketSpans.length === 0) {
        console.error("No spans found for Bracket Number:", bracketNumber);
        return;
    }

    // 필터링된 span 태그의 상태를 'completed'로 변경
    bracketSpans.each(function () {
        $(this).data("status", "completed"); // data 속성 업데이트
        $(this).attr("data-status", "completed"); // DOM 속성 업데이트
        $(this).closest(".team").addClass("completed"); // .team에 completed 클래스 추가
    });

    console.log("Updated spans for Bracket Number:", bracketNumber);
}

function validateScores() {
    var scoreA = parseInt($("#scoreA").val(), 10);
    var scoreB = parseInt($("#scoreB").val(), 10);

    // 음수 방지: 유효하지 않은 경우 값을 초기화
    if ($("#scoreA")[0].validity.valid === false) {
        $("#scoreA").val(0);
    }
    if ($("#scoreB")[0].validity.valid === false) {
        $("#scoreB").val(0);
    }


}

$(document).ready(function() {
    var errorMessage = $("#error-message").text();
    if (errorMessage) {
        alert(errorMessage);
    }
});

$(document).ready(function () {

    if (!isLeader) {
        $("button#resultSave").prop("disabled", true); // 결과 저장 버튼 비활성화
        $("form[action='/match'] button").remove(); // 대진표 섞기 버튼 숨기기
    }
});

// $(document).ready(function() {
//     var errorMessage = $("#error-message").text();
//     if (errorMessage) {
//         $("#modal-error-message").text(errorMessage);
//         $("#errorModal").modal('show');
//     }
// });

