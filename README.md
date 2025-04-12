# Personal Finance Manager

**Personal Finance Manager** is an offline-first Android application designed to help users take control of their finances with privacy and ease. It allows tracking of income, assets, EMIs, and recurring transactions, along with visual insights to support better financial decisions.

## Features

- Track income sources, cash in hand, bank balances, fixed deposits (FDs), and recurring deposits (RDs).
- Schedule and manage recurring transactions for better financial planning.
- Visualize your asset distribution using interactive charts.
- Offline-first architecture – all data is stored locally for maximum privacy.
- Manage EMIs and loan repayments with automatic calculations or timely reminders.
- Planned support for AI-powered personalized financial recommendations.

## Tech Stack

- **Android Studio** – Native Android development environment
- **Java** – Core programming language
- **Offline Storage (SQL Lite)** – Secure local data handling
- **MPAndroidChart** – Data visualization
- **AndroidX & Jetpack Libraries** – Modern development components

## Dependencies

```gradle
implementation libs.appcompat
implementation libs.material
implementation libs.activity
implementation libs.constraintlayout
implementation libs.biometric
implementation "androidx.security:security-crypto:1.1.0-alpha06"
implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'

testImplementation libs.junit
androidTestImplementation libs.ext.junit
androidTestImplementation libs.espresso.core
