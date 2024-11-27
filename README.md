# Family Cloud Storage Management System

## Project Overview

This project focuses on developing a family-oriented cloud storage management system. Unlike traditional cloud services designed for individual use, this system offers a cohesive solution for managing family files. It centralizes the storage of family photos, videos, and documents, enhancing the sharing experience. Additionally, it integrates with NAS systems for secure data retention at home.

### Key Features

1. **Family Shared Space**: Create a dedicated space for family members to easily share files, organize them into folders, set access permissions, and provide a user-friendly interface for management.

2. **Family Calendar Integration**: Synchronize family members' calendars with the cloud system, supporting color-coded events, reminders, and recurring tasks.

3. **Family Task Management**: Enable creation, assignment, and tracking of tasks within the system, with options for setting priorities, deadlines, and attaching notes or files.

4. **Family Photo Album**: Centralized photo management, allowing organization into albums, tagging, descriptions, and sharing with specific members. Includes editing and filtering options.

5. **Family Media Library**: Manage media resources, support various formats, create playlists, and stream content across devices. Includes metadata management and recommendations.

6. **Family Security Settings**: Advanced security features like two-factor authentication, data encryption, and activity logs, with fine-grained access control for different members.

7. **File Operations**: Create folders, move files, advanced search with filters, drag-and-drop uploads, and batch operations for file management.

8. **File Recycle Bin**: Store recently deleted files with recovery options.

9. **File Online Preview**: Preview files directly within the system.

## Implementation Plan

### Frontend

- **Framework**: Developed using the Vue.js framework with a component-based architecture.
- **Components**:
  - **Page Container**: Manages layout with header, footer, and main content.
  - **Login & Registration**: Implemented with Vue Components and Vue Router for seamless navigation.
  - **Home & Main Interface**: Uses Vuex for state management, showing user info and entry points.
  - **File Management**: Handles file operations with Axios for upload/download requests.
  - **Recycle Bin**: Manages deleted files with Vuex for data synchronization.

### Interaction Layers

- **User Interaction**: Initiated via browser, loading SPA main modules.
- **Component Utilization**: Utilizes third-party libraries and plugins, such as ElementUI and Vuex.
- **Service Layer**: Handles data filtering and processing, interacting with network requests via Axios.

### Backend (Spring Boot)

- **Application Layer**: Entry point for HTTP requests, forwarding to Nginx.
- **Forwarding Layer (Nginx)**: Manages request forwarding, load balancing, and logging.
- **Interface & Business Layers**: Divided into user, file, management, and callback services.
- **Infrastructure**: Utilizes Redis, RabbitMQ, Elasticsearch, and OSS for foundational support.

## Feasibility Study

- **Social Demand**: Increasing need for digital data management and sharing within families.
- **Privacy & Security**: Growing awareness drives demand for secure storage solutions.
- **Customization**: Families desire personalized data management options.
- **Backup Awareness**: Emphasis on reliable data backup and recovery drives system adoption.

## Technical Foundation

- **Frontend**: Utilizes Vue.js, Element UI, and Node.js for a modular and scalable interface.
- **Backend**: Employs Spring Boot, MyBatis-Plus, and sa-token for robust backend services.
- **Storage**: Integrates MySQL, Redis, Alibaba Cloud OSS, and MinIO for comprehensive data management.

This project is designed to meet the evolving needs of modern families, providing a secure and integrated environment for digital life.
