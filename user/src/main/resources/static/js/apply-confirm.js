const selectedUserIds = new Set();
let chkUser = new Set(); // 👈 배열 대신 Set으로 선언
let chkUserId = new Set(); // 👈 배열 대신 Set으로 선언
// let selectedUserMap = new Map(); // key: userNo, value: userId
// const initialAppliedUsers = new Set(); // 👈 최초 신청자 고정 보관함

const selectedUserMap = new Map();     // key: userNo, value: userId (최종 서버 전송용 전체 선택 목록)
const dateSelectedMap = new Map();     // key: 날짜(예: "2026-10-10"), value: Set(그 날짜에 체크된 userNo들)
const dateInitialAppliedMap = new Map(); // 💡 [추가] 날짜별 최초 신청자(이미 신청된 유저) Set 맵
const initialAppliedUsers = new Set(); // 최초 신청자 보관함

// const apply = /*[[${apply}]]*/ [];
const userInfo = /*[[${userInfo}]]*/ [];

let selectMap = new Map(); // key: userNo, value: userId
let initialUser = new Set(); // 👈 최초 신청자 고정 보관함

document.addEventListener('DOMContentLoaded', function() {
    var statusButtons ;
    const applicantItems = document.querySelectorAll('.applicant-item');
    for(var i = 0; i < apply.length; i++) {
        applicantItems.forEach(item => {
            const chooseStatus = item.dataset.chooseStatus;
            statusButtons = item.querySelectorAll('.button_status'+i);

            statusButtons.forEach(button => {
                const targetStatus = button.dataset.status;
                if (chooseStatus === targetStatus) {
                    button.classList.add('choice_status');
                } else {
                    button.classList.remove('choice_status');
                }
            });
        });
    }

    history.pushState(null, null, location.href);
    // 2. 사용자가 뒤로가기 버튼을 누를 때 발생하는 'popstate' 이벤트를 감지합니다.
    window.addEventListener('popstate', function(event) {
        const isConfirmed = confirm("정말 페이지를 나가시겠습니까? 변경된 내용이 저장되지 않을 수 있습니다.");

        if (isConfirmed) {
            // [확인]을 누른 경우: 진짜로 뒤로가기 수행
            history.back();
        } else {
            // [취소]를 누른 경우: 현재 페이지에 머물도록 히스토리 다시 복구
            history.pushState(null, null, location.href);
        }
    });
});

function openChkPopup(url) {
    // 1. 서버에 해당 url의 HTML 내용을 요청
    fetch(url)
        .then(response => {
            if (!response.ok) throw new Error('네트워크 응답 실패');
            return response.text(); // 페이지를 텍스트(HTML)로 읽어옴
        })
        .then(html => {
            // 2. 모달 컨텐츠 영역에 서버에서 가져온 HTML 주입
            document.getElementById('modalContent').innerHTML = html;
            // 3. 모달 display를 flex로 바꿔서 화면에 노출
            document.getElementById('significantModal').style.display = 'flex';

        })
        .catch(error => {
            console.error('모달 로드 중 에러 발생:', error);
            alert('페이지를 불러오는 중 오류가 발생했습니다.');
        });
}

// 특이사항 팝업
function openSignificantModal(element) {
    const statusNow = element.dataset.statusNow; // 💡 statusNow 값 가져오기
    // 1. 서버에 해당 url의 HTML 내용을 요청
    fetch(element.dataset.url)
    .then(response => {
        if (!response.ok) throw new Error('네트워크 응답 실패');
        return response.text(); // 페이지를 텍스트(HTML)로 읽어옴
    })
    .then(html => {
        const modalContent = document.getElementById('modalContent');
        modalContent.innerHTML = html;

        // ============================================================
        // 💡 3. statusNow와 일치하는 버튼 찾아서 색상(active-status) 입히기
        // ============================================================
        if (statusNow != null && statusNow !== '') {
            // 모달 내부의 button_status 클래스 중 data-status가 statusNow와 같은 요소 탐색
            const targetBtn = modalContent.querySelector(`.button_status[data-status='${statusNow}']`);

            if (targetBtn) {
                // 기존에 선택된 버튼들의 active 클래스 제거
                modalContent.querySelectorAll('.button_status').forEach(btn => btn.classList.remove('active-status'));

                // 현재 statusNow와 일치하는 버튼에 active-status 추가
                targetBtn.classList.add('active-status');
            }
        }
        // ============================================================

        // 4. 모달 출력
        document.getElementById('significantModal').style.display = 'flex';

    })
    .catch(error => {
        console.error('모달 로드 중 에러 발생:', error);
        alert('페이지를 불러오는 중 오류가 발생했습니다.');
    });
}

// 팝업 닫기
function closeModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = 'none';
    } else {
        console.error("해당 ID의 모달 요소를 찾을 수 없습니다.");
    }
}

async function imgDownload(code) {
    if (window.event) {
        window.event.preventDefault();
    }
    const campaignArea = document.getElementById('campaignArea');
    const campaignIdDown = document.getElementById('campaignIdDown');
    const campaignTitleDown = document.getElementById('campaignTitleDown');
    const campaignIdToDownload = Number(campaignArea.dataset.campaignId);
    const campaignId = Number(campaignIdDown.dataset.campaignId);
    const campaignTitle = String(campaignTitleDown.dataset.campaignTitle);

    if (!campaignArea) {
        alert('다운로드할 캠페인 정보를 찾을 수 없습니다.');
        return;
    }

    if (isNaN(campaignIdToDownload) || campaignIdToDownload <= 0) {
        alert('유효한 캠페인 ID를 찾을 수 없습니다.');
        return;
    }

    try {
        const requestData = {
            campaignId: campaignId,
            campaignTitle: campaignTitle,
            code: code
        };
        const response = await fetch('/file/imgDownload', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify(requestData)
        });

        if (!response.ok) {
            if (response.status === 400) { alert('잘못된 요청입니다. 캠페인 ID가 전달되지 않았습니다.'); }
            else if (response.status === 404) { alert('요청된 캠페인 또는 파일을 찾을 수 없습니다.'); }
            else if (response.status === 500) { alert('서버 오류로 ZIP 파일 생성에 실패했습니다.'); }
            else { alert(`파일 다운로드 요청 중 오류 발생: ${response.status}`); }
            console.error('ZIP 다운로드 실패:', response.status, await response.text());
            return;
        }

        const blob = await response.blob();
        const contentDisposition = response.headers.get('Content-Disposition');
        let filename = `campaign_${campaignIdToDownload}_files.zip`;

        if (contentDisposition && contentDisposition.indexOf('filename=') !== -1) {
            filename = contentDisposition.split('filename=')[1].split(';')[0].replace(/"/g, '');
            try { filename = decodeURIComponent(filename); } catch (e) { console.warn('filename 디코딩 실패:', e); }
        }

        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = filename;
        document.body.appendChild(a);
        a.click();
        window.URL.revokeObjectURL(url);
        document.body.removeChild(a);

        alert('캠페인 파일들이 ZIP 형태로 다운로드됩니다.');

    } catch (error) {
        console.error('캠페인 ZIP 파일 다운로드 요청 중 오류 발생:', error);
        alert('캠페인 ZIP 파일 다운로드에 실패했습니다. 다시 시도해 주세요.');
    }
}

