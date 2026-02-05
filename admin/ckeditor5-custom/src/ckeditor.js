/**
 * @license Copyright (c) 2003-2020, CKSource - Frederico Knabben. All rights reserved.
 * For licensing, see LICENSE.md or https://ckeditor.com/legal/ckeditor-oss-license
 */

// The editor creator to use.
import { Essentials } from '@ckeditor/ckeditor5-essentials/src/essentials';
import { Autoformat } from '@ckeditor/ckeditor5-autoformat/src/autoformat'; // Autoformat도 named export
import { Bold } from '@ckeditor/ckeditor5-basic-styles/src/bold'; // Bold도 named export
import { Italic } from '@ckeditor/ckeditor5-basic-styles/src/italic'; // Italic도 named export
import { BlockQuote } from '@ckeditor/ckeditor5-block-quote/src/blockquote'; // BlockQuote도 named export
import { Font } from '@ckeditor/ckeditor5-font/src/font'; // Font 플러그인을 불러옴 (FontColor, FontBackgroundColor, FontSize를 포함)
import { Alignment } from '@ckeditor/ckeditor5-alignment/src/alignment'; // ⭐️ 글씨 정렬 플러그인 ⭐️

import { FontSize } from '@ckeditor/ckeditor5-font/src/fontsize'; // 글씨 크기 플러그인
import { FontColor } from '@ckeditor/ckeditor5-font/src/fontcolor'; // 글씨 색상 플러그인
import { FontBackgroundColor } from '@ckeditor/ckeditor5-font/src/fontbackgroundcolor'; // 글씨 배경 색상 플러그인

// import { CKFinder } from '@ckeditor/ckeditor5-ckfinder/src/ckfinder'; // 만약 CKFinder 사용한다면 이렇게
// import { EasyImage } from '@ckeditor/ckeditor5-easy-image/src/easyimage'; // 만약 EasyImage 사용한다면 이렇게
import { Heading } from '@ckeditor/ckeditor5-heading/src/heading';
// import { Image, ImageUpload, ImageToolbar, ImageCaption, ImageStyle, ImageResize, ImageTextAlternative } from '@ckeditor/ckeditor5-image/src/image'; // 이미지 관련 플러그인은 보통 한 파일에 묶여있거나, 개별적으로 named export
import { Image } from '@ckeditor/ckeditor5-image/src/image'; // ⭐️ 중괄호 추가! ⭐️

// ⭐️⭐️⭐️ 나머지 이미지 관련 플러그인들은 각각의 파일에서 Named Import ⭐️⭐️⭐️
import { ImageCaption } from '@ckeditor/ckeditor5-image/src/imagecaption';
import { ImageToolbar } from '@ckeditor/ckeditor5-image/src/imagetoolbar'; // ⭐️ 이 줄을 추가해주세요! ⭐️

import { ImageStyle } from '@ckeditor/ckeditor5-image/src/imagestyle';
import { ImageUpload } from '@ckeditor/ckeditor5-image/src/imageupload';
import { ImageResize } from '@ckeditor/ckeditor5-image/src/imageresize';
import { ImageTextAlternative } from '@ckeditor/ckeditor5-image/src/imagetextalternative';

// ⭐️⭐️⭐️ Table 플러그인 임포트 (이것도 'default' export일 가능성이 높음) ⭐️⭐️⭐️
import { Table } from '@ckeditor/ckeditor5-table/src/table'; // ⭐️ 중괄호 추가! ⭐️

// ⭐️⭐️⭐️ 나머지 테이블 관련 플러그인들은 각각의 파일에서 Named Import ⭐️⭐️⭐️
import { TableToolbar } from '@ckeditor/ckeditor5-table/src/tabletoolbar';

import { TableProperties } from '@ckeditor/ckeditor5-table/src/tableproperties'; // ⭐️ 이 부분 ⭐️
import { TableCellProperties } from '@ckeditor/ckeditor5-table/src/tablecellproperties'; // ⭐️ 이 부분 ⭐️

import { Indent } from '@ckeditor/ckeditor5-indent/src/indent';
import { Link } from '@ckeditor/ckeditor5-link/src/link';
import { List } from '@ckeditor/ckeditor5-list/src/list';
import { MediaEmbed } from '@ckeditor/ckeditor5-media-embed/src/mediaembed';
import { Paragraph } from '@ckeditor/ckeditor5-paragraph/src/paragraph';
import { PasteFromOffice } from '@ckeditor/ckeditor5-paste-from-office/src/pastefromoffice';
// import { Table, TableToolbar } from '@ckeditor/ckeditor5-table/src/table';
import { TextTransformation } from '@ckeditor/ckeditor5-typing/src/texttransformation';

// ⭐️ ClassicEditorBase는 예외! 얘는 'default'로 export 되는게 맞을거야. ⭐️
import { ClassicEditor as ClassicEditorBase } from '@ckeditor/ckeditor5-editor-classic/src/classiceditor';

