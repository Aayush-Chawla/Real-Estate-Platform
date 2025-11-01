# Real Estate Platform

A microservices-based real estate platform built with Spring Boot, enabling property listings, appointments, messaging, reviews, and content management.

## 👥 Team Members

- **Aryan Srivastava** - A073
- **Aryan Walia** - A078  
- **Aayush Chawla** - A008
- **Priya Pandey** - A048

## Architecture Overview

The platform consists of 6 core microservices with API Gateway and service discovery:

- **Auth Service** - User authentication and authorization
- **Property Service** - Property listings and management
- **Appointment Service** - Viewing appointments between buyers and sellers
- **Messaging Service** - 1:1 conversations per property
- **Review Service** - Property and seller ratings
- **Content Service** - Posts and comments system
- **API Gateway** - Request routing and security
- **Eureka Server** - Service discovery

## Core Features

### Authentication & Authorization
- User registration and login
- Role-based access control (BUYER, SELLER, ADMIN)
- JWT token-based authentication
- Session management
- User profile management

### Property Management
- Property listing creation and editing
- Image upload and management
- Amenity management (many-to-many)
- Structured address storage with geolocation
- Property search and filtering
- Availability status tracking

### Appointment System
- Schedule property viewings
- Appointment status management (PENDING, CONFIRMED, RESCHEDULED, CANCELLED, COMPLETED)
- Buyer-seller coordination
- Appointment history tracking

### Messaging System
- 1:1 conversations between buyer and seller per property
- File attachment support
- Message delivery tracking
- Conversation history

### Review & Rating System
- Unified reviews for properties and sellers
- 5-star rating system
- Review text with character limits
- One review per user per target

### Content Management
- Blog posts and announcements
- Comment system with optional threading
- Post visibility controls (PUBLIC, UNLISTED, PRIVATE)
- Content moderation support

## Technical Stack

- **Backend**: Spring Boot, Spring Cloud
- **Database**: MySQL/PostgreSQL per service
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Authentication**: JWT tokens
- **Caching**: Redis
- **Search**: Elasticsearch/OpenSearch (recommended)
- **File Storage**: AWS S3/Google Cloud Storage
- **Message Queue**: Apache Kafka/RabbitMQ (for events)

## Database Schema

### Auth Service

#### users
```sql
id              UUID PRIMARY KEY
name            VARCHAR(120) NOT NULL
email           VARCHAR(160) NOT NULL UNIQUE
phone           VARCHAR(24) UNIQUE
password_hash   VARCHAR(255) NOT NULL
image_url       VARCHAR(512)
role            ENUM('BUYER','SELLER','ADMIN') NOT NULL
status          ENUM('ACTIVE','SUSPENDED','DEACTIVATED') DEFAULT 'ACTIVE'
created_at      TIMESTAMP
updated_at      TIMESTAMP
deleted_at      TIMESTAMP NULL
version         INT

Indexes: email_idx(email), role_idx(role)
```

#### sessions
```sql
id          UUID PRIMARY KEY
user_id     UUID FOREIGN KEY → users(id)
token       VARCHAR(512) UNIQUE
created_at  TIMESTAMP
expires_at  TIMESTAMP

Indexes: user_id_idx(user_id), expires_at_idx(expires_at)
```

### Property Service

#### properties
```sql
id                  UUID PRIMARY KEY
seller_id           UUID NOT NULL
title               VARCHAR(160) NOT NULL
description         TEXT
type_of_property    ENUM('APARTMENT','HOUSE','VILLA','PLOT','STUDIO') NOT NULL
buy_or_rental       ENUM('BUY','RENT') NOT NULL
price               DECIMAL(14,2) NOT NULL
area_sqft           INT
rooms               INT
furnishing_status   ENUM('UNFURNISHED','SEMI_FURNISHED','FURNISHED')
availability_status ENUM('AVAILABLE','BOOKED','UNAVAILABLE') DEFAULT 'AVAILABLE'
floor_number        INT
total_floors        INT
address_line1       VARCHAR(255)
address_line2       VARCHAR(255)
city                VARCHAR(100)
state               VARCHAR(100)
zipcode             VARCHAR(20)
country             VARCHAR(100)
latitude            DECIMAL(10,7)
longitude           DECIMAL(10,7)
created_at          TIMESTAMP
updated_at          TIMESTAMP
deleted_at          TIMESTAMP NULL
version             INT

Indexes: seller_idx(seller_id), city_idx(city), price_idx(price), geo_idx(latitude,longitude)
```