function downloadExcel(campaignId, applyDate, timeSegment) {
    if (window.event) {
        window.event.preventDefault();
    }
    if (!campaignId) {
        console.error("캠페인 ID가 유효하지 않습니다.");
        alert("다운로드할 캠페인 ID가 올바르지 않습니다.");
        return;
    }
    const downloadUrl = `/file/excelDownload/${campaignId}/${applyDate}/${timeSegment}`;

    // window.location.href = downloadUrl;
    window.top.location.href = downloadUrl;
}

function leaveWork(buttonElement) {
    if (window.event) {
        window.event.preventDefault();
    }

    const btnLeader = document.querySelector(`.btn_leader`);

    if (btnLeader) {
        if (btnLeader.getAttribute('data-clicked') === 'true') {
            return;
        }

        // 1. [클릭 직후] 잠금 표식을 남기고, '처리 중'을 뜻하는 클래스를 일시적으로 부여합니다.
        btnLeader.setAttribute('data-clicked', 'true');
        btnLeader.classList.add('status-processing'); // 🟡 처리 중 색상용 클래스 추가
        btnLeader.style.pointerEvents = 'none';
    }

    const clickedButton = event.target; // ⭐ 1. 클릭된 버튼을 먼저 변수에 저장! ⭐
    const applicantItems = document.querySelectorAll('.applicant-item');
    const userNo = buttonElement.dataset.userNo; // ⭐ data-user-no 속성 값 읽기 ⭐
    const status = buttonElement.dataset.status; // ⭐ data-status 속성 값 읽기 ⭐
    const applyDate = buttonElement.dataset.applyDate; // ⭐ data-status 속성 값 읽기 ⭐
    const campaignId = buttonElement.dataset.campaignId; // ⭐ data-status 속성 값 읽기 ⭐

    const requestBody = {
        userNo: userNo,
        campaignId: campaignId,
        applyDate: applyDate,
        status: "9"
    };

    if (!status && !userNo) {
        console.error("상태 또는 사용자 정보가 올바르지 않습니다.", {status: status, userNo: userNo});
        alert("상태 변경에 필요한 정보가 부족합니다. 다시 시도해 주세요.");
        return;
    }

    const url = `/apply/updateStatusLeader`;

    fetch(url, {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken
        },
        body: JSON.stringify(requestBody)
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(errorData => {
                throw new Error(errorData.message || '상태 변경 중 알 수 없는 오류가 발생했습니다.');
            });
        }
        return response.json();
    })
    .then(data => {
        if(status == "0"){
            if("${status}" == 2){
                alert("출근 or 지각 터치 후 퇴근 버튼 터치 하세요.");
                return ;
            }
        } else{
            if(status == "1" || status == "3"){
                if("${status}" == 2){
                    alert("출결 확인");
                    button.classList.remove('active-status');
                    return ;
                }
            }
        }
        alert(data.message);

        const buttons = document.querySelectorAll(`a[th-onclick$=", [[${userNo}]])"]`);
        buttons.forEach(btn => btn.classList.remove('active-status'));
        clickedButton.classList.add('active-status');
    })
    .catch(error => {
        console.error('상태 변경 중 오류 발생:', error);
        alert('상태 변경에 실패했습니다: ' + error.message);
    })
    .finally(() => {
        // 3. [통신 종료] 성공하든 실패하든 3초 뒤에 연타 잠금과 '처리 중' 디자인을 걷어냅니다.
        // (성공했다면 2번에서 넣은 'active-status' 색상만 남게 됩니다.)
        setTimeout(function() {
            if (btnLeader) {
                btnLeader.removeAttribute('data-clicked');
                btnLeader.classList.remove('status-processing'); // 🟡 처리 중 클래스 제거
                btnLeader.style.pointerEvents = 'auto';
            }
        }, 3000);
    });

}

function initStatus(index, userNo, campaignId, applyDate, division){
    if (window.event) {
        window.event.preventDefault();
    }
    const clickedButton = event.target; // ⭐ 1. 클릭된 버튼을 먼저 변수에 저장! ⭐
    const url = `/apply/initStatus/${userNo}/${campaignId}/${applyDate}`;
    fetch(url, {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken
        },
        body: JSON.stringify({ newStatus: "0", division: division })
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(errorData => {
                throw new Error(errorData.message || '상태 변경 중 알 수 없는 오류가 발생했습니다.');
            });
        }
        return response.json();
    })
    .then(data => {
        alert(data.message);
        const buttons = document.querySelectorAll(`a[th-onclick$=", [[${userNo}]])"]`);
        buttons.forEach(btn => btn.classList.remove('active-status'));
        clickedButton.classList.add('active-status');
        const applicantItems = document.querySelectorAll('.applicant-item');

        applicantItems.forEach(item => {
            const chooseStatus = item.dataset.chooseStatus;
            const statusButtons = item.querySelectorAll('.button_status'+index);
            statusButtons.forEach(button => {
                const targetStatus = button.dataset.status;
                if (chooseStatus === targetStatus) {
                    button.classList.remove('choice_status');
                }
                button.classList.remove('active-status');
            });
        });
    })
    .catch(error => {
        console.error('상태 변경 중 오류 발생:', error);
        alert('상태 변경에 실패했습니다: ' + error.message);
    });
}

// 현재 화면에 있는 체크박스 상태를 selectedUserMap에 동기화(백업)하는 공통 함수
function checkToMap() {
    document.querySelectorAll('.user_Chk').forEach(cb => {
        const userNo = String(cb.value);
        const userId = cb.dataset.userId || cb.closest('td')?.querySelector('.user_Id')?.value;

        if (cb.checked) {
            if (userId) {
                selectedUserMap.set(userNo, userId); // 체크되어 있다면 맵에 저장
            }
        } else {
            // 체크가 풀렸다면 기존 신청자든 아니든 맵에서 제거 (취소 대상 반영을 위해)
            selectedUserMap.delete(userNo);
        }
    });
}

