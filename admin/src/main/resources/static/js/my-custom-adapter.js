// src/main/resources/static/js/my-custom-adapter.js
// ⭐️ 이 파일을 새로 만들고 여기에 아래 코드를 넣어줘! ⭐️

// MyCustomUploadAdapterPlugin 함수 자체를 export default 해줘야 다른 모듈에서 import 가능
export default function MyCustomUploadAdapterPlugin(editor) {
    editor.plugins.get('FileRepository').createUploadAdapter = (loader) => {
        return new MyUploadAdapter(loader);
    };
}

// MyUploadAdapter 클래스는 MyCustomUploadAdapterPlugin 함수와 같은 파일 내부에 정의되어도 괜찮아.
class MyUploadAdapter {
    constructor(loader) {
        this.loader = loader;
        this.controller = new AbortController(); // 업로드 취소를 위한 컨트롤러
    }

    upload() {
        const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
        const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

        return this.loader.file
            .then(file => new Promise((resolve, reject) => {
                const data = new FormData();
                data.append('upload', file);

                fetch('/admin/upload/image', { 
                    method: 'POST', 
                    headers: {
                        [csrfHeader]: csrfToken
                    },
                    body: data,
                    signal: this.controller.signal // 업로드 중단 시그널
                })
                .then(response => {
                    const contentType = response.headers.get('content-type');
                    if (!contentType || !contentType.includes('application/json')) {
                        throw new Error("서버 응답이 JSON이 아닙니다.");
                    }
                    return response.json();
                })
                .then(result => {
                    if (result.uploaded && result.url) {
                        resolve({ default: result.url });
                    } else {
                        reject(result.error?.message || '이미지 업로드 실패.');
                    }
                })
                .catch(error => {
                    console.error('파일 업로드 오류:', error);
                    alert('파일 업로드 도중 문제가 발생했습니다.');
                    reject(error);
                });
            }));
    }

    abort() {
        this.controller.abort(); // 요청 중단 실행
        console.warn('파일 업로드가 취소되었습니다.');
    }
}