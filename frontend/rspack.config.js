const path = require('path');
const { rspack } = require('@rspack/core');

const PORT = 3000;

const argv = process.argv.slice(2);
const isProduction = argv.indexOf('production') > -1;

const confg = {
	entry: './src/index.tsx',
	devtool: isProduction ? 'hidden-nosources-source-map' : 'eval-source-map',
	mode: isProduction ? 'production' : 'development',
	output: {
		publicPath: 'auto',
	},
	devServer: {
		liveReload: true,
		static: [path.resolve(__dirname)],
		port: PORT,
		open: true,
	},
	module: {
		rules: [
			{
				test: /\.([jt]sx?)$/,
				loader: 'ts-loader',
				options: {
					// don't fail build on TS issues - linting will help you manage those
					transpileOnly: true,
				},
			},
			{
				test: /\.css$/i,
				use: ['style-loader', 'css-loader'],
			},
			{
				test: /\.(eot|svg|ttf|woff|woff2|png|jpg|gif)$/i,
				type: 'asset',
			},
		],
	},
	plugins: [
		new rspack.HtmlRspackPlugin({
			template: path.resolve(__dirname, './public/index.html'),
			filename: 'index.html',
		}),

		new rspack.CopyRspackPlugin({
			patterns: [
				{
					from: 'public',
					globOptions: {
						ignore: ['*/index.html'], // Exclude index.html properly
					},
				},
			],
		}),
	].filter(Boolean),
	resolve: {
		extensions: ['.ts', '.tsx', '.js', '.jsx', '.json'],
		tsConfig: path.resolve(__dirname, './tsconfig.json'),
	},
	optimization: {
		minimize: isProduction,
		minimizer: [
			new rspack.SwcJsMinimizerRspackPlugin({
				extractComments: true,
			}),
			new rspack.LightningCssMinimizerRspackPlugin(),
		],
	},
	performance: {
		hints: false,
	},
};

module.exports = confg;