function rosterRemove(index, userNo, campaignId, applyDate) {
    const clickedButton = event.currentTarget || event.target;
    const url = `/apply/rosterRemove/${userNo}/${campaignId}/${applyDate}`;

    const targetContainer = clickedButton.closest('.applyInfo');
    fetch(url, {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken
        },
        body: JSON.stringify({ newStatus: "0" })
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(errorData => {
                throw new Error(errorData.message || '상태 변경 중 알 수 없는 오류가 발생했습니다.');
            });
        }
        return response.json();
    })
    .then(data => {
        alert(data.message);
        if (targetContainer) {
            targetContainer.remove();
        }
    })
    .catch(error => {
        console.error('상태 변경 중 오류 발생:', error);
        alert('상태 변경에 실패했습니다: ' + error.message);
    });
}

function toggleAll(masterCheckbox) {
    // class가 chkSelect인 모든 체크박스를 가져옴
    const checkboxes = document.querySelectorAll('.chkSelectUser');
    checkboxes.forEach(cb => {
        cb.checked = masterCheckbox.checked;
    });
}

function toggleAllLeader(masterCheckbox) {
    const checkLeader = document.querySelectorAll('.chkSelectLeader');
    checkLeader.forEach(cb => {
        cb.checked = masterCheckbox.checked;
    });
}

function updateAttendanceStatus(status, userNo, campaignId, applyDate, index, isBulk = false) {
    if (window.event) {
        window.event.preventDefault();
    }
    let userNos = [];
    const clickedButtonIndex = document.querySelector(`.button_status` + index);
    const wageChk = document.getElementById("wageChk").value;
    const workHour = document.getElementById("workHour").value;

    if (isBulk) {
        const checkedBoxes = document.querySelectorAll('.chkSelect:checked');
        if (checkedBoxes.length === 0) {
            alert("선택된 인원이 없습니다.");
            return;
        }
        userNos = Array.from(checkedBoxes).map(cb => Number(cb.dataset.userNo || cb.value));
    } else {
        // 2. 개별 '출근/퇴근' 버튼을 눌렀을 때는 오직 해당 라인의 userNo 1개만 사용 (체크박스 무시!)
        if (!userNo) {
            alert("대상자 정보가 없습니다.");
            return;
        }
        userNos.push(Number(userNo));
    }

    if (clickedButtonIndex) {
        if (clickedButtonIndex.getAttribute('data-clicked') === 'true') {
            return;
        }

        // 1. [클릭 직후] 잠금 표식을 남기고, '처리 중'을 뜻하는 클래스를 일시적으로 부여합니다.
        clickedButtonIndex.setAttribute('data-clicked', 'true');
        clickedButtonIndex.classList.add('status-processing'); // 🟡 처리 중 색상용 클래스 추가
        clickedButtonIndex.style.pointerEvents = 'none';
    }

    const clickedButton = event.target;
    const applicantItems = document.querySelectorAll('.applicant-item');
    var requestBody;
    if(wageChk == "01"){
        requestBody = {
            userNos: userNos,
            campaignId: campaignId,
            applyDate: applyDate,
            status: status,
            workHour : workHour
        };
    } else {
        requestBody = {
            userNos: userNos,
            campaignId: campaignId,
            applyDate: applyDate,
            status: status,
            workHour : workHour
        };

    }

    applicantItems.forEach(item => {
        const chooseStatus = item.dataset.chooseStatus;
        const statusButtons = item.querySelectorAll('.button_status'+index);

        statusButtons.forEach(button => {
            const targetStatus = button.dataset.status;

            // if (chooseStatus === targetStatus) {
            //     button.classList.remove('choice_status');

            // }
        });
    });

    if (!userNos || !campaignId || !applyDate) {
        console.error("상태 또는 사용자 정보가 올바르지 않습니다.", {status: status, userNo: userNo});
        alert("상태 변경에 필요한 정보가 부족합니다. 다시 시도해 주세요.");
        return;
    }
    const url = `/apply/updateStatus`;

    fetch(url, {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken
        },
        body: JSON.stringify(requestBody)
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(errorData => {
                throw new Error(errorData.message || '상태 변경 중 알 수 없는 오류가 발생했습니다.');
            });
        }
        return response.json();
    })
    .then(data => {
        if(status == "0"){
            if("${status}" == 2){
                alert("출근 or 지각 터치 후 퇴근 버튼 터치 하세요.");
                return ;
            }
        } else{
            if(status == "1" || status == "3"){
                if("${status}" == 2){
                    alert("출결 확인");
                    return ;
                }
            }
        }
        alert(data.message);

        const buttons = document.querySelectorAll(`a[th-onclick$=", [[${userNo}]])"]`);
        buttons.forEach(btn => btn.classList.remove('active-status'));
        clickedButton.classList.add('active-status');
    })
    .catch(error => {
        console.error('상태 변경 중 오류 발생:', error);
        alert('상태 변경에 실패했습니다: ' + error.message);
    })
    .finally(() => {
        // 3. [통신 종료] 성공하든 실패하든 3초 뒤에 연타 잠금과 '처리 중' 디자인을 걷어냅니다.
        // (성공했다면 2번에서 넣은 'active-status' 색상만 남게 됩니다.)
        setTimeout(function() {
            if (clickedButtonIndex) {
                clickedButtonIndex.removeAttribute('data-clicked');
                clickedButtonIndex.classList.remove('status-processing'); // 🟡 처리 중 클래스 제거
                clickedButtonIndex.style.pointerEvents = 'auto';
            }
        }, 3000);
    });
}

