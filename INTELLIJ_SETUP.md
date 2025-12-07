# Running in IntelliJ IDEA

## Prerequisites

- IntelliJ IDEA (Community or Ultimate edition)
- Java JDK 17 or higher
- Maven (already installed as you mentioned)

## Setup Steps

### 1. Open the Project

1. **Open IntelliJ IDEA**
2. **File → Open** (or **Open** from welcome screen)
3. **Navigate to and select the `hotel-reservation` folder**
   - **Important:** Select the `hotel-reservation` folder, NOT the parent directory
   - Path should be: `.../Hotel Reservation System/hotel-reservation`
4. Click **OK**

### 2. Wait for Maven Import

- IntelliJ will automatically detect the `pom.xml` file
- A notification will appear: **"Maven projects need to be imported"**
- Click **"Import Maven Project"** or **"Enable Auto-Import"**
- Wait for dependencies to download (this may take a few minutes)

### 3. Configure JDK

1. **File → Project Structure** (or press `Ctrl+Alt+Shift+S` / `Cmd+;` on Mac)
2. Go to **Project** tab
3. Set **Project SDK** to **Java 17** or higher
4. Set **Project language level** to match your JDK version
5. Click **OK**

### 4. Set Main Class

1. **File → Project Structure** → **Project Settings** → **Artifacts**
2. Or go to **Run → Edit Configurations**
3. Click **+** → **Application**
4. Configure:
   - **Name:** Hotel Reservation System
   - **Main class:** `com.hotelapp.MainApp`
   - **Use classpath of module:** `hotel-reservation`
   - **JRE:** Project SDK (Java 17+)

### 5. Run the Application

**Option A: Using Run Configuration**
1. **Run → Run 'Hotel Reservation System'** (or press `Shift+F10`)
2. Or click the green ▶️ button in the toolbar

**Option B: Quick Run**
1. Open `src/main/java/com/hotelapp/MainApp.java`
2. Right-click on the file → **Run 'MainApp.main()'**
3. Or click the green ▶️ icon next to the `main` method

**Option C: Using Maven**
1. Open **Maven** tool window (View → Tool Windows → Maven)
2. Expand **hotel-reservation** → **Plugins** → **javafx**
3. Double-click **javafx:run**

## Troubleshooting

### "Cannot resolve symbol" errors

1. **File → Invalidate Caches / Restart**
2. Select **Invalidate and Restart**
3. Wait for re-indexing

### Maven dependencies not downloading

1. **File → Settings** (or `Ctrl+Alt+S` / `Cmd+,` on Mac)
2. **Build, Execution, Deployment** → **Build Tools** → **Maven**
3. Check **Maven home directory** is correct
4. Click **Apply** → **OK**
5. Right-click on `pom.xml` → **Maven** → **Reload Project**

### JavaFX not found

1. Make sure **JavaFX** dependencies are in `pom.xml` (they should be)
2. **File → Project Structure** → **Libraries**
3. Check that JavaFX libraries are listed
4. If not, reload Maven project

### Application won't start

1. Check **Run** → **Edit Configurations**
2. Make sure **Main class** is: `com.hotelapp.MainApp`
3. Make sure **Working directory** is: `$MODULE_DIR$`
4. Check **Console** tab for error messages

### Database errors

- The database will be created automatically on first run
- Location: `database/hotel.db` (relative to project root)
- If you see database errors, delete `database/hotel.db` and restart

## Default Login

- **Username:** `admin`
- **Password:** `admin`

## Project Structure in IntelliJ

```
hotel-reservation/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/hotelapp/
│       │       ├── MainApp.java          ← Main entry point
│       │       ├── controllers/
│       │       ├── models/
│       │       ├── dao/
│       │       ├── services/
│       │       └── utils/
│       └── resources/
│           ├── fxml/                     ← UI layouts
│           └── styles/
├── database/
│   ├── schema.sql
│   └── hotel.db                          ← Created on first run
└── pom.xml                               ← Maven configuration
```

## Tips

- **Enable Auto-Import:** IntelliJ will automatically import Maven changes
- **Code Completion:** IntelliJ will provide full code completion for JavaFX
- **Debug Mode:** Set breakpoints and use **Debug** instead of **Run** (Shift+F9)
- **Maven Tool Window:** Use it to run Maven goals without terminal


