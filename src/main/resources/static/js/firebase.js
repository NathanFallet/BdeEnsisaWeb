// Import the functions you need from the SDKs you need
import { initializeApp } from "https://www.gstatic.com/firebasejs/9.17.1/firebase-app.js";
import { getAnalytics } from "https://www.gstatic.com/firebasejs/9.17.1/firebase-analytics.js";
// TODO: Add SDKs for Firebase products that you want to use
// https://firebase.google.com/docs/web/setup#available-libraries

// Your web app's Firebase configuration
// For Firebase JS SDK v7.20.0 and later, measurementId is optional
const firebaseConfig = {
    apiKey: "AIzaSyBSOu-OlYeUyyaDLy4YFBro8UbvN79zk2Q",
    authDomain: "bde-ensisa.firebaseapp.com",
    projectId: "bde-ensisa",
    storageBucket: "bde-ensisa.appspot.com",
    messagingSenderId: "1014585297543",
    appId: "1:1014585297543:web:0b126688528fccf86b8622",
    measurementId: "G-GL9NLCLL4P"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const analytics = getAnalytics(app);