function processAttendance(statusValue) {
    // 체크박스 중 선택된 것들만 수집
    const checkedBoxes = document.querySelectorAll('.chkSelect:checked');

    if (checkedBoxes.length === 0) {
        alert('선택된 사용자가 없습니다.');
        return;
    }
    const userNos = [];
    const userIds = [];

    if(statusValue == '3'){
        // [수정] 체크된 모든 유저의 정보를 수집하여 URL을 만듭니다.
        let campaignId = '';
        let applyDate = '';

        checkedBoxes.forEach((cb, index) => {
            const applyInfoDiv = cb.closest('.applyInfo');
            userNos.push(cb.dataset.userNo);
            userIds.push(cb.dataset.userId);

            // 각 체크박스나 행(Row)에서 데이터를 가져옵니다. (기존 Thymeleaf 구조에 맞게 dataset 사용 권장)
            // 예시: <input type="checkbox" class="chkSelect" th:data-user-no="${user.userNo}" ...>

            if (index === 0) {
                // campaignId와 applyDate는 일괄 처리 시 동일하므로 첫 번째 행에서만 가져옵니다.
                campaignId = cb.dataset.campaignId;
                applyDate = cb.dataset.applyDate;
            }
        });
        // 쉼표(,)로 구분된 유저 ID 문자열 생성 (예: "12,15,19")
        const userNoStr = userNos.join(',');
        const userIdStr = userIds.join(',');

        // 최종적으로 요청할 동적 URL 생성
        const targetUrl = `/apply/significant/${userNoStr}/${userIdStr}/${campaignId}/${applyDate}`;

        // 작성하신 함수 호출! (HTML 조각을 받아와 모달을 열어줌)
        openChkPopup(targetUrl);
    } else if(statusValue == '8'){
        const statusText = statusValue === '8' ? '퇴근' : '';
        if (!confirm(`선택한 ${checkedBoxes.length}명을 일괄 [${statusText}] 처리하시겠습니까?`)) {
            return;
        }

        // 기존 개별 클릭 시 작동하던 타임리프 바인딩 데이터를 엘리먼트에서 동적으로 읽어옴
        let successCount = 0;

        // 비동기 처리가 순차적으로 다 끝난 후 새로고침하기 위해 Promise 배열 생성
        const promises = Array.from(checkedBoxes).map(cb => {
            // 체크박스와 가장 가까운 상위 .applyInfo 레이아웃을 찾음
            const applyInfoDiv = cb.closest('.applyInfo');

            // onclick 문자열에서 인자값들 추출하는 대신, 안전하게 기존 설계된 구조를 활용하거나
            // 기존 함수(updateAttendanceStatus)를 트리거 시켜버립니다.
            const btn = applyInfoDiv.querySelector(`.btn-area-confirmB a[data-status="${statusValue}"]`);

            if (btn) {
                // 기존에 정의된 함수를 체크된 유저 수만큼 강제로 호출시킴
                return eval(btn.getAttribute('onclick').replace('this.dataset.status', `'${statusValue}'`));
            }
        });

        // 일괄 실행 후 완료 메시지 및 새로고침
        Promise.all(promises).then(() => {
            alert(`선택된 인원의 ${statusText} 처리가 요청되었습니다.`);
        }).catch(err => {
            console.error("일괄 처리 중 에러 발생: ", err);
            // location.reload();
        });
    } else {
        const statusText = statusValue === '1' ? '출근' : '퇴근';

        if (checkedBoxes.length === 0) {
            alert("처리를 진행할 인원을 선택해 주세요.");
            return;
        }

        if (!confirm(`선택한 ${checkedBoxes.length}명을 일괄 [${statusText}] 처리하시겠습니까?`)) {
            return;
        }

        // 💡 개별 호출을 반복(map)하지 않고, 첫 번째 체크박스의 정보와 선택한 statusValue를 넘겨서
        // updateAttendanceStatus를 딱 '한 번만' 직접 실행합니다.
        const firstCb = checkedBoxes[0];
        const userNo = firstCb.dataset.userNo;
        const campaignId = firstCb.dataset.campaignId;
        const applyDate = firstCb.dataset.applyDate;

        // 함수 1회 실행!
        updateAttendanceStatus(statusValue, null, campaignId, applyDate, null, true);
    }
}

function updateSignificantStatus(status, userNo, campaignId, applyDate) {
    const clickedButton = event.target;
    const applicantItems = document.querySelectorAll('.applicant-item');
    const requestBody = {
        userNos: userNo,
        campaignId: campaignId,
        applyDate: applyDate,
        status: status
    };

    if (!userNo || !campaignId || !applyDate) {
        console.error("상태 또는 사용자 정보가 올바르지 않습니다.", {status: status, userNo: userNo});
        alert("상태 변경에 필요한 정보가 부족합니다. 다시 시도해 주세요.");
        return;
    }
    const url = `/apply/updateStatus`;

    fetch(url, {
        method: 'PATCH',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken
        },
        body: JSON.stringify(requestBody)
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(errorData => {
                throw new Error(errorData.message || '상태 변경 중 알 수 없는 오류가 발생했습니다.');
            });
        }
        return response.json();
    })
    .then(data => {
        if(status == "0"){
            if("${status}" == 2){
                alert("출근 or 지각 터치 후 퇴근 버튼 터치 하세요.");
                return ;
            }
        } else{
            if(status == "1" || status == "3"){
                if("${status}" == 2){
                    alert("출결 확인");
                    return ;
                }
            }
        }
        alert(data.message);

        const buttons = document.querySelectorAll(`a[th-onclick$=", [[${userNo}]])"]`);
        buttons.forEach(btn => btn.classList.remove('active-status'));
        clickedButton.classList.add('active-status');
    })
    .catch(error => {
        console.error('상태 변경 중 오류 발생:', error);
        alert('상태 변경에 실패했습니다: ' + error.message);
    })
    .finally(() => {
        // 3. [통신 종료] 성공하든 실패하든 3초 뒤에 연타 잠금과 '처리 중' 디자인을 걷어냅니다.
        // (성공했다면 2번에서 넣은 'active-status' 색상만 남게 됩니다.)
        setTimeout(function() {
            if (clickedButtonIndex) {
                clickedButtonIndex.removeAttribute('data-clicked');
                clickedButtonIndex.classList.remove('status-processing'); // 🟡 처리 중 클래스 제거
                clickedButtonIndex.style.pointerEvents = 'auto';
            }
        }, 3000);
    });
}

function significantInsert(campaignId, userNo, userId, applyDate) {
    // 모달창 내부에서 바뀐 클래스명(.modal-reason, .modal-amount)으로 요소를 정확히 찾아옵니다.

    const reasonInput = document.querySelector('.modal-reason');
    const amountInput = document.querySelector('.modal-amount');

    const reasonInfo = reasonInput ? reasonInput.value.trim() : "";
    const amount = amountInput ? amountInput.value.trim() : "";

    if (!reasonInfo) {
        alert('특이사항 내용을 입력해 주세요.');
        return;
    }

    const requestBody = {
        campaignId: campaignId,
        userNos: userNo, // 이제 파라미터로 넘어온 정확한 대상(인솔자 혹은 유저)의 번호가 바인딩됩니다.
        userIds: userId,
        missionDate: applyDate,
        amount: amount,
        reasonInfo: reasonInfo,
        category: "OVERPAY"
    };

    fetch('/apply/significantInsert', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken
        },
        body: JSON.stringify(requestBody)
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(errorData => {
                throw new Error(errorData.message || '등록 중 오류가 발생했습니다.');
            });
        }
        return response.json();
    })
    .then(data => {
        alert(data.message);
        closeSignificantModal(); // 저장 완료 후 모달창 닫기
        // location.reload();       // 부모창 새로고침해서 결과 반영
    })
    .catch(error => {
        console.error('에러 발생:', error);
        alert('등록에 실패했습니다: ' + error.message);
    });
}

