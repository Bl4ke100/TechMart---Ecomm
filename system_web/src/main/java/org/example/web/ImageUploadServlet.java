package org.example.web;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

@WebServlet("/api/products/images")
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 1024 * 1024 * 10, maxRequestSize = 1024 * 1024 * 50)
public class ImageUploadServlet extends HttpServlet {
    private static final String UPLOAD_DIR = "d:/Uni Stuff/BCD/auction_system/auction_system/product-images";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String role = request.getHeader("X-Role");
        if (!"ADMIN".equals(role)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        String productId = request.getParameter("productId");
        if (productId == null || productId.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing productId");
            return;
        }

        File productDir = new File(UPLOAD_DIR, productId);
        if (!productDir.exists()) productDir.mkdirs();

        try {
            for (int i = 1; i <= 3; i++) {
                Part part = request.getPart("image" + i);
                if (part != null && part.getSize() > 0) {
                    try (InputStream is = part.getInputStream()) {
                        Files.copy(is, new File(productDir, i + ".jpg").toPath(), StandardCopyOption.REPLACE_EXISTING);
                    }
                }
            }
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write("{\"message\":\"Images uploaded successfully\"}");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Upload failed");
        }
    }
}
