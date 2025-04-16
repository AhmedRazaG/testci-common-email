package org.apache.commons.mail;

import static org.junit.Assert.*;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Provider;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.junit.*;

/**
 * Combined JUnit test class for the Email class.
 * 
 * This class tests the following specified methods:
 *   - addBcc(String... emails)
 *   - addCc(String email)
 *   - addHeader(String name, String value)
 *   - addReplyTo(String email, String name)
 *   - buildMimeMessage()
 *   - getHostName()
 *   - getMailSession()
 *   - getSentDate()
 *   - getSocketConnectionTimeout()
 *   - setFrom(String email)
 * 
 * Additional tests for buildMimeMessage() and getMailSession() are provided
 * to increase branch coverage above 70%.
 */
public class EmailTest {

    private static final String[] TEST_EMAILS = {
        "user1@example.com",
        "user2@example.com",
        "test.email@domain.co.uk",
        "admin@service.io"
    };

    private EmailConcrete email;
    private Date testDate;
    private String testHostName = "smtp.testserver.com";

    @Before
    public void setUp() throws Exception {
        email = new EmailConcrete();
        testDate = new Date();
    }

    @After
    public void tearDown() throws Exception {
        email = null;
    }

    /* ============================
       Tests for the 10 Specified Methods
       ============================ */

    // addBcc(String... emails)
    @Test
    public void testAddBcc() throws Exception {
        email.addBcc(TEST_EMAILS);
        List<InternetAddress> bccList = email.getBccAddresses();
        assertEquals("Expected 4 BCC addresses", 4, bccList.size());
    }

    // addCc(String email)
    @Test
    public void testAddCc() throws Exception {
        email.addCc("test.cc@example.com");
        List<InternetAddress> ccList = email.getCcAddresses();
        assertEquals("Expected 1 CC address", 1, ccList.size());
    }

    // addHeader(String name, String value)
    @Test
    public void testAddHeader() {
        // Valid header
        email.addHeader("Custom-Header", "HeaderValue123");

        // Test invalid header parameters
        try {
            email.addHeader("", "ValidValue");
            fail("Expected IllegalArgumentException for empty header name");
        } catch (IllegalArgumentException e) {
            assertEquals("name can not be null or empty", e.getMessage());
        }
        try {
            email.addHeader(null, "ValidValue");
            fail("Expected IllegalArgumentException for null header name");
        } catch (IllegalArgumentException e) {
            assertEquals("name can not be null or empty", e.getMessage());
        }
        try {
            email.addHeader("Valid-Header", "");
            fail("Expected IllegalArgumentException for empty header value");
        } catch (IllegalArgumentException e) {
            assertEquals("value can not be null or empty", e.getMessage());
        }
        try {
            email.addHeader("Valid-Header", null);
            fail("Expected IllegalArgumentException for null header value");
        } catch (IllegalArgumentException e) {
            assertEquals("value can not be null or empty", e.getMessage());
        }
    }

    // addReplyTo(String email, String name)
    @Test
    public void testAddReplyTo() throws Exception {
        email.addReplyTo("reply@example.com", "Reply User");
        List<InternetAddress> replyList = email.getReplyToAddresses();
        assertEquals("Expected 1 reply-to address", 1, replyList.size());
    }

    // buildMimeMessage() (positive case)
    @Test
    public void testBuildMimeMessage() throws Exception {
        email.setHostName("smtp.example.com");
        email.addTo("receiver@example.com");
        email.setFrom("sender@example.com");
        email.setMsg("Test Message");
        email.buildMimeMessage();
        MimeMessage mimeMessage = email.getMimeMessage();
        assertNotNull("MimeMessage should be built", mimeMessage);
    }

    // buildMimeMessage() negative: missing From address
    @Test
    public void testBuildMimeMessageNoFrom() throws Exception {
        email.setHostName("smtp.example.com");
        email.addTo("receiver@example.com");
        email.setMsg("Test Message");
        try {
            email.buildMimeMessage();
            fail("Expected EmailException for missing From address");
        } catch (EmailException e) {
            assertEquals("From address required", e.getMessage());
        }
    }

