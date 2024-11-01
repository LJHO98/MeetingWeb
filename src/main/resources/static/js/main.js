// 리스트 아이콘 클릭
$(document).off('click', '#listIcon').on('click', '#listIcon', function() {
    var $side = $("#side");
    var $icon = $(this).find("i");
    console.log("리스트 아이콘 클릭됨");

    // toggle 클래스를 추가/제거하여 사이드바를 열고 닫음
    if ($side.hasClass("toggle")) {
        $side.removeClass("toggle"); // 사이드바 열기
        console.log("사이드바 열림");
        $icon.removeClass("bi-x").addClass("bi-list"); // 아이콘 변경
    } else {
        $side.addClass("toggle"); // 사이드바 닫기
        console.log("사이드바 닫힘");
        $icon.removeClass("bi-list").addClass("bi-x"); // 아이콘 변경
    }
});
