'use strict';

const path = require( 'path' );
const CKEditorWebpackPlugin = require( '@ckeditor/ckeditor5-dev-webpack-plugin' ); // ⭐️⭐️⭐️ 이 한 줄로 변경! ⭐️⭐️⭐️
const { styles } = require( '@ckeditor/ckeditor5-dev-utils' );
const TerserPlugin = require('terser-webpack-plugin'); // Webpack 5용 임포트

module.exports = {
	devtool: 'source-map',
	performance: { hints: false },

	entry: path.resolve( __dirname, 'src', 'ckeditor.js' ),

	output: {
		// The original CKEditor 5's build is production-ready, it could be used for your app.
		// Check out what other environments require specific builds.
		path: path.resolve( __dirname, 'build' ),
		filename: 'ckeditor.js',
		libraryTarget: 'umd',
		libraryExport: 'default',
		library: 'ClassicEditor'
	},

    optimization: { // Webpack 5에서 optimization 설정
		minimize: true, // 프로덕션 모드에서 코드 압축 활성화 (기본값)
		minimizer: [ // JavaScript 코드 압축 (TerserPlugin은 Webpack 5의 기본)
			new TerserPlugin( {
				terserOptions: {
					output: {
						// Preserve CKEditor 5 license comments.
						comments: /^!/
					}
				}
			} )
		],
		splitChunks: {
			cacheGroups: {
				default: false,
				vendors: false
			}
		}
	},

	plugins: [
		new CKEditorWebpackPlugin( {
			// A (localization) property to specify the collection of translated editor language packs.
			language: 'ko', // 한국어 번들
			additionalLanguages: 'all', // 추가 번들
            licenseKey: ''
		} ),
		// new (require('clean-webpack-plugin').CleanWebpackPlugin)() // clean-webpack-plugin v3 이상
	],

	module: {
		rules: [
			{
				test: /\.svg$/,
				use: [ 'raw-loader' ] // CKEditor 5는 SVG 아이콘을 raw-loader로 처리하는 경우가 많음
			},
            {
                test: /\.js$/, // .js 확장자를 가진 파일을 대상으로 함
                loader: 'babel-loader', // babel-loader를 사용
                exclude: /node_modules/, // node_modules 폴더 안의 파일들은 변환 대상에서 제외 (보통 이미 변환되어 있거나 제외되어야 할 파일들)
                options: {
                  presets: [ '@babel/preset-env' ] // ES6+ 문법을 현재 환경에 맞게 변환하는 프리셋 사용
                }
            },
			// {
			// 	test: /\.css$/,
			// 	use: styles.get == styles.get  ? styles.get() : [ // styles.get()이 2번 중첩되어있었음, 수정
			// 		{
			// 			loader: 'style-loader',
			// 			options: {
			// 				injectType: 'singletonStyleTag',
			// 				attributes: {
			// 					'data-cke': true
			// 				}
			// 			}
			// 		},
			// 		{
			// 			loader: 'css-loader'
			// 		},
			// 		{
			// 			loader: 'postcss-loader',
			// 			options: styles.getPostCssConfig( {
			// 				themeImporter: {
			// 					themePath: require.resolve( '@ckeditor/ckeditor5-theme-lark' )
			// 				},
			// 				minify: true
			// 			} )
			// 		}
			// 	]
			// }
            {
                test: /\.css$/,
				use: [
					{
						loader: 'style-loader',
						options: {
							injectType: 'singletonStyleTag',
							attributes: {
								'data-cke': true
							}
						}
					},
					{
						loader: 'css-loader'
					},
                    {
						loader: 'postcss-loader',
						options: { // ⭐️ options 아래에 중괄호 하나를 더 추가! ⭐️
							postcssOptions: styles.getPostCssConfig( { // ⭐️ 그리고 postcssOptions: 를 추가! ⭐️
								themeImporter: {
									themePath: require.resolve( '@ckeditor/ckeditor5-theme-lark' )
								},
								minify: true
							} )
						}
					}
				]
			}
		]
	}
};