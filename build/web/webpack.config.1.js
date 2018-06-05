"use strict";

const path = require("path");

const webpack = require("webpack");
const HtmlPlugin = require("html-webpack-plugin");
const CopyPlugin = require("copy-webpack-plugin");
const HtmlIncludeAssetsPlugin = require("html-webpack-include-assets-plugin");
const ExtractTextPlugin = require("extract-text-webpack-plugin");
const OptimizeCssAssetsPlugin = require("optimize-css-assets-webpack-plugin");

const port = 3000;

module.exports = opts => ({
  mode: 'development',
  context: __dirname,
  devtool: (!opts || !opts.prod) && "source-map",
  entry: ['./src/index.js'],
 
  module: {
    rules: [
      {
        test: /\.jsx?$/,         // Match both .js and .jsx files
        exclude: /node_modules/, 
        loaders: ["remove-hashbag-loader", "babel-loader",  'babel-loader?presets[]=es2015,presets[]=stage-0,presets[]=react'], 
      },
      {
        test: /\.css$/,
        loader: 'style-loader!css-loader'
      }
    ]
  },
  resolveLoader: {
    alias: {
      "remove-hashbag-loader": path.join(__dirname, "./loaders/remove-hashbag-loader"),
      "cesiumSource": "cesium",
      "cesium": "cesium/Cesium"
    }
  },
  output: {
    path: __dirname,
    filename: 'dst/bundle.js'
    },
  plugins: [
    new HtmlPlugin({
      template: "index.html"
    }),
    new HtmlIncludeAssetsPlugin({
      append: false,
      assets: [
        "cesium/Widgets/widgets.css",
        "cesium/Cesium.js"
      ]
    }),
    new CopyPlugin([
      {
        from: `node_modules/cesium/Build/Cesium${(!opts || !opts.prod) ? "" : "Unminified"}`,
        to: "cesium"
      }
    ]),
    new webpack.DefinePlugin({
      "process.env": {
        NODE_ENV: JSON.stringify((!opts || !opts.prod) ? "production" : "development"),
        CESIUM_BASE_URL: JSON.stringify("/cesium")
      }
    })
    ],
    externals: {
      cesium: "Cesium"
    }
  // resolve: {
  //   alias: {
  //     "cesium-react": path.resolve(__dirname, "..", "src")
  //   }
  // }
});