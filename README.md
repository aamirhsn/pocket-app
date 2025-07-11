# Pocket - Secure Spend Tracking & Card Storage

A modern Android application for secure spend tracking and card storage, built with Jetpack Compose and Material 3 design principles.

## Features

### 🔐 Security
- **Biometric Authentication**: Fingerprint/Face unlock required to access the app
- **Encrypted Local Storage**: All sensitive data encrypted using SQLCipher
- **Secure Card Storage**: Credit/debit card information stored with encryption

### 💰 Spend Tracking
- **Add Spends**: Track expenses with category, amount, place, and online/offline status
- **Categories**: Food, Transport, Shopping, Entertainment, Bills, and more
- **Analytics**: Visual charts showing spend distribution by category
- **Local Storage**: All data stored securely on device

### 💳 Card Management
- **Store Cards**: Save credit and debit card information securely
- **Card Details**: Name, number, expiry date, CVV, and card type
- **Secure Display**: Card numbers and CVV hidden by default with show/hide toggle
- **Encrypted Storage**: All card data encrypted in local database

### 🎨 Modern UI/UX
- **Material 3 Design**: Latest Android design language with expressive UI
- **Apple-like Colors**: Modern, engaging color scheme inspired by Apple's design
- **Bottom Navigation**: Easy navigation between Spend Tracker and Cards sections
- **Responsive Layout**: Optimized for different screen sizes

## Architecture

### Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Design System**: Material 3
- **Navigation**: Jetpack Navigation Compose
- **Database**: Room with SQLCipher encryption
- **Authentication**: BiometricPrompt API
- **Charts**: Custom Canvas-based charts

### Project Structure
```
app/src/main/java/com/phone/pocket/
├── MainActivity.kt                 # Main app entry point
├── auth/
│   └── BiometricHelper.kt         # Biometric authentication
├── data/
│   ├── Card.kt                    # Card entity
│   ├── Spend.kt                   # Spend entity
│   ├── CardDao.kt                 # Card database operations
│   ├── SpendDao.kt                # Spend database operations
│   └── PocketDatabase.kt          # Encrypted Room database
├── ui/
│   ├── screens/
│   │   ├── SplashScreen.kt        # Authentication screen
│   │   ├── SpendTrackerScreen.kt  # Spend tracking UI
│   │   └── CardsScreen.kt         # Card management UI
│   ├── components/
│   │   └── SpendChart.kt          # Analytics chart component
│   └── theme/
│       ├── Color.kt               # Custom color palette
│       ├── Theme.kt               # Material 3 theme
│       └── Type.kt                # Typography
```

## Setup & Installation

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 11 or higher
- Android SDK API 27+ (Android 8.1)

### Build Instructions
1. Clone the repository
2. Open the project in Android Studio
3. Sync Gradle files
4. Build the project: `./gradlew assembleDebug`
5. Install on device or emulator

### Permissions
The app requires the following permissions:
- `USE_BIOMETRIC`: For fingerprint/face authentication
- `USE_FINGERPRINT`: For fingerprint authentication (legacy)

## Security Features

### Data Encryption
- All sensitive data encrypted using SQLCipher
- Database password stored securely
- Card numbers and CVV encrypted at rest

### Authentication
- Biometric authentication required on app launch
- Graceful fallback if biometric not available
- Secure session management

### Privacy
- All data stored locally on device
- No cloud synchronization
- No data collection or analytics

## Usage

### First Launch
1. App shows splash screen with Pocket logo
2. Biometric authentication prompt appears
3. Authenticate using fingerprint or face
4. Access main app interface

### Adding Spends
1. Navigate to "Spend Tracker" tab
2. Tap the "+" floating action button
3. Fill in spend details:
   - Category (dropdown)
   - Amount (₹)
   - Place
   - Online/Offline toggle
   - Notes (optional)
4. Tap "Add" to save

### Adding Cards
1. Navigate to "Cards" tab
2. Tap the "+" floating action button
3. Fill in card details:
   - Card name
   - Card number (16 digits)
   - Expiry date (MM/YY)
   - CVV
   - Card type (Credit/Debit)
4. Tap "Add" to save

### Viewing Analytics
- Spend distribution chart appears automatically when data is available
- Chart shows spend by category with color-coded bars
- Legend displays category names and amounts

## Development

### Adding New Features
1. Follow the existing architecture patterns
2. Use Material 3 components for UI
3. Implement proper error handling
4. Add unit tests for new functionality

### Database Schema
```sql
-- Cards table
CREATE TABLE cards (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    number TEXT NOT NULL, -- encrypted
    expiry TEXT NOT NULL,
    cvv TEXT NOT NULL, -- encrypted
    type TEXT NOT NULL
);

-- Spends table
CREATE TABLE spends (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    category TEXT NOT NULL,
    amount REAL NOT NULL,
    place TEXT NOT NULL,
    online INTEGER NOT NULL,
    date INTEGER NOT NULL,
    notes TEXT
);
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For issues and feature requests, please create an issue in the repository.

---

**Pocket** - Secure, modern, and intuitive spend tracking for Android. 