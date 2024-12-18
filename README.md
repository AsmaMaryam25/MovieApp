# MovieApp 🎥  

**Authors**  
- **Turan Talayhan**  
- **Daniel Overballe Lerche**  
- **Marcus Reiner Langkilde**  
- **Asma Maryam**
- **Haleef Abu Talib**

---  


## Table of Contents
- [Prerequisites](#prerequisites)
- [Setup Instructions](#setup-instructions)
- [Overview](#overview)
- [Features](#features)
- [Design Highlights](#design-highlights)
- [Figma Prototype](#figma-prototype)

## Prerequisites
Before setting up anything you are going to need the following things to be able to run and build the project:
- [Android Studio](https://developer.android.com/studio) (version Ladybug or later)
- A [TMDB](https://www.themoviedb.org/) API key
- A [Firebase](https://firebase.google.com/docs/android/setup) project with a [Firestore](https://firebase.google.com/docs/firestore/) database setup which has read and write set to true

## Setup Instructions
1. **Clone the repository**
  ```bash
  git clone https://github.com/AsmaMaryam25/MovieApp.git
  cd MovieApp
  ```
2. **Open the project in Android Studio**
   - Open Android Studio.
   - Click on "File > Open" and select the cloned repository folder.
3. **Sync Gradle**
   - Android Studio will prompt you to sync the Gradle files. Click "Sync Now." and wait till it is finished
4. **Set up API Keys**
   - Add your TMDB API key to the local.properties file
  ```properties
  TMDB_API=<your_api_key_here>
  ```
5. **Setup firebase**
   - Add the `google-services.json` file from your Firebase project in the project settings and put the file in the `app` directory 
6. **Run the App**
   - Connect an Android device or start an emulator.
   - Click the green "Run" button in Android Studio.

## Overview  

This project focuses on building a modern movie application using **Kotlin Jetpack Compose**, **Firebase**, and the **TMDb API**. The app aims to provide users with a seamless experience for discovering, searching, and managing their favorite movies and watchlists.  

---  

## Features  

### 1. Movie Search  
- Users can search for movies by title using a dedicated search page.  
- The results display relevant metadata, including movie thumbnails.  

### 2. Watch List  
- Users can add or remove movies from their personal watchlist.  
- The watchlist is presented as a scrollable list, and selecting a movie redirects to its detailed page.  

### 3. Categorized Home Page  
- Movies are displayed in carousels under categories such as:  
  - **Now Playing**  
  - **Popular**  
  - **Top Rated**  
  - **Upcoming**  

### 4. Movie Metadata  
- The movie details page provides information on:  
  - Cast and crew  
  - Ratings  
  - A button to view trailers on YouTube (if available).  

### 5. User-Friendly Navigation  
- A bottom navigation bar ensures easy access to key sections like Home, Search, Favorites, and Watchlist.  
- The design follows minimalist principles, making it visually calming in both Light and Dark modes.  

---  

## Design Highlights  

- **Consistency**: The thumbnail layout is uniform across search and favorites pages.  
- **Accessibility**: The navigation bar allows users to return to the Home or previous page with a single tap.  
- **Aesthetic Choices**: The app uses a clean color palette, including blue tones, to maintain a professional yet engaging look.  

---  

## Figma Prototype  

[Click here to view the prototype in Figma](https://www.figma.com/proto/p6uLDVN9WIQM0IqQCDdCPY/UI%2FUX-LO-FI-WIREFRAME?node-id=4213-323&t=A9vJNChlRFYsKwyG-1)  
