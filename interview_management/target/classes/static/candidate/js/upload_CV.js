//import modules
import { initializeApp } from "https://www.gstatic.com/firebasejs/11.0.1/firebase-app.js";
import { getStorage, ref, uploadBytesResumable, getDownloadURL } from "https://www.gstatic.com/firebasejs/11.0.1/firebase-storage.js";
console.log("Upload CV script loaded");

// Firebase config
const firebaseConfig = {
    apiKey: "AIzaSyA_9NzSD9i6dCQJWUU66l9rLdsg9MfvNbg",
    authDomain: "interviewmangement-7da65.firebaseapp.com",
    projectId: "interviewmangement-7da65",
    storageBucket: "interviewmangement-7da65.appspot.com",
    messagingSenderId: "29115534424",
    appId: "1:29115534424:web:3181f68a570e6a47ef4e2d",
    measurementId: "G-BJCK4P9GRM"
};

// Initialize Firebase
const app = initializeApp(firebaseConfig);
const storage = getStorage(app);

/**
 * Upload CV file to Firebase Storage
 * @param file
 */
function uploadCV(file) {

    //path to store file
    const storageRef = ref(storage, `CV/${file.name}`);

    // Upload file
    const uploadTask = uploadBytesResumable(storageRef, file);

    // Update progress bar
    uploadTask.on('state_changed', (snapshot) => {
        const progress = (snapshot.bytesTransferred / snapshot.totalBytes) * 100;
        console.log('Upload is ' + progress + '% done');

        // Update progress bar
        document.getElementById("cvName").placeholder = progress === 100 ? "UPLOAD DONE" : "UPLOADING...";

    }, (error) => {
        console.error("Error uploading CV:", error);
    }, async () => {
        //Get download URL
        const downloadURL = await getDownloadURL(uploadTask.snapshot.ref);
        document.getElementById('cvName').value = file.name;
        document.getElementById('cvLink').value = downloadURL;
        console.log('CV download URL:', downloadURL);

        // Display see CV link
        const seeCVLink = document.getElementById('seeCVLink');
        seeCVLink.href = downloadURL;
        seeCVLink.style.display = 'inline';
        seeCVLink.textContent = 'See CV';
    });
}

// Listen for file selection
document.getElementById('cv').addEventListener('change', (event) => {
    const file = event.target.files[0];
    document.getElementById('cvLink').value = '';
    if (file) {
        uploadCV(file);
        console.log("File selected:", file);
    }else {
        console.error("File not found");
    }
});
