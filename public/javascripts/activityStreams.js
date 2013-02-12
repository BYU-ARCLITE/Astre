/**
 * The activity stream helper object. This helps with managing tokens and saving activities.
 * @type {{token: string, renewToken: Function, save: Function}}
 * @author Joshua Monson
 */
var activityStreams = {

    /**
     * The host server. Redefine this to match yours.
     */
    host: "http://localhost:9000/",

    /**
     * The auth token. This is obtained by making a call to the authorization service.
     */
    token: "",

    /**
     * The authorize function. Overwrite this to make an ajax call or do whatever you do to obtain the token.
     * When the token is obtained call the callback function passing in the new token.
     */
    authorize: function(callback) {
        console.error("The activityStreams.authorize function needs to be overwritten.")
    },

    /**
     * This calls the overwritten authorize function, stores the new token, and tries to save the activity again.
     * @param doc
     */
    renewToken: function(doc) {
        activityStreams.authorize(function(t) {
            activityStreams.token = t;
            activityStreams.save(doc);
        });
    },

    /**
     * This makes the ajax call to the ser
     * @param doc The activity object according to the activity stream json specification.
     */
    save: function(doc) {

        // If there is no token then get it
        if (activityStreams.token === "")
            activityStreams.renewToken(doc);
        else {

            // Make the ajax call to save the activity
            $.ajax({
                url: activityStreams.host + "api/1.0/receive",
                type: "POST",
                crossDomain: true,
                data: JSON.stringify(doc),
                contentType: "application/json; charset=utf-8",
                dataType: "json",
                headers: {
                    "Authorization": "Token " + activityStreams.token
                },
                success: function(data) {
                    if (data.success !== true)
                        console.error("Error saving activity. Message: " + data.message);
                },
                error: function(data) {
                    var response = JSON.parse(data.responseText);

                    // Check to see if the reason why we failed is because the token expired
                    if (response.message === "Authorization token is expired")
                        activityStreams.renewToken(doc);
                    else
                        console.error("Error saving activity. Message: " + response.message);
                }
            });
        }
    }
};