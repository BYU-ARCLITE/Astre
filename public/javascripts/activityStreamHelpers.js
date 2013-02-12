/**
 * This is an example helper file. You can create your own for your own application.
 * @type {{generator: string, provider: string, objects: {captionTrack: Function, image: Function, movie: Function, user: Function, word: Function}, activities: {click: Function}}}
 */
var activityStreamHelpers = {
    generator: "Unknown Application",
    provider: "Unkown Application (via activityStreams.js)",

    objects: {
        captionTrack: function(name, movie, uid, url, author) {
            return {
                attachments: [movie],
                author: author,
                displayName: name,
                id: activityStreamHelpers.generator + "|" + activityStreamHelpers.provider + ":[captionTrack]" + uid,
                summary: "Caption track for movie: " + movie.displayName,
                url: url
            };
        },

        image: function (url, width, height) {
            return {
                url: url,
                width: width,
                height: height
            };
        },

        movie: function (name, url, uid, image, summary) {
            return {
                displayName: name,
                id: activityStreamHelpers.generator + "|" + activityStreamHelpers.provider + ":[movie]" + uid,
                objectType: "movie",
                image: image,
                summary: summary,
                url: url
            };
        },

        user: function (name, uid, url, image) {
            return {
                displayName: name,
                id: activityStreamHelpers.generator + "|" + activityStreamHelpers.provider + ":[user]" + uid,
                objectType: "person",
                image: image,
                url: url
            };
        },

        word: function (word, uid) {
            return {
                displayName: word,
                id: activityStreamHelpers.generator + "|" + activityStreamHelpers.provider + ":[word]" + uid,
                objectType: "word"
            }
        }
    },

    activities: {
        click: function (actor, object, target) {
            var time = new Date();
            var timestamp = time.getTime();
            var timeString = time.toISOString();
            return {
                actor: actor,
                content: "",
                generator: activityStreamHelpers.generator,
                id: activityStreamHelpers.generator + "|" + activityStreamHelpers.provider + ":" + actor.displayName +
                    ":click@" + timestamp,
                object: object,
                published: timeString,
                provider: activityStreamHelpers.provider,
                target: target,
                title: actor.displayName + " clicked on " + object.displayName + " in " + target.displayName,
                verb: "click"
            };
        }
    }
};