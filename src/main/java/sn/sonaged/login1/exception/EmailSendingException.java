package sn.sonaged.login1.exception;

public class EmailSendingException extends RuntimeException
{
    public EmailSendingException(String message, Throwable cause)
    {
        super(message, cause);
    }
}