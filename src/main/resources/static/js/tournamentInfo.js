 $(document).ready(function() {
            // 모달 열기
            $('#applyBtn').on('click', function() {
                $('#applicationModal').fadeIn();

                // 모달이 열릴 때 라디오 버튼과 체크박스 초기화
                $('input[name="groupId"]').prop('checked', false);
                $('#agreeCheck').prop('checked', false);
            });

            // 모달 닫기
            $('.close').on('click', function() {
                $('#applicationModal').fadeOut();
            });

            // 모달 외부 클릭 시 닫기
            $(window).on('click', function(event) {
                if ($(event.target).is('#applicationModal')) {
                    $('#applicationModal').fadeOut();
                }
            });

            // 신청 버튼 클릭 시 유효성 검사 및 폼 제출
            $('#submitApplication').on('click', function() {
                const selectedGroup = $('input[name="groupId"]:checked').val();
                const agreeCheck = $('#agreeCheck').is(':checked');

                if (!selectedGroup) {
                    alert("모임을 선택해주세요.");
                    return;
                }

                if (!agreeCheck) {
                    alert("대회 규정에 동의해야 합니다.");
                    return;
                }

                // 유효성 검사 통과 시 폼 제출
                $('#applyForm').submit();
            });
 })