document.getElementById('leaveWorkBtn').addEventListener('click', () => {
    // 안전장치: 데이터가 비어있거나 null인 경우 예외 처리
    if (!apply || apply.length === 0) {
        alert("적립 가능한 캠페인 신청 정보가 없습니다.");
        return;
    }

    // 변수로 받아온 배열을 순회합니다.
    // 2. 오브젝트 통째로 서버에 전송
    fetch('/apply/pointEarn', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken
        },
        body: JSON.stringify(apply)
        // 💡 campaignId, applyDate, timeSegment, wageChk, workHour 등이 통째로 넘어감
    })
    .then(res => res.json())
    .then(data => {
        if(data.success) {
            alert("🎉 퇴근 포인트 적립 및 업데이트가 " + data.message + "건 완료되었습니다!");
            // location.reload();
        } else {
            alert(`❌ 실패: ${data.message}`);
        }
    })
    .catch(err => console.error("통신 에러:", err));
});

function openPopup(url) {
    console.log("url : ", url);
    var sendData = {
        campaignTitle: apply[0].campaignTitle,
        recruitmentNum: apply[0].recruitmentNum,
        applicantsNum: apply[0].applicantsNum,
        eventPeriodStr: apply[0].eventPeriodStr,
        eventPeriodEnd: apply[0].eventPeriodEnd,
        applyDate: document.getElementById('dateSelector').value
        // applyDate: "ALL"
    };

    // 1. 서버에 해당 url의 HTML 내용을 요청
    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken
        },
        body: JSON.stringify(sendData)
    })
    .then(response => {
        if (!response.ok) throw new Error('네트워크 응답 실패');
        return response.text();
    })
    .then(html => {
        // 2. 모달 컨텐츠 영역에 서버에서 가져온 HTML 주입
        document.getElementById('modalContentUser').innerHTML = html;
        document.getElementById('modalPopup').style.display = 'flex';
        // 💡 [추가] 팝업이 열릴 때 날짜 selectbox의 두 번째 값(index 1)을 기본 선택하고 목록 로드
        const dateSelect = document.getElementById('dates');

        // if (dateSelect && dateSelect.options.length > 1) {
        //     dateSelect.selectedIndex = 0; // 두 번째 옵션(인덱스 1) 선택

        //     // // onchange에 등록된 loadUserList 함수 실행 (또는 이벤트 트리거)
        //     // if (typeof loadUserList === 'function') {
        //     //     loadUserList(dateSelect);
        //     // } else {
        //     //     dateSelect.dispatchEvent(new Event('change'));
        //     // }
        // }
        // 💡 [수정 1] 팝업이 열릴 때, 이미 체크되어 있거나(이미 신청된 경우 등)
        // 기존에 전역 배열(chkUser)에 기억되어 있던 ID들을 체크박스에 반영하고 배열에도 동기화합니다.
        const userChkList = document.querySelectorAll('.user_Chk');

        userChkList.forEach(cb => {
            const userNo = String(cb.value); // 유저 고유 번호 (id)
            // 체크박스 태그에 data-user-id가 박혀있거나, 숨김 input에서 가져오기
            const userId = cb.dataset.userId || cb.closest('td')?.querySelector('.user_Id')?.value;
            // 🔥 [핵심 수정] 반드시 'chk-applied' 클래스가 있는 경우에만 initialAppliedUsers에 넣어야 합니다!
            // if (cb.classList.contains('chk-applied')) {
            //     initialAppliedUsers.add(userNo);
            //     if (userId) {
            //         selectedUserMap.set(userNo, userId);
            //     }
            // }

            // 사용자가 체크를 직접 했거나 기존 신청자인 경우 체크 유지
            if (cb.classList.contains('chk-applied') || cb.checked) {
                cb.checked = true;
                if (userId) {
                    initialUser.add(userNo);
                    selectMap.set(userNo, userId);
                    // selectedUserMap.set(userNo, userId);
                }
            }
        });

        // 2. 💡 [날짜 변경 시] 날짜 값이 바뀌면 자동으로 다시 조회
        // const dateInput = document.getElementById('dates');
        // if (dateInput) {
        //     dateInput.addEventListener('change', function() {
        //         loadUserList(true); // 날짜가 바뀌었으므로 isDateChanged = true
        //     });
        // }
        loadUserList(true);
        // 💡 [수정 2] 팝업이 열린 직후, 현재까지 선택된 총 인원수 카운트 초기화/반영
        if (typeof updateChkCount === 'function') {
            updateChkCount();
        }
    })
    .catch(error => {
        console.error('모달 로드 중 에러 발생:', error);
        alert('페이지를 불러오는 중 오류가 발생했습니다.');
    });
}

// 💡 체크박스 상태 변화에 따라 선택된 인원 수를 실시간으로 업데이트하는 함수
// function updateChkCount() {
//     const chkPcs = document.getElementById('chkPcs');
//     const recPcs = document.getElementById('recPcs');

//     // 1. 모집 인원(recPcs) 설정 (안전장치 포함)
//     if (recPcs && typeof apply !== 'undefined' && apply.length > 0) {
//         recPcs.textContent = apply[0].recruitmentNum;
//     }

//     // 2. 현재 선택된 총 인원수(selectedUserMap 기준)를 화면에 반영
//     if (chkPcs) {
//         // 날짜를 이동하거나 체크를 바꿀 때, 전체 통합 맵의 크기(.size)를 정확히 보여줍니다.
//         chkPcs.textContent = selectedUserMap.size;
//     }

//     console.log("updateChkCount 호출됨. 현재 선택된 총 인원수(selectedUserMap.size): " + selectedUserMap.size);
// }

// 💡 체크박스 상태 변화에 따라 선택된 인원 수를 실시간으로 업데이트하는 함수
function updateChkCount(ele) {
    const chkPcs = document.getElementById('chkPcs');
    const recPcs = document.getElementById('recPcs');

    // 1. 모집 인원 세팅 (안전장치)
    if (recPcs && typeof apply !== 'undefined' && apply.length > 0) {
        recPcs.textContent = apply[0].recruitmentNum;
    }

    // 💡 2. 현재 선택된 날짜 가져오기 (카운트는 항상 계산되어야 함!)
    const currentDate = document.getElementById("dates") ? document.getElementById("dates").value : "";
    let checkedCount = 0;

    // 3. 현재 날짜의 selectMap에서 실제 체크된 개수 가져오기
    if (currentDate && selectMap.has(currentDate)) {
        checkedCount = selectMap.get(currentDate).size;
    } else {
        // checkedCount = document.querySelectorAll('.user_Chk:checked').length;
        checkedCount = selectMap.size;
    }

    // 4. 화면에 출력 (항상 실행)
    if (chkPcs) {
        chkPcs.textContent = checkedCount;
    }

    console.log(`updateChkCount 호출됨 [날짜: ${currentDate}], 현재 날짜 체크된 인원수: ${checkedCount}`);
}


