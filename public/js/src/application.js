requirejs.config({
    shim: {
        "underscore": {
            exports: "_"
        },

        "backbone": {
            deps: [
                "underscore",
                "jquery"
            ],
            exports: "Backbone"
        },

        "bootstrap": {
            deps: ["jquery"],
            exports: "$.fn.popover"
        },

        "jquery.ui": {
            deps: ["jquery"],
            exports: "$.fn.draggable"
        }
    },

    paths: {
        "jquery": "../lib/jquery",
        "jquery.ui": "../lib/jquery.ui",
        "jquery.fileupload": "../lib/jquery.fileupload",
        "underscore": "../lib/underscore",
        "backbone": "../lib/backbone",
        "bootstrap": "../lib/bootstrap",
        "require.text": "../lib/require.text"
    },

    enforceDefine: true
});

define([
    "jquery",
    "backbone",
    "router/router",
    "bootstrap",
    "jquery.fileupload"
],

function($, Backbone, Router) {
    var Application = function() {
        this.router = new Router();
    }

    window.application = new Application();
    Backbone.history.start();

    $('#import-file').fileupload({
        url: "/importPdri",
        dataType: 'json',

        add: function (e, data) {
            $('#import').val(data.files[0].name);

            $('.progress-bar').css('width', "0%");
            $('.progress').removeClass("hidden");

            data.submit();
        },

        progressall: function (e, data) {
            var progress = parseInt(data.loaded / data.total * 100, 10);
            $('.progress-bar').css('width', progress + '%');
        },

        done: function (e, data) {
            console.log("uploaded");
            console.log(data.result);
            $('.progress').addClass("hidden");
            $('#import').val("");
            $('<p/>').text("Imported: " + data.files[0].name).appendTo('#files');
            $('<p/>').text("Imported data: " + JSON.stringify(data.result)).appendTo('#files');
        }
    });

});