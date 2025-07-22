# WebP Image Converter - Java Spring Boot Project

This is a personal Java Spring Boot project inspired by my internship experience. It demonstrates the logic of converting and compressing images from URLs or file uploads into the optimized WebP format using Google’s `cwebp` tool.



---

## 🔧 Features

-  Convert images from external URLs or local uploads
-  WebP compression using Google’s `cwebp` tool
-  Smart compression logic (lossless for PNG, quality-based for others)
-  Avoids reconversion if already WebP and ≤ 200KB
-  Handles image cleanup and temporary file management
-  Built entirely with **Java + Spring Boot**

---

## 📁 Project Structure

```bash
imageConverter/
├── src/
│   ├── main/
│   │   ├── java/com/rhytham/imageConverter/
│   │   │   ├── controller/ImageController.java
│   │   │   ├── model/ImageRequest.java
│   │   │   ├── service/ImageConvertService.java
│   │   │   └── util/ImageUtil.java
│   │   └── resources/
│   │       ├── static/cwebp.exe
│   │       └── application.properties
├── converted-images/   # Output folder (auto-created at runtime, not in repo)
├── pom.xml
└── README.md
```

## ⚙️ application.properties

```properties
spring.application.name=imageConverter

# Directory to save converted images
output.path=converted-images/

# Upload limits
spring.servlet.multipart.max-file-size=200MB
spring.servlet.multipart.max-request-size=200MB

# Server settings
server.port=9090

# Logging
logging.level.org.springframework=INFO
logging.level.com.rhytham=DEBUG
```

## 🚀 How to Run

### 1. Clone the repository

```bash
git clone https://github.com/Rhytham23/imageConverter.git
cd imageConverter
```

### 2. Add cwebp.exe

Download `cwebp.exe` from the [Google WebP tools](https://developers.google.com/speed/webp/download) and place it in:

```
src/main/resources/static/
```

### 3. Build and run the application

```bash
mvn clean install
mvn spring-boot:run
```

## 📬 How to Use (API Endpoints)

### 🔗 Convert from image URLs

**POST** `http://localhost:9090/api/convert`

**Body (JSON):**

```json
{
  "imageUrls": [
    "https://example.com/sample1.jpg",
    "https://example.com/sample2.png"
  ]
}
```

### 🖼️ Upload local images

**POST** `http://localhost:9090/api/upload`

**Form-Data:**

- Key: `files`
- Value: One or more image files

Converted images will be saved in the `converted-images/` folder.

## 🧪 Sample Input/Output (Optional)

If you'd like, you can include example images:

```
images/
├── sample-input.jpg
└── sample-output.webp
```

## 📦 Technologies Used

- Java 24
- Spring Boot
- Maven
- REST APIs
- Google WebP (`cwebp.exe`)
- Postman (for testing)

## 📝 Note

This sample project replicates the image conversion module I built during my internship, where we processed around 10,000 images fetched from a database and uploaded them to a GCP bucket. This GitHub version demonstrates the same core logic but keeps it minimal for clarity and learning purposes.