// UploadAdapter도 CKFinder에서 내보낼 때는 named export로 'CKFinderUploadAdapter' 이런 이름일 수 있음.
// import { CKFinderUploadAdapter } from '@ckeditor/ckeditor5-adapter-ckfinder/src/uploadadapter';

// ckeditor5-custom/src/ckeditor.js 파일 (위에서부터 찾아가며 수정)

// ... (기존 import 문들) ...

// ⭐️⭐️⭐️ 여기에 마이클의 커스텀 업로드 어댑터 코드 추가 ⭐️⭐️⭐️
class MyCustomUploadAdapter {
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

class MyUploadAdapterPlugin {
    constructor(editor) {
        editor.plugins.get('FileRepository').createUploadAdapter = (loader) => {
            return new MyCustomUploadAdapter(loader);
        };
    }
}
// ⭐️⭐️⭐️ 여기까지 MyCustomUploadAdapterPlugin 정의 ⭐️⭐️⭐️

// ... (기존 ClassicEditor 클래스 정의 시작) ...

export default class ClassicEditor extends ClassicEditorBase {
    // static defaultConfig = { ... }; // ⭐️⭐️⭐️ 이 부분을 삭제! ⭐️⭐️⭐️

    // ... (필요하다면 다른 멤버 변수나 메서드 추가 가능) ...
}


// Editor configuration.
ClassicEditor.defaultConfig = {
	toolbar: {
		items: [
			'heading',
			'|',
			'bold',
			'italic',
            // 'fontColor', // ⭐️ 새로 추가! ⭐️
            // 'fontBackgroundColor', // ⭐️ 새로 추가! ⭐️
            '|',
            // 'alignment', // ⭐️ 새로 추가! ⭐️
			'link',
			'bulletedList',
			'numberedList',
			'|',
			'indent',
			'outdent',
			'|',
			'imageUpload',
			'blockQuote',
			'insertTable',
			'mediaEmbed',
			'undo',
			'redo'
		]
	},
	image: {
        toolbar: [
            'imageTextAlternative',  // 이미지 대체 텍스트 편집
            'toggleImageCaption',    // 이미지 캡션 토글
            'imageStyle:inline',     // 이미지 인라인 스타일
            'imageStyle:block',      // 이미지 블록 스타일
            'imageStyle:side',       // 이미지 사이드(좌/우) 스타일
            '|',                     // 구분선
            'resizeImage',           // 드래그로 크기 조절하는 핸들 활성화 ⭐️중요⭐️
            'resizeImage:50',        // 50% 크기 버튼
            'resizeImage:75',        // 75% 크기 버튼
            'resizeImage:original'   // 원본 크기 버튼
        ],
        resizeOptions: [             // 크기 선택 옵션
            { name: 'resizeImage:original', value: null, icon: 'originalSize' },
            { name: 'resizeImage:50', value: '50%', icon: 'medium' },
            { name: 'resizeImage:75', value: '75%', icon: 'large' }
        ],
        styles: [                   // 이미지 정렬 스타일
            'full', 'side', 'alignLeft', 'alignCenter', 'alignRight'
        ]
    },
	table: {
        contentToolbar: [
            'tableColumn',
			'tableRow',
			'mergeTableCells',
            '|',
            'tableProperties',
            'tableCellProperties',
            '|',
            'toggleTableCaption'
        ]
	},
	// This value must be kept in sync with the language defined in webpack.config.js.
	language: 'ko',

    // ⭐️⭐️⭐️ 여기에 licenseKey 설정 추가 ⭐️⭐️⭐️ClassicEditor.defaultConfig
    licenseKey: 'GPL', 
    // ⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️⭐️

    // 만약 이 licenseKey가 최상단 config 객체가 아닌
    // ckfinder나 다른 특정 플러그인의 설정 안에서 사용되어야 한다면,
    // 해당 플러그인의 설정 객체 안에 넣어야 할 수도 있어.
    // 하지만 대부분의 경우 defaultConfig 최상단에 넣는 것이 효과적이야.

};

// ⭐️⭐️⭐️ 클래스 밖에서 defaultConfig 설정 추가 ⭐️⭐️⭐️
// Plugins to include in the build.
ClassicEditor.builtinPlugins = [
	Essentials,
	// CKFinderUploadAdapter,
	Autoformat,
	Bold,
	Italic,
	BlockQuote,
	Heading,
    Font,
    Alignment, // ⭐️ 여기에 추가 ⭐️
	Image,
	ImageCaption,
	ImageStyle,
	ImageToolbar,
	ImageUpload, // 이 부분은 그대로 두자. 기본 이미지 업로더로 사용!
    ImageResize,
	Indent,
	Link,
	List,
	MediaEmbed,
	Paragraph,
	PasteFromOffice,
	Table,
	TableToolbar,
    TableProperties, // ⭐️ 이 부분 ⭐️
    TableCellProperties, // ⭐️ 이 부분 ⭐️
	TextTransformation,
    ImageTextAlternative,
    MyUploadAdapterPlugin
];