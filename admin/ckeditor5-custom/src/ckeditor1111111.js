
import { Essentials } from '@ckeditor/ckeditor5-essentials/src/essentials';
import { Autoformat } from '@ckeditor/ckeditor5-autoformat/src/autoformat';
import { Bold } from '@ckeditor/ckeditor5-basic-styles/src/bold';
import { Italic } from '@ckeditor/ckeditor5-basic-styles/src/italic';
import { BlockQuote } from '@ckeditor/ckeditor5-block-quote/src/blockquote';
import { Font } from '@ckeditor/ckeditor5-font/src/font';
import { Alignment } from '@ckeditor/ckeditor5-alignment/src/alignment';
import { FontSize } from '@ckeditor/ckeditor5-font/src/fontsize';
import { FontColor } from '@ckeditor/ckeditor5-font/src/fontcolor';
import { FontBackgroundColor } from '@ckeditor/ckeditor5-font/src/fontbackgroundcolor';
import { Heading } from '@ckeditor/ckeditor5-heading/src/heading';
import { Image } from '@ckeditor/ckeditor5-image/src/image';
import { ImageCaption } from '@ckeditor/ckeditor5-image/src/imagecaption';
import { ImageToolbar } from '@ckeditor/ckeditor5-image/src/imagetoolbar';
import { ImageStyle } from '@ckeditor/ckeditor5-image/src/imagestyle';
import { ImageUpload } from '@ckeditor/ckeditor5-image/src/imageupload';
import { ImageResize } from '@ckeditor/ckeditor5-image/src/imageresize';
import { ImageTextAlternative } from '@ckeditor/ckeditor5-image/src/imagetextalternative';
import { Table } from '@ckeditor/ckeditor5-table/src/table';
import { TableToolbar } from '@ckeditor/ckeditor5-table/src/tabletoolbar';
import { TableProperties } from '@ckeditor/ckeditor5-table/src/tableproperties';
import { TableCellProperties } from '@ckeditor/ckeditor5-table/src/tablecellproperties';️
import { Indent } from '@ckeditor/ckeditor5-indent/src/indent';
import { Link } from '@ckeditor/ckeditor5-link/src/link';
import { List } from '@ckeditor/ckeditor5-list/src/list';
import { MediaEmbed } from '@ckeditor/ckeditor5-media-embed/src/mediaembed';
import { Paragraph } from '@ckeditor/ckeditor5-paragraph/src/paragraph';
import { PasteFromOffice } from '@ckeditor/ckeditor5-paste-from-office/src/pastefromoffice';
import { TextTransformation } from '@ckeditor/ckeditor5-typing/src/texttransformation';
import { ClassicEditor as ClassicEditorBase } from '@ckeditor/ckeditor5-editor-classic/src/classiceditor';

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

                    .catch(error => {
                        console.error('파싱 에러:', error);
                        alert('서버 응답에 문제가 있습니다. 관리자에게 문의하세요.');
                      });
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

export default class ClassicEditor extends ClassicEditorBase {

}



ClassicEditor.defaultConfig = {
	toolbar: {
		items: [
			'heading',
			'|',
			'bold',
			'italic',
            '|',
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
            'imageTextAlternative',
            'toggleImageCaption',
            'imageStyle:inline',
            'imageStyle:block',
            'imageStyle:side',
            '|',
            'resizeImage',
            'resizeImage:50',
            'resizeImage:75',
            'resizeImage:original'
        ],
        resizeOptions: [
            { name: 'resizeImage:original', value: null, icon: 'originalSize' },
            { name: 'resizeImage:50', value: '50%', icon: 'medium' },
            { name: 'resizeImage:75', value: '75%', icon: 'large' }
        ],
        styles: [
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

	language: 'ko',
    licenseKey: 'GPL',
};
ckeditor.js, my-custom-adapter.js, ckeditor-init.js
ClassicEditor.builtinPlugins = [
	Essentials,
	Autoformat,
	Bold,
	Italic,
	BlockQuote,
	Heading,
    Font,
    Alignment,
	Image,
	ImageCaption,
	ImageStyle,
	ImageToolbar,
	ImageUpload,
    ImageResize,
	Indent,
	Link,
	List,
	MediaEmbed,
	Paragraph,
	PasteFromOffice,
	Table,
	TableToolbar,
    TableProperties,
    TableCellProperties,
	TextTransformation,
    ImageTextAlternative,
    MyUploadAdapterPlugin
];
