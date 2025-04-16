package org.apache.commons.mail;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.Map;

// This EmailTest class tests the following: addBcc, addCc, addHeader, addReplyto
// Test to trigger CI
public class EmailTest {

    private EmailConcrete email;

    @Before
    public void setUp() {
        // Initialize the email object before each test
        email = new EmailConcrete();
    }

    @Test
    public void testAddBcc_NullInput() {
        try {
            // Try to add null BCC emails, expecting an EmailException
            email.addBcc((String[]) null);
            fail("Expected EmailException to be thrown");
        } catch (EmailException e) {
            // Assert the message or other properties if needed
            assertEquals("Address List provided was invalid", e.getMessage());
        }
    }

    @Test
    public void testAddBcc_EmptyInput() {
        try {
            // Try to add empty BCC emails, expecting an EmailException
            email.addBcc(); 
            fail("Expected EmailException to be thrown");
        } catch (EmailException e) {
            // Assert the message or other properties if needed
            assertEquals("Address List provided was invalid", e.getMessage());
        }
    }
    
    
    @Test
    public void testAddBcc_MultipleValidEmails() throws EmailException {
        // Test adding multiple valid BCC emails
        String[] emails = {"test1@example.com", "test2@example.com", "test3@example.com"};
        email.addBcc(emails);
        assertNotNull(email); // No exception should be thrown
    }
    
    @Test
    public void testAddCc_ValidEmail() throws EmailException {
        // Test adding a valid CC email
        String validEmail = "test@example.com";
        email.addCc(validEmail);
        assertNotNull(email); // No exception should be thrown
    }

    @Test(expected = EmailException.class)
    public void testAddCc_InvalidEmail() throws EmailException {
        // Test if an exception is thrown for an invalid CC email
        String invalidEmail = "invalid-email";
        email.addCc(invalidEmail);
    }

    @Test
    public void testAddHeader_ValidInput() {
        // Ensure headers map is initially empty
        Map<String, String> headersBefore = email.getHeaders();
        assertNotNull(headersBefore);
        assertEquals(0, headersBefore.size());

        // Add a valid header
        email.addHeader("Content-Type", "text/html");

        // Ensure header is added successfully
        Map<String, String> headersAfter = email.getHeaders();
        assertNotNull(headersAfter);
        assertEquals(1, headersAfter.size());
        assertEquals("text/html", headersAfter.get("Content-Type"));
    }

    @Test
    public void testAddHeader_NullName() {
        // Test case to check if an exception is thrown when the header name is null
        try {
            email.addHeader(null, "value");
            fail("Expected IllegalArgumentException for null name"); // Fail if no exception is thrown
        } catch (IllegalArgumentException e) {
            assertEquals("name can not be null or empty", e.getMessage()); // Assert the correct error message
        }
    }

    @Test
    public void testAddHeader_EmptyName() {
        // Test case to check if an exception is thrown when the header name is empty
        try {
            email.addHeader("", "value");
            fail("Expected IllegalArgumentException for empty name"); // Fail if no exception is thrown
        } catch (IllegalArgumentException e) {
            assertEquals("name can not be null or empty", e.getMessage()); // Assert the correct error message
        }
    }

    @Test
    public void testAddHeader_NullValue() {
        // Test case to check if an exception is thrown when the header value is null
        try {
            email.addHeader("Content-Type", null);
            fail("Expected IllegalArgumentException for null value"); // Fail if no exception is thrown
        } catch (IllegalArgumentException e) {
            assertEquals("value can not be null or empty", e.getMessage()); // Assert the correct error message
        }
    }

    @Test
    public void testAddHeader_EmptyValue() {
        // Test case to check if an exception is thrown when the header value is empty
        try {
            email.addHeader("Content-Type", "");
            fail("Expected IllegalArgumentException for empty value"); // Fail if no exception is thrown
        } catch (IllegalArgumentException e) {
            assertEquals("value can not be null or empty", e.getMessage()); // Assert the correct error message
        }
    }

    @Test
    public void testAddHeader_ValidInputs() {
        // Test case to check if adding a valid header works without throwing an exception
        email.addHeader("Content-Type", "text/html");
        assertNotNull(email); // Assert that no exception was thrown and the email object is still valid
    }


 // Test if an exception is thrown when the reply-to email is empty
    @Test
    public void testAddReplyTo_EmptyEmail() {
        EmailException exception = null;
        try {
            email.addReplyTo("", "John Doe");
        } catch (EmailException e) {
            exception = e;
        }

        // Assert that an exception was thrown
        assertNotNull("EmailException should have been thrown", exception);
    }

    // Test if an exception is thrown when the reply-to email is invalid
    @Test
    public void testAddReplyTo_InvalidEmail() {
        EmailException exception = null;
        try {
            email.addReplyTo("invalid-email", "John Doe");
        } catch (EmailException e) {
            exception = e;
        }

        // Assert that an exception was thrown
        assertNotNull("EmailException should have been thrown", exception);
    }

    @Test
    public void testAddReplyTo_NullName() throws EmailException {
        // Test adding a reply-to email with a null name
        email.addReplyTo("test@example.com", null);
        assertNotNull(email); // No exception should be thrown
    }

    @Test
    public void testAddReplyTo_ValidInput() throws EmailException {
        // Test adding a valid reply-to email with a name
        email.addReplyTo("test@example.com", "John Doe");
        assertNotNull(email); // No exception should be thrown
    }
}