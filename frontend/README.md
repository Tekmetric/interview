# Visual Vehicle Inspection Tool

A React application for vehicle inspection and image annotation. Reviewers can use this to test the complete workflow from vehicle selection to annotation creation.

Since the main focus on the app is on annotations, vehicle list provides fixed number of vehicle items, but on the listing view it is augumented with extra data created (images, annotations).

This can be expanded in the future by assigining the reports to Repair Orders, e.g. identifying parts that need service or replacement. From there it can be connected to the Parts API, etc. It can also be exported to generate reports such as https://www.ecgassociation.eu/wp-content/uploads/2025/06/Inspection-Guidelines-v4.1.pdf (page 34).

## Quick Start

### Setup
```bash
cd frontend
nvm use
npm install
npm start
```

##  How to Use the Application

### 1. Home Page - Vehicle List
- View table list of 10 sample vehicles
- Click on Quick look or a table row to expand table row with preview of uploaded images and number of annotations per category.
- Click any vehicle to see details and images
- Each vehicle shows make, model, year, VIN, and license plate

### 2. Vehicle Detail Page
- Browse and annotate all images for the selected vehicle
- **File Upload**: Click "Choose File" to add new images
- **Edit Mode Toggle**: Switch between view and annotation modes

### 3. Image Annotation System
- **Create**: Click anywhere on an image to add an annotation box (make sure you're in edit mode)
- **Resize**: Drag the corner handles to adjust size
- **Move**: Click and drag the annotation to reposition
- **Label**: Click the text label to edit annotation description
- **Categorize**: Choose color (Red, Yellow, Green) for priority
- **Delete**: Click the × button to remove annotations

### 4. Data Persistence
- Annotations automatically save to database
- Each image maintains its own annotation table
- Data persists between page refreshes

## How to Test the Application

### Functional Testing
1. Vehicle Navigation
   - Click different vehicles on home page
   - Verify images load correctly
   - Test back navigation

2. File Upload
   - Go to any vehicle page
   - Upload a new image file
   - Verify image appears in gallery

3. Annotation Creation
   - Enable edit mode on any image
   - Create multiple annotations
   - Test resize, move, and delete
   - Verify labels can be edited
   - Test category color changes

4. Data Persistence
   - Create annotations on an image
   - Refresh the page
   - Verify annotations still exist
   - Test on different images / devices

### Technical Testing
```bash
npm test

# Run specific component tests
npm test -- AnnotationOverlay.test.js
npm test -- VehiclePage.test.js
npm test -- Home.test.js
```

## Backend Requirements

Backend runs at `https://marioloncaric.com/tekmetric-api/` endpoint.
It is a small PHP/MySQL application with:
- MySQL database with sample vehicle data
- CORS enabled for local development

### Endpoints
- `/` `POST` (image upload)
- `/?get_vehicles=1` `GET` (get array of vehicles)
- `/?get_vehicle={vehicle_id}` `GET` (get vehicle data with images)
- `/annotations.php?image_id={image_id}` `GET` (get array of annotations for image)
- `/annotations.php?image_id={image_id}` `POST` (update annotations)
- `/annotations.php?image_id={image_id}` `DELETE` (delete annotations)


##  Key Features to Test

### Annotation System
- Click-to-create annotations boxes
- Drag handles for resizing
- Click-and-drag for moving
- Inline label editing
- Color-coded categories
- Touch support for mobile

### User Experience
- Responsive design
- Intuitive navigation
- Real-time data sync
- Error handling
- Loading states

### Data Management
- Vehicle list with images
- File upload functionality
- Annotation persistence
- Per-image annotation tables
