# Business Expense Tracker

A comprehensive Spring Boot application for tracking business expenses, inventory, sales, and overhead costs with visual dashboards.

## Features

- ✅ **Product Management** - Add, edit, and track products with names, prices, and stock levels
- ✅ **Inventory Expenses** - Track inventory purchases with product selection, quantity, and unit costs
- ✅ **Overhead Costs** - Add lumpsum overhead expenses with depreciation over time periods
- ✅ **Running Costs** - Distribute additional costs across units for accurate per-unit cost calculation
- ✅ **Sales Tracking** - Record daily sales with product selection and automatic total calculation
- ✅ **Dashboard** - Visual charts using Chart.js showing:
  - Gross Profit (Sales - Direct Costs)
  - Net Profit (Gross Profit - Overhead Depreciation)
  - Monthly sales vs expenses trends
  - Expenses by category breakdown
  - Top selling products
- ✅ **JWT Authentication** - Secure API access with JWT tokens
- ✅ **Cookie-based UI Authentication** - Seamless user experience
- ✅ **Single Admin User** - Simple login system (no registration needed)

## Tech Stack

- **Backend**: Spring Boot 3.2.0+, Spring Security, Spring Data JPA
- **Frontend**: Thymeleaf, Bootstrap 5, Chart.js
- **Database**: PostgreSQL (Neon DB)
- **Authentication**: JWT + Cookies
- **Deployment**: Render (free tier)

## Business Logic

### Expense Categories

1. **INVENTORY** - Direct product purchases
2. **OVERHEAD** - Fixed costs spread over time
3. **RUNNING_COSTS** - Variable costs distributed across units

### Profit Calculations

- **Gross Profit** = Sales - (Inventory + Running Costs)
- **Net Profit** = Gross Profit - Overhead Depreciation

## Setup Instructions

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- PostgreSQL database (Neon DB recommended)

### 1. Create Neon Database (Free)

1. Visit [neon.tech](https://neon.tech) and sign up
2. Create a new project
3. Note down the connection details:
   - Host
   - Database name
   - Username
   - Password
4. Your connection string will look like:
   ```
   jdbc:postgresql://[HOST]/[DATABASE]?sslmode=require
   ```

### 2. Configure Application

Update `src/main/resources/application.properties` or set environment variables:

```properties
SPRING_DATASOURCE_URL=jdbc:postgresql://your-neon-host/your-database?sslmode=require
SPRING_DATASOURCE_USERNAME=your-username
SPRING_DATASOURCE_PASSWORD=your-password
JWT_SECRET=your-base64-encoded-secret-key-here
ADMIN_USERNAME=admin
ADMIN_PASSWORD=admin123
```

### 3. Build and Run Locally

```bash
cd "Business Expense Tracker"
mvn clean package
mvn spring-boot:run
```

The application will be available at `http://localhost:8080`

#### Main Class Location

The main class is located at:  
`src/main/java/com/business/expensetracker/BusinessExpenseTrackerApplication.java`

You can run it directly from your IDE or with:

```bash
java -jar target/expense-tracker-1.0.0.jar
```

### 4. Default Login

- **Username**: admin
- **Password**: admin123

**Important:** Change the default password after first login by updating the `ADMIN_PASSWORD` environment variable and restarting the application.

## Deploying to Render (Free)

1. Push code to GitHub
2. Create a new Web Service on [render.com](https://render.com)
3. Connect your GitHub repository
4. Set build/start commands:
   - Build: `mvn clean package`
   - Start: `java -jar target/expense-tracker-1.0.0.jar`
5. Add environment variables as above
6. Deploy

## Usage Guide

- **Products**: Add/edit products with name, price, and stock
- **Expenses**: Add inventory, overhead, or running costs
- **Sales**: Record sales with product, quantity, and price
- **Dashboard**: View financial summaries and charts

## API Endpoints

- Products: `/api/products`
- Expenses: `/api/expenses`
- Sales: `/api/sales`
- Dashboard: `/api/dashboard/*`

## Security

- All API endpoints (except login) require JWT authentication
- JWT tokens are stored in HTTP-only cookies for UI access
- Passwords are hashed using BCrypt
- CSRF protection is disabled (stateless JWT authentication)
- Use HTTPS in production

## Future Enhancements

- [ ] PDF reports generation
- [ ] Email notifications for low stock
- [ ] Multi-user support with roles
- [ ] Mobile app
- [ ] Invoice generation
- [ ] Tax calculations
- [ ] Profit forecasting

## Support

For issues or questions, please create an issue in the GitHub repository.

## License

This project was created as a personal passion project. Feel free to modify and use it as needed.