'use strict';

// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);


// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send('Hello from Firebase!');
// });

/**
 * Triggers a Push notification to user whenever he receives a new message
 */
exports.sendPushNotification = functions.firestore
  .document('/chatMessages/{chatId}/messages/{messageId}')
  .onCreate((event) => {
    const message = event.data();
    const initiatorUid = message.initiatorUid;
    const recipientUid = message.recipientUid;
    console.log(`initiatorUid ${initiatorUid} recipientUid ${recipientUid}`);

    if (recipientUid === initiatorUid) {
      //if sender is receiver, don't send notification
      return;
    }

    admin.firestore().collection('users').doc(recipientUid).get()
      .then((documentSnapshot) => {
        const user = documentSnapshot.data();
        console.log(`Notifying ${user.name} for message- ${message.message}`);

        let notificationTitle = '';
        if (user.name) {
          notificationTitle = user.name;
        } else if (user.email) {
          notificationTitle = user.email;
        } else if (user.mobile) {
          notificationTitle = user.mobile;
        } else {
          // should never happen
          notificationTitle = 'New message Received'
        }

        const payload = {
          notification: {
            title: notificationTitle,
            body: message.message,
            icon: user.photoUrl ? user.photoUrl : 'ic_launcher',
            sound: 'default'
          }
        };

        return admin.messaging().sendToDevice(user.fcmToken, payload)
          .then((response) => {
              console.log(
                `Successfully sent notification to ${user.name}`, response
              );
              return;
          })
          .catch((error) => {
            console.error(`Error sending notification to ${user.name}`, error);
            return;
          });

      })
      .catch((error) => {
        console.error('Error occured: ', error);
        return;
      })

  });