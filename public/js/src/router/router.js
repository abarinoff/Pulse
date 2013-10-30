define([
    "jquery",
    "backbone"
],

function($, Backbone) {

    var Router = Backbone.Router.extend({
        routes: {
            "": "home"
        },

        home: function () {
            console.log("Navigated to home");
        }
    });

    return Router;
});