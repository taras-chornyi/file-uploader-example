package com.chornyi.examples.file.upload;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Iterator;
import java.util.List;

@WebServlet("/UploadDownloadFileServlet")
@Slf4j
public class UploadDownloadFileServlet extends HttpServlet {

    private ServletFileUpload uploader = null;

    @Override
    public void init() {
        DiskFileItemFactory fileFactory = new DiskFileItemFactory();
        File filesDir = (File) getServletContext().getAttribute("FILES_DIR_FILE");
        fileFactory.setRepository(filesDir);
        this.uploader = new ServletFileUpload(fileFactory);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fileName = request.getParameter("fileName");
        if (fileName == null || fileName.equals("")) {
            throw new ServletException("File Name can't be null or empty");
        }

        ServletContext ctx = request.getServletContext();
        File file = new File(ctx.getAttribute("FILES_DIR") + File.separator + fileName);
        if (!file.exists()) {
            throw new ServletException("File doesn't exists on server.");
        }

        log.debug("File location on server::" + file.getAbsolutePath());
        InputStream fis = new FileInputStream(file);
        String mimeType = ctx.getMimeType(file.getAbsolutePath());
        response.setContentType(mimeType != null ? mimeType : "application/octet-stream");
        response.setContentLength((int) file.length());
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

        ServletOutputStream os = response.getOutputStream();
        byte[] bufferData = new byte[1024];
        int read = 0;
        while ((read = fis.read(bufferData)) != -1) {
            os.write(bufferData, 0, read);
        }
        os.flush();
        os.close();
        fis.close();
        log.info("File downloaded at client successfully");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!ServletFileUpload.isMultipartContent(request)) {
            throw new ServletException("Content type is not multipart/form-data");
        }

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.write("<html><head></head><body>");
        try {
            List<FileItem> fileItemsList = uploader.parseRequest(request);
            Iterator<FileItem> fileItemsIterator = fileItemsList.iterator();
            while (fileItemsIterator.hasNext()) {
                FileItem fileItem = fileItemsIterator.next();
                log.debug("FieldName=" + fileItem.getFieldName());
                log.debug("FileName=" + fileItem.getName());
                log.debug("ContentType=" + fileItem.getContentType());
                log.debug("Size in bytes=" + fileItem.getSize());

                File file = new File(request.getServletContext().getAttribute("FILES_DIR") + File.separator + fileItem.getName());
                log.debug("Absolute Path at server=" + file.getAbsolutePath());
                fileItem.write(file);
                out.write("File " + fileItem.getName() + " uploaded successfully.");
                out.write("<br>");
                out.write("<a href=\"UploadDownloadFileServlet?fileName=" + fileItem.getName() + "\">Download " + fileItem.getName() + "</a>");
            }
        } catch (FileUploadException e) {
            out.write("Exception in uploading file.");
        } catch (Exception e) {
            out.write("Exception in uploading file.");
        }
        out.write("</body></html>");
    }

}