function chkBoxSelect(ele) {
    const currentDate = document.getElementById("dates") ? document.getElementById("dates").value : "";
    const userNo = ele.value; // 체크박스의 value (회원 고유 ID)
    const userId = ele.dataset.userId || ele.closest('td')?.querySelector('.user_Id')?.value;

    if (!currentDate) {
        alert("조회할 날짜를 먼저 선택해 주세요.");
        ele.checked = false; // 체크 강제 해제
        return;
    }

    // 💡 2. [안전장치] selectMap에 해당 날짜의 Map이 없으면 새로 생성해서 반드시 장착
    if (!selectMap.has(currentDate)) {
        selectMap.set(currentDate, new Map());
    }

    // // 1. 현재 날짜용 Set 가져오기 (없으면 생성)
    // if (!dateSelectedMap.has(currentDate)) {
    //     dateSelectedMap.set(currentDate, new Set());
    // }
    const currentSet = dateSelectedMap.get(currentDate);

    // console.log(`체크박스 상태 변경: userNo=${userNo}, userId=${userId}, checked=${ele.checked}`);
    // if (ele.checked) {
    //     // chkUser.add(userNo);    // 체크된 경우 배열/세트에 추가
    //     // chkUserId.add(userId); // 체크된 경우 배열/세트에 추가
    //     // selectedUserMap.set(userNo, userId); // 👈 ID와 userId를 쌍으로 저장!

    //     currentSet.add(userNo);
    //     if (userId) {
    //         selectedUserMap.set(userNo, userId); // 전체 통합용이 필요하다면 유지
    //     }
    // } else {
    //     // chkUser.delete(userNo); // 체크가 해제된 경우 제거
    //     // chkUserId.delete(userId); // 체크가 해제된 경우 제거
    //     // selectedUserMap.delete(userNo); // 👈 ID와 userId 쌍 제거

    //     currentSet.delete(userNo);

    //     // 주의: 만약 이 유저가 다른 날짜에서도 체크된 적이 없다면 selectedUserMap에서 제거
    //     // (만들려는 기획에 따라 전체 맵에서 유지할지 말지 결정하되, 일반적으로는 해제 시 제거)
    //     let isCheckedInOtherDate = false;
    //     for (let [date, set] of dateSelectedMap.entries()) {
    //         if (date !== currentDate && set.has(userNo)) {
    //             isCheckedInOtherDate = true;
    //             break;
    //         }
    //     }
    //     if (!isCheckedInOtherDate && !initialAppliedUsers.has(userNo)) {
    //         selectedUserMap.delete(userNo);
    //     }
    // }
    const dateUserMap = selectMap.get(currentDate);

    // 4. 안전하게 .set() 또는 .delete() 수행
    if (ele.checked) {
        dateUserMap.set(userNo, userId);
        console.log(`[추가됨] 날짜: ${currentDate}, 번호: ${userNo}, 아이디: ${userId}`);
    } else {
        dateUserMap.delete(userNo);
        console.log(`[제외됨] 날짜: ${currentDate}, 번호: ${userNo}, 아이디: ${userId}`);
    }

    // 3. 총 카운트 갱신 (전체 선택된 고유 유저 수 기준)
    if (typeof updateChkCount === 'function') {
        updateChkCount();
    }
}

function closeModalApply() {
    // 모달 닫기 및 컨텐츠 초기화
    const modal = document.getElementById('modalPopup');
    if(modal)
        modal.style.display = 'none';
    const content = document.getElementById('modalContentUser');
        if(content) content.innerHTML = '';
    // document.getElementById('significantModal').style.display = 'none';
    // document.getElementById('modalContent').innerHTML = '';
}

document.getElementById('modalPopup').addEventListener('click', function(e) {
    if (e.target === this) {
        this.style.display = 'none';
    }
});

function SelectBoxInput() {
    const code = document.getElementById('searchCode').value;
    const inputArea = document.getElementById('inputArea');
    const savedKeyword = document.getElementById('savedKeyword').value; // Hidden input에서 가져온 값
}


// 부모 창 문서 전체에서 클릭 이벤트를 감시
document.addEventListener('click', function(event) {
    // 클릭된 요소가 'btnSearch' 버튼이거나 그 내부(텍스트 등)인 경우
    const searchInput = document.getElementById('keyword');
    const searchBtn = event.target.closest('#btnSearch');

    const target = event.target;
    // target이 존재하고, 아이디가 'btnSearch'이거나 그 내부 요소를 클릭한 경우
    if (target && (target.id === 'btnSearch' || target.closest('#btnSearch'))) {
        event.preventDefault(); // 기본 동작 방지
        loadUserList(false);    // 여기서 딱 1번만 실행!
    }

    if (searchInput && searchBtn) {
        searchInput.addEventListener('keydown', function(event) {
            if (event.key === 'Enter') {
                event.preventDefault();
                searchBtn.click(); // 엔터 치면 위에서 감시 중인 클릭 이벤트가 실행됨
            }
        });
    }

    // // 클릭된 요소의 ID가 'btnSearch'인지 확인
    // if (event.target && event.target.id === 'btnSearch') {

    //     // 💡 기존 검색 로직을 여기에 작성
    //     const keyword = document.getElementById('keyword').value;
    //     const campaignId = apply[0].campaignId; // 현재 선택된 캠페인 ID (동적으로 세팅)
    //     // let code = $("input[name='code']:checked").val();
    //     const code = document.querySelector('.code-radio:checked').value;
    //     const url = `/apply/popup01/search?campaignId=${campaignId}&code=${code}&keyword=${encodeURIComponent(keyword)}`;

    //     fetch(url)
    //         .then(response => response.json())
    //         .then(data => {
    //             // 🔥 데이터를 받아와서 테이블의 tbody 영역만 새로 그려줍니다.
    //             // const tbody = document.querySelector('.modal_table tbody');
    //             const tbody = document.getElementById('userList');
    //             tbody.innerHTML = ''; // 기존 목록 비우기
    //             if(data.length === 0) {
    //                 tbody.innerHTML = '<tr><td colspan="4">검색 결과가 없습니다.</td></tr>';
    //                 return;
    //             }

    //             data.forEach(user => {
    //             // 💡 1. 신청 여부에 따라 'chk-applied' 클래스를 동적으로 생성합니다.
    //             // (JSON으로 넘어오는 필드명이 대소문자에 따라 isApplied 또는 is_applied 일 수 있으니 콘솔 로그를 확인해 보세요!)
    //             const classAppend = (user.isApplied === 'Y') ? 'chk-applied' : '';

    //             // 💡 2. Y이면 'checked' 속성을 문자열로 추가하여 기본 체크 상태로 만들기
    //             const checkedAppend = (user.isApplied === 'Y') ? 'checked' : '';
    //             // 💡 2. 기존 HTML 구조에 맞게 체크박스 속성들과 직급명(guardTypeNm) 등을 채워줍니다.
    //             const row = `
    //                 <tr>
    //                     <td>
    //                         <input type="checkbox"
    //                             value="${user.id}"
    //                             class="user_Chk ${classAppend}"
    //                             ${checkedAppend}
    //                             name="Attendance">
    //                     </td>
    //                     <td>${user.userNm}</td>
    //                     <td>${user.guardTypeNm || '미지정'}</td>
    //                     <td>${user.phoneNum}</td>
    //                 </tr>
    //             `;
    //             tbody.insertAdjacentHTML('beforeend', row);
    //         });
    //     })
    //     .catch(error => console.error('검색 오류:', error));
    // }

    // const searchInput = document.getElementById('keyword');
    // const searchBtn = document.getElementById('btnSearch');

    // if (searchInput && searchBtn) {
    //     searchInput.addEventListener('keydown', function(event) {
    //         if (event.key === 'Enter') {
    //             event.preventDefault();
    //             searchBtn.click();
    //         }
    //     });
    // }
});


