// src/main/resources/static/js/ckeditor-init.js (예시 경로)

// ⭐️ npm으로 설치한 ClassicEditor를 임포트
import ClassicEditor from '@ckeditor/ckeditor5-build-classic';

// ⭐️ 너의 커스텀 업로드 어댑터 플러그인도 임포트해야 해.
// (만약 MyCustomUploadAdapterPlugin 코드가 이 파일 안에 있다면 아래 import는 필요 없어)
import MyCustomUploadAdapterPlugin from './my-custom-adapter-plugin.js'; // 실제 경로와 파일명에 맞춰 수정!

// CKEditor 초기화 코드
ClassicEditor
    .create(document.querySelector('#content'), {
        extraPlugins: [ MyCustomUploadAdapterPlugin ],
        image: {
            toolbar: [
                'imageTextAlternative', 'toggleImageCaption', 'imageStyle:inline', 'imageStyle:block', 'imageStyle:side',
                'resizeImage', 
                'resizeImage:50', 'resizeImage:75', 'resizeImage:original', 
            ],
            resizeOptions: [
                { name: 'resizeImage:original', value: null, icon: 'originalSize' },
                { name: 'resizeImage:50', value: '50%', icon: 'medium' },
                { name: 'resizeImage:75', value: '75%', icon: 'large' }
            ],
            resizeUnit: 'px',
            styles: ['full', 'side', 'alignLeft', 'alignCenter', 'alignRight']
        },
        ckfinder: { // 이 부분은 너의 custom adapter가 처리하므로 불필요할 수 있지만, 예시상 남겨둘게.
            uploadUrl: '/upload/image'
        }
    })
    .catch(error => {
        console.error(error);
    });

// MyCustomUploadAdapterPlugin.js 파일 예시 (별도 파일로 존재 시)
// src/main/resources/static/js/my-custom-adapter-plugin.js
// MyCustomUploadAdapterPlugin을 export default 해줘야 다른 파일에서 import 가능
export default function MyCustomUploadAdapterPlugin(editor) {
    editor.plugins.get('FileRepository').createUploadAdapter = (loader) => {
        return new MyUploadAdapter(loader);
    };
}

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
    abort() { /* ... */ }
}