#### amenities
```sql
id          UUID PRIMARY KEY
code        VARCHAR(64) UNIQUE NOT NULL
label       VARCHAR(120)
created_at  TIMESTAMP
updated_at  TIMESTAMP
```

#### property_amenities
```sql
id           UUID PRIMARY KEY
property_id  UUID FOREIGN KEY → properties(id)
amenity_id   UUID FOREIGN KEY → amenities(id)

UNIQUE(property_id, amenity_id)
Indexes: property_idx(property_id), amenity_idx(amenity_id)
```

#### property_images
```sql
id           UUID PRIMARY KEY
property_id  UUID FOREIGN KEY → properties(id)
url          VARCHAR(512) NOT NULL
sort_order   INT
created_at   TIMESTAMP
updated_at   TIMESTAMP

Indexes: property_idx(property_id)
```

### Appointment Service

#### appointments
```sql
id            UUID PRIMARY KEY
buyer_id      UUID NOT NULL
seller_id     UUID NOT NULL
property_id   UUID NOT NULL
scheduled_at  DATETIME NOT NULL
status        ENUM('PENDING','CONFIRMED','RESCHEDULED','CANCELLED','COMPLETED') DEFAULT 'PENDING'
notes         VARCHAR(1000)
created_at    TIMESTAMP
updated_at    TIMESTAMP
canceled_at   TIMESTAMP NULL

Indexes: buyer_idx(buyer_id), seller_idx(seller_id), property_idx(property_id), status_idx(status)
```

### Messaging Service

#### conversations
```sql
id               UUID PRIMARY KEY
buyer_id         UUID NOT NULL
seller_id        UUID NOT NULL
property_id      UUID NOT NULL
created_at       TIMESTAMP
last_message_at  TIMESTAMP

UNIQUE(buyer_id, seller_id, property_id)
Indexes: buyer_idx(buyer_id), seller_idx(seller_id), property_idx(property_id)
```

#### messages
```sql
id               UUID PRIMARY KEY
conversation_id  UUID FOREIGN KEY → conversations(id)
sender_id        UUID NOT NULL
body             TEXT NOT NULL
attachment_url   VARCHAR(512)
sent_at          TIMESTAMP
delivered_at     TIMESTAMP NULL

Indexes: conversation_idx(conversation_id, sent_at)
```

### Review Service

#### reviews
```sql
id           UUID PRIMARY KEY
target_type  ENUM('PROPERTY','SELLER') NOT NULL
target_id    UUID NOT NULL
user_id      UUID NOT NULL
rating       TINYINT NOT NULL CHECK (rating >= 1 AND rating <= 5)
review_text  VARCHAR(2000)
created_at   TIMESTAMP
updated_at   TIMESTAMP

UNIQUE(target_type, target_id, user_id)
Indexes: target_idx(target_type, target_id), user_idx(user_id)
```

### Content Service

#### posts
```sql
id           UUID PRIMARY KEY
author_id    UUID NOT NULL
title        VARCHAR(255) NOT NULL
body         TEXT NOT NULL
published    BOOLEAN DEFAULT FALSE
visibility   ENUM('PUBLIC','UNLISTED','PRIVATE') DEFAULT 'PUBLIC'
created_at   TIMESTAMP
updated_at   TIMESTAMP
published_at TIMESTAMP
deleted_at   TIMESTAMP NULL
version      INT

Indexes: author_idx(author_id), published_idx(published), created_idx(created_at)
```

