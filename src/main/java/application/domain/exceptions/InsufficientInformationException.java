package application.domain.exceptions;

public class InsufficientInformationException extends DomainException {
    public InsufficientInformationException(String message) {
        super(message);
    }
}
