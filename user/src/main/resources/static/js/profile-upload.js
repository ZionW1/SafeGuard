/**
 * 프로필 이미지 업로드 및 미리보기 공통 로직
 */
function showPreview(file, imgFileId, defImg, prv) {
    const reader = new FileReader();
    reader.onload = (e) => {
        const imgFile = document.getElementById(imgFileId);
        if (imgFile) imgFile.style.display = 'none';
        if (defImg) defImg.style.display = 'none';
        prv.src = e.target.result;
        prv.style.display = "inline";
    };
    reader.readAsDataURL(file);
}

async function markFileDeleted(id) {
    // CSRF 토큰은 HTML의 메타 태그나 hidden input에서 가져옵니다.
    const csrfToken = document.querySelector('input[name="_csrf"]')?.value;
    if (!csrfToken) {
        console.error("CSRF token not found");
        return;
    }
    
    const url = `/mypage/markDeleted/${id}`;
    try {
        const response = await fetch(url, {
            method: 'POST',
            headers: { "X-CSRF-TOKEN": csrfToken }
        });
        if (response.ok) {
            const text = await response.text();
            if (text === 'SUCCESS') {
                alert('파일 삭제 성공');
                window.location.reload();
            }
        }
    } catch (error) {
        alert('에러 발생: ' + error.message);
    }
}

// 초기화 함수
function initProfileUpload() {
    const dropArea = document.getElementById("drop-area"); 
    const imageInput = document.getElementById("image");
    const preview = document.getElementById("preview");
    const defaultImage = document.getElementById("defaultImage");
    
    const dropAreaM = document.getElementById("drop_area_mobile");
    const imageInputM = document.getElementById("image_mobile");
    const previewM = document.getElementById("preview_mobile");
    const defaultImageM = document.getElementById("defaultImage_mobile");

    const initDisplay = (input, defImg, prv, imgFileId) => {
        if(!input) return;
        const imgFile = document.getElementById(imgFileId);
        if (defImg && (!input.files || input.files.length === 0)) {
            defImg.style.display = 'block';
            prv.style.display = 'none';
            if (imgFile) imgFile.style.display = 'none';
        }
    };

    initDisplay(imageInput, defaultImage, preview, "imgFile");
    initDisplay(imageInputM, defaultImageM, previewM, "imgFile_mobile");

    // 이벤트 리스너 등록 로직 (데스크탑)
    if (dropArea && imageInput) {
        dropArea.addEventListener("click", () => imageInput.click());
        // ... (나머지 드래그앤드롭 이벤트들 생략 - 위와 동일) ...
        imageInput.addEventListener("change", () => {
            const file = imageInput.files[0];
            if (file) showPreview(file, "imgFile", defaultImage, preview);
        });
    }

    // 모바일 이벤트 등록 로직
    if (dropAreaM && imageInputM) {
        dropAreaM.addEventListener("click", () => imageInputM.click());
        // ... (나머지 드래그앤드롭 이벤트들 생략 - 위와 동일) ...
        imageInputM.addEventListener("change", () => {
            const file = imageInputM.files[0];
            if (file) showPreview(file, "imgFile_mobile", defaultImageM, previewM);
        });
    }
}

// 페이지 로드 시 실행
document.addEventListener("DOMContentLoaded", initProfileUpload);