var path = require('path');
var webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');
const HtmlIncludeAssetsPlugin = require('html-webpack-include-assets-plugin');

// The path to the cesium source code
const cesiumSource = 'node_modules/cesium/Source';
const cesiumWorkers = '../Build/Cesium/Workers';


module.exports = {
    context: __dirname,

    //mode: 'production',
    devtool: 'source-map',
    entry: ['./src/App.js'],
    output: {
        path: __dirname+"/dst",
        filename: 'bundle.js',
         // Needed by Cesium for multiline strings
         sourcePrefix: ''
    },
    amd: {
        // Enable webpack-friendly use of require in cesium
        toUrlUndefined: true
    },
    resolve: {
        extensions: ['.js', '.jsx', '.css'],
        alias: {
            // Cesium module name
            cesium: path.resolve(__dirname, cesiumSource)
        }
    },
    externals: {
        cesium: "Cesium"
      },
    plugins: [
        new HtmlWebpackPlugin({
            template: 'src/index.html'
        }),
        // Copy Cesium Assets, Widgets, and Workers to a static directory
        new CopyWebpackPlugin([{from: path.join(cesiumSource, cesiumWorkers), to: 'Workers'}]),
        new CopyWebpackPlugin([{from: path.join(cesiumSource, 'Assets'), to: 'Assets'}]),
        new CopyWebpackPlugin([{from: path.join(cesiumSource, 'Widgets'), to: 'Widgets'}]),
        new webpack.DefinePlugin({
            // Define relative base path in cesium for loading assets
            CESIUM_BASE_URL: JSON.stringify('')
        }),
        // Split cesium into a seperate bundle
        new webpack.optimize.CommonsChunkPlugin({
            name: 'cesium',
            filename: 'cesium.js',
            minChunks: function (module) {
                return module.context && module.context.indexOf('cesium') !== -1;
            }
        }),
        new CopyWebpackPlugin([
            {
              from: `node_modules/cesium/Build/CesiumUnminified`,
              to: "cesium"
            }
        ]),
        new HtmlIncludeAssetsPlugin({
            append: false,
            assets: [
                "cesium/Widgets/widgets.css",
                "cesium/Cesium.js"
            ]
        }),
        new webpack.DefinePlugin({
            "process.env": {
                NODE_ENV: JSON.stringify("development"),
                CESIUM_BASE_URL: JSON.stringify("/cesium")
            }
        }) 
    ],
    module: {

        rules: [
            {
                test: /\.css$/,
                loader: "style-loader!css-loader"
            },
            {
                test: /\.(png|gif|jpg|jpeg|svg|xml|json)$/,
                use: ['url-loader']
            },
            {
                test: /\.(js|jsx)$/,
                loaders: 'babel-loader',
                exclude: /node_modules/,
                options: {
                    plugins: [
                        [
                            "babel-plugin-transform-builtin-extend", {
                                globals: ["Error", "Array"]
                            }
                        ]
                    ],
                    presets: ['es2015', 'stage-0', 'react'],
                }
            },
            {
                test: /\.less$/,
                use: [{
                        loader: "style-loader",
                        options: {
                            url: false
                        }
                    },
                    {
                        loader: "css-loader",
                        options: {
                            url: false
                        }
                    },
                    {
                        loader: "less-loader",
                        options: {
                            url: false
                        }
                    }
                ]
            }
        ]
    }
};