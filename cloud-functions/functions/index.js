'use strict';

// The Cloud Functions for Firebase SDK to create Cloud Functions and setup triggers.
const functions = require('firebase-functions');

// The Firebase Admin SDK to access the Firebase Realtime Database.
const admin = require('firebase-admin');
admin.initializeApp(functions.config().firebase);

const nodemailer = require('nodemailer');

// TODO: Configure the `gmail.email` and `gmail.password` Google Cloud environment variables.
// For Gmail, enable these:
// 1. https://www.google.com/settings/security/lesssecureapps
// 2. https://accounts.google.com/DisplayUnlockCaptcha
const gmailEmail = functions.config().gmail.email;
const gmailPassword = functions.config().gmail.password;
const mailTransport = nodemailer.createTransport({
  service: 'gmail',
  auth: {
    user: gmailEmail,
    pass: gmailPassword,
  }
});

const APP_NAME = 'SaathMeTravel';


// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send('Hello from Firebase!');
// });

exports.sendWelcomeEmail = functions.auth.user().onCreate((user) => {
  const email = user.email; // The email of the user.
  const displayName = user.displayName; // The display name of the user.
  // [END eventAttributes]

  sendWelcomeEmail(email, displayName);
});

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

// Sends a welcome email to the given user.
function sendWelcomeEmail(email, displayName) {
  if (!email) {
    console.log(`${displayName} has no email`);
    return;
  }
  const mailOptions = {
    from: `${APP_NAME} <info.saathmetravel@gmail.com>`,
    to: email,
  };

  // The user subscribed to the newsletter.
  mailOptions.subject = `Welcome to ${APP_NAME}!`;
  mailOptions.text = `
    Hi ${displayName || ''}! Thanks for signing up.
    ${APP_NAME} is a social travelling app to match the travellers sharing a common journey.

    You can plan your journey, search nearby travellers planning same journey and then connect with them.
    We hope you would enjoy our app.

    Happy Saath Me Travelling :D

    We are in beta. Please provide your valuable feedback at info.saathmetravel@gmail.com
  `;
  mailTransport.sendMail(mailOptions).then(() => {
    console.log('New welcome email sent to:', email);
    return;
  }).catch((error) => {
    console.error('Error sending mail: ', error);
    return;
  })
}