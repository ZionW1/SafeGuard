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
    }
    upload() {
        return this.loader.file
            .then(file => new Promise((resolve, reject) => {
                const data = new FormData();
                data.append('upload', file);

                fetch('/upload/image', { method: 'POST', body: data })
                    .then(response => response.json())
                    .then(result => {
                        if (result.uploaded && result.url) {
                            resolve({ default: result.url });
                        } else {
                            reject(result.error && result.error.message ? result.error.message : '이미지 업로드 실패.');
                        }
                    })
                    .catch(err => reject('파일 업로드 중 네트워크 오류: ' + err));
            }));
    }
    abort() {
        // TODO: 업로드 취소 로직 구현 (필요시)
        console.warn('File upload aborted.');
    }
}