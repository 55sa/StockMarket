# 📊 **StockEasy**: Real-Time Stock Market Analysis App

A powerful **stock market analysis** app built with **Jetpack Compose**, **Hilt**, and **Retrofit**. **StockEasy** provides real-time data visualization, watchlist management, and cutting-edge AI-driven trading insights, empowering users to make data-driven investment decisions.

---

## 🌟 **Key Features**

### 🔹 **Real-Time Stock Data**
- Fetch intraday, weekly, and monthly stock data using the **Alpha Vantage API**.
- Visualize data through interactive **charts** with smooth animations.

### 🔹 **Watchlist Management**
- Create a personalized watchlist to monitor your favorite stocks.
- Add or remove companies with a single tap.

### 🔹 **Company Listings**
- Search and filter from a comprehensive **Nasdaq stock database**.
- Access detailed company information, including sector, industry, and performance metrics.

### 🔹 **AI-Powered Trading Insights**
- Leverage **OpenAI GPT-3.5** to analyze trading data and generate actionable insights.
- Identify trends, active trading periods, and opportunities based on historical data.

### 🔹 **User Behavior Analysis**
- Weekly analysis of trading activity: **total trades**, **sector performance**, and most traded stocks.
- Detailed breakdown of **buying** and **selling patterns**.

### 🔹 **Offline Support**
- Seamlessly cache stock data using **Room Database** for offline access.

### 🔹 **Secure User Authentication**
- Manage user accounts securely with **Firebase Authentication**.

---

## 🛠️ **Tech Stack**

### **Frontend**
- **Kotlin**: Core development language.
- **Jetpack Compose**: Modern UI toolkit for a clean, responsive interface.
- **Compose Destinations**: Type-safe and structured navigation.
- **Accompanist**: Enhances UI capabilities like swipe refresh and animations.
- **ComposeChart**: Elegant chart visualizations for trading data.

### **Backend**
- **Retrofit**: Networking library for seamless API integrations.
- **OkHttp**: Handles HTTP requests for stock data streaming.
- **Room Database**: Local storage for caching and offline support.

### **AI Integration**
- **OpenAI GPT-3.5**: Generates actionable trading insights and weekly summaries.

### **Other Tools**
- **Hilt**: Dependency injection for clean and modular architecture.
- **OpenCSV**: Parses and processes large CSV datasets.

---

## 💻 **Screenshots**

![MainScreen](ScreenShot/MainScreen.png)

![Search](ScreenShot/Search.png)
### Main Screen
<img src="ScreenShot/MainScreen.png" alt="MainScreen" width="300"/>

![Company1](ScreenShot/Company1.png)
### Search Screen
<img src="ScreenShot/Search.png" alt="Search" width="300"/>

![Company2](ScreenShot/Company2.png)
### Company Details 1
<img src="ScreenShot/Company1.png" alt="Company1" width="300"/>

![Analysis1](ScreenShot/analysis1.png)
### Company Details 2
<img src="ScreenShot/Company2.png" alt="Company2" width="300"/>

![Analysis2](ScreenShot/analysis2.png)
### Analysis Screen 1
<img src="ScreenShot/analysis1.png" alt="Analysis1" width="300"/>

![Analysis3](ScreenShot/analysis3.png)
### Analysis Screen 2
<img src="ScreenShot/analysis2.png" alt="Analysis2" width="300"/>

![Login1](ScreenShot/login1.png)
### Analysis Screen 3
<img src="ScreenShot/analysis3.png" alt="Analysis3" width="300"/>
### Login Screen 1
<img src="ScreenShot/login1.png" alt="Login1" width="300"/>
### Login Screen 2
<img src="ScreenShot/login2.png" alt="Login2" width="300"/>

![Login2](ScreenShot/login2.png)

---

## 🚀 **How It Works**

1. **Stock Data Fetching**:  
   - Real-time stock data is fetched using **Alpha Vantage** API.
   - Data is parsed with **OpenCSV** and stored locally using **Room**.

2. **AI Insights Generation**:  
   - Trading data is analyzed for patterns (active times, most traded stocks).
   - Summaries and trading suggestions are generated using **GPT-3.5**.

3. **User Authentication**:  
   - Firebase Authentication ensures secure login and user data management.

---

## 📋 **Upcoming Features**

### ✅ **Weekly AI Summaries**
- Generate automated weekly reports using AI.  
- Users will receive notifications and access reports from their profile.

### ✅ **Robinhood Integration**
- Integrate real **Robinhood API** to fetch user portfolios.  
- Analyze real-time portfolio performance.

### ✅ **Enhanced Risk Management**
- Statistical risk analysis with visual insights.  
- Introduce tools for **asset allocation analysis** and **risk warnings**.

---

## 🔑 **Setup Instructions**

1. Clone this repository:
   ```bash
   git clone https://github.com/your-repo-url
   cd stock-market-app
   ```

2. Add your API keys in the `local.properties` file:
   ```properties
   OPENAI_API_KEY=your_openai_api_key
   ALPHA_VANTAGE_API_KEY=your_alpha_vantage_key
   ```

3. Build and run the app:
   - Ensure **Android Studio** (Arctic Fox or newer) is installed.
   - Open the project and sync dependencies.

4. Configure Firebase Authentication:
   - Follow the [Firebase setup guide](https://firebase.google.com/docs/auth) and connect your Firebase project.

---

## ⚙️ **Dependencies**

Add these dependencies in your `build.gradle`:

```gradle
dependencies {
    implementation "androidx.compose.ui:ui:1.5.0"
    implementation "com.google.dagger:hilt-android:2.44"
    implementation "com.squareup.retrofit2:retrofit:2.9.0"
    implementation "com.google.firebase:firebase-auth:21.0.1"
    implementation "io.github.ehsannarmani:composecharts:1.0.0"
    implementation "com.opencsv:opencsv:5.5.2"
}
```

---

## 🧪 **Testing**

- Unit testing for data parsing and AI analysis.
- UI testing for Compose components using **Compose Test Rules**.

---

## 👨‍💻 **Contributors**

- **Your Team Name**  
  - Developers: Jiahao, Jiahe (Herbert), Charlie  
  - Designers: Jiahe, Jiahao

---

## 📜 **License**

This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for details.

---

## 📩 **Contact**

For inquiries, feature requests, or collaborations:  
**Email**: hzjh@bu.edu 
**Website**: In progress

---
