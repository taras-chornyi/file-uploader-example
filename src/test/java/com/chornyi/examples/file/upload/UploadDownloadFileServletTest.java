package com.chornyi.examples.file.upload;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

public class UploadDownloadFileServletTest {

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private ServletContext servletContext;
    @Mock
    private ServletOutputStream outputStream;
    @Mock
    private RequestDispatcher requestDispatcher;
    @Mock
    private ServletFileUpload uploader;

    @Spy
    private UploadDownloadFileServlet servlet;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = ServletException.class)
    public void doGetWithEmptyFileName() throws Exception {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        when(response.getWriter()).thenReturn(printWriter);

        servlet.doGet(request, response);
    }

    @Test
    public void doGetWithFileName() throws Exception {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);

        when(request.getParameter("fileName")).thenReturn("test.txt");
        when(request.getServletContext()).thenReturn(servletContext);

        when(request.getServletContext().getAttribute("FILES_DIR")).thenReturn("src/test/resources");
        when(response.getOutputStream()).thenReturn(outputStream);
        when(response.getWriter()).thenReturn(printWriter);

        servlet.doGet(request, response);

        assertNotNull(response);
    }

    @Test(expected = ServletException.class)
    public void doPostWithoutMultipart() throws Exception {
        servlet.doPost(request, response);
    }

}
