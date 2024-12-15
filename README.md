# Stock Market App

A comprehensive stock market analysis app built with Jetpack Compose, Hilt, Room, Retrofit, and more. This app provides features like real-time stock data visualization, watchlist management, and insights generation using GPT-3.5 for trading analysis.

## Features

- **Real-Time Stock Data**: Fetch and display intraday, weekly, and monthly stock data with detailed insights.
- **Watchlist Management**: Add, remove, and view companies in your personalized watchlist.
- **Company Listings**: Search and filter companies from a database of stock listings.
- **Trading Analysis**: Leverage OpenAI's GPT-3.5 to analyze intraday trading data and provide insights.
- **Offline Support**: Caches data locally using Room Database for smooth offline access.
- **User Authentication**: Manage user authentication using Firebase.
- **Modern UI**: Built with Jetpack Compose for a clean and responsive UI.
- **Navigation**: Uses Compose Destinations for structured and type-safe navigation.

## Tech Stack

### Frontend
- **Kotlin**: The language for all app development.
- **Jetpack Compose**: For building a responsive and modern UI.
- **Compose Destinations**: Simplifies navigation in a strongly typed way.
- **Accompanist**: Adds utility components like swipe refresh.
- **ComposeChart**: https://github.com/ehsannarmani/ComposeCharts

### Backend
- **Room**: Local database for caching stock data.
- **Retrofit**: Networking library to fetch stock data from Alpha Vantage API.
- **OkHttp**: HTTP client for streaming and networking tasks.
- **Firebase**: For authentication and user data storage.

### Dependency Injection
- **Hilt**: For dependency injection and modular app architecture.

### Parsing
- **OpenCSV**: Parses CSV data from APIs for stock information.

### AI Integration
- **OpenAI GPT-3.5**: Provides trading insights by analyzing intraday stock data.

## IMPORTANT!!
You have  to go your local.properties file add your GPT API KEYS:

OPENAI_API_KEY = yourapikey

More Info:
https://openai.com/index/openai-api/



