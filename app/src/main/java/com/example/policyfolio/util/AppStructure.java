package com.example.policyfolio.util;

public class AppStructure {
    /*
    THIS CLASS IS ONLY CREATED TO EXPLAIN THE STRUCTURE OF THE APPLICATION.

    The app uses fragments and the activities only handle intents and callbacks
        Every Activity has its own callback that is used to transfer control between the activity and its fragments

    Every Activity is bound with a View Model that is used to store and share data between the Activity and its Fragments

    All the data is fetched using a repository which has a single instance throughout the course of the Apps life
        The Repository fetches its data from
            GRAPH API - used to fetch User's Facebook profile
            DATA MANAGER - used to fetch Database from Firestore
            CACHE - a class that stores recently fetched information
            APP DATABASE - an app level database created using Room
        Repository Returns Live Data to the VieModels and Activities/Fragments use observers to modify th UI to reflect the changes.
        1. The data is fetched from the Cache to avoid delay.
        2. Meanwhile a background thread fetches data from the AppLevel Database.
        3. Also, the instance if datamanager constantly updates the AppLevel database by fetching ot from Firestore

    Constants stores all the "constant" values used throughout the app.

    Every Function Name is chosen so as to explain its functionality and is followed with a brief documentation
    */
}