#### comments
```sql
id                 UUID PRIMARY KEY
post_id            UUID FOREIGN KEY → posts(id)
parent_comment_id  UUID FOREIGN KEY → comments(id) NULL
author_id          UUID NOT NULL
body               TEXT NOT NULL
created_at         TIMESTAMP
updated_at         TIMESTAMP
deleted_at         TIMESTAMP NULL

Indexes: post_idx(post_id), parent_idx(parent_comment_id), author_idx(author_id)
```

## API Endpoints Overview

### Auth Service
- `POST /auth/register` - User registration
- `POST /auth/login` - User login
- `POST /auth/logout` - User logout
- `GET /auth/profile` - Get user profile
- `PUT /auth/profile` - Update user profile

### Property Service
- `GET /properties` - List properties with filtering
- `POST /properties` - Create property listing
- `GET /properties/{id}` - Get property details
- `PUT /properties/{id}` - Update property
- `DELETE /properties/{id}` - Delete property
- `POST /properties/{id}/images` - Upload property images
- `GET /amenities` - List all amenities

### Appointment Service
- `POST /appointments` - Schedule appointment
- `GET /appointments` - List user appointments
- `GET /appointments/{id}` - Get appointment details
- `PUT /appointments/{id}` - Update appointment
- `DELETE /appointments/{id}` - Cancel appointment

### Messaging Service
- `GET /conversations` - List user conversations
- `POST /conversations` - Create/get conversation
- `GET /conversations/{id}/messages` - Get conversation messages
- `POST /conversations/{id}/messages` - Send message

### Review Service
- `POST /reviews` - Create review
- `GET /reviews` - List reviews by target
- `PUT /reviews/{id}` - Update review
- `DELETE /reviews/{id}` - Delete review

### Content Service
- `GET /posts` - List posts
- `POST /posts` - Create post
- `GET /posts/{id}` - Get post details
- `PUT /posts/{id}` - Update post
- `DELETE /posts/{id}` - Delete post
- `POST /posts/{id}/comments` - Add comment
- `GET /posts/{id}/comments` - Get post comments

## Security Features

- JWT-based authentication with role validation
- API Gateway handles token verification
- Service-level authorization checks
- Rate limiting and request throttling
- Input validation and sanitization
- Secure file upload with signed URLs
- HTTPS enforcement
- CORS configuration

## Scalability Features

- Database per service pattern
- UUID primary keys for distributed systems
- Optimistic locking with version columns
- Soft deletes for data recovery
- Cursor-based pagination
- Redis caching for performance
- Event-driven architecture for loose coupling
- Horizontal service scaling capability

## Development Setup

1. **Prerequisites**
   - Java 17+
   - Maven 3.6+
   - MySQL/PostgreSQL
   - Redis
   - Docker (optional)

2. **Environment Variables**
   ```
   DB_HOST, DB_PORT, DB_NAME, DB_USERNAME, DB_PASSWORD
   JWT_SECRET, JWT_EXPIRATION
   REDIS_HOST, REDIS_PORT
   S3_BUCKET, AWS_ACCESS_KEY, AWS_SECRET_KEY
   ```

3. **Running Services**
   ```bash
   # Start Eureka Server
   cd eureka-server && mvn spring-boot:run
   
   # Start API Gateway
   cd api-gateway && mvn spring-boot:run
   
   # Start each service
   cd auth-service && mvn spring-boot:run
   cd property-service && mvn spring-boot:run
   cd appointment-service && mvn spring-boot:run
   cd messaging-service && mvn spring-boot:run
   cd review-service && mvn spring-boot:run
   cd content-service && mvn spring-boot:run
   ```

## Production Considerations

- Use connection pooling for database connections
- Implement circuit breakers for service-to-service calls
- Set up monitoring and alerting (Prometheus, Grafana)
- Use distributed tracing (Zipkin, Jaeger)
- Implement health checks for each service
- Configure load balancing
- Set up automated backups
- Use container orchestration (Kubernetes)

## License

This project is licensed under the MIT License.