    // buildMimeMessage() negative: missing recipient(s)
    @Test
    public void testBuildMimeMessageNoRecipients() throws Exception {
        email.setHostName("smtp.example.com");
        email.setFrom("sender@example.com");
        email.setMsg("Test Message");
        try {
            email.buildMimeMessage();
            fail("Expected EmailException for no recipients");
        } catch (EmailException e) {
            assertEquals("At least one receiver address required", e.getMessage());
        }
    }

    // getHostName()
    @Test
    public void testGetHostName() throws Exception {
        assertNull("Host name should be null initially", email.getHostName());
        email.setHostName(testHostName);
        assertEquals("Host name not set properly", testHostName, email.getHostName());
    }

    // getMailSession() when session is set externally
    @Test
    public void testGetMailSession() throws Exception {
        Properties props = new Properties();
        props.setProperty("mail.smtp.host", "smtp.example.com");
        props.setProperty("mail.smtp.port", "587");
        props.setProperty("mail.smtp.auth", "true");
        Session session = Session.getInstance(props);
        email.setMailSession(session);
        Session mailSession = email.getMailSession();
        assertNotNull("Mail session should not be null", mailSession);
        assertEquals("smtp.example.com", mailSession.getProperty("mail.smtp.host"));
        assertEquals("587", mailSession.getProperty("mail.smtp.port"));
    }

    // getSentDate()
    @Test
    public void testGetSentDate() {
        email.setSentDate(testDate);
        assertEquals("Sent date mismatch", testDate, email.getSentDate());
    }

    // getSocketConnectionTimeout()
    @Test
    public void testGetSocketConnectionTimeout() {
        email.setSocketConnectionTimeout(30000);
        assertEquals("Socket connection timeout mismatch", 30000, email.getSocketConnectionTimeout());
    }

    // setFrom(String email)
    @Test
    public void testSetFrom() throws Exception {
        email.setFrom("sender@example.com");
        InternetAddress from = email.getFromAddress();
        assertNotNull("From address should not be null", from);
        assertEquals("sender@example.com", from.getAddress());
    }

    /* ============================
       Extra Tests to Increase Coverage of buildMimeMessage()
       ============================ */

    // Test that calling buildMimeMessage() twice throws IllegalStateException.
    @Test
    public void testBuildMimeMessageAlreadyBuilt() throws Exception {
        email.setHostName("smtp.example.com");
        email.addTo("receiver@example.com");
        email.setFrom("sender@example.com");
        email.setMsg("Test Message");
        email.buildMimeMessage();
        try {
            email.buildMimeMessage();
            fail("Expected IllegalStateException when building MimeMessage twice");
        } catch (IllegalStateException e) {
            assertEquals("The MimeMessage is already built.", e.getMessage());
        }
    }

    // Test branch: when content is null but emailBody (MimeMultipart) is provided.
    @Test
    public void testBuildMimeMessageWithEmailBody() throws Exception {
        email.setHostName("smtp.example.com");
        email.setFrom("sender@example.com");
        email.addTo("receiver@example.com");
        email.setSubject("Multipart Test");
        MimeMultipart multipart = new MimeMultipart();
        email.setContent(multipart);
        // Force content to be null.
        email.content = null;
        email.contentType = null;
        email.buildMimeMessage();
        MimeMessage msg = email.getMimeMessage();
        Object content = msg.getContent();
        assertNotNull("Content from emailBody should not be null", content);
        assertTrue("Content should be instance of MimeMultipart", content instanceof MimeMultipart);
    }

    // Test branch: when both content and emailBody are null (should set empty text).
    @Test
    public void testBuildMimeMessageWithNoContentOrBody() throws Exception {
        email.setHostName("smtp.example.com");
        email.setFrom("sender@example.com");
        email.addTo("receiver@example.com");
        email.content = null;
        email.emailBody = null;
        email.buildMimeMessage();
        MimeMessage msg = email.getMimeMessage();
        Object content = msg.getContent();
        assertTrue("Content should be a String", content instanceof String);
        assertEquals("Content should be empty", "", content);
    }

