// modal-handler.js
const ModalManager = {
    async open(url, targetId) {
        // 이미 로드되어 있는지 확인
        let modal = document.getElementById(targetId);
        
        if (!modal) {
            await loadModal(url);
            modal = document.getElementById(targetId);
        }
        
        modal.classList.add('active');
    },
    
    close(targetId) {
        const modal = document.getElementById(targetId);
        if (modal) modal.classList.remove('active');
    }
};

// 사용 예시 (약관보기 버튼 클릭 시)
// <a href="#" onclick="ModalManager.open('privacy.html', 'privacyModal')">약관보기</a>