function resChk() {
    document.querySelectorAll('.user_Chk').forEach(chk => {
        // 보관된 Set에 ID가 있으면 체크, 없으면 체크 해제
        chk.checked = selectedUserIds.has(chk.value);
    });
    updateChkCount(); // 카운트 갱신
}

function handleChkChange(checkbox) {
    if (checkbox.checked) {
        selectedUserIds.add(checkbox.value);   // 체크하면 Set에 추가
    } else {
        selectedUserIds.delete(checkbox.value); // 체크 해제하면 Set에서 삭제
    }
    updateChkCount(); // 카운트 갱신
}

function applyCampaign() {
    // 현재 날짜의 체크된 유저 Map 가져오기
    const currentDate = document.getElementById("dates").value;
    const dateUserMap = selectMap.get(currentDate);

    document.querySelectorAll('.user_Chk').forEach(cb => {
        const userNo = String(cb.value);
        const userId = cb.dataset.userId || cb.closest('td')?.querySelector('.user_Id')?.value;
        if (cb.checked) {
            if (userId)
                selectMap.set(userNo, userId);
        } else {
            if (!cb.classList.contains('chk-applied')) {
                selectMap.delete(userNo);
            }
        }
    });

    if (!dateUserMap || dateUserMap.size === 0) {
        alert("체크된 인원이 없습니다.");
        return;
    }

    const userNos = [];
    const userIds = [];

    // selectMap의 데이터를 돌면서 각각의 배열에 push
    dateUserMap.forEach((userId, userNo) => {
        userNos.push(userNo);     // 유저 번호만 쏙쏙
        userIds.push(userId);     // 유저 아이디만 쏙쏙
    });

    // Map을 전송용 배열로 변환
    const applyList = [];
    dateUserMap.forEach((userId, userNo) => {
        applyList.push({ userNo: userNo, userId: userId });
    });

    const applyDate = document.querySelector(".sync-nm");

    const sendData = {
        campaignId: campId,
        applyDate: currentDate,
        userList: applyList, // 체크한 유저 목록

        userNos: userNos,         // 예: ["101", "102", "103"]
        userIds: userIds,         // 예: ["user_a", "user_b", "user_c"]
        recruitmentNum: apply[0].recruitmentNum,
        applicantsNum: apply[0].applicantsNum,
        appPeriodStr: (typeof apply !== 'undefined' && apply.length > 0) ? apply[0].appPeriodStr : null,
        appPeriodEnd: (typeof apply !== 'undefined' && apply.length > 0) ? apply[0].appPeriodEnd : null,
        eventPeriodStr: apply[0].eventPeriodStr,
        eventPeriodEnd: apply[0].eventPeriodEnd,
        timeSegment: apply[0].timeSegment,
        applyDateS: applyDate ? applyDate.value : ""
    };

    console.log("sendData : " + JSON.stringify(sendData))
    console.log("sendData : " + JSON.stringify(sendData.userList[0].userNo))
    console.log("sendData : " + JSON.stringify(sendData.userList[0].userId))

    // 서버로 POST 전송
    fetch('/apply/save', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', [csrfHeader]: csrfToken },
        body: JSON.stringify(sendData)
    })

    .then(async res => {
    // 서버에서 보내준 응답을 JSON으로 파싱
    const data = await res.json();

    // 만약 HTTP 상태 코드가 200대(성공)가 아니라면 (예: 400 Bad Request 등)
    if (!res.ok) {
        // 서버에서 던진 에러 메시지(data.message)를 담아서 에러를 throw 합니다.
        throw new Error(data.message || '요청 처리에 실패했습니다.');
    }

    return data; // 성공한 경우 데이터를 다음 .then으로 넘김
})
.then(result => {
    // 💡 여기가 성공했을 때만 타는 곳입니다!
    console.log("반영된 건수:", result);
    alert("수정 사항 " + result + "건 성공적으로 반영되었습니다.");
    loadUserList(false); // 리스트 새로고침
})
.catch(error => {
    // 💡 백엔드의 IllegalArgumentException 메시지가 이곳으로 들어옵니다!
    console.error("에러 발생:", error.message);
    alert(error.message); // 서비스에서 throw한 에러 메시지가 경고창으로 뜹니다.
});

    // .then(res => res.json())
    // .then(result => {
    //     // alert("저장되었습니다!");
    //     console.log("반영된 건수:", result);

    //     alert("수정 사항 " + result + "건 성공적으로 반영되었습니다.");
    //     loadUserList(false); // 리스트 새로고침
    // });
}

