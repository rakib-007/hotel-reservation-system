# Hotel Reservation System - Quick Reference Guide

## How Each Feature Works

### 1. Login System

**Flow:**
```
User Input → LoginController.handleLogin() 
→ SQL Query: SELECT * FROM users WHERE username=? AND password=?
→ If Match → Load Dashboard FXML
→ If No Match → Show Error Alert
```

**Key Code:**
```java
String sql = "SELECT id, role FROM users WHERE username = ? AND password = ?";
PreparedStatement ps = c.prepareStatement(sql);
ps.setString(1, username);
ps.setString(2, password);
ResultSet rs = ps.executeQuery();
if (rs.next()) {
    // Login successful - navigate to dashboard
}
```

---

### 2. Button Click & Navigation

**How It Works:**
1. **FXML declares button**: `<Button onAction="#methodName"/>`
2. **Controller has method**: `@FXML public void methodName()`
3. **Method loads FXML**: `FXMLLoader.load(getClass().getResource("/fxml/page.fxml"))`
4. **Replace Scene**: `stage.setScene(new Scene(root))`
5. **New controller's `initialize()` runs automatically**

**Example:**
```java
@FXML
public void openDashboard() {
    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
    Parent root = loader.load();  // Creates controller, injects @FXML fields, calls initialize()
    Stage stage = (Stage) currentComponent.getScene().getWindow();
    stage.setScene(new Scene(root));  // Replaces entire UI
}
```

---

### 3. Search Functionality

#### A. In-Memory Search (GuestController)

**How It Works:**
1. All data loaded into `ObservableList<CustomerRow> allGuests`
2. User types in search field
3. Java Stream filters the list:
   ```java
   allGuests.stream()
       .filter(row -> 
           row.getName().toLowerCase().contains(searchText) ||
           row.getPhone().contains(searchText) ||
           row.getNidPassport().toLowerCase().contains(searchText)
       )
       .collect(Collectors.toList())
   ```
4. Filtered list set to TableView: `tblGuests.setItems(filteredList)`

**Advantage**: Fast, no database query  
**Use Case**: Small datasets, frequently searched

#### B. Database Search (DashboardController)

**How It Works:**
1. User enters phone or NID
2. SQL query executed:
   ```java
   SELECT * FROM customers WHERE phone = ? OR nid_passport = ?
   ```
3. Results displayed in UI

**Advantage**: Works with large datasets  
**Use Case**: Exact match search

---

### 4. Database Operations

#### Transaction Example

```java
Connection conn = DBUtil.getConnection();
conn.setAutoCommit(false);  // Start transaction

try {
    // Multiple operations
    customerDAO.findOrCreate(customer, conn);  // Same connection
    reservationDAO.insertReservation(..., conn);  // Same connection
    roomDAO.updateStatus(..., conn);  // Same connection
    
    conn.commit();  // All succeed - save changes
} catch (Exception e) {
    conn.rollback();  // Error - undo all changes
    throw e;
} finally {
    conn.setAutoCommit(true);  // Restore auto-commit
}
```

**Why Transactions?**
- Ensures data consistency
- All operations succeed or all fail (atomicity)
- Prevents partial updates

---

### 5. TableView Data Display

**How It Works:**
1. Create `ObservableList`: `FXCollections.observableArrayList()`
2. Set to TableView: `tableView.setItems(list)`
3. Configure columns:
   ```java
   colName.setCellValueFactory(cell -> 
       new SimpleStringProperty(cell.getValue().getName())
   );
   ```
4. TableView automatically displays data
5. When list changes, TableView updates automatically

**Example:**
```java
ObservableList<Reservation> reservations = FXCollections.observableArrayList();
tblReservations.setItems(reservations);

colGuest.setCellValueFactory(cell -> 
    new SimpleObjectProperty<>(cell.getValue().getCustomerName())
);
```

---

### 6. Reservation Booking Flow

```
1. User selects room → Only FREE rooms shown in ComboBox
2. User selects dates → Price calculated automatically
3. User enters customer info → Validated
4. Click "Book" → ReservationService.bookReservation()

Inside bookReservation():
  a. Start Transaction
  b. Find or Create Customer
  c. Check Room Availability (no overlapping CONFIRMED reservations)
  d. Insert Reservation (status = "CONFIRMED")
  e. Update Room Status to "BOOKED"
  f. Commit Transaction

5. Show Success Alert
```