    // Test branch: headers are folded and added.
    @Test
    public void testBuildMimeMessageWithHeaders() throws Exception {
        email.setHostName("smtp.example.com");
        email.setFrom("sender@example.com");
        email.addTo("receiver@example.com");
        email.addHeader("X-Test-Header", "TestValue");
        email.buildMimeMessage();
        MimeMessage msg = email.getMimeMessage();
        String[] headers = msg.getHeader("X-Test-Header");
        assertNotNull("X-Test-Header should be present", headers);
        assertTrue("Header value should contain TestValue", headers[0].contains("TestValue"));
    }

    // Test branch: if sent date is not set, it is automatically assigned.
    @Test
    public void testBuildMimeMessageSetsSentDate() throws Exception {
        email.setHostName("smtp.example.com");
        email.setFrom("sender@example.com");
        email.addTo("receiver@example.com");
        email.setMsg("Test Message");
        // Do not set sentDate explicitly.
        email.buildMimeMessage();
        MimeMessage msg = email.getMimeMessage();
        assertNotNull("Sent date should be automatically set", msg.getSentDate());
    }

    /* ============================
       Extra Tests to Increase Coverage of getMailSession()
       ============================ */

    // Test getMailSession() when no host is set (simulate system properties cleared).
    @Test
    public void testGetMailSessionNoHost() {
        // Clear system property for mail host temporarily.
        String original = System.getProperty(Email.MAIL_HOST);
        System.clearProperty(Email.MAIL_HOST);
        try {
            // Do not set hostName in the Email instance.
            try {
                email.getMailSession();
                fail("Expected EmailException when no host is set");
            } catch (EmailException e) {
                assertEquals("Cannot find valid hostname for mail session", e.getMessage());
            }
        } finally {
            if (original != null) {
                System.setProperty(Email.MAIL_HOST, original);
            }
        }
    }

    // Test advanced branch in getMailSession() by setting various fields.
    @Test
    public void testGetMailSessionAdvanced() throws Exception {
        // Set various fields so that multiple branches are executed.
        email.setHostName("smtp.advanced.com");
        email.setSmtpPort(2525);
        email.setSocketConnectionTimeout(5000);
        email.setSocketTimeout(4000);
        email.setBounceAddress("bounce@advanced.com");
        email.setSSLOnConnect(true);
        email.setAuthentication("user", "pass");
        Session session = email.getMailSession();
        assertNotNull("Session should be created", session);
        // Check that properties were set as expected.
        assertEquals("smtp.advanced.com", session.getProperty(Email.MAIL_HOST));
        // When SSL is enabled, smtpPort should be replaced by sslSmtpPort (default "465").
        assertEquals("465", session.getProperty(Email.MAIL_SMTP_SOCKET_FACTORY_PORT));
        assertEquals("javax.net.ssl.SSLSocketFactory", session.getProperty(Email.MAIL_SMTP_SOCKET_FACTORY_CLASS));
        assertEquals("bounce@advanced.com", session.getProperty(Email.MAIL_SMTP_FROM));
        assertEquals("4000", session.getProperty(Email.MAIL_SMTP_TIMEOUT));
        assertEquals("5000", session.getProperty(Email.MAIL_SMTP_CONNECTIONTIMEOUT));
    }

    /* ============================
       Concrete Subclass for Testing
       ============================ */

    /**
     * A concrete subclass of Email for testing purposes.
     * It implements the abstract methods setMsg and getHeaders.
     * Also, sendMimeMessage is overridden to simulate sending without a real SMTP server.
     */
    public static class EmailConcrete extends Email {
        @Override
        public Email setMsg(String msg) throws EmailException {
            this.setContent(msg, Email.TEXT_PLAIN);
            return this;
        }
        @Override
        protected Properties getHeaders() {
            return new Properties();
        }
        @Override
        public String sendMimeMessage() throws EmailException {
            if (this.message == null) {
                throw new EmailException("MimeMessage has not been created yet");
            }
            try {
                this.message.setHeader("Message-ID", "dummy-id");
            } catch (Exception e) {
                throw new EmailException(e);
            }
            return "dummy-id";
        }
    }
}