function cancelCampaign(campaignId) {
    const timeSegment = apply[0].timeSegment; // 혹은 해당 캠페인의 timeSegment 값
    // 1. 팝업창이 열렸을 당시 '이미 신청 상태였던' 유저들의 No 목록 (chk-applied 클래스 활용)
    const appliedUsers = [];
    document.querySelectorAll('.user_Chk.chk-applied').forEach(cb => {
        appliedUsers.push(cb.value);
    });

    // 2. 현재 체크박스에 '체크가 되어 있는' 유저들의 No 목록
    const checkedUsers = [];
    document.querySelectorAll('.user_Chk:checked').forEach(cb => {
        checkedUsers.push(cb.value);
    });

    // 3. 🔥 핵심: 기존에 신청했었는데(original), 현재 체크가 풀린(current에 없는) 유저 골라내기 (취소 대상자)
    const usersToCancel = appliedUsers.filter(userNo => !checkedUsers.includes(userNo));

    // 4. 새롭게 체크된 유저 골라내기 (신규 등록 대상자 - 필요시 사용)
    const usersToInsert = checkedUsers.filter(userNo => !appliedUsers.includes(userNo));

    // ========================================================
    // 5. 서버에 취소 요청 보내기 (취소 대상자가 있을 때만)
    // ========================================================
    if (usersToCancel.length > 0) {
        const cancelData = {
            campaignId: campaignId,
            timeSegment: timeSegment,
            userNos: usersToCancel, // [34, 43] 형태로 전송
            eventPeriodStr: apply[0].eventPeriodStr,
            eventPeriodEnd: apply[0].eventPeriodEnd
            // 만약 날짜 정보도 필요하다면 dto에 맞게 추가 (예: eventPeriodStr, eventPeriodEnd 등)
        };

        fetch('/apply/userCancel', { // 본인의 Controller URL 확인
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                [csrfHeader]: csrfToken
            },
            body: JSON.stringify(cancelData)
        })
        .then(response => response.json())
        .then(data => {
            alert("선택한 유저의 신청 취소가 완료되었습니다.");
            // location.reload(); // 화면 새로고침
        })
        .catch(error => {
            console.error("취소 처리 중 에러:", error);
        });
    } else {
        alert("수정된 내역이 없습니다.");
    }
}

function loadUserList(isDateChanged = false) {
    console.log("loadUserList")

    const applyDate = document.getElementById('dates').value;
    // const selectedCode = document.querySelector('input.code-radio:checked')?.value || '';
    const keyword = document.getElementById('keyword');
    // const datesValue = document.getElementById("dates").value; // 현재 날짜
    const code = document.querySelector('.code-radio:checked')?.value || "";
    const selectedCode = document.querySelector('input.code-radio:checked')?.value || '';

    if (isDateChanged) {
        keyword.value = "";
        const radios = document.querySelectorAll('input.code-radio');
        let selectedRadio = document.querySelector('input.code-radio:checked');
        console.log("radios : " + radios)
        console.log("selectRadios : " +selectedRadio)

        if (!selectedRadio && radios.length > 0) {
            // 선택된 게 없다면 첫 번째 라디오 버튼을 강제로 체크!
            radios[0].checked = true;
            selectedRadio = radios[0];
            console.log("선택된 직급/구분 라디오가 없어 첫 번째 항목을 기본 선택합니다." + selectedRadio);
        }
        radios[0].checked = true;
        selectedRadio = radios[0];
    }

    const datesValue = document.getElementById("dates").value; // 현재 날짜
    const searchData = {
        campaignId: campId,
        code: selectedCode,
        keyword: keyword.value,
        dates: datesValue,
        appPeriodStr: (typeof apply !== 'undefined' && apply.length > 0) ? apply[0].appPeriodStr : null,
        appPeriodEnd: (typeof apply !== 'undefined' && apply.length > 0) ? apply[0].appPeriodEnd : null,
        eventPeriodStr: (typeof apply !== 'undefined' && apply.length > 0) ? apply[0].eventPeriodStr : null,
        eventPeriodEnd: (typeof apply !== 'undefined' && apply.length > 0) ? apply[0].eventPeriodEnd : null
    };

    fetch(`/apply/search`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            [csrfHeader]: csrfToken
        },
        body: JSON.stringify(searchData)
    })
    .then(response => response.text())
    .then(data => {
        console.log("data : " , JSON.stringify(data));
        console.log("data A : " , JSON.stringify(data));

        let userList = (typeof data === 'string') ? JSON.parse(data) : data;
        if (!Array.isArray(userList)) {
            console.error("데이터가 배열이 아닙니다:", userList);
            return;
        }

        const tableBody = document.getElementById("userList");
        let htmlRows = '';

        // 💡 1. 날짜가 아예 바뀐 경우라면, 해당 날짜(또는 전체)의 initialUser와 selectMap을 초기화
        if (isDateChanged) {
            initialUser.clear();
            selectMap.clear(); // 전체를 비우거나, selectMap.delete(datesValue)로 해당 날짜만 비울 수 있습니다.
        }

        // 💡 2. [핵심] 현재 날짜에 해당하는 Map을 selectMap에서 가져오거나, 없으면 새로 만들어 줍니다.
        if (!selectMap.has(datesValue)) {
            selectMap.set(datesValue, new Map());
        }
        const dateUserMap = selectMap.get(datesValue);

        // 💡 3. 서버에서 받아온 유저 리스트를 돌면서 처리
        userList.forEach(user => {
            const userNo = String(user.id);
            const userId = user.userId;
            const isApplied = (user.isApplied === 'Y');

            if (isDateChanged) {
                // [날짜 변경 시] 서버에서 이미 신청된 상태('Y')인 것만 초기값 및 해당 날짜 맵에 등록
                if (isApplied) {
                    initialUser.add(userNo);
                    dateUserMap.set(userNo, userId);
                }
            } else {
                // [직급/검색 조회 시]
                // 서버에서 신청된 상태('Y')이고, 아직 dateUserMap에 기록이 없다면 서버 기준 신청자를 동기화
                if (isApplied && !dateUserMap.has(userNo)) {
                    dateUserMap.set(userNo, userId);
                }
            }

            // 💡 4. 체크 여부 판단: 날짜별 맵(dateUserMap)에 포함되어 있거나 서버 신청 상태('Y')이면 체크 유지
            const isChecked = (dateUserMap.has(userNo) || isApplied) ? 'checked' : '';
            const className = isApplied ? 'chk-applied' : '';

            // --- [HTML 행 조립] ---
            htmlRows += `
                <tr>
                    <td>
                        <input type="checkbox" value="${userNo}"
                            data-user-id="${userId}"
                            class="user_Chk ${className}"
                            ${isChecked}
                            onclick="chkBoxSelect(this)">
                    </td>
                    <td>${user.userNm}</td>
                    <td>${user.guardTypeNm}</td>
                    <td>${user.phoneNum}</td>
                </tr>
            `;
        });

        // 테이블에 HTML 주입
        tableBody.innerHTML = htmlRows;

        const appliedBoxes = document.querySelectorAll('.user_Chk.chk-applied');

        appliedBoxes.forEach(cb => {
            // cb.checked = true;  // 자동으로 체크 표시 활성화
            // cb.disabled = true; // 유저가 임의로 체크 해제하지 못하도록 비활성화
            // (시각적 효과) 해당 행을 흐리게 만들어서 이미 신청 완료된 상태임을 표시
            const row = cb.closest('tr');
            if (row) {
                row.style.backgroundColor = '#f8f9fa';
                row.style.color = '#bbb';
            }
        });

        // 인원 카운트 갱신 함수 호출
        if (typeof updateChkCount === 'function') {
            updateChkCount(isDateChanged);
        }
    })
    .catch(error => {
        console.error('유저 리스트 조회 실패:', error);
    });
}