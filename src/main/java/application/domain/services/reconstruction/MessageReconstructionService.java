package application.domain.services.reconstruction;

import application.domain.exceptions.InsufficientInformationException;
import application.domain.exceptions.MessageReconstructionException;
import application.domain.models.SurveillanceDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MessageReconstructionService {

    public String getMessage(List<SurveillanceDevice> devices) {
        if (devices == null || devices.size() < 3) {
            throw new InsufficientInformationException(
                    "Se requieren los fragmentos de mensaje de los tres dispositivos de vigilancia para reconstruir el mensaje.");
        }

        List<List<String>> fragments = new ArrayList<>();
        for (SurveillanceDevice device : devices) {
            if (device == null || device.getMessage() == null || device.getMessage().isEmpty()) {
                throw new MessageReconstructionException(
                        "Todos los dispositivos deben proporcionar un fragmento de mensaje válido y no vacío.");
            }
            fragments.add(device.getMessage());
        }

        return reconstructMessage(fragments);
    }

    public String reconstructMessage(List<List<String>> messages) {
        if (messages == null || messages.isEmpty()) {
            throw new MessageReconstructionException("No se recibieron fragmentos de mensaje para reconstruir.");
        }

        int minLength = Integer.MAX_VALUE;
        int maxLength = 0;

        for (List<String> fragment : messages) {
            if (fragment == null || fragment.isEmpty()) {
                throw new MessageReconstructionException("Uno de los fragmentos del mensaje está vacío o es nulo.");
            }
            minLength = Math.min(minLength, fragment.size());
            maxLength = Math.max(maxLength, fragment.size());
        }

        if (minLength == 0) {
            throw new MessageReconstructionException("No es posible reconstruir un mensaje con fragmentos de longitud cero.");
        }

        // Align fragments considering possible leading lag/offset when lengths differ
        int targetLength = minLength;
        List<String> reconstructedWords = new ArrayList<>(targetLength);

        for (int i = 0; i < targetLength; i++) {
            String resolvedWord = null;
            for (List<String> fragment : messages) {
                int offset = fragment.size() - targetLength;
                String candidate = fragment.get(i + offset);
                if (candidate != null) {
                    String trimmed = candidate.trim();
                    if (!trimmed.isEmpty()) {
                        if (resolvedWord == null) {
                            resolvedWord = trimmed;
                        } else if (!resolvedWord.equals(trimmed)) {
                            throw new MessageReconstructionException(
                                    "Inconsistencia detectada al reconstruir el mensaje en la posición " + i
                                            + ": '" + resolvedWord + "' vs '" + trimmed + "'.");
                        }
                    }
                }
            }
            if (resolvedWord != null) {
                reconstructedWords.add(resolvedWord);
            }
        }

        if (reconstructedWords.isEmpty()) {
            throw new MessageReconstructionException(
                    "No se pudo reconstruir el mensaje: todos los fragmentos contienen únicamente cadenas vacías.");
        }

        return reconstructedWords.stream().collect(Collectors.joining(" "));
    }
}