**Key Validation:**
- Check-in date < Check-out date
- Room not already booked for those dates
- Customer fields not empty

---

### 7. Check-In / Check-Out

#### Check-In:
```
User clicks "Check In" 
→ Validate: Reservation status = "CONFIRMED"
→ Transaction:
   - Update reservation status to "CHECKED_IN"
   - Update room status to "OCCUPIED"
→ Commit
```

#### Check-Out:
```
User clicks "Check Out"
→ Validate: Reservation status = "CHECKED_IN"
→ Transaction:
   - Update reservation status to "COMPLETED"
   - Update room status to "FREE"
→ Commit
```

---

### 8. Room Status Lifecycle

```
FREE → BOOKED (when reservation created)
  ↓
OCCUPIED (when checked in)
  ↓
FREE (when checked out)

Or:

FREE → MAINTENANCE → FREE (manual update)
```

---

### 9. Reservation Status Lifecycle

```
CONFIRMED (after booking)
  ↓
CHECKED_IN (after check-in)
  ↓
COMPLETED (after check-out)

Or:

CONFIRMED → CANCELLED (if cancelled)
```

---

### 10. FXML Injection

**How @FXML Works:**
1. FXML file declares: `<TextField fx:id="tfName"/>`
2. Controller has field: `@FXML private TextField tfName;`
3. When FXML loads, FXMLLoader automatically:
   - Creates TextField instance
   - Injects it into controller's `tfName` field
4. Controller's `initialize()` method runs
5. Controller can use `tfName` immediately

**Example:**
```xml
<!-- FXML -->
<TextField fx:id="txtUsername"/>
<Button onAction="#handleLogin"/>
```

```java
// Controller
@FXML private TextField txtUsername;

public void initialize() {
    // txtUsername is already initialized here!
    txtUsername.setText("Default");
}

@FXML
public void handleLogin() {
    String username = txtUsername.getText();  // Works!
}
```

---

### 11. Automatic Price Calculation

**How It Works:**
1. User selects room → `cmbRoom.setOnAction(e -> updatePrice())`
2. User changes dates → `dpCheckIn.valueProperty().addListener(...)`
3. Calculation triggered:
   ```java
   long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
   double total = room.getPrice() * nights;
   ```
4. Label updated: `lblTotal.setText("Tk " + total)`

**JavaFX Property Listeners:**
- Automatically triggered when value changes
- Enables reactive UI updates

---

### 12. Guest Search (Dashboard)

**SQL Query:**
```sql
SELECT id, name, phone, 
       COALESCE(address, '') as address,
       COALESCE(nid_passport, '') as nid_passport
FROM customers 
WHERE phone = ? OR nid_passport = ?
```

**COALESCE Purpose:**
- Handles NULL values gracefully
- Ensures backward compatibility if columns added later

---

### 13. Database Initialization

**Flow:**
```
MainApp.start()
  ↓
DBInit.initDatabase()
  ↓
1. Create database folder if not exists
2. Execute schema.sql (create tables)
3. Run migrations (add columns if missing)
4. Ensure admin user exists
5. Seed sample rooms (if database empty)
```

**Migration Logic:**
- Checks if columns exist before adding
- Prevents errors on re-initialization

---

### 14. Error Handling

**Pattern Used:**
```java
try {
    // Database operation
    List<Room> rooms = roomDAO.getAllRooms();
    // Update UI
} catch (SQLException e) {
    e.printStackTrace();
    showError("Failed to load rooms: " + e.getMessage());
}
```

**User-Friendly Messages:**
- Technical errors logged to console
- User sees simple, understandable messages

---

## File Structure

