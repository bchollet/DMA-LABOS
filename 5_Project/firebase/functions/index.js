//import firebase functions modules
const functions = require('firebase-functions');
const admin = require("firebase-admin");
const messaging = require("firebase-admin/messaging");
const serviceAccount = require("./key.json");
const { initializeApp } = require('firebase-admin/app');

const app = initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: "https://dma-project-78524-default-rtdb.europe-west1.firebasedatabase.app"
  });

//$env:GOOGLE_APPLICATION_CREDENTIALS="C:\work\key.json"
exports.pushNotification = functions.database.ref('/messages/{msgId}').onCreate((change, context) => {
    console.log('Push notification event triggered : ' + change.key + " " + change.val().content);
	
    //  Get the current value of what was written to the Realtime Database.
    const valueObject = change.val(); 
    // Create a notification
    const message = {
        notification: {
            title: "Nouveau message de " + valueObject.author,
            body: valueObject.content,
        },
        topic: "pushNotifications",
        
    //Create an options object that contains the time to live for the notification and the priority
        // options: {
        //     priority: "high",
        //     timeToLive: 60 * 60 * 24
        // }
    };

    return messaging.getMessaging(app).send(message)
});