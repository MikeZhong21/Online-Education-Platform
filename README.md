# Online Education Platform

## Project Description

It is an online education platform that focuses on selling and offering various courses. It provides rich learning support features and interactive functionalities to enhance the learning atmosphere and increase user engagement.

## Technology Stack

- **Backend**: SpringBoot, SpringCloud, MyBatis, MyBatisPlus, MySQL, Redis, Redisson, Caffeine, RabbitMQ, XXL-JOB
- **Web Server**: Nginx

## Key Implementations

- Developed a distributed lock component based on Redisson to realize the following functions with zero code intrusion via annotations:
  - Lock type switching
  - Locking strategy switching
  - Dynamic lock names using SPEL
  - Rate limiting

- Designed a high-performance video playback progress recording system that achieves second-level accuracy in video playback tracking without increasing the pressure on the database.

- Conducted a systematic reconstruction of the like feature within the evaluation system:
  - Redesigned the data structure for likes
  - Decoupling it from business logic to create a general-purpose liking system.

- Implemented like record caching and count using Redis, and performed data persistence through scheduled tasks, greatly enhancing the concurrency capability of the liking system while reducing database load.

- Implemented point statistics for various user learning and interaction behaviors, such as sign-in points, learning points, and Q&A points

- Aggregated monthly points to form seasonal leaderboards. Achieved real-time updates for the current season leaderboard and persistent storage for historical season results.

- Involved in the design and development of the coupon system. The coupon system supports redeeming coupons based on exchange codes.
  - Designed a highly efficient exchange code generation algorithm capable of supporting 2 billion entries and functioning independently of the database for validation
  - Optimized the concurrency safety and performance of the coupon claiming feature
  - Designed and implemented a coupon stacking recommendation algorithm that suggests the best combination of coupons based on the items in the user's shopping cart