```
src/main/java/com/hotelapp/
├── MainApp.java                    # Application entry point
├── models/                         # Domain objects
│   ├── Customer.java
│   ├── Room.java
│   └── Reservation.java
├── dao/                            # Data Access Objects
│   ├── CustomerDAO.java
│   ├── RoomDAO.java
│   └── ReservationDAO.java
├── services/                       # Business logic
│   └── ReservationService.java
├── controllers/                    # UI Controllers
│   ├── LoginController.java
│   ├── DashboardController.java
│   ├── ReservationController.java
│   ├── GuestController.java
│   ├── RoomController.java
│   ├── CheckInOutController.java
│   └── ReservationDetailController.java
└── utils/                          # Utilities
    ├── DBUtil.java                 # Connection factory
    ├── DBInit.java                 # Database initialization
    └── DBManager.java              # Database utilities

src/main/resources/
└── fxml/                           # UI Layouts
    ├── login.fxml
    ├── dashboard.fxml
    ├── reserve.fxml
    ├── guests.fxml
    ├── rooms.fxml
    ├── checkinout.fxml
    └── reservation_detail.fxml

database/
├── schema.sql                      # Database schema
└── hotel.db                        # SQLite database file
```

---

## Database Queries Reference

### Find Overlapping Reservations
```sql
SELECT * FROM reservations
WHERE NOT (checkout <= ? OR checkin >= ?)
  AND status = 'CONFIRMED'
  AND room_id = ?
```

**Logic**: Two date ranges overlap if NOT (one ends before other starts OR one starts after other ends)

### Get Free Rooms
```sql
SELECT * FROM rooms WHERE status = 'FREE'
```

### Get Customer's Reservations
```sql
SELECT r.*, c.name, rm.room_number
FROM reservations r
LEFT JOIN customers c ON r.customer_id = c.id
LEFT JOIN rooms rm ON r.room_id = rm.id
WHERE r.customer_id = ?
```

### Get Today's Check-Ins
```sql
SELECT * FROM reservations
WHERE checkin = CURRENT_DATE
  AND status = 'CONFIRMED'
```

---

## Key Classes Quick Reference

| Class | Purpose | Key Methods |
|-------|---------|-------------|
| **CustomerDAO** | Customer database operations | `createCustomer()`, `findOrCreate()`, `getAllCustomers()` |
| **RoomDAO** | Room database operations | `getAllRooms()`, `getFreeRooms()`, `updateStatus()` |
| **ReservationDAO** | Reservation database operations | `insertReservation()`, `findReservationsBetween()`, `getAllReservations()` |
| **ReservationService** | Business logic | `bookReservation()`, `checkIn()`, `checkOut()`, `cancelReservation()` |
| **DBUtil** | Database connection | `getConnection()` |
| **DBInit** | Database setup | `initDatabase()`, `getJdbcUrl()` |

---

## Common Patterns

### Pattern 1: Load Data and Display
```java
public void loadData() {
    try {
        List<Item> items = dao.getAllItems();
        ObservableList<Item> observableList = FXCollections.observableArrayList(items);
        tableView.setItems(observableList);
    } catch (SQLException e) {
        showError("Failed to load: " + e.getMessage());
    }
}
```

### Pattern 2: Form Submission
```java
@FXML
public void handleSubmit() {
    // 1. Validate
    if (field.getText().isEmpty()) {
        showError("Field required");
        return;
    }
    
    // 2. Process
    try {
        service.processData(...);
        showSuccess("Success!");
        clearForm();
    } catch (Exception e) {
        showError("Error: " + e.getMessage());
    }
}
```

### Pattern 3: Search/Filter
```java
@FXML
public void handleSearch() {
    String searchText = tfSearch.getText().trim().toLowerCase();
    if (searchText.isEmpty()) {
        tableView.setItems(allItems);
        return;
    }
    
    List<Item> filtered = allItems.stream()
        .filter(item -> item.getName().toLowerCase().contains(searchText))
        .collect(Collectors.toList());
    
    tableView.setItems(FXCollections.observableArrayList(filtered));
}
```

---

## Tips & Best Practices

1. **Always use PreparedStatement** - Prevents SQL injection
2. **Use transactions** - For operations affecting multiple tables
3. **Handle exceptions** - Show user-friendly messages
4. **Validate input** - Check before database operations
5. **Use ObservableList** - For TableView automatic updates
6. **Close resources** - Use try-with-resources for Connections
7. **Trim user input** - Remove whitespace from text fields
8. **Use @FXML annotation** - Required for FXML injection
9. **Initialize in initialize()** - Set up UI components here
10. **Separate concerns** - Controllers handle UI, Services handle logic, DAOs handle data

---

This quick reference covers the essential concepts. For detailed explanations, see PROJECT_OVERVIEW.md

